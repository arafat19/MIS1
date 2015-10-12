package com.athena.mis.procurement.actions.purchaserequest

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update purchase request object and grid data
 *  For details go through Use-Case doc named 'UpdatePurchaseRequestActionService'
 */
class UpdatePurchaseRequestActionService extends BaseService implements ActionIntf {

    private static final String PURCHASE_REQUEST_UPDATE_SUCCESS_MESSAGE = "Purchase request has been updated successfully"
    private static final String PURCHASE_REQUEST_UPDATE_FAILURE_MESSAGE = "Purchase request could not be updated"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Fail to update purchase request due to invalid input"
    private static final String PURCHASE_REQUEST_OBJ = "purchaseRequest"
    private static final String IS_APPROVED = "isApproved"
    private static final String BUDGET_OBJ = "budget"
    private static final String SERVER_ERROR_MESSAGE = "Fail to update purchase request"
    private static final String NOT_EDITABLE_MSG = "Selected PR has been sent for approval, make PR editable to update"


    private final Logger log = Logger.getLogger(getClass())

    PurchaseRequestService purchaseRequestService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    UnitCacheUtility unitCacheUtility
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    /**
     * Get parameters from UI and build purchase request object for update
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(IS_APPROVED, Boolean.FALSE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            // check here for required params are present
            if ((!parameterMap.id) || (!parameterMap.version)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            long id = Long.parseLong(parameterMap.id.toString())
            ProcPurchaseRequest oldPurchaseRequest = purchaseRequestService.read(id)
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            Object budget = null

            if (oldPurchaseRequest.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_EDITABLE_MSG)
                return result
            }

            if (oldPurchaseRequest.approvedByDirectorId > 0 || oldPurchaseRequest.approvedByProjectDirectorId > 0) {
                result.put(IS_APPROVED, Boolean.TRUE)
            }

            ProcPurchaseRequest purchaseRequest = buildPurchaseRequest(parameterMap, oldPurchaseRequest)
            purchaseRequest.sentForApproval = false
            purchaseRequest.validate()
            if (purchaseRequest.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            result.put(PURCHASE_REQUEST_OBJ, purchaseRequest)
            result.put(BUDGET_OBJ, budget)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Update Purchase request object in DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -object receive from pre execute method
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcPurchaseRequest purchaseRequest = (ProcPurchaseRequest) preResult.get(PURCHASE_REQUEST_OBJ)
            Integer updateStatus = purchaseRequestService.update(purchaseRequest)
            result.put(PURCHASE_REQUEST_OBJ, purchaseRequest)
            result.put(IS_APPROVED, preResult.get(IS_APPROVED))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Failed to update PR")
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
     * 1. Show newly created pr in grid
     * 2. Wrap grid object
     * 3. Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * -map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            String approvedByDirector
            String approvedByProjectDirector
            String isSentForApproval
            ProcPurchaseRequest purchaseRequestInstance = (ProcPurchaseRequest) receiveResult.get(PURCHASE_REQUEST_OBJ)
            GridEntity object = new GridEntity()
            object.id = purchaseRequestInstance.id
            Project project = (Project) projectCacheUtility.read(purchaseRequestInstance.projectId)
            AppUser systemUser = (AppUser) appUserCacheUtility.read(purchaseRequestInstance.createdBy)
            approvedByDirector = purchaseRequestInstance.approvedByDirectorId ? Tools.YES : Tools.NO
            approvedByProjectDirector = purchaseRequestInstance.approvedByProjectDirectorId ? Tools.YES : Tools.NO
            isSentForApproval = purchaseRequestInstance.sentForApproval ? Tools.YES : Tools.NO

            object.cell = [
                    Tools.LABEL_NEW,
                    purchaseRequestInstance.id,
                    project.name,
                    purchaseRequestInstance.itemCount,
                    approvedByDirector,
                    approvedByProjectDirector,
                    systemUser.username,
                    isSentForApproval
            ]
            result.put(Tools.MESSAGE, PURCHASE_REQUEST_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, PURCHASE_REQUEST_UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Get purchase request object
     * @param parameterMap - serialized parameters from UI
     * @param oldPurchaseRequest -previous object of selected purchase request
     * @param discount - discount
     * @return - newly created purchase request object
     */
    private ProcPurchaseRequest buildPurchaseRequest(GrailsParameterMap parameterMap, ProcPurchaseRequest oldPurchaseRequest) {

        ProcPurchaseRequest purchaseRequest = new ProcPurchaseRequest(parameterMap)
        AppUser systemUser = procSessionUtil.appSessionUtil.getAppUser()

        purchaseRequest.updatedOn = new Date()
        purchaseRequest.updatedBy = systemUser.id

        purchaseRequest.projectId = oldPurchaseRequest.projectId
        purchaseRequest.createdOn = oldPurchaseRequest.createdOn
        purchaseRequest.createdBy = oldPurchaseRequest.createdBy
        purchaseRequest.id = oldPurchaseRequest.id
        purchaseRequest.version = oldPurchaseRequest.version

        purchaseRequest.indentId = purchaseRequest.indentId == null ? 0 : purchaseRequest.indentId

        purchaseRequest.approvedByDirectorId = 0L
        purchaseRequest.approvedByProjectDirectorId = 0L
        purchaseRequest.companyId = oldPurchaseRequest.companyId
        purchaseRequest.itemCount = oldPurchaseRequest.itemCount
        return purchaseRequest
    }
}
