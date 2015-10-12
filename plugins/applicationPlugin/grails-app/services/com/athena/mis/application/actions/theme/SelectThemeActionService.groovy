package com.athena.mis.application.actions.theme

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.ThemeCacheUtility
import com.athena.mis.application.entity.Theme
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select object specific theme while selecting grid row
 *  For details go through Use-Case doc named 'SelectThemeActionService'
 */
class SelectThemeActionService extends BaseService implements ActionIntf {

    private static final String NOT_FOUND_MASSAGE = "Selected theme information not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select theme information"
    private static final String NOT_DEVELOPMENT_USER = "You are not allowed to select theme"
    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    ThemeCacheUtility themeCacheUtility

    /**
     * Check the access of Development user
     * @param parameters - N/A
     * @param obj - N/A
     * @return - map contains hasAccess(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            if (!appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                result.put(Tools.MESSAGE, NOT_DEVELOPMENT_USER)
                return result
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * Get theme details of selected theme
     * 1. Check the existence of theme object
     * @param parameters - get params from UI
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            Integer themeId = Integer.parseInt(parameterMap.id.toString())
            Theme theme = themeCacheUtility.findById(themeId)
            if (!theme) {
                result.put(Tools.MESSAGE, NOT_FOUND_MASSAGE)
            }
            result.put(Tools.ENTITY, theme)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, NOT_FOUND_MASSAGE)
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
     * Build a map with theme object to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap executeResult = (LinkedHashMap) obj

            Theme theme = (Theme) executeResult.get(Tools.ENTITY)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.ENTITY, theme)
            result.put(Tools.VERSION, theme.version)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
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
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            }
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
}

