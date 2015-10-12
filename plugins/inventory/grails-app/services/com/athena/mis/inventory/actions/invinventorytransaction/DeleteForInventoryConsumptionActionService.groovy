package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Class for deleting InventoryConsumption(parent)
 *  For details go through Use-Case doc named 'DeleteForInventoryConsumptionActionService'
 */
class DeleteForInventoryConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService

    private static final String DELETE_INVENTORY_CONSUMPTION_SUCCESS_MESSAGE = "Inventory-Consumption Transaction has been deleted successfully"
    private static final String DELETE_INVENTORY_CONSUMPTION_FAILURE_MESSAGE = "Inventory-Consumption Transaction could not be deleted"
    private static final String ITEM_FOUND = "Inventory-Consumption Transaction could not be delete due to the existence of its item(s)"
    private static final String INVENTORY_CONSUMPTION_OBJ = "invInventoryTransaction"
    private static final String DELETED = "deleted"
    private static final String REVERSED_ITEM_ASSOCIATION_MESSAGE = " reversed transaction associated with this inventory transaction"

    /**
     * validate different criteria to delete InventoryConsumption(parent). Such as :
     *      Check existence of invTransactionId
     *      Check existence of invInventoryTransaction
     *      Check approval of any child
     *      Check availableAmount to delete etc.
     *
     * @Params parameters -Receives InvInventoryTransactionId(consumption Id) from UI
     * @Params obj -N/A
     *
     * @Return -a map containing all objects necessary for execute (Parents(invTranConsumption & invTranProduction), Children(invTranConsumptionDetails & invTranProductionDetails))
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long invTransactionId = Long.parseLong(params.id.toString())

            InvInventoryTransaction invTransaction = (InvInventoryTransaction) invInventoryTransactionService.read(invTransactionId)
            if (!invTransaction) { //existence of object which will be deleted
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            if (invTransaction.itemCount > 0) {  //if there is a child then parent could not be deleted
                result.put(Tools.MESSAGE, ITEM_FOUND)
                return result
            }

            int countReversedTransaction = countReversedTransaction(invTransaction.id)
            if (countReversedTransaction > 0) { //if there any reverseAdjustment of an item then parent could not be deleted
                result.put(Tools.MESSAGE, countReversedTransaction + REVERSED_ITEM_ASSOCIATION_MESSAGE)
                return result
            }

            result.put(INVENTORY_CONSUMPTION_OBJ, invTransaction)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INVENTORY_CONSUMPTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     *  Method to delete parents(inventoryConsumption)
     *
     * @Params parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains parents(inventoryConsumption)
     *
     * @Return -map contains isError(true/false)
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) preResult.get(INVENTORY_CONSUMPTION_OBJ)
            Boolean updateStatus = invInventoryTransactionService.delete(invInventoryTransaction.id)
            if (!updateStatus.booleanValue()) {
                result.put(Tools.MESSAGE, DELETE_INVENTORY_CONSUMPTION_FAILURE_MESSAGE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INVENTORY_CONSUMPTION_FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * @Params obj -N/A
     * @Return -a map containing delete success message
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(DELETED, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INVENTORY_CONSUMPTION_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INVENTORY_CONSUMPTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to delete inventoryConsumption(Parent)
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
            result.put(Tools.MESSAGE, DELETE_INVENTORY_CONSUMPTION_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INVENTORY_CONSUMPTION_FAILURE_MESSAGE)
            return result
        }
    }

    private static final String REVERSED_TRANSACTION_QUERY = """
             SELECT count(id) FROM inv_inventory_transaction_details
                WHERE inventory_transaction_id=:invTransactionId
                AND approved_by > 0
                AND is_current = false
                AND adjustment_parent_id > 0
            """
    /**
     * Method to get count of reversedTransactions(child)
     * @Param invTransactionId -InvInventoryTransaction.id (parent id)
     *
     * @Return -(int)total
     */
    private int countReversedTransaction(long invTransactionId) {
        Map queryParams = [
                invTransactionId: invTransactionId
        ]
        List<GroovyRowResult> countResult = executeSelectSql(REVERSED_TRANSACTION_QUERY, queryParams)
        int total = countResult[0].count
        return total
    }
}

