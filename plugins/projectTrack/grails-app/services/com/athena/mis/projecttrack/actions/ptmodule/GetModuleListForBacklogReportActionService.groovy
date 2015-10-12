package com.athena.mis.projecttrack.actions.ptmodule

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class GetModuleListForBacklogReportActionService extends BaseService implements ActionIntf {

    @Autowired
    PtSessionUtil ptSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String SERVER_ERROR_MESSAGE = "Fail to load module List"
    private static final String DEFAULT_ERROR_MESSAGE = "Can't Load module List"
    private static final String MODULE_LIST = "moduleList"
    private static final String PROJECT_ID = "projectId"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred for invalid input"

    /**
     * Get project id from params
     * 1. Check parameters for INPUT_VALIDATION_ERROR
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // Checking parameterMap input validation error
            if (!parameterMap.projectId) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            result.put(PROJECT_ID, projectId.toLong())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
     * Get module List
     * @param parameters - N/A
     * @param obj - get a map from executePreCondition method
     * @return - a map with item list & isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            long projectId = (long) preResult.get(PROJECT_ID)
            List<GroovyRowResult> moduleList = moduleList(projectId)
            result.put(MODULE_LIST, moduleList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Show module list on Gird of UI
     * @param obj - get a map from execute method with module list
     * @return - a map with module list & isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            List<GroovyRowResult> moduleList = (List<GroovyRowResult>) receiveResult.get(MODULE_LIST)
            moduleList = Tools.listForKendoDropdown(moduleList, null, 'ALL')
            result.put(MODULE_LIST, moduleList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!receiveResult.message) {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
                return result
            }
            result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    private static final String SELECT_QUERY = """
        SELECT module.id, module.name
        FROM pt_module module
            LEFT JOIN pt_project_module pm ON module.id = pm.module_id
        WHERE pm.project_id = :projectId
            AND module.company_id = :companyId
        ORDER BY name
    """

    private List<GroovyRowResult> moduleList(long projectId) {
        Map queryParams = [
                projectId: projectId,
                companyId: ptSessionUtil.appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> backlogList = executeSelectSql(SELECT_QUERY, queryParams)
        return backlogList
    }
}
