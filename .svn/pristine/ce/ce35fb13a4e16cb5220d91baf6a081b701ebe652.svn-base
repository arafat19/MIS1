package com.athena.mis.budget.actions.budgetscope

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgBudgetScope
import com.athena.mis.budget.entity.BudgProjectBudgetScope
import com.athena.mis.budget.service.BudgetScopeService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.budget.utility.BudgetScopeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Delete budget scope object from DB and budgetScopeCacheUtility
 * For details go through Use-Case doc named 'DeleteBudgetScopeActionService'
 */
class DeleteBudgetScopeActionService extends BaseService implements ActionIntf {
    BudgetScopeService budgetScopeService
    @Autowired
    BudgetScopeCacheUtility budgetScopeCacheUtility
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String DELETE_SUCCESS_MESSAGE = "Budget scope has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Budget scope could not be deleted, please refresh the list"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete budget scope"
    private static final String INVALID_INPUT = "Failed to delete budget scope due to invalid input"
    private static final String HAS_ASSOCIATION_PROJECT_BUDGET_SCOPE = " project scope mapping associated with selected budget scope"
    private static final String HAS_ASSOCIATION_BUDGET = " budget is associated with selected budget scope"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Checking pre condition and association before deleting the budget scope object
     * 1. Get budgetScope id from params
     * 2. Checking input validity by budgetScope id
     * 3. Check the access of director, project director and CFO
     * 4. Get budget scope object by budgetScope id
     * 5. Pull budget scope object from budgetScopeCacheUtility
     * 6. Check the existence of budget scope object
     * 7. Check association with budget using hasAssociation method
     * @param parameters -  serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

            // Has access check of director, project director and CFO
            boolean dir = budgSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_DIRECTOR)
            boolean pd = budgSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR)
            boolean cfo = budgSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_CFO)
            if ((!dir) && (!pd) && (!cfo)) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            long budgetScopeId = Long.parseLong(params.id.toString())
            BudgBudgetScope budgetScope = (BudgBudgetScope) budgetScopeCacheUtility.read(budgetScopeId)
            if (!budgetScope) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            Map preResult = (Map) hasAssociation(budgetScope)
            Boolean hasAssociation = (Boolean) preResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Delete budget scope object from DB and budgetScopeCacheUtility
     * 1. This function is in transactional boundary and will roll back in case of any exception
     * 2. Delete budget scope from DB by using delete method of budgetScopeService
     * 3. Delete budget scope from budgetScopeCacheUtility using delete method
     * @param parameters - parameters from UI
     * @param obj - N/A
     * @return - a boolean value of result sql
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long budgetScopeId = Long.parseLong(parameterMap.id.toString())
            Boolean resultSql = (Boolean) budgetScopeService.delete(budgetScopeId)
            budgetScopeCacheUtility.delete(budgetScopeId)
            return resultSql
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Budget Scope delete failed')
            return Boolean.FALSE
        }
    }

    /**
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put("deleted", Boolean.TRUE)
        result.put(Tools.MESSAGE, DELETE_SUCCESS_MESSAGE)
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

    /**
     * Check association with budget and project budget scope
     * @param budgetScope - object of budgetScope from executePreCondition method
     * @return - a map of hasAssociation(TRUE/FALSE)
     */
    @Transactional(readOnly = true)
    public LinkedHashMap hasAssociation(BudgBudgetScope budgetScope) {
        LinkedHashMap result = new LinkedHashMap()
        long budgetScopeId = budgetScope.id
        int count = 0
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        count = BudgProjectBudgetScope.countByBudgetScopeId(budgetScopeId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_PROJECT_BUDGET_SCOPE)
            return result
        }

        count = BudgBudget.countByBudgetScopeId(budgetScopeId.intValue())
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_BUDGET)
            return result
        }
        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }
}
