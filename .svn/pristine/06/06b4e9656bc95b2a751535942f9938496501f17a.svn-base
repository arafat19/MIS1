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
 *  Delete inventory transaction object(Inventory In From Inventory) from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeleteForInventoryInFromInventoryActionService'
 */
class DeleteForInventoryInFromInventoryActionService extends BaseService implements ActionIntf {

    InvInventoryTransactionService invInventoryTransactionService

    private final Logger log = Logger.getLogger(getClass())

    private static final String DELETE_INVENTORY_TRANSACTION_SUCCESS_MESSAGE = "Inventory-In has been deleted successfully"
    private static final String DELETE_INVENTORY_TRANSACTION_FAILURE_MESSAGE = "Inventory-In could not be deleted, please refresh the Inventory In"
    private static final String ITEM_ASSOCIATION_MESSAGE = "Item associated with this inventory transaction"
    private static final String INVENTORY_TRANSACTION_OBJ = "inventoryTransaction"
    private static final String DELETED = "deleted"
    private static final String REVERSED_ITEM_ASSOCIATION_MESSAGE = " reversed transaction associated with this inventory transaction"

    /**
     * Checking pre condition and association before deleting the inventory transaction object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long inventoryTransactionId = Long.parseLong(params.id.toString())

            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(inventoryTransactionId)
            // checking the existence of inventory transaction object
            if (!invInventoryTransaction) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            // check item count of inventory transaction object
            if (invInventoryTransaction.itemCount > 0) {
                result.put(Tools.MESSAGE, invInventoryTransaction.itemCount + ITEM_ASSOCIATION_MESSAGE)
                return result
            }

            int countReversedTransaction = countReversedTransaction(invInventoryTransaction.id)
            // check if any reverse transaction exists with this transaction
            if (countReversedTransaction > 0) {
                result.put(Tools.MESSAGE, countReversedTransaction + REVERSED_ITEM_ASSOCIATION_MESSAGE)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(INVENTORY_TRANSACTION_OBJ, invInventoryTransaction)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INVENTORY_TRANSACTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Delete inventory transaction object(Inventory In From Inventory) from DB
     * @param params -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing isError(true/false) depending on method success and relevant message
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) preResult.get(INVENTORY_TRANSACTION_OBJ)
            // delete inventory transaction object (Inventory In From Inventory)
            Boolean success = invInventoryTransactionService.delete(invInventoryTransaction.id)
            if (!success.booleanValue()) {
                result.put(Tools.MESSAGE, DELETE_INVENTORY_TRANSACTION_FAILURE_MESSAGE)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INVENTORY_TRANSACTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(DELETED, Boolean.TRUE.booleanValue())
            result.put(Tools.MESSAGE, DELETE_INVENTORY_TRANSACTION_SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INVENTORY_TRANSACTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_INVENTORY_TRANSACTION_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INVENTORY_TRANSACTION_FAILURE_MESSAGE)
            return result
        }
    }

    private static final String REVERSED_TRANSACTION_QUERY = """
             SELECT count(iitd.id) FROM inv_inventory_transaction_details iitd
                WHERE iitd.inventory_transaction_id=:invTransactionId
                AND iitd.approved_by > 0
                AND iitd.is_current = false
                AND iitd.adjustment_parent_id > 0
    """

    /**
     * check if any reverse transaction exists with the transaction
     * @param invTransactionId -id of inventory transaction object
     * @return -an integer containing the value of count
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