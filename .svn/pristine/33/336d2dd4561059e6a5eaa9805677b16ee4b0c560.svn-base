package com.athena.mis.procurement.actions.purchaseorderdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
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
 * Create Purchase Order details and show in the grid
 * For details go through Use-Case doc named 'CreatePurchaseOrderDetailsActionService'
 */
class CreatePurchaseOrderDetailsActionService extends BaseService implements ActionIntf {

    private static final String PURCHASE_ORDER_DETAILS_SAVE_SUCCESS_MESSAGE = "Purchase Order Details has been saved successfully"
    private static final String PURCHASE_ORDER_DETAILS_SAVE_FAILURE_MESSAGE = "Purchase Order Details could not be saved"
    private static final String QUANTITY_EXCEEDED_MESSAGE = "Purchase Order Details Quantity exceeds Purchase Request Quantity."
    private static final String PURCHASE_ORDER_DETAILS_OBJ = "purchaseOrderDetailsInstance"
    private static final String PURCHASE_REQUEST_DETAILS_OBJ = "purchaseRequestDetails"
    private static final String SERVER_ERROR_MESSAGE = "Fail to save Purchase Order Details"
    private static final String PURCHASE_ORDER_NOT_FOUND = "Purchase Order has not found"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Fail to create Purchase Order due to Invalid Input"
    private static final String ITEM_ALREADY_EXISTS = "Purchase Request details with same item already exists"
    private static final String NOT_EDITABLE_MSG = "The PO has been sent for approval, make PO editable to create PO details"

    private final Logger log = Logger.getLogger(getClass())

    PurchaseOrderService purchaseOrderService
    PurchaseOrderDetailsService purchaseOrderDetailsService
    PurchaseRequestDetailsService purchaseRequestDetailsService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility

    /**
     * 1. pull purchase order
     * 2. if purchase order is sent for approval then PO details can not be created
     * 3. receive purchase order details instance from controller
     * 4. check item existence
     * 5. pull purchase request details object
     * 6. available quantity check
     * @param params - serialize parameters from UI
     * @param obj - N/A
     * @return - a map containing purchase order, purchase order details, purchase request details object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if ((!parameterMap.purchaseOrderId)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            long purchaseOrderId = Long.parseLong(parameterMap.purchaseOrderId.toString())
            ProcPurchaseOrder purchaseOrder = purchaseOrderService.read(purchaseOrderId)
            if (!purchaseOrder) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND)
                return result
            }
            if (purchaseOrder.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_EDITABLE_MSG)
                return result
            }
            ProcPurchaseOrderDetails purchaseOrderDetailsInstance = (ProcPurchaseOrderDetails) obj
            int duplicateCount = ProcPurchaseOrderDetails.countByPurchaseOrderIdAndItemId(purchaseOrder.id, purchaseOrderDetailsInstance.itemId)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, ITEM_ALREADY_EXISTS)
                return result
            }
            ProcPurchaseRequestDetails purchaseRequestDetails = purchaseRequestDetailsService.read(purchaseOrderDetailsInstance.purchaseRequestDetailsId)
            double availableQuantity = purchaseRequestDetails.quantity - purchaseRequestDetails.poQuantity
            if (purchaseOrderDetailsInstance.quantity > availableQuantity) {
                result.put(Tools.MESSAGE, QUANTITY_EXCEEDED_MESSAGE)
                return result
            }
            // checks input validation
            purchaseOrderDetailsInstance.validate()
            if (purchaseOrderDetailsInstance.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            result.put(PURCHASE_REQUEST_DETAILS_OBJ, purchaseRequestDetails)
            result.put(PURCHASE_ORDER_DETAILS_OBJ, purchaseOrderDetailsInstance)
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
     * 1. receive po, po details, pr details object from pre execute method
     * 2. use flag isPOApproved to send mail to Director & Project Director
     * 3. if po is already approved set approvedByDirectorId & approvedByProjectDirectorId 0L
     * 4. create po details
     * 5. update pr details for quantity
     * 6. update  po for total price
     * @param parameters - N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing purchase order details, approval status, success/failure message
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcPurchaseOrderDetails purchaseOrderDetailsInstance = (ProcPurchaseOrderDetails) preResult.get(PURCHASE_ORDER_DETAILS_OBJ)
            ProcPurchaseRequestDetails purchaseRequestDetailsInstance = (ProcPurchaseRequestDetails) preResult.get(PURCHASE_REQUEST_DETAILS_OBJ)

            ProcPurchaseOrderDetails newPurchaseOrderDetails = purchaseOrderDetailsService.create(purchaseOrderDetailsInstance)
            purchaseRequestDetailsInstance.poQuantity += newPurchaseOrderDetails.quantity
            updatePRForPurchaseOrderDetails(purchaseRequestDetailsInstance)
            updatePOForPODetailsCreate(purchaseOrderDetailsInstance)

            result.put(PURCHASE_ORDER_DETAILS_OBJ, newPurchaseOrderDetails)
            result.put(Tools.MESSAGE, PURCHASE_ORDER_DETAILS_SAVE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Failed to create PO Details")
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
     * 1. Show newly created po details in grid
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            ProcPurchaseOrderDetails purchaseOrderDetails = (ProcPurchaseOrderDetails) receiveResult.get(PURCHASE_ORDER_DETAILS_OBJ)

            Item item = (Item) itemCacheUtility.read(purchaseOrderDetails.itemId)
            String quantityWithUnit = Tools.formatAmountWithoutCurrency(purchaseOrderDetails.quantity) + Tools.SINGLE_SPACE + item.unit
            String createdOn = DateUtility.getLongDateForUI(purchaseOrderDetails.createdOn)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(purchaseOrderDetails.createdBy)
            ItemType itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            double total = purchaseOrderDetails.quantity * purchaseOrderDetails.rate

            GridEntity object = new GridEntity()
            object.id = purchaseOrderDetails.id
            object.cell = [
                    Tools.LABEL_NEW,
                    purchaseOrderDetails.id,
                    itemType.name,
                    item.name,
                    quantityWithUnit,
                    Tools.makeAmountWithThousandSeparator(purchaseOrderDetails.rate),
                    Tools.makeAmountWithThousandSeparator(total),
                    createdBy.username,
                    createdOn
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
                result.put(Tools.MESSAGE, PURCHASE_ORDER_DETAILS_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    private static final String UPDATE_QUERY = """
                      UPDATE proc_purchase_request_details
                      SET
                          po_quantity=:poQuantity,
                          version = version + 1
                      WHERE
                          id=:id AND
                          version=:version
    """

    // Update poQuantity on edit ProcPurchaseOrder
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
                total_price = total_price + (:quantity * :rate) - :vatTax,
                total_vat_tax = total_vat_tax + :vatTax,
                item_count = item_count + 1,
                version = version+1,
                updated_on =:updatedOn,
                updated_by =:updatedBy
            WHERE id =:purchaseOrderId
    """

    // Update Purchase Order for PO Details create event (also unApprove PO)
    private int updatePOForPODetailsCreate(ProcPurchaseOrderDetails purchaseOrderDetails) {
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