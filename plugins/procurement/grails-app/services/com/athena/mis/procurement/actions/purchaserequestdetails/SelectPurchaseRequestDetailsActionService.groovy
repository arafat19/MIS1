package com.athena.mis.procurement.actions.purchaserequestdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.procurement.entity.ProcPurchaseRequestDetails
import com.athena.mis.procurement.service.PurchaseRequestDetailsService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select specific object of selected pr details at grid row and show for UI
 *  For details go through Use-Case doc named 'SelectPurchaseRequestDetailsActionService'
 */
class SelectPurchaseRequestDetailsActionService extends BaseService implements ActionIntf {

    private static final String PURCHASE_REQUEST_DETAILS_NOT_FOUND_MESSAGE = "Purchase request details not found"
    private static final String SERVER_ERROR_MESSAGE = "Can't get purchase request details"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred for invalid input"
    private static final String PURCHASE_REQUEST_DETAILS_OBJ = "prDetails"
    private static final String ITEM_LIST = "itemList"
    private static final String PURCHASE_REQUEST_MAP = "purchaseRequestMap"

    private final Logger log = Logger.getLogger(getClass())

    PurchaseRequestDetailsService purchaseRequestDetailsService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }
    /**
     * 1. get purchase request details object by id
     * 2. pull purchase request details object
     * 3. build new purchase request details object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            // check here for required params are present
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            long id = Long.parseLong(parameterMap.id.toString())
            ProcPurchaseRequestDetails purchaseRequestDetails = purchaseRequestDetailsService.read(id)
            if (!purchaseRequestDetails) {
                result.put(Tools.MESSAGE, PURCHASE_REQUEST_DETAILS_NOT_FOUND_MESSAGE)
                return result
            }

            Map purchaseRequestMap = buildPurchaseRequestMap(purchaseRequestDetails)
            Map lstItem = purchaseRequestMap.itemList
            List itemList = []
            itemList << lstItem
            result.put(PURCHASE_REQUEST_MAP, purchaseRequestMap)
            result.put(ITEM_LIST, itemList)
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
     * Get purchase request object
     * @param prDetails - purchase request details id
     * @return - purchase request object
     */
    private LinkedHashMap buildPurchaseRequestMap(ProcPurchaseRequestDetails prDetails) {
        Project project = (Project) projectCacheUtility.read(prDetails.projectId)
        Object budgetDetails = budgetImplService.readBudgetDetails(prDetails.projectId, prDetails.itemId)
        double availableQuantity = budgetDetails.remainingQuantity + prDetails.quantity
        Item item = (Item) itemCacheUtility.read(prDetails.itemId)
        Map itemForList = [
                'id': item.id,
                'name': item.name + Tools.PARENTHESIS_START + availableQuantity + Tools.PARENTHESIS_END,
                'unit': item.unit,
                'quantity': availableQuantity
        ]

        Map purchaseRequestMap = [
                projectId: prDetails.projectId,
                purchaseRequestId: prDetails.purchaseRequestId,
                projectName: project.name,
                itemList: itemForList,
                itemId: item.id,
                itemTypeId: item.itemTypeId
        ]
        return purchaseRequestMap
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }
    /**
     * Show selected object on the form
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            ProcPurchaseRequestDetails purchaseRequestDetails = (ProcPurchaseRequestDetails) receiveResult.get(PURCHASE_REQUEST_DETAILS_OBJ)
            Map purchaseRequestDetailsMap = (LinkedHashMap) receiveResult.get(PURCHASE_REQUEST_MAP)
            result.put(Tools.ENTITY, purchaseRequestDetails)
            result.put(PURCHASE_REQUEST_MAP, purchaseRequestDetailsMap)
            result.put(ITEM_LIST, receiveResult.get(ITEM_LIST))
            result.put(Tools.VERSION, purchaseRequestDetails.version)
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
                result.put(Tools.MESSAGE, PURCHASE_REQUEST_DETAILS_NOT_FOUND_MESSAGE)
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
