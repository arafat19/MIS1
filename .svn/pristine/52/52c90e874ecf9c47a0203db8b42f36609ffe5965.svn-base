package com.athena.mis.budget.actions.budgtask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Show UI for Budget task CRUD
 * For details go through Use-Case doc named 'ShowBudgTaskActionService'
 */
class ShowBudgTaskActionService extends BaseService implements ActionIntf {

    @Autowired
    BudgSessionUtil budgSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String BUDGET_ID = "budgetId"
    private static final String GRID_OBJECT = "gridObject"
    private static final String BUDGET_OBJECT = "budgetObject"
    private static final String BUDG_TASK_LIST = "budgTaskList"
    private static final String BUDGET_NOT_FOUND = "Budget not found"
    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load budget task page"

    /**
     * Check the existence of BudgBudget object by budget id from params
     * 1. Check the existence of BudgBudget object
     * @param parameter s- parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long budgetId = Long.parseLong(params.budgetId.toString())

            //Checking budget is exist or not by budgetId
            if (budgetId < 0) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            BudgBudget budget = BudgBudget.read(budgetId)
            //Checking the existence of budget object
            if (!budget) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            result.put(BUDGET_OBJECT, budget)
            result.put(BUDGET_ID, budgetId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
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
     * Give budget task list, budget object, count
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing budget task list, count of budgTaskList
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            long budgetId = Long.parseLong(preResult.get(BUDGET_ID).toString())
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)
            List<GroovyRowResult> budgTaskList = list(budgetId)
            int count = budgTaskList.size()
            result.put(BUDGET_ID, budgetId)
            result.put(BUDGET_OBJECT, preResult.get(BUDGET_OBJECT))
            result.put(BUDG_TASK_LIST, budgTaskList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return null
        }
    }

    /**
     * Wrap budget task list in grid entity
     * 1. Receive budget task list and it's count from execute method
     * @param obj - get a map from execute method with budget task list
     * @return - a wrapped map with budget task list, pageNumber & count
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            List<GroovyRowResult> budgTaskList = (List<GroovyRowResult>) executeResult.get(BUDG_TASK_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List wrappedBudgTaskList = wrapListInGridEntityList(budgTaskList, start)
            Map gridObject = [page: pageNumber, total: count, rows: wrappedBudgTaskList]

            result.put(BUDGET_OBJECT, executeResult.get(BUDGET_OBJECT))
            result.put(BUDGET_ID, executeResult.get(BUDGET_ID))
            result.put(GRID_OBJECT, gridObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Get list of budget task for grid
     * @param budgetId - BudgBudget.id
     * @return - a list of Budget task
     */
    private List list(long budgetId) {
        long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
        String strQuery = """
            SELECT task.id, task.name, TO_CHAR(task.start_date,'${DateUtility.dd_MM_yyyy_SLASH}') AS task_start_date,
                TO_CHAR(task.end_date,'${DateUtility.dd_MM_yyyy_SLASH}') AS task_end_date, se.key AS task_status
            FROM budg_task task
                LEFT JOIN system_entity se ON se.id = task.status_id
            WHERE task.company_id = :companyId
            AND task.budget_id = :budgetId
            ORDER BY task.name ASC
            LIMIT :resultPerPage OFFSET :start
        """
        Map queryParams = [
                budgetId: budgetId,
                companyId: companyId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstResult = executeSelectSql(strQuery, queryParams)
        return lstResult
    }

    /**
     * Wrap budget task list
     * @param budgTaskList - list of budget task
     * @param start - starting index of the page
     * @return - a list of wrapped budget task
     */
    private List wrapListInGridEntityList(List<GroovyRowResult> budgTaskList, int start) {
        List budgTasks = []
        int counter = start + 1
        for (int i = 0; i < budgTaskList.size(); i++) {
            GroovyRowResult budgTask = budgTaskList[i]
            GridEntity obj = new GridEntity()
            obj.id = budgTask.id
            obj.cell = [
                    counter,
                    budgTask.name,
                    budgTask.task_status,
                    budgTask.task_start_date,
                    budgTask.task_end_date
            ]
            budgTasks << obj
            counter++
        }
        return budgTasks

    }
}
