package com.athena.mis.integration.qsmeasurement.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.qs.entity.QsMeasurement
import org.apache.log4j.Logger

/**
 * Get sum of QsMeasurement by budget id, used in update budget
 * For details go through Use-Case doc named 'GetQsSumOfBudgetImplActionService'
 */
class GetQsSumOfBudgetImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass());

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get sum of QsMeasurement by budget id
     * @param parameters -id of budget
     * @param obj -true/false
     * @return -sum of QsMeasurement quantity
     */
    public Object execute(Object parameters, Object obj) {
        try {
            long budgetId = Long.parseLong(parameters.toString())
            boolean isGovt = Boolean.parseBoolean(obj.toString())
            double totalSumOfBudget = (double) QsMeasurement.getQsSumOfBudget(budgetId, isGovt)
            return totalSumOfBudget
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return 0
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