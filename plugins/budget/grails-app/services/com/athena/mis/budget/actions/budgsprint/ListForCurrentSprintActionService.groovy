package com.athena.mis.budget.actions.budgsprint

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Get list of current sprints of projects for grid
 *  For details go through Use-Case doc named 'ListForCurrentSprintActionService'
 */
class ListForCurrentSprintActionService extends BaseService implements ActionIntf {

    @Autowired
    BudgSessionUtil budgSessionUtil

    protected final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Could not load current sprint list"
    private static final String LST_CURRENT_SPRINT = "lstCurrentSprint"

    /**
     * Do nothing for executePreCondition operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get list of current sprint
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initPager(params)
            resultPerPage = 25
            Map serviceReturn = listOfCurrentSprint()
            List<GroovyRowResult> lstCurrentSprint = serviceReturn.lstCurrentSprint
            int count = serviceReturn.count
            result.put(LST_CURRENT_SPRINT, lstCurrentSprint)
            result.put(Tools.COUNT, count)
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
     * Do nothing for executePostCondition operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap current sprint list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List<GroovyRowResult> lstCurrentSprint = (List<GroovyRowResult>) executeResult.get(LST_CURRENT_SPRINT)
            List lstWrappedCurrentSprint = wrapCurrentSprintList(lstCurrentSprint, start)
            int count = (int) executeResult.get(Tools.COUNT)
            Map gridOutput = [page: pageNumber, total: count, rows: lstWrappedCurrentSprint]
            result.put(LST_CURRENT_SPRINT, gridOutput)
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
     * @param obj -map returned from previous method, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap returnResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (returnResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, returnResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Wrap current sprint list for grid
     * @param lstCurrentSprint -list of current sprint
     * @param start -start index
     * @return -wrapped list of current sprint
     */
    private List wrapCurrentSprintList(List<GroovyRowResult> lstCurrentSprint, int start) {
        List lstWrappedCurrentSprint = []
        int counter = start + 1
        GridEntity obj
        GroovyRowResult singleRow
        for (int i = 0; i < lstCurrentSprint.size(); i++) {
            singleRow = lstCurrentSprint[i]
            obj = new GridEntity()
            obj.id = singleRow.id
            obj.cell = [
                    counter,
                    singleRow.id,
                    singleRow.project_name,
                    singleRow.sprint_name,
                    singleRow.task_count
            ]
            lstWrappedCurrentSprint << obj
            counter++
        }
        return lstWrappedCurrentSprint
    }

    private static final String LIST_QUERY = """
        SELECT sprint.id, project.name AS project_name, sprint.name AS sprint_name, COUNT(task.id) AS task_count
        FROM budg_sprint sprint
        LEFT JOIN project ON project.id = sprint.project_id
        LEFT JOIN budg_sprint_budget sb ON sb.sprint_id = sprint.id
        LEFT JOIN budg_budget budget ON budget.id = sb.budget_id
        LEFT JOIN budg_task task ON task.budget_id = budget.id
        WHERE sprint.is_active = true
        AND sprint.end_date >= task.start_date AND task.end_date >= sprint.start_date
        AND sprint.company_id = :companyId
        GROUP BY sprint.id, project.name, sprint.name
        ORDER BY project.name
        LIMIT :resultPerPage OFFSET :start
    """

    private static final String COUNT_QUERY = """
        SELECT COUNT(DISTINCT(sprint.id))
        FROM budg_sprint sprint
        LEFT JOIN budg_sprint_budget sb ON sb.sprint_id = sprint.id
        LEFT JOIN budg_budget budget ON budget.id = sb.budget_id
        WHERE sprint.is_active = true
        AND budget.task_count > 0
        AND sprint.company_id = :companyId
    """

    /**
     * Get list and count of current sprint
     * @return -a map containing list and count of current sprint
     */
    private Map listOfCurrentSprint() {
        long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
        Map queryParams = [
                companyId: companyId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstCurrentSprint = executeSelectSql(LIST_QUERY, queryParams)
        List countResults = executeSelectSql(COUNT_QUERY, queryParams)
        int count = countResults[0].count
        return [lstCurrentSprint: lstCurrentSprint, count: count]
    }
}
