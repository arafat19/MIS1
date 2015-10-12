package com.athena.mis.application.actions.requestmap

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.service.RequestMapService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Reset request map with default value of individual plugin
 * For details go through Use-Case doc named 'ResetRequestMapByPluginIdActionService'
 */
class ResetRequestMapByPluginIdActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    RequestMapService requestMapService
    SpringSecurityService springSecurityService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String ERROR_MESSAGE = "Failed to reset request map"
    private static final String SUCCESS_MESSAGE = "Request Map successfully reset to default"

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
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * Reset default request map by company id and plugin id
     * 1. This method is in transactional block and will roll back in case of any exception
     * @param params - parameters from UI
     * @param obj - N/A
     * @return - a boolean value
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long pluginId = Long.parseLong(parameterMap.pluginId.toString())
            long companyId = appSessionUtil.getCompanyId()
            boolean success = requestMapService.resetDefaultRequestMapsByPluginId(companyId, pluginId)
            if (success) {
                springSecurityService.clearCachedRequestmaps()
            }
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
     * @return -  a map containing isError(false) and success message
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
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
                result.put(Tools.MESSAGE, ERROR_MESSAGE)
            }
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }
}
