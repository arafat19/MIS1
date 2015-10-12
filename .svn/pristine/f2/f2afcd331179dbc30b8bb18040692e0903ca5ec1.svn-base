package com.athena.mis.integration.budget.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgBudgetDetails
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Get budget details object by budget id and item id, used in inventory consumption
 * For details go through Use-Case doc named 'ReadBudgetDetailsByBudgetAndItemImplActionService'
 */
class ReadBudgetDetailsByBudgetAndItemImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass());

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get budget details object by budget id and item id
     * @param budgetIdObj -id of budget object
     * @param itemIdObj -id of item object
     * @return -object of budget details
     */
    @Transactional(readOnly = true)
    public Object execute(Object budgetIdObj, Object itemIdObj) {
        try {
            long budgetId = Long.parseLong(budgetIdObj.toString())
            long itemId = Long.parseLong(itemIdObj.toString())
            BudgBudgetDetails budgetDetails = BudgBudgetDetails.findByBudgetIdAndItemId(budgetId, itemId, [readOnly: true])
            return budgetDetails
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
     * Do nothing for build success failure for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}
