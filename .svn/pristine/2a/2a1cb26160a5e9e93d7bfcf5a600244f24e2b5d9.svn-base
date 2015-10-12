package com.athena.mis.application.actions.entitycontent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.EntityContent
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.service.ProjectService
import com.athena.mis.application.utility.ContentEntityTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete entityContent(Project attachment) object
 *  For details go through Use-Case doc named 'DeleteEntityContentActionService'
 */
class DeleteEntityContentActionService extends BaseService implements ActionIntf {

    EntityContentService entityContentService
    ProjectService projectService
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String DELETE_SUCCESS_MESSAGE = "Attachment has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Attachment could not be deleted"
    private static final String ENTITY_NOT_FOUND = "Selected attachment not found"
    private static final String INVALID_INPUT_MSG = "Failed to delete attachment due to invalid input"
    private static final String DELETED = "deleted"

    /**
     * Check different criteria to delete entityContent(Project attachment) object
     *      1) Check existence of required parameter
     *      2) Check existence of entityContent(attachment) object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) { //check existence of required parameter
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }

            long entityContentId = Long.parseLong(params.id.toString())
            EntityContent entityContent = entityContentService.read(entityContentId)
            if (!entityContent) {//check existence of object
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Delete entityContent(Project attachment) object from DB
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -Boolean value (true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long entityContentId = Long.parseLong(params.id.toString())
            EntityContent entityContent = entityContentService.read(entityContentId)
            int deleteStatus = entityContentService.delete(entityContentId)
            if (!deleteStatus) { //delete entityContent(Project attachment) object from DB
                result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
                return result
            }
            updateContentCount(entityContent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DELETE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
     * contains delete success message to show on UI
     * @param obj -N/A
     * @return -a map contains boolean value(true) and delete success message to show on UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(DELETED, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update content count counter as content entity type
     * @param entityContent - object of EntityContent
     */
    private void updateContentCount(EntityContent entityContent) {
        SystemEntity entityContentType = (SystemEntity) contentEntityTypeCacheUtility.read(entityContent.entityTypeId)
        int count = -1
        int updateCount = 1
        switch (entityContentType.reservedId) {
            case contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_BUDGET:
                updateCount = (Integer) budgetImplService.updateContentCountForBudget(entityContent.entityId, count)
                break
            case contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_PROJECT:
                updateCount = projectService.updateContentCountForProject(entityContent.entityId, count)
                Project project =  projectService.read(entityContent.entityId)
                projectCacheUtility.update(project, projectCacheUtility.SORT_ON_NAME, projectCacheUtility.SORT_ORDER_ASCENDING)
                break
            case contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_FINANCIAL_YEAR:
                updateCount = (Integer) accountingImplService.updateContentCountForFinancialYear(entityContent.entityId, count)
                break
            default:
                break
        }
        if (updateCount <= 0) {
            throw new RuntimeException("Error occurred updating content count")
        }
    }
}
