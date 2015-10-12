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
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update inventoryConsumption object
 *  For details go through Use-Case doc named 'UpdateForInventoryConsumptionActionService'
 */
class UpdateForInventoryConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility

    private static final String INVENTORY_CONSUMPTION_UPDATE_SUCCESS_MESSAGE = "Inventory-Consumption has been updated successfully"
    private static final String INVENTORY_CONSUMPTION_UPDATE_FAILURE_MESSAGE = "Fail to update Inventory-Consumption"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String INVENTORY_CONSUMPTION_OBJ = "invTransaction"
    private static final String OBJECT_NOT_FOUND = "Inventory-Consumption Transaction might be changed, Please refresh/reload the page"
    private static final String TRANSACTION_EXISTS = "This transaction already exists"
    private static final String BUDGET_NOT_FOUND = "Budget not found by the given budget item"
    private static final String BUDGET_OBJ = "budget"
    private static final String APPROVAL_COUNT = "approvalCount"

    /**
     * validate different criteria to update InventoryConsumption. Such as :
     *      Check existence of parameters
     *      Check existence of oldInvInventoryTransaction
     *      Check existence of budget
     *      Check approval
     *      Check existence of oldConsumption object
     *      Check existence of InvProductionLineItem Obj,
     *      Check availableAmount to consume etc.
     *
     * Create parent objects(InvTransaction) of InvTranConsumption & invTranProduction,
     * Create children objects(InvInventoryTransactionDetails) of TranDetailsConsumption & TranDetailsProduction,
     *
     * @Params parameters -Receives the serialized parameters send from UI e.g inventoryId, productionLineItemId, productionDate etc.
     * @Params obj -N/A
     *
     * @Return -a map containing all objects necessary for execute (Parents(invTranConsumption & invTranProduction), Children(invTranConsumptionDetails & invTranProductionDetails))
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            // check here for required params are present
            if ((!parameterMap.id) || (!parameterMap.version)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            // check inventoryIn Transaction Object existence
            long inventoryTransactionId = Long.parseLong(parameterMap.id.toString())
            int version = Integer.parseInt(parameterMap.version.toString())
            InvInventoryTransaction oldInvInventoryTransaction = invInventoryTransactionService.read(inventoryTransactionId)

            if ((!oldInvInventoryTransaction) || (oldInvInventoryTransaction.version != version)) {
                result.put(Tools.MESSAGE, OBJECT_NOT_FOUND)
                return result
            }

            Object budget = budgetImplService.readByBudgetItem(parameterMap.budgetItem.toString())
            if (!budget) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }

            // Build invTransactionConsumption Object
            InvInventoryTransaction invInventoryTransaction = buildInventoryOutForUpdate(parameterMap, oldInvInventoryTransaction, budget)

            //check weather same consumption-parent is present or not
            int existingInvInvTransactionCount = countInvConsumptionByInventoryAndBudgetForEdit(invInventoryTransaction.inventoryId, invInventoryTransaction.budgetId, invInventoryTransaction.id)
            if (existingInvInvTransactionCount > 0) {
                result.put(Tools.MESSAGE, TRANSACTION_EXISTS)
                return result
            }

            // checks input validation
            invInventoryTransaction.validate()
            if (invInventoryTransaction.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            result.put(BUDGET_OBJ, budget)
            result.put(INVENTORY_CONSUMPTION_OBJ, invInventoryTransaction)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INVENTORY_CONSUMPTION_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     *  Method to update InventoryConsumption (parent) object
     *
     * @Params parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains InventoryConsumption (parent) object
     *
     * @Return -map contains isError(true/false), InventoryConsumption (parent) object
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) preResult.get(INVENTORY_CONSUMPTION_OBJ)

            Long invTransactionId = invInventoryTransaction.id
            // get totalApprovedItemCount of inventory transaction to show on grid
            int approvalCount = countApprovedItem(invTransactionId)

            Integer updateStatus = invInventoryTransactionService.update(invInventoryTransaction)
            if (!updateStatus) {
                result.put(Tools.MESSAGE, INVENTORY_CONSUMPTION_UPDATE_FAILURE_MESSAGE)
                return result
            }

            result.put(BUDGET_OBJ, preResult.get(BUDGET_OBJ))
            result.put(INVENTORY_CONSUMPTION_OBJ, invInventoryTransaction)
            result.put(APPROVAL_COUNT, approvalCount)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INVENTORY_CONSUMPTION_UPDATE_FAILURE_MESSAGE)
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

            int approvalCount = (int) receiveResult.get(APPROVAL_COUNT)

            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) receiveResult.get(INVENTORY_CONSUMPTION_OBJ)
            Object budget = receiveResult.get(BUDGET_OBJ)

            InvInventory invInventory = (InvInventory) invInventoryCacheUtility.read(invInventoryTransaction.inventoryId)
            SystemEntity invType = (SystemEntity) invInventoryTypeCacheUtility.read(invInventory.typeId)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(invInventoryTransaction.createdBy)
            AppUser updatedBy = (AppUser) appUserCacheUtility.read(invInventoryTransaction.updatedBy)

            object.id = invInventoryTransaction.id
            object.cell = [Tools.LABEL_NEW,
                    invInventoryTransaction.id,
                    invType.key + Tools.COLON + invInventory.name,
                    budget.budgetItem,
                    invInventoryTransaction.itemCount,
                    approvalCount,
                    createdBy.username,
                    updatedBy ? updatedBy.username : Tools.EMPTY_SPACE
            ]

            result.put(Tools.MESSAGE, INVENTORY_CONSUMPTION_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INVENTORY_CONSUMPTION_UPDATE_FAILURE_MESSAGE)
            return result
        }

    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to update inventory-consumption
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, INVENTORY_CONSUMPTION_UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INVENTORY_CONSUMPTION_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     *  Build the parent object (InvInventoryTransaction) of InventoryConsumption
     * @Param params -Serialized parameters from UI
     * @Param budget -Budget object
     * @Return -InvInventoryTransaction object (InventoryConsumption)
     */
    private InvInventoryTransaction buildInventoryOutForUpdate(GrailsParameterMap parameter, InvInventoryTransaction oldInvTransaction, Object budget) {

        oldInvTransaction.updatedOn = new Date()
        oldInvTransaction.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
        oldInvTransaction.comments = parameter.comments ? parameter.comments : null
        if (oldInvTransaction.itemCount == 0) {
            long inventoryId = Long.parseLong(parameter.inventoryId.toString())
            InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
            oldInvTransaction.projectId = inventory.projectId
            oldInvTransaction.inventoryTypeId = inventory.typeId
            oldInvTransaction.inventoryId = inventoryId
            oldInvTransaction.budgetId = budget.id
        }
        return oldInvTransaction
    }

    private static final String INV_CONSUMP_INV_BUD_EDIT_QUERY = """
            SELECT COUNT(id) count
            FROM inv_inventory_transaction
            WHERE inventory_id=:inventoryId AND
                  budget_id=:budgetId AND
                  id <> :id AND
                  transaction_type_id=:transactionTypeId AND
                  transaction_entity_type_id=:transactionEntityTypeId
    """

    /**
     * Method returns countOfInvConsumption to check weather same consumption-parent is present or not
     * @Param inventoryId -Inventory.id
     * @Param budgetId -budget.id
     * @Param id -invInventoryTransaction.id(object which will be updated)
     *
     * @Return int value
     */
    private int countInvConsumptionByInventoryAndBudgetForEdit(long inventoryId, long budgetId, long id) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeConsumption = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)
        SystemEntity transactionEntityNone = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_NONE, companyId)

        Map queryParams = [
                id: id,
                transactionTypeId: transactionTypeConsumption.id,
                transactionEntityTypeId: transactionEntityNone.id,
                inventoryId: inventoryId,
                budgetId: budgetId
        ]
        List resultCount = executeSelectSql(INV_CONSUMP_INV_BUD_EDIT_QUERY, queryParams)
        return resultCount[0].count
    }

    private static final String APPROVED_ITEM_COUNT_QUERY = """
             SELECT COUNT(id) FROM inv_inventory_transaction_details
                WHERE inventory_transaction_id=:invTransactionId AND
                      approved_by > 0 AND
                      is_current = TRUE
    """

    /**
     * Method to get totalApprovedItemCount of inventory transaction
     * @Param invTransactionId -invInventoryTransaction.id(object which will be updated)
     * @Return int value
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