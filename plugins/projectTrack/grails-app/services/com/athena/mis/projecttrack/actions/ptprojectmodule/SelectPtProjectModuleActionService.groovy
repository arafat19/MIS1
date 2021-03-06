package com.athena.mis.projecttrack.actions.ptprojectmodule

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.entity.PtProjectModule
import com.athena.mis.projecttrack.utility.PtProjectModuleCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select ptProjectModule object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectPtProjectModuleActionService'
 */
class SelectPtProjectModuleActionService extends BaseService implements ActionIntf{

    @Autowired
    PtProjectModuleCacheUtility ptProjectModuleCacheUtility

	private final Logger log = Logger.getLogger(getClass())

    private static final String PROJECT_MODULE_NOT_FOUND_MASSAGE = "Selected project module is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select project module"
    private static final String PROJECT_MODULE = "projectModule"
    private static final String LST_MODULE = "lstModule"

    /**
     * Get parameters from UI and check if id exists in parameterMap
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
            result.put(Tools.MESSAGE, PROJECT_MODULE_NOT_FOUND_MASSAGE)
            return result
        }
	}

    /**
     * Get ptProjectModule object by id
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */

	public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long projectModuleId = Long.parseLong(parameterMap.id.toString())
            PtProjectModule projectModule = (PtProjectModule)ptProjectModuleCacheUtility.read(projectModuleId)
            // check whether the ptModule object exists or not
            if (!projectModule) {
                result.put(Tools.MESSAGE, PROJECT_MODULE_NOT_FOUND_MASSAGE)
                return result
            }
            result.put(PROJECT_MODULE, projectModule)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_MODULE_NOT_FOUND_MASSAGE)
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
     * Build a map with ptProjectModule object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
	public Object buildSuccessResultForUI(Object obj) {

        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            PtProjectModule projectModule = (PtProjectModule) executeResult.get(PROJECT_MODULE)
            // pull remaining module list that are not mapped with given project
            List<GroovyRowResult> lstModule =(List<GroovyRowResult>) listOfModule(projectModule)
            result.put(LST_MODULE, Tools.listForKendoDropdown(lstModule,null,null))
            result.put(Tools.ENTITY, projectModule)
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
            }
            Map previousResult = (Map) obj              // cast map returned from previous method
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

    private static final String QUERY_FOR_MODULE_LIST = """
            SELECT pm.id, pm.name as name
                FROM pt_module pm
                    WHERE pm.id NOT IN
                        (
                            SELECT module_id
                            FROM pt_project_module
                            WHERE project_id = :projectId
                            AND company_id = :companyId
                            AND id != :id
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
                id: projectModule.id,
                projectId: projectModule.projectId,
                companyId: projectModule.companyId
        ]
        List<GroovyRowResult> lstProjectModule = executeSelectSql(QUERY_FOR_MODULE_LIST, queryParams)
        return lstProjectModule
    }
}
