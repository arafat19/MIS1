package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.integration.budget.BudgetPluginConnector
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
 *  Update inventory transaction out object and grid data
 *  For details go through Use-Case doc named 'UpdateForInventoryOutActionService'
 */
class UpdateForInventoryOutActionService extends BaseService implements ActionIntf {

    private static final String INV_OUT_UPDATE_SUCCESS_MESSAGE = "Inventory out has been updated successfully"
    private static final String INV_OUT_UPDATE_FAILURE_MESSAGE = "Can not update inventory out"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to update inventory out"
    private static final String INV_TRANSACTION_NOT_FOUND = "Inventory transaction may in use or change. Please refresh"
    private static final String INV_OUT_OBJ = "inventoryOutInstance"
    private static final String BUDGET_OBJ = "budget"
    private static final String BUDGET_NOT_FOUND_MESSAGE = "Budget not found by budget item"
    private static final String ALREADY_EXISTS = "Inventory out transaction already exists with same inventory"
    private static final String SAME_INVENTORY = "Inventory-Out transaction can't be occurred in same inventory"
    private static final String ALREADY_ACK = "The Inventory-Out transaction already acknowledged"

    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
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

    /**
     * Check pre condition and build inventory transaction out object with parameters from UI for update
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

            long id = Long.parseLong(parameterMap.id.toString())
            int version = Integer.parseInt(parameterMap.version.toString())
            // get inventory transaction out object
            InvInventoryTransaction oldInventoryTransaction = invInventoryTransactionService.read(id)
            // check if inventory transaction out object exists or not
            if ((!oldInventoryTransaction) || (oldInventoryTransaction.version != version)) {
                result.put(Tools.MESSAGE, INV_TRANSACTION_NOT_FOUND)
                return result
            }
            // build inventory transaction out object
            InvInventoryTransaction invInventoryTransaction = buildInventoryTransaction(parameterMap, oldInventoryTransaction)
            Object budget = null
            if (parameterMap.budgetItem) {
                // get budget by budget line item
                budget = budgetImplService.readByBudgetItem(parameterMap.budgetItem)
                // check if budget exists or not
                if (!budget) {
                    result.put(Tools.MESSAGE, BUDGET_NOT_FOUND_MESSAGE)
                    return result
                }
                invInventoryTransaction.budgetId = budget.id
            }
            // check if IN and OUT inventory is same
            if (invInventoryTransaction.inventoryId == invInventoryTransaction.transactionEntityId) {
                result.put(Tools.MESSAGE, SAME_INVENTORY)
                return result
            }

            // check uniqueness of inventory transaction
            int duplicateCount = checkDuplicateInventoryOutForUpdate(invInventoryTransaction)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, ALREADY_EXISTS)
                return result
            }
            // count acknowledged inventory out transaction
            int ackCount = getAckCountOfInventoryOut(invInventoryTransaction.id)
            // if the transaction is already acknowledged then fromInventory, toInventory and budget is not changeable
            if ((ackCount > 0) && (invInventoryTransaction.inventoryId != oldInventoryTransaction.inventoryId ||
                    invInventoryTransaction.transactionEntityId != oldInventoryTransaction.transactionEntityId ||
                    invInventoryTransaction.budgetId != oldInventoryTransaction.budgetId)) {
                result.put(Tools.MESSAGE, ALREADY_ACK)
                return result
            }

            result.put(BUDGET_OBJ, budget)
            result.put(INV_OUT_OBJ, invInventoryTransaction)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Update inventory transaction out object in DB
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
            InvInventoryTransaction inventoryOutInstance = (InvInventoryTransaction) preResult.get(INV_OUT_OBJ)
            int updateStatus = 0
            // update inventory transaction out object
            updateStatus = invInventoryTransactionService.update(inventoryOutInstance)
            if (updateStatus <= 0) {
                result.put(Tools.MESSAGE, INV_OUT_UPDATE_FAILURE_MESSAGE)
                return result
            }

            result.put(BUDGET_OBJ, preResult.get(BUDGET_OBJ))
            result.put(INV_OUT_OBJ, inventoryOutInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Show updated inventory transaction out object in grid
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
            InvInventoryTransaction inventoryOut = (InvInventoryTransaction) executeResult.get(INV_OUT_OBJ)
            Object budget = executeResult.get(BUDGET_OBJ)

            InvInventory fromInventory = (InvInventory) invInventoryCacheUtility.read(inventoryOut.inventoryId)
            SystemEntity fromInventoryType = (SystemEntity) invInventoryTypeCacheUtility.read(fromInventory.typeId)
            String frmInventory = fromInventoryType.key + Tools.COLON + fromInventory.name
            InvInventory to_Inventory = (InvInventory) invInventoryCacheUtility.read(inventoryOut.transactionEntityId)
            SystemEntity toInventoryType = (SystemEntity) invInventoryTypeCacheUtility.read(to_Inventory.typeId)
            String toInventory = toInventoryType.key + Tools.COLON + to_Inventory.name
            String transactionDate = DateUtility.getLongDateForUI(inventoryOut.transactionDate)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(inventoryOut.createdBy)
            AppUser updatedBy = (AppUser) appUserCacheUtility.read(inventoryOut.updatedBy)
            int approvedCount = countApprovedItem(inventoryOut.id)
            GridEntity object = new GridEntity()    // build grid entity object
            object.id = inventoryOut.id
            object.cell = [
                    Tools.LABEL_NEW,
                    inventoryOut.id,
                    frmInventory,
                    toInventory,
                    budget ? budget.budgetItem : Tools.EMPTY_SPACE,
                    inventoryOut.itemCount,
                    approvedCount,
                    transactionDate,
                    createdBy.username,
                    updatedBy ? updatedBy.username : Tools.EMPTY_SPACE
            ]

            result.put(Tools.ENTITY, object)
            result.put(Tools.MESSAGE, INV_OUT_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
                result.put(Tools.MESSAGE, INV_OUT_UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build inventory transaction out object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldInventoryTransaction -old inventory transaction out object
     * @return -updated inventory transaction out object
     */
    private InvInventoryTransaction buildInventoryTransaction(GrailsParameterMap parameterMap, InvInventoryTransaction oldInventoryTransaction) {

        oldInventoryTransaction.comments = parameterMap.comments ? parameterMap.comments : null
        oldInventoryTransaction.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
        oldInventoryTransaction.updatedOn = new Date()
        if (oldInventoryTransaction.itemCount == 0) {
            long inventoryId = Long.parseLong(parameterMap.inventoryId.toString())
            InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
            oldInventoryTransaction.inventoryId = inventoryId
            oldInventoryTransaction.inventoryTypeId = inventory.typeId
            oldInventoryTransaction.projectId = inventory.projectId
            oldInventoryTransaction.transactionEntityId = Long.parseLong(parameterMap.transactionEntityId.toString())
            oldInventoryTransaction.transactionDate = DateUtility.parseMaskedFromDate(parameterMap.transactionDate.toString())
        }
        return oldInventoryTransaction
    }

