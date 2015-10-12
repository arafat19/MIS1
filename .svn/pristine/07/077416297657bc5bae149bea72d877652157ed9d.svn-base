package com.athena.mis.application.actions.contentcategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.ContentCategoryService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.ContentCategoryCacheUtility
import com.athena.mis.application.utility.ContentTypeCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update ContentCategory object and grid data
 *  For details go through Use-Case doc named 'UpdateContentCategoryActionService'
 */
class UpdateContentCategoryActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    ContentCategoryService contentCategoryService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    ContentTypeCacheUtility contentTypeCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String UPDATE_FAILURE_MSG = "Content category could not be updated"
    private static final String UPDATE_SUCCESS_MSG = "Content category has been updated successfully"
    private static final String CONTENT_NOT_FOUND = "Content category not found to be updated, refresh the page"
    private static final String NAME_EXIST_MESSAGE = "Given content name already exists for same content type"
    private static final String CONTENT_CATEGORY = "contentCategory"
    private static final String DEFAULT_ERROR_MSG = "Error occurred at content category update"

    /**
     * Check if user has access to update ContentCategory object or not
     * Get ContentCategory object from cache utility by id
     * Check existence of ContentCategory object
     * Get parameters from UI and build ContentCategory object for update
     * Check uniqueness of name of ContentCategory object by contentTypeId and companyId
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)  // default value
            // only development role type user can update ContentCategory
            if (!appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long contentCategoryId = Long.parseLong(params.id.toString())
            // get ContentCategory object from cache utility by id
            ContentCategory oldContentCategory = (ContentCategory) contentCategoryCacheUtility.read(contentCategoryId)
            // check if ContentCategory object exists or not
            if (!oldContentCategory) {
                result.put(Tools.MESSAGE, CONTENT_NOT_FOUND)
                return result
            }
            // build ContentCategory object for update
            ContentCategory contentCategory = buildContentCategoryObjectForUpdate(params, oldContentCategory)
            // check uniqueness of name of ContentCategory object by contentTypeId and companyId
            int countName = contentCategoryCacheUtility.countByNameAndContentTypeIdAndIdNotEqual(contentCategory.name, contentCategory.contentTypeId, contentCategoryId)
            if (countName > 0) {
                result.put(Tools.MESSAGE, NAME_EXIST_MESSAGE)
                return result
            }
            result.put(CONTENT_CATEGORY, contentCategory)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * Update ContentCategory object in DB & update cache utility accordingly
     * This function is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            ContentCategory contentCategory = (ContentCategory) preResult.get(CONTENT_CATEGORY)
            contentCategoryService.update(contentCategory)  // update ContentCategory object in DB
            // update cache utility accordingly and keep the data sorted
            contentCategoryCacheUtility.update(contentCategory, contentCategoryCacheUtility.SORT_ON_NAME, contentCategoryCacheUtility.SORT_ORDER_ASCENDING)
            result.put(CONTENT_CATEGORY, contentCategory)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(DEFAULT_ERROR_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
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
     * Show updated ContentCategory object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj    // cast map returned from execute method
            ContentCategory contentCategory = (ContentCategory) executeResult.get(CONTENT_CATEGORY)
            SystemEntity contentType = (SystemEntity) contentTypeCacheUtility.read(contentCategory.contentTypeId)
            GridEntity object = new GridEntity()    // build grid entity object
            object.id = contentCategory.id
            object.cell = [
                    Tools.LABEL_NEW,
                    contentCategory.id,
                    contentCategory.name,
                    contentType.key,
                    contentCategory.width > 0 ? contentCategory.width : Tools.EMPTY_SPACE,
                    contentCategory.height > 0 ? contentCategory.height : Tools.EMPTY_SPACE,
                    contentCategory.maxSize,
                    contentCategory.extension ? contentCategory.extension : Tools.EMPTY_SPACE
            ]

            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
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
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build ContentCategory object for update
     * @param params -serialized parameters from UI
     * @param oldContentCategory -old ContentCategory object
     * @return -updated ContentCategory object
     */
    private ContentCategory buildContentCategoryObjectForUpdate(GrailsParameterMap params, ContentCategory oldContentCategory) {
        ContentCategory newContentCategory = new ContentCategory(params)

        oldContentCategory.contentTypeId = newContentCategory.contentTypeId
        oldContentCategory.name = newContentCategory.name
        oldContentCategory.extension = newContentCategory.extension
        oldContentCategory.width = newContentCategory.width
        oldContentCategory.height = newContentCategory.height
        oldContentCategory.maxSize = newContentCategory.maxSize
        oldContentCategory.updatedBy = appSessionUtil.getAppUser().id
        oldContentCategory.updatedOn = new Date()
        return oldContentCategory
    }
}
