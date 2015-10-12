package com.athena.mis.budget.actions.projectbudgetscope

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgBudgetScope
import com.athena.mis.budget.entity.BudgProjectBudgetScope
import com.athena.mis.budget.utility.BudgetScopeCacheUtility
import com.athena.mis.budget.utility.ProjectBudgetScopeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Get Project Budget Scope mapping
 * For details go through Use-Case doc named 'GetProjectBudgetScopeActionService'
 */
class GetProjectBudgetScopeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Can't select project budget scope"
    private static final String BUDGET_SCOPE_LIST = "budgetScopeList"
    private static final String NO_BUDGET_SCOPE = "No budget scope to this project"

    @Autowired
    BudgetScopeCacheUtility budgetScopeCacheUtility
    @Autowired
    ProjectBudgetScopeCacheUtility projectBudgetScopeCacheUtility

    /**
     * do nothing for post operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get Budget type list
     * 1. Pull project budget scope list from projectBudgetScopeCacheUtility
     * 2. Get budget scope ids from project budget type list
     * 3. Get budget scope list from budgetScopeCacheUtility
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map of budget type list & isError(True/False)
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long projectId = Long.parseLong(params.projectId.toString())

            List<BudgProjectBudgetScope> projectBudgetScopeList = (List) projectBudgetScopeCacheUtility.list()
            List<Long> budgetScopeIds = []

            if (projectBudgetScopeList.size() > 0) {
                for (int i = 0; i < projectBudgetScopeList.size(); i++) {
                    if (projectBudgetScopeList[i].projectId == projectId) {
                        budgetScopeIds << projectBudgetScopeList[i].budgetScopeId
                    }
                }
            } else {
                result.put(Tools.MESSAGE, NO_BUDGET_SCOPE)
                return result
            }

            List<BudgBudgetScope> budgetScopeList = (List) budgetScopeCacheUtility.listByIds(budgetScopeIds)

            result.put(BUDGET_SCOPE_LIST, Tools.listForKendoDropdown(budgetScopeList, null, null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
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
     * Get object from execute method
     * @param obj - get budget scope list from execute method
     * @return - a linked hash map of received result from execute method
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return receivedResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
}
