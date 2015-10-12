package com.athena.mis.budget.actions.sprintbudget

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.budget.entity.BudgSprint
import com.athena.mis.budget.service.BudgSprintService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 * Show Sprint Budget mapping in the grid.
 * For details go through Use-Case doc named 'ShowSprintBudgetActionService'
 */
class ShowSprintBudgetActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    BudgSprintService budgSprintService

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load sprint budget page"
    private static final String SPRINT_BUDGET_LIST = "sprintBudgetList"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred due to invalid input"
    private static final String SPRINT_NAME = "sprintName"
    private static final String SPRINT_ID = "sprintId"

    /**
     *
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.sprintId) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Get sprint budget list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(params)

            long sprintId = Long.parseLong(parameterMap.sprintId.toString())
            BudgSprint sprint = budgSprintService.read(sprintId)

            Map serviceReturn = listSprintBudget(sprintId)
            List<GroovyRowResult> sprintBudgetList = (List<GroovyRowResult>) serviceReturn.sprintBudgetList
            int count = (int) serviceReturn.count

            result.put(SPRINT_NAME, sprint.name)
            result.put(SPRINT_ID, sprint.id)
            result.put(SPRINT_BUDGET_LIST, sprintBudgetList)
            result.put(Tools.COUNT, count)
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
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Wrap sprint budget list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<GroovyRowResult> sprintBudgetList = (List<GroovyRowResult>) executeResult.get(SPRINT_BUDGET_LIST)
            int count = (int) executeResult.count
            List lstSprintBudget = wrapListInGridEntityList(sprintBudgetList, start)
            Map output = [page: pageNumber, total: count, rows: lstSprintBudget]
            result.put(SPRINT_ID, executeResult.get(SPRINT_ID))
            result.put(SPRINT_NAME, executeResult.get(SPRINT_NAME))
            result.put(SPRINT_BUDGET_LIST, output)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
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
            Map receiveResult = (Map) obj
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * wrap list of sprint budget in grid entity
     * @param lstSprint -list of sprint budget objects
     * @param start -starting index of the page
     * @return -list of wrapped sprint budget
     */
    private List wrapListInGridEntityList(List<GroovyRowResult> sprintBudgetList, int start) {
        List lstSprintBudget = [] as List
        int counter = start + 1

        for (int i = 0; i < sprintBudgetList.size(); i++) {
            GroovyRowResult sprintBudget = sprintBudgetList[i]
            GridEntity obj = new GridEntity()
            obj.id = sprintBudget.id
            obj.cell = [
                    counter,
                    sprintBudget.id,
                    sprintBudget.budget_item,
                    Tools.formatAmountWithoutCurrency(sprintBudget.quantity)+ Tools.SINGLE_SPACE + sprintBudget.unit
            ]
            lstSprintBudget << obj
            counter++
        }
        return lstSprintBudget
    }

    private static final String SELECT_QUERY = """
            SELECT sprint_budget.id AS id, budget.budget_item AS budget_item, sprint_budget.quantity AS quantity, unit.key AS unit
                FROM budg_sprint_budget sprint_budget
                LEFT JOIN budg_sprint sprint ON sprint.id= sprint_budget.sprint_id
                LEFT JOIN budg_budget budget ON budget.id= sprint_budget.budget_id
                LEFT JOIN project ON project.id=sprint.project_id
                LEFT JOIN system_entity unit ON unit.id = budget.unit_id
            WHERE sprint.id = :sprintId
            ORDER BY budget.budget_item
            LIMIT :resultPerPage OFFSET :start
    """

    private static final String COUNT_QUERY = """
            SELECT COUNT(sprint_budget.id)
                FROM budg_sprint_budget sprint_budget
                LEFT JOIN budg_sprint sprint ON sprint.id= sprint_budget.sprint_id
                LEFT JOIN budg_budget budget ON budget.id= sprint_budget.budget_id
                LEFT JOIN project ON project.id=sprint.project_id
            WHERE sprint.id = :sprintId
    """
    /**
     *
     * @param sprintId - sprint id
     * @return - a map containing lst of sprintBudget & count
     */
    private Map listSprintBudget(long sprintId) {
        Map queryParams = [
                sprintId: sprintId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstSprintBudget = executeSelectSql(SELECT_QUERY, queryParams)
        List<GroovyRowResult> resultCount = executeSelectSql(COUNT_QUERY, queryParams)
        int count = (int) resultCount[0][0]
        return [sprintBudgetList: lstSprintBudget, count: count]
    }
}
