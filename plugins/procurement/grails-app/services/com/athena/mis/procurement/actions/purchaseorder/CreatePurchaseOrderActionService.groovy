package com.athena.mis.procurement.actions.purchaseorder

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.service.PurchaseOrderService
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Create Purchase Order and show in the grid
 * For details go through Use-Case doc named 'CreatePurchaseOrderActionService'
 */
class CreatePurchaseOrderActionService extends BaseService implements ActionIntf {

    private static final String PURCHASE_ORDER_SAVE_SUCCESS_MESSAGE = "Purchase order has been saved successfully"
    private static final String PURCHASE_ORDER_SAVE_FAILURE_MESSAGE = "Purchase order could not be saved"
    private static final String PURCHASE_REQUEST_NOT_APPROVED = "Purchase request is not approved"
    private static final String PURCHASE_ORDER_OBJ = "purchaseOrderInstance"
    private static final String SERVER_ERROR_MESSAGE = "Fail to save purchase order"
    private static final String DISCOUNT_INVALID = "Discount can not be greater than PO total price"

    private final Logger log = Logger.getLogger(getClass())

    PurchaseOrderService purchaseOrderService
    PurchaseRequestService purchaseRequestService
    @Autowired
    SupplierCacheUtility supplierCacheUtility

    /**
     * Check all pre conditions and get po object
     * @param params -N/A
     * @param obj - po object from controller
     * @return - purchase order object and isError(True/False)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            ProcPurchaseOrder purchaseOrderInstance = (ProcPurchaseOrder) obj

            //check the purchase request approval
            ProcPurchaseRequest purchaseRequest = purchaseRequestService.read(purchaseOrderInstance.purchaseRequestId)
            if (!purchaseRequest || purchaseRequest.approvedByDirectorId <= 0 || purchaseRequest.approvedByProjectDirectorId <= 0) {
                result.put(Tools.MESSAGE, PURCHASE_REQUEST_NOT_APPROVED)
                return result
            }

            if (purchaseOrderInstance.discount != 0) {
                result.put(Tools.MESSAGE, DISCOUNT_INVALID)
                return result
            }

            result.put(PURCHASE_ORDER_OBJ, purchaseOrderInstance)
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
     * Create purchase order
     * @param parameters -N/A
     * @param obj - object from pre execute method
     * @return - a map containing newly created po object
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcPurchaseOrder purchaseOrderInstance = (ProcPurchaseOrder) preResult.get(PURCHASE_ORDER_OBJ)
            ProcPurchaseOrder returnPurchaseOrder = purchaseOrderService.create(purchaseOrderInstance)
            if (!returnPurchaseOrder) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_SAVE_FAILURE_MESSAGE)
                return result
            }
            result.put(PURCHASE_ORDER_OBJ, returnPurchaseOrder)
            result.put(Tools.MESSAGE, PURCHASE_ORDER_SAVE_SUCCESS_MESSAGE)
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
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_SAVE_FAILURE_MESSAGE)
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
     * 1. Show newly created po in grid
     * 2. Wrap grid object
     * 3. Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            ProcPurchaseOrder purchaseOrderInstance = (ProcPurchaseOrder) receiveResult.get(PURCHASE_ORDER_OBJ)

            String approvedByDirector = purchaseOrderInstance.approvedByDirectorId ? Tools.YES : Tools.NO
            String approvedByProjectDirector = purchaseOrderInstance.approvedByProjectDirectorId ? Tools.YES : Tools.NO
            String createdOn = DateUtility.getDateForUI(purchaseOrderInstance.createdOn)
            Object supplier = supplierCacheUtility.read(purchaseOrderInstance.supplierId)

            GridEntity object = new GridEntity()
            object.id = purchaseOrderInstance.id
            object.cell = [
                    Tools.LABEL_NEW,
                    purchaseOrderInstance.id,
                    createdOn,
                    purchaseOrderInstance.purchaseRequestId,
                    supplier ? supplier.name : Tools.EMPTY_SPACE,
                    purchaseOrderInstance.itemCount,
                    Tools.makeAmountWithThousandSeparator(purchaseOrderInstance.discount),
                    Tools.makeAmountWithThousandSeparator(purchaseOrderInstance.totalPrice),
                    Tools.makeAmountWithThousandSeparator(purchaseOrderInstance.trCostTotal),
                    Tools.makeAmountWithThousandSeparator(purchaseOrderInstance.totalVatTax),
                    purchaseOrderInstance.sentForApproval ? Tools.YES : Tools.NO,
                    approvedByDirector,
                    approvedByProjectDirector
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
}
