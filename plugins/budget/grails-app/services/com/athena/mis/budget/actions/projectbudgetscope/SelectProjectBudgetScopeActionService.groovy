package com.athena.mis.budget.actions.projectbudgetscope

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select Project Budget scope Object  and show in UI for editing
 *  For details go through Use-Case doc named 'SelectProjectBudgetScopeActionService'
 */
class SelectProjectBudgetScopeActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Can't select project budget scope"
    private static final String LST_ASSIGNED_BUDGET_SCOPE = "lstAssignedBudgetScope"
    private static final String LST_AVAILABLE_BUDGET_SCOPE = "lstAvailableBudgetScope"

    //@todo:model keep existing query
    private static final String QUERY_FOR_ASSIGN_BUDGET = """
            SELECT pbt.budget_scope_id AS id, bt.name
            FROM budg_project_budget_scope pbt
            INNER JOIN budg_budget_scope bt ON pbt.budget_scope_id = bt.id
            WHERE pbt.project_id = :projectId AND
                 pbt.company_id =:companyId
        """
    //@todo:model keep existing query
    private static final String QUERY_FOR_AVAILABLE_BUDGET = """
            SELECT id, name
            FROM budg_budget_scope
            WHERE id NOT IN(SELECT budget_scope_id AS id
                FROM budg_project_budget_scope
                WHERE project_id =:projectId AND company_id =:companyId)
            AND company_id =:companyId
        """

    /**
     * Check access of role type director, project director and CFO
     * @param parameters - N/A
     * @param obj -  N/A
     * @return - a map containing hasAccess(True/False)
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
     * Get list of assigned budget scope
     * 1. Get available budget scope
     * 2. Get project id from params
     * 3. Get project object from projectCacheUtility
     * 4. Get Assigned budget scope from executeSelectSql by project id and company id
     * 5. Get Available budget scope from executeSelectSql by project id and company id
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing lstAssignedBudgetType, lstAvailableBudgetType & isError(TRUE/FALSE)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            Project project = (Project) projectCacheUtility.read(projectId)

            List lstAssignedBudgetScope = executeSelectSql(QUERY_FOR_ASSIGN_BUDGET, [projectId: project.id, companyId: budgSessionUtil.appSessionUtil.getCompanyId()])
            List lstAvailableBudgetScope = executeSelectSql(QUERY_FOR_AVAILABLE_BUDGET, [projectId: project.id, companyId: budgSessionUtil.appSessionUtil.getCompanyId()])
            result.put(LST_ASSIGNED_BUDGET_SCOPE, lstAssignedBudgetScope)
            result.put(LST_AVAILABLE_BUDGET_SCOPE, lstAvailableBudgetScope)
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
     * @param obj - get budget type list from execute method
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