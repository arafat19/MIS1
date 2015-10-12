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
 *  Update Procurement Terms and condition object and grid data
 *  For details go through Use-Case doc named 'UpdateProcTermsAndConditionActionService'
 */
class UpdateProcTermsAndConditionActionService extends BaseService implements ActionIntf {

    private static final String PROC_TERMS_AND_CONDITION_UPDATE_SUCCESS_MESSAGE = "Terms and condition has been updated successfully"
    private static final String PROC_TERMS_AND_CONDITION_UPDATE_FAILURE_MESSAGE = "Can not updated Terms and condition"
    private static final String PROC_TERMS_AND_CONDITION_NOT_FOUND = "Terms and condition not found"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred for invalid input"
    private static final String PROC_TERMS_AND_CONDITION_OBJ = "procTermsAndCondition"
    private static final String PURCHASE_ORDER_NOT_FOUND_MESSAGE = "Purchase order not found"
    private static final String NOT_EDITABLE_MSG = "The PO has been sent for approval, make PO editable to update terms and conditions"

    private final Logger log = Logger.getLogger(getClass())

    ProcTermsAndConditionService procTermsAndConditionService
    PurchaseOrderService purchaseOrderService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * Get parameters from UI and build terms & conditions object for update
     * 1. pull previous old object
     * 2. check terms & conditions existence
     * 3. pull purchase order object
     * 4. check po existence
     * 5. if purchase order is sent for approval then terms and conditions can not be updated
     * 6. build terms & conditions object
     * @param parameters -N/A
     * @param obj -get indent object from controller
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id || !params.version) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            long procTermsAndConditionId = Long.parseLong(params.id.toString())
            int version = Integer.parseInt(params.version.toString())
            ProcTermsAndCondition oldProcTermsAndCondition = (ProcTermsAndCondition) procTermsAndConditionService.readByIdAndVersion(procTermsAndConditionId, version)
            if (!oldProcTermsAndCondition) {
                result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_NOT_FOUND)
                return result
            }
            ProcPurchaseOrder purchaseOrder = purchaseOrderService.read(oldProcTermsAndCondition.purchaseOrderId)
            if (!purchaseOrder) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND_MESSAGE)
                return result
            }
            if (purchaseOrder.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_EDITABLE_MSG)
                return result
            }
            ProcTermsAndCondition procTermsAndCondition = buildProcTermsAndConditionObject(params, oldProcTermsAndCondition)
            result.put(PROC_TERMS_AND_CONDITION_OBJ, procTermsAndCondition)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * 1. receive terms & conditions object from pre execute method
     * 2. update terms & conditions
     * @param parameters - N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing purchase order, terms and conditions object, approval status, success/failure message
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcTermsAndCondition procTermsAndCondition = (ProcTermsAndCondition) preResult.get(PROC_TERMS_AND_CONDITION_OBJ)
            procTermsAndConditionService.update(procTermsAndCondition)
            result.put(PROC_TERMS_AND_CONDITION_OBJ, procTermsAndCondition)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Fail to update Terms And Condition")
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_UPDATE_FAILURE_MESSAGE)
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
                failureResult.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_UPDATE_FAILURE_MESSAGE)
            }
            return failureResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            failureResult.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_UPDATE_FAILURE_MESSAGE)
            return failureResult
        }
    }

    /**
     * @return - newly build terms & conditions object
     */
    private ProcTermsAndCondition buildProcTermsAndConditionObject(GrailsParameterMap parameterMap, ProcTermsAndCondition oldProcTermsAndCondition) {
        AppUser user = procSessionUtil.appSessionUtil.getAppUser()
        ProcTermsAndCondition procTermsAndCondition = new ProcTermsAndCondition()
        procTermsAndCondition.id = oldProcTermsAndCondition.id
        procTermsAndCondition.version = oldProcTermsAndCondition.version
        procTermsAndCondition.projectId = oldProcTermsAndCondition.projectId
        procTermsAndCondition.purchaseOrderId = oldProcTermsAndCondition.purchaseOrderId
        procTermsAndCondition.createdOn = oldProcTermsAndCondition.createdOn
        procTermsAndCondition.details = parameterMap.details
        procTermsAndCondition.companyId = oldProcTermsAndCondition.companyId
        procTermsAndCondition.createdBy = oldProcTermsAndCondition.createdBy
        procTermsAndCondition.updatedBy = user.id
        procTermsAndCondition.updatedOn = new Date()

        return procTermsAndCondition
    }
}
