package com.athena.mis.procurement.actions.purchaserequestdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.entity.ProcPurchaseRequestDetails
import com.athena.mis.procurement.service.PurchaseRequestDetailsService
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Delete Purchase Request Details and remove from grid
 * For details go through Use-Case doc named 'DeletePurchaseRequestDetailsActionService'
 */
class DeletePurchaseRequestDetailsActionService extends BaseService implements ActionIntf {

    private static final String DELETE_PRD_SUCCESS_MESSAGE = "Item of purchase request has been deleted successfully"
    private static final String DELETE_PRD_FAILURE_MESSAGE = "Item of purchase request could not be deleted, Please refresh the page"
    private static final String PO_EXIST = "The PR Item has association within purchase order"
    private static final String PRD_DETAILS_OBJ = "purchaseRequestDetails"
    private static final String PURCHASE_REQUEST_DETAILS_OBJ = "purchaseRequestDetails"
    private static final String DELETED = "deleted"
    private static final String NOT_EDITABLE_MSG = "The PR has been sent for approval, make PR editable to delete PR details"

    PurchaseRequestDetailsService purchaseRequestDetailsService
    PurchaseRequestService purchaseRequestService
    @Autowired
    ProcSessionUtil procSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check all pre conditions for delete
     * @param parameters - serialize parameters from UI
     * @param obj - N/A
     * @return - map with purchase request object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long purchaseRequestDetailsId = Long.parseLong(params.id.toString())

            ProcPurchaseRequestDetails purchaseRequestDetails = (ProcPurchaseRequestDetails) purchaseRequestDetailsService.read(purchaseRequestDetailsId)

            if (!purchaseRequestDetails) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            ProcPurchaseRequest purchaseRequest = purchaseRequestService.read(purchaseRequestDetails.purchaseRequestId)
            if (purchaseRequest.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_EDITABLE_MSG)
                return result
            }

            if (purchaseRequestDetails.poQuantity > 0) {
                result.put(Tools.MESSAGE, PO_EXIST)
                return result
            }

            result.put(PRD_DETAILS_OBJ, purchaseRequestDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PRD_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Delete purchase request from DB and grid
     * 1. pull pr details object
     * 2. pull pr object
     * 3. delete pr details
     * 4. update item count
     * 5. update budget details for re-approval
     * @param parameters - N/A
     * @param obj- object from pre execute method
     * @return - a map containing pr object
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj

            ProcPurchaseRequestDetails purchaseRequestDetails = (ProcPurchaseRequestDetails) preResult.get(PRD_DETAILS_OBJ)
            int updateStatus = purchaseRequestDetailsService.delete(purchaseRequestDetails.id, purchaseRequestDetails.companyId)
            int updatePR = decreaseItemCount(purchaseRequestDetails.purchaseRequestId)

            result.put(PURCHASE_REQUEST_DETAILS_OBJ, purchaseRequestDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Failed to delete PR Details")
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PRD_FAILURE_MESSAGE)
            return result
        }
    }
    // do nothing for post condition
    public Object executePostCondition(Object parameters, Object obj) {
            return null
    }

    /**
     * Set param deleted, budget map and get relevant message
     * @param obj -N/A
     * @return - A map containing message
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(DELETED, Boolean.TRUE.booleanValue())
            result.put(Tools.MESSAGE, DELETE_PRD_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PRD_FAILURE_MESSAGE)
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_PRD_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PRD_FAILURE_MESSAGE)
            return result
        }
    }

    private static final String DECREASE_QUERY = """
                      UPDATE proc_purchase_request
                      SET
                          item_count = item_count - 1,
                          version = version + 1
                      WHERE id = :purchaseRequestId
                      """

    private int decreaseItemCount(long purchaseRequestId) {
        int itemCount = executeUpdateSql(DECREASE_QUERY, [purchaseRequestId: purchaseRequestId])
        if (itemCount <= 0) {
            throw new RuntimeException('Failed to decrease purchase request')
        }
        return itemCount
    }

}
