package com.athena.mis.projecttrack.actions.ptbacklog

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.entity.PtModule
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.PtBacklogPriorityCacheUtility
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select ptBacklog object and show in UI for MyBacklog
 *  For details go through Use-Case doc named 'SelectMyPtBacklogActionService'
 */
class SelectMyPtBacklogActionService extends BaseService implements ActionIntf {

    PtSprintService ptSprintService
    PtBacklogService ptBacklogService
    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility
    @Autowired
    PtBacklogPriorityCacheUtility ptBacklogPriorityCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String BACKLOG_NOT_FOUND_MASSAGE = "Selected backlog is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select backlog"
    private static final String MODULE = "module"
    private static final String SPRINT = "sprint"
    private static final String PRIORITY = "priority"

    /**
     * Check if PtBacklog.id exists in parameterMap
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BACKLOG_NOT_FOUND_MASSAGE)
            return result
        }
    }

    /**
     * Get ptBacklog object by id
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long backlogId = Long.parseLong(parameterMap.id.toString())
            PtBacklog backlog = (PtBacklog) ptBacklogService.read(backlogId)    // get backlog object
            // check whether the ptBacklog object exists or not
            if (!backlog) {
                result.put(Tools.MESSAGE, BACKLOG_NOT_FOUND_MASSAGE)
                return result
            }
            PtModule module = (PtModule) ptModuleCacheUtility.read(backlog.moduleId)
            PtSprint sprint = ptSprintService.read(backlog.sprintId)
            String sprintName = (sprint != null) ? sprint.name : Tools.EMPTY_SPACE
            SystemEntity priority = (SystemEntity) ptBacklogPriorityCacheUtility.read(backlog.priorityId)
            result.put(PRIORITY, priority.value)
            result.put(MODULE, module.name)
            result.put(SPRINT, sprintName)
            result.put(Tools.ENTITY, backlog)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BACKLOG_NOT_FOUND_MASSAGE)
            return result
        }
    }

    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }

    /**
     * Build a map with ptBacklog object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            PtBacklog backlog = (PtBacklog) executeResult.get(Tools.ENTITY)
            String moduleName = (String) executeResult.get(MODULE)
            String sprintName = (String) executeResult.get(SPRINT)
            String priority = (String) executeResult.get(PRIORITY)
            result.put(MODULE, moduleName)
            result.put(SPRINT, sprintName)
            result.put(PRIORITY, priority)
            result.put(Tools.ENTITY, backlog)
            result.put(Tools.VERSION, backlog.version.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
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
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
                return result
            }
            Map previousResult = (Map) obj  // cast map returned from previous method
            result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

}
