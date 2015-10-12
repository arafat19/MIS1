package com.athena.mis.projecttrack.actions.ptbacklog

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.entity.PtProjectModule
import com.athena.mis.projecttrack.service.PtAcceptanceCriteriaService
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.service.PtProjectModuleService
import com.athena.mis.projecttrack.utility.PtBacklogPriorityCacheUtility
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search ptBacklog and show specific list of ptBacklog for grid
 *  For details go through Use-Case doc named 'SearchPtBacklogActionService'
 */
class SearchPtBacklogActionService extends BaseService implements ActionIntf {

    PtBacklogService ptBacklogService
    PtAcceptanceCriteriaService ptAcceptanceCriteriaService
    PtProjectModuleService ptProjectModuleService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    PtBacklogPriorityCacheUtility ptBacklogPriorityCacheUtility
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility
    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_BACKLOG_FAILURE_MESSAGE = "Failed to load backlog page"
    private static final String LST_BACKLOG = "lstBacklog"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get ptBacklog list and count for grid with specific search result
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initSearch(parameterMap) // initialize parameters for flexGrid
            List<Long> lstModuleIds = lstUserModuleIds()
            LinkedHashMap backlogList = search(lstModuleIds)
            List<GroovyRowResult> lstBacklog = backlogList.searchResult
            int count = (int) backlogList.count
            result.put(LST_BACKLOG, lstBacklog)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_BACKLOG_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }

    /**
     * Wrap ptBacklog list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj    // cast map returned from execute method
            List<GroovyRowResult> lstBacklog = (List) executeResult.get(LST_BACKLOG)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List<GroovyRowResult> lstWrappedBacklogs = wrapBacklogs(lstBacklog, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedBacklogs]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_BACKLOG_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, SHOW_BACKLOG_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_BACKLOG_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of ptBacklog in grid entity
     * @param lstBacklog -list of ptBacklog object(s)
     * @param start -starting index of the page
     * @return -list of wrapped ptBacklogs
     */
    private List wrapBacklogs(List<GroovyRowResult> lstBacklog, int start) {
        List lstWrappedBacklogs = []
        int counter = start + 1
        for (int i = 0; i < lstBacklog.size(); i++) {
            GroovyRowResult backlog = lstBacklog[i]
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
    private List<Long> lstUserModuleIds(){
        List<Long> lstModuleIds = []
        List<Long> lstProjectIds = (List<Long>) ptSessionUtil.getUserPtProjectIds()
        List<PtProjectModule> lstProjectModule = ptProjectModuleService.findAllByProjectIdInList(lstProjectIds)

        for (int i = 0; i < lstProjectModule.size(); i++) {
            lstModuleIds << lstProjectModule[i].moduleId
        }
        return lstModuleIds
    }

    /**
     * Search and get specific list pf sprint by search key word
     * @return -a lis of sprint according to search result
     */
    private LinkedHashMap search(List<Long> lstModuleIds) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId();
        String lstUserModuleIds = Tools.buildCommaSeparatedStringOfIds(lstModuleIds)
        SystemEntity entityType = (SystemEntity) appUserEntityTypeCacheUtility.readByReservedAndCompany(appUserEntityTypeCacheUtility.PT_PROJECT, companyId)

        String str_query = """
            SELECT backlog.id,module.code code,priority.key priority,backlog.purpose,backlog.benefit,au.username username,
                (SELECT COUNT(acceptance.id) FROM pt_acceptance_criteria acceptance WHERE acceptance.backlog_id = backlog.id AND acceptance.company_id = :companyId) AS count
            FROM pt_backlog backlog
                LEFT JOIN system_entity priority ON priority.id = backlog.priority_id
                LEFT JOIN pt_module module ON module.id = backlog.module_id
                LEFT JOIN pt_project_module pm ON pm.module_id = backlog.module_id
                LEFT JOIN pt_project project ON project.id = pm.project_id
                LEFT JOIN app_user_entity ape ON ape.entity_id = project.id
                LEFT JOIN app_user au ON au.id = ape.app_user_id
            WHERE backlog.company_id =:companyId
                AND ape.entity_type_id = :entityTypeId
                AND au.id = :appUserId
                AND module.id IN (${lstUserModuleIds})
                AND backlog.sprint_id = 0
                AND ${queryType} ILIKE :query
            GROUP BY backlog.id, backlog.module_id,module.code, priority.key,backlog.purpose, backlog.benefit,au.username
            ORDER BY backlog.id DESC
            LIMIT :resultPerPage OFFSET :start
        """

        Map queryParams = [
                companyId: companyId,
                appUserId: ptSessionUtil.appSessionUtil.getAppUser().id,
                entityTypeId: entityType.id,
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                resultPerPage: resultPerPage,
                start: start
        ]

        String str_count = """
            SELECT COUNT(backlog.id)
            FROM pt_backlog backlog
             LEFT JOIN pt_module module ON module.id = backlog.module_id
            WHERE backlog.company_id = :companyId
                AND module.id IN (${lstUserModuleIds})
                AND backlog.sprint_id = 0
                AND ${queryType} ILIKE :query
        """

        List<GroovyRowResult> lstResult = executeSelectSql(str_query, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(str_count, queryParams)

        int total = (int) countResult[0].count
        return [searchResult: lstResult, count: total]
    }
}
