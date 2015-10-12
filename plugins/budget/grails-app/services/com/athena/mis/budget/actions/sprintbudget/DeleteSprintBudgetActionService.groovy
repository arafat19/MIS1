package com.athena.mis.budget.actions.sprintbudget

import com.athena.mis.budget.entity.BudgSprintBudget
import com.athena.mis.budget.service.BudgSprintBudgetService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

class DeleteSprintBudgetActionService {

    private final Logger log = Logger.getLogger(getClass())

    private static final String DELETE_SUCCESS_MESSAGE = "Budget has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Budget could not be deleted, Please refresh the Sprint's Budget"
    private static final String SPRINT_BUDGET_OBJ = "sprintBudget"
    private static final String ENTITY_NOT_FOUND = "Sprint's budget not found"
    private static final String DELETED = "deleted"

    BudgSprintBudgetService budgSprintBudgetService

    /**
     *
     * @param parameters - serialized parameters for UI
     * @param obj - N/A
     * @return - sprint budget object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long sprintBudgetId = Long.parseLong(params.id.toString())

            BudgSprintBudget sprintBudget = budgSprintBudgetService.read(sprintBudgetId)

            // Checking the existence of sprint's budget object
            if (!sprintBudget) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(SPRINT_BUDGET_OBJ, sprintBudget)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     *
     * @param params - N/A
     * @param obj - object from pre condition method
     * @return  - a map containing isError True/False
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            BudgSprintBudget sprintBudget = (BudgSprintBudget) preResult.get(SPRINT_BUDGET_OBJ)
            //delete sprint's budget
            budgSprintBudgetService.delete(sprintBudget.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Sprint\'s budget delete failed')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(DELETED, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }
}
