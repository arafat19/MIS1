package com.athena.mis.budget.actions.budgetscope

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.budget.entity.BudgBudgetScope
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.budget.utility.BudgetScopeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * List of Budget scope
 * For details go through Use-Case doc named 'ListBudgetScopeActionService'
 */
class ListBudgetScopeActionService extends BaseService implements ActionIntf {

    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    BudgetScopeCacheUtility budgetScopeCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load budget scope list"
    private final Logger log = Logger.getLogger(getClass())

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
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
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
     * Give Budget scope List
     * 1. Get budgetScopeList from budgetScopeCacheUtility
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing budget scope list, total of budget scope list
     */
    public Object execute(Object parameters, Object obj) {
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if ((!params.sortname) || (params.sortname.toString().equals(ID))) {
                params.sortname = budgetScopeCacheUtility.SORT_BY_NAME
                params.sortorder = ASCENDING_SORT_ORDER
            }
            initPager(params)
            int count = budgetScopeCacheUtility.count()
            List budgetScopeList = budgetScopeCacheUtility.list(this)
            return [budgetScopeList: budgetScopeList, count: count]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    /**
     * Wrap Budget scope List in Grid Entity
     * @param obj - get a map from execute method with budget details list
     * @return - a wrapped map with budget scope list, pageNumber & count
     */
    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<BudgBudgetScope> budgetScopeList = (List<BudgBudgetScope>) executeResult.budgetScopeList
            int count = (int) executeResult.count
            List budgetScope = wrapListInGridEntityList(budgetScopeList, start)
            return [page: pageNumber, total: count, rows: budgetScope]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null]
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
                result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap Budget scope list for showing in the grid
     * @param budgetScopeList - list of budget Type
     * @param start - starting index of the page
     * @return -  list of wrapped budget scopes
     */
    private List wrapListInGridEntityList(List<BudgBudgetScope> budgetScopeList, int start) {
        List budgetScopes = []
        int counter = start + 1
        for (int i = 0; i < budgetScopeList.size(); i++) {
            BudgBudgetScope budgetScope = budgetScopeList[i]
            GridEntity obj = new GridEntity()
            obj.id = budgetScope.id
            obj.cell = [counter, budgetScope.id, budgetScope.name]
            budgetScopes << obj
            counter++
        }
        return budgetScopes
    }
}
