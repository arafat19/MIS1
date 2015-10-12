package com.athena.mis.budget.actions.budgtask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Search budget task and show specific list of budget task
 * For details go through Use-Case doc named 'SearchBudgTaskActionService'
 */
class SearchBudgTaskActionService extends BaseService implements ActionIntf {

    @Autowired
    BudgSessionUtil budgSessionUtil

    protected final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load budget task List"
    private static final String LST_BUDG_TASK = "lstbudgTask"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get specific budget task list from DB
     * @param params - serialized params from UI
     * @param obj - N/A
     * @return - a map containing budgTaskList and count
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initSearch(parameterMap)                     // initialize parameters for flexGrid
            long budgetId = Long.parseLong(parameterMap.budgetId.toString())
            List<GroovyRowResult> lstBudgTask = search(budgetId)
            int count = lstBudgTask.size()
            result.put(LST_BUDG_TASK, lstBudgTask)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return null
        }
    }


    /**
     * Wrap budget taskList in Grid Entity
     * @param obj - get a map from execute method with budget task list
     * @return - a wrapped map with budget task list, pageNumber and count
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            List budgTaskList = (List) executeResult.get(LST_BUDG_TASK)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List wrappedSprint = wrapBudgTaskListGrid(budgTaskList, start)
            Map output = [page: pageNumber, total: count, rows: wrappedSprint]
            return output
        }
        catch (Exception ex) {
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Get list of budget task for grid
     * @param budgetId - BudgBudget.id
     * @return - a list of scope of work
     */
    private List search(long budgetId) {
        long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
        String str_query = """
              SELECT task.id, task.name, TO_CHAR(task.start_date,'${DateUtility.dd_MM_yyyy_SLASH}') AS task_start_date,
                TO_CHAR(task.end_date, '${DateUtility.dd_MM_yyyy_SLASH}') AS task_end_date, se.key AS task_status
            FROM budg_task task
                LEFT JOIN system_entity se ON se.id = task.status_id
            WHERE task.company_id = :companyId
            AND task.budget_id = :budgetId
            AND ${queryType} ILIKE :query
            ORDER BY task.name ASC
            LIMIT :resultPerPage OFFSET :start
        """

        Map queryParams = [
                budgetId: budgetId,
                companyId: companyId,
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> lstResult = executeSelectSql(str_query, queryParams)
        return lstResult
    }

    /**
     * Wrap budget task list
     * @param budgTaskList - list of budget task
     * @param start - starting index of the page
     * @return - a list of budget task
     */
    private List wrapBudgTaskListGrid(List<GroovyRowResult> budgTaskList, int start) {
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
