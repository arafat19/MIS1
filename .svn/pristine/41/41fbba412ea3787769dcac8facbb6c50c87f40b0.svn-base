package com.athena.mis.integration.budget.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgBudget
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class UpdateContentCountForBudgetImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional
    public Object execute(Object budgetIdObj, Object countIdObj) {
        try {
            long budgetId = Long.parseLong(budgetIdObj.toString())
            int count = Long.parseLong(countIdObj.toString())
            return updateForContentCount(budgetId, count)
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new Integer(0)
        }
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

    private static final String UPDATE_QUERY = """
        UPDATE budg_budget
        SET content_count = content_count + :contentCount,
            version = version + 1
        WHERE
            id=:id
    """


    /**
     * Update content count of budget
     * @param budgetId - BudgBudget.id
     * @param count - content count
     * @return - an integer object of update counter
     */
    private Integer updateForContentCount(long budgetId, int count) {
        Map queryParams = [
                contentCount: count,
                id: budgetId
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Content count updated for Budget")
        }
        return (new Integer(updateCount))
    }

}
