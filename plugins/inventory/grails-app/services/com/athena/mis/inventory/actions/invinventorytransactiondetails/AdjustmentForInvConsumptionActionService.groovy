package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.application.utility.ValuationTypeCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.integration.fixedasset.FixedAssetPluginConnector
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class for adjustment of InventoryConsumptionDetails(Child)
 *  For details go through Use-Case doc named 'AdjustmentForInvConsumptionActionService'
 */
class AdjustmentForInvConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String ADJUSTMENT_SAVE_SUCCESS_MESSAGE = "Adjustment has been saved successfully"
    private static final String ADJUSTMENT_SAVE_FAILURE_MESSAGE = "Can not saved the Adjustment"
    private static final String INVENTORY_REVERSE_ADJ_OBJ = "inventoryTransaction"
    private static final String INVENTORY_ADJ_DETAILS_OBJ = "inventoryAdjDetails"
    private static final String INVENTORY_CURRENT_OBJ = "currentDetails"
    private static final String INVALID_ENTRY = "Error occurred due to invalid input"
    private static final String ADJ_QTY_LOWER_ZERO = "Adjustment quantity can't be lower than or equal zero"
    private static final String NOT_APPROVED = "Adjustment(s) not required for unapproved inventory Transaction"
    private static final String INV_INVENTORY_TRANSACTION_NOT_FOUND = "Inventory Transaction not found"
    private static final String UNAVAILABLE_STOCK_ERROR = "Unavailable Stock for the adjustment"
    private static final String ADJUSTMENT_QTY_ERROR = "Adjustable quantity not found"
    private static final String ADJUSTMENT_PARENT_ID = "adjustmentParentId"
    private static final String UNAVAILABLE_BUDGET_QUANTITY = "Item quantity exceeds its budget quantity"
    private static final String NOT_PROJECT_MANAGER = "You are not allowed to adjust this consumption"
    private static final String ADJUSTMENT_NOT_ALLOWED = "Adjustment not allowed for this item"

    InvInventoryTransactionService invInventoryTransactionService
    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ValuationTypeCacheUtility valuationTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired(required = false)
    FixedAssetPluginConnector fixedAssetImplService

    /**
     * validate different criteria to adjust InventoryConsumptionDetails(child). Such as :
     *      Check parameters
     *      Check if login user has ProjectManager Role or not
     *      Check existence of inventoryConsumptionDetails
     *      Check approval of child
     *      Check existence of inventoryConsumption(Parent)
     *      Check existence of corresponding BudgetDetails
     *
     * @Params parameters -Receives from UI
     * @Params obj -N/A
     *
     * @Return -a map containing 3 inventoryTransactionDetails(currentTranDetails, reverseTranDetails and adjustedTranDetails) object for execute
     * map -contains isError(true/false) depending on method success
     */
    @Transactional
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            //check if login user has ProjectManager Role or not(Only ProjectManager can adjust consumption)
            if (!hasRoleProjectManager()) {
                result.put(Tools.MESSAGE, NOT_PROJECT_MANAGER)
                return result
            }

            //check parameters
            if ((!parameterMap.id) || (!parameterMap.actualQuantity) || (!parameterMap.comments)) {
                result.put(Tools.MESSAGE, INVALID_ENTRY)
                return result
            }

            double actualQuantity = Double.parseDouble(parameterMap.actualQuantity.toString())
            if (actualQuantity <= 0) {//adjustment quantity must be grater than 0
                result.put(Tools.MESSAGE, ADJ_QTY_LOWER_ZERO)
                return result
            }

            long adjustmentParentId = Long.parseLong(parameterMap.id.toString())
            int version = Integer.parseInt(parameterMap.version.toString())
            InvInventoryTransactionDetails currentTransactionDetails = invInventoryTransactionDetailsService.read(adjustmentParentId)
            //check existence of parentObject, version matching, and isCurrent
            if ((!currentTransactionDetails) || (currentTransactionDetails.version != version) || (!currentTransactionDetails.isCurrent)) {
                result.put(Tools.MESSAGE, INV_INVENTORY_TRANSACTION_NOT_FOUND)
                return result
            }

            if (currentTransactionDetails.approvedBy <= 0) { //unapproved transaction could not be adjusted
                result.put(Tools.MESSAGE, NOT_APPROVED)
                return result
            }

            if (actualQuantity == currentTransactionDetails.actualQuantity) { //adjusted quantity must differ
                result.put(Tools.MESSAGE, ADJUSTMENT_QTY_ERROR)
                return result
            }

            Item item = (Item) itemCacheUtility.read(currentTransactionDetails.itemId)
            if (item.isIndividualEntity) { //item not allowed for item which isIndividualEntity=true(For FixedAsset)
                result.put(Tools.MESSAGE, ADJUSTMENT_NOT_ALLOWED)
                return result
            }

            double adjustmentQuantity = actualQuantity - currentTransactionDetails.actualQuantity
            if (actualQuantity > currentTransactionDetails.actualQuantity) {
                InvInventoryTransaction invTransaction = invInventoryTransactionService.read(currentTransactionDetails.inventoryTransactionId)
                Object budgetDetails = (Object) budgetImplService.readBudgetDetailsByBudgetAndItem(invTransaction.budgetId, currentTransactionDetails.itemId)
                if (!budgetDetails) { //check existence of corresponding budgetDetails object
                    result.put(Tools.MESSAGE, INVALID_ENTRY)
                    return result
                }

/*                // check for available stock in Budget
                double totalConsumedQuantity = getTotalApprovedConsumedQuantity(invTransaction.budgetId, currentTransactionDetails.itemId)
                totalConsumedQuantity = totalConsumedQuantity + adjustmentQuantity
                double availableBudgetQuantity = Double.parseDouble(budgetDetails.quantity.toString())
                if (totalConsumedQuantity > availableBudgetQuantity) {
                    result.put(Tools.MESSAGE, UNAVAILABLE_BUDGET_QUANTITY)
                    return result
                }*/

                // check available stock in inventory to consume
                double availableStock = getAvailableStock(currentTransactionDetails.inventoryId, currentTransactionDetails.itemId)
                if (Math.abs(adjustmentQuantity) > availableStock) {
                    result.put(Tools.MESSAGE, UNAVAILABLE_STOCK_ERROR)
                    return result
                }
            }

            String comments = parameterMap.comments
            //build reverseTransactionDetails object
            InvInventoryTransactionDetails reverseTransDetails = copyForReverseAdjustment(currentTransactionDetails, comments, invSessionUtil.appSessionUtil.getAppUser())
            //build adjustmentTransactionDetails object
            InvInventoryTransactionDetails adjustInvTransDetails = copyForAdjustment(currentTransactionDetails, actualQuantity, comments, invSessionUtil.appSessionUtil.getAppUser())

            currentTransactionDetails.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
            currentTransactionDetails.updatedOn = new Date()
            currentTransactionDetails.isCurrent = false

            result.put(INVENTORY_ADJ_DETAILS_OBJ, adjustInvTransDetails)
            result.put(INVENTORY_REVERSE_ADJ_OBJ, reverseTransDetails)
            result.put(INVENTORY_CURRENT_OBJ, currentTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ADJUSTMENT_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     *  Method to adjust InventoryConsumptionDetails(child)
     *
     * @Params parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains 3 inventoryTransactionDetails(currentTranDetails, reverseTranDetails and adjustedTranDetails) object
     *
     * @Return -a map containing inventoryTransaction(Parent), inventoryTransactionDetails(child) object for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            InvInventoryTransactionDetails currentDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_CURRENT_OBJ)
            InvInventoryTransactionDetails reverseDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_REVERSE_ADJ_OBJ)
            InvInventoryTransactionDetails adjDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_ADJ_DETAILS_OBJ)

            //First : create reverseTransactionDetails object
            InvInventoryTransactionDetails newReverseAdjDetails = invInventoryTransactionDetailsService.create(reverseDetails)
            adjDetails.approvedOn = DateUtility.addOneSecond(adjDetails.approvedOn)
            // required for transaction report

            //Second : calculate valuation and get consumption rate
            double rate = calculateValuationForOut(adjDetails.actualQuantity, adjDetails.inventoryId, adjDetails.itemId)
            adjDetails.rate = rate

            //Third : create new adjustedTransactionDetails object
            InvInventoryTransactionDetails newAdjDetails = invInventoryTransactionDetailsService.create(adjDetails)
            if (!newAdjDetails) {
                result.put(Tools.MESSAGE, ADJUSTMENT_SAVE_FAILURE_MESSAGE)
                return result
            }

            //Fourth : set isCurrent = FALSE of which transaction has been adjusted
            int updateInvInventoryTransaction = setIsCurrentTransaction(currentDetails)

            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(newAdjDetails.inventoryTransactionId)

            // increase total consumption at budget details
            double totalConsumedQuantity = getTotalConsumedQuantity(invInventoryTransaction.budgetId, newAdjDetails.itemId)
            int updateConsumption = increaseTotalConsumption(invInventoryTransaction.budgetId, newAdjDetails.itemId, totalConsumedQuantity)

            result.put(INVENTORY_ADJ_DETAILS_OBJ, newAdjDetails)
            result.put(INVENTORY_CURRENT_OBJ, currentDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to adjust inventory consumption')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ADJUSTMENT_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap adjustedConsumptionDetails object for grid
     * @param obj -map returned from execute
     * @return -a map containing all objects necessary for show page (wrappedAdjustedConsumptionDetailsObject)
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            InvInventoryTransactionDetails adjDetails = (InvInventoryTransactionDetails) receiveResult.get(INVENTORY_ADJ_DETAILS_OBJ)
            InvInventoryTransactionDetails currentDetails = (InvInventoryTransactionDetails) receiveResult.get(INVENTORY_CURRENT_OBJ)
            GridEntity object = new GridEntity()
            object.id = adjDetails.id
            Item item = (Item) itemCacheUtility.read(adjDetails.itemId)
            Item fixedAsset = (Item) itemCacheUtility.read(adjDetails.fixedAssetId)
            Object fixedAssetDetails = fixedAssetImplService.getFixedAssetDetailsById(adjDetails.fixedAssetDetailsId)
            String transactionDate = DateUtility.getLongDateForUI(adjDetails.transactionDate)
            String approvedOn = DateUtility.getLongDateForUI(adjDetails.approvedOn)
            AppUser approvedBy = (AppUser) appUserCacheUtility.read(adjDetails.approvedBy)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(adjDetails.createdBy)
            object.cell = [Tools.LABEL_NEW,
                    adjDetails.id,
                    item.name,
                    adjDetails.actualQuantity.toString() + Tools.SINGLE_SPACE + item.unit,
                    adjDetails.rate,
                    transactionDate,
                    approvedOn,
                    fixedAsset ? fixedAsset.name : Tools.EMPTY_SPACE,
                    fixedAssetDetails ? fixedAssetDetails.name : Tools.EMPTY_SPACE,
                    createdBy.username,
                    approvedBy.username
            ]
            result.put(Tools.MESSAGE, ADJUSTMENT_SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(ADJUSTMENT_PARENT_ID, currentDetails.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ADJUSTMENT_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to adjust inventoryConsumptionDetails(Child)
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, ADJUSTMENT_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ADJUSTMENT_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    private static final String APPR_CONSUM_QUAN_QUERY = """
        SELECT  coalesce(sum(iitd.actual_quantity),0) AS total
        FROM inv_inventory_transaction_details iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
        WHERE iit.budget_id=:budgetId
            AND iitd.item_id=:itemId
            AND iit.transaction_type_id=:transactionTypeId
            AND iitd.approved_by > 0
            AND iitd.is_current = true
        """
    /**
     * get total consumed quantity of an approved item against budget
     * @param budgetId -budget.id
     * @param itemId -item.id
     * @return -totalConsumedQuantity(double value)
     */
    private double getTotalApprovedConsumedQuantity(long budgetId, long itemId) {
        // pull transactionType object
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        Map queryParams = [
                transactionTypeId: transactionTypeCons.id,
                budgetId: budgetId,
                itemId: itemId
        ]
        List<GroovyRowResult> invInventoryTransactionDetailsList = executeSelectSql(APPR_CONSUM_QUAN_QUERY, queryParams)
        double totalConsumedQuantity = Double.parseDouble(invInventoryTransactionDetailsList[0].total.toString())
        return totalConsumedQuantity
    }

    private static final String AVL_STOCK_QUERY = """
            SELECT available_stock FROM vw_inv_inventory_stock
            WHERE inventory_id=:inventoryId
            AND item_id=:itemId
        """
    /**
     * Method returns available_stock of an item in an inventory using view(vw_inv_inventory_stock)
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return double value
     */
    private double getAvailableStock(long inventoryId, long itemId) {
        Map queryParams = [inventoryId: inventoryId, itemId: itemId]
        List<GroovyRowResult> result = executeSelectSql(AVL_STOCK_QUERY, queryParams)
        if (result.size() > 0) {
            return (double) result[0].available_stock
        }
        return 0.0d
    }

    /**
     * Method to get itemRate based on valuation of that item
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     * @Param outQuantity -total consumed quantity
     *
     * @Return double value
     */
    private double calculateValuationForOut(double outQuantity, long inventoryId, long itemId) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        // pull valuation type object
        SystemEntity valuationTypeFifoObj = (SystemEntity) valuationTypeCacheUtility.readByReservedAndCompany(valuationTypeCacheUtility.VALUATION_TYPE_FIFO, companyId)
        SystemEntity valuationTypeLifoObj = (SystemEntity) valuationTypeCacheUtility.readByReservedAndCompany(valuationTypeCacheUtility.VALUATION_TYPE_LIFO, companyId)
        SystemEntity valuationTypeAvgObj = (SystemEntity) valuationTypeCacheUtility.readByReservedAndCompany(valuationTypeCacheUtility.VALUATION_TYPE_AVG, companyId)

        Item item = (Item) itemCacheUtility.read(itemId)
        double itemRate = 0.0d
        switch (item.valuationTypeId) {
            case valuationTypeFifoObj.id:
                itemRate = getRateForFifo(outQuantity, inventoryId, itemId)   // get rate , update FIFO count
                break
            case valuationTypeLifoObj.id:
                itemRate = getRateForLifo(outQuantity, inventoryId, itemId)  // get rate , update LIFO count
                break
            case valuationTypeAvgObj.id:
                itemRate = getRateForAverage(inventoryId, itemId)           // get rate (outQuantity is not required here)
                break
            default:
                throw new RuntimeException('Failed to calculateValuationForOut due to unrecognized VALUATION_TYPE')
        }
        return itemRate
    }

    /**
     * Returns the rate for FIFO measurement
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     * @Param outQuantity -total consumed quantity
     *
     * @Return double value
     */
    private double getRateForFifo(double outQuantity, long inventoryId, long itemId) {
        List<GroovyRowResult> lstFIFO = getListForFIFO(inventoryId, itemId)
        double totalFifoAmount = 0.0d
        double inventoryOutQuantity = outQuantity     // copy the out quantity in a variable
        for (int i = 0; i < lstFIFO.size(); i++) {
            GroovyRowResult inventoryTransactionDetails = lstFIFO[i]
            double fifoAvailable = inventoryTransactionDetails.actual_quantity - inventoryTransactionDetails.fifo_quantity

            if (inventoryOutQuantity > fifoAvailable) {
                inventoryOutQuantity = inventoryOutQuantity - fifoAvailable
                inventoryTransactionDetails.fifo_quantity = inventoryTransactionDetails.actual_quantity
                updateFifoCount(inventoryTransactionDetails)  // save This Inventory Details with updated FIFO count
                totalFifoAmount = totalFifoAmount + (inventoryTransactionDetails.rate * fifoAvailable)
            } else {
                inventoryTransactionDetails.fifo_quantity = inventoryTransactionDetails.fifo_quantity + inventoryOutQuantity
                updateFifoCount(inventoryTransactionDetails)  // save This Inventory Details with updated FIFO count
                totalFifoAmount = totalFifoAmount + (inventoryTransactionDetails.rate * inventoryOutQuantity)
                break
            }
        }
        double unitPriceFifo = totalFifoAmount / outQuantity
        return unitPriceFifo.round(2)
    }

    private static final String LST_FIFO_QUERY = """
                SELECT itd.id, itd.version, itd.actual_quantity, itd.fifo_quantity, itd.rate, itd.transaction_type_id
                FROM inv_inventory_transaction_details itd
                WHERE
                    itd.is_increase = true
                    AND itd.approved_by > 0
                    AND itd.item_id=:itemId
                    AND itd.inventory_id=:inventoryId
                    AND itd.fifo_quantity < itd.actual_quantity
                    ORDER BY itd.approved_on ASC
            """
    /**
     * Returns list of groovyRowResult to calculate fifoQuantity of an item
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return List < GroovyRowResult >
     */
    private List<GroovyRowResult> getListForFIFO(long inventoryId, long itemId) {
        Map queryParams = [inventoryId: inventoryId, itemId: itemId]
        List<GroovyRowResult> lstFifo = executeSelectSql(LST_FIFO_QUERY, queryParams)
        return lstFifo
    }

    private static final String UPDATE_FIFO_COUNT_QUERY = """
        UPDATE inv_inventory_transaction_details
        SET
            version=:newVersion,
            fifo_quantity=:fifoQuantity
        WHERE
            id=:id
            AND version=:version
        """
    /**
     * Method to update fifo_quantity of invTransactionDetails object(children)
     * @Param inventoryTransactionDetails -InventoryTransactionDetails object (child)
     * @Return int value(if return value <=0 : throw exception to rollback all DB transaction)
     */
    private int updateFifoCount(GroovyRowResult inventoryTransactionDetails) {
        Map queryParams = [
                id: inventoryTransactionDetails.id,
                version: inventoryTransactionDetails.version,
                newVersion: inventoryTransactionDetails.version + 1,
                fifoQuantity: inventoryTransactionDetails.fifo_quantity
        ]
        int updateCount = executeUpdateSql(UPDATE_FIFO_COUNT_QUERY, queryParams)
        if (updateCount > 0) {
            inventoryTransactionDetails.version = inventoryTransactionDetails.version + 1
        } else {
            throw new RuntimeException('Failed to update FIFO count')
        }
        return updateCount
    }

    /**
     * Returns the rate for LIFO measurement
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     * @Param outQuantity -total consumed quantity
     *
     * @Return double value
     */
    private double getRateForLifo(double outQuantity, long inventoryId, long itemId) {
        List<GroovyRowResult> lstLIFO = getListForLIFO(inventoryId, itemId)
        double totalLifoAmount = 0.0d
        double inventoryOutQuantity = outQuantity     // copy the out quantity in a variable

        for (int i = 0; i < lstLIFO.size(); i++) {
            GroovyRowResult inventoryTransactionDetails = lstLIFO[i]
            double lifoAvailable = inventoryTransactionDetails.actual_quantity - inventoryTransactionDetails.lifo_quantity

            if (inventoryOutQuantity > lifoAvailable) {
                inventoryOutQuantity = inventoryOutQuantity - lifoAvailable
                inventoryTransactionDetails.lifo_quantity = inventoryTransactionDetails.actual_quantity
                updateLifoCount(inventoryTransactionDetails)  // save This inventory Details with updated LIFO count
                totalLifoAmount = totalLifoAmount + (inventoryTransactionDetails.rate * lifoAvailable)
            } else {
                inventoryTransactionDetails.lifo_quantity = inventoryTransactionDetails.lifo_quantity + inventoryOutQuantity
                updateLifoCount(inventoryTransactionDetails)  // save This inventory Details with updated LIFO count
                totalLifoAmount = totalLifoAmount + (inventoryTransactionDetails.rate * inventoryOutQuantity)
                break
            }
        }
        double unitPriceLifo = totalLifoAmount / outQuantity
        return unitPriceLifo.round(2)
    }

    private static final String LIFO_LIST_QUERY = """
                SELECT itd.id, itd.version, itd.actual_quantity, itd.lifo_quantity, itd.rate, itd.transaction_type_id
                FROM inv_inventory_transaction_details itd
                WHERE
                itd.is_increase = true
                AND itd.approved_by > 0
                AND itd.item_id=:itemId
                AND itd.inventory_id=:inventoryId
                AND itd.lifo_quantity < itd.actual_quantity
                ORDER BY itd.approved_on DESC
            """
    /**
     * Returns list of groovyRowResult to calculate lifoQuantity of an item
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return List < GroovyRowResult >
     */
    private List<GroovyRowResult> getListForLIFO(long inventoryId, long itemId) {
        Map queryParams = [
                itemId: itemId,
                inventoryId: inventoryId
        ]
        List<GroovyRowResult> lstLifo = executeSelectSql(LIFO_LIST_QUERY, queryParams)
        return lstLifo
    }

    private static final String UPDATE_LIFO_COUNT_QUERY = """
        UPDATE inv_inventory_transaction_details
        SET
            version=:newVersion,
            lifo_quantity=:lifoQuantity
        WHERE
            id=:id
            AND version=:version
        """
    /**
     * Method to update lifo_quantity of invTransactionDetails object(children)
     * @Param inventoryTransactionDetails -InventoryTransactionDetails object (child)
     * @Return int value(if return value <=0 : throw exception to rollback all DB transaction)
     */
    private int updateLifoCount(GroovyRowResult inventoryTransactionDetails) {
        Map queryParams = [
                id: inventoryTransactionDetails.id,
                version: inventoryTransactionDetails.version,
                newVersion: inventoryTransactionDetails.version + 1,
                lifoQuantity: inventoryTransactionDetails.lifo_quantity
        ]
        int updateCount = executeUpdateSql(UPDATE_LIFO_COUNT_QUERY, queryParams)
        if (updateCount > 0) {
            inventoryTransactionDetails.version = inventoryTransactionDetails.version + 1
        } else {
            throw new RuntimeException('Failed to update LIFO count')
        }
        return updateCount
    }

    /**
     * Returns the rate for Average measurement
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return double value
     */
    private double getRateForAverage(long inventoryId, long itemId) {
        List<GroovyRowResult> lstAverage = getListForAverage(inventoryId, itemId)
        // Average calculation
        double totQuantity = 0
        double totAmount = 0
        for (int i = 0; i < lstAverage.size(); i++) {
            GroovyRowResult inventoryTransactionDetails = lstAverage[i]
            totQuantity = totQuantity + inventoryTransactionDetails.actual_quantity
            totAmount = totAmount + (inventoryTransactionDetails.rate * inventoryTransactionDetails.actual_quantity)
        }
        double unitPriceAvg = totAmount / totQuantity
        return unitPriceAvg.round(2)
    }

    private static final String AVERAGE_LIST_QUERY = """
                SELECT itd.id, itd.actual_quantity, itd.rate, itd.transaction_type_id
                FROM inv_inventory_transaction_details itd
                WHERE
                itd.is_increase = true
                AND itd.approved_by > 0
                AND itd.item_id=:itemId
                AND itd.inventory_id=:inventoryId
                AND ( (itd.fifo_quantity < itd.actual_quantity) OR (itd.lifo_quantity < itd.actual_quantity) )
            """
    /**
     * Returns list of groovyRowResult to calculate averageQuantity of an item
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return list of GroovyRowResult
     */
    private List<GroovyRowResult> getListForAverage(long inventoryId, long itemId) {
        Map queryParams = [
                itemId: itemId,
                inventoryId: inventoryId
        ]
        List<GroovyRowResult> lstAverage = executeSelectSql(AVERAGE_LIST_QUERY, queryParams)
        return lstAverage
    }

    private static final String INVENTORY_TRANSACTION_DETAILS_UPDATE_QUERY = """
                      UPDATE inv_inventory_transaction_details
                      SET is_current=false,
                        version=:newVersion,
                        updated_by =:updatedBy,
                        updated_on =:updatedOn
                      WHERE id =:id AND
                            version=:version
                      """

    /**
     * update previous inv_inventory_transaction_details at the time of adjustment(set isCurrent = false)
     * @param invTransactionDetails -invInventoryTransactionDetails object which will be adjusted
     * @return -updateCount(if updateCount<=0, then rollback whole transaction)
     */
    private int setIsCurrentTransaction(InvInventoryTransactionDetails invInventoryTransactionDetails) {
        Map queryParams = [
                id: invInventoryTransactionDetails.id,
                version: invInventoryTransactionDetails.version,
                newVersion: invInventoryTransactionDetails.version + 1,
                updatedBy: invInventoryTransactionDetails.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(invInventoryTransactionDetails.updatedOn)
        ]
        int updateCount = executeUpdateSql(INVENTORY_TRANSACTION_DETAILS_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update inventory transaction details')
        }
        return updateCount
    }

    /**
     * Method to check that if login user has projectManager role or not
     * @return -return TRUE if loginUser has projectManager role; otherwise return FALSE
     */
    private boolean hasRoleProjectManager() {
        List<Long> userRoleList = invSessionUtil.appSessionUtil.getUserRoleIds()
        for (int i = 0; i < userRoleList.size(); i++) {
            if (userRoleList[i] == roleTypeCacheUtility.ROLE_TYPE_PROJECT_MANAGER) {
                return true
            }
        }
        return false
    }

    private InvInventoryTransactionDetails copyForAdjustment(InvInventoryTransactionDetails currentTransactionDetails, double newQuantity, String comments, AppUser user) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_ADJUSTMENT, companyId)

        InvInventoryTransactionDetails newDetails = new InvInventoryTransactionDetails()
        newDetails.id = 0
        newDetails.version = 0
        newDetails.inventoryTransactionId = currentTransactionDetails.inventoryTransactionId
        newDetails.transactionDetailsId = currentTransactionDetails.transactionDetailsId
        newDetails.inventoryTypeId = currentTransactionDetails.inventoryTypeId
        newDetails.inventoryId = currentTransactionDetails.inventoryId
        newDetails.approvedBy = user.id
        newDetails.itemId = currentTransactionDetails.itemId
        newDetails.vehicleId = currentTransactionDetails.vehicleId
        newDetails.vehicleNumber = currentTransactionDetails.vehicleNumber
        newDetails.suppliedQuantity = newQuantity
        newDetails.actualQuantity = newQuantity
        newDetails.shrinkage = 0
        newDetails.rate = currentTransactionDetails.rate                        // same as parents rate
        newDetails.supplierChalan = currentTransactionDetails.supplierChalan
        newDetails.stackMeasurement = currentTransactionDetails.stackMeasurement
        newDetails.fifoQuantity = 0
        newDetails.lifoQuantity = 0
        newDetails.acknowledgedBy = currentTransactionDetails.acknowledgedBy
        newDetails.createdOn = new Date()
        newDetails.createdBy = user.id
        newDetails.updatedOn = null
        newDetails.updatedBy = 0
        newDetails.comments = comments
        newDetails.mrfNo = currentTransactionDetails.mrfNo
        newDetails.transactionDate = currentTransactionDetails.transactionDate
        newDetails.fixedAssetId = currentTransactionDetails.fixedAssetId
        newDetails.fixedAssetDetailsId = currentTransactionDetails.fixedAssetDetailsId
        newDetails.transactionTypeId = transactionTypeAdj.id              // TRANSACTION_TYPE_ADJUSTMENT
        newDetails.adjustmentParentId = currentTransactionDetails.adjustmentParentId == 0 ? currentTransactionDetails.id : currentTransactionDetails.adjustmentParentId
        newDetails.approvedOn = new Date()
        newDetails.isIncrease = currentTransactionDetails.isIncrease
        newDetails.isCurrent = true
        newDetails.overheadCost = currentTransactionDetails.overheadCost
        newDetails.invoiceAcknowledgedBy = currentTransactionDetails.invoiceAcknowledgedBy
        return newDetails
    }

    private InvInventoryTransactionDetails copyForReverseAdjustment(InvInventoryTransactionDetails oldConsumpDetails, String comments, AppUser user) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeRevAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_REVERSE_ADJUSTMENT, companyId)


        InvInventoryTransactionDetails newDetails = new InvInventoryTransactionDetails()
        newDetails.id = 0
        newDetails.version = 0
        newDetails.inventoryTransactionId = oldConsumpDetails.inventoryTransactionId
        newDetails.transactionDetailsId = 0
        newDetails.inventoryTypeId = oldConsumpDetails.inventoryTypeId
        newDetails.inventoryId = oldConsumpDetails.inventoryId
        newDetails.approvedBy = user.id
        newDetails.itemId = oldConsumpDetails.itemId
        newDetails.vehicleId = 0
        newDetails.vehicleNumber = null
        newDetails.suppliedQuantity = oldConsumpDetails.suppliedQuantity
        newDetails.actualQuantity = oldConsumpDetails.actualQuantity
        newDetails.shrinkage = oldConsumpDetails.shrinkage
        newDetails.rate = oldConsumpDetails.rate                     // same as parents rate
        newDetails.supplierChalan = null
        newDetails.stackMeasurement = null
        newDetails.fifoQuantity = 0
        newDetails.lifoQuantity = 0
        newDetails.acknowledgedBy = 0
        newDetails.createdOn = new Date()
        newDetails.createdBy = user.id
        newDetails.updatedOn = null
        newDetails.updatedBy = 0
        newDetails.comments = comments
        newDetails.mrfNo = null
        newDetails.transactionDate = oldConsumpDetails.transactionDate
        newDetails.fixedAssetId = 0
        newDetails.fixedAssetDetailsId = 0
        newDetails.transactionTypeId = transactionTypeRevAdj.id              // TRANSACTION_TYPE_REVERSE_ADJUSTMENT
        newDetails.adjustmentParentId = oldConsumpDetails.adjustmentParentId == 0 ? oldConsumpDetails.id : oldConsumpDetails.adjustmentParentId
        newDetails.approvedOn = new Date()
        newDetails.isIncrease = !(oldConsumpDetails.isIncrease)
        newDetails.isCurrent = false
        newDetails.overheadCost = oldConsumpDetails.overheadCost
        newDetails.invoiceAcknowledgedBy = oldConsumpDetails.invoiceAcknowledgedBy
        return newDetails
    }

    private static final String TOTAL_APPROVED_CONSUMED_QUANTITY_QUERY = """
            SELECT  coalesce(sum(iitd.actual_quantity),0) AS total
        FROM inv_inventory_transaction_details iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
        WHERE iit.budget_id=:budgetId
            AND iitd.item_id=:itemId
            AND iit.transaction_type_id=:transactionTypeId
            AND iitd.approved_by > 0
            AND iitd.is_current = true
    """
    /**
     * Method to check totalConsumedAmount of selected item against budget
     * @Param budgetId -Budget.id
     * @Param itemId -Item.id
     *
     * @Return -double value (totalConsumedQuantity)
     */
    private double getTotalConsumedQuantity(long budgetId, long itemId) {
        // pull transactionType object
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        Map queryParams = [
                transactionTypeId: transactionTypeCons.id,
                budgetId: budgetId,
                itemId: itemId
        ]
        List<GroovyRowResult> invInventoryTransactionDetailsList = executeSelectSql(TOTAL_APPROVED_CONSUMED_QUANTITY_QUERY, queryParams)
        double totalConsumedQuantity = Double.parseDouble(invInventoryTransactionDetailsList[0].total.toString())
        return totalConsumedQuantity
    }

    private static final String INCREASE_TOTAL_CONSUMPTION_QUERY = """
        UPDATE budg_budget_details SET
            total_consumption = :totalConsumedQuantity,
            version=version+1
        WHERE
            budget_id = :budgetId
            AND item_id = :itemId
    """
    /**
     * method increase total consumption of parent
     * @Param budgetId -
     * @Param itemId -
     * @Return int -
     */
    private int increaseTotalConsumption(long budgetId, long itemId, double totalConsumedQuantity) {
        Map queryParams = [
                budgetId: budgetId,
                itemId: itemId,
                totalConsumedQuantity: totalConsumedQuantity
        ]
        int updateTotalConsumption = executeUpdateSql(INCREASE_TOTAL_CONSUMPTION_QUERY, queryParams)

        if (updateTotalConsumption <= 0) {
            throw new RuntimeException("Fail to increase total consumption")
        }
        return updateTotalConsumption
    }
}
