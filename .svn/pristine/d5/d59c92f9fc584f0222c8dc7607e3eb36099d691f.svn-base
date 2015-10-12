package com.athena.mis.projecttrack.actions.ptprojectmodule

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.service.PtProjectModuleService
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.projecttrack.utility.PtProjectModuleCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Get list of ptProjectModule for grid
 *  For details go through Use-Case doc named 'ListPtProjectModuleActionService'
 */

class ListPtProjectModuleActionService extends BaseService implements ActionIntf {

    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtProjectModuleCacheUtility ptProjectModuleCacheUtility
    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility
    PtProjectModuleService ptProjectModuleService

    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_PROJECT_MODULE_FAILURE_MESSAGE = "Failed to load project module page"
    private static final String LST_PROJECT_MODULE = "lstPtProjectModule"
    private static final String INVALID_INPUT_MSG = "Failed to load page due to invalid input"
    private static final String PROJECT_NOT_FOUND = "Project not found"

    /**
     * Get parameters from UI
     * 1. Check validity for input
     * 2. Check existence for ptProject object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            if (!parameters.projectId) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_PROJECT_MODULE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Get ptModule list for grid
     * Get count of total ptProjectModule
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {

        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)                                                // initialize parameters for flexGrid
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            // checking is system entity type id or not
            if (projectId < 0) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND)
                return result
            }
            // Get map of project module list and count
            List<GroovyRowResult> lstPtProjectModule = listOfProjectModules(projectId, this)
            long companyID = ptSessionUtil.appSessionUtil.getCompanyId()
            int count = ptProjectModuleService.countByProjectIdAndCompanyId(projectId, companyID)
            result.put(LST_PROJECT_MODULE, lstPtProjectModule)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_PROJECT_MODULE_FAILURE_MESSAGE)
            return null
        }
    }

    /**
     * Do nothing for post condition
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }

    /**
     * wrap ptProjectModule list for grid
     * @param obj - return from execute method
     * @return- map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj                                // cast map returned from execute method
            List lstPtModule = (List) executeResult.get(LST_PROJECT_MODULE)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedModule = wrapProjectModule(lstPtModule, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedModule]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_PROJECT_MODULE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, SHOW_PROJECT_MODULE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_PROJECT_MODULE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of ptProjectModule in grid entity
     * @param lstPtProjectModule -list of ptProjectModule object(s)
     * @param start -starting index of the page
     * @return -list of wrapped ptProjectModule
     */
    private List wrapProjectModule(List<GroovyRowResult> lstPtProjectModule, int start) {
        List lstWrappedProjectModule = []
        int counter = start + 1
        for (int i = 0; i < lstPtProjectModule.size(); i++) {
            GroovyRowResult ptProjectModule = lstPtProjectModule[i]

            GridEntity obj = new GridEntity()
            obj.id = ptProjectModule.id
            obj.cell = [
                    counter,
                    ptProjectModule.module_name
            ]
            lstWrappedProjectModule << obj
            counter++
        }
        return lstWrappedProjectModule
    }

    private static final String QUERY_FOR_LIST =
            """
        SELECT pm.id, m.name as module_name
        FROM pt_project_module pm
        LEFT JOIN  pt_module m ON pm.module_id = m.id
        WHERE pm.project_id = :projectId
        AND pm.company_id = :companyId
        ORDER BY pm.module_id ASC
        LIMIT :resultPerPage OFFSET :start
    """
    /**
     * Get list of project module
     * @param projectId - PtProject.id
     * @param baseService - object of BaseService
     * @return - a map containing project module list and it's count
     */
    public List<GroovyRowResult> listOfProjectModules(long projectId, BaseService baseService) {
        Map queryParams = [
                projectId: projectId,
                companyId: ptSessionUtil.appSessionUtil.getCompanyId(),
                resultPerPage: baseService.resultPerPage,
                start: baseService.start
        ]
        List<GroovyRowResult> projectModuleList = executeSelectSql(QUERY_FOR_LIST, queryParams)
        return projectModuleList
    }
}
