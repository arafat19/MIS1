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

/**
 *  Select specific User-Role mapping object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectUserRoleActionService'
 */
class SelectUserRoleActionService extends BaseService implements ActionIntf {

    private static final String OBJ_NOT_FOUND_MASSAGE = "Selected user role mapping not found"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to select user group mapping"
    private static final String LST_APP_USER = "lstAppUser"

    private final Logger log = Logger.getLogger(getClass())

    UserRoleService userRoleService
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get User-Role mapping object by id
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing User-Group mapping object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long userId = Long.parseLong(params.userId.toString())
            long roleId = Long.parseLong(params.roleId.toString())
            UserRole userRole = userRoleService.read(userId, roleId)
            if (!userRole) {    //check existence of select UserRole object
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
                return result
            }
            List<GroovyRowResult> lstAppUser = (List<GroovyRowResult>) listAppUserForDropDown(userRole)
            result.put(LST_APP_USER, Tools.listForKendoDropdown(lstAppUser, null, null))
            result.put(Tools.ENTITY, userRole)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with necessary objects to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing User-Role mapping object to show on UI
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            UserRole userRole = (UserRole) executeResult.get(Tools.ENTITY)
            result.put(Tools.ENTITY, userRole)
            result.put(LST_APP_USER, executeResult.get(LST_APP_USER))
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
            AND user_id <> :id
        )
        ORDER BY username
    """

    private List listAppUserForDropDown(UserRole userRole) {
        Map queryParams = [
                id: userRole.userId,
                roleId: userRole.roleId,
                companyId: appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> lstAppUser = executeSelectSql(QUERY_FOR_APP_USER, queryParams)
        return lstAppUser
    }
}
