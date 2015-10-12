package com.athena.mis.application.actions.appgroup

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppGroup
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppGroupService
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.application.utility.AppGroupCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.UserGroupCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete user group(appGroup) object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAppGroupActionService'
 */
class DeleteAppGroupActionService extends BaseService implements ActionIntf {
    //call appGroup service
    AppGroupService appGroupService
    AppUserEntityService appUserEntityService
    @Autowired
    AppGroupCacheUtility appGroupCacheUtility
    @Autowired
    UserGroupCacheUtility userGroupCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    //assign appGroup information removing status information
    private static final String GROUP_DELETE_SUCCESS_MSG = "Group has been successfully deleted!"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete Group"
    private static final String NOT_FOUND_ERROR_MESSAGE = "Selected Group not found, Please refresh page"
    private static final String DELETED = "deleted"
    private static final String APP_GROUP_OBJ = "appGroup"
    private static final String HAS_ASSOCIATION_USER_GROUP_MAPPING = " User-Group-Mapping associated with selected Group"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * Get user group(appGroup) object
     * 1. pull group object from cache utility by groupId
     * 2. check group existence
     * 3. check group-user association
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long groupId = Long.parseLong(params.id.toString())

            AppGroup appGroup = (AppGroup) appGroupCacheUtility.read(groupId)
            if (!appGroup) {
                result.put(Tools.MESSAGE, NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            SystemEntity appUserSysEntityObject = (SystemEntity) appUserEntityTypeCacheUtility.readByReservedAndCompany(appUserEntityTypeCacheUtility.GROUP, appSessionUtil.getCompanyId())
            long entityTypeId = appUserSysEntityObject.id
            int countUserGroup = appUserEntityService.countByEntityIdAndEntityTypeId(groupId, entityTypeId)

            if (countUserGroup > 0) {
                result.put(Tools.MESSAGE, countUserGroup + HAS_ASSOCIATION_USER_GROUP_MAPPING)
                return result
            }
            result.put(APP_GROUP_OBJ, appGroup)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Delete user group(appGroup) object from DB and cache utility
     * This method is in transactional block and will roll back in case of any exception
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -boolean value(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AppGroup appGroup = (AppGroup) preResult.get(APP_GROUP_OBJ)
            appGroupService.delete(appGroup.id)
            appGroupCacheUtility.delete(appGroup.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete Group')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
        Map result = new LinkedHashMap()
        result.put(DELETED, Boolean.TRUE)
        result.put(Tools.MESSAGE, GROUP_DELETE_SUCCESS_MSG)
        return result
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
                LinkedHashMap receivedResult = (LinkedHashMap) obj
                String receivedMessage = receivedResult.get(Tools.MESSAGE)
                if (receivedMessage) {
                    result.put(Tools.MESSAGE, receivedMessage)
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

}
