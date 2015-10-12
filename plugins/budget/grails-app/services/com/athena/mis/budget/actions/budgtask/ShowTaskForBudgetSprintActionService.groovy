package com.athena.mis.budget.actions.budgtask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.budget.entity.BudgSprint
import com.athena.mis.budget.service.BudgSprintService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class ShowTaskForBudgetSprintActionService extends BaseService implements ActionIntf {
    BudgSprintService budgSprintService

    private static final String FAILURE_MSG = "Fail to load source ledger"
    private static final String LEDGER_LIST_WRAP = "lstBudgetSprint"
    private static final String NOT_FOUND_MSG = "Sprint not found"
    private static final String BUDGET_SPRINT_NOT_FOUND = "Budget sprint not found"
    private static final String SPRINT_ID = "sprintId"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Make a map with required information to show on UI
     * If navigated form another report then-
     * 1. Get all required information to show on UI
     * 2. Get source ledger list for grid
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all necessary objects
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (params.sprintId) {
                if (!params.rp) {
                    params.rp = 20
                    params.page = 1
                }
                initPager(params)
                long sprintId = Long.parseLong(params.sprintId.toString())
                result.put(SPRINT_ID, sprintId)
                BudgSprint sprint = budgSprintService.read(sprintId)
                if (!sprint) {
                    result.put(Tools.MESSAGE, NOT_FOUND_MSG)
                    return result
                }
                LinkedHashMap serviceReturn = getBudgetSprintList(sprint)
                List<GroovyRowResult> lstBudgetSprint = serviceReturn.lstBudgetSprint
                int count = serviceReturn.count
                if (count <= 0) {
                    result.put(Tools.MESSAGE, BUDGET_SPRINT_NOT_FOUND)
                    return result
                }
                List lstWrappedBudgetSprint = wrapBudgetSprintList(lstBudgetSprint, start)
                Map gridOutput = [page: pageNumber, total: count, rows: lstWrappedBudgetSprint]
                result.put(LEDGER_LIST_WRAP, gridOutput)
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
     * Do nothing for build success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Do nothing for build failure result
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Get budget sprint list and count
     * @param sprint -object of BudgSprint
     * @return -a map of budget sprint and count
     */
    private LinkedHashMap getBudgetSprintList(BudgSprint sprint) {
        String queryStr = """
            SELECT budget.budget_item AS budget_lime_item, task.name AS task_name, task.start_date, task.end_date,
            status.key AS status, task.id
            FROM budg_sprint_budget sb
            LEFT JOIN budg_budget budget on budget.id = sb.budget_id
            LEFT JOIN budg_task task on task.budget_id = budget.id
            LEFT JOIN system_entity status on status.id = task.status_id
            WHERE sb.sprint_id = :sprintId
            AND :endDate >= task.start_date AND task.end_date >= :startDate
            ORDER BY budget.budget_item, task.start_date
            LIMIT :resultPerPage OFFSET :start;
        """
        Map queryParams = [
                sprintId: sprint.id,
                startDate: DateUtility.getSqlDate(sprint.startDate),
                endDate: DateUtility.getSqlDate(sprint.endDate),
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstBudgetSprint = executeSelectSql(queryStr, queryParams)

        String queryCount = """
            SELECT COUNT(task.id)
            FROM budg_sprint_budget sb
            LEFT JOIN budg_budget budget on budget.id = sb.budget_id
            LEFT JOIN budg_task task on task.budget_id = budget.id
            WHERE sb.sprint_id = :sprintId
            AND :endDate >= task.start_date AND task.end_date >= :startDate;
        """
        List countResults = executeSelectSql(queryCount, queryParams)
        int count = countResults[0].count
        return [lstBudgetSprint: lstBudgetSprint, count: count]
    }

    /**
     * Wrap budget sprint list in grid entity
     * @param lstBudgetSprint -budget sprint
     * @param start -starting index of the page
     * @return -list of wrapped budget sprint
     */
    private List wrapBudgetSprintList(List<GroovyRowResult> lstBudgetSprint, int start) {
        List lstWrappedBudgetSprint = []
        int counter = start + 1
        GroovyRowResult budgetSprint
        GridEntity obj

        for (int i = 0; i < lstBudgetSprint.size(); i++) {
            budgetSprint = lstBudgetSprint[i]
            obj = new GridEntity()
            obj.id = budgetSprint.id
            obj.cell = [
                    counter,
                    budgetSprint.budget_lime_item,
                    budgetSprint.task_name,
                    DateUtility.getLongDateForUI(budgetSprint.start_date),
                    DateUtility.getLongDateForUI(budgetSprint.end_date),
                    budgetSprint.status
            ]
            lstWrappedBudgetSprint << obj
            counter++
        }
        return lstWrappedBudgetSprint
    }
}

