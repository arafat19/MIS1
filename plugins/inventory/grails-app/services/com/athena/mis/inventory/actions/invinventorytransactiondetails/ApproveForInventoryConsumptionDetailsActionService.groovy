package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.application.utility.ValuationTypeCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class for Approve InventoryConsumptionDetails(Child)
 *  For details go through Use-Case doc named 'ApproveForInventoryConsumptionDetailsActionService'
 */
class ApproveForInventoryConsumptionDetailsActionService extends BaseService implements ActionIntf {

    private static final String APPROVE_INV_CONSUMPTION_SUCCESS_MESSAGE = "Item of Inventory-Consumption Transaction has been approved successfully"
    private static final String APPROVE_INV_CONSUMPTION_FAILURE_MESSAGE = "Item of Inventory-Consumption Transaction could not be approved, Please refresh the Inventory-Consumption"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String INVENTORY_DETAILS_OBJ = "invInventoryTransactionDetails"
    private static final String INVENTORY_TRANSACTION_OBJ = "invInventoryTransaction"
    private static final String APPROVED = "approved"
    private static final String UNAVAILABLE_QUANTITY = "Item Stock is not available"
    private static final String UNAVAILABLE_BUDGET_QUANTITY = "Item quantity exceeds its budget quantity"
    private static final String ALREADY_APPROVED = "This transaction already approved"
    private static final String ITEM_LIST = "itemList"
    private static final String NOT_PROJECT_MANAGER = "You are not allowed to approve this transaction"

    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    ValuationTypeCacheUtility valuationTypeCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService

    private final Logger log = Logger.getLogger(getClass())

    /**
     * validate different criteria to approve InventoryConsumptionDetails(child). Such as :
     *      Check if login user has ProjectManager Role or not
     *      Check existence of inventoryConsumptionDetails
     *      Check approval of child
     *      Check existence of inventoryConsumption(Parent)
     *      Check existence of corresponding BudgetDetails
     *
     * @Params parameters -Receives InvInventoryTransactionDetailsId(child Id) from UI
     * @Params obj -N/A
     *
     * @Return -a map containing inventoryTransaction(Parent), inventoryTransactionDetails(child) object for execute
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            //check if login user has ProjectManager Role or not(Only ProjectManager can approve consumption)
            if (!invSessionUtil.appSessionUtil.hasRole(RoleTypeCacheUtility.ROLE_TYPE_PROJECT_MANAGER)) {
                result.put(Tools.MESSAGE, NOT_PROJECT_MANAGER)
                return result
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long invTranDetailsId = Long.parseLong(params.id.toString())
            InvInventoryTransactionDetails invTranDetails = (InvInventoryTransactionDetails) invInventoryTransactionDetailsService.read(invTranDetailsId)
            if (!invTranDetails) { //check existence of transaction which will be approved
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            if (invTranDetails.approvedBy > 0) { //check if all ready approved or not
                result.put(Tools.MESSAGE, ALREADY_APPROVED)
                return result
            }

            InvInventoryTransaction invTransaction = invInventoryTransactionService.read(invTranDetails.inventoryTransactionId)
            if (!invTransaction) {//check existence of parent(InventoryTransaction)
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            Object budgetDetails = (Object) budgetImplService.readBudgetDetailsByBudgetAndItem(invTransaction.budgetId, invTranDetails.itemId)
            if (!budgetDetails) {//check existence of corresponding budgetDetails object
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            double availableBudgetQuantity = Double.parseDouble(budgetDetails.quantity.toString())

/*            // check available stock in budget
            boolean isOverBudgetQuantity = checkAvailableBudgetQuantity(availableBudgetQuantity, invTranDetails.actualQuantity, invTransaction.budgetId, invTranDetails.itemId)
            if (!isOverBudgetQuantity) {
                result.put(Tools.MESSAGE, UNAVAILABLE_BUDGET_QUANTITY)
                return result
            }*/

            // check available stock in inventory to consume
            double availableStock = getAvailableStock(invTranDetails.inventoryId, invTranDetails.itemId)
            if (invTranDetails.actualQuantity > availableStock) {
                result.put(Tools.MESSAGE, UNAVAILABLE_QUANTITY)
                return result
            }

            // set updated by and approve by
            long userId = invSessionUtil.appSessionUtil.getAppUser().id
            invTranDetails.approvedBy = userId
            invTranDetails.updatedBy = userId
            invTranDetails.updatedOn = new Date()
            invTranDetails.approvedOn = new Date()

            result.put(INVENTORY_TRANSACTION_OBJ, invTransaction)
            result.put(INVENTORY_DETAILS_OBJ, invTranDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVE_INV_CONSUMPTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     *  Method to approve InventoryConsumptionDetails(child)
     *
     * @Params parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains inventoryTransaction(Parent), inventoryTransactionDetails(child)
     *
     * @Return -a map containing inventoryTransaction(Parent), inventoryTransactionDetails(child) object for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvInventoryTransactionDetails invTranDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_DETAILS_OBJ)
            InvInventoryTransaction invInventoryTransaction =(InvInventoryTransaction) preResult.get(INVENTORY_TRANSACTION_OBJ)
            //get consumptionRate by calculatingValuation
            double unitValuation = calculateValuationForOut(invTranDetails.actualQuantity, invTranDetails.inventoryId, invTranDetails.itemId)
            invTranDetails.rate = unitValuation

            //update invTransactionDetailsObject(Set approvedBy, approvedOn, rate)
            int newInvTransDetails = updateToApproveTransaction(invTranDetails)

