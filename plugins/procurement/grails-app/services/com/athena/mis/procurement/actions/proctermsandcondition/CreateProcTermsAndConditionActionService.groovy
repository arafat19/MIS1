package com.athena.mis.procurement.actions.proctermsandcondition

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
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
 * Create Procurement Terms and condition and show in the grid
 * For details go through Use-Case doc named 'CreateProcTermsAndConditionActionService'
 */
class CreateProcTermsAndConditionActionService extends BaseService implements ActionIntf {

    private static final String PROC_TERMS_AND_CONDITION_SAVE_SUCCESS_MESSAGE = "Terms and condition has been saved successfully"
    private static final String PROC_TERMS_AND_CONDITION_SAVE_FAILURE_MESSAGE = "Can not saved Terms and condition"
    private static final String PROC_TERMS_AND_CONDITION_OBJ = "procTermsAndCondition"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred for invalid input"
    private static final String PURCHASE_ORDER_NOT_FOUND_MESSAGE = "Purchase order not found"
    private static final String NOT_EDITABLE_MSG = "The PO has been sent for approval, make PO editable to create terms and conditions"

    private final Logger log = Logger.getLogger(getClass())

    ProcTermsAndConditionService procTermsAndConditionService
    PurchaseOrderService purchaseOrderService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * 1. pull purchase order object
     * 2. check po existence
     * 3. if purchase order is sent for approval then terms and conditions can not be created
     * 4. build ProcTermsAndCondition object
     * @param parameters -serialized parameters from UI.
     * @param obj - N/A
     * @return - a map containing po object & terms and condition object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.purchaseOrderId) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            long purchaseOrderId = Long.parseLong(params.purchaseOrderId.toString())
            ProcPurchaseOrder purchaseOrder = purchaseOrderService.read(purchaseOrderId)
            if (!purchaseOrder) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND_MESSAGE)
                return result
            }
            if (purchaseOrder.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_EDITABLE_MSG)
                return result
            }
            ProcTermsAndCondition procTermsAndCondition = buildProcTermsAndConditionObject(params, purchaseOrder)
            result.put(PROC_TERMS_AND_CONDITION_OBJ, procTermsAndCondition)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * 1. receive po and terms & conditions object from pre execute method
     * 2. create terms & conditions
     * @param parameters - N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing po object, terms & conditions object , user id
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcTermsAndCondition procTermsAndCondition = (ProcTermsAndCondition) preResult.get(PROC_TERMS_AND_CONDITION_OBJ)

            ProcTermsAndCondition returnProcTermsAndCondition = procTermsAndConditionService.create(procTermsAndCondition)
            if (!returnProcTermsAndCondition) {
                result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_SAVE_FAILURE_MESSAGE)
                return result
            }

            result.put(PROC_TERMS_AND_CONDITION_OBJ, returnProcTermsAndCondition)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Fail to create Terms And Condition")
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_SAVE_FAILURE_MESSAGE)
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
     * 1. Show newly created terms & conditions in grid
     * 2. Wrap grid object
     * 3. Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            ProcTermsAndCondition procTermsAndCondition = (ProcTermsAndCondition) receiveResult.get(PROC_TERMS_AND_CONDITION_OBJ)
            GridEntity object = new GridEntity()
            object.id = procTermsAndCondition.id
            AppUser appUser = (AppUser) appUserCacheUtility.read(procTermsAndCondition.createdBy)
            object.cell = [Tools.LABEL_NEW, procTermsAndCondition.details, appUser.username]
            result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap failureResult = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                failureResult.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                failureResult.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_SAVE_FAILURE_MESSAGE)
            }
            return failureResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            failureResult.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_SAVE_FAILURE_MESSAGE)
            return failureResult
        }
    }

    /**
     * Get procTermsAndCondition object
     * @param parameterMap - serialized parameters from UI
     * @param purchaseOrder - purchase order object
     * @return - new procTermsAndCondition object
     */
    private ProcTermsAndCondition buildProcTermsAndConditionObject(GrailsParameterMap parameterMap, ProcPurchaseOrder purchaseOrder) {
        AppUser user = procSessionUtil.appSessionUtil.getAppUser()
        ProcTermsAndCondition procTermsAndCondition = new ProcTermsAndCondition()
        procTermsAndCondition.version = 0
        procTermsAndCondition.projectId = purchaseOrder.projectId
        procTermsAndCondition.purchaseOrderId = purchaseOrder.id
        procTermsAndCondition.companyId = user.companyId
        procTermsAndCondition.details = parameterMap.details
        procTermsAndCondition.createdBy = user.id
        procTermsAndCondition.createdOn = new Date()
        procTermsAndCondition.updatedBy = 0L
        procTermsAndCondition.updatedOn = new Date()

        return procTermsAndCondition
    }
}
