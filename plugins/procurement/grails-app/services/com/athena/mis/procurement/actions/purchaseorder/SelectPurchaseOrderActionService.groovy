package com.athena.mis.procurement.actions.purchaseorder

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.service.PurchaseOrderService
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select specific object of selected po at grid row and show for UI
 *  For details go through Use-Case doc named 'SelectPurchaseOrderActionService'
 */
class SelectPurchaseOrderActionService extends BaseService implements ActionIntf {

    private static final String PURCHASE_ORDER_NOT_FOUND_MESSAGE = "Purchase order not found"
    private static final String SERVER_ERROR_MESSAGE = "Fail to retrieve purchase order"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred for invalid input"
    private static final String PURCHASE_ORDER_OBJ = "purchaseOrder"
    private static final String PURCHASE_REQUEST_MAP = "purchaseRequestMap"

    private final Logger log = Logger.getLogger(getClass())


    PurchaseOrderService purchaseOrderService
    PurchaseRequestService purchaseRequestService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }
    /**
     * Get purchase order object by id
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
            ProcPurchaseOrder purchaseOrder = purchaseOrderService.read(id)
            if (purchaseOrder == null) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND_MESSAGE)
                return result
            }
            // Get ProcPurchaseRequestDetails information
            LinkedHashMap purchaseRequestMap = buildPurchaseRequestMap(purchaseOrder.purchaseRequestId)

            result.put(PURCHASE_ORDER_OBJ, purchaseOrder)
            result.put(PURCHASE_REQUEST_MAP, purchaseRequestMap)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
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

            ProcPurchaseOrder purchaseOrder = (ProcPurchaseOrder) receiveResult.get(PURCHASE_ORDER_OBJ)
            LinkedHashMap purchaseRequestDetailsMap = (LinkedHashMap) receiveResult.get(PURCHASE_REQUEST_MAP)

            result.put(Tools.ENTITY, purchaseOrder)
            result.put(PURCHASE_REQUEST_MAP, purchaseRequestDetailsMap)
            result.put(Tools.VERSION, purchaseOrder.version)
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
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND_MESSAGE)
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
     * @param purchaseRequestId - purchase request id
     * @return - purchase request object
     */
    private LinkedHashMap buildPurchaseRequestMap(long purchaseRequestId) {
        ProcPurchaseRequest purchaseRequest = purchaseRequestService.read(purchaseRequestId)
        Project project = (Project) projectCacheUtility.read(purchaseRequest.projectId)
        LinkedHashMap purchaseRequestDetailsMap = [
                purchaseRequestId: purchaseRequest.id,
                projectId: project.id,
                projectName: project.name
        ]
        return purchaseRequestDetailsMap
    }
}
