package com.athena.mis.procurement.actions.proctermsandcondition

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.entity.ProcTermsAndCondition
import com.athena.mis.procurement.service.ProcTermsAndConditionService
import com.athena.mis.procurement.service.PurchaseOrderService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Delete Procurement Terms and condition from DB as well as from grid.
 * For details go through Use-Case doc named 'DeleteProcTermsAndConditionActionService'
 */
class DeleteProcTermsAndConditionActionService extends BaseService implements ActionIntf {

    private static final String PROC_TERMS_AND_CONDITION_DELETE_SUCCESS_MESSAGE = "Terms and condition has been deleted successfully"
    private static final String PROC_TERMS_AND_CONDITION_DELETE_FAILURE_MESSAGE = "Terms and condition could not be deleted, Please refresh page"
    private static final String PROC_TERMS_AND_CONDITION_NOT_FOUND = "Terms and condition not found"
    private static final String PROC_TERMS_AND_CONDITION_OBJ = "procTermsAndCondition"
    private static final String DELETED = "deleted"
    private static final String PURCHASE_ORDER_NOT_FOUND_MESSAGE = "Purchase order not found"
    private static final String NOT_EDITABLE_MSG = "The PO has been sent for approval, make PO editable to delete terms and conditions"

    ProcTermsAndConditionService procTermsAndConditionService
    PurchaseOrderService purchaseOrderService
    @Autowired
    ProcSessionUtil procSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    /**
     * 1. pull procTermsAndCondition object by procTermsAndConditionId
     * 2. pull purchase order object
     * 3. check po existence
     * 4. if purchase order is sent for approval then terms and conditions can not be deleted
     * @param params - serialize parameters from UI
     * @param obj -N/A
     * @return - a map containing procTermsAndCondition object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long procTermsAndConditionId = Long.parseLong(params.id.toString())
            ProcTermsAndCondition procTermsAndCondition = procTermsAndConditionService.read(procTermsAndConditionId)
            if (!procTermsAndCondition) {
                result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_NOT_FOUND)
                return result
            }
            ProcPurchaseOrder purchaseOrder = purchaseOrderService.read(procTermsAndCondition.purchaseOrderId)
            if (!purchaseOrder) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND_MESSAGE)
                return result
            }
            if (purchaseOrder.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_EDITABLE_MSG)
                return result
            }
            result.put(PROC_TERMS_AND_CONDITION_OBJ, procTermsAndCondition)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * 1. receive procTermsAndCondition object
     * 2. delete selected terms & conditions
     * @param params - N/A
     * @param obj - receive from pre execute method
     * @return - a map containing purchase order object isError(True/False)
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            ProcTermsAndCondition procTermsAndCondition = (ProcTermsAndCondition) receiveResult.get(PROC_TERMS_AND_CONDITION_OBJ)
            procTermsAndConditionService.delete(procTermsAndCondition.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Fail to delete Terms And Condition")
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_DELETE_FAILURE_MESSAGE)
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
     * Set delete operation True
     * @param obj- N/A
     * @return - a map containing deleted=True and success msg
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(DELETED, Boolean.TRUE.booleanValue())
            result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_DELETE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_DELETE_FAILURE_MESSAGE)
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
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_DELETE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_DELETE_FAILURE_MESSAGE)
            return result
        }
    }
}