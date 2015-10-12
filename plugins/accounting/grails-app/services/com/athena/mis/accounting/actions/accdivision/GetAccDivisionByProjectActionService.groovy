package com.athena.mis.accounting.actions.accdivision

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccDivision
import com.athena.mis.accounting.utility.AccDivisionCacheUtility
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Class to get list of all active AccDivision(s) associated with specific project
 *  For details go through Use-Case doc named 'GetAccDivisionByProjectActionService'
 */
class GetAccDivisionByProjectActionService extends BaseService implements ActionIntf {

    @Autowired
    AccDivisionCacheUtility accDivisionCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String ERROR_MESSAGE = "Failed to load division list"
    private static final String ACC_DIVISION_LIST = "accDivisionList"
    private static final String PROJECT = "project"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"

    /**
     * Check different criteria to get accDivision list
     *      1) Check existence of required parameter
     *      2) Check existence of selected project
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains project object & isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            // check here the required params are present
            if (!parameterMap.projectId) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            Long projectId = Long.parseLong(parameterMap.projectId.toString())
            Project project = (Project) projectCacheUtility.read(projectId)
            if (!project) { //Check existence of project object
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            result.put(PROJECT, project)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Get list of active accDivision object(s) of a specific project
     * @param parameters -N/A
     * @param obj -Project object send from executePreCondition
     * @return -list of active accDivision object(s) and boolean value (true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Project project = (Project) preResult.get(PROJECT)
            List<AccDivision> accDivisionList = accDivisionCacheUtility.listByProjectIdAndIsActive(project.id)
            result.put(ACC_DIVISION_LIST, Tools.listForKendoDropdown(accDivisionList,null,null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    /**
     * do nothing for buildSuccessResultForUI operation
     */
    public Object buildSuccessResultForUI(Object storeResult) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError(true) & relevant error message
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
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }
}
