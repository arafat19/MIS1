package com.athena.mis.application.actions.requestmap

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Role
import com.athena.mis.application.service.RequestMapService
import com.athena.mis.application.utility.RoleCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update request map object
 *  For details go through Use-Case doc named 'UpdateRequestMapActionService'
 */
class UpdateRequestMapActionService extends BaseService implements ActionIntf {

    private static String REQUEST_MAP_UPDATE_FAILURE_MESSAGE = "Request map could not be updated"
    private static String REQUEST_MAP_UPDATE_SUCCESS_MESSAGE = "Request map has been updated successfully"
    private static String ERROR_FOR_INVALID_INPUT = "Error occurred for invalid input"

    private final Logger log = Logger.getLogger(getClass())

    RequestMapService requestMapService
    SpringSecurityService springSecurityService
    @Autowired
    RoleCacheUtility roleCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * Check pre conditions before updating request map
     * 1. Check if user has access to update request map or not
     * 2. Check required params
     * @param parameters - parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for execute
     * map contains isError(true/false) and hasAccess(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)

            // only development admin role type user can update request map
            if (appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            } else {
                return result
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters
            // check required params
            if (!params.roleId) {
                result.put(Tools.MESSAGE, ERROR_FOR_INVALID_INPUT)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.MESSAGE, REQUEST_MAP_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update request map object in DB and clear cache of request map by spring security service
     * 1. Get role object
     * 2. This function is in transactional block and will roll back in case of any exception
     * @param parameters - parameters from UI
     * @param obj - N/A
     * @return - a boolean value get from update
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            int pluginId = Integer.parseInt(params.pluginId.toString())
            List<Long> requestMapIds = []

            if (params.assignedFeatureIds.toString().length() > 0) {
                List<String> lstTemp = params.assignedFeatureIds.split(Tools.UNDERSCORE)
                for (int i = 0; i < lstTemp.size(); i++) {
                    requestMapIds << lstTemp[i].toLong()
                }
            }
            Role role = (Role) roleCacheUtility.read(params.roleId.toLong())
            boolean success = requestMapService.update(requestMapIds, role.authority, pluginId)
            // to clear cache in spring security service
            springSecurityService.clearCachedRequestmaps()
            return new Boolean(success)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return Boolean.FALSE
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show success message
     * @param obj - N/A
     * @return - a map contains isError(true/false) & success or failure message depending on method success or failure
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, REQUEST_MAP_UPDATE_SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REQUEST_MAP_UPDATE_FAILURE_MESSAGE)
            return result;
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
            result.put(Tools.MESSAGE, REQUEST_MAP_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REQUEST_MAP_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
}
