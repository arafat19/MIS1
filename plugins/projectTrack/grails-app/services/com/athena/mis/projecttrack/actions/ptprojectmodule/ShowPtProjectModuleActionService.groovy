package com.athena.mis.projecttrack.actions.ptprojectmodule

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.entity.PtProject
import com.athena.mis.projecttrack.service.PtProjectModuleService
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.projecttrack.utility.PtProjectCacheUtility
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
 *  For details go through Use-Case doc named 'ShowPtProjectModuleActionService'
 */
class ShowPtProjectModuleActionService extends BaseService implements ActionIntf {

    PtProjectModuleService ptProjectModuleService
    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility
    @Autowired
    PtProjectModuleCacheUtility ptProjectModuleCacheUtility
    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_FAILURE_MSG_SHOW_PROJECT_MODULE = "Failed to load module page"
    private static final String GRID_OBJ = "gridObj"
    private static final String LST_PROJECT_MODULE = "lstProjectModule"
    private static final String LST_MODULE = "lstModule"
    private static final String INVALID_INPUT_MSG = "Failed to load page due to invalid input"
    private static final String PROJECT_NOT_FOUND = "Project not found"
    private static final String PROJECT_ID = "projectId"
    private static final String PROJECT_NAME = "projectName"
    private static final String PROJECT_OBJ = "projectObj"

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

            long projectId = Long.parseLong(parameters.projectId.toString())
            PtProject project = (PtProject) ptProjectCacheUtility.read(projectId)
            if (!project) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND)
                return result
            }
            result.put(PROJECT_ID, projectId.toLong())
            result.put(PROJECT_OBJ, project)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PROJECT_MODULE)
            return result
        }
    }

    /**
     * Get ptProjectModule list and count of total ptProjectModule for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            LinkedHashMap preResult = (LinkedHashMap) obj
            initPager(parameterMap)   // initialize parameters for flexGrid
            Long projectId = (Long) (preResult.get(PROJECT_ID))
            // pull remaining module list that are not mapped with given project
            List<GroovyRowResult> lstModule = (List<GroovyRowResult>) listOfModule(projectId)
            // Get map of project module list and count
            List<GroovyRowResult> lstProjectModule = listOfProjectModule(projectId, this)
            long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
            int count = ptProjectModuleService.countByProjectIdAndCompanyId(projectId, companyId)
            PtProject project = (PtProject) preResult.get(PROJECT_OBJ)
            result.put(PROJECT_ID, projectId)
            result.put(PROJECT_NAME, project.name)
            result.put(LST_MODULE, lstModule)
            result.put(LST_PROJECT_MODULE, lstProjectModule)
            result.put(Tools.COUNT, count.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PROJECT_MODULE)
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
     * Wrap ptProjectModule list for grid
     * 1. Get lstModule list from cacheUtility for module drop down
     * 2. Get lstProject list from cacheUtility for project drop down
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj                        // cast map returned from execute method
            List<GroovyRowResult> lstProjectModule = (List<GroovyRowResult>) executeResult.get(LST_PROJECT_MODULE)
            List<GroovyRowResult> lstModule = (List<GroovyRowResult>) executeResult.get(LST_MODULE)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedProjectModule = wrapProjectModule(lstProjectModule, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedProjectModule]

            result.put(PROJECT_ID, executeResult.get(PROJECT_ID))
            result.put(PROJECT_NAME, executeResult.get(PROJECT_NAME))
            result.put(LST_MODULE, Tools.listForKendoDropdown(lstModule, null, null))
            result.put(LST_PROJECT_MODULE, lstProjectModule)
            result.put(GRID_OBJ, gridObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PROJECT_MODULE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
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
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PROJECT_MODULE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PROJECT_MODULE)
            return result
        }

    }

    /**
     * Wrap list of ptProjectModule in grid entity
     * @param lstProjectModule -list of ptProjectModule object(s)
     * @param start -starting index of the page
     * @return -list of wrapped ptModule
     */
    private List wrapProjectModule(List<GroovyRowResult> lstProjectModule, int start) {
        List lstWrappedProjectModule = []
        int counter = start + 1
        for (int i = 0; i < lstProjectModule.size(); i++) {
            GroovyRowResult projectModule = lstProjectModule[i]
            GridEntity obj = new GridEntity()
            obj.id = projectModule.id
            obj.cell = [
                    counter,
                    projectModule.module_name
            ]
            lstWrappedProjectModule << obj
            counter++
        }
        return lstWrappedProjectModule
    }

    private static final String QUERY_FOR_LIST = """
        SELECT pm.id, m.name as module_name
        FROM pt_project_module pm
        LEFT JOIN  pt_module m ON pm.module_id = m.id
        WHERE pm.project_id = :projectId
        AND pm.company_id = :companyId
        ORDER BY pm.module_id ASC
        LIMIT :resultPerPage OFFSET :start
    """
    /**
     *
     * @param projectId - project id
     * @param baseService - base service object
     * @return - list of mapped project-module
     */
    private List<GroovyRowResult> listOfProjectModule(Long projectId, BaseService baseService) {
        Map queryParams = [
                projectId: projectId,
                companyId: ptSessionUtil.appSessionUtil.getCompanyId(),
                resultPerPage: baseService.resultPerPage,
                start: baseService.start
        ]
        List<GroovyRowResult> lstProjectModule = executeSelectSql(QUERY_FOR_LIST, queryParams)
        return lstProjectModule
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
    private List<GroovyRowResult> listOfModule(Long projectId) {
        Map queryParams = [
                projectId: projectId,
                companyId: ptSessionUtil.appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> lstProjectModule = executeSelectSql(QUERY_FOR_MODULE_LIST, queryParams)
        return lstProjectModule
    }
}
