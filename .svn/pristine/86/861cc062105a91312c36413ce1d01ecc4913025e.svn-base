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
 *  Update purchase order details object and grid data
 *  For details go through Use-Case doc named 'UpdatePurchaseOrderDetailsActionService'
 */
class UpdatePurchaseOrderDetailsActionService extends BaseService implements ActionIntf {

    private static final String QUANTITY_LOWER_THAN_INVENTORY_IN = "PO Item Quantity can't be lower than Inventory-In Quantity"
    private static final String QUANTITY_LOWER_THAN_FIXED_ASSET = "PO Item Quantity can't be lower than Fixed Asset Details Quantity"
    private static final String VOUCHER_EXCEEDS_PO_TOTAL = "Voucher total exceeds total Price of PO"
    private static final String TOTAL_PRICE_LOWER_THAN_DISCOUNT = "PO total Price can not be lower than discount"
    private static final String PURCHASE_ORDER_DETAILS_UPDATE_FAILURE_MESSAGE = "Purchase Order Details could not be updated"
    private static final String PURCHASE_ORDER_DETAILS_UPDATE_SUCCESS_MESSAGE = "Purchase Order Details has been updated successfully"
    private static final String PURCHASE_ORDER_DETAILS_NOT_FOUND = "Purchase Order Details not found. Please refresh and try again"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Please input all fields correctly"
    private static final String SERVER_ERROR_MESSAGE = "Fail to update Purchase Order Details"
    private static final String PURCHASE_REQUEST_DETAILS_OBJ = "purchaseRequestDetails"
    private static final String PURCHASE_ORDER_DETAILS_OBJ = "purchaseOrderDetails"
    private static final String PURCHASE_ORDER_OBJ = "purchaseOrder"
    private static final String QUANTITY_EXCEEDED_MESSAGE = "Purchase Order quantity exceeds Purchase Request Quantity"
    private static final String RATE_CHANGED_ERROR = "Rate can't be changed"
    private static final String NOT_EDITABLE_MSG = "The PO has been sent for approval, make PO editable to update PO details"

