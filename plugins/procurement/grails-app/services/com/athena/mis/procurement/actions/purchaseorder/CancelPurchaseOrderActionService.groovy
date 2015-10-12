package com.athena.mis.procurement.actions.purchaseorder

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.procurement.entity.*
import com.athena.mis.procurement.service.*
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Cancel Purchase Order and remove from grid
 * For details go through Use-Case doc named 'CancelPurchaseOrderActionService'
 */
class CancelPurchaseOrderActionService extends BaseService implements ActionIntf {

    private static final String INVENTORY_IN_FOUND = "Purchase Order has association within Inventory Transaction(s)"
    private static final String CANCEL_PURCHASE_ORDER_SUCCESS_MSG = "Purchase Order has been cancelled successfully"
    private static final String CANCEL_PURCHASE_ORDER_FAILURE_MSG = "Purchase Order could not be cancelled"
    private static final String PURCHASE_ORDER_OBJ = "purchaseOrder"
    private static final String CANCELLED = "cancelled"
    private static final String HAS_ASSOCIATION_MSG_VOUCHER = " voucher is associated with this purchase order"

    private final Logger log = Logger.getLogger(getClass())

    PurchaseOrderService purchaseOrderService
    PurchaseOrderDetailsService purchaseOrderDetailsService
    PurchaseRequestDetailsService purchaseRequestDetailsService
    ProcCancelledPOService procCancelledPOService
    ProcCancelledPODetailsService procCancelledPODetailsService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService
    @Autowired
    ProcSessionUtil procSessionUtil

    /**
     * 1. get ProcPurchaseOrder object by id
     * 2. check if ProcPurchaseOrder object exists or not
     * 3. check association with relevant domains
     * @param parameters -serialize parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long purchaseOrderId = Long.parseLong(params.id.toString())
            // get ProcPurchaseOrder object by id
            ProcPurchaseOrder purchaseOrder = (ProcPurchaseOrder) purchaseOrderService.read(purchaseOrderId)
            // check if ProcPurchaseOrder object exists or not
            if (!purchaseOrder) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            // check association with relevant domains
            Map preResult = (Map) hasAssociation(purchaseOrder)
            Boolean hasAssociation = (Boolean) preResult.get(Tools.HAS_ASSOCIATION)
            // PO with association can not be cancelled
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
            result.put(Tools.MESSAGE, CANCEL_PURCHASE_ORDER_FAILURE_MSG)
            return result
        }
    }

    /**
     * Delete purchase order from DB and grid
     * 1. build ProcCancelledPO object
     * 2. save ProcCancelledPO object in DB
     * 3. delete purchase order details object(s)
     * 4. delete ProcPurchaseOrder object from DB
     * The method is in transactional block and will roll back in any case of exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)// default value
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            GrailsParameterMap params = (GrailsParameterMap) parameters
            ProcPurchaseOrder purchaseOrder = (ProcPurchaseOrder) preResult.get(PURCHASE_ORDER_OBJ)
            String cancelReason = params.reason
            ProcCancelledPO cancelledPO = buildProcCancelledPOObject(purchaseOrder, cancelReason)
            procCancelledPOService.create(cancelledPO)

            List<ProcPurchaseOrderDetails> lstPODetails = ProcPurchaseOrderDetails.findAllByPurchaseOrderId(purchaseOrder.id, [readOnly: true])
            if (lstPODetails.size() > 0) {
                for (int i = 0; i < lstPODetails.size(); i++) {
                    ProcPurchaseOrderDetails purchaseOrderDetails = lstPODetails[i]
                    deletePurchaseOrderDetails(purchaseOrderDetails)
                }
            }

            Boolean updateStatus = purchaseOrderService.delete(purchaseOrder.id, purchaseOrder.companyId)
            if (!updateStatus.booleanValue()) {
                result.put(Tools.MESSAGE, CANCEL_PURCHASE_ORDER_FAILURE_MSG)
                return result
            }

            result.put(PURCHASE_ORDER_OBJ, purchaseOrder)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CANCEL_PURCHASE_ORDER_FAILURE_MSG)
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
     * @return -a map containing message
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(CANCELLED, Boolean.TRUE.booleanValue())
            result.put(Tools.MESSAGE, CANCEL_PURCHASE_ORDER_SUCCESS_MSG)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CANCEL_PURCHASE_ORDER_FAILURE_MSG)
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
            result.put(Tools.MESSAGE, CANCEL_PURCHASE_ORDER_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CANCEL_PURCHASE_ORDER_FAILURE_MSG)
            return result
        }
    }

    /**
     * Association check for different plugins
     * @param purchaseOrder -object of ProcPurchaseOrder
     * @return -relative association message
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
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MSG_VOUCHER)
                return result
            }
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    private static final String ACC_VOUCHER_COUNT = """
        SELECT COUNT(id) AS count
        FROM acc_voucher
        WHERE instrument_id =:purchaseOrderId
        AND instrument_type_id =:instrumentTypeId
    """

    /**
     * Count voucher with instrument id equal to purchase order id
     * @param purchaseOrderId -purchase order id
     * @return -int value (1 for success, 0 for failure)
     */
    private int countAccVoucher(long purchaseOrderId) {
        Map queryParams = [
                purchaseOrderId: purchaseOrderId,
                instrumentTypeId: accountingImplService.getInstrumentTypePurchaseOrder()
        ]
        List results = executeSelectSql(ACC_VOUCHER_COUNT, queryParams)
        int count = results[0].count
        return count
    }

