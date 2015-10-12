package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class for deleting unapproved InventoryConsumptionDetails(child)
 *  For details go through Use-Case doc named 'DeleteForInventoryConsumptionDetailsActionService'
 */
class DeleteForInventoryConsumptionDetailsActionService extends BaseService implements ActionIntf {

    private static
    final String DELETE_INV_CONSUMPTION_SUCCESS_MESSAGE = "Item of Inventory-Consumption Transaction has been deleted successfully"
    private static
    final String DELETE_INV_CONSUMPTION_FAILURE_MESSAGE = "Item of Inventory-Consumption Transaction could not be deleted, Please refresh the page"
    private static final String NOT_DELETE_APPROVE_TRANS = "Approved transaction could not be deleted"
    private static final String INV_CONSUMPTION_DETAILS_OBJ = "inventoryTransactionDetails"
    private static final String INVENTORY_TRANSACTION_OBJ = "inventoryTransaction"
    private static final String DELETED = "deleted"
    private static final String ITEM_LIST = "itemList"

    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    /**
     * validate different criteria to delete InventoryConsumptionDetails(child). Such as :
     *      Check existence of inventoryConsumptionDetails (Which transaction will be deleted)
     *      Check approval of child
     *
     * @Params parameters -Receives InvInventoryTransactionDetailsId(child Id) from UI
     * @Params obj -N/A
     *
     * @Return -a map containing invInventoryTransactionDetails object for execute
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long inventoryTransactionDetailsId = Long.parseLong(params.id.toString())

            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) invInventoryTransactionDetailsService.read(inventoryTransactionDetailsId)
            if (!invInventoryTransactionDetails) { //check existence of transactionDetailsObject
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            if (invInventoryTransactionDetails.approvedBy > 0) {
                //check approval(Approved transaction can not be deleted)
                result.put(Tools.MESSAGE, NOT_DELETE_APPROVE_TRANS)
                return result
            }

            result.put(INV_CONSUMPTION_DETAILS_OBJ, invInventoryTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INV_CONSUMPTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     *  Method to delete InventoryConsumptionDetails(child)
     *
     * @Params parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains InventoryConsumptionDetails(child)
     *
     * @Return -a map containing InvInventoryTransaction(parent) object for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj

            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) preResult.get(INV_CONSUMPTION_DETAILS_OBJ)

            //delete transactionDetailsObject
            Boolean deleteStatus = invInventoryTransactionDetailsService.delete(invInventoryTransactionDetails.id)

            //decrease item count in parent(InvInventoryTransaction)
            int decreaseItemCount = decreaseItemCount(invInventoryTransactionDetails.inventoryTransactionId)

            //pull inventoryTransactionObject(Parent) to get listOfConsumableItems for drop-down
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(invInventoryTransactionDetails.inventoryTransactionId)

/*
            // increase total consumption at budget details
            double totalConsumedQuantity = getTotalConsumedQuantity(invInventoryTransaction.budgetId, invInventoryTransactionDetails.itemId)
            int updateConsumption = decreaseTotalConsumption(invInventoryTransaction.budgetId, invInventoryTransactionDetails.itemId, totalConsumedQuantity )
*/

            result.put(INVENTORY_TRANSACTION_OBJ, invInventoryTransaction)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to create inventory consumption details')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INV_CONSUMPTION_FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Method to show deleteSuccess message on UI and consumableItemList for drop-down
     * @param obj -Receives map from execute which contains InventoryConsumptionObject(Parent)
     *
     * @Return -a map containing listOfConsumableItems for drop-down
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) executeResult.get(INVENTORY_TRANSACTION_OBJ)

            //get list-Of-Consumable-Item-Stock
            List<GroovyRowResult> itemList = getItemListForConsumption(invInventoryTransaction.inventoryId, invInventoryTransaction.budgetId)

            result.put(ITEM_LIST, Tools.listForKendoDropdown(itemList, null, null))
            result.put(DELETED, Boolean.TRUE.booleanValue())
            result.put(Tools.MESSAGE, DELETE_INV_CONSUMPTION_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INV_CONSUMPTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to delete inventoryConsumptionDetails(Child)
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
            result.put(Tools.MESSAGE, DELETE_INV_CONSUMPTION_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INV_CONSUMPTION_FAILURE_MESSAGE)
            return result
        }
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
    private List<GroovyRowResult> getItemListForConsumption(long inventoryId, long budgetId) {
        Map queryParams = [
                inventoryId: inventoryId,
                budgetId: budgetId
        ]
        List<GroovyRowResult> materialList = executeSelectSql(ITEM_LST_CONSUM_QUERY, queryParams)
        return materialList
    }

    private static final String DECRE_ITEM_COUNT_QUERY = """
        UPDATE inv_inventory_transaction SET
            item_count = item_count - 1,
            version=version+1
        WHERE
            id=:inventoryTransactionId
    """
    /**
     * Method to decrease item count at child deletion
     * @Param inventoryTransactionId -InvInventoryTransaction.id (parent id)
     * @Return -deleteCount(intValue) if deleteCount <= 0 the throw exception to rollback whole transaction
     */
    private int decreaseItemCount(long inventoryTransactionId) {
        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId
        ]
        int deleteCount = executeUpdateSql(DECRE_ITEM_COUNT_QUERY, queryParams);

        if (deleteCount <= 0) {
            throw new RuntimeException("Fail to decrease item count")
        }
        return deleteCount
    }

    private static final String TOTAL_CONSUMED_QUANTITY_QUERY = """
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
        List<GroovyRowResult> invInventoryTransactionDetailsList = executeSelectSql(TOTAL_CONSUMED_QUANTITY_QUERY, queryParams)
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
    private int decreaseTotalConsumption(long budgetId, long itemId, double totalConsumedQuantity) {
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
