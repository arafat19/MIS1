package com.athena.mis.budget.actions.budget

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.budget.model.BudgetProjectModel
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get Budget list by Inventory Ids
 * For details go through Use-Case doc named 'GetBudgetGridListByInventoryActionService'
 */
class GetBudgetGridListByInventoryActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String ERROR_MESSAGE = "Failed to load budget list"
    private static final String BUDGET_LIST = "budgetList"

    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get budget list from BudgetProjectModel using project ids from getUserProjectIdsByInventoryIds method
     * 1. Get list Inventory ids from getEntityIdsByType method of inventoryImplService.getUserInventoryIds()
     * 2. Get list of Project id from getUserProjectIdsByInventoryIds method using inventoryIds
     * @param parameters - parameters from UI
     * @param obj - N/A
     * @return - a map containing budget list, count & isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            initPager(parameterMap)
            List<Long> inventoryIds = inventoryImplService.getUserInventoryIds()
            List<Long> projectIds = getUserProjectIdsByInventoryIds(inventoryIds)
            List<BudgetProjectModel> budgetList = []
            int total = 0
            if (projectIds.size() > 0) {
                budgetList = BudgetProjectModel.findAllByProjectIdInList(projectIds, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                total = (int) BudgetProjectModel.countByProjectIdInList(projectIds)
            }

            result.put(BUDGET_LIST, budgetList)
            result.put(Tools.COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
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
     * Wrap Budget list in grid entity
     * 1. Get budget list and count from execute
     * @param obj - map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            List<BudgetProjectModel> budgetList = (List<BudgetProjectModel>) receiveResult.get(BUDGET_LIST)
            int count = (int) receiveResult.get(Tools.COUNT)
            List budgetListWrap = wrapGridEntityList(budgetList)
            result = [page: pageNumber, total: count, rows: budgetListWrap]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
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
                result.put(Tools.MESSAGE, ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap budget list in grid entity
     * @param budgetList -list of budget object(s)
     * @return -list of wrapped  budget
     */
    private List wrapGridEntityList(List<BudgetProjectModel> budgetList) {
        List budgets = []
        GridEntity obj
        for (int i = 0; i < budgetList.size(); i++) {
            obj = new GridEntity()
            obj.id = budgetList[i].budgetId
            obj.cell = [
                    budgetList[i].budgetId,
                    budgetList[i].budgetItem,
                    budgetList[i].budgetDetails,
                    budgetList[i].projectId,
                    budgetList[i].projectName
            ]
            budgets << obj
        }
        return budgets
    }

    /**
     * Give Project Ids those are associated with user by inventory ids
     * @param inventoryList - list of inventory
     * @return - unique project IDs
     */
    private List getUserProjectIdsByInventoryIds(List<Long> inventoryList) {
        List<Long> projectIds = []
        for (int i = 0; i < inventoryList.size(); i++) {
            Object inventory = inventoryImplService.readInventory(inventoryList[i])
            projectIds << (Long) inventory.projectId
        }
        return projectIds.unique()
    }

}
