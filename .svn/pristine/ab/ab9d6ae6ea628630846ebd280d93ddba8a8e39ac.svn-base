package com.athena.mis.budget.actions.projectbudgetscope

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.budget.service.ProjectBudgetScopeService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Update Project Budget scope object, projectCacheUtility and show in the UI
 * For details go through Use-Case doc named 'UpdateProjectBudgetScopeActionService'
 */
class UpdateProjectBudgetScopeActionService extends BaseService implements ActionIntf {

    private static final String UPDATE_FAILURE_MESSAGE = "One or More Budget Scope(s) is associated with Budget(s)."
    private static final String UPDATE_SUCCESS_MESSAGE = "Project scope mapping has been updated successfully"
    private static final String ERROR_FOR_INVALID_INPUT = "Error occurred for invalid input"

    private final Logger log = Logger.getLogger(getClass())

    ProjectBudgetScopeService projectBudgetScopeService
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    /**
     * Check access of role type director, project director and CFO
     * 1. Get project id and check the validity of input
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing isError(TRUE/FALSE)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)

            boolean dir = budgSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_DIRECTOR)
            boolean pd = budgSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR)
            boolean cfo = budgSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_CFO)
            if ((!dir) && (!pd) && (!cfo)) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.projectId) {
                result.put(Tools.MESSAGE, ERROR_FOR_INVALID_INPUT)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update project budget scope object in DB & projectCacheUtility
     * 1. This function is in transactional boundary and will roll back in case of any exception
     * 2. Get project object from projectCacheUtility by project id
     * 3. Get Assigned Budget type ids from params
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            List<Long> assignedBudgetScopeIds = []

            if (params.assignedBudgetTypeIds.toString().length() > 0) {
                List<String> lstTemp = params.assignedBudgetTypeIds.split(Tools.UNDERSCORE)
                for (int i = 0; i < lstTemp.size(); i++) {
                    assignedBudgetScopeIds << lstTemp[i].toLong()
                }
            }
            Project project = (Project) projectCacheUtility.read(Long.parseLong(params.projectId.toString()))
            boolean success = projectBudgetScopeService.update(assignedBudgetScopeIds, project.id)

            return new Boolean(success)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Project Budget Scope update failed')
            return Boolean.FALSE
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show updated project budget type object in grid
     * Show success message
     * @param obj - N/A
     * @return - a map containing update success message
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true/false & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
}