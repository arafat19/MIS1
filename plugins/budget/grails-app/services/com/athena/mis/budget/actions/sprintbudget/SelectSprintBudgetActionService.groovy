package com.athena.mis.budget.actions.sprintbudget

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgSprintBudget
import com.athena.mis.budget.service.BudgSprintBudgetService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Select sprint budget object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectSprintBudgetActionService'
 */
class SelectSprintBudgetActionService extends BaseService implements ActionIntf{
    private final Logger log = Logger.getLogger(getClass())

    private static final String SPRINT_BUDGET_NOT_FOUND_MESSAGE = "Sprint's budget not found"
    private static final String SERVER_ERROR_MESSAGE = "Internal Server Error"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred for invalid input"
    private static final String SPRINT_BUDGET_MAP = "sprintBudget"

    BudgSprintBudgetService budgSprintBudgetService

    /**
     * do nothing for post operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * 1. check required parameters
     * 2. get sprint budget object by id
     * 3. check if sprint budget object exists or not
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
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

            BudgSprintBudget sprintBudget = budgSprintBudgetService.read(id)
            if (!sprintBudget) {
                result.put(Tools.MESSAGE, SPRINT_BUDGET_NOT_FOUND_MESSAGE)
                return result
            }
            Map sprintBudgetObject = buildSprintBudgetMap(sprintBudget)
            result.put(SPRINT_BUDGET_MAP, sprintBudgetObject)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     *
     * @param obj - sprint budget object
     * @return - a map containing sprint budget object & isError True/False
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(SPRINT_BUDGET_MAP, receiveResult.get(SPRINT_BUDGET_MAP))
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
                result.put(Tools.MESSAGE, SPRINT_BUDGET_NOT_FOUND_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    private static final String SELECT_BUDGET_QUERY = """
         SELECT sprint_budget.id AS id,sprint_budget.version AS version,budget.id AS budget_id , budget.budget_item AS budget_item ,
                unit.key AS unit,budget.details AS details, sprint_budget.quantity AS curr_quantity,
               ((budget.budget_quantity-coalesce((SELECT SUM(quantity) FROM budg_sprint_budget WHERE budget_id = :budgetId),0)) + sprint_budget.quantity) AS quantity
            FROM budg_sprint_budget sprint_budget
            LEFT JOIN budg_budget budget ON budget.id = sprint_budget.budget_id
            LEFT JOIN system_entity unit ON unit.id = budget.unit_id
            WHERE budget.id = :budgetId
            AND sprint_budget.sprint_id = :sprintId
            GROUP BY budget.id,budget.budget_item,budget.budget_quantity,budget.details,unit.key,
                     sprint_budget.quantity,sprint_budget.id,sprint_budget.version
    """
    /**
     *
     * @param budgetId - budget id
     * @param sprintId - sprint id
     * @return - lst of budget
     */
    private Map buildSprintBudgetMap(BudgSprintBudget budgSprintBudget) {

        Map queryParams = [
                budgetId: budgSprintBudget.budgetId,
                sprintId: budgSprintBudget.sprintId
        ]
        List<GroovyRowResult> lstSprintBudget = executeSelectSql(SELECT_BUDGET_QUERY, queryParams)
        Map sprintBudget = [
                id: lstSprintBudget[0].id,
                version: lstSprintBudget[0].version,
                budgetId: lstSprintBudget[0].budget_id,
                currQuantity: lstSprintBudget[0].curr_quantity,
                budgetItem:lstSprintBudget[0].budget_item,
                unit:lstSprintBudget[0].unit,
                quantity:lstSprintBudget[0].quantity,
                details: lstSprintBudget[0].details
        ]
        return sprintBudget
    }
}
