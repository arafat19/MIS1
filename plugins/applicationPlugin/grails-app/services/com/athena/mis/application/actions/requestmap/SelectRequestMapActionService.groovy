package com.athena.mis.application.actions.requestmap

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Role
import com.athena.mis.application.utility.RoleCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select request map object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectRequestMapActionService'
 */
class SelectRequestMapActionService extends BaseService implements ActionIntf {

    private static final String SERVER_ERROR_MESSAGE = "Internal Server Error"
    private static final String MODULE_NOT_FOUND = "Select a Module"
    private static final String LST_ASSIGNED_FEATURES = "lstAssignedFeatures"
    private static final String LST_AVAILABLE_FEATURES = "lstAvailableFeatures"

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    RoleCacheUtility roleCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    /**
     * Check if user has access to update request map or not
     * @param parameters - N/A
     * @param obj - N/A
     * @return -  a map contains hasAccess(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            // only development admin role type user can show request map
            if (appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            return result
        }
    }

    /**
     * Get list of assigned & available features
     * 1. Get role object
     * 2. Check required parameters
     * @param parameters - parameters from UI
     * @param obj - N/A
     * @return - a map containing list of assigned, available features, isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            Long roleId = Long.parseLong(parameterMap.roleId.toString())
            Role role = (Role) roleCacheUtility.read(roleId)
            // check required parameters
            if (!parameterMap.pluginId) {
                result.put(Tools.MESSAGE, MODULE_NOT_FOUND)
                return result
            }

            int pluginId = Integer.parseInt(parameterMap.pluginId.toString())

            // return assigned feature names of given role name
            List lstAssignedFeatures = getAssignedFeatureByRole(role.authority, pluginId)

            // return available feature names of given role name
            List lstAvailableFeatures = getAvailableFeatureByRole(role.authority, pluginId)

            result.put(LST_ASSIGNED_FEATURES, lstAssignedFeatures)
            result.put(LST_AVAILABLE_FEATURES, lstAvailableFeatures)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            log.error(ex.getMessage())
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
     * Build a map with request map object & other necessary properties to show on UI
     * @param obj - map returned from execute method
     * @return - a map containing all objects necessary for show
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            result.put(LST_ASSIGNED_FEATURES, receivedResult.get(LST_ASSIGNED_FEATURES))
            result.put(LST_AVAILABLE_FEATURES, receivedResult.get(LST_AVAILABLE_FEATURES))
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            log.error(ex.getMessage())
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
                Map previousResult = (Map) obj
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Get list of assigned features
     * @param roleAuthority - role authority is a string comes from caller method
     * @param pluginId - plugin id
     * @return - a list of assigned features
     */
    private List<GroovyRowResult> getAssignedFeatureByRole(String roleAuthority, int pluginId) {
        String queryStr = """
            SELECT id , feature_name
            FROM request_map
                WHERE
                id NOT IN (${roleTypeCacheUtility.REQUEST_MAP_ROOT},${roleTypeCacheUtility.REQUEST_MAP_LOGOUT})
                AND(
                config_attribute LIKE '%,${roleAuthority},%'
                OR
                config_attribute LIKE '${roleAuthority},%'
                OR
                config_attribute LIKE '%,${roleAuthority}'
                OR
                config_attribute = '${roleAuthority}'
                )
                AND plugin_id = ${pluginId}
                AND feature_name IS NOT NULL
            """

        List<GroovyRowResult> lstAssignedFeatures = executeSelectSql(queryStr)
        return lstAssignedFeatures
    }

    /**
     * Get list of available features
     * @param roleAuthority - role authority is a string comes from caller method
     * @param pluginId - plugin id
     * @return - a list of available features
     */
    private List<GroovyRowResult> getAvailableFeatureByRole(String roleAuthority, int pluginId) {
        String queryStr = """
            SELECT id, feature_name
            FROM request_map
            WHERE id NOT IN (
                SELECT id
                FROM request_map
                WHERE
                config_attribute LIKE '%,${roleAuthority},%'
                OR
                config_attribute LIKE '${roleAuthority},%'
                OR
                config_attribute LIKE '%,${roleAuthority}'
                OR
                config_attribute = '${roleAuthority}'
            )
            AND plugin_id = ${pluginId}
            AND feature_name IS NOT NULL
        """

        List<GroovyRowResult> lstAvailableFeatures = executeSelectSql(queryStr)
        return lstAvailableFeatures
    }
}
