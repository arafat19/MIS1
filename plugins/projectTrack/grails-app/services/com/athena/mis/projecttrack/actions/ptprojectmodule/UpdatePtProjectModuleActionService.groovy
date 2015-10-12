package com.athena.mis.projecttrack.actions.ptprojectmodule

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.entity.PtModule
import com.athena.mis.projecttrack.entity.PtProjectModule
import com.athena.mis.projecttrack.service.PtProjectModuleService
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.projecttrack.utility.PtProjectModuleCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Update ptProjectModule object
 *  For details go through Use-Case doc named 'UpdatePtProjectModuleActionService'
 */
class UpdatePtProjectModuleActionService extends BaseService implements ActionIntf {


    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility
    @Autowired
    PtProjectModuleCacheUtility ptProjectModuleCacheUtility
    PtProjectModuleService ptProjectModuleService

    private final Logger log = Logger.getLogger(getClass())

    private static final String PROJECT_MODULE_UPDATE_FAILURE_MESSAGE = "Project Module could not be updated"
    private static final String PROJECT_MODULE_UPDATE_SUCCESS_MESSAGE = "Project Module has been updated successfully"
    private static final String OBJ_NOT_FOUND = "Selected project module not found"
    private static final String PROJECT_MODULE_OBJ = "projectModule"
    private static final String CANNOT_UPDATE = "Already inserted"

    /**
     * Get parameters from UI and build ptProjectModule object for update
     * 1. check if id exists in parameterMap
     * 2. check if old ptProjectModule object exists
     * 3. build ptProjectModule object with new parameters
     * 4. check existence for duplicate entry
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {

        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long projectModuleId = Long.parseLong(parameterMap.id)
            PtProjectModule oldProjectModule = (PtProjectModule) ptProjectModuleService.read(projectModuleId)
            // get ptProjectModule object from cacheUtility
            // check whether selected ptProjectModule object exists or not
            if (!oldProjectModule) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            PtProjectModule projectModule = buildProjectModuleObject(parameterMap, oldProjectModule)
            // build ptProjectModule object for update
            int count = ptProjectModuleService.countByIdNotEqualAndProjectIdAndModuleIdAndCompanyId(projectModule)
            if (count > 0) {
                // check duplicate existence
                result.put(Tools.MESSAGE, CANNOT_UPDATE)
                return result
            }
            result.put(PROJECT_MODULE_OBJ, projectModule)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_MODULE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update ptProjectModule object in DB and cacheUtility
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            PtProjectModule projectModule = (PtProjectModule) preResult.get(PROJECT_MODULE_OBJ)
            ptProjectModuleService.update(projectModule)  // update projectModule object in DB
            ptProjectModuleCacheUtility.update(projectModule, ptProjectModuleCacheUtility.SORT_ON_PROJECT_ID, ptProjectModuleCacheUtility.SORT_ORDER_ASCENDING)
            result.put(PROJECT_MODULE_OBJ, projectModule)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_MODULE_UPDATE_FAILURE_MESSAGE)
            return result
        }

    }
    /**
     * Do nothing for post condition
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }
    /**
     * Build grid object to show a single row in grid
     * 1. Get ptProject name by id(projectId)
     * 2. Get ptModule name by id(moduleId)
     * 3. Build success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            // cast map returned from execute method
            PtProjectModule projectModule = (PtProjectModule) executeResult.get(PROJECT_MODULE_OBJ)
            GridEntity object = new GridEntity()
            // build grid entity object
            PtModule module = (PtModule) ptModuleCacheUtility.read(projectModule.moduleId)
            // pull ptModule object by id
            object.id = projectModule.id
            object.cell = [
                    Tools.LABEL_NEW,
                    module.name
            ]
            result.put(Tools.MESSAGE, PROJECT_MODULE_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_MODULE_UPDATE_FAILURE_MESSAGE)
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
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PROJECT_MODULE_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_MODULE_UPDATE_FAILURE_MESSAGE)
            return result
        }

    }

    /**
     * Make existing/old object Up-to-date with new parameters
     * @param parameterMap -serialized parameters from UI
     * @param oldProjectModule -old ptProjectModule object
     * @return -updated ptProjectModule object
     */
    private PtProjectModule buildProjectModuleObject(GrailsParameterMap parameterMap, PtProjectModule oldProjectModule) {
        PtProjectModule projectModule = new PtProjectModule(parameterMap)
        oldProjectModule.moduleId = projectModule.moduleId
        return oldProjectModule
    }
}
