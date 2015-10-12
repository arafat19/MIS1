package com.athena.mis.procurement.actions.purchaserequest

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.service.IndentService
import com.athena.mis.procurement.service.PurchaseOrderService
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Delete Purchase Request and remove from grid
 * For details go through Use-Case doc named 'DeletePurchaseRequestActionService'
 */
class DeletePurchaseRequestActionService extends BaseService implements ActionIntf {

    private static final String DELETE_PURCHASE_REQUEST_SUCCESS_MESSAGE = "Purchase Request has been deleted successfully"
    private static final String DELETE_PURCHASE_REQUEST_FAILURE_MESSAGE = "Purchase Request could not be deleted, Please refresh the Purchase Request"
    private static final String DELETE_FAILURE_MESSAGE_FOR_ITEM = " item(s) are associated with this purchase request"
    private static final String DELETE_FAILURE_MESSAGE_FOR_PO = " Purchase Order(s) are associated with this purchase request"
    private static final String NOT_EDITABLE_MSG = "Selected PR has been sent for approval, make PR editable to delete"
    private static final String PURCHASE_REQUEST_OBJ = "purchaseRequest"
    private static final String DELETED = "deleted"

    PurchaseRequestService purchaseRequestService
    PurchaseOrderService purchaseOrderService
    IndentService indentService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    UnitCacheUtility unitCacheUtility

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
            long purchaseRequestId = Long.parseLong(params.id.toString())

            ProcPurchaseRequest purchaseRequest = (ProcPurchaseRequest) purchaseRequestService.read(purchaseRequestId)

            if (!purchaseRequest) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            if (purchaseRequest.itemCount > 0) {
                result.put(Tools.MESSAGE, purchaseRequest.itemCount + DELETE_FAILURE_MESSAGE_FOR_ITEM)
                return result
            }

            if (purchaseRequest.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_EDITABLE_MSG)
                return result
            }

            int poCount = ProcPurchaseOrder.countByPurchaseRequestId(purchaseRequest.id)
            if (poCount > 0) {
                result.put(Tools.MESSAGE, poCount + DELETE_FAILURE_MESSAGE_FOR_PO)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(PURCHASE_REQUEST_OBJ, purchaseRequest)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PURCHASE_REQUEST_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Delete purchase request from DB and grid
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
            ProcPurchaseRequest purchaseRequest = (ProcPurchaseRequest) preResult.get(PURCHASE_REQUEST_OBJ)

            Boolean updateStatus = purchaseRequestService.delete(purchaseRequest)

            result.put(PURCHASE_REQUEST_OBJ, purchaseRequest)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Failed to delete PR")
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PURCHASE_REQUEST_FAILURE_MESSAGE)
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
     * Set param deleted, budget map and get relevant message
     * @param obj -N/A
     * @return - A map containing message
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(DELETED, Boolean.TRUE.booleanValue())
            result.put(Tools.MESSAGE, DELETE_PURCHASE_REQUEST_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PURCHASE_REQUEST_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, DELETE_PURCHASE_REQUEST_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PURCHASE_REQUEST_FAILURE_MESSAGE)
            return result
        }
    }
}
