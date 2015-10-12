package com.athena.mis.application.actions.contentcategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.service.ContentCategoryService
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.ContentCategoryCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete ContentCategory object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteContentCategoryActionService'
 */
class DeleteContentCategoryActionService extends BaseService implements ActionIntf {

    ContentCategoryService contentCategoryService
    EntityContentService entityContentService
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String DELETE_SUCCESS_MESSAGE = "Content category has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Content category could not be deleted, please refresh the content category list"
    private static final String OBJ_NOT_FOUND = "Selected content category not found, please refresh the content category list"
    private static final String ENTITY_CONTENT_FOUND_MSG = " entity content(s) has been created by this content category"
    private static final String DEFAULT_ERROR_MSG = "Error occurred at content category delete"
    private static final String IS_RESERVED_MSG = "Selected content category is reserved"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check if user has access to delete ContentCategory object or not
     * Check required parameter
     * Get ContentCategory object from cache utility by id
     * Check if ContentCategory object is reserved or not
     * Check association with contentCategoryId of EntityContent
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            result.put(Tools.HAS_ACCESS, Boolean.TRUE) // default value
            // only development role type user can delete ContentCategory object
            if (!appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            GrailsParameterMap params = (GrailsParameterMap) parameters
            // check required parameter
            if (!params.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long contentCategoryId = Long.parseLong(params.id.toString())
            // get ContentCategory object from cache utility by id
            ContentCategory contentCategory = (ContentCategory) contentCategoryCacheUtility.read(contentCategoryId)
            // check if ContentCategory object exists or not
            if (!contentCategory) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            // reserved ContentCategory object can not be deleted
            if (contentCategory.isReserved) {
                result.put(Tools.MESSAGE, IS_RESERVED_MSG)
                return result
            }
            // check association with contentCategoryId of EntityContent
            int countEntityContent = entityContentService.countByContentCategoryId(contentCategoryId)
            if (countEntityContent > 0) {
                result.put(Tools.MESSAGE, countEntityContent + ENTITY_CONTENT_FOUND_MSG)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Delete ContentCategory object from DB and cache utility
     * This function is in transactional block and will roll back in case of any exception
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long contentCategoryId = Long.parseLong(parameterMap.id.toString())
            contentCategoryService.delete(contentCategoryId)    // delete employee object from DB
            contentCategoryCacheUtility.delete(contentCategoryId)   // delete employee object from cache utility
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post operation
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
        return [deleted: Boolean.TRUE.booleanValue(), message: DELETE_SUCCESS_MESSAGE]
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj   // cast map returned from previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }
}
