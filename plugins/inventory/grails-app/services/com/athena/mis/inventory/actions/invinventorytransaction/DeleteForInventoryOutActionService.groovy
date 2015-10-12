package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete inventory transaction out object from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeleteForInventoryOutActionService'
 */
class DeleteForInventoryOutActionService extends BaseService implements ActionIntf {

    private static final String DELETE_STORE_TRANSACTION_SUCCESS_MESSAGE = "Inventory Out has been deleted successfully"
    private static final String DELETE_STORE_TRANSACTION_FAILURE_MESSAGE = "Inventory Out could not be deleted, please try again"
    private static final String MATERIAL_ASSOCIATION_MESSAGE = " item(s) is associated with this inventory transaction"
    private static final String STORE_TRANSACTION_OBJ = "inventoryTransaction"
    private static final String DELETED = "deleted"
    private static final String ALREADY_ACK = "The Inventory-Out transaction already acknowledged"
    private static final String REVERSED_ITEM_ASSOCIATION_MESSAGE = " reversed transaction associated with this inventory transaction"

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Checking pre condition and association before deleting the inventory transaction out object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long inventoryTransactionId = Long.parseLong(params.id.toString())
            // get inventory transaction out object
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(inventoryTransactionId)
            // checking the existence of inventory transaction out object
            if (!invInventoryTransaction) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            // check item count of inventory transaction out object
            if (invInventoryTransaction.itemCount > 0) {
                result.put(Tools.MESSAGE, invInventoryTransaction.itemCount + MATERIAL_ASSOCIATION_MESSAGE)
                return result
            }

            int countReversedTransaction = countReversedTransaction(invInventoryTransaction.id)
            // check if any reverse adjustment exists with this inventory transaction out object
            if (countReversedTransaction > 0) {
                result.put(Tools.MESSAGE, countReversedTransaction + REVERSED_ITEM_ASSOCIATION_MESSAGE)
                return result
            }
            // check if inventory transaction out is already acknowledged
            int ackCount = getAckCountOfInventoryOut(invInventoryTransaction.id)
            if (ackCount > 0) {
                result.put(Tools.MESSAGE, ALREADY_ACK)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(STORE_TRANSACTION_OBJ, invInventoryTransaction)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_STORE_TRANSACTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Delete inventory transaction out object from DB
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
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) preResult.get(STORE_TRANSACTION_OBJ)
            // delete inventory transaction out object
            Boolean isDeleted = invInventoryTransactionService.delete(invInventoryTransaction.id)
            if (!isDeleted.booleanValue()) {
                result.put(Tools.MESSAGE, DELETE_STORE_TRANSACTION_FAILURE_MESSAGE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_STORE_TRANSACTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post operation
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
            result.put(Tools.MESSAGE, DELETE_STORE_TRANSACTION_SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_STORE_TRANSACTION_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, DELETE_STORE_TRANSACTION_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_STORE_TRANSACTION_FAILURE_MESSAGE)
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
     * Count reverse adjustment associated with inventory transaction out object
     * @param invTransactionId -id of inventory transaction out object
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

    private static final String INVENTORY_OUT_COUNT_QUERY = """
         SELECT count(iit.id)
         FROM inv_inventory_transaction iit
            WHERE iit.transaction_id=:invTransactionId AND
            iit.transaction_type_id=:transactionTypeId AND
            iit.transaction_entity_type_id=:transactionEntityTypeId
    """

    /**
     * Count acknowledged inventory out transaction
     * @param invTransactionId -id of inventory transaction out object
     * @return -an integer containing the value of count
     */
    private int getAckCountOfInventoryOut(long invTransactionId) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)

        Map queryParams = [
                invTransactionId: invTransactionId,
                transactionTypeId: transactionTypeIn.id,
                transactionEntityTypeId: transactionEntityInventory.id
        ]
        List<GroovyRowResult> countResult = executeSelectSql(INVENTORY_OUT_COUNT_QUERY, queryParams)
        int total = countResult[0].count
        return total
    }
}