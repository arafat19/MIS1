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
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update purchase request details object and grid data
 *  For details go through Use-Case doc named 'UpdatePurchaseRequestDetailsActionService'
 */
class UpdatePurchaseRequestDetailsActionService extends BaseService implements ActionIntf {

    private static
    final String PURCHASE_REQUEST_DETAILS_UPDATE_SUCCESS_MESSAGE = "Purchase request details has been updated successfully"
    private static final String PURCHASE_REQUEST_UPDATE_FAILURE_MESSAGE = "Purchase request could not be updated"
    private static final String BUDGET_DETAILS_NOT_FOUND = "Budget details not found."
    private static final String QUANTITY_EXCEEDED_MESSAGE = "Purchase request quantity exceeds project\'s item quantity."
    private static final String QUANTITY_LOWER_THAN_PO = "PR item quantity can't be lower than PO Item quantity."
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Fail to update purchase request due to invalid input"
    private static final String PURCHASE_REQUEST_DETAILS_OBJ = "purchaseRequestDetails"
    private static final String SERVER_ERROR_MESSAGE = "Fail to update purchase request"
    private static String IS_PR_APPROVED = "isPRApproved"
    private static final String NOT_EDITABLE_MSG = "The PR has been sent for approval, make PR editable to edit PR details"

    private final Logger log = Logger.getLogger(getClass())

    PurchaseRequestDetailsService purchaseRequestDetailsService
    PurchaseRequestService purchaseRequestService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService

    /**
     * Get parameters from UI and build purchase request details object for update
     * 1. check for required params are present
     * 2. check if budgetDetails object exist
     * 3. check if edited quantity is allowed
     * 4. pull old Purchase Request Details object
     * 5. check available quantity in budget
     * 5. checks input validation
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap param = (GrailsParameterMap) parameters

            // check here for required params are present
            if ((!param.id) || (!param.version) || (!param.quantity)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            //check if edited quantity is allowed
            long id = Long.parseLong(param.id.toString())
            double quantity = Double.parseDouble(param.quantity)

            ProcPurchaseRequestDetails oldPRDetails = purchaseRequestDetailsService.read(id)

            ProcPurchaseRequest purchaseRequest = purchaseRequestService.read(oldPRDetails.purchaseRequestId)
            if (purchaseRequest.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_EDITABLE_MSG)
                return result
            }

            if (oldPRDetails.quantity != quantity) {
                Object budgetDetails = budgetImplService.readBudgetDetails(oldPRDetails.projectId, oldPRDetails.itemId)
                if (!budgetDetails) {
                    result.put(Tools.MESSAGE, BUDGET_DETAILS_NOT_FOUND)
                    return result
                }

                double availableQuantity = budgetDetails.remainingQuantity + oldPRDetails.quantity - quantity
                if (availableQuantity < 0) {
                    result.put(Tools.MESSAGE, QUANTITY_EXCEEDED_MESSAGE)
                    return result
                }

                if (quantity < oldPRDetails.poQuantity) {
                    result.put(Tools.MESSAGE, QUANTITY_LOWER_THAN_PO)
                    return result
                }
            }

            ProcPurchaseRequestDetails purchaseRequestDetails = buildPurchaseRequestDetails(param, oldPRDetails)
            // checks input validation
            purchaseRequestDetails.validate()
            if (purchaseRequestDetails.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            result.put(PURCHASE_REQUEST_DETAILS_OBJ, purchaseRequestDetails)
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
     * 1. receive pr, pr details object from pre execute method
     * 2. use flag isPOApproved to send mail to Director & Project Director
     * 3. if po is already approved set approvedByDirectorId & approvedByProjectDirectorId 0L
     * 4. update pr details
     * 5. update pr details for quantity
     * 6. update  pr for approval
     * @param parameters - N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing purchase request details, approval status, success/failure message
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Boolean isPRApproved = Boolean.FALSE
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcPurchaseRequestDetails purchaseRequestDetails = (ProcPurchaseRequestDetails) preResult.get(PURCHASE_REQUEST_DETAILS_OBJ)
            int updateStatus = purchaseRequestDetailsService.update(purchaseRequestDetails)

            result.put(PURCHASE_REQUEST_DETAILS_OBJ, purchaseRequestDetails)
            result.put(IS_PR_APPROVED, isPRApproved)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Failed to update PR Details")
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
     * 1. Show newly created pr details in grid
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
            String updatedDate
            AppUser updatedBy
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
            result.put(Tools.MESSAGE, PURCHASE_REQUEST_DETAILS_UPDATE_SUCCESS_MESSAGE)
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
     * Build purchase request details object with new params
     * @param parameterMap
     * @param oldPurchaseRequestDetails
     * @return purchase request details object
     */
    private ProcPurchaseRequestDetails buildPurchaseRequestDetails(GrailsParameterMap parameterMap, ProcPurchaseRequestDetails oldPurchaseRequestDetails) {

        ProcPurchaseRequestDetails purchaseRequestDetails = new ProcPurchaseRequestDetails(parameterMap)
        AppUser systemUser = procSessionUtil.appSessionUtil.getAppUser()
        int version = Integer.parseInt(parameterMap.version.toString())
        //ensure internal inputs
        purchaseRequestDetails.updatedOn = new Date()
        purchaseRequestDetails.updatedBy = systemUser.id

        // set old purchaseRequest property to new purchaseRequest for validation
        purchaseRequestDetails.projectId = oldPurchaseRequestDetails.projectId
        purchaseRequestDetails.poQuantity = oldPurchaseRequestDetails.poQuantity
        purchaseRequestDetails.createdOn = oldPurchaseRequestDetails.createdOn
        purchaseRequestDetails.createdBy = oldPurchaseRequestDetails.createdBy
        purchaseRequestDetails.itemId = oldPurchaseRequestDetails.itemId
        purchaseRequestDetails.id = oldPurchaseRequestDetails.id
        purchaseRequestDetails.version = version
        purchaseRequestDetails.companyId = oldPurchaseRequestDetails.companyId

        return purchaseRequestDetails
    }

}
