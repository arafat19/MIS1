package com.athena.mis.projecttrack.actions.ptproject

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.entity.PtProject
import com.athena.mis.projecttrack.utility.PtProjectCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select Project object
 *  For details go through Use-Case doc named 'SelectPtProjectActionService'
 */
class SelectPtProjectActionService extends BaseService implements ActionIntf {

    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String PROJECT_NOT_FOUND_MESSAGE = "Selected project is not found"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to select project"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get parameters from UI and build Project object for select
     * 1. Check validity for input
     * 2. Check existence of Project object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long projectId = Long.parseLong(parameterMap.id.toString())
            PtProject project = (PtProject) ptProjectCacheUtility.read(projectId)
            // Get Project object from cache utility
            if (!project) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND_MESSAGE)
                return result
            }
            result.put(Tools.ENTITY, project)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_NOT_FOUND_MESSAGE)
            return result
        }
    }
    /**
     * Build a map with Project object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            PtProject project = (PtProject) executeResult.get(Tools.ENTITY)
            result.put(Tools.ENTITY, project)
            result.put(Tools.VERSION, project.version.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
            if (!obj) {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            Map previousResult = (Map) obj
            result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
}
