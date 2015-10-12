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
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update inventory transaction(Inventory In from Supplier) object and grid data
 *  For details go through Use-Case doc named 'UpdateForInventoryInFromSupplierActionService'
 */
class UpdateForInventoryInFromSupplierActionService extends BaseService implements ActionIntf {

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String INVENTORY_IN_UPDATE_SUCCESS_MESSAGE = "Inventory-In transaction has been updated successfully"
    private static final String INVENTORY_IN_UPDATE_FAILURE_MESSAGE = "Can not update Inventory-In transaction"
    private static final String SERVER_ERROR_MESSAGE = "Failed to update inventory in transaction"
    private static final String INVENTORY_TRANSACTION_NOT_FOUND = "Inventory transaction may in use or change. Please refresh"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String INVENTORY_TRANSACTION_OBJ = "inventoryTransaction"
    private static final String PURCHASE_ORDER_NOT_APPROVE = "Your selected purchase order is not approved"
    private static final String PURCHASE_ORDER_NOT_FOUND = "Purchase order may in use or change. Please refresh the page"
    private static final String PURCHASE_ORDER_EXIST_MESSAGE = "Purchase order already stored in to this inventory"
    private static final String APPROVAL_COUNT = "approvalCount"

    /**
     * Check pre condition and build inventory transaction (Inventory In From Supplier) object with parameters from UI for update
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long id = Long.parseLong(parameterMap.id.toString())
            // get inventory transaction (Inventory In From Supplier) object
            InvInventoryTransaction oldInventoryTransaction = invInventoryTransactionService.read(id)
            // check if the inventory transaction (Inventory In From Supplier) object exists or not
            if (!oldInventoryTransaction) {
                result.put(Tools.MESSAGE, INVENTORY_TRANSACTION_NOT_FOUND)
                return result
            }
            Object purchaseOrder = null
            if (oldInventoryTransaction.itemCount <= 0) {
                long purchaseOrderId = Long.parseLong(parameterMap.purchaseOrderId.toString())

                if (purchaseOrderId != oldInventoryTransaction.transactionId) {
                    // check PO validation
                    purchaseOrder = procurementImplService.readPO(purchaseOrderId)
                    if (!purchaseOrder) {
                        result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND)
                        return result
                    }
                    // check PO approval
                    if (purchaseOrder.approvedByDirectorId <= 0 || purchaseOrder.approvedByProjectDirectorId <= 0) {
                        result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_APPROVE)
                        return result
                    }
                }
            }
            // build inventory transaction (Inventory In From Supplier) object for update
            InvInventoryTransaction invInventoryTransaction = buildInventoryTransaction(parameterMap, oldInventoryTransaction, purchaseOrder)

            // checks input validation
            invInventoryTransaction.validate()
            if (invInventoryTransaction.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            int countInventoryInFromSupplier = countInventoryInFromSupplierForEdit(invInventoryTransaction.inventoryId, invInventoryTransaction.transactionId, invInventoryTransaction.id)
            // check if entry from PO already exists in this inventory
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
     * Update inventory transaction (Inventory In From Supplier) object in DB
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            InvInventoryTransaction inventoryInInstance = (InvInventoryTransaction) preResult.get(INVENTORY_TRANSACTION_OBJ)

            Long invTransactionId = inventoryInInstance.id
            int approvalCount = countApprovedItem(invTransactionId) // cont number of approved items
            // update inventory transaction (Inventory In From Supplier) object
            int updateStatus = invInventoryTransactionService.update(inventoryInInstance)
            if (updateStatus <= 0) {
                result.put(Tools.MESSAGE, INVENTORY_IN_UPDATE_FAILURE_MESSAGE)
                return result
            }

            result.put(INVENTORY_TRANSACTION_OBJ, inventoryInInstance)
            result.put(APPROVAL_COUNT, approvalCount)
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
     * Show updated inventory transaction (Inventory In From Supplier) object in grid
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

            int approvalCount = (int) executeResult.get(APPROVAL_COUNT)
            InvInventoryTransaction inventoryInFromSupplier = (InvInventoryTransaction) executeResult.get(INVENTORY_TRANSACTION_OBJ)

            Supplier supplier = (Supplier) supplierCacheUtility.read(inventoryInFromSupplier.transactionEntityId)
            long purchaseOrderId = inventoryInFromSupplier.transactionId
            SystemEntity inventoryType = (SystemEntity) invInventoryTypeCacheUtility.read(inventoryInFromSupplier.inventoryTypeId)
            InvInventory fromInventory = (InvInventory) invInventoryCacheUtility.read(inventoryInFromSupplier.inventoryId)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(inventoryInFromSupplier.createdBy)
            AppUser updatedBy = (AppUser) appUserCacheUtility.read(inventoryInFromSupplier.updatedBy)
            GridEntity object = new GridEntity()    // build grid entity object
            object.id = inventoryInFromSupplier.id
            object.cell = [
                    Tools.LABEL_NEW,
                    inventoryInFromSupplier.id,
                    inventoryType.key + Tools.COLON + fromInventory.name,
                    supplier.name,
                    purchaseOrderId,
                    inventoryInFromSupplier.itemCount,
                    approvalCount,
                    createdBy.username,
                    updatedBy ? updatedBy.username : Tools.EMPTY_SPACE
            ]

            result.put(Tools.ENTITY, object)
            result.put(Tools.MESSAGE, INVENTORY_IN_UPDATE_SUCCESS_MESSAGE)
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
            LinkedHashMap previousResult = (LinkedHashMap) obj  // cast map returned from previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, INVENTORY_IN_UPDATE_FAILURE_MESSAGE)
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
     * Build inventory transaction (Inventory In From Supplier) object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldInventoryTransaction -old inventory transaction (Inventory In From Inventory) object
     * @param purchaseOrder -purchase order object
     * @return -updated inventory transaction (Inventory In From Supplier) object
     */
    private InvInventoryTransaction buildInventoryTransaction(GrailsParameterMap parameterMap, InvInventoryTransaction oldInventoryTransaction, Object purchaseOrder) {

        oldInventoryTransaction.comments = parameterMap.comments ? parameterMap.comments : null
        oldInventoryTransaction.updatedOn = new Date()
        oldInventoryTransaction.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
        if (oldInventoryTransaction.itemCount <= 0) {
            long inventoryId = Long.parseLong(parameterMap.inventoryId.toString())
            InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
            oldInventoryTransaction.inventoryTypeId = inventory.typeId
            oldInventoryTransaction.inventoryId = inventoryId
            oldInventoryTransaction.projectId = inventory.projectId
            if (purchaseOrder) {
                oldInventoryTransaction.transactionEntityId = purchaseOrder.supplierId
                oldInventoryTransaction.transactionId = purchaseOrder.id
            }
        }
        return oldInventoryTransaction
    }

