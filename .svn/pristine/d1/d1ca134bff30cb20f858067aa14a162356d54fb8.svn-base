package com.athena.mis.projecttrack.actions.ptmodule

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.entity.PtModule
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.service.PtModuleService
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.projecttrack.utility.PtProjectModuleCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Delete Module object from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeletePtModuleActionService'
 */
class DeletePtModuleActionService extends BaseService implements ActionIntf {

    PtModuleService ptModuleService
    PtBacklogService ptBacklogService
    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility
    @Autowired
    PtProjectModuleCacheUtility ptProjectModuleCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String HAS_ASSOCIATION_PROJECT_MODULE_MESSAGE = " project(s) associated with this module"
    private static final String HAS_ASSOCIATION_BACKLOG_MESSAGE = " backlog associated with this module"
    private static final String MODULE_DELETE_SUCCESS_MSG = "Module has been successfully deleted"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete module"
    /**
     * Checking pre condition before deleting the Module object
     * 1. Check validity for input
     * 2. Check existence of Module object
     * 3. Check association of Module
     * @param parameters -parameters from UI
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
            long moduleId = Long.parseLong(parameterMap.id.toString())
            PtModule module = (PtModule) ptModuleCacheUtility.read(moduleId)    // get module object from cache
            // check whether selected module object exists or not
            if (!module) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            // check association of module with relevant domains
            Map associationResult = (Map) hasAssociation(module)
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
     * Delete Module object from DB
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long moduleId = Long.parseLong(parameterMap.id.toString())
            ptModuleService.delete(moduleId)    // delete module object from DB
            ptModuleCacheUtility.delete(moduleId)
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
     * Do nothing for post condition
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }

    /**
     * Build success message for delete
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(Tools.MESSAGE, MODULE_DELETE_SUCCESS_MSG)
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
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
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
     * Check association of Module with relevant domains
     * @param module -Module object
     * @return a map containing hasAssociation(true/false) and relevant message
     */
    private LinkedHashMap hasAssociation(PtModule module) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        long moduleId = module.id
        int count = 0

        count = ptProjectModuleCacheUtility.countByModuleId(moduleId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_PROJECT_MODULE_MESSAGE)
            return result
        }
        count = ptBacklogService.countByModuleId(moduleId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_BACKLOG_MESSAGE)
            return result
        }
        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }
}
