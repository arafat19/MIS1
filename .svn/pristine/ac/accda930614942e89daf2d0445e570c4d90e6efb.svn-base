package com.athena.mis.application.actions.appshellscript

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppShellScript
import com.athena.mis.application.service.AppShellScriptService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new shell script object and show in grid
 *  For details go through Use-Case doc named 'CreateAppShellScriptActionService'
 */
class CreateAppShellScriptActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String SHELL_SCRIPT = 'shellScript'
    private static final String CREATE_SUCCESS_MESSAGE = 'Shell Script has been successfully saved'
    private static final String CREATE_FAILURE_MESSAGE = 'Failed to saved Shell Script'
    private static final String NAME_MUST_BE_UNIQUE = 'Shell Script name must be unique'

    AppShellScriptService appShellScriptService

    @Autowired
    AppSessionUtil appSessionUtil


    /**
     * @params parameters - serialize parameters form UI
     * @params obj - N/A
     * Check duplicate Shell Script name
     * @return - A map of containing error message
     * */

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            int duplicateCount = AppShellScript.countByNameIlike(params.name.toString())
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, NAME_MUST_BE_UNIQUE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post - operation
        return null
    }

    /**
     * Create Shell Script object to DB
     * @params parameters - serialize parameters form UI
     * @params obj - N/A
     * @return - A map of saved Shell Script object or error messages
     * Ths method is in Transactional boundary so will rollback in case of any exception
     * */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            AppShellScript appShellScript = buildAppShellScriptObj(parameterMap)     //build ShellScript object

            appShellScriptService.create(appShellScript)
            result.put(SHELL_SCRIPT, appShellScript)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * @params obj - map from execute method
     * Show newly created Shell Script to grid
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
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
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
                    result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
                }
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /*
   * Build Shell Script object for create
   * */

    private AppShellScript buildAppShellScriptObj(GrailsParameterMap parameterMap) {
        AppUser appUser = appSessionUtil.appUser
        AppShellScript appShellScript = new AppShellScript(parameterMap)
        appShellScript.companyId = appUser.getCompanyId()
        appShellScript.createdBy = appUser.id
        appShellScript.createdOn = new Date()
        appShellScript.updatedBy = 0L
        appShellScript.updatedOn = null
        return appShellScript
    }
}
