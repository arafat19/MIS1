package com.athena.mis.document.actions.appuserdocsubcategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocSubCategoryUserMapping
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.service.DocSubCategoryUserMappingService
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class CreateAppUserDocSubCategoryActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SAVE_SUCCESS_MESSAGE = "Member has been saved successfully"
    private static final String SAVE_FAILURE_MESSAGE = "Failed to create member"
    private static final String APP_USER_SUB_CATEGORY = "appUserSubCategory"
    private static final String OBJ_NOT_FOUND = " not found"
    private static final String DEFAULT_CATEGORY_NAME = 'Category'
    private static final String SUB_CATEGORY_LABEL = 'Sub Category'
    private static final String CATEGORY_ALREADY_MAPPED = 'This user already mapped with this '

    DocSubCategoryUserMappingService docSubCategoryUserMappingService

    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility

    /**
     * 1. Get parameters from UI and build AllCategoryUserMapping object
     * 2. check if id is valid/invalid
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all object necessary for execute
     */
    public Object executePreCondition(Object params, Object obj) {
        Map result = new LinkedHashMap()
        String categoryLabel = DEFAULT_CATEGORY_NAME
        String subCategoryLabel = SUB_CATEGORY_LABEL
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check required parameters
            if (!parameterMap.categoryId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long categoryId = Long.parseLong(parameterMap.categoryId.toString())


            DocCategory docCategory = (DocCategory) docCategoryCacheUtility.read(categoryId)
            if (!docCategory) {
                result.put(Tools.MESSAGE, categoryLabel + OBJ_NOT_FOUND)
                return result
            }

            long subCategoryId = Long.parseLong(parameterMap.sub_category_id.toString())
            DocSubCategory docSubCategory = (DocSubCategory) docSubCategoryCacheUtility.read(subCategoryId)
            if (!docSubCategory) {
                result.put(Tools.MESSAGE, subCategoryLabel + OBJ_NOT_FOUND)
                return result
            }
            long userId = Long.parseLong(parameterMap.userId.toString())
            long companyId = docSessionUtil.appSessionUtil.getCompanyId()

            DocSubCategoryUserMapping isCategoryUserMapping = docSubCategoryUserMappingService.findByCompanyIdAndUserIdAndCategoryIdAndSubCategoryId(companyId, userId, categoryId, subCategoryId)
            if (isCategoryUserMapping) {
                result.put(Tools.MESSAGE, CATEGORY_ALREADY_MAPPED + categoryLabel)
                return result
            }

            DocSubCategoryUserMapping subCategoryUserMapping = buildAppUserSubCategoryObject(parameterMap, docCategory, docSubCategory)
            result.put(APP_USER_SUB_CATEGORY, subCategoryUserMapping)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Save AllCategoryUserMapping object in DB
     * This method is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -a AllCategoryUserMapping obj returned from controller
     * @return -a AllCategoryUserMapping object necessary for buildSuccessResultForUI
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (LinkedHashMap) obj
            DocSubCategoryUserMapping subCategoryUserMapping = (DocSubCategoryUserMapping) preResult.get(APP_USER_SUB_CATEGORY)
            docSubCategoryUserMappingService.create(subCategoryUserMapping)
            result.put(APP_USER_SUB_CATEGORY, subCategoryUserMapping)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show newly created AllCategoryUserMapping object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (LinkedHashMap) obj
            DocSubCategoryUserMapping docSubCategoryUserMapping = (DocSubCategoryUserMapping) executeResult.get(APP_USER_SUB_CATEGORY)
            AppUser user = (AppUser) appUserCacheUtility.read(docSubCategoryUserMapping.userId)
            GridEntity object = new GridEntity()
            object.id = docSubCategoryUserMapping.id
            object.cell = [
                    Tools.LABEL_NEW,
                    docSubCategoryUserMapping.id,
                    user.username,
                    docSubCategoryUserMapping.isSubCategoryManager ? Tools.YES : Tools.NO
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.MESSAGE, SAVE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (LinkedHashMap) obj
            if (preResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Build appUserSubCategory Object
     * @param parameterMap - GrailsParameterMap from UI
     * @param categoryId - category id
     * @return subCategoryUserMapping - An object of appUserSubCategoryMapping
     */
    private DocSubCategoryUserMapping buildAppUserSubCategoryObject(GrailsParameterMap parameterMap, DocCategory category, DocSubCategory subCategory) {
        DocSubCategoryUserMapping subCategoryUserMapping = new DocSubCategoryUserMapping(parameterMap)
        subCategoryUserMapping.userId = Long.parseLong(parameterMap.userId)
        subCategoryUserMapping.categoryId = category.id
        subCategoryUserMapping.subCategoryId = subCategory.id
        subCategoryUserMapping.companyId = docSessionUtil.appSessionUtil.getAppUser().id
        subCategoryUserMapping.createdBy = docSessionUtil.appSessionUtil.getAppUser().id
        subCategoryUserMapping.createdOn = new Date()
        subCategoryUserMapping.updatedBy = 0
        subCategoryUserMapping.updatedOn = null
        return subCategoryUserMapping
    }
}
