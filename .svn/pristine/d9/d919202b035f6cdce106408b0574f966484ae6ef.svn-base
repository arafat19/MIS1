package com.athena.mis.procurement.actions.purchaseorderdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.entity.ProcPurchaseOrderDetails
import com.athena.mis.procurement.entity.ProcPurchaseRequestDetails
import com.athena.mis.procurement.service.PurchaseOrderDetailsService
import com.athena.mis.procurement.service.PurchaseOrderService
import com.athena.mis.procurement.service.PurchaseRequestDetailsService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Delete indent from DB as well as from grid.
 * For details go through Use-Case doc named 'DeletePurchaseOrderDetailsActionService'
 */
class DeletePurchaseOrderDetailsActionService extends BaseService implements ActionIntf {

    private static final String PO_ASSOCIATED_INVENTORY_IN = "Purchase order Details associated within Inventory-In transaction."
    private static final String PO_ASSOCIATED_FIXED_ASSET = "Purchase order Details associated within Fixed Asset Details."
    private static final String VOUCHER_EXCEEDS_PO_TOTAL = "Voucher total exceeds total Price of PO"
    private static final String TOTAL_PRICE_LOWER_THAN_DISCOUNT = "Total Price is lower than the discount"
    private static final String DELETE_PURCHASE_ORDER_DETAILS_SUCCESS_MESSAGE = "Purchase order details has been deleted successfully"
    private static final String DELETE_PURCHASE_ORDER_DETAILS_FAILURE_MESSAGE = "Purchase order details could not be deleted, refresh the list and try again"
    private static final String PURCHASE_ORDER_DETAILS_OBJ = "purchaseOrderDetails"
    private static final String DELETED = "deleted"
    private static final String PURCHASE_REQUEST_DETAILS_OBJ = "purchaseRequestDetails"
    private static final String NOT_EDITABLE_MSG = "The PO has been sent for approval, make PO editable to delete PO details"

