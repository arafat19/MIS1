package com.athena.mis.procurement.actions.purchaseorder

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.entity.ProcTermsAndCondition
import com.athena.mis.procurement.service.PurchaseOrderService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Delete Purchase Order and remove from grid
 * For details go through Use-Case doc named 'DeletePurchaseOrderActionService'
 */
class DeletePurchaseOrderActionService extends BaseService implements ActionIntf {

    private static final String INVENTORY_IN_FOUND = "Purchase Order has association within Inventory Transaction(s)"
    private static final String DELETE_PURCHASE_ORDER_SUCCESS_MESSAGE = "Purchase Order has been deleted successfully"
    private static final String DELETE_PURCHASE_ORDER_FAILURE_MESSAGE = "Purchase Order could not be deleted, Please refresh the page"
    private static final String HAS_ASSOCIATION_MESSAGE_PURCHASE_REQUEST_DETAILS_ITEM = " item(s) associated with this purchase order"
    private static final String PURCHASE_ORDER_OBJ = "purchaseOrder"
    private static final String DELETED = "deleted"
    private static final String HAS_ASSOCIATION_MESSAGE_VOUCHER = " voucher is associated with this purchase order"
    private static final String HAS_ASSOCIATION_MESSAGE_TRANSPORT_COST = " transport cost is associated with this purchase order"
    private static final String HAS_ASSOCIATION_MSG_TERMS_AND_CON = " terms and conditions cost are associated with this purchase order"

    private final Logger log = Logger.getLogger(getClass())

    PurchaseOrderService purchaseOrderService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService

    /**
     * Check all pre conditions for delete
     * @param parameters - serialize parameters from UI
     * @param obj - N/A
     * @return - map with purchase order object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long purchaseOrderId = Long.parseLong(params.id.toString())
            ProcPurchaseOrder purchaseOrder = (ProcPurchaseOrder) purchaseOrderService.read(purchaseOrderId)
            if (!purchaseOrder) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            if (purchaseOrder.itemCount > 0) {
                result.put(Tools.MESSAGE, purchaseOrder.itemCount.toString() + HAS_ASSOCIATION_MESSAGE_PURCHASE_REQUEST_DETAILS_ITEM)
                return result
            }
            Map preResult = (Map) hasAssociation(purchaseOrder)
            Boolean hasAssociation = (Boolean) preResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(PURCHASE_ORDER_OBJ, purchaseOrder)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PURCHASE_ORDER_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Delete purchase order from DB and grid
     * @param parameters - N/A
     * @param obj - object from pre execute method
     * @return - a map containing po object
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcPurchaseOrder purchaseOrder = (ProcPurchaseOrder) preResult.get(PURCHASE_ORDER_OBJ)
            Boolean updateStatus = purchaseOrderService.delete(purchaseOrder.id, purchaseOrder.companyId)

            if (!updateStatus.booleanValue()) {
                result.put(Tools.MESSAGE, DELETE_PURCHASE_ORDER_FAILURE_MESSAGE)
                return result
            }
            result.put(PURCHASE_ORDER_OBJ, purchaseOrder)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PURCHASE_ORDER_FAILURE_MESSAGE)
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
     * Set param deleted and get relevant message
     * @param obj -N/A
     * @return - A map containing message
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(DELETED, Boolean.TRUE.booleanValue())
            result.put(Tools.MESSAGE, DELETE_PURCHASE_ORDER_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PURCHASE_ORDER_FAILURE_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_PURCHASE_ORDER_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PURCHASE_ORDER_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Association check for different plugins
     * @param purchaseOrder - purchase order id
     * @return - relative association message
     */
    private LinkedHashMap hasAssociation(ProcPurchaseOrder purchaseOrder) {
        LinkedHashMap result = new LinkedHashMap()
        long purchaseOrderId = purchaseOrder.id
        int count = 0
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
            Object invTransaction = inventoryImplService.readInvTransactionByPurchaseOrderId(purchaseOrderId)
            if (invTransaction) {
                result.put(Tools.MESSAGE, INVENTORY_IN_FOUND)
                return result
            }
        }
        if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
            count = countAccVoucher(purchaseOrderId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_VOUCHER)
                return result
            }
        }
        count = countTransportCost(purchaseOrderId);
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_TRANSPORT_COST)
            return result
        }
        count = ProcTermsAndCondition.countByPurchaseOrderId(purchaseOrderId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MSG_TERMS_AND_CON)
            return result
        }
        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    /**
     * Count voucher
     * @param purchaseOrderId - purchase order id
     * @return - int value (1 for success, 0 for failure)
     */
    private int countAccVoucher(long purchaseOrderId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM acc_voucher
            WHERE instrument_id = ${purchaseOrderId}
            AND instrument_type_id = ${accountingImplService.getInstrumentTypePurchaseOrder()}
        """

        List results = executeSelectSql(queryStr);
        int count = results[0].count;
        return count;
    }

    private static final String QUERY_COUNT = """
                                    SELECT COUNT(id) AS count
                                          FROM proc_transport_cost
                                    WHERE purchase_order_id = :purchaseOrderId
    """

    /**
     * Count transport cost
     * @param purchaseOrderId - purchase order id
     * @return - int value (1 for success, 0 for failure)
     */
    private int countTransportCost(long purchaseOrderId) {
        List results = executeSelectSql(QUERY_COUNT, [purchaseOrderId: purchaseOrderId]);
        int count = results[0].count;
        return count;
    }
}
