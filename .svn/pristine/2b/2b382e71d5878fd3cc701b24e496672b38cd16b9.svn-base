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
 *  Create new ContentCategory object and show in grid
 *  For details go through Use-Case doc named 'CreateContentCategoryActionService'
 */
class CreateContentCategoryActionService extends BaseService implements ActionIntf {

    ContentCategoryService contentCategoryService
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ContentTypeCacheUtility contentTypeCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String CREATE_FAILURE_MSG = "Content category could not be created"
    private static final String DEFAULT_ERROR_MSG = "Error occurred at content category create"
    private static final String CREATE_SUCCESS_MSG = "Content category has been created successfully"
    private static final String NAME_EXIST_MESSAGE = "Given content name already exists for same content type"
    private static final String CONTENT_CATEGORY = "contentCategory"

    /**
     * Check if user has access to create ContentCategory object or not
     * Get parameters from UI and build ContentCategory object
     * Check uniqueness of name of ContentCategory object by contentTypeId and companyId
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)  // default value
            // only development role type user can create ContentCategory object
            if (!appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            ContentCategory contentCategory = buildContentCategoryObject(parameterMap)
            // build ContentCategory object with params
            // check uniqueness of name of ContentCategory object by contentTypeId and companyId
            int countName = contentCategoryCacheUtility.countByNameAndContentTypeId(contentCategory.name, contentCategory.contentTypeId)
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
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Save ContentCategory object in DB and update cache utility accordingly
     * This method is in transactional block and will roll back in case of any exception
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
            ContentCategory newContentCategory = contentCategoryService.create(contentCategory)
            // save new ContentCategory object in DB
            // add new ContentCategory object in cache utility and keep the data sorted
            contentCategoryCacheUtility.add(newContentCategory, contentCategoryCacheUtility.SORT_ON_NAME, contentCategoryCacheUtility.SORT_ORDER_ASCENDING)
            result.put(CONTENT_CATEGORY, newContentCategory)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }

    /**
     * Show newly created ContentCategory object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            ContentCategory contentCategory = (ContentCategory) executeResult.get(CONTENT_CATEGORY)
            SystemEntity contentType = (SystemEntity) contentTypeCacheUtility.read(contentCategory.contentTypeId)
            GridEntity object = new GridEntity()    // build grid object
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
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
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
            LinkedHashMap previousResult = (LinkedHashMap) obj   // cast map returned from previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build ContentCategory object
     * @param params -serialized parameters from UI
     * @return -new ContentCategory object
     */
    private ContentCategory buildContentCategoryObject(GrailsParameterMap params) {
        long contentCategoryId = contentCategoryService.getContentCategoryId()
        ContentCategory contentCategory = new ContentCategory(params)
        contentCategory.id = contentCategoryId
        contentCategory.systemContentCategory = contentCategoryId
        contentCategory.createdBy = appSessionUtil.getAppUser().id
        contentCategory.createdOn = new Date()
        contentCategory.updatedOn = null
        contentCategory.updatedBy = 0L
        contentCategory.companyId = appSessionUtil.getCompanyId()
        return contentCategory
    }
}
