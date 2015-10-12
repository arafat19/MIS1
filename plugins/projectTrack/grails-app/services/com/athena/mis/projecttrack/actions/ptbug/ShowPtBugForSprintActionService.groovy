package com.athena.mis.projecttrack.actions.ptbug

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.entity.PtBug
import com.athena.mis.projecttrack.entity.PtModule
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtBugService
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.PtBacklogPriorityCacheUtility
import com.athena.mis.projecttrack.utility.PtBugStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ShowPtBugForSprintActionService extends BaseService implements ActionIntf {

    PtSprintService ptSprintService
    PtBugService ptBugService
    SystemEntityService systemEntityService
    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility
    @Autowired
    PtBacklogPriorityCacheUtility ptBacklogPriorityCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass());

    private static final String PT_SPRINT = "ptSprint"
    private static final String SHOW_FAILURE_MESSAGE = "Failed to load bug information page"
    private static final String GRID_OBJ = "gridObj"
    private static final String NAME = "sprintName"
    private static final String SPRINT_ID = "sprintId"
    private static final String PROJECT_ID = "projectId"
    private static final String MODULE_LIST = "lstModule"

    /**
     * Check the params and sprint object existence
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)            // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.sprintId) {           // check required params
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long sprintId = Long.parseLong(params.sprintId.toString())
            PtSprint ptSprint = ptSprintService.read(sprintId)
            if (!ptSprint) {                                      // check whether sprint object exists or not
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            result.put(PROJECT_ID, ptSprint.projectId)
            result.put(PT_SPRINT, ptSprint)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for executePostCondition
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get sprint object and module list for drop down
     * @param parameter - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameter, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)                // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameter
            initPager(params)              // initialize parameters for flexGrid
            if (!params.rp) {
                params.rp = DEFAULT_RESULT_PER_PAGE
            }
            LinkedHashMap preResult = (LinkedHashMap) obj          // cast map returned from execute method
            PtSprint ptSprint = (PtSprint) preResult.get(PT_SPRINT)
            List<GroovyRowResult> lstModule = getModuleBySprintAndProject(ptSprint.id, ptSprint.projectId)
            result.put(PT_SPRINT, ptSprint)
            result.put(MODULE_LIST, Tools.listForKendoDropdown(lstModule, null, 'Select a module'))
            result.put(PROJECT_ID, preResult.get(PROJECT_ID))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap bug list for grid
     * @param obj - map returned from execute method
     * @return - a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj   // cast map returned from execute method
            PtSprint ptSprint = (PtSprint) executeResult.get(PT_SPRINT)
            List<PtBug> bugsList = ptBugService.findAllBySprintIdAndCompanyId(this, ptSprint.id, ptSprint.companyId)
            int count = ptBugService.countBySprintIdAndCompanyId(ptSprint.id, ptSprint.companyId)
            List lstWrappedBugs = wrapBugs(bugsList, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedBugs]
            result.put(GRID_OBJ, gridObj)
            result.put(NAME, ptSprint.name)
            result.put(SPRINT_ID, ptSprint.id)
            result.put(MODULE_LIST, executeResult.get(MODULE_LIST))
            result.put(PROJECT_ID, executeResult.get(PROJECT_ID))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    private static final String SELECT_QUERY = """
        SELECT  module.id AS id, module.name AS name
        FROM pt_module module
             JOIN pt_project_module pm ON module.id = pm.module_id
             JOIN pt_sprint sprint ON sprint.project_id = pm.project_id
        WHERE sprint.id = :sprintId
        AND pm.project_id = :projectId
        AND module.company_id = :companyId
        ORDER BY name
        LIMIT :resultPerPage OFFSET :start
    """

    /**
     * Get Module list by sprintId and projectId
     * @param sprintId - sprint.id
     * @param projectId - project.id
     * @return - module list
     */
    private List<GroovyRowResult> getModuleBySprintAndProject(long sprintId, long projectId) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        Map queryParams = [
                projectId: projectId,
                sprintId: sprintId,
                companyId: companyId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> moduleList = executeSelectSql(SELECT_QUERY, queryParams)
        return moduleList
    }

    /**
     * Wrap the bug list for grid
     * @param lstBug - list of mapped sprint-bug object
     * @param start - starting index of page
     * @return - list of wrapped bugs
     */
    private List wrapBugs(List<PtBug> lstBug, int start) {
        List lstWrappedBugs = []
        int counter = start + 1
        for (int i = 0; i < lstBug.size(); i++) {
            PtBug ptBug = lstBug[i]
            PtModule module = (PtModule) ptModuleCacheUtility.read(ptBug.moduleId)
            SystemEntity bugSeverity = systemEntityService.read(ptBug.severity)
            SystemEntity bugType = systemEntityService.read(ptBug.type)
            GridEntity obj = new GridEntity()
            obj.id = ptBug.id
            obj.cell = [
                    counter,
                    ptBug.id,
                    module.name,
                    ptBug.title,
                    bugSeverity.key,
                    bugType.key
            ]
            lstWrappedBugs << obj
            counter++
        }
        return lstWrappedBugs
    }
}
