package com.athena.mis.application.actions.theme

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.config.ThemeCacheUtility
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Theme
import com.athena.mis.application.service.ThemeService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update theme object, theme CacheUtility and grid data
 *  For details go through Use-Case doc named 'UpdateThemeActionService'
 */
class UpdateThemeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    ThemeService themeService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ThemeCacheUtility themeCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String UPDATE_FAILURE_MSG = "Theme Information could not be updated"
    private static final String UPDATE_SUCCESS_MSG = "Theme Information has been updated successfully"
    private static final String NOT_DEVELOPMENT_USER = "You are not allowed to update theme"
    private static final String THEM_NOT_FOUND = "Theme not found to be updated, refresh the page"
    private static final String THEME = "theme"
    private static final String SORT_ON_KEY = "key"

    /**
     * Get parameters from UI and build theme object for update
     * 1. Check the access of Development user
     * 2. Check the existence of old theme object
     * 3. Build new theme object
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)

            if (!appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                result.put(Tools.MESSAGE, NOT_DEVELOPMENT_USER)
                return result
            }
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long themeId = Integer.parseInt(params.id.toString())
            Theme oldTheme = themeService.read(themeId)
            if (!oldTheme) {
                result.put(Tools.MESSAGE, THEM_NOT_FOUND)
                return result
            }

            Theme theme = buildThemeObjectForUpdate(params, oldTheme) // build theme object for update

            result.put(THEME, theme)
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
     * Update theme object in DB & update cache utility
     * 1. This function is in transactional block and will roll back in case of any exception
     * @param parameters - N/A
     * @param obj - serialized parameters from UI
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Theme theme = (Theme) preResult.get(THEME)
            themeService.update(theme)
            theme.version = theme.version + 1
            themeCacheUtility.update(theme, SORT_ON_KEY, ASCENDING_SORT_ORDER)
            result.put(THEME, theme)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
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
     * Show updated theme object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {

            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            Theme theme = (Theme) executeResult.get(THEME)
            GridEntity object = new GridEntity()
            object.id = theme.id
            object.cell = [
                    Tools.LABEL_NEW,
                    theme.key,
                    theme.value,
                    Tools.makeDetailsShort(theme.description, Tools.DEFAULT_LENGTH_DETAILS_OF_AREA_DES)
            ]

            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
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
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build theme object for update
     * @param params - serialized parameters from UI
     * @param oldTheme - old theme object
     * @return - updated theme object
     */
    private Theme buildThemeObjectForUpdate(GrailsParameterMap params, Theme oldTheme) {
        Theme newTheme = new Theme(params)
        AppUser user = appSessionUtil.getAppUser()

        oldTheme.value = newTheme.value
        oldTheme.updatedOn = new Date()
        oldTheme.updatedBy = user.id
        return oldTheme
    }
}

