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
 *  Class for deleting unapproved InventoryOutDetails(child)
 *  For details go through Use-Case doc named 'DeleteForInventoryOutDetailsActionService'
 */
class DeleteForInventoryOutDetailsActionService extends BaseService implements ActionIntf {

    private static final String DELETE_INV_TRANSACTION_SUCCESS_MESSAGE = "Inventory-Out details has been deleted successfully"
    private static final String DELETE_INV_TRANSACTION_FAILURE_MESSAGE = "Inventory-Out details could not be deleted, Please try again"
    private static final String INV_TRANSACTION_DETAILS_OBJ = "inventoryTransaction"
    private static final String ITEM_LIST = "itemList"
    private static final String INV_OUT_OBJ = "inventoryOut"
    private static final String DELETED = "deleted"
    private static final String ALREADY_ACK_ERROR = "Acknowledged transaction is not deleted"
    private static final String APPROVED_INV_TRANSACTION_DELETE_PROHIBITED = "Approved inventory transaction cannot be deleted"

    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    InvInventoryTransactionService invInventoryTransactionService

    private final Logger log = Logger.getLogger(getClass())

    /**
     * validate different criteria to delete InventoryOutDetails(child). Such as :
     *      Check existence of inventoryOutDetails(Which transaction will be deleted)
     *      Check existence of inventoryTransactionOut(Parent)
     *      Check approval of child
     *      Check acknowledgement of child
     *
     * @Params parameters -Receives InvInventoryTransactionDetailsId(child Id) from UI
     * @Params obj -N/A
     *
     * @Return -a map containing invInventoryTransactionOut(Parent), invInventoryTransactionOutDetails(Child) object for execute
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long inventoryTransactionDetailsId = Long.parseLong(params.id.toString())
            InvInventoryTransactionDetails invInventoryTransactionDetails = invInventoryTransactionDetailsService.read(inventoryTransactionDetailsId)
            if (!invInventoryTransactionDetails) {//check existence of child object
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            if (invInventoryTransactionDetails.approvedBy > 0) { //check approval(Approved transaction can not be deleted)
                result.put(Tools.MESSAGE, APPROVED_INV_TRANSACTION_DELETE_PROHIBITED)
                return result
            }

            if (invInventoryTransactionDetails.acknowledgedBy > 0) {//check acknowledgement(Acknowledged transaction can not be deleted)
                result.put(Tools.MESSAGE, ALREADY_ACK_ERROR)
                return result
            }

            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(invInventoryTransactionDetails.inventoryTransactionId)
            if (!invInventoryTransaction) { //check existence of parent object
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(INV_OUT_OBJ, invInventoryTransaction)
            result.put(INV_TRANSACTION_DETAILS_OBJ, invInventoryTransactionDetails)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INV_TRANSACTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     *  Method to delete InventoryOutDetails(child)
     *
     * @Params parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains invInventoryTransaction(Parent) and InventoryConsumptionDetails(child) object
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
            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) preResult.get(INV_TRANSACTION_DETAILS_OBJ)
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) preResult.get(INV_OUT_OBJ)

            //delete inventoryTransactionOutDetails(Child) object
            invInventoryTransactionDetailsService.delete(invInventoryTransactionDetails.id)
            //decrease item count in parent(InvInventoryTransaction)
            decreaseItemCount(invInventoryTransaction.id)
            // pull latest material list
            List<GroovyRowResult> itemList = listAvailableItemInInventory(invInventoryTransaction.inventoryId)

            result.put(ITEM_LIST, Tools.listForKendoDropdown(itemList, null, null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to create inventory out details')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INV_TRANSACTION_FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Method to show deleteSuccess message on UI and itemList for drop-down
     * @param obj -Receives map from execute which contains itemList
     *
     * @Return -a map containing listOfItems for drop-down
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            List materialList = (List) receiveResult.get(ITEM_LIST)
            result.put(DELETED, Boolean.TRUE.booleanValue())
            result.put(ITEM_LIST, materialList)
            result.put(Tools.MESSAGE, DELETE_INV_TRANSACTION_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INV_TRANSACTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to delete inventoryOutDetails(Child)
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
            result.put(Tools.MESSAGE, DELETE_INV_TRANSACTION_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INV_TRANSACTION_FAILURE_MESSAGE)
            return result
        }
    }

    private static final String LST_AVAIL_ITEM_IN_INV_QUERY = """
        SELECT item.id,item.name as name,item.unit as unit,vcs.consumeable_stock as curr_quantity
        FROM vw_inv_inventory_consumable_stock vcs
            LEFT JOIN item  on item.id=vcs.item_id
        WHERE vcs.inventory_id=:inventoryId
            AND vcs.consumeable_stock>0
            AND item.is_individual_entity = false
            ORDER BY item.name ASC
    """
    /**
     * Get List of available item from view(vw_inv_inventory_consumable_stock)
     * @Param inventoryId -InvInventory.id
     * @Return -List<GroovyRowResult> (itemList)
     */
    private List<GroovyRowResult> listAvailableItemInInventory(long inventoryId) {
        Map queryParams = [inventoryId: inventoryId]
        List<GroovyRowResult> itemListWithQnty = executeSelectSql(LST_AVAIL_ITEM_IN_INV_QUERY, queryParams)
        return itemListWithQnty
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
        int updateCount = executeUpdateSql(DECRE_ITEM_COUNT_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to decrease item count")
        }
        return updateCount
    }
}