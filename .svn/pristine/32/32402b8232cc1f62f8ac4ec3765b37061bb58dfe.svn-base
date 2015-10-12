package com.athena.mis.projecttrack.actions.ptproject

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.entity.PtProject
import com.athena.mis.projecttrack.service.PtProjectService
import com.athena.mis.projecttrack.utility.PtProjectCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Update Project object
 *  For details go through Use-Case doc named 'UpdatePtProjectActionService'
 */
class UpdatePtProjectActionService extends BaseService implements ActionIntf {

    PtProjectService ptProjectService
    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String OBJ_NOT_FOUND = "Selected project not found"
    private final static String NAME_EXIST_MESSAGE = "Same project name already exists "
    private final static String CODE_EXIST_MESSAGE = "Same project code already exists "
    private static final String PROJECT_OBJ = "project"
    private static final String PROJECT_UPDATE_FAILURE_MESSAGE = "Project could not be updated"
    private static final String PROJECT_UPDATE_SUCCESS_MESSAGE = "Project has been updated successfully"

    /**
     * Get parameters from UI and build Project object for update
     * 1. Check validity for input
     * 2. Check existence of Project object
     * 3. Build Project object with new parameters
     * 4. Check existence of name
     * 5. Check existence of code
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long projectId = Long.parseLong(parameterMap.id.toString())
            PtProject oldProject = ptProjectService.read(projectId)    // Get Project object from cache utility
            if (!oldProject) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            PtProject project = buildProjectObject(parameterMap, oldProject)
            int countName = ptProjectCacheUtility.countByNameIlikeAndIdNotEqual(project.name, project.id)
            if (countName > 0) {
                result.put(Tools.MESSAGE, NAME_EXIST_MESSAGE)
                return result
            }
            int countCode = ptProjectCacheUtility.countByCodeAndIdNotEqual(project.code, project.id)
            if (countCode > 0) {
                result.put(Tools.MESSAGE, CODE_EXIST_MESSAGE)
                return result
            }
            result.put(PROJECT_OBJ, project)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_UPDATE_FAILURE_MESSAGE)
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
     * Update Project object in DB and cache utility
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            // cast map returned from executePreCondition method
            PtProject project = (PtProject) preResult.get(PROJECT_OBJ)
            ptProjectService.update(project)            // update new Project object in DB
            // update new Project object in cache utility
            ptProjectCacheUtility.update(project, ptProjectCacheUtility.SORT_ON_NAME, ptProjectCacheUtility.SORT_ORDER_ASCENDING)
            result.put(PROJECT_OBJ, project)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * 1. Build grid object to show a single row (updated object) in grid
     * 2. Build success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj                   // cast map returned from execute method
            PtProject project = (PtProject) executeResult.get(PROJECT_OBJ)
            GridEntity object = new GridEntity()                                // build grid object
            object.id = project.id
            object.cell = [
                    Tools.LABEL_NEW,
                    project.id,
                    project.name,
                    project.code
            ]
            result.put(Tools.MESSAGE, PROJECT_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, project.version.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_UPDATE_FAILURE_MESSAGE)
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
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PROJECT_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Make existing/old object Up-to-date with new parameters
     * @param parameterMap -serialized parameters from UI
     * @param oldProject -old Project object
     * @return -updated Project object
     */
    private PtProject buildProjectObject(GrailsParameterMap parameterMap, PtProject oldProject) {
        PtProject newProject = new PtProject(parameterMap)
        oldProject.name = newProject.name
        oldProject.code = newProject.code
        oldProject.updatedOn = new Date()
        oldProject.updatedBy = ptSessionUtil.appSessionUtil.getAppUser().id
        return oldProject
    }
}
