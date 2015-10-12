package com.athena.mis.application.actions.appgroup

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppGroup
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppGroupService
import com.athena.mis.application.utility.AppGroupCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new group(appGroup) object and show in grid
 *  For details go through Use-Case doc named 'CreateAppGroupActionService'
 */
class CreateAppGroupActionService extends BaseService implements ActionIntf {

    AppGroupService appGroupService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    AppGroupCacheUtility appGroupCacheUtility

    private static final String GROUP_CREATE_SUCCESS_MSG = "Group has been successfully saved"
    private static final String DEFAULT_ERROR_MESSAGE = "Group has not been saved."
    private static final String NAME_NOT_FOUND_FAILURE_MSG = "Group Name not found"
    private static final String DUPLICATE_NAME_FAILURE_MSG = "Duplicate Name found"
    private static final String APP_GROUP = "appGroup"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * Get user group
     * 1. build user group
     * 2. check group existence
     * 3. duplicate check for group
     * @param params - serialize parameters from UI
     * @param obj - N/A
     * @return - a map containing app-group and isError(true/false) and relevant message
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            AppGroup appGroup = buildUserGroupObject(parameterMap)
            if (!appGroup.name) {
                result.put(Tools.MESSAGE, NAME_NOT_FOUND_FAILURE_MSG)
                return result
            }

            int countExisting = appGroupCacheUtility.countByNameIlike(appGroup.name)
            if (countExisting > 0) {
                result.put(Tools.MESSAGE, DUPLICATE_NAME_FAILURE_MSG)
                return result
            }
            result.put(APP_GROUP, appGroup)
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
     * Save group(appGroup) object in DB and update cache utility accordingly
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
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            AppGroup appGroup = (AppGroup) receivedResult.get(APP_GROUP)
            AppGroup returnGroup = appGroupService.create(appGroup)
            appGroupCacheUtility.add(returnGroup, appGroupCacheUtility.SORT_ON_NAME, appGroupCacheUtility.SORT_ORDER_ASCENDING)
            result.put(APP_GROUP, returnGroup)
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
     * Show newly created group(appGroup) object in grid
     * Show success message
     * @param obj -object of appGroup
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            AppGroup appGroup = (AppGroup) receiveResult.get(APP_GROUP)
            GridEntity object = new GridEntity()
            object.id = appGroup.id
            object.cell = [
                    Tools.LABEL_NEW,
                    appGroup.id,
                    appGroup.name
            ]

            result.put(Tools.MESSAGE, GROUP_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
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
     * @return - newly built appGroup object
     */
    private AppGroup buildUserGroupObject(GrailsParameterMap parameterMap) {
        AppUser user = appSessionUtil.getAppUser()
        AppGroup appGroup = new AppGroup()
        appGroup.id = 0
        appGroup.version = 0
        appGroup.name = parameterMap.name.toString()
        appGroup.createdOn = new Date()
        appGroup.createdBy = user.id
        appGroup.updatedOn = null
        appGroup.updatedBy = 0
        appGroup.companyId = user.companyId
        return appGroup
    }
}