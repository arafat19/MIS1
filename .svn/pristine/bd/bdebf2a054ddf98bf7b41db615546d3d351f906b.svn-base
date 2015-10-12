package com.athena.mis.procurement.actions.purchaseorder

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.service.PurchaseOrderService
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update purchase order object and grid data
 *  For details go through Use-Case doc named 'UpdatePurchaseOrderActionService'
 */
class UpdatePurchaseOrderActionService extends BaseService implements ActionIntf {

    private static final String SUPPLIER_CANT_CHANGE = "Supplier can't be updated"
    private static final String TOTAL_PRICE_EXCEEDS_VOUCHER = "Total price of purchase order exceeded total amount of its voucher(s)"
    private static final String PURCHASE_ORDER_UPDATE_FAILURE_MESSAGE = "Purchase order could not be updated"
    private static final String PURCHASE_ORDER_UPDATE_SUCCESS_MESSAGE = "Purchase order has been updated successfully"
    private static final String PURCHASE_REQUEST_NOT_FOUND = "Purchase request not found. Please refresh and try again"
    private static final String PURCHASE_ORDER_NOT_FOUND = "Purchase order not found. Please refresh and try again"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Please input all fields correctly"
    private static final String SERVER_ERROR_MESSAGE = "Fail to update purchase order"
    private static final String PURCHASE_ORDER_OBJ = "purchaseOrder"
    private static final String DISCOUNT_INVALID = "Discount can not be greater than PO total price"
    private static final String NOT_EDITABLE_MSG = "Selected PO has been sent for approval, make PO editable to update"

    PurchaseOrderService purchaseOrderService
    PurchaseRequestService purchaseRequestService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    SupplierCacheUtility supplierCacheUtility

