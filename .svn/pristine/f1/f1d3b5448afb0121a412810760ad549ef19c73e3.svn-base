package com.athena.mis.application.actions.role

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Role
import com.athena.mis.application.service.RoleService
import com.athena.mis.application.utility.RoleCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update role CRUD and to show on grid list
 *  For details go through Use-Case doc named 'UpdateRoleActionService'
 */
class UpdateRoleActionService extends BaseService implements ActionIntf {

    private static final String ROLE_UPDATE_FAILURE_MESSAGE = "Role could not be updated"
    private static final String ROLE_UPDATE_SUCCESS_MESSAGE = "Role has been updated successfully"
    private static final String RESERVED_ROLE_MESSAGE = "Selected role is reserved and can not be updated"
    private static final String ROLE_NAME_DUPLICATE = "Role name already exists"
    private static final String ROLE = "role"
    private static final String ROLE_NOT_FOUND_MESSAGE = "Role not found"

    private final Logger log = Logger.getLogger(getClass())

    RoleService roleService
    @Autowired
    RoleCacheUtility roleCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * Check different criteria for updating role object
     *      1) Check access permission to update role
     *      2) Check duplicate role name
     *      3) Check if role is reserved or not
     * @param params -parameter send from UI
     * @param obj -N/A
     * @return -a map containing role object for execute method
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)

            //Only admin can update role object
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
                return result
            }

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long roleId = Long.parseLong(parameterMap.id.toString())
            Role oldRole = roleService.read(roleId)
            if (!oldRole) { //check existence of object
                result.put(Tools.MESSAGE, ROLE_NOT_FOUND_MESSAGE)
                return result
            }

            if (oldRole.roleTypeId < 0) { // check if reserved role
                result.put(Tools.MESSAGE, RESERVED_ROLE_MESSAGE)
                return result
            }

            //build role object
            Role newRole = buildRoleObject(parameterMap, oldRole)

            //check duplicate role name
            int countDuplicate = roleCacheUtility.countByNameIlikeAndIdNotEqual(newRole.name, newRole.id)
            if (countDuplicate > 0) {
                result.put(Tools.MESSAGE, ROLE_NAME_DUPLICATE)
                return result
            }

            result.put(ROLE, newRole)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            return result
        }
    }

    /**
     * Update role object both in Db and cache
     * @param parameters -N/A
     * @param obj -roleObject from executePreCondition method
     * @return -a map contains role object for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     * @return
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Role role = (Role) preResult.get(ROLE)
            roleService.update(role)//Update in Db
            role.version = role.version + 1
            //Update in cache and keep the data sorted
            roleCacheUtility.update(role, roleCacheUtility.AUTHORITY, roleCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ROLE, role)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ROLE_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ROLE_UPDATE_FAILURE_MESSAGE)
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
     * Wrap updated role to show on grid
     * @param obj -Role object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            Role role = (Role) executeResult.get(ROLE)
            GridEntity object = new GridEntity()
            object.id = role.id
            object.cell = [Tools.LABEL_NEW, role.id, role.name, role.authority]
            Map resultMap = [entity: object, version: role.version]
            result.put(Tools.MESSAGE, ROLE_UPDATE_SUCCESS_MESSAGE)
            result.put(ROLE, resultMap)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ROLE_UPDATE_FAILURE_MESSAGE)
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
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, ROLE_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ROLE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build roleObject to update
     * @param params -GrailsParameterMap
     * @param oldRole -Role object
     * @return -Role object
     */
    private Role buildRoleObject(GrailsParameterMap parameterMap, Role oldRole) {
        Role newRole = new Role(parameterMap)
        oldRole.name = newRole.name
        return oldRole
    }
}
