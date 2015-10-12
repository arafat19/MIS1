package com.athena.mis.application.actions.userrole

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Role
import com.athena.mis.application.utility.RoleCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for User-Role mapping CRUD and list of User-Role mapping object(s) for grid
 *  For details go through Use-Case doc named 'ShowUserRoleActionService'
 */
class ShowUserRoleActionService extends BaseService implements ActionIntf {

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleCacheUtility roleCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load User-Role page"
    private static final String GRID_OBJ = "gridObj"
    private static final String SORT_NAME = "sortname"
    private static final String LABEL_USER = "user"
    private static final String ROLE_OBJ = "roleObj"
    private static final String ROLE_ID = "roleId"
    private static final String ROLE_NAME = "roleName"
    private static final String ROLE_NOT_FOUND = "Selected role not found"
    private static final String LST_USER_ROLE = "lstUserRole"
    private static final String LST_APP_USER = "lstAppUser"

    /**
     * check access permission to get list of User-Role mapping object(s)
     * @param parameters -N/A
     * @param obj -N/A
     * @return -boolean value of access permission(true/false)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            //Only admin has right to get list of User-Role mapping object(s)
            if (appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            }
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.roleId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long roleId= Long.parseLong(params.roleId)
            Role role = (Role) roleCacheUtility.read(roleId)
            if (!role) {
                result.put(Tools.MESSAGE, ROLE_NOT_FOUND)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(ROLE_OBJ, role)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            return result
        }
    }

    /**
     * get list of User-Role mapping object(s)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains User-Role mapping object(s)
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        try {
            Map result = new LinkedHashMap()
            GrailsParameterMap parameters = (GrailsParameterMap) params
            parameters.put(SORT_NAME, LABEL_USER)
            initPager(parameters)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Role role = (Role) preResult.get(ROLE_OBJ)
            Map serviceReturn = list(role)
            List<GroovyRowResult> lstUserRole = (List<GroovyRowResult>) serviceReturn.userRoleList
            int count = (int) serviceReturn.count
            result.put(LST_USER_ROLE, lstUserRole)
            result.put(Tools.COUNT, count)
            result.put(ROLE_OBJ, role)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap User-Role mapping object(s) to show on grid
     * @param obj -a map contains list of User-Role mapping object(s) and count
     * @return -wrapped list of User-Role mapping object(s)
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            Role role = (Role) executeResult.get(ROLE_OBJ)
            List<GroovyRowResult> lstUserRole = (List<GroovyRowResult>) executeResult.get(LST_USER_ROLE)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List resultUserRoleList = wrapListInGridEntityList(lstUserRole, start)
            Map gridObj = [page: pageNumber, total: count, rows: resultUserRoleList]
            List<GroovyRowResult> lstAppUser = listAppUserForDropDown(role.id)
            lstAppUser = Tools.listForKendoDropdown(lstAppUser, null, null)
            result.put(GRID_OBJ, gridObj)
            result.put(ROLE_ID, role.id)
            result.put(ROLE_NAME, role.name)
            result.put(LST_APP_USER, lstAppUser)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * @param userRoleList -list of User-Role mapped objects
     * @param start -start index
     * @return -wrappedUserRoleMapped Object list
     */
    private List wrapListInGridEntityList(List<GroovyRowResult> userRoleList, int start) {
        List userRoles = [] as List
        try {
            int counter = start + 1
            for (int i = 0; i < userRoleList.size(); i++) {
                GroovyRowResult userRole = userRoleList[i]
                GridEntity obj = new GridEntity()
                obj.id = userRole.user_id
                obj.cell = [counter, userRole.user_id, userRole.role_id, userRole.user]
                userRoles << obj
                counter++
            }
            return userRoles
        } catch (Exception ex) {
            log.error(ex.getMessage())
            userRoles = []
            return userRoles
        }
    }

    private static final String QUERY_FOR_LIST = """
        SELECT user_role.user_id, app_user.username as user, user_role.role_id
        FROM user_role
        LEFT JOIN app_user ON app_user.id = user_role.user_id
        WHERE app_user.enabled = true AND
        app_user.company_id = :companyId AND
        user_role.role_id = :roleId
        ORDER BY app_user.username
        LIMIT :resultPerPage OFFSET :start
    """

    private static final String QUERY_FOR_COUNT = """
        SELECT COUNT(user_role.user_id)
        FROM user_role
        LEFT JOIN app_user ON app_user.id = user_role.user_id
        WHERE app_user.enabled = true AND
        app_user.company_id = :companyId AND
        user_role.role_id = :roleId
    """

    /**
     * get list of User-Role mapped object(s) to show on grid
     * @return -a map contains list of userRole mapped object(s) and count
     */
    private LinkedHashMap list(Role role) {
        Map queryParams = [
                roleId: role.id,
                companyId: appSessionUtil.getCompanyId(),
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstUserRole = executeSelectSql(QUERY_FOR_LIST, queryParams)
        List<GroovyRowResult> resultCount = executeSelectSql(QUERY_FOR_COUNT, queryParams)
        int count = (int) resultCount[0][0]
        return [userRoleList: lstUserRole, count: count]
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
