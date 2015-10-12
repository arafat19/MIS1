package com.athena.mis.procurement.actions.purchaserequest

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Create Purchase Request and show in the grid
 * For details go through Use-Case doc named 'CreatePurchaseRequestActionService'
 */
class CreatePurchaseRequestActionService extends BaseService implements ActionIntf {

    private static final String PURCHASE_REQUEST_SAVE_SUCCESS_MESSAGE = "Purchase request has been saved successfully"
    private static final String PURCHASE_REQUEST_SAVE_FAILURE_MESSAGE = "Purchase request could not be saved"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Fail to create Purchase request due to invalid input"
    private static final String PROJECT_NOT_FOUND = "Project not found"
    private static final String PURCHASE_REQUEST_OBJ = "purchaseRequestInstance"
    private static final String SERVER_ERROR_MESSAGE = "Fail to create purchase request"

    private final Logger log = Logger.getLogger(getClass())

    PurchaseRequestService purchaseRequestService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    UnitCacheUtility unitCacheUtility

    /**
     * Check all pre conditions and get pr object
     * @param params -N/A
     * @param obj - pr object from controller
     * @return - purchase request object and isError(True/False)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            ProcPurchaseRequest purchaseRequestInstance = (ProcPurchaseRequest) obj
            Project project = (Project) projectCacheUtility.read(purchaseRequestInstance.projectId)

            // checks input validation
            purchaseRequestInstance.validate()
            if (purchaseRequestInstance.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            //checking project existence
            if (!project) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND)
                return result
            }
            purchaseRequestInstance.sentForApproval = false

            result.put(PURCHASE_REQUEST_OBJ, purchaseRequestInstance)
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
     * Create purchase request
     * @param parameters -N/A
     * @param obj - object from pre execute method
     * @return - a map containing newly created pr object
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcPurchaseRequest purchaseRequestInstance = (ProcPurchaseRequest) preResult.get(PURCHASE_REQUEST_OBJ)
            ProcPurchaseRequest newPurchaseRequest = purchaseRequestService.create(purchaseRequestInstance)

            result.put(PURCHASE_REQUEST_OBJ, newPurchaseRequest)
            result.put(Tools.MESSAGE, PURCHASE_REQUEST_SAVE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Failed to create PR")
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
            ProcPurchaseRequest purchaseRequestInstance = (ProcPurchaseRequest) receiveResult.get(PURCHASE_REQUEST_OBJ)
            GridEntity object = new GridEntity()
            String approvedByDirector
            String approvedByProjectDirector
            String isSentForApproval
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
            result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
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
                result.put(Tools.MESSAGE, PURCHASE_REQUEST_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }
}

