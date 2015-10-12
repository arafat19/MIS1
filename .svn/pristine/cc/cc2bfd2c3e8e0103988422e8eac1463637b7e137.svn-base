package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.integration.fixedasset.FixedAssetPluginConnector
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
 * Update inventoryConsumptionDetails (against a budget) and show in grid
 * For details go through Use-Case doc named 'UpdateForInventoryConsumptionDetailsActionService'
 */
class UpdateForInventoryConsumptionDetailsActionService extends BaseService implements ActionIntf {

    private static final String INV_CONSUMPTION_UPDATE_SUCCESS_MESSAGE = "Inventory-Consumption has been updated successfully"
    private static final String INV_CONSUMPTION_UPDATE_FAILURE_MESSAGE = "Fail to update Inventory-Consumption"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String INV_CONSUMPTION_OBJ = "inventoryTransactionDetails"
    private static final String OBJECT_NOT_FOUND = "Inventory-Consumption Transaction might be changed, Please refresh/reload the page"
    private static final String UNAVAILABLE_BUDGET_QUANTITY = "Item quantity exceeds its budget quantity"
    private static final String ITEM_LIST = "itemList"
    private static final String INV_CONSUMPTION_NOT_FOUND = "Inventory-Consumption Transaction not found"
    private static final String INV_TRANSACTION_OBJ = "invInventoryTransaction"
    private static final String APPROVED_INV_TRANSACTION_UPDATE_PROHIBITED = "Approved inventory transaction cannot be edited"
    private static final String UNAVAILABLE_QUANTITY = "Insufficient stock"
    private static final String FIXED_ASSET_NOT_FOUND_ERROR = "Fixed asset not found"

    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired(required = false)
    FixedAssetPluginConnector fixedAssetImplService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility

    /**
     * validate different criteria to update consumedItem. Such as :
     *      Check existence of InvInventoryTransactionDetails(child) Obj which will be updated,
     *      Check approval of InvInventoryTransactionDetails(child) Obj which will be updated,
     *      Check existence of InvInventoryTransaction(Parent) Obj,
     *      Check corresponding budgetItem & isConsumedAgainstFixedAsset of budgetItem
     *      Check availableStockAmount to consume,
     *
     * @Params params -Receives the serialized parameters send from UI
     * @Params obj -N/A
     *
     * @Return -a map containing all objects necessary for execute (InvInventoryTransaction(Parent), InvInventoryTransactionDetails(child))
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check here for required params are present
            if ((!parameterMap.id) || (!parameterMap.version) || (!parameterMap.actualQuantity)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            // check inventoryIn Transaction Object existence
            long inventoryTransactionDetailsId = Long.parseLong(parameterMap.id.toString())
            int version = Integer.parseInt(parameterMap.version.toString())
            InvInventoryTransactionDetails oldInvTransactionDetails = invInventoryTransactionDetailsService.read(inventoryTransactionDetailsId)
            if (!oldInvTransactionDetails || (oldInvTransactionDetails.version != version)) { //check existence & version of updating object
                result.put(Tools.MESSAGE, OBJECT_NOT_FOUND)
                return result
            }

            if (oldInvTransactionDetails.approvedBy > 0) { //approved transaction can not be updated
                result.put(Tools.MESSAGE, APPROVED_INV_TRANSACTION_UPDATE_PROHIBITED)
                return result
            }

            long inventoryTransactionId = Long.parseLong(parameterMap.inventoryTransactionId.toString())
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(inventoryTransactionId)
            if (!invInventoryTransaction) { //Check existence of InvInventoryTransaction(parent object)
                result.put(Tools.MESSAGE, INV_CONSUMPTION_NOT_FOUND)
                return result
            }

            //build object to update
            InvInventoryTransactionDetails newInvInventoryTransactionDetails = buildInvTranDetailsObjForUpdate(parameterMap, oldInvTransactionDetails)

            // check for available stock in inventory
            double consumableQuantity = getConsumableStock(oldInvTransactionDetails.inventoryId, oldInvTransactionDetails.itemId)
            double previousQuantity = consumableQuantity + oldInvTransactionDetails.actualQuantity
            double availableQuantity = previousQuantity - newInvInventoryTransactionDetails.actualQuantity
            if (availableQuantity < 0) {
                result.put(Tools.MESSAGE, UNAVAILABLE_QUANTITY)
                return result
            }

            //check existence of corresponding budgetDetails object
            Object budgetDetails = (Object) budgetImplService.readBudgetDetailsByBudgetAndItem(invInventoryTransaction.budgetId, newInvInventoryTransactionDetails.itemId)
            if (!budgetDetails) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            //if budgetItem property of corresponding selected item is : isConsumedAgainstFixedAsset == true
            //  then fixed asset must be given
            if (budgetDetails.isConsumedAgainstFixedAsset == true
                    && (newInvInventoryTransactionDetails.fixedAssetId <= 0 || newInvInventoryTransactionDetails.fixedAssetDetailsId <= 0)) {
                result.put(Tools.MESSAGE, FIXED_ASSET_NOT_FOUND_ERROR)
                return result
            }
/*
            // check for available stock in Budget
            double availableBudgetQuantity = Double.parseDouble(budgetDetails.quantity.toString())
            double totalConsumedQuantity = getTotalConsumedQuantity(invInventoryTransaction.budgetId, newInvInventoryTransactionDetails.itemId)
            totalConsumedQuantity = totalConsumedQuantity - oldInvTransactionDetails.actualQuantity + newInvInventoryTransactionDetails.actualQuantity
            if (totalConsumedQuantity > availableBudgetQuantity) {
                result.put(Tools.MESSAGE, UNAVAILABLE_BUDGET_QUANTITY)
                return result
            }*/

