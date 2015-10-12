package com.athena.mis.procurement.actions.purchaseorder

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SupplierItem
import com.athena.mis.application.service.SupplierItemService
import com.athena.mis.application.service.SupplierService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.application.utility.SupplierItemCacheUtility
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.entity.ProcPurchaseOrderDetails
import com.athena.mis.procurement.service.PurchaseOrderService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Approve Purchase Order
 * For details go through Use-Case doc named 'ApprovePurchaseOrderActionService'
 */
class ApprovePurchaseOrderActionService extends BaseService implements ActionIntf {

    private static final String PURCHASE_ORDER_SAVE_SUCCESS_MESSAGE = "Purchase Order has been approved successfully"
    private static final String PURCHASE_ORDER_SAVE_FAILURE_MESSAGE = "Purchase Order could not be approved"
    private static final String PURCHASE_ORDER_NOT_FOUND = "Purchase Order not found"
    private static final String ALREADY_APPROVED_DIRECTOR = "Purchase Order already approved by director"
    private static final String ALREADY_APPROVED_PROJECT_DIRECTOR = "Purchase Order already approved by project director"
    private static final String PO_HAS_NO_ITEMS = "PO has no item(s)"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Fail to approve Purchase Order due to Invalid Input"
    private static final String PURCHASE_ORDER_OBJ = "purchaseOrderInstance"
    private static final String SYSTEM_USER_OBJ = "systemUserInstance"
    private static final String SERVER_ERROR_MESSAGE = "Fail to approve Purchase Order"
    private static final String IS_USER_DIRECTOR = "isUserDirector"
    private static final String IS_USER_PROJECT_DIRECTOR = "isUserProjectDirector"
    private static final String HAS_NO_RIGHT_MESSAGE = "Logged-In User has no right to approve the purchase order"
    private static final String NOT_SENT_FOR_APPROVAL_MESSAGE = "Selected purchase order has not been sent for approval"

    private final Logger log = Logger.getLogger(getClass())

    PurchaseOrderService purchaseOrderService
    SupplierItemService supplierItemService
    SupplierService supplierService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    SupplierItemCacheUtility supplierItemCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    /**
     * Check all pre conditions
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing po object, system user obj, isDirector, isProjectManager & isError(True/False)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            boolean isUserDirector = procSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_DIRECTOR)
            boolean isUserProjectDirector = procSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR)
            // user has not right to approve PR
            if (!isUserDirector && !isUserProjectDirector) {
                result.put(Tools.MESSAGE, HAS_NO_RIGHT_MESSAGE)
                return result
            }
            long id = Long.parseLong(parameterMap.id.toString())
            ProcPurchaseOrder purchaseOrderInstance = purchaseOrderService.read(id)
            //checking purchaseOrder Instance existence
            if (!purchaseOrderInstance) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND)
                return result
            }
            if (!purchaseOrderInstance.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_SENT_FOR_APPROVAL_MESSAGE)
                return result
            }
            int approveAs = Integer.parseInt(parameterMap.approveAs.toString())
            AppUser systemUser = procSessionUtil.appSessionUtil.getAppUser()
            // if it is already approved by director then return
            if (purchaseOrderInstance.approvedByDirectorId && (approveAs == roleTypeCacheUtility.ROLE_TYPE_DIRECTOR)) {
                result.put(Tools.MESSAGE, ALREADY_APPROVED_DIRECTOR)
                return result
            }
            // if it is already approved by project director then return
            else if (purchaseOrderInstance.approvedByProjectDirectorId && (approveAs == roleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR)) {
                result.put(Tools.MESSAGE, ALREADY_APPROVED_PROJECT_DIRECTOR)
                return result
            }
            if (purchaseOrderInstance.itemCount <= 0) {
                result.put(Tools.MESSAGE, PO_HAS_NO_ITEMS)
                return result
            }
            result.put(IS_USER_DIRECTOR, isUserDirector)
            result.put(IS_USER_PROJECT_DIRECTOR, isUserProjectDirector)
            result.put(PURCHASE_ORDER_OBJ, purchaseOrderInstance)
            result.put(SYSTEM_USER_OBJ, systemUser)
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
     * Get purchase order objects
     * @param parameters - N/A
     * @param obj -  object from pre execute method
     * @return - a map with purchase order object and relevant message
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            GrailsParameterMap params = (GrailsParameterMap) parameters
            ProcPurchaseOrder purchaseOrderInstance = (ProcPurchaseOrder) preResult.get(PURCHASE_ORDER_OBJ)
            AppUser systemUser = (AppUser) preResult.get(SYSTEM_USER_OBJ)
            int approveAs = Integer.parseInt(params.approveAs.toString())
            //set approval data of purchase request
            boolean isUserDirector = (boolean) preResult.get(IS_USER_DIRECTOR)
            boolean isUserProjectDirector = (boolean) preResult.get(IS_USER_PROJECT_DIRECTOR)
            //set approval data of purchase request
            if (isUserDirector && (approveAs == roleTypeCacheUtility.ROLE_TYPE_DIRECTOR)) {
                purchaseOrderInstance.approvedByDirectorId = systemUser.id
            } else if (isUserProjectDirector && (approveAs == roleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR)) {
                purchaseOrderInstance.approvedByProjectDirectorId = systemUser.id

            } else {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
                return result
            }

            updateForApproval(purchaseOrderInstance)
            if (purchaseOrderInstance.approvedByDirectorId > 0) {
                createSupplierItem(purchaseOrderInstance)
            }

            result.put(PURCHASE_ORDER_OBJ, purchaseOrderInstance)
            result.put(Tools.MESSAGE, PURCHASE_ORDER_SAVE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            //@todo:rollback
            throw new RuntimeException("Fail to approve PO")
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
     * 1. Show newly approved po in grid
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
            ProcPurchaseOrder purchaseOrderInstance = (ProcPurchaseOrder) receiveResult.get(PURCHASE_ORDER_OBJ)
            GridEntity object = new GridEntity()
            object.id = purchaseOrderInstance.id
            String createdOn = DateUtility.getDateForUI(purchaseOrderInstance.createdOn)
            Object supplier = supplierCacheUtility.read(purchaseOrderInstance.supplierId)

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
                    purchaseOrderInstance.approvedByDirectorId ? Tools.YES : Tools.NO,
                    purchaseOrderInstance.approvedByProjectDirectorId ? Tools.YES : Tools.NO
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
            if (receiveResult.message) {
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

    private static final String UPDATE_QUERY = """
                      UPDATE proc_purchase_order
                        SET
                          approved_by_director_id=:approvedByDirectorId,
                          approved_by_project_director_id=:approvedByProjectDirectorId,
                          version= version + 1
                        WHERE
                          id=:id AND
                          version=:version
                 """
    /**
     * Update Purchase Order for approval
     * @param purchaseOrder
     * @return - int value for success(e.g- 1) or throw exception with message for failure
     */
    private int updateForApproval(ProcPurchaseOrder purchaseOrder) {
        Map queryParams = [
                id: purchaseOrder.id,
                version: purchaseOrder.version,
                approvedByProjectDirectorId: purchaseOrder.approvedByProjectDirectorId,
                approvedByDirectorId: purchaseOrder.approvedByDirectorId
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to approve PO")
        }
        return updateCount;
    }

