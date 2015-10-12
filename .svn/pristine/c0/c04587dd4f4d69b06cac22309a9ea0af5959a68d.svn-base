package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.integration.procurement.ProcurementPluginConnector
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
 *  Approve inventory transaction details(Inventory In From Supplier) object and remove from grid
 *  For details go through Use-Case doc named 'ApproveForInventoryInDetailsFromSupplierActionService'
 */
class ApproveForInventoryInDetailsFromSupplierActionService extends BaseService implements ActionIntf {

    private static String APPROVE_INV_IN_SUCCESS_MESSAGE = "Item of Inventory-In Transaction has been approved successfully"
    private static String APPROVE_INV_IN_FAILURE_MESSAGE = "Item of Inventory-In Transaction could not be approved, please refresh the page"
    private static String INVENTORY_DETAILS_OBJ = "invInventoryTransactionDetails"
    private static final String INVENTORY_TRANSACTION_OBJ = "invInventoryTransaction"
    private static String APPROVED = "approved"
    private static String ALREADY_APPROVED = "This Inventory-In transaction already approved"
    private static String LST_ITEM = "lstItem"

    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    InvInventoryTransactionService invInventoryTransactionService
    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired
    InvSessionUtil invSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check pre condition before approving inventory transaction details(Inventory In From Supplier) object
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
            long inventoryTransactionDetailsId = Long.parseLong(params.id.toString())
            // get inventory transaction details object(Inventory In From Supplier)
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
            // get inventory transaction parent object(Inventory In From Supplier)
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(invInventoryTransactionDetails.inventoryTransactionId)
            // check if inventory transaction parent object exists or not
            if (!invInventoryTransaction) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            // set approved by(user id) and approved on(current date)
            long userId = invSessionUtil.appSessionUtil.getAppUser().id
            invInventoryTransactionDetails.approvedBy = userId
            invInventoryTransactionDetails.approvedOn = new Date()

            result.put(INVENTORY_TRANSACTION_OBJ, invInventoryTransaction)
            result.put(INVENTORY_DETAILS_OBJ, invInventoryTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVE_INV_IN_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Approve inventory transaction details(Inventory In From Supplier) object
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
            int updateCount = updateToApproveTransaction(invInventoryTransactionDetails)
            if (updateCount <= 0) {
                result.put(Tools.MESSAGE, APPROVE_INV_IN_FAILURE_MESSAGE)
                return result
            }
            result.put(INVENTORY_TRANSACTION_OBJ, preResult.get(INVENTORY_TRANSACTION_OBJ))
            result.put(INVENTORY_DETAILS_OBJ, invInventoryTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVE_INV_IN_FAILURE_MESSAGE)
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
     * Remove inventory transaction details object (Inventory In From Supplier) from grid
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
            // get list of item from PO details
            List<GroovyRowResult> lstItem = procurementImplService.listPOItemByPurchaseOrder(invInventoryTransaction.transactionId)
            result.put(LST_ITEM, lstItem)
            result.put(Tools.MESSAGE, APPROVE_INV_IN_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVE_INV_IN_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, APPROVE_INV_IN_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVE_INV_IN_FAILURE_MESSAGE)
            return result
        }
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