    private final Logger log = Logger.getLogger(getClass())
    /**
     * Get parameters from UI and build purchase order object for update
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
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // check here for required params are present
            if ((!parameterMap.id) || (!parameterMap.version)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            // check if ProcPurchaseRequestDetails object exist
            long purchaseRequestId = Long.parseLong(parameterMap.purchaseRequestId.toString())
            ProcPurchaseRequest purchaseRequest = purchaseRequestService.read(purchaseRequestId)
            if (!purchaseRequest) {
                result.put(Tools.MESSAGE, PURCHASE_REQUEST_NOT_FOUND)
                return result
            }
            //check if edited quantity is allowed
            long purchaseOrderId = Long.parseLong(parameterMap.id.toString())
            int version = Integer.parseInt(parameterMap.version.toString())
            ProcPurchaseOrder oldPurchaseOrder = purchaseOrderService.read(purchaseOrderId)
            if (!oldPurchaseOrder || oldPurchaseOrder.version != version) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND)
                return result
            }
            if (oldPurchaseOrder.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_EDITABLE_MSG)
                return result
            }
            double poDiscount
            if (parameterMap.discount.toString().length() > 0) {
                try {
                    poDiscount = Double.parseDouble(parameterMap.discount.toString())
                } catch (Exception e) {
                    poDiscount = 0.0d
                }
            } else {
                poDiscount = 0.0d
            }
            ProcPurchaseOrder newPurchaseOrder = buildPurchaseOrder(parameterMap, oldPurchaseOrder, poDiscount)
            double totalItemPrice = getTotalItemPrice(purchaseOrderId)
            if (newPurchaseOrder.discount > totalItemPrice) {
                result.put(Tools.MESSAGE, DISCOUNT_INVALID)
                return result
            }
            if (oldPurchaseOrder.supplierId != newPurchaseOrder.supplierId) {
                Object storeTransaction = inventoryImplService.readInvTransactionByPurchaseOrderId(purchaseOrderId)
                if (storeTransaction) {
                    result.put(Tools.MESSAGE, SUPPLIER_CANT_CHANGE)
                    return result
                }
            }
            double totalAmountOfVoucher = accountingImplService.getTotalAmountByPurchaseOrderId(newPurchaseOrder.id)
            if (newPurchaseOrder.totalPrice < totalAmountOfVoucher) {
                result.put(Tools.MESSAGE, TOTAL_PRICE_EXCEEDS_VOUCHER)
                return result
            }
            result.put(PURCHASE_ORDER_OBJ, newPurchaseOrder)
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
     * Update Purchase Order object in DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -object receive from pre execute method
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj

            ProcPurchaseOrder purchaseOrder = (ProcPurchaseOrder) preResult.get(PURCHASE_ORDER_OBJ)
            purchaseOrderService.update(purchaseOrder)
            result.put(PURCHASE_ORDER_OBJ, purchaseOrder)
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
                result.put(Tools.MESSAGE, PURCHASE_ORDER_UPDATE_FAILURE_MESSAGE)
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
     * -map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            ProcPurchaseOrder purchaseOrder = (ProcPurchaseOrder) receiveResult.get(PURCHASE_ORDER_OBJ)
            Object budget
            String approvedByDirector
            String approvedByProjectDirector
            String createdOn

            GridEntity object = new GridEntity()
            object.id = purchaseOrder.id
            approvedByDirector = purchaseOrder.approvedByDirectorId ? Tools.YES : Tools.NO
            approvedByProjectDirector = purchaseOrder.approvedByProjectDirectorId ? Tools.YES : Tools.NO
            createdOn = DateUtility.getDateForUI(purchaseOrder.createdOn)
            Supplier supplier = (Supplier) supplierCacheUtility.read(purchaseOrder.supplierId)

            object.cell = [
                    Tools.LABEL_NEW,
                    purchaseOrder.id,
                    createdOn,
                    purchaseOrder.purchaseRequestId,
                    supplier.name,
                    purchaseOrder.itemCount,
                    Tools.makeAmountWithThousandSeparator(purchaseOrder.discount),
                    Tools.makeAmountWithThousandSeparator(purchaseOrder.totalPrice),
                    Tools.makeAmountWithThousandSeparator(purchaseOrder.trCostTotal),
                    Tools.makeAmountWithThousandSeparator(purchaseOrder.totalVatTax),
                    purchaseOrder.sentForApproval ? Tools.YES : Tools.NO,
                    approvedByDirector,
                    approvedByProjectDirector
            ]

            result.put(Tools.MESSAGE, PURCHASE_ORDER_UPDATE_SUCCESS_MESSAGE)
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
     * Get purchase order object
     * @param parameterMap - serialized parameters from UI
     * @param oldPurchaseOrder -previous object of selected purchase order
     * @param discount - discount
     * @return - newly created purchase order object
     */
    private ProcPurchaseOrder buildPurchaseOrder(GrailsParameterMap parameterMap, ProcPurchaseOrder oldPurchaseOrder, double discount) {

        ProcPurchaseOrder newPurchaseOrder = new ProcPurchaseOrder(parameterMap)
        AppUser systemUser = procSessionUtil.appSessionUtil.getAppUser()

        newPurchaseOrder.discount = discount
        //ensure internal inputs
        newPurchaseOrder.updatedOn = new Date()
        newPurchaseOrder.updatedBy = systemUser.id

        // set old newPurchaseOrder property to new newPurchaseOrder for validation
        newPurchaseOrder.projectId = oldPurchaseOrder.projectId
        newPurchaseOrder.purchaseRequestId = oldPurchaseOrder.purchaseRequestId
        newPurchaseOrder.createdOn = oldPurchaseOrder.createdOn
        newPurchaseOrder.createdBy = oldPurchaseOrder.createdBy
        newPurchaseOrder.approvedByDirectorId = 0L
        newPurchaseOrder.approvedByProjectDirectorId = 0L
        newPurchaseOrder.sentForApproval = false
        newPurchaseOrder.trCostCount = oldPurchaseOrder.trCostCount
        newPurchaseOrder.trCostTotal = oldPurchaseOrder.trCostTotal
        newPurchaseOrder.totalVatTax = oldPurchaseOrder.totalVatTax
        newPurchaseOrder.id = oldPurchaseOrder.id
        newPurchaseOrder.version = oldPurchaseOrder.version
        newPurchaseOrder.itemCount = oldPurchaseOrder.itemCount
        newPurchaseOrder.companyId = oldPurchaseOrder.companyId
        newPurchaseOrder.comments = parameterMap.comments ? parameterMap.comments : null
        newPurchaseOrder.totalPrice = oldPurchaseOrder.totalPrice + oldPurchaseOrder.discount - newPurchaseOrder.discount

        return newPurchaseOrder
    }

    private static final String QUERY_SELECT = """
            SELECT coalesce (SUM((rate*quantity)-vat_tax),0) AS total_item_price FROM proc_purchase_order_details
            WHERE purchase_order_id = :purchaseOrderId
            """
    /**
     * Get total item price of a purchase order
     * @param purchaseOrderId - purchase order id
     * @return - total item price
     */
    private double getTotalItemPrice(long purchaseOrderId) {
        List<GroovyRowResult> queryResult = executeSelectSql(QUERY_SELECT, [purchaseOrderId: purchaseOrderId]);
        return queryResult[0].total_item_price
    }
}