            result.put(INV_TRANSACTION_OBJ, invInventoryTransaction)
            result.put(INV_CONSUMPTION_OBJ, newInvInventoryTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INV_CONSUMPTION_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Method to update InvTransactionConsumptionDetails
     *
     * @param parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains InvInventoryTransaction(Parent), InvInventoryTransactionDetails(child))
     * @Return -a map containing all objects necessary for executePostCondition(InvInventoryTransaction(Parent), InvInventoryTransactionDetails(child))
     * map -contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) preResult.get(INV_TRANSACTION_OBJ)
            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) preResult.get(INV_CONSUMPTION_OBJ)
            int updateStatus = updateForConsumption(invInventoryTransactionDetails)
            if (!updateStatus) {
                result.put(Tools.MESSAGE, INV_CONSUMPTION_UPDATE_FAILURE_MESSAGE)
                return result
            }
/*

            // increase total consumption at budget details
            double totalConsumedQuantity = getTotalConsumedQuantity(invInventoryTransaction.budgetId, invInventoryTransactionDetails.itemId)
            int updateConsumption = increaseTotalConsumption(invInventoryTransaction.budgetId, invInventoryTransactionDetails.itemId, totalConsumedQuantity )
*/

            result.put(INV_TRANSACTION_OBJ, invInventoryTransaction)
            result.put(INV_CONSUMPTION_OBJ, invInventoryTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INV_CONSUMPTION_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap unapprovedConsumption object for grid
     * @param obj -map returned from executePostCondition
     * @return -a map containing all objects necessary for show page (wrappedUnapprovedConsumption)
     * map -contains isError(true/false) depending on method success, itemList for drop-down to consume again
     */
    @Transactional
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            InvInventoryTransactionDetails invTransactionDetails = (InvInventoryTransactionDetails) receiveResult.get(INV_CONSUMPTION_OBJ)
            GridEntity object = new GridEntity()
            object.id = invTransactionDetails.id
            Item item = (Item) itemCacheUtility.read(invTransactionDetails.itemId)
            Item fixedAsset = (Item) itemCacheUtility.read(invTransactionDetails.fixedAssetId)
            Object fixedAssetDetails = fixedAssetImplService.getFixedAssetDetailsById(invTransactionDetails.fixedAssetDetailsId)
            String actualQuantity = Tools.formatAmountWithoutCurrency(invTransactionDetails.actualQuantity) + Tools.SINGLE_SPACE + item.unit
            String transactionDate = DateUtility.getLongDateForUI(invTransactionDetails.transactionDate)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(invTransactionDetails.createdBy)
            AppUser updateBy = (AppUser) appUserCacheUtility.read(invTransactionDetails.updatedBy)

            object.cell = [
                    Tools.LABEL_NEW,
                    invTransactionDetails.id,
                    item.name,
                    actualQuantity,
                    transactionDate,
                    fixedAsset ? fixedAsset.name : Tools.EMPTY_SPACE,
                    fixedAssetDetails ? fixedAssetDetails.name : Tools.EMPTY_SPACE,
                    createdBy.username,
                    updateBy.username
            ]
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) receiveResult.get(INV_TRANSACTION_OBJ)

            //get itemList for drop-down to consume again
            List<GroovyRowResult> itemList = getItemListForConsumption(invInventoryTransaction.inventoryId, invInventoryTransaction.budgetId)
            result.put(ITEM_LIST, Tools.listForKendoDropdown(itemList,null,null))
            result.put(Tools.MESSAGE, INV_CONSUMPTION_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INV_CONSUMPTION_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to update consumptionDetails object
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, INV_CONSUMPTION_UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INV_CONSUMPTION_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     *  build consumptionDetailsObject(child) to update
     * @param param -GrailsParameterMap sent from UI
     * @param oldInvTranDetails -oldInvInventoryTransactionDetails which will be updated
     * @param inventoryTransaction -InvInventoryTransaction object(parent)
     * @return invInventoryTransactionDetails object
     */
    private InvInventoryTransactionDetails buildInvTranDetailsObjForUpdate(GrailsParameterMap param, InvInventoryTransactionDetails oldInvTranDetails) {
        InvInventoryTransactionDetails invInventoryTransactionDetails = new InvInventoryTransactionDetails()

        double actualQuantity = Double.parseDouble(param.actualQuantity.toString())

        invInventoryTransactionDetails.id = oldInvTranDetails.id
        invInventoryTransactionDetails.version = Integer.parseInt(param.version.toString())
        invInventoryTransactionDetails.acknowledgedBy = oldInvTranDetails.acknowledgedBy
        invInventoryTransactionDetails.actualQuantity = actualQuantity
        invInventoryTransactionDetails.approvedBy = oldInvTranDetails.approvedBy
        invInventoryTransactionDetails.comments = param.comments ? param.comments : null
        invInventoryTransactionDetails.createdBy = oldInvTranDetails.createdBy
        invInventoryTransactionDetails.createdOn = oldInvTranDetails.createdOn
        invInventoryTransactionDetails.fifoQuantity = oldInvTranDetails.fifoQuantity
        invInventoryTransactionDetails.inventoryId = oldInvTranDetails.inventoryId
        invInventoryTransactionDetails.inventoryTransactionId = oldInvTranDetails.inventoryTransactionId
        invInventoryTransactionDetails.inventoryTypeId = oldInvTranDetails.inventoryTypeId
        invInventoryTransactionDetails.lifoQuantity = oldInvTranDetails.lifoQuantity
        invInventoryTransactionDetails.itemId = oldInvTranDetails.itemId
        invInventoryTransactionDetails.mrfNo = oldInvTranDetails.mrfNo
        invInventoryTransactionDetails.rate = oldInvTranDetails.rate
        invInventoryTransactionDetails.shrinkage = oldInvTranDetails.shrinkage
        invInventoryTransactionDetails.stackMeasurement = oldInvTranDetails.stackMeasurement
        invInventoryTransactionDetails.suppliedQuantity = actualQuantity
        invInventoryTransactionDetails.supplierChalan = oldInvTranDetails.supplierChalan
        invInventoryTransactionDetails.transactionDate = DateUtility.parseMaskedFromDate(param.transactionDate.toString())
        invInventoryTransactionDetails.transactionDetailsId = oldInvTranDetails.transactionDetailsId
        invInventoryTransactionDetails.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
        invInventoryTransactionDetails.updatedOn = new Date()
        invInventoryTransactionDetails.vehicleId = oldInvTranDetails.vehicleId
        invInventoryTransactionDetails.vehicleNumber = oldInvTranDetails.vehicleNumber

        long fixedAssetId = param.fixedAssetId ? Long.parseLong(param.fixedAssetId.toString()) : 0L
        invInventoryTransactionDetails.fixedAssetId = fixedAssetId

        long fixedAssetDetailsId = param.fixedAssetDetailsId ? Long.parseLong(param.fixedAssetDetailsId.toString()) : 0L
        invInventoryTransactionDetails.fixedAssetDetailsId = fixedAssetDetailsId

        invInventoryTransactionDetails.adjustmentParentId = 0L
        invInventoryTransactionDetails.approvedOn = null
        invInventoryTransactionDetails.isIncrease = false
        invInventoryTransactionDetails.isCurrent = oldInvTranDetails.isCurrent
        invInventoryTransactionDetails.overheadCost = oldInvTranDetails.overheadCost

        return invInventoryTransactionDetails
    }

    private static final String LST_CONSUM_QUERY = """
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
     * Get List of Consumable stock of available items from view(vw_inv_inventory_consumable_stock)
     *
     * @param inventoryId -Inventory.id
     * @param budgetId -Budget.id
     *
     * @return - a map containing list of GroovyRowResult(itemList)
     */
    public List<GroovyRowResult> getItemListForConsumption(long inventoryId, long budgetId) {
        Map queryParams = [
                budgetId: budgetId,
                inventoryId: inventoryId
        ]
        List<GroovyRowResult> materialList = executeSelectSql(LST_CONSUM_QUERY, queryParams)
        return materialList
    }

    private static final String CONSUM_STOCK_QUERY = """
        SELECT   consumeable_stock
        FROM vw_inv_inventory_consumable_stock
        WHERE inventory_id=:inventoryId
            AND item_id=:itemId
    """

    /**
     * Method to check consumable stock to consume in an inventory of an item using view(vw_inv_inventory_consumable_stock)
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return -double value
     */
    private double getConsumableStock(long inventoryId, long itemId) {
        Map queryParams = [inventoryId: inventoryId, itemId: itemId]
        List<GroovyRowResult> result = executeSelectSql(CONSUM_STOCK_QUERY, queryParams)
        if (result.size() > 0) {
            return (double) result[0].consumeable_stock
        }
        return 0.0d
    }

    private static final String TOTAL_CONSUM_QUAN_QUERY = """
        SELECT  coalesce(sum(iitd.actual_quantity),0) AS total
        FROM inv_inventory_transaction_details iitd
        LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
        WHERE iit.budget_id=:budgetId
        AND iitd.item_id=:itemId
        AND iit.transaction_type_id=:transactionTypeId
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
        List<GroovyRowResult> invInventoryTransactionDetailsList = executeSelectSql(TOTAL_CONSUM_QUAN_QUERY, queryParams)
        double totalConsumedQuantity = Double.parseDouble(invInventoryTransactionDetailsList[0].total.toString())
        return totalConsumedQuantity
    }

    private static final String UPDATE_QUERY = """
                     UPDATE inv_inventory_transaction_details
                        SET  version = :newVersion,
                             comments =:comments,
                             fixed_asset_id =:fixedAssetId,
                             fixed_asset_details_id =:fixedAssetDetailsId,
                             transaction_date = :transactionDate,
                             updated_by = :updatedBy,
                             updated_on = :updatedOn,
                             actual_quantity = :actualQuantity,
                             supplied_quantity = :suppliedQuantity
                        WHERE id = :id AND
                              version = :version
                     """
    /**
     * update transactionDetails(child)
     * @Param invInventoryTransactionDetails -InvInventoryTransactionDetails.id(child object)
     * @Return -updateCount(int value)
     */
    private int updateForConsumption(InvInventoryTransactionDetails invInventoryTransactionDetails) {
        Map queryParams = [
                id: invInventoryTransactionDetails.id,
                version: invInventoryTransactionDetails.version,
                newVersion: invInventoryTransactionDetails.version + 1,
                comments: invInventoryTransactionDetails.comments,
                fixedAssetId: invInventoryTransactionDetails.fixedAssetId,
                fixedAssetDetailsId: invInventoryTransactionDetails.fixedAssetDetailsId,
                transactionDate: DateUtility.getSqlDate(invInventoryTransactionDetails.transactionDate),
                updatedBy: invInventoryTransactionDetails.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(invInventoryTransactionDetails.updatedOn),
                actualQuantity: invInventoryTransactionDetails.actualQuantity,
                suppliedQuantity: invInventoryTransactionDetails.suppliedQuantity
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to update consumption transaction details")
        }
        return updateCount
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
