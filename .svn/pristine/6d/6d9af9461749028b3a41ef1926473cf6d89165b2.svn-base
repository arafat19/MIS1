package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select inventoryConsumption object (parent) and show in UI for editing
 *  For details go through Use-Case doc named 'SelectForInventoryConsumptionActionService'
 */
class SelectForInventoryConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private static final String INVENTORY_CONSUMPTION_NOT_FOUND_MESSAGE = "Inventory-Consumption Transaction not found on server"
    private static final String SERVER_ERROR_MESSAGE = "Can't get Inventory-Consumption Transaction Details"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String INV_INVENTORY_TRANSACTION = "invInventoryTransaction"
    private static final String BUDGET_ITEM = "budgetItem"
    private static final String PROJECT_NAME = "projectName"
    private static final String BUDGET_DETAILS = "budgetDetails"
    private static final String INVENTORY_LIST = "inventoryList"

    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get inventoryConsumption by id
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // check here for required params are present
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            long id = Long.parseLong(parameterMap.id.toString())
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(id)
            if (!invInventoryTransaction) {
                result.put(Tools.MESSAGE, INVENTORY_CONSUMPTION_NOT_FOUND_MESSAGE)
                return result
            }

            long inventoryTypeId = Long.parseLong(invInventoryTransaction.inventoryTypeId.toString())
            List<Object> inventoryList = invSessionUtil.getUserInventoriesByType(inventoryTypeId)

            result.put(INVENTORY_LIST, inventoryList)
            result.put(INV_INVENTORY_TRANSACTION, invInventoryTransaction)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) receiveResult.get(INV_INVENTORY_TRANSACTION)
            Object budget = budgetImplService.readBudget(invInventoryTransaction.budgetId)  //get budget object
            Project project = (Project) projectCacheUtility.read(budget.projectId)   //get project object

            result.put(BUDGET_ITEM, budget.budgetItem)
            result.put(PROJECT_NAME, project.name)
            result.put(BUDGET_DETAILS, budget.details)
            result.put(INVENTORY_LIST, receiveResult.get(INVENTORY_LIST))
            result.put(INV_INVENTORY_TRANSACTION, invInventoryTransaction)
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
                result.put(Tools.MESSAGE, INVENTORY_CONSUMPTION_NOT_FOUND_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }
}
