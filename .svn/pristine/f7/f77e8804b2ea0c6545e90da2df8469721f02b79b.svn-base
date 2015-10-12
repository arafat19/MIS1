package com.athena.mis.application.actions.userrole

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.UserRole
import com.athena.mis.application.service.UserRoleService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete User-Role mapping object
 *  For details go through Use-Case doc named 'DeleteUserRoleActionService'
 */
class DeleteUserRoleActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    UserRoleService userRoleService
    @Autowired
    AppSessionUtil appSessionUtil

    public static final String ENTITY_NOT_FOUND_ERROR_MESSAGE = "User-Role mapping not found"
    private static final String SUCCESS_MESSAGE = "User-Role mapping has been deleted successfully"
    private static final String INVALID_INPUT_MESSAGE = "Failed to delete User-Role mapping due to invalid input"
    private static final String FAILURE_MESSAGE = "Failed to delete User-Role mapping"
    private static final String OBJ_USER_ROLE = "objUserRole"
    private static final String LST_APP_USER = "lstAppUser"
    private static final String ROLE_ID = "roleId"

    /**
     * Check different criteria to delete User-Role mapping object
     *      1) Check access permission to delete User-Role mapping object
     *      2) Check existence of required parameter
     *      3) Check existence of userRole mapping object
     * @param params -parameter send from UI
     * @param obj -N/A
     * @return -userRole mapping object and isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)

            //Only admin can delete User-Role mapping object
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.userId || !params.roleId) { //check existence of required parameter
                result.put(Tools.MESSAGE, INVALID_INPUT_MESSAGE)
                return result
            }

            Long userId = Long.parseLong(params.userId.toString())
            Long roleId = Long.parseLong(params.roleId.toString())
            UserRole userRole = userRoleService.read(userId, roleId)
            if (!userRole) { //Check existence of userRole mapping object
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            result.put(OBJ_USER_ROLE, userRole)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Delete User-Role mapping object in DB
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -contains boolean value(true/false)
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj
            UserRole userRole = (UserRole) preResult.get(OBJ_USER_ROLE)
            result.put(ROLE_ID, userRole.roleId)
            userRoleService.delete(userRole)//delete object from DB
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete user-role')
            return Boolean.FALSE
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * contains delete success message to show on UI
     * @param obj -map returned from execute method
     * @return -a map contains boolean value(false) and delete success message to show on UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            Long roleId = (Long) executeResult.get(ROLE_ID)
            List<GroovyRowResult> lstAppUser = listAppUserForDropDown(roleId)
            lstAppUser = Tools.listForKendoDropdown(lstAppUser, null, null)
            result.put(LST_APP_USER, lstAppUser)
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

    private static final String QUERY_FOR_APP_USER = """
        SELECT id, username || ' (' || id || ')' as name
        FROM app_user
        WHERE company_id = :companyId
        AND enabled = true
        AND id NOT IN
        (
            SELECT user_id
            FROM user_role
            WHERE role_id = :roleId
        )
        ORDER BY username
    """

    private List listAppUserForDropDown(long roleId) {
        Map queryParams = [
                roleId: roleId,
                companyId: appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> lstAppUser = executeSelectSql(QUERY_FOR_APP_USER, queryParams)
        return lstAppUser
    }
}
