package com.athena.mis.projecttrack.actions.ptbacklog

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.projecttrack.entity.PtProjectModule
import com.athena.mis.projecttrack.service.PtProjectModuleService
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.entity.PtModule
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.PtBacklogPriorityCacheUtility
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ListBackLogForSprintActionService extends BaseService implements ActionIntf {

    PtSprintService ptSprintService
    PtBacklogService ptBacklogService
    PtProjectModuleService ptProjectModuleService
    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility
    @Autowired
    PtBacklogPriorityCacheUtility ptBacklogPriorityCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load backlog information page"
    private static final String LST_BACKLOG = "lstBackLog"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get ptBacklog list of specific sprint for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameter, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)                // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameter
            long sprintId = Long.parseLong(params.sprintId.toString())
            if (!params.rp) {
                params.rp = DEFAULT_RESULT_PER_PAGE
            }
            initPager(params)              // initialize parameters for flexGrid
            List<Long> lstModuleIds = mappedUserModule()
            LinkedHashMap backlogList = getBacklogList(lstModuleIds, sprintId)
            List<GroovyRowResult> lstBacklog = backlogList.searchResult
            int count = (int) backlogList.count
            result.put(LST_BACKLOG, lstBacklog)
            result.put(Tools.COUNT, count)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null;
    }

    /**
     * Wrap ptBacklog list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            List<GroovyRowResult> backLogList = (List<GroovyRowResult>) executeResult.get(LST_BACKLOG)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List wrapedBackLog = wrapBackLog(backLogList, start)                    // warp BackLog list for grid
            Map gridOutput = [page: pageNumber, total: count, rows: wrapedBackLog]
            return gridOutput
        }
        catch (Exception ex) {
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
            if (!obj) {
                result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
                return result
            }
            LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from execute method
            result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of ptBacklog in grid entity
     * @param lstBackLog -list of ptBacklog object(s) for a sprint
     * @param start -starting index of the page
     * @return -list of wrapped ptBacklogs
     */
    private List wrapBackLog(List<GroovyRowResult> lstBackLog, int start) {
        List lstWrappedBacklogs = []
        int counter = start + 1
        for (int i = 0; i < lstBackLog.size(); i++) {
            GroovyRowResult backlog = lstBackLog[i]
            GridEntity obj = new GridEntity()
            obj.id = backlog.id
            obj.cell = [
                    counter,
                    backlog.id,
                    backlog.code,
                    backlog.priority,
                    backlog.purpose,
                    backlog.benefit,
                    backlog.count,
                    backlog.username
            ]
            lstWrappedBacklogs << obj
            counter++
        }
        return lstWrappedBacklogs
    }

    /**
     * Get those module ids that are mapped with logged user
     * @return - list of mapped user module ids
     */
    private List<Long> mappedUserModule(){
        List<Long> lstModuleIds = []
        List<Long> lstProjectIds = (List<Long>) ptSessionUtil.getUserPtProjectIds()
        List<PtProjectModule> lstProjectModule = ptProjectModuleService.findAllByProjectIdInList(lstProjectIds)

        for (int i = 0; i < lstProjectModule.size(); i++) {
            lstModuleIds << lstProjectModule[i].moduleId
        }
        return lstModuleIds
    }

    /**
     * Get backlog list
     * @return -a lis of sprint according to getBacklogList result
     */
    private LinkedHashMap getBacklogList(List<Long> lstModuleIds, long sprintId) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        String lstUserModuleIds = Tools.buildCommaSeparatedStringOfIds(lstModuleIds)

        String str_query = """
            SELECT backlog.id id,module.code code,priority.key priority,backlog.purpose,backlog.benefit,au.username username,
                (SELECT COUNT(acceptance.id) FROM pt_acceptance_criteria acceptance WHERE acceptance.backlog_id = backlog.id AND acceptance.company_id = :companyId) AS count
            FROM pt_backlog backlog
                LEFT JOIN system_entity priority ON priority.id = backlog.priority_id
                LEFT JOIN pt_module module ON module.id = backlog.module_id
                LEFT JOIN pt_project_module pm ON pm.module_id = backlog.module_id
                LEFT JOIN pt_project project ON project.id = pm.project_id
                LEFT JOIN app_user_entity ape ON ape.entity_id = project.id
                LEFT JOIN app_user au ON au.id = backlog.created_by
            WHERE backlog.company_id =:companyId
                AND module.id IN (${lstUserModuleIds})
                AND backlog.sprint_id = :sprintId
            GROUP BY backlog.id, backlog.module_id,module.code, priority.key,backlog.purpose, backlog.benefit,au.username
            ORDER BY  ${sortColumn} ${sortOrder}
            LIMIT :resultPerPage OFFSET :start
        """

        String str_count = """
            SELECT COUNT(backlog.id)
            FROM pt_backlog backlog
             LEFT JOIN pt_module module ON module.id = backlog.module_id
            WHERE backlog.company_id = :companyId
                AND module.id IN (${lstUserModuleIds})
                AND backlog.sprint_id = :sprintId
        """

        Map queryParams = [
                companyId: companyId,
                sprintId: sprintId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstResult = executeSelectSql(str_query, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(str_count, queryParams)

        int total = (int) countResult[0].count
        return [searchResult: lstResult, count: total]
    }


}