    /**
     * Create SupplierItem from purchase order details
     * 1. pull purchase order details
     * 2. create supplier item
     * 3. update supplier item cache utility
     * 4. update supplier table and supplier cache utility
     * @param purchaseOrder - purchase order object
     */
    private void createSupplierItem(ProcPurchaseOrder purchaseOrder) {
        List<ProcPurchaseOrderDetails> purchaseOrderDetailsList = ProcPurchaseOrderDetails.findAllByPurchaseOrderId(purchaseOrder.id, [readOnly: true])

        int countPODetails = purchaseOrderDetailsList.size()
        if (countPODetails > 0) {
            ProcPurchaseOrderDetails purchaseOrderDetails
            for (int i = 0; i < countPODetails; i++) {
                purchaseOrderDetails = purchaseOrderDetailsList[i]
                SupplierItem existingSupplierItem = supplierItemCacheUtility.readByItemIdAndSupplierId(purchaseOrderDetails.itemId, purchaseOrder.supplierId)
                if (!existingSupplierItem) {
                    ///add supplier item if it is not exist
                    SupplierItem supplierItem = buildSupplierItem(purchaseOrder.supplierId, purchaseOrderDetails.itemId)
                    SupplierItem newSupplierItemInstance = supplierItemService.create(supplierItem)
                    supplierItemCacheUtility.add(newSupplierItemInstance, supplierItemCacheUtility.SORT_ON_NAME, supplierItemCacheUtility.SORT_ORDER_ASCENDING)
                    Supplier supplier = (Supplier) supplierCacheUtility.read(supplierItem.supplierId)
                    supplier.itemCount = supplier.itemCount + 1
                    supplierService.updateForSupplierItem(supplier)
                    supplierCacheUtility.update(supplier, supplierCacheUtility.SORT_ON_NAME, supplierCacheUtility.SORT_ORDER_ASCENDING)
                }
            }
        }
    }
    /**
     * Build supplier item object
     * @param supplierId - supplier id
     * @param itemId - item id
     * @return -supplier item object
     */
    private SupplierItem buildSupplierItem(long supplierId, long itemId) {
        SupplierItem supplierItem = new SupplierItem()
        supplierItem.version = 0
        supplierItem.supplierId = supplierId
        supplierItem.itemId = itemId
        supplierItem.companyId = procSessionUtil.appSessionUtil.getCompanyId()
        supplierItem.createdOn = new Date()
        supplierItem.createdBy = procSessionUtil.appSessionUtil.appUser.id
        return supplierItem
    }
}
