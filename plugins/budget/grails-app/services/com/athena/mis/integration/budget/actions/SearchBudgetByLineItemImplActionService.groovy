package com.athena.mis.integration.budget.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get list of budget by budget line item, used in purchase order search
 * For details go through Use-Case doc named 'SearchBudgetByLineItemImplActionService'
 */
class SearchBudgetByLineItemImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get list of budget by budget line item
     * @param parameters -budget line item
     * @param obj -N/A
     * @return -a list of budget
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        try {
            String budgetItem = parameters
            return searchByBudgetItem(budgetItem)
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return []
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

    /**
     * Get list of budget by budget line item
     * @param budgetItem -budget line item
     * @return -a list of budget
     */
    private List<BudgBudget> searchByBudgetItem(String budgetItem) {
        List<Long> projectIds = (List<Long>) budgSessionUtil.appSessionUtil.getUserProjectIds()
        List<BudgBudget> budgetList = BudgBudget.findAllByProjectIdInListAndBudgetItemIlike(projectIds, Tools.PERCENTAGE + budgetItem + Tools.PERCENTAGE, [readOnly: true])
        return budgetList
    }
}
