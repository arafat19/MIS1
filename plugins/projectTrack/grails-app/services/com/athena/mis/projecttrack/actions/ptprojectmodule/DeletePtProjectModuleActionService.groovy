package com.athena.mis.projecttrack.actions.ptprojectmodule

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.entity.PtProjectModule
import com.athena.mis.projecttrack.service.PtProjectModuleService
import com.athena.mis.projecttrack.utility.PtProjectModuleCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Delete ptProjectModule object from DB and Cache
 *  For details go through Use-Case doc named 'DeletePtProjectModuleActionService'
 */
class DeletePtProjectModuleActionService extends BaseService implements ActionIntf {

    PtProjectModuleService ptProjectModuleService
    @Autowired
    PtProjectModuleCacheUtility ptProjectModuleCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String PROJECT_MODULE_DELETE_SUCCESS_MSG = "Project module has been successfully deleted"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete project module"
    private static final String MODULE_OBJ = "moduleObj"
    private static final String LST_MODULE = "lstModule"
    /**
     * Checking pre condition before deleting the ptProjectModule object
     * 1. Check validity for input
     * 2. Check existence for ptProjectModule object
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
            long projectModuleId = Long.parseLong(parameterMap.id.toString())
            PtProjectModule module = (PtProjectModule) ptProjectModuleCacheUtility.read(projectModuleId)
            // get projectModule object
            // check whether selected projectModule object exists or not
            if (!module) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            result.put(MODULE_OBJ, module)
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
     * Delete ptProjectModule object from DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -N/A
     * @param obj - object from previous method
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {

        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            PtProjectModule projectModule = (PtProjectModule) executeResult.get(MODULE_OBJ)
            ptProjectModuleService.delete(projectModule.id)    // delete ptProjectModule object from DB
            ptProjectModuleCacheUtility.delete(projectModule.id) //delete ptProjectModule object from cache
            // pull remaining module list that are not mapped with given project
            List<GroovyRowResult> lstModule = (List<GroovyRowResult>) listOfModule(projectModule)
            result.put(LST_MODULE, Tools.listForKendoDropdown(lstModule, null, null))
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
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap postResult = (LinkedHashMap) obj
            result.put(LST_MODULE, postResult.get(LST_MODULE))
            result.put(Tools.MESSAGE, PROJECT_MODULE_DELETE_SUCCESS_MSG)
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

    private static final String QUERY_FOR_MODULE_LIST = """
        SELECT pm.id, pm.name as name
            FROM pt_module pm
                WHERE pm.id NOT IN
                    (
                        SELECT module_id
                        FROM pt_project_module
                        WHERE project_id = :projectId
                        AND company_id = :companyId
                    )
                AND company_id = :companyId
        ORDER BY pm.id ASC
    """

    /**
     *
     * @param projectId - project id
     * @return - list of remaining module that are not mapped with this project.
     */
    private List<GroovyRowResult> listOfModule(PtProjectModule projectModule) {
        Map queryParams = [
                projectId: projectModule.projectId,
                companyId: projectModule.companyId
        ]
        List<GroovyRowResult> lstProjectModule = executeSelectSql(QUERY_FOR_MODULE_LIST, queryParams)
        return lstProjectModule
    }
}
