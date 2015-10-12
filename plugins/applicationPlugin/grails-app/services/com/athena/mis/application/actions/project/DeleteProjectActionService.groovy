package com.athena.mis.application.actions.project

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.ProjectService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete project object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteProjectActionService'
 */
class DeleteProjectActionService extends BaseService implements ActionIntf {

    private static final String DELETE_PROJECT_SUCCESS_MESSAGE = "Project has been deleted successfully"
    private static
    final String DELETE_PROJECT_FAILURE_MESSAGE = "Project could not be deleted, Please refresh the Project List"
    private static final String HAS_ASSOCIATION_USER_PROJECT = " user is associated with selected project"
    private static final String HAS_ASSOCIATION_ENTITY_CONTENT_PROJECT = " content is associated with selected project"
    private static final String HAS_ASSOCIATION_MESSAGE_BUDGET = " budget is associated with selected project"
    private static
    final String HAS_ASSOCIATION_MESSAGE_BUDGET_DETAILS = " budget details is associated with selected project"
    private static
    final String HAS_ASSOCIATION_MESSAGE_PURCHASE_REQUEST = " purchase request is associated with selected project"
    private static
    final String HAS_ASSOCIATION_MESSAGE_PURCHASE_ORDER = " purchase order is associated with selected project"
    private static
    final String HAS_ASSOCIATION_MESSAGE_INVENTORY_TRANSACTION = " store transaction is associated with selected project"
    private static final String HAS_ASSOCIATION_MESSAGE_INVENTORY = " store is associated with selected project"
    private static final String HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS = " voucher is associated with this project"

    private final Logger log = Logger.getLogger(getClass())

    ProjectService projectService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * 1. check user access as Admin role
     * 2. pull project object
     * 3. check for project existence
     * 4. association check for project with different domains
     * 5. check input validation
     * @param parameters - serialize parameters from UI
     * @param obj -N/A
     * @return - a map containing isError(true/false) depending on method success &  relevant message.
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long projectId = Long.parseLong(params.id.toString())

            Project project = (Project) projectCacheUtility.read(projectId)

            if (!project) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            Map preResult = (Map) hasAssociation(project)
            Boolean hasAssociation = (Boolean) preResult.get(Tools.HAS_ASSOCIATION)

            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PROJECT_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Delete project object from DB and cache utility
     * 1. pull project object from cache utility by project id
     * 2. delete selected project
     * 3. delete from cache utility
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -boolean value true/false depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long projectId = Long.parseLong(params.id.toString())
            Project project = (Project) projectCacheUtility.read(projectId)
            projectService.delete(project)
            projectCacheUtility.delete(project.id)
            return Boolean.TRUE
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete project')
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
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return [deleted: Boolean.TRUE.booleanValue(), message: DELETE_PROJECT_SUCCESS_MESSAGE]
    }
    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_PROJECT_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PROJECT_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * 1. check association with entity_id(project_id) of app_user_entity(user_project)
     * 2. check association with project_id of budg_budget
     * 3. check association with project_id of budget_details
     * 4. check association with project_id of purchase_request
     * 5. check association with project_id of purchase_order
     * 6. check association with project_id of inventory
     * 7. check association with project_id of inventory_transaction
     * 8. check association with project_id of voucher_details
     * @param project - project object
     * @return - a map containing hasAccess(true/false) & relevant association check message
     */
    private LinkedHashMap hasAssociation(Project project) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)
        long projectId = project.id
        long companyId = project.companyId
        Integer count = 0


        if (project.contentCount > 0) {
            result.put(Tools.MESSAGE, project.contentCount.toString() + HAS_ASSOCIATION_ENTITY_CONTENT_PROJECT)
            return result
        }

        count = countUserProject(projectId)
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_USER_PROJECT)
            return result
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.BUDGET)) {
            count = countBudget(projectId, companyId)
            if (count.intValue() > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_BUDGET)
                return result
            }

            count = countBudgetDetails(projectId, companyId)
            if (count.intValue() > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_BUDGET_DETAILS)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.PROCUREMENT)) {
            count = countPurchaseRequest(projectId, companyId)
            if (count.intValue() > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_PURCHASE_REQUEST)
                return result
            }

            count = countPurchaseOrder(projectId, companyId)
            if (count.intValue() > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_PURCHASE_ORDER)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
            count = countInventory(projectId)
            if (count.intValue() > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_INVENTORY)
                return result
            }

            count = countInventoryTransaction(projectId, companyId)
            if (count.intValue() > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_INVENTORY_TRANSACTION)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
            count = countVoucherDetails(projectId)
            if (count.intValue() > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS)
                return result
            }
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    private static final String SELECT_QUERY = """
            SELECT COUNT(id) AS count
            FROM app_user_entity
            WHERE entity_id =:projectId AND
            entity_type_id =:entityTypeId
            """

    /**
     * Get total user_entity(user_project) number of given project-id
     * @param projectId - project id
     * @return - total user_entity(user_project) number
     */
    private int countUserProject(long projectId) {
        SystemEntity appUserSysEntityObject = (SystemEntity) appUserEntityTypeCacheUtility.readByReservedAndCompany(appUserEntityTypeCacheUtility.PROJECT, appSessionUtil.getCompanyId())
        Map queryParams = [
                projectId: projectId,
                entityTypeId: appUserSysEntityObject.id
        ]
        List results = executeSelectSql(SELECT_QUERY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_QUERY = """
            SELECT COUNT(id) AS count
            FROM budg_budget
            WHERE project_id =:projectId AND
            company_id =:companyId
            """
    /**
     * Get total budget number of given project-id
     * @param projectId - project id
     * @return - total budget number
     */
    private int countBudget(long projectId, long companyId) {
        Map queryParams = [
                projectId: projectId,
                companyId: companyId
        ]
        List results = executeSelectSql(COUNT_QUERY, queryParams)
        int count = results[0].count
        return count
    }
    /**
     * Get total budget_details number of given project-id
     * @param projectId - project id
     * @return - total budget_details number
     */
    private int countBudgetDetails(long projectId, long companyId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM budg_budget_details
            WHERE project_id = ${projectId} AND
                  company_id = ${companyId}
            """
        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }
    /**
     * Get total purchase_request number of given project-id
     * @param projectId - project id
     * @return - total purchase_request number
     */
    private int countPurchaseRequest(long projectId, long companyId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_request
            WHERE project_id = ${projectId} AND
                  company_id = ${companyId}
            """
        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }
    /**
     * Get total purchase_order number of given project-id
     * @param projectId - project id
     * @return - total purchase_order number
     */
    private int countPurchaseOrder(long projectId, long companyId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_order
            WHERE project_id = ${projectId} AND
                  company_id = ${companyId}
            """
        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }
    /**
     * Get total inventory number of given project-id
     * @param projectId - project id
     * @return - total inventory number
     */
    private int countInventory(long projectId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM inv_inventory
            WHERE project_id = ${projectId} """

        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }
    /**
     * Get total inventory_transaction number of given project-id
     * @param projectId - project id
     * @return - total inventory_transaction number
     */
    private int countInventoryTransaction(long projectId, long companyId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction
            WHERE project_id = ${projectId} AND
                  company_id = ${companyId}
            """
        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }
    /**
     * Get total voucher_details number of given project-id
     * @param projectId - project id
     * @return - total voucher_details number
     */
    private int countVoucherDetails(long projectId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM acc_voucher_details
            WHERE project_id=${projectId}
            """
        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }
}
