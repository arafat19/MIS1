package com.athena.mis.procurement.actions.purchaserequest

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Approve Purchase Request For showing on the Dash Board
 * For details go through Use-Case doc named 'ApprovePRForDashBoardActionService'
 */
class ApprovePRForDashBoardActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String PURCHASE_REQUEST_SAVE_SUCCESS_MESSAGE = "Purchase Request has been approved successfully."
    private static final String PURCHASE_REQUEST_SAVE_FAILURE_MESSAGE = "Purchase Request could not be approved."
    private static final String PURCHASE_REQUEST_NOT_FOUND = "Purchase Request not found."
    private static final String ALREADY_APPROVED_DIRECTOR = "Purchase Request already approved by director."
    private static final String ALREADY_APPROVED_PROJECT_DIRECTOR = "Purchase Request already approved by project director."
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Fail to approve Purchase Request due to Invalid Input"
    private static final String PR_HAS_NO_ITEMS = "PR has no item(s)"
    private static final String PURCHASE_REQUEST_OBJ = "purchaseRequestInstance";
    private static final String SYSTEM_USER_OBJ = "systemUserInstance";
    private static final String SERVER_ERROR_MESSAGE = "Fail to approve Purchase Request"
    private static final String IS_USER_DIRECTOR = "isUserDirector"
    private static final String IS_USER_PROJECT_DIRECTOR = "isUserProjectDirector"
    private static final String HAS_NO_RIGHT_MESSAGE = "Logged-In User has no right to approve the purchase Request."
    private static final String IS_BOTH_APPROVED = "isBothApproved"
    private static final String NOT_SEND_ERROR = "Purchase Request has not been sent for approval"

    PurchaseRequestService purchaseRequestService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    /**
     * Check all pre conditions
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing pr object, system user obj, isDirector, isProjectManager & isError(True/False)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            boolean isUserDirector = procSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_DIRECTOR)
            boolean isUserProjectDirector = procSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR)

            // user has not right to approve PR
            if (!isUserDirector && !isUserProjectDirector) {
                result.put(Tools.MESSAGE, HAS_NO_RIGHT_MESSAGE)
                return result
            }

            long id = Long.parseLong(parameterMap.id.toString())
            ProcPurchaseRequest purchaseRequestInstance = purchaseRequestService.read(id)

            //checking purchaseRequest Instance existence
            if (!purchaseRequestInstance) {
                result.put(Tools.MESSAGE, PURCHASE_REQUEST_NOT_FOUND)
                return result
            }

            // if the selected PR is not sent for approval then it can't be approved
            if (!purchaseRequestInstance.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_SEND_ERROR)
                return result
            }

            int approveAs = Integer.parseInt(parameterMap.approveAs.toString())
            AppUser systemUser = procSessionUtil.appSessionUtil.getAppUser()

            // if it is already approved by director
            if (purchaseRequestInstance.approvedByDirectorId && (approveAs == roleTypeCacheUtility.ROLE_TYPE_DIRECTOR)) {
                result.put(Tools.MESSAGE, ALREADY_APPROVED_DIRECTOR)
                return result
            }

            // if it is already approved by project director
            else if (purchaseRequestInstance.approvedByProjectDirectorId && (approveAs == roleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR)) {
                result.put(Tools.MESSAGE, ALREADY_APPROVED_PROJECT_DIRECTOR)
                return result
            }

            if (purchaseRequestInstance.itemCount <= 0) {
                result.put(Tools.MESSAGE, PR_HAS_NO_ITEMS)
                return result
            }

            result.put(IS_USER_DIRECTOR, isUserDirector)
            result.put(IS_USER_PROJECT_DIRECTOR, isUserProjectDirector)
            result.put(PURCHASE_REQUEST_OBJ, purchaseRequestInstance)
            result.put(SYSTEM_USER_OBJ, systemUser)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Get purchase request objects
     * @param parameters - N/A
     * @param obj -  object from pre execute method
     * @return - a map with purchase request object and relevant message
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            GrailsParameterMap params = (GrailsParameterMap) parameters
            ProcPurchaseRequest purchaseRequestInstance = (ProcPurchaseRequest) preResult.get(PURCHASE_REQUEST_OBJ)
            AppUser systemUser = (AppUser) preResult.get(SYSTEM_USER_OBJ)
            int approveAs = Integer.parseInt(params.approveAs.toString())

            //set approval data of purchase request
            boolean isUserDirector = (boolean) preResult.get(IS_USER_DIRECTOR)
            boolean isUserProjectDirector = (boolean) preResult.get(IS_USER_PROJECT_DIRECTOR)
            //set approval data of purchase request
            if (isUserDirector && (approveAs == roleTypeCacheUtility.ROLE_TYPE_DIRECTOR)) {
                purchaseRequestInstance.approvedByDirectorId = systemUser.id
            } else if (isUserProjectDirector && (approveAs == roleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR)) {
                purchaseRequestInstance.approvedByProjectDirectorId = systemUser.id

            } else {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
                return result
            }

            int purchaseOrderUpdateStatus = updateForApproval(purchaseRequestInstance)

            result.put(PURCHASE_REQUEST_OBJ, purchaseRequestInstance)
            result.put(Tools.MESSAGE, PURCHASE_REQUEST_SAVE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
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
     * 1. Show newly approved pr in grid
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
            boolean isBothApproved = false

            ProcPurchaseRequest purchaseRequestInstance = (ProcPurchaseRequest) receiveResult.get(PURCHASE_REQUEST_OBJ)
            AppUser user = (AppUser) appUserCacheUtility.read(purchaseRequestInstance.createdBy)
            GridEntity object = new GridEntity()
            object.id = purchaseRequestInstance.id

            String approvedByDirector = purchaseRequestInstance.approvedByDirectorId ? Tools.YES : Tools.NO
            String approvedByProjectDirector = purchaseRequestInstance.approvedByProjectDirectorId ? Tools.YES : Tools.NO

            object.cell = [Tools.LABEL_NEW,
                    purchaseRequestInstance.id,
                    user.username,
                    approvedByDirector,
                    approvedByProjectDirector
            ]
            if (purchaseRequestInstance.approvedByDirectorId > 0 && purchaseRequestInstance.approvedByProjectDirectorId > 0) {
                isBothApproved = true
            }
            result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            result.put(Tools.ENTITY, object)
            result.put(IS_BOTH_APPROVED, isBothApproved)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
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
        LinkedHashMap failureResult = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                failureResult.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                failureResult.put(Tools.MESSAGE, PURCHASE_REQUEST_SAVE_FAILURE_MESSAGE)
            }
            return failureResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            failureResult.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return failureResult
        }
    }

    private static final String UPDATE_QUERY = """
                      UPDATE proc_purchase_request SET
                          approved_by_director_id=:approvedByDirectorId,
                          approved_by_project_director_id=:approvedByProjectDirectorId,
                          version = version + 1
                      WHERE
                          id=:id AND
                          version=:version
                        """
    /**
     * Update Purchase request for approval
     * @param purchaseRequest
     * @return - int value for success(e.g- 1) or throw exception with message for failure
     */
    private int updateForApproval(ProcPurchaseRequest purchaseRequest) {
        Map queryParams = [
                id: purchaseRequest.id,
                version: purchaseRequest.version,
                approvedByDirectorId: purchaseRequest.approvedByDirectorId,
                approvedByProjectDirectorId: purchaseRequest.approvedByProjectDirectorId
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to approve PR")
        }
        return updateCount
    }
}