    /**
     * Build object of ProcCancelledPO
     * @param purchaseOrder -object of ProcPurchaseOrder
     * @param reason -reason for cancelling PO
     * @return -object of ProcCancelledPO
     */
    private ProcCancelledPO buildProcCancelledPOObject(ProcPurchaseOrder purchaseOrder, String reason) {
        ProcCancelledPO cancelledPO = new ProcCancelledPO()
        cancelledPO.id = purchaseOrder.id
        cancelledPO.version = purchaseOrder.version
        cancelledPO.projectId = purchaseOrder.projectId
        cancelledPO.purchaseRequestId = purchaseOrder.purchaseRequestId
        cancelledPO.paymentMethodId = purchaseOrder.paymentMethodId
        cancelledPO.modeOfPayment = purchaseOrder.modeOfPayment
        cancelledPO.createdOn = purchaseOrder.createdOn
        cancelledPO.createdBy = purchaseOrder.createdBy
        cancelledPO.updatedOn = purchaseOrder.updatedOn ? purchaseOrder.updatedOn : null
        cancelledPO.updatedBy = purchaseOrder.updatedBy
        cancelledPO.comments = purchaseOrder.comments ? purchaseOrder.comments : null
        cancelledPO.approvedByDirectorId = purchaseOrder.approvedByDirectorId
        cancelledPO.approvedByProjectDirectorId = purchaseOrder.approvedByProjectDirectorId
        cancelledPO.trCostCount = purchaseOrder.trCostCount
        cancelledPO.trCostTotal = purchaseOrder.trCostTotal
        cancelledPO.supplierId = purchaseOrder.supplierId
        cancelledPO.itemCount = purchaseOrder.itemCount
        cancelledPO.totalPrice = purchaseOrder.totalPrice
        cancelledPO.discount = purchaseOrder.discount
        cancelledPO.totalVatTax = purchaseOrder.totalVatTax
        cancelledPO.companyId = purchaseOrder.companyId
        cancelledPO.sentForApproval = purchaseOrder.sentForApproval
        cancelledPO.cancelledBy = procSessionUtil.appSessionUtil.getAppUser().id
        cancelledPO.cancelledOn = new Date()
        cancelledPO.cancelReason = reason
        return cancelledPO
    }

    /**
     * Build object of ProcCancelledPODetails
     * @param purchaseOrderDetails -object of ProcPurchaseOrderDetails
     * @return -object of ProcCancelledPODetails
     */
    private ProcCancelledPODetails buildProcCancelledPODetailsObject(ProcPurchaseOrderDetails purchaseOrderDetails) {

        ProcCancelledPODetails cancelledPODetails = new ProcCancelledPODetails()
        cancelledPODetails.id = purchaseOrderDetails.id
        cancelledPODetails.version = purchaseOrderDetails.version
        cancelledPODetails.purchaseOrderId = purchaseOrderDetails.purchaseOrderId
        cancelledPODetails.projectId = purchaseOrderDetails.projectId
        cancelledPODetails.purchaseRequestId = purchaseOrderDetails.purchaseRequestId
        cancelledPODetails.purchaseRequestDetailsId = purchaseOrderDetails.purchaseRequestDetailsId
        cancelledPODetails.quantity = purchaseOrderDetails.quantity
        cancelledPODetails.rate = purchaseOrderDetails.rate
        cancelledPODetails.itemId = purchaseOrderDetails.itemId
        cancelledPODetails.storeInQuantity = purchaseOrderDetails.storeInQuantity
        cancelledPODetails.createdOn = purchaseOrderDetails.createdOn
        cancelledPODetails.createdBy = purchaseOrderDetails.createdBy
        cancelledPODetails.updatedOn = purchaseOrderDetails.updatedOn ? purchaseOrderDetails.updatedOn : null
        cancelledPODetails.updatedBy = purchaseOrderDetails.updatedBy
        cancelledPODetails.comments = purchaseOrderDetails.comments ? purchaseOrderDetails.comments : null
        cancelledPODetails.companyId = purchaseOrderDetails.companyId
        cancelledPODetails.fixedAssetDetailsCount = purchaseOrderDetails.fixedAssetDetailsCount
        cancelledPODetails.vatTax = purchaseOrderDetails.vatTax
        return cancelledPODetails
    }

    /**
     * 1. Build object of ProcCancelledPODetails
     * 2. save ProcCancelledPODetails object in DB
     * 3. update po quantity of purchase request details object
     * 4. delete ProcPurchaseOrderDetails object form DB
     * @param purchaseOrderDetails -object of ProcPurchaseOrderDetails
     */
    private void deletePurchaseOrderDetails(ProcPurchaseOrderDetails purchaseOrderDetails) {
        ProcCancelledPODetails cancelledPODetails = buildProcCancelledPODetailsObject(purchaseOrderDetails)
        procCancelledPODetailsService.create(cancelledPODetails)

        ProcPurchaseRequestDetails purchaseRequestDetails = purchaseRequestDetailsService.read(purchaseOrderDetails.purchaseRequestDetailsId)
        purchaseRequestDetails.poQuantity = purchaseRequestDetails.poQuantity - purchaseOrderDetails.quantity

        updatePurchaseRequestDetails(purchaseRequestDetails)
        purchaseOrderDetailsService.delete(purchaseOrderDetails)
    }

    private static final String UPDATE_QUERY = """
        UPDATE proc_purchase_request_details
        SET
        po_quantity=:poQuantity,
        version=version+1
        WHERE
        id=:id AND
        version=:version
    """

    /**
     * Update po quantity of purchase request details object
     * @param purchaseRequestDetails -object of ProcPurchaseRequestDetails
     * @return -an integer containing the value of count
     */
    private int updatePurchaseRequestDetails(ProcPurchaseRequestDetails purchaseRequestDetails) {
        Map queryParams = [
                id: purchaseRequestDetails.id,
                version: purchaseRequestDetails.version,
                poQuantity: purchaseRequestDetails.poQuantity
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to cancel PO")
        }
        return updateCount
    }
}
