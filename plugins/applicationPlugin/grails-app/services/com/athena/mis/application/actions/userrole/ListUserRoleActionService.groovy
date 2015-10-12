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
 *  Class to get list of User-Role mapping object(s) to show on grid
 *  For details go through Use-Case doc named 'ListUserRoleActionService'
 */
class ListUserRoleActionService extends BaseService implements ActionIntf {

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleCacheUtility roleCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to get User-Role list"
    private static final String ROLE_OBJ = "roleObj"
    private static final String ROLE_NOT_FOUND = "Selected role not found"

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
    @Transactional
    public Object execute(Object params, Object obj) {
        try {
            initPager(params)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Role role = (Role) preResult.get(ROLE_OBJ)
            Map serviceReturn = list(role)
            return serviceReturn
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
        try {
            Map executeResult = (Map) obj
            List<GroovyRowResult> userRoleList = (List<GroovyRowResult>) executeResult.userRoleList
            int count = (int) executeResult.count
            List wrappedUserRole = wrapListInGridEntityList(userRoleList, start)
            return [page: pageNumber, total: count, rows: wrappedUserRole]
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return [page: pageNumber, total: 0, rows: null]
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
        int counter = start + 1
        for (int i = 0; i < userRoleList.size(); i++) {
            GroovyRowResult userRole = userRoleList[i]
            GridEntity obj = new GridEntity()
            obj.id = userRole.user_id
            obj.cell = [counter, userRole.user_id, userRole.role_id, userRole.user]
            userRoles << obj
            counter++;
        }
        return userRoles
    }

    private static final String QUERY_FOR_LIST = """
        SELECT user_role.user_id, app_user.username as user, user_role.role_id
        FROM user_role
        LEFT JOIN app_user ON app_user.id = user_role.user_id
        LEFT JOIN role ON role.id = user_role.role_id
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
}
