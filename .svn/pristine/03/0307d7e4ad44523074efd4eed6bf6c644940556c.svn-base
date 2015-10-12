package com.athena.mis.application.actions.appshellscript

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppShellScript
import com.athena.mis.application.service.AppShellScriptService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of shell script for grid
 *  For details go through Use-Case doc named 'ListAppShellScriptActionService'
 */
class ListAppShellScriptActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load Shell Script list"
    private static final String LST_SHELL_SCRIPT = "lstShellScript"
    private static final String GRID_OBJ = "gridObj"

    AppShellScriptService appShellScriptService

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get the list of shellScript object
     * @param params - serialize parameters from UI
     * @param obj - N/A
     * @return result - A map of containing all object necessary for buildSuccessResultForUI
     */

    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {

            GrailsParameterMap parameters = (GrailsParameterMap) params
            initPager(parameters)
            Map mapResult = appShellScriptService.list()
            result.put(LST_SHELL_SCRIPT, mapResult.lstShellScript)
            result.put(Tools.COUNT, mapResult.count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap list of shellScript for grid
     * @param obj - a map returned from execute method
     * @return result - a map containing necessary information for show page
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            List<AppShellScript> lstShellScript = (List<AppShellScript>) executeResult.get(LST_SHELL_SCRIPT)
            int total = (int) executeResult.get(Tools.COUNT)
            List<AppShellScript> lstWrapShellScript = wrapShellScriptList(lstShellScript, start)
            Map gridObject = [page: pageNumber, total: total, rows: lstWrapShellScript]
            result.put(GRID_OBJ, gridObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (preResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list in grid entity list
     * @param lstShellScript - list of Shell Script
     * @param start - starting index of the page
     * @return lstWrapShellScript - list of wrap Shell Script
     */
    private List wrapShellScriptList(List<AppShellScript> lstShellScript, int start) {
        List lstWrapShellScript = []
        int counter = start + 1
        for (int i = 0; i < lstShellScript.size(); i++) {
            AppShellScript appShellScript = lstShellScript[i]
            GridEntity obj = new GridEntity()
            obj.id = appShellScript.id
            obj.cell = [
                    counter,
                    appShellScript.name,
                    Tools.makeDetailsShort(appShellScript.script, 150),
            ]
            lstWrapShellScript << obj
            counter++
        }
        return lstWrapShellScript
    }
}
