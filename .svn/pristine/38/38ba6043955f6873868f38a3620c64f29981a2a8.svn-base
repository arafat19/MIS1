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
import org.springframework.transaction.annotation.Transactional

class ListTaskForBudgetSprintActionService extends BaseService implements ActionIntf {

    BudgSprintService budgSprintService

    private static final String SPRINT_ID = "sprintId"
    private static final String SPRINT_OBJ = "sprintObj"
    private static final String NOT_FOUND_MSG = "Sprint not found"
    private static final String LST_BUDGET_SPRINT = "lstBudgetSprint"
    private static final String FAILURE_MSG = "Fail to generate budget sprint report"
    private static final String BUDGET_SPRINT_NOT_FOUND = "Budget sprint not found"
    private static final String INVALID_SPRINT_ID = "Enter digits only"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. check required parameters
     * 2. check existence of sprint object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.sprintId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long sprintId = Long.parseLong(params.sprintId)

            BudgSprint sprint = budgSprintService.read(sprintId)
            if (!sprint) {
                result.put(Tools.MESSAGE, NOT_FOUND_MSG)
                return result
            }
            result.put(SPRINT_OBJ, sprint)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Get budget sprint list
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.rp) {
                parameterMap.rp = 20
                parameterMap.page = 1
            }
            initPager(parameterMap)
            BudgSprint sprint = (BudgSprint) preResult.get(SPRINT_OBJ)
            LinkedHashMap serviceReturn = getBudgetSprintList(sprint)
            List<GroovyRowResult> lstBudgetSprint = serviceReturn.lstBudgetSprint
            int count = serviceReturn.count
            if (count <= 0) {
                result.put(Tools.MESSAGE, BUDGET_SPRINT_NOT_FOUND)
                return result
            }
            result.put(LST_BUDGET_SPRINT, lstBudgetSprint)
            result.put(Tools.COUNT, count)
            result.put(SPRINT_ID, sprint.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
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
     * Wrap budget sprint list in grid entity
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List<GroovyRowResult> lstBudgetSprint = (List<GroovyRowResult>) executeResult.get(LST_BUDGET_SPRINT)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedBudgetSprint = wrapBudgetSprintList(lstBudgetSprint, start)
            Map gridOutput = [page: pageNumber, total: count, rows: lstWrappedBudgetSprint]
            result.put(LST_BUDGET_SPRINT, gridOutput)
            result.put(SPRINT_ID, executeResult.get(SPRINT_ID))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
            LinkedHashMap executeResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
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
}
