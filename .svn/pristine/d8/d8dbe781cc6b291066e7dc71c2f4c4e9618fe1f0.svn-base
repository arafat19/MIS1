package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvInventoryTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update inventory transaction(Inventory In from Inventory) object and grid data
 *  For details go through Use-Case doc named 'UpdateForInventoryInFromInventoryActionService'
 */
class UpdateForInventoryInFromInventoryActionService extends BaseService implements ActionIntf {

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    InvSessionUtil invSessionUtil
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
    private static final String INVENTORY_TRANSACTION_OUT_OBJ = "inventoryTransactionOut"
    private static final String TRANSACTION_EXIST = "This transaction already exists"
    private static final String APPROVAL_COUNT = "approvalCount"

    /**
     * Check pre condition and build inventory transaction (Inventory In From Inventory) object with parameters from UI for update
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            long id = Long.parseLong(parameterMap.id.toString())
            int version = Integer.parseInt(parameterMap.version.toString())
            // get inventory transaction (Inventory In From Inventory) object
            InvInventoryTransaction oldInventoryTransaction = invInventoryTransactionService.read(id)
            if (oldInventoryTransaction.version != version) {
                result.put(Tools.MESSAGE, INVENTORY_TRANSACTION_NOT_FOUND)
                return result
            }
            // get inventory transaction out object
            InvInventoryTransaction invInventoryTransactionOut = null
            if (oldInventoryTransaction.itemCount <= 0) {
                long transactionId = Long.parseLong(parameterMap.transactionId)
                invInventoryTransactionOut = invInventoryTransactionService.read(transactionId)
            } else {
                invInventoryTransactionOut = invInventoryTransactionService.read(oldInventoryTransaction.transactionId)
            }
            // build inventory transaction (Inventory In From Inventory) object
            InvInventoryTransaction invInventoryTransaction = buildInventoryTransaction(parameterMap, oldInventoryTransaction, invInventoryTransactionOut)

            // checks input validation
            invInventoryTransaction.validate()
            if (invInventoryTransaction.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            // check if this transaction already exists or not
            int countExistingTransaction = countTransactionByTransactionIdForEdit(invInventoryTransaction.inventoryId, invInventoryTransaction.transactionId, invInventoryTransaction.id)
            if (countExistingTransaction >= 1) {
                result.put(Tools.MESSAGE, TRANSACTION_EXIST)
                return result
            }

            result.put(INVENTORY_TRANSACTION_OBJ, invInventoryTransaction)
            result.put(INVENTORY_TRANSACTION_OUT_OBJ, invInventoryTransactionOut)
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
     * Update inventory transaction (Inventory In From Inventory) object in DB
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
            // update inventory transaction (Inventory In From Inventory) object
            int updateStatus = invInventoryTransactionService.update(inventoryInInstance)
            if (updateStatus <= 0) {
                result.put(Tools.MESSAGE, INVENTORY_IN_UPDATE_FAILURE_MESSAGE)
                return result
            }

            result.put(INVENTORY_TRANSACTION_OBJ, inventoryInInstance)
            result.put(INVENTORY_TRANSACTION_OUT_OBJ, preResult.get(INVENTORY_TRANSACTION_OUT_OBJ))
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show updated inventory transaction (Inventory In From Inventory) object in grid
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
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) executeResult.get(INVENTORY_TRANSACTION_OBJ)
            InvInventoryTransaction invInventoryTransactionOut = (InvInventoryTransaction) executeResult.get(INVENTORY_TRANSACTION_OUT_OBJ)

            String transferDate = DateUtility.getLongDateForUI(invInventoryTransactionOut.transactionDate)
            String transactionDate = DateUtility.getLongDateForUI(invInventoryTransaction.transactionDate)
            InvInventory fromInventory = (InvInventory) invInventoryCacheUtility.read(invInventoryTransaction.transactionEntityId)
            InvInventory toInventory = (InvInventory) invInventoryCacheUtility.read(invInventoryTransaction.inventoryId)
            SystemEntity inventoryType = (SystemEntity) invInventoryTypeCacheUtility.read(invInventoryTransaction.inventoryTypeId)
            SystemEntity transactionEntityType = (SystemEntity) invInventoryTypeCacheUtility.read(fromInventory.typeId)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(invInventoryTransaction.createdBy)
            AppUser updatedBy = (AppUser) appUserCacheUtility.read(invInventoryTransaction.updatedBy)
            GridEntity object = new GridEntity()    // build grid entity object
            object.id = invInventoryTransaction.id
            object.cell = [
                    Tools.LABEL_NEW, invInventoryTransaction.id,
                    transferDate,
                    transactionDate,
                    inventoryType.key + Tools.COLON + toInventory.name,
                    transactionEntityType.key + Tools.COLON + fromInventory.name,
                    invInventoryTransaction.itemCount,
                    approvalCount,
                    createdBy.username,
                    updatedBy ? updatedBy.username : Tools.EMPTY_SPACE,
                    invInventoryTransactionOut.id
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
            LinkedHashMap previousResult = (LinkedHashMap) obj   // cast map returned from previous method
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
     * Build inventory transaction (Inventory In From Inventory) object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldInventoryTransaction -old inventory transaction (Inventory In From Inventory) object
     * @param invInventoryTransactionOut -inventory transaction out object
     * @return -updated inventory transaction (Inventory In From Inventory) object
     */
    private InvInventoryTransaction buildInventoryTransaction(GrailsParameterMap parameterMap, InvInventoryTransaction oldInventoryTransaction, InvInventoryTransaction invInventoryTransactionOut) {

        oldInventoryTransaction.transactionDate = DateUtility.parseMaskedFromDate(parameterMap.transactionDate.toString())
        oldInventoryTransaction.comments = parameterMap.comments ? parameterMap.comments : null
        oldInventoryTransaction.updatedOn = new Date()
        oldInventoryTransaction.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
        if (oldInventoryTransaction.itemCount == 0) {
            long inventoryId = Long.parseLong(parameterMap.inventoryId.toString())
            InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
            oldInventoryTransaction.projectId = inventory.projectId
            oldInventoryTransaction.inventoryTypeId = inventory.typeId
            oldInventoryTransaction.inventoryId = inventoryId
            oldInventoryTransaction.transactionEntityId = invInventoryTransactionOut.inventoryId
            oldInventoryTransaction.transactionId = invInventoryTransactionOut.id
            oldInventoryTransaction.budgetId = invInventoryTransactionOut.budgetId
        }
        return oldInventoryTransaction
    }

    private static final String TRANC_COUNT_QUERY = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction
            WHERE transaction_id=:transactionId
            AND inventory_id=:inventoryId
            AND id <> :id
            AND transaction_type_id=:transactionTypeIdIn
            AND transaction_entity_type_id=:transactionEntityTypeId
    """

    /**
     * Count number of transaction by transactionId and inventoryId
     * @param inventoryId -id of inventory
     * @param transactionId -id of inventory transaction out
     * @param id -id of inventory transaction object
     * @return -an integer containing the value of count
     */
    private int countTransactionByTransactionIdForEdit(long inventoryId, long transactionId, long id) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)
        Map queryParams = [
                id: id,
                transactionTypeIdIn: transactionTypeIn.id,
                transactionEntityTypeId: transactionEntityInventory.id,
                inventoryId: inventoryId,
                transactionId: transactionId
        ]
        List results = executeSelectSql(TRANC_COUNT_QUERY, queryParams)
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

