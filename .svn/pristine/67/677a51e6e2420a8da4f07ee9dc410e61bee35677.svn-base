package com.athena.mis.integration.budget.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgBudgetDetails
import com.athena.mis.budget.model.BudgetProjectItemModel
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Get budget details object by budget details id, used in purchase request details
 * For details go through Use-Case doc named 'ReadBudgetDetailsImplActionService'
 */
class ReadBudgetDetailsImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass());

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get budget details object by budget details id
     * @param item -itemId as object
     * @param project - projectId as object
     * @return -object of budget details
     */
    @Transactional(readOnly = true)
    public Object execute(Object project, Object item) {
        try {
            long projectId = Long.parseLong(project.toString())
            long itemId = Long.parseLong(item.toString())
            BudgetProjectItemModel budgetDetails = BudgetProjectItemModel.findByProjectIdAndItemId(projectId, itemId, [readOnly: true])
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
     * Do nothing for build failure result for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}
