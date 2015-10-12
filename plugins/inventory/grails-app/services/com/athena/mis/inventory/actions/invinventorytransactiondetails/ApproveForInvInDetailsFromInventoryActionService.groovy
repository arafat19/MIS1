package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Approve inventory transaction details(Inventory In From Inventory) object and remove from grid
 *  For details go through Use-Case doc named 'ApproveForInvInDetailsFromInventoryActionService'
 */
class ApproveForInvInDetailsFromInventoryActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String APPROVE_INV_TRANSACTION_SUCCESS_MESSAGE = "Inventory Transaction has been approved successfully"
    private static final String APPROVE_INV_TRANSACTION_FAILURE_MESSAGE = "Inventory Transaction could not be approved, please refresh the page"
    private static final String INVENTORY_DETAILS_OBJ = "invInventoryTransactionDetails"
    private static final String INVENTORY_TRANSACTION_OBJ = "invInventoryTransaction"
    private static final String APPROVED = "approved"
    private static final String ALREADY_APPROVED = "This inventory transaction already approved"
    private static final String OUT_NOT_APPROVED = "Inventory Transaction Out not approved"
    private static final String LST_ITEM = "lstItem"
    private static final String USER_ID = "userId"

    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    InvSessionUtil invSessionUtil

    /**
     * Check pre condition before approving inventory transaction details(Inventory In From Inventory) object
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
            long inventoryTransactionDetailsId = Long.parseLong(params.id.toString())
            // get inventory transaction details object(Inventory In From Inventory)
            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) invInventoryTransactionDetailsService.read(inventoryTransactionDetailsId)
            // check if inventory transaction details object exists or not
            if (!invInventoryTransactionDetails) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            // check if inventory transaction details object already approved or not
            if (invInventoryTransactionDetails.approvedBy > 0) {
                result.put(Tools.MESSAGE, ALREADY_APPROVED)
                return result
            }
            // get inventory transaction parent object(Inventory In From Inventory)
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(invInventoryTransactionDetails.inventoryTransactionId)
            // check if inventory transaction parent object exists or not
            if (!invInventoryTransaction) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            //get inventory transaction out details object
            InvInventoryTransactionDetails invTransOutDetails = invInventoryTransactionDetailsService.read(invInventoryTransactionDetails.transactionDetailsId)
            // check if inventory transaction out details object is approved or not
            if (invTransOutDetails.approvedBy <= 0) {
                result.put(Tools.MESSAGE, OUT_NOT_APPROVED)
                return result
            }

            // set approved by(user id) and approved on(current date)
            long userId = invSessionUtil.appSessionUtil.getAppUser().id
            invInventoryTransactionDetails.approvedBy = userId
            invInventoryTransactionDetails.approvedOn = new Date()
            invInventoryTransactionDetails.rate = invTransOutDetails.rate

            result.put(USER_ID, userId)
            result.put(INVENTORY_TRANSACTION_OBJ, invInventoryTransaction)
            result.put(INVENTORY_DETAILS_OBJ, invInventoryTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVE_INV_TRANSACTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Approve inventory transaction details(Inventory In From Inventory) object
     * @param params -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method

            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_DETAILS_OBJ)
            // update necessary properties of inventory transaction details object for approval
            updateToApproveTransaction(invInventoryTransactionDetails)

            result.put(INVENTORY_TRANSACTION_OBJ, preResult.get(INVENTORY_TRANSACTION_OBJ))
            result.put(INVENTORY_DETAILS_OBJ, invInventoryTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVE_INV_TRANSACTION_FAILURE_MESSAGE)
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
     * Remove inventory transaction details object (Inventory In From Inventory) from grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for UI
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(APPROVED, Boolean.TRUE.booleanValue())
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) executeResult.get(INVENTORY_TRANSACTION_OBJ)
            // get unacknowledged item list of inventory transaction details out object
            List<GroovyRowResult> lstItem = getItemListOfInventoryOut(invInventoryTransaction.transactionId)
            List lstNewItem = buildItemList(lstItem)    // build item list for drop down
            result.put(LST_ITEM, lstNewItem)
            result.put(Tools.MESSAGE, APPROVE_INV_TRANSACTION_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVE_INV_TRANSACTION_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, APPROVE_INV_TRANSACTION_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVE_INV_TRANSACTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build item list for drop down
     * @param lstItem -list of item
     * @return -custom list of item for drop down
     */
    private List buildItemList(List<GroovyRowResult> lstItem) {
        List lstNewItem = []
        Map newItem
        for (int i = 0; i < lstItem.size(); i++) {
            if (lstItem[i].quantity > 0) {
                newItem = [
                        id: lstItem[i].id,
                        name: lstItem[i].name,
                        unit: lstItem[i].unit,
                        quantity: lstItem[i].quantity
                ]
                lstNewItem << newItem
            }
        }
        return lstNewItem
    }

    private static final String ITEM_LIST_QUERY = """
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
        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId
        ]
        List<GroovyRowResult> itemList = executeSelectSql(ITEM_LIST_QUERY, queryParams)
        return itemList
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
     * Update necessary properties of inventory transaction details object for approval
     * @param invInventoryTransactionDetails -inventory transaction details object
     * @return -an integer containing the value of update count
     */
    private int updateToApproveTransaction(InvInventoryTransactionDetails invInventoryTransactionDetails) {
        Map queryParams = [
                id: invInventoryTransactionDetails.id,
                newVersion: invInventoryTransactionDetails.version + 1,
                oldVersion: invInventoryTransactionDetails.version,
                approvedBy: invInventoryTransactionDetails.approvedBy,
                rate: invInventoryTransactionDetails.rate,
                approvedOn: DateUtility.getSqlDateWithSeconds(invInventoryTransactionDetails.approvedOn)
        ]

        int updateCount = executeUpdateSql(INVENTORY_TRANSACTION_DETAILS_UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException("Fail to update approve transaction details")
        }
        return updateCount
    }
}
