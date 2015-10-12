package com.athena.mis.budget.actions.budget

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

/**
 * Search Budget By Inventory
 * For details go through Use-Case doc named 'SearchBudgetGridByInventoryActionService'
 */
class SearchBudgetGridListForSprintActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String ERROR_MESSAGE = "Failed to load budget list"
    private static final String BUDGET_LIST = "budgetList"

    BudgSprintService budgSprintService

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     *
     * @param parameters
     * @param obj
     * @return
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            initSearch(parameterMap)
            long sprintId = Long.parseLong(parameterMap.sprintId.toString())
            BudgSprint sprintObj = budgSprintService.read(sprintId)
            List<GroovyRowResult> lstSprintBudget = search(sprintObj)
            int total = lstSprintBudget.size()

            result.put(BUDGET_LIST, lstSprintBudget)
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
     * Wrap budget list for grid
     * 1. Get budget list from execute
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(Tools.COUNT)
            List<GroovyRowResult> budgetList = (List<GroovyRowResult>) receiveResult.get(BUDGET_LIST)
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
     *
     * @param budgetList
     * @return
     */
    private List wrapGridEntityList(List<GroovyRowResult> budgetList) {
        List lstSprintBudget = [] as List
        int counter = start + 1

        for (int i = 0; i < budgetList.size(); i++) {
            GroovyRowResult budget = budgetList[i]
            GridEntity obj = new GridEntity()
            obj.id = budget.id
            obj.cell = [
                    counter,
                    budget.id,
                    budget.budget_item,
                    budget.details,
                    Tools.formatAmountWithoutCurrency(budget.quantity) + Tools.SINGLE_SPACE + budget.unit
            ]
            lstSprintBudget << obj
            counter++
        }
        return lstSprintBudget
    }

    /**
     *
     * @param sprint - BudgSprint.id
     * @return - lst of budget
     */
    private List search(BudgSprint sprint) {
        String str_query = """
            SELECT budget.id AS id, budget.budget_item AS budget_item,budget.details AS details,entity.key AS unit,
                  (budget.budget_quantity-coalesce(SUM(sprint_budget.quantity),0)) AS quantity
            FROM budg_budget budget
            LEFT JOIN budg_sprint_budget sprint_budget ON sprint_budget.budget_id = budget.id
            LEFT JOIN system_entity entity ON entity.id = budget.unit_id
            WHERE budget.project_id = :projectId
            AND :endDate >= budget.start_date AND budget.end_date >= :startDate
            AND (${queryType} ILIKE :query
               OR budget.details ILIKE :query)
            GROUP BY budget.id,budget.budget_item,budget.budget_quantity,budget.details,entity.key
            ORDER BY budget.budget_item ASC
            LIMIT :resultPerPage OFFSET :start

    """
        Map queryParams = [
                sprintId: sprint.id,
                projectId: sprint.projectId,
                startDate: DateUtility.getSqlDate(sprint.startDate),
                endDate: DateUtility.getSqlDate(sprint.endDate),
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> lstResult = executeSelectSql(str_query, queryParams)
        return lstResult
    }
}
