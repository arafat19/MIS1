package com.athena.mis.document.actions.appuserdoccategory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategoryUserMapping
import com.athena.mis.document.service.DocCategoryUserMappingService
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UpdateAppUserDocCategoryActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String UPDATE_SUCCESS_MESSAGE = "Member has been successfully updated"
    private static final String UPDATE_FAILURE_MESSAGE = "Member could not be updated"
    private static final String APP_USER_CATEGORY = "appUserCategory"
    private static final String OBJ_NOT_FOUND_MSG = "Object not found"
    private static final String CATEGORY_LABEL = 'categoryLabel'
    private static final String DEFAULT_CATEGORY_NAME = 'Category'

    DocCategoryUserMappingService docCategoryUserMappingService

    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility

    /**
     * 1. Get parameters from UI and build AllCategoryUserMapping object
     * 2. get categoryLabel from System configuration
     * 3. check if id is valid/invalid
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all object necessary for execute
     * This function is in transactional block and will roll back in case of any exception
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        Map result = new LinkedHashMap()
        String categoryLabel = DEFAULT_CATEGORY_NAME
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            long companyId = docSessionUtil.appSessionUtil.getCompanyId()

            SysConfiguration sysConfiguration = docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_CATEGORY_LABEL, companyId)
            if (sysConfiguration) {
                categoryLabel = sysConfiguration.value
            }

            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long appUserCategoryMappingId = Long.parseLong(parameterMap.id)
            DocCategoryUserMapping oldDocCategoryUserMapping = docCategoryUserMappingService.read(appUserCategoryMappingId)
            if (!oldDocCategoryUserMapping) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MSG)
                return result
            }
            DocCategoryUserMapping newDocCategoryUserMapping = buildAppUserForCategoryObject(parameterMap, oldDocCategoryUserMapping)
            result.put(APP_USER_CATEGORY, newDocCategoryUserMapping)
            result.put(CATEGORY_LABEL, categoryLabel)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update AllCategoryUserMapping object in DB
     * This method is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -a AllCategoryUserMapping obj returned from controller
     * @return -a AllCategoryUserMapping object necessary for buildSuccessResultForUI
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (LinkedHashMap) obj
        String categoryLabel = preResult.get(CATEGORY_LABEL)
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            DocCategoryUserMapping docCategoryUserMapping = (DocCategoryUserMapping) preResult.get(APP_USER_CATEGORY)
            docCategoryUserMappingService.update(docCategoryUserMapping)
            result.put(APP_USER_CATEGORY, docCategoryUserMapping)
            result.put(CATEGORY_LABEL, categoryLabel)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
     * Show newly updated AllCategoryUserMapping object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        Map executeResult = (LinkedHashMap) obj
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            DocCategoryUserMapping docCategoryUserMapping = (DocCategoryUserMapping) executeResult.get(APP_USER_CATEGORY)
            AppUser user = (AppUser) appUserCacheUtility.read(docCategoryUserMapping.userId)
            GridEntity object = new GridEntity()
            object.id = docCategoryUserMapping.id
            object.cell = [
                    Tools.LABEL_NEW,
                    docCategoryUserMapping.id,
                    user.username,
                    docCategoryUserMapping.isCategoryManager ? Tools.YES : Tools.NO
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
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
     * @param obj -map returned from previous methods
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        Map preResult = (LinkedHashMap) obj
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (preResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build AllCategoryUserMapping Object
     * @param parameterMap - GrailsParameterMap from UI
     * @param oldDocCategoryUserMapping - old AllCategoryUserMapping object
     * @return allCategoryUserMapping - An object of AllCategoryUserMapping
     */
    private DocCategoryUserMapping buildAppUserForCategoryObject(GrailsParameterMap parameterMap, DocCategoryUserMapping oldDocCategoryUserMapping) {
        DocCategoryUserMapping newDocCategoryUserMapping = new DocCategoryUserMapping(parameterMap)
        oldDocCategoryUserMapping.userId = newDocCategoryUserMapping.userId
        oldDocCategoryUserMapping.isCategoryManager = newDocCategoryUserMapping.isCategoryManager
        oldDocCategoryUserMapping.updatedBy = docSessionUtil.appSessionUtil.getAppUser().id
        oldDocCategoryUserMapping.updatedOn = new Date()
        return oldDocCategoryUserMapping
    }
}