    private static final String INV_IN_FROM_SUPP_COUNT_QUERY = """
                SELECT COUNT(id) AS count
                FROM inv_inventory_transaction
                WHERE transaction_id=:purchaseOrderId
                    AND inventory_id=:inventoryId
                    AND transaction_type_id=:transactionTypeId
                    AND transaction_entity_type_id=:transactionEntityTypeId
                    AND inv_inventory_transaction.id <> :invTransactionId
    """

    /**
     * Count number of transaction by purchaseOrderId and inventoryId
     * @param inventoryId -id of inventory
     * @param purchaseOrderId -id of purchase order
     * @param invTransactionId -id of inventory transaction object
     * @return -an integer containing the value of count
     */
    private int countInventoryInFromSupplierForEdit(long inventoryId, long purchaseOrderId, long invTransactionId) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)
        Map queryParams = [
                transactionTypeId: transactionTypeIn.id,
                transactionEntityTypeId: transactionEntitySupplier.id,
                inventoryId: inventoryId,
                purchaseOrderId: purchaseOrderId,
                invTransactionId: invTransactionId
        ]
        List results = executeSelectSql(INV_IN_FROM_SUPP_COUNT_QUERY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String APPROVED_ITEM_COUNT_QUERY = """
        SELECT count(iitd.id) FROM inv_inventory_transaction_details iitd
        WHERE iitd.inventory_transaction_id=:invTransactionId AND
            iitd.approved_by > 0
            AND iitd.is_current = TRUE
    """

    /**
     * Count number of approved items
     * @param invTransactionId -id of inventory transaction object
     * @return -an integer containing the value of count
     */
    private int countApprovedItem(long invTransactionId) {
        Map queryParams = [
                invTransactionId: invTransactionId
        ]
        List<GroovyRowResult> countResult = executeSelectSql(APPROVED_ITEM_COUNT_QUERY, queryParams)
        int total = countResult[0].count
        return total
    }
}

