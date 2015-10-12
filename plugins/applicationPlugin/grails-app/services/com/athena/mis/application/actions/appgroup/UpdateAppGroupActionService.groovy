package com.athena.mis.application.actions.appgroup

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppGroup
import com.athena.mis.application.service.AppGroupService
import com.athena.mis.application.utility.AppGroupCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update new group(appGroup) object and show in grid
 *  For details go through Use-Case doc named 'UpdateAppGroupActionService'
 */
class UpdateAppGroupActionService extends BaseService implements ActionIntf {

    AppGroupService appGroupService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    AppGroupCacheUtility appGroupCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Group could not be updated"
    private static final String GROUP_UPDATE_SUCCESS_MESSAGE = "Group has been updated successfully"
    private static final String NOT_FOUND_ERROR_MESSAGE = "Group not found"
    private static final String APP_GROUP_OBJ = "appGroup"
    private static final String DUPLICATE_NAME_FAILURE_MSG = "Same group name already exists"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * Get user group
     * 1. pull previous state of group object
     * 2. check group existence
     * 3. build user group
     * 4. duplicate check for group
     * @param parameters - serialize parameters from UI
     * @param obj - N/A
     * @return - a map containing app-group and isError(true/false) and relevant message
     */
    @Transactional
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long groupId = Long.parseLong(parameterMap.id.toString())
            AppGroup oldGroup = appGroupService.read(groupId)
            if (!oldGroup) {
                result.put(Tools.MESSAGE, NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            AppGroup appGroup = buildUserGroupObject(parameterMap, oldGroup)

            int countExisting = appGroupCacheUtility.countByNameIlikeAndIdNotEqual(appGroup.name, appGroup.id)
            if (countExisting > 0) {
                result.put(Tools.MESSAGE, DUPLICATE_NAME_FAILURE_MSG)
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
     * Update group(appGroup) object in DB and update cache utility accordingly
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -saved group(appGroup) object
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AppGroup appGroup = (AppGroup) preResult.get(APP_GROUP_OBJ)
            appGroupService.update(appGroup)
            appGroupCacheUtility.update(appGroup, appGroupCacheUtility.SORT_ON_NAME, appGroupCacheUtility.SORT_ORDER_ASCENDING)
            result.put(APP_GROUP_OBJ, appGroup)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
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
     * Show newly updated group(appGroup) object in grid
     * Show success message
     * @param obj -object of appGroup
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AppGroup appGroup = (AppGroup) executeResult.get(APP_GROUP_OBJ)
            GridEntity object = new GridEntity()
            object.id = appGroup.id
            object.cell = [
                    Tools.LABEL_NEW,
                    appGroup.id,
                    appGroup.name]

            result.put(Tools.MESSAGE, GROUP_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, appGroup.version)
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
    /**
     * Get newly built appGroup object
     * @param parameterMap - serialize parameters from UI
     * @param oldGroup - previous state of group object
     * @return - newly built appGroup object
     */
    private AppGroup buildUserGroupObject(GrailsParameterMap parameterMap, AppGroup oldGroup) {
        oldGroup.name = parameterMap.name
        oldGroup.updatedOn = new Date()
        oldGroup.updatedBy = appSessionUtil.getAppUser().id
        return oldGroup
    }
}