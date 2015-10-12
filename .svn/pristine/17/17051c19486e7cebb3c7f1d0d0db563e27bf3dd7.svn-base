package com.athena.mis.projecttrack.actions.ptbacklog

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class GetBackLogForModuleActionService extends BaseService implements ActionIntf {

    @Autowired
    PtSessionUtil ptSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String SERVER_ERROR_MESSAGE = "Fail to load backlog List"
    private static final String DEFAULT_ERROR_MESSAGE = "Can't Load backlog List"
    private static final String BACKLOG_LIST = "backlogList"
    private static final String MODULE_ID = "moduleId"
    private static final String STR_IDEA = "idea"
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
            if (!parameterMap.moduleId) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            long moduleId = Long.parseLong(parameterMap.moduleId.toString())
            result.put(MODULE_ID, moduleId)
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
            long moduleId = (long) preResult.get(MODULE_ID)
            List<GroovyRowResult> backlogList = backlogList(moduleId)
            result.put(BACKLOG_LIST, backlogList)
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
            List<GroovyRowResult> backlogList = (List<GroovyRowResult>) receiveResult.get(BACKLOG_LIST)
            result.put(BACKLOG_LIST, Tools.listForKendoDropdown(backlogList, STR_IDEA, null))
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
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
        SELECT  bl.id AS id,  bl.idea AS idea
        FROM pt_backlog bl
        WHERE bl.module_id = :moduleId
            AND bl.company_id = :companyId
            AND bl.sprint_id = 0
        ORDER BY idea
    """
    /**
     * get backlog list for sprint by moduleId and companyId
     * @param moduleId - module id
     * @return -backlog List
     */
    private List<GroovyRowResult> backlogList(long moduleId) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        Map queryParams = [
                moduleId: moduleId,
                companyId: companyId
        ]
        List<GroovyRowResult> backlogList = executeSelectSql(SELECT_QUERY, queryParams)
        return backlogList
    }

}
