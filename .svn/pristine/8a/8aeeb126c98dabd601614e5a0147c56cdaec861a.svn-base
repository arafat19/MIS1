package com.athena.mis.projecttrack.actions.ptmodule

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.entity.PtModule
import com.athena.mis.projecttrack.service.PtModuleService
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Update Module object and grid data
 *  For details go through Use-Case doc named 'UpdatePtModuleActionService'
 */
class UpdatePtModuleActionService extends BaseService implements ActionIntf {

    PtModuleService ptModuleService
    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String MODULE_UPDATE_FAILURE_MESSAGE = "Module could not be updated"
    private static final String MODULE_UPDATE_SUCCESS_MESSAGE = "Module has been updated successfully"
    private static final String OBJ_NOT_FOUND = "Selected module not found"
    private static final String MODULE_OBJ = "module"
    private final static String NAME_EXIST_MESSAGE = "Same module name already exists "
    private final static String CODE_EXIST_MESSAGE = "Same module code already exists "

    /**
     * Get parameters from UI and build Module object for update
     * 1. Check if id exists in parameterMap
     * 2. Check if old Module object exists
     * 3. Check existence of same name
     * 4. Check existence of same code
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
            long moduleId = Long.parseLong(parameterMap.id.toString())
            PtModule oldModule = (PtModule) ptModuleService.read(moduleId) // get Module object
            // check whether selected Module object exists or not
            if (!oldModule) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            PtModule module = buildModuleObject(parameterMap, oldModule)  // build Module object for update

            int countName = ptModuleCacheUtility.countByNameIlikeAndIdNotEqual(module.name, module.id)
            if (countName > 0) {
                result.put(Tools.MESSAGE, NAME_EXIST_MESSAGE)
                return result
            }
            int countCode = ptModuleCacheUtility.countByCodeAndIdNotEqual(module.code, module.id)
            if (countCode > 0) {
                result.put(Tools.MESSAGE, CODE_EXIST_MESSAGE)
                return result
            }
            result.put(MODULE_OBJ, module)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, MODULE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Update Module object in DB
     * This function is in transactional block and will roll back in case of any exception
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
            PtModule module = (PtModule) preResult.get(MODULE_OBJ)
            ptModuleService.update(module)  // update module object in DB
            ptModuleCacheUtility.update(module, ptModuleCacheUtility.SORT_ON_NAME, ptModuleCacheUtility.SORT_ORDER_ASCENDING)
            result.put(MODULE_OBJ, module)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, MODULE_UPDATE_FAILURE_MESSAGE)
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
     * Build grid object to show a single row (updated object) in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            PtModule module = (PtModule) executeResult.get(MODULE_OBJ)
            GridEntity object = new GridEntity()                // build grid entity object
            object.id = module.id
            object.cell = [
                    Tools.LABEL_NEW,
                    module.name,
                    module.code
            ]
            result.put(Tools.MESSAGE, MODULE_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, module.version.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, MODULE_UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, MODULE_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, MODULE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Make existing/old object Up-to-date with new parameters
     * @param parameterMap -serialized parameters from UI
     * @param oldModule -old Module object
     * @return -updated Module object
     */
    private PtModule buildModuleObject(GrailsParameterMap parameterMap, PtModule oldModule) {
        PtModule module = new PtModule(parameterMap)
        oldModule.name = module.name
        oldModule.code = module.code
        return oldModule
    }

}
