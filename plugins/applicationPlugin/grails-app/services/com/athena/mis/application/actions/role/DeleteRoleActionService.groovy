package com.athena.mis.application.actions.role

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Role
import com.athena.mis.application.entity.UserRole
import com.athena.mis.application.service.RoleService
import com.athena.mis.application.utility.RoleCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete role object from DB as well as from cache
 *  For details go through Use-Case doc named 'DeleteRoleActionService'
 */
class DeleteRoleActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    RoleService roleService
    @Autowired
    RoleCacheUtility roleCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String RESERVED_ROLE_MESSAGE = "Selected role is reserved and can not be deleted"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete role"
    private static final String ENTITY_NOT_FOUND_ERROR_MESSAGE = "No entity found with this id or might have been deleted by someone"
    private static final String DELETE_ROLE_SUCCESS_MESSAGE = "Role has been deleted successfully"
    private static final String DELETE_ROLE_FAILURE_MESSAGE = "Role could not be deleted, Please refresh the Role List"
    private static final String HAS_ASSOCIATION_MESSAGE_USER_ROLE = " user is associated with selected role"

    /**
     * Check different criteria to delete role object
     *      1) Check access permission to delete role
     *      2) Check existence of role object
     *      3) Check if role is reserved or not
     *      4) check association with UserRole domain
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            //Only admin can delete role object
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters

            long roleId = Long.parseLong(params.id.toString())
            Role role = (Role) roleCacheUtility.read(roleId)
            if (!role) { //check existence of role object
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            if (role.roleTypeId < 0) { // check if reserved role
                result.put(Tools.MESSAGE, RESERVED_ROLE_MESSAGE)
                return result
            }

            //check association with UserRole
            int countUserRole = UserRole.countByRole(role)
            if (countUserRole > 0) {
                result.put(Tools.MESSAGE, countUserRole + HAS_ASSOCIATION_MESSAGE_USER_ROLE)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Delete role object from DB & cache.
     * Also UPDATE configAttribute of common requestMap(s) e.g :root, logout, manage password, & change password
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -Boolean value (true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long roleId = Long.parseLong(parameterMap.id.toString())
            Role role = (Role) roleCacheUtility.read(roleId)
            roleService.delete(roleId) //delete from DB
            roleCacheUtility.delete(role.id) //delete from cache
            //UPDATE configAttribute of common requestMap(s) e.g :root, logout, manage password, & change password
            updateRequestMap(role)
            return Boolean.TRUE
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete role')
            return Boolean.FALSE
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * contains delete success message to show on UI
     * @param obj -N/A
     * @return -a map contains boolean value(true) and delete success message to show on UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return [deleted: Boolean.TRUE.booleanValue(), message: DELETE_ROLE_SUCCESS_MESSAGE]
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
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_ROLE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_ROLE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * remove role from common request maps
     * @param role -Role object
     * @return -boolean value
     */
    private boolean updateRequestMap(Role role) {
        String queryStr = """
            UPDATE request_map
                SET config_attribute =
            CASE WHEN config_attribute LIKE '%,${role.authority},%' THEN
                REPLACE(config_attribute, ',${role.authority},' , ',')
            WHEN config_attribute LIKE '%,${role.authority}' THEN
                REPLACE(config_attribute, ',${role.authority}' , '')
                ELSE config_attribute
                END
        """
        int updateCount = executeUpdateSql(queryStr)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while deleting role from common requestMap')
        }
        return true;
    }
}