    PurchaseOrderService purchaseOrderService
    PurchaseOrderDetailsService purchaseOrderDetailsService
    PurchaseRequestDetailsService purchaseRequestDetailsService
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Get parameters from UI and build purchase order details object for update
     * 1. pull purchase order details
     * 2. pull purchase order object
     * 3. if purchase order is sent for approval then PO details can not be updated
     * 4. build new purchase order details  with new parameters
     * 5. check vat tax
     * 6. checks input validation
     * 7. change poQuantity of purchase request details
     * 8. Set Total VatTax & Total price
     * 9. get totalAmountOfVoucher
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
            if ((!parameterMap.id) || (!parameterMap.version) || (!parameterMap.quantity)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            //check if edited quantity is allowed
            long id = Long.parseLong(parameterMap.id.toString())
            int version = Integer.parseInt(parameterMap.version.toString())
            ProcPurchaseOrderDetails oldPurchaseOrderDetails = purchaseOrderDetailsService.read(id)
            if (!oldPurchaseOrderDetails || oldPurchaseOrderDetails.version != version) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_DETAILS_NOT_FOUND)
                return result
            }
            ProcPurchaseOrder purchaseOrder = purchaseOrderService.read(oldPurchaseOrderDetails.purchaseOrderId)
            if (purchaseOrder.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_EDITABLE_MSG)
                return result
            }
            ProcPurchaseOrderDetails newPurchaseOrderDetails = buildPurchaseOrderDetails(parameterMap, oldPurchaseOrderDetails)
            if (newPurchaseOrderDetails.vatTax == '') {
                newPurchaseOrderDetails.vatTax = 0.0d
            }
            // checks input validation
           /* newPurchaseOrderDetails.validate()
            if (newPurchaseOrderDetails.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }*/
            ProcPurchaseRequestDetails prDetails = null
            if (oldPurchaseOrderDetails.quantity != newPurchaseOrderDetails.quantity) {
                prDetails = purchaseRequestDetailsService.read(newPurchaseOrderDetails.purchaseRequestDetailsId)
                //change poQuantity of purchase request details
                prDetails.poQuantity = prDetails.poQuantity - oldPurchaseOrderDetails.quantity
                prDetails.poQuantity = prDetails.poQuantity + newPurchaseOrderDetails.quantity
                double availableQuantity = prDetails.quantity - prDetails.poQuantity
                if (availableQuantity < 0) {
                    result.put(Tools.MESSAGE, QUANTITY_EXCEEDED_MESSAGE)
                    return result
                }
                if ((newPurchaseOrderDetails.quantity < oldPurchaseOrderDetails.quantity) &&
                        (newPurchaseOrderDetails.quantity < oldPurchaseOrderDetails.storeInQuantity)) {
                    result.put(Tools.MESSAGE, QUANTITY_LOWER_THAN_INVENTORY_IN)
                    return result
                }
                if ((newPurchaseOrderDetails.quantity < oldPurchaseOrderDetails.quantity) &&
                        (newPurchaseOrderDetails.quantity < oldPurchaseOrderDetails.fixedAssetDetailsCount)) {
                    result.put(Tools.MESSAGE, QUANTITY_LOWER_THAN_FIXED_ASSET)
                    return result
                }
            }
            if (oldPurchaseOrderDetails.rate != newPurchaseOrderDetails.rate) {
                if (oldPurchaseOrderDetails.fixedAssetDetailsCount > 0 || oldPurchaseOrderDetails.storeInQuantity > 0) {
                    result.put(Tools.MESSAGE, RATE_CHANGED_ERROR)
                    return result
                }
            }
            //Set Total VatTax
            purchaseOrder.totalVatTax = purchaseOrder.totalVatTax - oldPurchaseOrderDetails.vatTax + newPurchaseOrderDetails.vatTax
            //Set Total price
            purchaseOrder.totalPrice = purchaseOrder.totalPrice - (oldPurchaseOrderDetails.rate * oldPurchaseOrderDetails.quantity)
            purchaseOrder.totalPrice = purchaseOrder.totalPrice + (newPurchaseOrderDetails.rate * newPurchaseOrderDetails.quantity)
            purchaseOrder.totalPrice = purchaseOrder.totalPrice + oldPurchaseOrderDetails.vatTax - newPurchaseOrderDetails.vatTax
            if (purchaseOrder.totalPrice < 0) {
                result.put(Tools.MESSAGE, TOTAL_PRICE_LOWER_THAN_DISCOUNT)
                return result
            }
            double totalAmountOfVoucher = accountingImplService.getTotalAmountByPurchaseOrderId(newPurchaseOrderDetails.purchaseOrderId)
            if (purchaseOrder.totalPrice < totalAmountOfVoucher) {
                result.put(Tools.MESSAGE, VOUCHER_EXCEEDS_PO_TOTAL)
                return result
            }
            result.put(PURCHASE_ORDER_OBJ, purchaseOrder)
            result.put(PURCHASE_REQUEST_DETAILS_OBJ, prDetails)
            result.put(PURCHASE_ORDER_DETAILS_OBJ, newPurchaseOrderDetails)
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
     * 4. update po details
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
            ProcPurchaseRequestDetails purchaseRequestDetails = (ProcPurchaseRequestDetails) preResult.get(PURCHASE_REQUEST_DETAILS_OBJ)
            ProcPurchaseOrderDetails purchaseOrderDetails = (ProcPurchaseOrderDetails) preResult.get(PURCHASE_ORDER_DETAILS_OBJ)
            ProcPurchaseOrder purchaseOrder = (ProcPurchaseOrder) preResult.get(PURCHASE_ORDER_OBJ)

            purchaseOrderDetailsService.update(purchaseOrderDetails)
            if (purchaseRequestDetails) {
                updatePRForPurchaseOrderDetails(purchaseRequestDetails)
            }
            // Now update po total price and upApprove
            updatePOForPODetailsUpdate(purchaseOrder)
            result.put(PURCHASE_ORDER_DETAILS_OBJ, purchaseOrderDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Failed to update PO Details")
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

            result.put(Tools.MESSAGE, PURCHASE_ORDER_DETAILS_UPDATE_SUCCESS_MESSAGE)
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
                result.put(Tools.MESSAGE, PURCHASE_ORDER_DETAILS_UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }

    /**
     * Get purchase order details object
     * @param parameterMap
     * @param oldPurchaseOrderDetails
     * @return - new purchase order details object
     */
    private ProcPurchaseOrderDetails buildPurchaseOrderDetails(GrailsParameterMap parameterMap, ProcPurchaseOrderDetails oldPurchaseOrderDetails) {

        ProcPurchaseOrderDetails purchaseOrderDetails = new ProcPurchaseOrderDetails(parameterMap)
        AppUser systemUser = procSessionUtil.appSessionUtil.getAppUser()

        //ensure internal inputs
        purchaseOrderDetails.updatedOn = new Date()
        purchaseOrderDetails.updatedBy = systemUser.id

        // set old purchaseOrderDetails property to new purchaseOrderDetails for validation
        purchaseOrderDetails.purchaseOrderId = oldPurchaseOrderDetails.purchaseOrderId
        purchaseOrderDetails.projectId = oldPurchaseOrderDetails.projectId
        purchaseOrderDetails.createdOn = oldPurchaseOrderDetails.createdOn
        purchaseOrderDetails.createdBy = oldPurchaseOrderDetails.createdBy
        purchaseOrderDetails.id = oldPurchaseOrderDetails.id
        purchaseOrderDetails.version = oldPurchaseOrderDetails.version
        purchaseOrderDetails.itemId = oldPurchaseOrderDetails.itemId
        purchaseOrderDetails.companyId = oldPurchaseOrderDetails.companyId

        return purchaseOrderDetails
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
            throw new RuntimeException("Failed to update PR")
        }
        return updateCount
    }

    private static final String PROC_PURCHASE_ORDER_UPDATE_QUERY = """
                      UPDATE proc_purchase_order SET
                          total_price =:totalPrice,
                          total_vat_tax =:totalVatTax,
                          version = version + 1,
                          updated_on =:updatedOn,
                          updated_by =:updatedBy
                      WHERE
                          id=:id AND
                          version=:version
    """

    // Update total price , approve on update PO-details
    private int updatePOForPODetailsUpdate(ProcPurchaseOrder purchaseOrder) {
        Map queryParams = [
                id: purchaseOrder.id,
                version: purchaseOrder.version,
                totalPrice: purchaseOrder.totalPrice,
                totalVatTax: purchaseOrder.totalVatTax,
                updatedOn: DateUtility.getSqlDateWithSeconds(new Date()),
                updatedBy: procSessionUtil.appSessionUtil.getAppUser().id
        ]
        int updateCount = executeUpdateSql(PROC_PURCHASE_ORDER_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update PO")
        }
        purchaseOrder.version = purchaseOrder.version + 1
        return updateCount
    }
}
