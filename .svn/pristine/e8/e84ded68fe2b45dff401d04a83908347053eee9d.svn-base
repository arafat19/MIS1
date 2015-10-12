package com.athena.mis.projecttrack.actions.ptproject

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.entity.PtProject
import com.athena.mis.projecttrack.service.PtProjectService
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.PtProjectCacheUtility
import com.athena.mis.projecttrack.utility.PtProjectModuleCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Delete Project object from DB and cache utility
 *  For details go through Use-Case doc named 'DeletePtProjectActionService'
 */
class DeletePtProjectActionService extends BaseService implements ActionIntf {

    PtProjectService ptProjectService
    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility
    @Autowired
    PtProjectModuleCacheUtility ptProjectModuleCacheUtility
    PtSprintService ptSprintService

    private final Logger log = Logger.getLogger(getClass())

    private static final String HAS_ASSOCIATION_PROJECT_MODULE_MESSAGE = " module(s) associated with this project"
    private static final String HAS_ASSOCIATION_SPRINT_MESSAGE = " sprint associated with this project"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete project"
    private static final String PROJECT_DELETE_SUCCESS_MSG = "Project has been successfully deleted"

    /**
     * Checking pre condition before deleting the project object
     * 1. Check validity for input
     * 2. Check existence of project object
     * 3. Check association of PtProject
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long projectId = Long.parseLong(params.id.toString())
            PtProject oldProject = (PtProject) ptProjectCacheUtility.read(projectId) // Get project object from cache utility
            if (!oldProject) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            // check association of project with relevant domains
            Map associationResult = (Map) hasAssociation(oldProject)
            Boolean hasAssociation = (Boolean) associationResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, associationResult.get(Tools.MESSAGE))
                return result
            }
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Delete Project object from DB and cache utility
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long projectId = Long.parseLong(parameterMap.id)
            ptProjectService.delete(projectId)                    // Delete Project object from DB
            ptProjectCacheUtility.delete(projectId)               // delete Project object from cache utility
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
     * Build success message for delete
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(Tools.MESSAGE, PROJECT_DELETE_SUCCESS_MSG)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Check association of Project with relevant domains
     * @param project -Project object
     * @return a map containing hasAssociation(true/false) and relevant message
     */
    private LinkedHashMap hasAssociation(PtProject project) {
        LinkedHashMap result = new LinkedHashMap()
        long projectId = project.id
        int count = 0
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        count = ptProjectModuleCacheUtility.countByProjectId(projectId)
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_PROJECT_MODULE_MESSAGE)
            return result
        }
        count = ptSprintService.countByProjectId(projectId)
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_SPRINT_MESSAGE)
            return result
        }
        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }


}
