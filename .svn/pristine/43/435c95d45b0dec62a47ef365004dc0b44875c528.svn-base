package com.athena.mis.budget.actions.budgetscope

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.budget.entity.BudgBudgetScope
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.budget.utility.BudgetScopeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select budget scope object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectBudgetScopeActionService'
 */
class SelectBudgetScopeActionService extends BaseService implements ActionIntf {

    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    BudgetScopeCacheUtility budgetScopeCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String BUDGET_TYPE_NOT_FOUND_MASSAGE = "Selected budget scope is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select budget scope"

    /**
     * Check the access of director, project director and CFO
     * @param parameters - N/A
     * @param obj - N/A
     * @return -a map contains hasAccess(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            boolean dir = budgSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_DIRECTOR)
            boolean pd = budgSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR)
            boolean cfo = budgSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_CFO)
            if ((!dir) && (!pd) && (!cfo)) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
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
     * Get Budget scope object
     * 1. Get budget scope id from parameters
     * 2. Pull budgetScopeInstance from budgetScopeCacheUtility
     * 3. Check the existence of budgetScopeInstance
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing budget scope object and other necessary messages
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long budgetScopeId = Long.parseLong(parameterMap.id.toString())
            BudgBudgetScope budgetScopeInstance = (BudgBudgetScope) budgetScopeCacheUtility.read(budgetScopeId)
            if (budgetScopeInstance) {
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                result.put(Tools.ENTITY, budgetScopeInstance)
            } else {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, BUDGET_TYPE_NOT_FOUND_MASSAGE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUDGET_TYPE_NOT_FOUND_MASSAGE)
            return result
        }
    }

    /**
     * Build a map with budget scope object, version & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            BudgBudgetScope budgetScopeInstance = (BudgBudgetScope) executeResult.get(Tools.ENTITY)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.ENTITY, budgetScopeInstance)
            result.put(Tools.VERSION, budgetScopeInstance.version)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
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
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
}
