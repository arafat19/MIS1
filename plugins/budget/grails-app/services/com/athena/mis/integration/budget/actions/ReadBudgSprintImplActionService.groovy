package com.athena.mis.integration.budget.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgSprint
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class ReadBudgSprintImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

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
            long budgSprintId = Long.parseLong(parameters.toString())
            BudgSprint budgSprint = BudgSprint.read(budgSprintId)
            return budgSprint
        } catch (Exception ex) {
            log.error(ex.getMessage())
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
