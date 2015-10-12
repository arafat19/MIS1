package com.athena.mis.application.actions.userrole

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.UserRole
import com.athena.mis.application.service.UserRoleService
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update User-Role mapping object and to show on grid list
 *  For details go through Use-Case doc named 'UpdateUserRoleActionService'
 */
class UpdateUserRoleActionService extends BaseService implements ActionIntf {

    UserRoleService userRoleService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String FAILURE_MESSAGE = 'Failed to update user-role'
    private static final String SUCCESS_MESSAGE = 'user-role has been update successfully'
    private static final String OBJECT_NOT_FOUND = 'user-role not found'
    private static final String OBJ_OLD_USER_ROLE = 'objUserRole'
    private static final String ROLE_ID = 'roleId'
    private static final String USER_ID = 'userId'

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria for updating User-Role mapping object
     *      1) Check access permission to update User-Role mapping object
     *      2) Check existence of selected AppUser & selected Role object
     *      3) Check existence of same userRole mapping object
     * @param params -parameters send from UI
     * @param obj -N/A
     * @return -a map contains oldUserRole object and isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)

            //Only admin has right to update User-Role mapping object
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            GrailsParameterMap parameters = (GrailsParameterMap) params
            Long existingUserId = parameters.existingUserId.toLong()
            long existingRoleId = Long.parseLong(parameters.existingRoleId.toString())

            UserRole existingUserRole = userRoleService.read(existingUserId, existingRoleId)
            if (!existingUserRole) {//check existence of old User-Role object
                result.put(Tools.MESSAGE, OBJECT_NOT_FOUND)
                return result
            }

            result.put(OBJ_OLD_USER_ROLE, existingUserRole)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update User-Role mapping object in DB
     * @param parameters -parameters send from UI
     * @param obj -contains oldUserRole mapping object send from executePreCondition
     * @return -contains updated userRole mapping object, roleId for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            UserRole existingUserRole = (UserRole) preResult.get(OBJ_OLD_USER_ROLE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            Long roleId = params.role.id.toString().toLong()
            Long userId = params.user.id.toString().toLong()
            boolean success //default value is FALSE

            //if user changed then update otherwise don't
            if (existingUserRole.user.id.equals(userId)) {
                success = true
            } else {
                success = userRoleService.update(existingUserRole, userId)
            }

            result.put(Tools.IS_ERROR, new Boolean(!success))
            result.put(ROLE_ID, roleId)
            result.put(USER_ID, userId)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap updated userRole mapping object to show on grid
     * @param obj -a map contains userRole mapping object & roleId send from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            long roleId = Long.parseLong(executeResult.get(ROLE_ID).toString())
            long userId = Long.parseLong(executeResult.get(USER_ID).toString())
            AppUser user = (AppUser) appUserCacheUtility.read(userId)
            GridEntity objGrid = new GridEntity()
            objGrid.id = userId
            objGrid.cell = [
                    Tools.LABEL_NEW,
                    userId,
                    roleId,
                    user.username
            ]

            result.put(Tools.ENTITY, objGrid)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }
}
