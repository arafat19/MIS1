package com.athena.mis.procurement.actions.purchaserequestdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.entity.ProcPurchaseRequestDetails
import com.athena.mis.procurement.service.PurchaseRequestDetailsService
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Create Purchase Request Details and show in the grid
 * For details go through Use-Case doc named 'CreatePurchaseRequestDetailsActionService'
 */
class CreatePurchaseRequestDetailsActionService extends BaseService implements ActionIntf {

    private static final String PURCHASE_REQUEST_DETAILS_SAVE_SUCCESS_MESSAGE = "Purchase request details has been saved successfully"
    private static final String PURCHASE_REQUEST_DETAILS_SAVE_FAILURE_MESSAGE = "Purchase request could not be saved"
    private static final String BUDGET_DETAILS_NOT_FOUND = "Budget details not found."
    private static final String PURCHASE_REQUEST_NOT_FOUND = "Purchase request has not found"
    private static final String QUANTITY_EXCEEDED_MESSAGE = "Purchase request quantity exceeds project\'s quantity"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Fail to create purchase request due to invalid input"
    private static final String PURCHASE_REQUEST_DETAILS_OBJ = "purchaseRequestDetailsInstance"
    private static final String SERVER_ERROR_MESSAGE = "Fail to create purchase request details"
    private static final String ITEM_ALREADY_EXISTS = "Purchase request details found with same item"
    private static final String IS_PR_APPROVED = "isPRApproved"
    private static final String NOT_EDITABLE_MSG = "The PR has been sent for approval, make PR editable to create PO details"

    private final Logger log = Logger.getLogger(getClass())

    PurchaseRequestService purchaseRequestService
    PurchaseRequestDetailsService purchaseRequestDetailsService
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService

    /**
     * Check all pre conditions and get pr details object
     * @param params -N/A
     * @param obj - pr object from controller
     * @return - purchase request details object and isError(True/False)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            if ((!parameterMap.purchaseRequestId)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            long purchaseRequestId = Long.parseLong(parameterMap.purchaseRequestId.toString())
                ProcPurchaseRequest purchaseRequest = purchaseRequestService.read(purchaseRequestId)
            if (!purchaseRequest) {
                result.put(Tools.MESSAGE, PURCHASE_REQUEST_NOT_FOUND)
                return result
            }

            if (purchaseRequest.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_EDITABLE_MSG)
                return result
            }

            ProcPurchaseRequestDetails prDetails = (ProcPurchaseRequestDetails) obj

            int duplicateCount = ProcPurchaseRequestDetails.countByPurchaseRequestIdAndItemId(prDetails.purchaseRequestId, prDetails.itemId)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, ITEM_ALREADY_EXISTS)
                return result
            }

            Object budgetDetails = budgetImplService.readBudgetDetails(prDetails.projectId, prDetails.itemId)

            if (!budgetDetails) {
                result.put(Tools.MESSAGE, BUDGET_DETAILS_NOT_FOUND)
                return result
            }
            if (prDetails.quantity > budgetDetails.remainingQuantity) {
                result.put(Tools.MESSAGE, QUANTITY_EXCEEDED_MESSAGE)
                return result
            }

            // checks input validation
            prDetails.validate()
            if (prDetails.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            result.put(PURCHASE_REQUEST_DETAILS_OBJ, prDetails)
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
     * Create purchase request details
     * @param parameters -N/A
     * @param obj - object from pre execute method
     * @return - a map containing newly created pr details object
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Boolean isPRApproved = Boolean.FALSE
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcPurchaseRequestDetails purchaseRequestDetails = (ProcPurchaseRequestDetails) preResult.get(PURCHASE_REQUEST_DETAILS_OBJ)
            ProcPurchaseRequest purchaseRequest = purchaseRequestService.read(purchaseRequestDetails.purchaseRequestId)

            //unapprove PR
            if (purchaseRequest.approvedByDirectorId > 0 || purchaseRequest.approvedByProjectDirectorId > 0) {
                purchaseRequest.approvedByDirectorId = 0L
                purchaseRequest.approvedByProjectDirectorId = 0L
                isPRApproved = Boolean.TRUE
            }

            ProcPurchaseRequestDetails newPurchaseRequestDetails = purchaseRequestDetailsService.create(purchaseRequestDetails)

            int updatePR = increaseItemCount(purchaseRequestDetails.purchaseRequestId)

            if (isPRApproved.booleanValue()) {
                //need to increase version because request already changed
                purchaseRequest.version = purchaseRequest.version + 1
                Integer updatePO = updateForApproval(purchaseRequest)
            }
            result.put(PURCHASE_REQUEST_DETAILS_OBJ, newPurchaseRequestDetails)
            result.put(Tools.MESSAGE, PURCHASE_REQUEST_DETAILS_SAVE_SUCCESS_MESSAGE)
            result.put(IS_PR_APPROVED, isPRApproved)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Failed to create PR Details")
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
            ProcPurchaseRequestDetails prDetails = (ProcPurchaseRequestDetails) receiveResult.get(PURCHASE_REQUEST_DETAILS_OBJ)
            GridEntity object = new GridEntity()
            object.id = prDetails.id
            Item item = (Item) itemCacheUtility.read(prDetails.itemId)
            ItemType itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            AppUser systemUser = (AppUser) appUserCacheUtility.read(prDetails.createdBy)
            object.cell = [
                    Tools.LABEL_NEW,
                    prDetails.id,
                    itemType.name,
                    item.name,
                    Tools.formatAmountWithoutCurrency(prDetails.quantity) + Tools.SINGLE_SPACE + item.unit,
                    Tools.makeAmountWithThousandSeparator(prDetails.rate),
                    Tools.makeAmountWithThousandSeparator((prDetails.quantity * prDetails.rate)),
                    DateUtility.getLongDateForUI(prDetails.createdOn),
                    systemUser.username
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
                result.put(Tools.MESSAGE, PURCHASE_REQUEST_DETAILS_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    // Update ProcPurchaseRequest for approval
    private static final String UPDATE_QUERY = """
                      UPDATE proc_purchase_request SET
                          approved_by_director_id=:approvedByDirectorId,
                          approved_by_project_director_id=:approvedByProjectDirectorId,
                          version = version + 1
                      WHERE
                          id=:id AND
                          version=:version
                        """

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

    private static final String QUERY_INCREASE = """
                      UPDATE proc_purchase_request SET
                          item_count = item_count + 1,
                          version = version + 1
                      WHERE
                          id=:purchaseRequestId
                      """

    private int increaseItemCount(long purchaseRequestId) {
        int itemCount = executeUpdateSql(QUERY_INCREASE, [purchaseRequestId: purchaseRequestId])
        if (itemCount <= 0) {
            throw new RuntimeException('Failed to increase purchase request')
        }
        return itemCount
    }
}