    private static final String COUNT_INVENTORY_OUT_TRANSACTION = """
        SELECT COUNT(id) count
            FROM inv_inventory_transaction
                WHERE inventory_id = :fromInventoryId
                AND transaction_date  = :transactionDate
                AND transaction_entity_id = :toInventoryId
                AND transaction_type_id = :transactionTypeId
                AND id <> :invOutId
    """

    /**
     * Get count of inventory transaction by inventory, transaction date and budgetId
     * @param invInventoryTransaction -inventory transaction out object
     * @return -a long variable containing the value of count
     */
    private long checkDuplicateInventoryOutForUpdate(InvInventoryTransaction invInventoryTransaction) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeOut = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_OUT, companyId)

        Map queryParams = [
                fromInventoryId: invInventoryTransaction.inventoryId,
                transactionDate: DateUtility.getSqlDate(invInventoryTransaction.transactionDate),
                toInventoryId: invInventoryTransaction.transactionEntityId,
                transactionTypeId: transactionTypeOut.id,
                invOutId: invInventoryTransaction.id
        ]
        List<GroovyRowResult> resultCount = executeSelectSql(COUNT_INVENTORY_OUT_TRANSACTION, queryParams)
        if (resultCount && resultCount.size() > 0) {
            return resultCount[0].count
        }
        return 0
    }

    private static final String ACK_COUNT_QUERY = """
             SELECT count(iit.id)
             FROM inv_inventory_transaction iit
                WHERE iit.transaction_id =:invTransactionId AND
                iit.transaction_type_id=:transactionTypeIdIn AND
                iit.transaction_entity_type_id=:transactionEntityTypeId
    """

    /**
     * Count acknowledged inventory out transaction
     * @param invTransactionId -id of inventory transaction out object
     * @return -an integer containing the value of count
     */
    private int getAckCountOfInventoryOut(long invTransactionId) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)

        Map queryParams = [
                transactionTypeIdIn: transactionTypeIn.id,
                transactionEntityTypeId: transactionEntityInventory.id,
                invTransactionId: invTransactionId
        ]
        List<GroovyRowResult> countResult = executeSelectSql(ACK_COUNT_QUERY, queryParams)
        int total = countResult[0].count
        return total
    }

    private static final String APPROVED_ITEM_COUNT_QUERY = """
             SELECT count(iitd.id) FROM inv_inventory_transaction_details iitd
                WHERE iitd.inventory_transaction_id=:invTransactionId AND
                iitd.approved_by > 0
                AND iitd.is_current = TRUE
    """

    /**
     * Count approved item of inventory transaction out object
     * @param invTransactionId -id of inventory transaction out object
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
