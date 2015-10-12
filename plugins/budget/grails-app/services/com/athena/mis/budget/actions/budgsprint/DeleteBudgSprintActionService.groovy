package com.athena.mis.budget.actions.budgsprint

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgSprint
import com.athena.mis.budget.service.BudgSprintBudgetService
import com.athena.mis.budget.service.BudgSprintService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete sprint object from DB  and remove it from grid
 *  For details go through Use-Case doc named 'DeleteBudgSprintActionService'
 */
class DeleteBudgSprintActionService extends BaseService implements ActionIntf {

    BudgSprintService budgSprintService
    BudgSprintBudgetService budgSprintBudgetService

    private final Logger log = Logger.getLogger(getClass())

    private static final String SPRINT_DELETE_SUCCESS_MSG = "Sprint has been deleted successfully"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete sprint"
    private static final String BUDGET_SPRINT_OBJ = "budgSprintObj"
    private static final String SPRINT_NOT_FOUND = "Selected sprint not found"
    private static final String DELETED = "deleted"
    private static final String HAS_BUDGET = " budget(s) are associated with this sprint"

    /**
     * 1. check required parameters
     * 2. check if sprint object exists or not
     * 3. check if sprint has any budget or not
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            // check required parameters
            if (!params.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long sprintId = Long.parseLong(params.id.toString())
            BudgSprint sprint = (BudgSprint) budgSprintService.read(sprintId)
            // check if sprint object exists or not
            if (!sprint) {
                result.put(Tools.MESSAGE, SPRINT_NOT_FOUND)
                return result
            }
            int count = budgSprintBudgetService.countBySprintId(sprintId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count + HAS_BUDGET)
                return result
            }
            result.put(BUDGET_SPRINT_OBJ, sprint)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Delete sprint object from DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executePreResult = (LinkedHashMap) obj
            BudgSprint sprint = (BudgSprint) executePreResult.get(BUDGET_SPRINT_OBJ)
            budgSprintService.delete(sprint.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(Tools.MESSAGE, SPRINT_DELETE_SUCCESS_MSG)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        result.put(DELETED, Boolean.TRUE)
        return result
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
                if (preResult.get(Tools.MESSAGE)) {
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
}
