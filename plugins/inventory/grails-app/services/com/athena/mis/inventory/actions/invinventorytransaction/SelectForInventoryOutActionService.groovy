package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvInventoryTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select inventory out transaction object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectForInventoryOutActionService'
 */
class SelectForInventoryOutActionService extends BaseService implements ActionIntf {

    private static final String INV_OUT_NOT_FOUND_MESSAGE = "Inventory transaction not found"
    private static final String SERVER_ERROR_MESSAGE = "Fail to get Inventory-Out-Transaction"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred for invalid input"
    private static final String INV_OUT_OBJ = "invInventoryTransaction"
    private static final String BUDGET_ITEM = "budgetItem"
    private static final String TRANSACTION_DATE = "transactionDate"
    private static final String INVENTORY_OUT_MAP = "inventoryOutMap"

    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    /**
     * Get inventory transaction out object by id
     * @param params -parameters from UI
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

            // check required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            long id = Long.parseLong(parameterMap.id.toString())
            // get inventory transaction out object
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(id)
            // check if inventory transaction object exists or not
            if (!invInventoryTransaction) {
                result.put(Tools.MESSAGE, INV_OUT_NOT_FOUND_MESSAGE)
                return result
            }

            result.put(INV_OUT_OBJ, invInventoryTransaction)
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
     * Build a map with inventory transaction object and other related properties to show on UI
     * @param parameters - N/A
     * @param obj -map returned from executePrecondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) preResult.get(INV_OUT_OBJ)
            Map inventoryMap = buildInvMap(invInventoryTransaction) // build map with necessary properties to show on UI

            result.put(BUDGET_ITEM, Tools.EMPTY_SPACE)
            if (invInventoryTransaction.budgetId > 0) {
                Object budget = budgetImplService.readBudget(invInventoryTransaction.budgetId)
                result.put(BUDGET_ITEM, budget.budgetItem)
            }
            String transactionDate = DateUtility.getDateForUI(invInventoryTransaction.transactionDate)
            result.put(TRANSACTION_DATE, transactionDate)
            result.put(INV_OUT_OBJ, invInventoryTransaction)
            result.put(INVENTORY_OUT_MAP, inventoryMap)
            result.put(Tools.VERSION, invInventoryTransaction.version)
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
     * Build a map with necessary objects to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.ENTITY, executeResult.get(INV_OUT_OBJ))
            result.put(INVENTORY_OUT_MAP, executeResult.get(INVENTORY_OUT_MAP))
            result.put(BUDGET_ITEM, executeResult.get(BUDGET_ITEM))
            result.put(TRANSACTION_DATE, executeResult.get(TRANSACTION_DATE))
            result.put(Tools.VERSION, executeResult.get(Tools.VERSION))
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
                result.put(Tools.MESSAGE, INV_OUT_NOT_FOUND_MESSAGE)
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
     * Build a map with necessary properties of inventory transaction object
     * @param invInventoryTransaction -inventory out transaction object
     * @return -a map containing necessary properties to show on UI
     */
    private Map buildInvMap(InvInventoryTransaction invInventoryTransaction) {
        InvInventory fromInventory = (InvInventory) invInventoryCacheUtility.read(invInventoryTransaction.inventoryId)
        InvInventory toInventory = (InvInventory) invInventoryCacheUtility.read(invInventoryTransaction.transactionEntityId)
        Map transactionMap = [
                fromInventoryTypeId: fromInventory.typeId,
                toInventoryTypeId: toInventory.typeId,
                invnetoryList: invSessionUtil.getUserInventoriesByType(fromInventory.typeId),
                transactionEntityList: invInventoryCacheUtility.listByType(toInventory.typeId)
        ]
        return transactionMap
    }
}
