package com.athena.mis.procurement.actions.purchaseorderdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 * Give Item List For Purchase Order Details.
 * For details go through Use-Case doc named 'GetForItemListForPurchaseOrderDetailsActionService'
 */
class GetForItemListForPurchaseOrderDetailsActionService extends BaseService implements ActionIntf {

    private static final String SERVER_ERROR_MESSAGE = "Fail to load Purchase Order List"
    private static final String DEFAULT_ERROR_MESSAGE = "Can't Load Purchase Order List"
    private static final String ENTITY_NOT_FOUND_ERROR_MESSAGE = "No entity found with this id or might have been deleted by someone!"
    private static final String ITEM_LIST = "itemList"
    private static final String ITEM_TYPE_ID = "itemTypeId"
    private static final String PURCHASE_REQUEST_ID = "purchaseRequestId"
    private static final String PURCHASE_ORDER_ID = "purchaseOrderId"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * 1. check entity existence
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing purchase request id, purchase order id, item type id
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            if (!parameterMap.purchaseRequestId || !parameterMap.itemTypeId || !parameterMap.purchaseOrderId) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            long purchaseRequestId = Long.parseLong(parameterMap.purchaseRequestId.toString())
            long purchaseOrderId = Long.parseLong(parameterMap.purchaseOrderId.toString())
            int itemTypeId = Integer.parseInt(parameterMap.itemTypeId.toString())

            result.put(PURCHASE_REQUEST_ID, purchaseRequestId)
            result.put(PURCHASE_ORDER_ID, purchaseOrderId)
            result.put(ITEM_TYPE_ID, itemTypeId)
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
     * 1. Get item list by item type id, pr id & po id
     * @param parameters - N/A
     * @param obj - object receive from pre execute method
     * @return - item list
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            long purchaseRequestId = (long) preResult.get(PURCHASE_REQUEST_ID)
            long purchaseOrderId = (long) preResult.get(PURCHASE_ORDER_ID)
            int itemTypeId = (int) preResult.get(ITEM_TYPE_ID)

            List<GroovyRowResult> itemList = []
            itemList = listItemByPurchaseRequestIdOrderIdAndItemType(purchaseRequestId, purchaseOrderId, itemTypeId)
            result.put(ITEM_LIST, Tools.listForKendoDropdown(itemList, null, null))
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
     * Get item list
     * @param obj - object receive from execute method
     * @return - a map containing item list
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            List<GroovyRowResult> itemList = (List<GroovyRowResult>) receiveResult.get(ITEM_LIST)
            result.put(ITEM_LIST, itemList)
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
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    private static final String PROC_PURCHASE_REQUEST_DETAILS_SELECT_QUERY = """
        SELECT DISTINCT prd.item_id AS id, m.name || ' (' || (prd.quantity - prd.po_quantity) || ')' AS name, m.unit,
        prd.id AS purchase_request_details_id, prd.purchase_request_id as purchase_request_id,(prd.quantity - prd.po_quantity) AS quantity
        FROM proc_purchase_request_details prd
        LEFT JOIN item m ON m.id=prd.item_id
        WHERE prd.purchase_request_id =:purchaseRequestId
        AND m.item_type_id =:itemType
        AND (prd.quantity -  prd.po_quantity) > 0
        AND prd.item_id NOT IN(
            SELECT item_id FROM proc_purchase_order_details
            WHERE purchase_order_id=:purchaseOrderId)
        ORDER BY name
        """
    /**
     *
     * @param purchaseRequestId - pr id
     * @param purchaseOrderId - po id
     * @param itemType - item type
     * @return - materials list
     */
    private List<GroovyRowResult> listItemByPurchaseRequestIdOrderIdAndItemType(long purchaseRequestId, long purchaseOrderId, int itemType) {
        Map queryParams = [
                purchaseRequestId: purchaseRequestId,
                itemType: itemType,
                purchaseOrderId: purchaseOrderId
        ]
        List<GroovyRowResult> lstMaterials = executeSelectSql(PROC_PURCHASE_REQUEST_DETAILS_SELECT_QUERY, queryParams)
        return lstMaterials
    }
}
