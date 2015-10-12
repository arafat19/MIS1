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
 *  Create new inventory transaction(Inventory Out) to move item from one inventory to another inventory and show in grid
 *  For details go through Use-Case doc named 'CreateForInventoryOutActionService'
 */
class CreateForInventoryOutActionService extends BaseService implements ActionIntf {

    private static final String INV_OUT_SAVE_SUCCESS_MESSAGE = "Inventory Out has been saved successfully"
    private static final String INV_OUT_SAVE_FAILURE_MESSAGE = "Inventory Out could not be saved"
    private static final String BUDGET_NOT_FOUND_MESSAGE = "Budget not found by budget item"
    private static final String INV_OUT_OBJ = "inventoryOut"
    private static final String BUDGET_OBJ = "budget"
    private static final String ALREADY_EXISTS = "Inventory out already exists with same inventory and date"
    private static final String SAME_INVENTORY = "Inventory-Out transaction can't be occurred in same inventory"

    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * Checking pre condition and building inventory transaction out object with parameters from UI
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
            // build inventory transaction out object
            long companyId = invSessionUtil.appSessionUtil.getCompanyId()
            InvInventoryTransaction invInventoryTransaction = buildInvInventoryTransactionObject(parameterMap, companyId)

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
            int duplicateCount = checkDuplicateInventoryOutForCreate(invInventoryTransaction, companyId)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, ALREADY_EXISTS)
                return result
            }

            result.put(BUDGET_OBJ, budget)
            result.put(INV_OUT_OBJ, invInventoryTransaction)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INV_OUT_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Create inventory transaction out object
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
            InvInventoryTransaction inventoryOutInstance = (InvInventoryTransaction) preResult.get(INV_OUT_OBJ)
            // create inventory transaction out object
            InvInventoryTransaction savedInventoryOut = invInventoryTransactionService.create(inventoryOutInstance)
            if (!savedInventoryOut) {
                result.put(Tools.MESSAGE, INV_OUT_SAVE_FAILURE_MESSAGE)
                return result
            }
            result.put(BUDGET_OBJ, preResult.get(BUDGET_OBJ))
            result.put(INV_OUT_OBJ, savedInventoryOut)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(INV_OUT_SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INV_OUT_SAVE_FAILURE_MESSAGE)
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
     * Show newly created inventory transaction out object in grid
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
            String createDate = DateUtility.getLongDateForUI(inventoryOut.transactionDate)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(inventoryOut.createdBy)
            GridEntity object = new GridEntity()    // build grid object
            object.id = inventoryOut.id
            object.cell = [
                    Tools.LABEL_NEW,
                    inventoryOut.id,
                    frmInventory,
                    toInventory,
                    budget ? budget.budgetItem : Tools.EMPTY_SPACE,
                    inventoryOut.itemCount,
                    Tools.STR_ZERO, createDate,
                    createdBy.username,
                    Tools.EMPTY_SPACE
            ]
            result.put(Tools.MESSAGE, INV_OUT_SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INV_OUT_SAVE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, INV_OUT_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INV_OUT_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build inventory transaction out object
     * @param parameterMap -serialized parameters from UI
     * @param companyId -id of company
     * @return -new inventory transaction out object
     */
    private InvInventoryTransaction buildInvInventoryTransactionObject(GrailsParameterMap parameterMap, long companyId) {
        SystemEntity transactionTypeOut = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_OUT, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)

        long inventoryId = Long.parseLong(parameterMap.inventoryId)
        InvInventory invInventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)

        InvInventoryTransaction invInventoryTransaction = new InvInventoryTransaction()
        invInventoryTransaction.version = 0
        invInventoryTransaction.transactionTypeId = transactionTypeOut.id
        invInventoryTransaction.transactionEntityTypeId = transactionEntityInventory.id
        invInventoryTransaction.transactionEntityId = Long.parseLong(parameterMap.transactionEntityId)
        invInventoryTransaction.transactionDate = DateUtility.parseMaskedFromDate(parameterMap.transactionDate)
        invInventoryTransaction.invProductionLineItemId = 0L
        invInventoryTransaction.transactionId = 0L
        invInventoryTransaction.projectId = invInventory.projectId
        invInventoryTransaction.createdOn = new Date()
        invInventoryTransaction.createdBy = invSessionUtil.appSessionUtil.getAppUser().id
        invInventoryTransaction.updatedOn = null
        invInventoryTransaction.updatedBy = 0L
        invInventoryTransaction.comments = parameterMap.comments ? parameterMap.comments : null
        invInventoryTransaction.inventoryTypeId = invInventory.typeId
        invInventoryTransaction.inventoryId = invInventory.id
        invInventoryTransaction.budgetId = 0L
        invInventoryTransaction.itemCount = 0
        invInventoryTransaction.companyId = companyId

        return invInventoryTransaction
    }

    private static final String COUNT_INVENTORY_OUT_TRANSACTION = """
        SELECT COUNT(id) count
            FROM inv_inventory_transaction
                WHERE inventory_id = :fromInventoryId
                AND transaction_date  = :transactionDate
                AND transaction_entity_id = :toInventoryId
                AND transaction_type_id = :transactionTypeId
    """

    /**
     * Get count of inventory transaction by inventory, transaction date and budgetId
     * @param invInventoryTransaction -inventory transaction out object
     * @param companyId -id of company
     * @return -a long variable containing the value of count
     */
    private long checkDuplicateInventoryOutForCreate(InvInventoryTransaction invInventoryTransaction, long companyId) {
        SystemEntity transactionTypeOut = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_OUT, companyId)

        Map queryParams = [
                fromInventoryId: invInventoryTransaction.inventoryId,
                toInventoryId: invInventoryTransaction.transactionEntityId,
                transactionDate: DateUtility.getSqlDate(invInventoryTransaction.transactionDate),
                transactionTypeId: transactionTypeOut.id
        ]
        List<GroovyRowResult> resultCount = executeSelectSql(COUNT_INVENTORY_OUT_TRANSACTION, queryParams)
        if (resultCount && resultCount.size() > 0) {
            return resultCount[0].count
        }
        return 0
    }
}
