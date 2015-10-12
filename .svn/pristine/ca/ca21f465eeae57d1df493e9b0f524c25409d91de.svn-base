package com.athena.mis.application.actions.appshellscript

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppShellScript
import com.athena.mis.application.service.AppShellScriptService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete shell script object from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAppShellScriptActionService'
 */
class DeleteAppShellScriptActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass());

    private static final String DELETE_SUCCESS_MSG = "Shell Script has been successfully deleted!"
    private static final String DELETE_FAILURE_MSG = "Shell Script has not been deleted."
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete Shell Script"
    private static final String OBJ_NOT_FOUND = "Shell Script not found"
    private static final String SHELL_SCRIPT = 'shellScript'

    AppShellScriptService appShellScriptService

    /*
    * @params parameters - Serialize parameters from UI
    * @params obj - N/A
    * Check for invalid input, object
    * @return - A map of containing Shell Script object or error messages
    * */

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap grailsParameterMap = (GrailsParameterMap) parameters
            if (!grailsParameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long id = Long.parseLong(grailsParameterMap.id.toString())
            AppShellScript appShellScript = appShellScriptService.read(id)
            if (!appShellScript) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            result.put(SHELL_SCRIPT, appShellScript)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post - operation
        return null
    }

    /**
     * Delete Shell Script object in DB
     * This function is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for build result
     */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            AppShellScript appShellScript = (AppShellScript) preResult.get(SHELL_SCRIPT)
            long id = appShellScript.id
            appShellScriptService.delete(id)
            result.put(SHELL_SCRIPT, appShellScript)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(DELETE_FAILURE_MSG)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * @params obj - map from execute method
    * Show success message
    * @return - A map containing all necessary object for show
    * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(Tools.DELETED, Boolean.TRUE)
        result.put(Tools.MESSAGE, DELETE_SUCCESS_MSG)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
    }

    /*
   * Build Failure result in case of any error
   * @params obj - A map from execute method
   * @return - A map containing all necessary message for show
   * */

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()

        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj
            if (obj) {
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                } else {
                    result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
                }
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }
}
