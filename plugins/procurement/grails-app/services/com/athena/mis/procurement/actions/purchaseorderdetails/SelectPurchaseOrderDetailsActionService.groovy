package com.athena.mis.procurement.actions.purchaseorderdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.procurement.entity.ProcPurchaseOrderDetails
import com.athena.mis.procurement.entity.ProcPurchaseRequestDetails
import com.athena.mis.procurement.service.PurchaseOrderDetailsService
import com.athena.mis.procurement.service.PurchaseRequestDetailsService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select specific object of selected po details at grid row and show for UI
 *  For details go through Use-Case doc named 'SelectPurchaseOrderDetailsActionService'
 */
class SelectPurchaseOrderDetailsActionService extends BaseService implements ActionIntf {
    private static final String PURCHASE_ORDER_DETAILS_NOT_FOUND_MESSAGE = "Purchase Order Details not found"
    private static final String SERVER_ERROR_MESSAGE = "Fail to retrieve purchase order details"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred for invalid input"
    private static final String PURCHASE_ORDER_DETAILS_OBJ = "purchaseOrderDetails"
    private static final String ITEM_LIST = "itemList"
    private static final String ITEM_ENTITY = "item"

    private final Logger log = Logger.getLogger(getClass())

    PurchaseOrderDetailsService purchaseOrderDetailsService
    PurchaseRequestDetailsService purchaseRequestDetailsService
    @Autowired
    ItemCacheUtility itemCacheUtility
    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * 1. get purchase order details object by id
     * 2. pull purchase request details object
     * 3. pull item object
     * 4. count available materials
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

            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            long id = Long.parseLong(parameterMap.id.toString())

            ProcPurchaseOrderDetails purchaseOrderDetails = purchaseOrderDetailsService.read(id)

            if (purchaseOrderDetails == null) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_DETAILS_NOT_FOUND_MESSAGE)
                return result
            }

            ProcPurchaseRequestDetails purchaseRequestDetails = purchaseRequestDetailsService.read(purchaseOrderDetails.purchaseRequestDetailsId)

            double availableMaterial = purchaseRequestDetails.quantity - purchaseRequestDetails.poQuantity + purchaseOrderDetails.quantity
            Item item = (Item) itemCacheUtility.read(purchaseOrderDetails.itemId)

            Map itemMap = [
                    'id': item.id,
                    'name': item.name + ' (' + availableMaterial + ')',
                    'unit': item.unit,
                    'quantity': availableMaterial,
                    'purchase_request_details_id': purchaseOrderDetails.purchaseRequestDetailsId,
                    'purchase_request_id': purchaseOrderDetails.purchaseRequestId
            ]
            List<GroovyRowResult> itemList = []
            itemList << itemMap

            result.put(ITEM_ENTITY, item)
            result.put(ITEM_LIST, itemList)
            result.put(PURCHASE_ORDER_DETAILS_OBJ, purchaseOrderDetails)
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
            ProcPurchaseOrderDetails purchaseOrderDetails = (ProcPurchaseOrderDetails) receiveResult.get(PURCHASE_ORDER_DETAILS_OBJ)
            List itemList = (List) receiveResult.get(ITEM_LIST)
            result.put(Tools.ENTITY, purchaseOrderDetails)
            result.put(ITEM_ENTITY, receiveResult.get(ITEM_ENTITY))
            result.put(ITEM_LIST, itemList)
            result.put(Tools.VERSION, purchaseOrderDetails.version)
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
                result.put(Tools.MESSAGE, PURCHASE_ORDER_DETAILS_NOT_FOUND_MESSAGE)
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