            // increase total consumption at budget details
            double totalConsumedQuantity = getTotalApprovedConsumedQuantity(invInventoryTransaction.budgetId, invTranDetails.itemId)
            int updateConsumption = increaseTotalConsumption(invInventoryTransaction.budgetId, invTranDetails.itemId, totalConsumedQuantity )

            result.put(INVENTORY_TRANSACTION_OBJ, invInventoryTransaction)
            result.put(INVENTORY_DETAILS_OBJ, invTranDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to approve inventory consumption')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVE_INV_CONSUMPTION_FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Method to show approveSuccess message on UI and consumableItemList for drop-down
     * @param obj -Receives map from execute which contains InventoryConsumptionObject(Parent)
     *
     * @Return -a map containing listOfConsumableItems for drop-down, approved consumptionTransactionDetails object for grid
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(APPROVED, Boolean.TRUE.booleanValue())
            LinkedHashMap executeResult = (LinkedHashMap) obj
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) executeResult.get(INVENTORY_TRANSACTION_OBJ)

            //get list-Of-Consumable-Item-Stock
            List<GroovyRowResult> itemList = getItemListForConsumption(invInventoryTransaction.inventoryId, invInventoryTransaction.budgetId)
            //build list-Of-Consumable-Item-Stock for drop-down
            List newItemList = buildItemList(itemList)

            result.put(ITEM_LIST, Tools.listForKendoDropdown(newItemList,null,null))
            result.put(Tools.MESSAGE, APPROVE_INV_CONSUMPTION_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVE_INV_CONSUMPTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to approve inventoryConsumptionDetails(Child)
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, APPROVE_INV_CONSUMPTION_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVE_INV_CONSUMPTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Method to build consumable-item-stock-list for drop-down
     * @Param itemList -list of groovyRawResult
     * @Return -newItemList
     */
    private List buildItemList(List<GroovyRowResult> itemList) {
        List newItemList = []
        Map newItem
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList[i].quantity > 0) {
                newItem = [
                        id: itemList[i].id,
                        name: itemList[i].name,
                        unit: itemList[i].unit,
                        quantity: itemList[i].quantity,
                        isConsumedAgainstFixedAsset: itemList[i].is_consumed_against_fixed_asset]
                newItemList << newItem
            }
        }
        return newItemList
    }

    private static final String TOTAL_APP_CONSUMED_QUAN_QUERY = """
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
        List<GroovyRowResult> invInventoryTransactionDetailsList = executeSelectSql(TOTAL_APP_CONSUMED_QUAN_QUERY, queryParams)
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

    private static final String ITEM_LST_CONSUM_QUERY = """
        SELECT
            vcs.item_id as id, item.name, item.unit, vcs.consumeable_stock as quantity,
            budget_details.is_consumed_against_fixed_asset
        FROM vw_inv_inventory_consumable_stock vcs
            LEFT JOIN budg_budget_details budget_details ON vcs.item_id = budget_details.item_id
            LEFT JOIN item ON item.id = vcs.item_id
        WHERE vcs.inventory_id=:inventoryId
            AND budget_details.budget_id=:budgetId
            AND vcs.consumeable_stock > 0
            AND item.is_individual_entity = false
            ORDER BY item.name
    """
    /**
     * Method to get list-of-consumable-item-stock for drop-down from view(vw_inv_inventory_consumable_stock)
     * @Param inventoryId -InvInventory.id
     * @Param budgetId -Budget.id
     * @Return -List<GroovyRowResult> (itemList)
     */
    public List<GroovyRowResult> getItemListForConsumption(long inventoryId, long budgetId) {
        Map queryParams = [inventoryId: inventoryId, budgetId: budgetId]
        List<GroovyRowResult> materialList = executeSelectSql(ITEM_LST_CONSUM_QUERY, queryParams)
        return materialList
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
                        SET  version = :newVersion,
                             approved_by = :approvedBy,
                             approved_on = :approvedOn,
                             rate=:rate
                        WHERE id = :id
                        AND version = :oldVersion
                        """
    /**
     * update inv_inventory_transaction_details at the time of approval
     * @param invTransactionDetails -invInventoryTransactionDetails object which will be approved
     * @return -updateCount(if updateCount<=0, then rollback whole transaction)
     */
    private int updateToApproveTransaction(InvInventoryTransactionDetails invTransactionDetails) {
        Map queryParams = [
                id: invTransactionDetails.id,
                newVersion: invTransactionDetails.version + 1,
                oldVersion: invTransactionDetails.version,
                approvedBy: invTransactionDetails.approvedBy,
                rate: invTransactionDetails.rate,
                approvedOn: DateUtility.getSqlDateWithSeconds(invTransactionDetails.approvedOn)
        ]
        int updateCount = executeUpdateSql(INVENTORY_TRANSACTION_DETAILS_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to update approve transaction details")
        }
        return updateCount
    }

    /**
     * Method to check that if totalConsumedAmount of a budgetItem is over or not
     *
     * @param availableBudgetQuantity -totalBudgetAmount remaining to consume
     * @param consumedQuantity -given quantity from UI to consume
     * @param budgetId - budget.id
     * @param itemId - item.id
     *
     * @return -return specific message if totalConsumedAmount > availableBudgetQuantity; otherwise return null
     */
    private boolean checkAvailableBudgetQuantity(double availableBudgetQuantity, double consumedQuantity, long budgetId, long itemId) {
        double totalConsumedQuantity = getTotalApprovedConsumedQuantity(budgetId, itemId)
        totalConsumedQuantity = totalConsumedQuantity + consumedQuantity
        if (totalConsumedQuantity > availableBudgetQuantity) {
            return false
        }
        return true
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