package com.athena.mis.projecttrack.actions.ptbacklog

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.projecttrack.entity.PtProject
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtProjectService
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtBugStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search active backlog and show specific list of active backlogs for grid
 *  For details go through Use-Case doc named 'SearchActiveBacklogActionService'
 */
class SearchActiveBacklogActionService extends BaseService implements ActionIntf  {

    PtSprintService ptSprintService
    PtProjectService ptProjectService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String BACKLOG_NOT_FOUND = "Backlog not found"
    private static final String PROJECT_NOT_FOUND = "Project not found"
    private static final String HAS_NO_ACTIVE_SPRINT = "Selected project has no active sprint"
    private static final String FAILURE_MESSAGE = "Failed to generate task details list"
    private static final String SPRINT_OBJ = "sprintObj"
    private static final String GRID_OBJ = "gridObj"
    private static final String BACKLOG_LIST = "backlogList"
    private static final String SPAN_START = "<span style='color:red'>"
    private static final String SPAN_END = "</span>"

    /**
     * Get parameters from UI and check required information
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)          // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.rp) {
                params.rp = 15
                params.page = 1
            }
            initSearch(params) // initialize parameters for flexGrid
            if (!params.projectId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long projectId = Long.parseLong(params.projectId.toString())
            PtProject project = ptProjectService.read(projectId)
            if (!project) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND)
                return result
            }
            PtSprint sprint = ptSprintService.findByProjectIdAndIsActive(projectId, true)
            if (!sprint) {
                result.put(Tools.MESSAGE, HAS_NO_ACTIVE_SPRINT)
                return result
            }
            result.put(SPRINT_OBJ, sprint)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Get list of story details
     * @param parameters -serialized parameters from UI
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            GrailsParameterMap params = (GrailsParameterMap) parameters
            PtSprint sprint = (PtSprint) preResult.get(SPRINT_OBJ)
            List<Long> lstStatusIds = []
            if (params.statusId.equals(Tools.EMPTY_SPACE)) {
                lstStatusIds = ptBacklogStatusCacheUtility.listOfAllStatusIds()
            } else {
                long statusId = Long.parseLong(params.statusId.toString())
                lstStatusIds << statusId
            }
            Map searchResult = search(sprint.id, lstStatusIds)
            List<GroovyRowResult> backlogList = (List<GroovyRowResult>) searchResult.backlogList
            if (backlogList.size() <= 0) {
                result.put(Tools.MESSAGE, BACKLOG_NOT_FOUND)
                return result
            }
            result.put(BACKLOG_LIST, backlogList)
            result.put(Tools.COUNT, searchResult.count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
            List<GroovyRowResult> lstBacklog = (List) executeResult.get(BACKLOG_LIST)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List<GroovyRowResult> lstWrappedBacklogs = wrapBacklogs(lstBacklog, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedBacklogs]
            result.put(GRID_OBJ, gridObj)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
        List backlogList = []
        int counter = start + 1
        for (int i = 0; i < lstBacklog.size(); i++) {
            GroovyRowResult eachRow = lstBacklog[i]
            GridEntity obj = new GridEntity()    // build grid object
            obj.id = eachRow.id

            obj.cell = [
                    counter,
                    eachRow.type,
                    eachRow.title,
                    eachRow.status,
                    eachRow.owner,
                    eachRow.pac_count,
                    eachRow.bug_count,
                    (eachRow.type.equals('Task') && eachRow.unresolved > 0) ? SPAN_START + eachRow.unresolved + SPAN_END : eachRow.unresolved
            ]
            backlogList << obj
            counter++
        }
        return backlogList
    }

    /**
     * Search and get specific list pf sprint by search key word
     * @return -a lis of sprint according to search result
     */
    private Map search(long sprintId, List<Long> lstStatusIds) {
        String strIds = Tools.buildCommaSeparatedStringOfIds(lstStatusIds)
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity reOpen = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.RE_OPENED_RESERVED_ID, companyId)
        SystemEntity submitted = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.SUBMITTED_RESERVED_ID, companyId)
        String queryForList = """
            (SELECT backlog.id AS id, 'Task' AS type, backlog.idea AS title, status.key AS status, au.username AS owner,
                COUNT(pac.id) AS pac_count,
                (SELECT COUNT(bug.id) FROM pt_bug bug WHERE bug.backlog_id = backlog.id) AS bug_count,
                (SELECT COUNT(bug.id) FROM pt_bug bug WHERE bug.status IN (${reOpen.id},${submitted.id}) AND bug.backlog_id = backlog.id) AS unresolved,
                backlog.status_id AS status_id
            FROM pt_backlog backlog
            LEFT JOIN pt_acceptance_criteria pac ON pac.backlog_id = backlog.id
            LEFT JOIN system_entity status on status.id = backlog.status_id
            LEFT JOIN app_user au ON au.id = backlog.owner_id
            WHERE backlog.sprint_id = :sprintId
            AND backlog.company_id = :companyId
            AND backlog.status_id IN (${strIds})
            AND backlog.idea ILIKE :query
            GROUP BY backlog.idea, backlog.id, status.key, au.username, backlog.status_id)

            UNION

            (SELECT bug.id AS id, 'Bug' AS type, bug.title AS title, status.key AS status, au.username AS owner, null AS pac_count,
                null AS bug_count, null AS unresolved, bug.status AS status_id
            FROM pt_bug bug
            LEFT JOIN system_entity status ON status.id = bug.status
            LEFT JOIN app_user au ON au.id = bug.owner_id
            WHERE bug.sprint_id = :sprintId
            AND bug.company_id = :companyId
            AND bug.backlog_id = 0
            AND bug.title ILIKE :query)
            ORDER BY type desc, status_id
            LIMIT :resultPerPage OFFSET :start
        """

        String queryForCount = """
            SELECT COUNT(id)
            FROM
                (SELECT backlog.id AS id
                FROM pt_backlog backlog
                WHERE backlog.sprint_id = :sprintId
                AND backlog.company_id = :companyId
                AND backlog.status_id IN (${strIds})
                AND backlog.idea ILIKE :query
                    UNION
                SELECT bug.id AS id
                FROM pt_bug bug
                WHERE bug.sprint_id = :sprintId
                AND bug.company_id = :companyId
                AND bug.backlog_id = 0
                AND bug.title ILIKE :query)
            AS task_details
        """

        Map queryParams = [
                sprintId: sprintId,
                companyId: ptSessionUtil.appSessionUtil.getCompanyId(),
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> backlogList = executeSelectSql(queryForList, queryParams)
        int count = (int) executeSelectSql(queryForCount, queryParams).first().count
        Map result = [backlogList: backlogList, count: count]
        return result
    }
}
