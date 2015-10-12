package com.athena.mis.integration.budget.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgBudgetScope
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Get budget type object by budget type id, used in purchase request report
 * For details go through Use-Case doc named 'ReadBudgetTypeImplActionService'
 */
class ReadBudgetTypeImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass());

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get budget scope object by budget scope id
     * @param parameters -id of budget scope
     * @param obj -N/A
     * @return -object of budget scope
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        try {
            long budgetScopeId = Long.parseLong(parameters.toString())
            BudgBudgetScope budgetScope = BudgBudgetScope.read(budgetScopeId)
            return budgetScope
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build success result for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Do nothing for build failure result for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}
