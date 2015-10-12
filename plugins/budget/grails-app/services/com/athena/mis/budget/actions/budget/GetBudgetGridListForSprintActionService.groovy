package com.athena.mis.budget.actions.budget

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.budget.entity.BudgSprint
import com.athena.mis.budget.model.BudgetProjectModel
import com.athena.mis.budget.service.BudgSprintService
import com.athena.mis.budget.service.BudgetService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
/**
 * Get Budget List By Project Ids From BudgetProjectModel
 * For details go through Use-Case doc named 'GetBudgetGridListForSprintActionService'
 */
class GetBudgetGridListForSprintActionService  extends BaseService implements ActionIntf {

    BudgSprintService budgSprintService
    BudgetService budgetService
    @Autowired
    UnitCacheUtility unitCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String ERROR_MESSAGE = "Failed to load budget list"
    private static final String BUDGET_LIST = "budgetList"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get budget list from BudgetProjectModel
     * 1. Get list of project id from getEntityIdsByType method of budgSessionUtil
     * 2. Get budget list form BudgetProjectModel by list of project ids
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
            long sprintId = Long.parseLong(parameterMap.sprintId.toString())
            BudgSprint sprintObj = budgSprintService.read(sprintId)
            Map serviceReturn = listOfBudget(sprintObj)
            List<GroovyRowResult> budgetList = (List<GroovyRowResult>) serviceReturn.budgetList
            int count = (int) serviceReturn.count

            result.put(BUDGET_LIST, budgetList)
            result.put(Tools.COUNT, count)
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
     * 1. Get Budget list & count from execute
     * @param obj - map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            List<GroovyRowResult> budgetList = (List<GroovyRowResult>) receiveResult.get(BUDGET_LIST)
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

    private static final String SELECT_QUERY = """
            SELECT budget.id AS id, budget.budget_item AS budget_item,budget.details AS details,entity.key AS unit,
                  (budget.budget_quantity-coalesce(SUM(sprint_budget.quantity),0)) AS quantity
            FROM budg_budget budget
            LEFT JOIN budg_sprint_budget sprint_budget ON sprint_budget.budget_id = budget.id
            LEFT JOIN system_entity entity ON entity.id = budget.unit_id
            WHERE budget.project_id = :projectId
            AND :endDate >= budget.start_date AND budget.end_date >= :startDate
            GROUP BY budget.id,budget.budget_item,budget.budget_quantity,budget.details,entity.key
            ORDER BY budget.budget_item ASC
            LIMIT :resultPerPage OFFSET :start

    """

    private static final String COUNT_QUERY = """
            SELECT COUNT(DISTINCT(budget.id))
            FROM budg_budget budget
            LEFT JOIN budg_sprint_budget sprint_budget ON sprint_budget.budget_id = budget.id
            WHERE budget.project_id = :projectId
            AND :endDate >= budget.start_date AND budget.end_date >= :startDate
    """
    /**
     *
     * @param sprint - BudgSprint.id
     * @return - lst of budget
     */
    private Map listOfBudget(BudgSprint sprint) {
        Map queryParams = [
                sprintId: sprint.id,
                projectId: sprint.projectId,
                startDate: DateUtility.getSqlDate(sprint.startDate),
                endDate: DateUtility.getSqlDate(sprint.endDate),
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstBudget = executeSelectSql(SELECT_QUERY, queryParams)
        List<GroovyRowResult> total = executeSelectSql(COUNT_QUERY, queryParams)
        int count = (int) total[0][0]
        return [budgetList: lstBudget, count: count]
    }
}
