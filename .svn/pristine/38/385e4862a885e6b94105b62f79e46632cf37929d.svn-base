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
 *  Class to create role object and to show on grid list
 *  For details go through Use-Case doc named 'CreateRoleActionService'
 */
class CreateRoleActionService extends BaseService implements ActionIntf {

    private static final String ROLE_SAVE_SUCCESS_MESSAGE = "Role has been saved successfully"
    private static final String ROLE_SAVE_FAILURE_MESSAGE = "Role could not be saved"
    private static final String ROLE_NAME_DUPLICATE = "Role name already exists"
    private static final String ROLE = "role"
    private static final String AUTHORITY_PREFIX = "ROLE_"

    private Logger log = Logger.getLogger(getClass())

    RoleService roleService
    @Autowired
    RoleCacheUtility roleCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * Check different criteria for creating new role
     *      1) Check access permission to create role
     *      2) Check duplicate role name
     * @param params -serialized parameters send from UI
     * @param obj -N/A
     * @return -a map containing role object for execute method
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)

            //Only admin can create role object
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            Role role = buildRoleObject(parameterMap) //build role object

            // check unique role name
            int countDuplicate = roleCacheUtility.countByNameIlike(role.name)
            if (countDuplicate > 0) {
                result.put(Tools.MESSAGE, ROLE_NAME_DUPLICATE)
                return result
            }

            result.put(ROLE, role)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * Save role object in DB as well as in cache;
     * Also UPDATE configAttribute of common requestMap(s) e.g :root, logout, manage password, & change password
     *
     * @param parameters -N/A
     * @param obj -roleObject send from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            Map preResult = (Map) obj
            Role role = (Role) preResult.get(ROLE)
            roleService.create(role)//save in DB
            //save in cache and keep the data sorted
            roleCacheUtility.add(role, roleCacheUtility.NAME, roleCacheUtility.SORT_ORDER_ASCENDING)
            //UPDATE configAttribute of common requestMap(s) e.g :root, logout, manage password, & change password
            updateApplicationRequestMap(role.authority)
            return role
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ROLE_SAVE_FAILURE_MESSAGE)
            return null
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap newly created role to show on grid
     * @param obj -newly created role object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Role roleInstance = (Role) obj
            GridEntity object = new GridEntity()
            object.id = roleInstance.id
            object.cell = [
                    Tools.LABEL_NEW,
                    roleInstance.id,
                    roleInstance.name
            ]
            Map resultMap = [entity: object, version: roleInstance.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, ROLE_SAVE_SUCCESS_MESSAGE)
            result.put(ROLE, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ROLE_SAVE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, ROLE_SAVE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ROLE_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    private static final String QUERY_SELECT_NEXTVAL_SEQUENCE = "SELECT NEXTVAL('role_id_seq') as id"

    /**
     * get latest role_id from role_id_sequence to generate code
     * @return -long value
     */
    private long getRoleId() {
        List results = executeSelectSql(QUERY_SELECT_NEXTVAL_SEQUENCE)
        long roleId = results[0].id
        return roleId
    }

    /**
     * build role object to create
     * @param parameterMap -GrailsParameterMap
     * @return -role object
     */
    private Role buildRoleObject(GrailsParameterMap parameterMap) {
        Role role = new Role(parameterMap)
        role.id = getRoleId()//get latest role_id from role_id_sequence
        role.version = 0L
        role.companyId = appSessionUtil.getCompanyId()
        role.authority = AUTHORITY_PREFIX + role.id.toString() + Tools.UNDERSCORE + role.companyId
        role.roleTypeId = 0L
        return role
    }

    /**
     * Set access in root, logout, manage password, & change password pages for newly created role
     * @param role -Role object
     * @return -boolean value
     */
    private boolean updateApplicationRequestMap(String role) {
        String updateQuery = """
           UPDATE request_map
           SET
           config_attribute = config_attribute || ',${role}'
           WHERE
           is_common = true;
        """
        int updateCount = executeUpdateSql(updateQuery)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating common requestMap')
        }
        return true
    }
}