    PurchaseOrderDetailsService purchaseOrderDetailsService
    PurchaseOrderService purchaseOrderService
    PurchaseRequestDetailsService purchaseRequestDetailsService
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService
    @Autowired
    ProcSessionUtil procSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check pre-conditions
     * 1. pull purchase order details object
     * 2. check purchase order details existence
     * 3. pull purchase order object
     * 4. if purchase order is sent for approval then PO details can not be deleted
     * 5. pull purchase request details object
     * 6. change poQuantity of purchase request details
     * 7. check association with inventory in , fixed asset
     * 8. get total amount of voucher
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing pr details, po, po details object, isError(True/False) and relative msg
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long purchaseOrderDetailsId = Long.parseLong(params.id.toString())
            ProcPurchaseOrderDetails purchaseOrderDetails = (ProcPurchaseOrderDetails) purchaseOrderDetailsService.read(purchaseOrderDetailsId)
            if (!purchaseOrderDetails) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            ProcPurchaseOrder purchaseOrder = purchaseOrderService.read(purchaseOrderDetails.purchaseOrderId)
            if (purchaseOrder.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_EDITABLE_MSG)
                return result
            }
            ProcPurchaseRequestDetails purchaseRequestDetails = purchaseRequestDetailsService.read(purchaseOrderDetails.purchaseRequestDetailsId)
            //change poQuantity of purchase request details
            purchaseRequestDetails.poQuantity = purchaseRequestDetails.poQuantity - purchaseOrderDetails.quantity
            if (purchaseOrderDetails.storeInQuantity > 0) {
                result.put(Tools.MESSAGE, PO_ASSOCIATED_INVENTORY_IN)
                return result
            }
            if (purchaseOrderDetails.fixedAssetDetailsCount > 0) {
                result.put(Tools.MESSAGE, PO_ASSOCIATED_FIXED_ASSET)
                return result
            }
            if (purchaseOrder.totalPrice < 0) {
                result.put(Tools.MESSAGE, TOTAL_PRICE_LOWER_THAN_DISCOUNT)
                return result
            }
            double totalAmountOfVoucher = accountingImplService.getTotalAmountByPurchaseOrderId(purchaseOrderDetails.purchaseOrderId)
            if (purchaseOrder.totalPrice < totalAmountOfVoucher) {
                result.put(Tools.MESSAGE, VOUCHER_EXCEEDS_PO_TOTAL)
                return result
            }
            result.put(PURCHASE_REQUEST_DETAILS_OBJ, purchaseRequestDetails)
            result.put(PURCHASE_ORDER_DETAILS_OBJ, purchaseOrderDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PURCHASE_ORDER_DETAILS_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * 1. receive po, po details, pr details object from pre execute method
     * 2. use flag isPOApproved to send mail to Director & Project Director
     * 3. if po is already approved set approvedByDirectorId & approvedByProjectDirectorId 0L
     * 4. delete po details
     * 5. update po details for quantity
     * 6. update  po for total price
     * @param params - N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing purchase order details object & isPoApproved flag
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcPurchaseOrderDetails purchaseOrderDetails = (ProcPurchaseOrderDetails) preResult.get(PURCHASE_ORDER_DETAILS_OBJ)
            ProcPurchaseRequestDetails purchaseRequestDetails = (ProcPurchaseRequestDetails) preResult.get(PURCHASE_REQUEST_DETAILS_OBJ)

            purchaseOrderDetailsService.delete(purchaseOrderDetails)
            updatePRForPurchaseOrderDetails(purchaseRequestDetails)
            updatePOForPODetailsDelete(purchaseOrderDetails)

            result.put(PURCHASE_ORDER_DETAILS_OBJ, purchaseOrderDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Failed to delete PO Details")
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PURCHASE_ORDER_DETAILS_FAILURE_MESSAGE)
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
     * Set delete operation True
     * @param obj- N/A
     * @return - a map containing deleted=True and success msg
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(DELETED, Boolean.TRUE.booleanValue())
            result.put(Tools.MESSAGE, DELETE_PURCHASE_ORDER_DETAILS_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PURCHASE_ORDER_DETAILS_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, DELETE_PURCHASE_ORDER_DETAILS_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PURCHASE_ORDER_DETAILS_FAILURE_MESSAGE)
            return result
        }
    }

    // Update poQuantity on edit ProcPurchaseOrder
    private static final String UPDATE_QUERY = """
                      UPDATE proc_purchase_request_details
                      SET
                          po_quantity=:poQuantity,
                          version = version + 1
                      WHERE
                          id=:id AND
                          version=:version
    """

    private int updatePRForPurchaseOrderDetails(ProcPurchaseRequestDetails purchaseRequestDetails) {
        Map queryParams = [
                id: purchaseRequestDetails.id,
                version: purchaseRequestDetails.version,
                poQuantity: purchaseRequestDetails.poQuantity
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update PO")
        }
        return updateCount
    }

    private static final String PROC_PURCHASE_ORDER_UPDATE_QUERY = """
            UPDATE proc_purchase_order
            SET
            total_price = total_price - (:quantity * :rate) + :vatTax,
            total_vat_tax = total_vat_tax - :vatTax,
            item_count = item_count - 1,
            version = version + 1,
            updated_on =:updatedOn,
            updated_by =:updatedBy
            WHERE id =:purchaseOrderId
    """

    // Update Purchase Order for PO Details delete event (also unApprove PO)
    private int updatePOForPODetailsDelete(ProcPurchaseOrderDetails purchaseOrderDetails) {
        Map queryParams = [
                quantity: purchaseOrderDetails.quantity,
                rate: purchaseOrderDetails.rate,
                vatTax: purchaseOrderDetails.vatTax,
                updatedOn: DateUtility.getSqlDateWithSeconds(new Date()),
                updatedBy: procSessionUtil.appSessionUtil.getAppUser().id,
                purchaseOrderId: purchaseOrderDetails.purchaseOrderId
        ]
        int updateCount = executeUpdateSql(PROC_PURCHASE_ORDER_UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update total price count')
        }
        return updateCount
    }
}