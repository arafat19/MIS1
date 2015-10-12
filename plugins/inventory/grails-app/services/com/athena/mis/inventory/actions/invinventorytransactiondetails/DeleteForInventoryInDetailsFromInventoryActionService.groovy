package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete inventory transaction details object(Inventory In From Inventory) from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeleteForInventoryInDetailsFromInventoryActionService'
 */
class DeleteForInventoryInDetailsFromInventoryActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    private static final String DELETE_INVENTORY_TRANSACTION_SUCCESS_MESSAGE = "Inventory-In details has been deleted successfully"
    private static final String DELETE_INVENTORY_TRANSACTION_FAILURE_MESSAGE = "Inventory-In details could not be deleted, please refresh the Inventory-In Details"
    private static final String INVENTORY_TRANSACTION_DETAILS_OBJ = "inventoryTransactionDetails"
    private static final String LST_ITEM = "lstItem"
    private static final String DELETED = "deleted"
    private static final String APPROVED_INV_TRANSACTION_DELETE_PROHIBITED = "Approved inventory transaction can not be deleted"
    private static final String STOCK_QUANTITY_MESSAGE = "Not sufficient stock to delete"

    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    InvInventoryTransactionService invInventoryTransactionService

    /**
     * Checking pre condition before deleting the inventory transaction details object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    //default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long inDetailsId = Long.parseLong(params.id.toString())
            // check whether inventory transaction details object exists or not
            InvInventoryTransactionDetails invInventoryTransactionDetails = invInventoryTransactionDetailsService.read(inDetailsId)
            if (!invInventoryTransactionDetails) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            // check if inventory transaction details is approved or not
            if (invInventoryTransactionDetails.approvedBy > 0) {
                result.put(Tools.MESSAGE, APPROVED_INV_TRANSACTION_DELETE_PROHIBITED)
                return result
            }

            // check available stock
            double availableQuantity = getConsumableStock(invInventoryTransactionDetails.inventoryId, invInventoryTransactionDetails.itemId)
            availableQuantity = availableQuantity - invInventoryTransactionDetails.actualQuantity
            if (availableQuantity < 0) {
                result.put(Tools.MESSAGE, STOCK_QUANTITY_MESSAGE)
                return result
            }

            result.put(INVENTORY_TRANSACTION_DETAILS_OBJ, invInventoryTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INVENTORY_TRANSACTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Delete inventory transaction details object(Inventory In From Inventory) from DB
     * Decrease item count in inventory transaction object
     * Remove acknowledgement from inventory out transaction details
     * This method is in transactional boundary and will roll back in case of any exception
     * @param params -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing isError(true/false) depending on method success and relevant message
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method

            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_TRANSACTION_DETAILS_OBJ)
            // delete inventory transaction details object from DB
            invInventoryTransactionDetailsService.delete(invInventoryTransactionDetails.id)
            // decrease item count in inventory transaction object(parent object)
            decreaseItemCount(invInventoryTransactionDetails.inventoryTransactionId)
            // remove acknowledgement from inventory out transaction details
            removeAcknowledgedFromInvTransactionDetails(invInventoryTransactionDetails.transactionDetailsId);
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(invInventoryTransactionDetails.inventoryTransactionId)
            // get unacknowledged item list of inventory transaction details out object
            List<GroovyRowResult> lstItem = getItemListOfInventoryOut(invInventoryTransaction.transactionId)

            result.put(LST_ITEM, lstItem)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DELETE_INVENTORY_TRANSACTION_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INVENTORY_TRANSACTION_FAILURE_MESSAGE)
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
     * @return -a map containing success message for UI and other necessary objects
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            result.put(DELETED, Boolean.TRUE.booleanValue())
            result.put(LST_ITEM, Tools.listForKendoDropdown(executeResult.get(LST_ITEM),null,null))
            result.put(Tools.MESSAGE, DELETE_INVENTORY_TRANSACTION_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
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

    private static final String CONSUMABLE_STOCK_COUNT = """
        SELECT consumeable_stock
        FROM vw_inv_inventory_consumable_stock
        WHERE inventory_id=:inventoryId
            AND item_id=:itemId
    """

    /**
     * Get consumable stock
     * @param inventoryId -id of inventory
     * @param itemId -id of item
     * @return -a double variable containing the value of stock
     */
    private double getConsumableStock(long inventoryId, long itemId) {
        Map queryParams = [inventoryId: inventoryId, itemId: itemId]
        List<GroovyRowResult> result = executeSelectSql(CONSUMABLE_STOCK_COUNT, queryParams)
        if (result.size() > 0) {
            return (double) result[0].consumeable_stock
        }
        return 0.0d
    }

    private static final String ITEM_LST_INV_OUT_QUERY = """
                SELECT iitd.id, iitd.version, (item.name ||'( ' ||iitd.actual_quantity || ' ' || item.unit || ')') AS name,
                iitd.actual_quantity AS quantity, item.unit
                FROM inv_inventory_transaction_details iitd
                LEFT JOIN item ON item.id = iitd.item_id
                WHERE iitd.inventory_transaction_id=:inventoryTransactionId
                AND iitd.acknowledged_by <= 0
                AND iitd.is_current = true
                AND item.is_individual_entity = false
                ORDER BY item.name ASC
    """

    /**
     * Get item list of inventory out transaction details
     * @param inventoryTransactionId -id of inventory transaction out object
     * @return -list of unacknowledged item of inventory transaction out
     */
    private List<GroovyRowResult> getItemListOfInventoryOut(long inventoryTransactionId) {
        Map queryParams = [inventoryTransactionId: inventoryTransactionId]
        List<GroovyRowResult> itemList = executeSelectSql(ITEM_LST_INV_OUT_QUERY, queryParams)
        return itemList
    }

    private static final String REMOVE_ACKNOWLEDGE_QUERY = """
        UPDATE inv_inventory_transaction_details SET
            version=version+1,
            acknowledged_by=0
        WHERE id=:invTransactionDetailsId
    """

    /**
     * Remove acknowledgement from inventory out transaction details after inventory in delete
     * @param invTransactionDetailsId -id of inventory transaction out details object
     * @return -an integer containing the value of update count
     */
    private int removeAcknowledgedFromInvTransactionDetails(long invTransactionDetailsId) {
        Map queryParams = [
                invTransactionDetailsId: invTransactionDetailsId
        ]
        int updateCount = executeUpdateSql(REMOVE_ACKNOWLEDGE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException("Fail to inventory inventory transaction details")
        }
        return updateCount;
    }

    private static final String DEC_ITEM_COUNT_QUERY = """
        UPDATE inv_inventory_transaction SET
            item_count = item_count - 1,
            version=version+1
        WHERE
            id=:inventoryTransactionId
    """

    /**
     * Decrease item count of inventory transaction object(parent object)
     * @param inventoryTransactionId -id of inventory transaction object
     * @return -an integer containing the value of update count
     */
    private int decreaseItemCount(long inventoryTransactionId) {
        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId
        ]
        int updateCount = executeUpdateSql(DEC_ITEM_COUNT_QUERY, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException("Fail to decrease item count")
        }
        return updateCount
    }
}