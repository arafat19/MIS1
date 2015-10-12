package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.*
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new inventory transaction(Inventory In From Supplier) to receive item from suppliers and show in grid
 *  For details go through Use-Case doc named 'CreateForInventoryInFromSupplierActionService'
 */
class CreateForInventoryInFromSupplierActionService extends BaseService implements ActionIntf {

    private static final String INVENTORY_IN_SAVE_SUCCESS_MESSAGE = "Inventory-In has been saved successfully"
    private static final String INVENTORY_IN_SAVE_FAILURE_MESSAGE = "Can not saved Inventory-In"
    private static final String SERVER_ERROR_MESSAGE = "Can't to save Inventory-In transaction"
    private static final String PURCHASE_ORDER_NOT_FOUND = "Purchase order may in use or change. Please refresh"
    private static final String PURCHASE_ORDER_NOT_APPROVE = "Your selected purchase order is not approved"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String INVENTORY_TRANSACTION_OBJ = "inventoryTransaction"
    private static final String PURCHASE_ORDER_EXIST_MESSAGE = "Purchase order already exists in this inventory"

    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * Checking pre condition and building inventory transaction object with parameters from UI
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long purchaseOrderId = Long.parseLong(parameterMap.purchaseOrderId)
            long companyId = invSessionUtil.appSessionUtil.getCompanyId()

            Object purchaseOrder = procurementImplService.readPO(purchaseOrderId)
            // check PO validation
            if (!purchaseOrder) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND)
                return result
            }

            // check PO approval
            if (purchaseOrder.approvedByDirectorId <= 0 || purchaseOrder.approvedByProjectDirectorId <= 0) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_APPROVE)
                return result
            }

            long inventoryId = Long.parseLong(parameterMap.inventoryId)
            InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)

            // build inventory transaction object (Inventory In From Supplier)
            InvInventoryTransaction invInventoryTransaction = buildInvInventoryTransactionObject(parameterMap, purchaseOrder, inventory, companyId)

            // checks input validation
            invInventoryTransaction.validate()
            if (invInventoryTransaction.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            int countInventoryInFromSupplier = countInventoryInFromSupplier(invInventoryTransaction.inventoryId, invInventoryTransaction.transactionId, companyId)
            // check whether PO already exists in the selected inventory or not
            if (countInventoryInFromSupplier >= 1) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_EXIST_MESSAGE)
                return result
            }

            result.put(INVENTORY_TRANSACTION_OBJ, invInventoryTransaction)
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
     * Create inventory transaction object (Inventory In From Supplier)
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) preResult.get(INVENTORY_TRANSACTION_OBJ)
            // create inventory transaction object
            InvInventoryTransaction returnInventoryTransaction = invInventoryTransactionService.create(invInventoryTransaction)
            if (!returnInventoryTransaction) {
                result.put(Tools.MESSAGE, INVENTORY_IN_SAVE_FAILURE_MESSAGE)
                return result
            }
            result.put(INVENTORY_TRANSACTION_OBJ, returnInventoryTransaction)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(SERVER_ERROR_MESSAGE)
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
     * Show newly created inventory transaction object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            InvInventoryTransaction inventoryInFromSupplier = (InvInventoryTransaction) executeResult.get(INVENTORY_TRANSACTION_OBJ)
            Supplier supplier = (Supplier) supplierCacheUtility.read(inventoryInFromSupplier.transactionEntityId)
            long purchaseOrderId = inventoryInFromSupplier.transactionId
            SystemEntity inventoryType = (SystemEntity) invInventoryTypeCacheUtility.read(inventoryInFromSupplier.inventoryTypeId)
            InvInventory fromInventory = (InvInventory) invInventoryCacheUtility.read(inventoryInFromSupplier.inventoryId)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(inventoryInFromSupplier.createdBy)

            GridEntity object = new GridEntity()    //build grid object
            object.id = inventoryInFromSupplier.id
            object.cell = [
                    Tools.LABEL_NEW,
                    inventoryInFromSupplier.id,
                    inventoryType.key + Tools.COLON + fromInventory.name,
                    supplier.name,
                    purchaseOrderId,
                    inventoryInFromSupplier.itemCount,
                    Tools.STR_ZERO,
                    createdBy.username,
                    Tools.EMPTY_SPACE
            ]

            result.put(Tools.MESSAGE, INVENTORY_IN_SAVE_SUCCESS_MESSAGE)
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
            LinkedHashMap previousResult = (LinkedHashMap) obj   // cast map returned from previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, INVENTORY_IN_SAVE_FAILURE_MESSAGE)
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
     * Build inventory transaction object (Inventory In From Supplier)
     * @param parameterMap -serialized parameters from UI
     * @param purchaseOrder -purchase order object
     * @param inventory -inventory object
     * @param companyId -id of company
     * @return -new inventory transaction object
     */
    private InvInventoryTransaction buildInvInventoryTransactionObject(GrailsParameterMap parameterMap, Object purchaseOrder, InvInventory inventory,
                                                                          long companyId) {
        InvInventoryTransaction invInventoryTransaction = new InvInventoryTransaction()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)

        invInventoryTransaction.version = 0
        invInventoryTransaction.transactionTypeId = transactionTypeIn.id
        invInventoryTransaction.transactionEntityTypeId = transactionEntitySupplier.id
        invInventoryTransaction.transactionEntityId = purchaseOrder.supplierId
        invInventoryTransaction.transactionId = purchaseOrder.id
        invInventoryTransaction.projectId = inventory.projectId
        invInventoryTransaction.createdOn = new Date()
        invInventoryTransaction.createdBy = invSessionUtil.appSessionUtil.getAppUser().id
        invInventoryTransaction.updatedOn = null
        invInventoryTransaction.updatedBy = 0L
        invInventoryTransaction.comments = parameterMap.comments ? parameterMap.comments : null
        invInventoryTransaction.inventoryTypeId = inventory.typeId
        invInventoryTransaction.inventoryId = inventory.id
        invInventoryTransaction.itemCount = 0
        invInventoryTransaction.companyId = companyId
        invInventoryTransaction.transactionDate = new Date()

        return invInventoryTransaction
    }

    private static final String INVENTORY_COUNT_QUERY = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction
            WHERE transaction_id=:transactionId
            AND inventory_id=:inventoryId
            AND transaction_type_id=:transactionTypeId
            AND transaction_entity_type_id=:transactionEntityTypeId
    """

    /**
     * Count number of transaction by purchaseOrderId and inventoryId
     * @param inventoryId -id of inventory
     * @param purchaseOrderId -id of purchase order
     * @param companyId -id of company
     * @return -an integer containing the value of count
     */
    private int countInventoryInFromSupplier(long inventoryId, long purchaseOrderId, long companyId) {
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)
        Map queryParams = [
                inventoryId: inventoryId,
                transactionId: purchaseOrderId,
                transactionTypeId: transactionTypeIn.id,
                transactionEntityTypeId: transactionEntitySupplier.id
        ]
        List results = executeSelectSql(INVENTORY_COUNT_QUERY, queryParams)
        int count = results[0].count
        return count
    }
}
