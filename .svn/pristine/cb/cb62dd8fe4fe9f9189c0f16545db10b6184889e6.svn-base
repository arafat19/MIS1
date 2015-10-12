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
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new InventoryConsumption to consume item(s) against a budget
 *  For details go through Use-Case doc named 'CreateForInventoryConsumptionActionService'
 */
class CreateForInventoryConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    BudgetPluginConnector budgetImplService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private static final String INVENTORY_NOT_FOUND = "Inventory not found."
    private static final String SERVER_ERROR_MESSAGE = "Fail to create Inventory Consumption"
    private static final String SAVE_FAILURE_MESSAGE = "Can not create Inventory-Consumption Transaction"
    private static final String SAVE_SUCCESS_MESSAGE = "Inventory-Consumption Transaction has been created successfully"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input."
    private static final String TRANSACTION_EXISTS = "This transaction already exists"
    private static final String BUDGET_NOT_FOUND = "Budget not found by the given budget item"
    private static final String BUDGET_OBJ = "budget"
    private static final String INVENTORY_TRANSACTION = "invInventoryTransaction"

    /**
     * validate different criteria to consume item(s). Such as :
     *      Check existence of Inventory Obj,
     *      Check existence of budget Obj
     *
     * Create parent object(InvTransaction) of InventoryConsumption
     *
     * @Params parameters -Receives the serialized parameters send from UI e.g inventoryId, budgetItem
     * @Params obj -N/A
     *
     * @Return -a map containing all objects necessary for execute
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long companyId = invSessionUtil.appSessionUtil.getCompanyId()
            if (!parameterMap.inventoryId) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            long inventoryId = Long.parseLong(parameterMap.inventoryId.toString())
            InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
            if (!inventory) {
                result.put(Tools.MESSAGE, INVENTORY_NOT_FOUND)
                return result
            }

            Object budget = budgetImplService.readByBudgetItem(parameterMap.budgetItem.toString())
            if (!budget) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }

            //check weather same consumption-parent is present or not
            int existingInvTransactionCount = countInvConsumptionByInventoryAndBudget(inventory.id, budget.id, companyId)
            if (existingInvTransactionCount > 0) {
                result.put(Tools.MESSAGE, TRANSACTION_EXISTS)
                return result
            }

            // Build invTransactionConsumption Object
            InvInventoryTransaction invInventoryTransaction = buildInventoryTransaction(parameterMap, budget, companyId)

            // checks input validation
            invInventoryTransaction.validate()
            if (invInventoryTransaction.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            result.put(BUDGET_OBJ, budget)
            result.put(INVENTORY_TRANSACTION, invInventoryTransaction)
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
     *  Method to save InventoryConsumption (parent) object
     *
     * @Params parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains InventoryConsumption (parent) object
     *
     * @Return -map contains isError(true/false), savedInventoryConsumption (parent) object
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj

            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) preResult.get(INVENTORY_TRANSACTION)
            InvInventoryTransaction newInvInventoryTransaction = invInventoryTransactionService.create(invInventoryTransaction)
            if (!newInvInventoryTransaction) {
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
                return result
            }

            result.put(BUDGET_OBJ, preResult.get(BUDGET_OBJ))
            result.put(INVENTORY_TRANSACTION, invInventoryTransaction)
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


    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     *  wrap InventoryConsumption for grid
     *
     * @Params obj -Receives map from execute which contains parent(InventoryConsumption) object
     *
     * @Return -a map containing all objects necessary for grid data
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GridEntity object = new GridEntity()

            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) receiveResult.get(INVENTORY_TRANSACTION)
            Object budget = receiveResult.get(BUDGET_OBJ)

            InvInventory invInventory = (InvInventory) invInventoryCacheUtility.read(invInventoryTransaction.inventoryId)
            SystemEntity invType = (SystemEntity) invInventoryTypeCacheUtility.read(invInventory.typeId)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(invInventoryTransaction.createdBy)

            object.id = invInventoryTransaction.id
            object.cell = [
                    Tools.LABEL_NEW,
                    invInventoryTransaction.id,
                    invType.key + Tools.COLON + invInventory.name,
                    budget.budgetItem,
                    invInventoryTransaction.itemCount,
                    Tools.STR_ZERO,
                    createdBy.username,
                    Tools.EMPTY_SPACE
            ]

            result.put(Tools.ENTITY, object)
            result.put(Tools.MESSAGE, SAVE_SUCCESS_MESSAGE)
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
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to create inventory-consumption
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
     *  Build the parent object (InvInventoryTransaction) of InventoryConsumption
     * @param parameterMap -Serialized parameters from UI
     * @param budget -Budget object
     * @param companyId -id of company
     * @return -InvInventoryTransaction object (InventoryConsumption)
     */
    private InvInventoryTransaction buildInventoryTransaction(GrailsParameterMap parameterMap, Object budget, long companyId) {
        InvInventoryTransaction invInventoryTransaction = new InvInventoryTransaction()

        SystemEntity transactionTypeConsumption = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)
        SystemEntity transactionEntityNone = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_NONE, companyId)

        long inventoryId = Long.parseLong(parameterMap.inventoryId.toString())
        InvInventory invInventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)

        invInventoryTransaction.version = 0
        invInventoryTransaction.transactionTypeId = transactionTypeConsumption.id
        invInventoryTransaction.transactionEntityTypeId = transactionEntityNone.id
        invInventoryTransaction.transactionEntityId = 0L
        invInventoryTransaction.transactionId = 0L
        invInventoryTransaction.projectId = invInventory.projectId
        invInventoryTransaction.createdOn = new Date()
        invInventoryTransaction.createdBy = invSessionUtil.appSessionUtil.getAppUser().id
        invInventoryTransaction.updatedOn = null
        invInventoryTransaction.updatedBy = 0L
        invInventoryTransaction.comments = parameterMap.comments ? parameterMap.comments : null
        invInventoryTransaction.inventoryTypeId = invInventory.typeId
        invInventoryTransaction.inventoryId = invInventory.id
        invInventoryTransaction.budgetId = budget.id
        invInventoryTransaction.itemCount = 0
        invInventoryTransaction.companyId = invSessionUtil.appSessionUtil.getAppUser().companyId
        invInventoryTransaction.transactionDate = new Date()

        return invInventoryTransaction
    }

    private static final String CONSUMPTION_COUNT_QUERY = """
        SELECT COUNT(id) count
        FROM inv_inventory_transaction
        WHERE inventory_id=:inventoryId AND
              budget_id=:budgetId AND
              transaction_type_id=:transactionTypeId AND
              transaction_entity_type_id=:transactionEntityTypeId
    """
    /**
     * Method returns countOfInvConsumption to check weather same consumption-parent is present or not
     * @param inventoryId -Inventory.id
     * @param budgetId -budget.id
     * @param companyId -id of company
     * @return int value
     */
    private int countInvConsumptionByInventoryAndBudget(long inventoryId, long budgetId, long companyId) {
        SystemEntity transactionTypeConsumption = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)
        SystemEntity transactionEntityNone = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_NONE, companyId)
        Map queryParams = [
                inventoryId: inventoryId,
                budgetId: budgetId,
                transactionTypeId: transactionTypeConsumption.id,
                transactionEntityTypeId: transactionEntityNone.id
        ]
        List resultCount = executeSelectSql(CONSUMPTION_COUNT_QUERY, queryParams)
        return resultCount[0].count
    }
}