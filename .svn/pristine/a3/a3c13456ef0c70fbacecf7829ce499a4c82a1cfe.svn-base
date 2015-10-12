package com.athena.mis.integration.budget.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgBudget
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Get budget object by budget id
 * For details go through Use-Case doc named 'ReadBudgetImplActionService'
 */
class ReadBudgetImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass());

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get budget object by budget id
     * @param parameters -id of budget
     * @param obj -N/A
     * @return -object of budget
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        try {
            long budgetId = Long.parseLong(parameters.toString())
            BudgBudget budget = BudgBudget.read(budgetId)
            return budget
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
