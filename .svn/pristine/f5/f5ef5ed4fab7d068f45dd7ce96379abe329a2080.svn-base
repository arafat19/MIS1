package com.athena.mis.application.actions.theme

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.config.ThemeCacheUtility
import com.athena.mis.application.entity.Theme
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of theme for grid
 *  For details go through Use-Case doc named 'ListThemeActionService'
 */
class ListThemeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    ThemeCacheUtility themeCacheUtility

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load theme page"
    private static final String NOT_DEVELOPMENT_USER = "You are not allowed to show theme list"
    private static final String THEME_LIST = "themeList"
    private static final String SORT_BY_KEY = "key"

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
     * Get list of theme
     * @param params - parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            initPager(parameterMap)

            int count = themeCacheUtility.count()
            List themeList = themeCacheUtility.list(this)
            result.put(THEME_LIST, themeList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
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
     * Wrap theme list for grid
     * @param obj - map returned from execute method
     * @return - a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            Map executeResult = (Map) obj
            List themeList = (List) executeResult.get(THEME_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List resultList = wrapThemeListInGridEntity(themeList, start)
            Map output = [page: pageNumber, total: count, rows: resultList]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of theme in grid entity
     * @param themeList - list of theme
     * @param start - starting index of the page
     * @return - list of wrapped theme
     */
    private List wrapThemeListInGridEntity(List<Theme> themeList, int start) {
        List lstTheme = [] as List
        int counter = start + 1
        for (int i = 0; i < themeList.size(); i++) {
            GridEntity obj = new GridEntity()
            obj.id = themeList[i].id
            obj.cell = [
                    counter,
                    themeList[i].key,
                    themeList[i].value,
                    Tools.makeDetailsShort(themeList[i].description, Tools.DEFAULT_LENGTH_DETAILS_OF_AREA_DES)
            ]
            lstTheme << obj
            counter++
        }
        return lstTheme
    }
}
