package com.athena.mis.application.actions.appshellscript

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppShellScript
import com.athena.mis.application.service.AppShellScriptService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update new Shell Script object and show in grid
 *  For details go through Use-Case doc named 'UpdateAppShellScriptActionService'
 */
class UpdateAppShellScriptActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String SHELL_SCRIPT = 'shellScript'
    private static final String UPDATE_SUCCESS_MESSAGE = 'Shell Script has been successfully updated'
    private static final String UPDATE_FAILURE_MESSAGE = 'Failed to saved Shell Script'
    private static final String NAME_MUST_BE_UNIQUE = 'Shell Script name must be unique'
    private static final String OBJ_NOT_FOUND = "Shell Script not found"

    AppShellScriptService appShellScriptService

    @Autowired
    AppSessionUtil appSessionUtil

    /*
    * @params parameters - Serialize parameters from UI
    * @params obj - N/A
    * Check for invalid input, object
    * Build new Shell Script
    * check for duplicate name
    * @return - A map on containing new Shell Script object or error messages
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
            AppShellScript oldAppShellScript = appShellScriptService.read(id)
            if (!oldAppShellScript) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            AppShellScript newAppShellScript = buildAppShellScript(grailsParameterMap, oldAppShellScript)
            int duplicateCount = AppShellScript.countByNameIlikeAndIdNotEqual(newAppShellScript.name, newAppShellScript.id)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, NAME_MUST_BE_UNIQUE)
                return result
            }

            result.put(SHELL_SCRIPT, newAppShellScript)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        //Do Nothing ofr post - operation
        return null
    }

    /**
     * Update Shell Script object in DB
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
            appShellScriptService.update(appShellScript)
            result.put(SHELL_SCRIPT, appShellScript)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)

            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * @params obj - map from execute method
    * Show newly updated Shell Script to grid
    * Show success message
    * @return - A map containing all necessary object for show
    * */

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            AppShellScript appShellScript = (AppShellScript) preResult.get(SHELL_SCRIPT)
            GridEntity object = new GridEntity()
            object.id = appShellScript.id
            object.cell = [
                    Tools.LABEL_NEW,
                    appShellScript.name,
                    Tools.makeDetailsShort(appShellScript.script, 150),
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, appShellScript.version)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
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
                    result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
                }
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
    * Build AppShellScript object for update
    * */

    private AppShellScript buildAppShellScript(GrailsParameterMap parameterMap, AppShellScript oldAppShellScript) {
        AppShellScript newAppShellScript = new AppShellScript(parameterMap)
        oldAppShellScript.name = newAppShellScript.name
        oldAppShellScript.script = newAppShellScript.script
        oldAppShellScript.updatedOn = new Date()
        oldAppShellScript.updatedBy = appSessionUtil.getAppUser().id
        return oldAppShellScript
    }
}
