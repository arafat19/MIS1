package com.athena.mis.projecttrack.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtProjectCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show sprint details list for grid
 *  For details go through Use-Case doc named 'PtListSprintDetailsReportActionService'
 */
class PtShowSprintDetailsReportActionService extends BaseService implements ActionIntf {

    PtSprintService ptSprintService
    PtBacklogService ptBacklogService
    SystemEntityService systemEntityService
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = "Failed to generate sprint details report";
    private static final String GRID_OBJ = "gridObj"
    private static final String SPRINT_NOT_FOUND = "Sprint not found"
    private static final String SPRINT_ID = "sprintId"
    private static final String PROJECT_ID = "projectId"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * 1. Get parameters from UI and checks required information
     * 2. Get list and of sprint details
     * 3. Wrap sprint details list for grid
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            // default values for left menu click
            result.put(GRID_OBJ, Boolean.FALSE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (params.sprintId) {
                long sprintId = Long.parseLong(params.sprintId.toString())
                PtSprint sprint = ptSprintService.read(sprintId)
                if (!sprint) {
                    result.put(Tools.MESSAGE, SPRINT_NOT_FOUND)
                    return result
                }
                initPager(params)
                long companyId = sprint.companyId
                Map serviceResult = getSprintDetailsList(sprint.id, companyId)
                List<GroovyRowResult> sprintDetailList = serviceResult.lstSprintDetails
                int count = serviceResult.count
                List gridRows = wrapSprintListGrid(sprintDetailList, this.start)
                Map gridObj = [page: pageNumber, total: count.toInteger(), rows: gridRows]
                Long projectId = (Long) sprint.projectId
                result.put(GRID_OBJ, gridObj)
                result.put(SPRINT_ID, sprintId.toLong())
                result.put(PROJECT_ID, projectId)
            }
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * do nothing for buildSuccess operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * do nothing for buildFailure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    private static final String QUERY_SPRINT_DETAILS = """
       SELECT id, type, module, idea_title, status, priority, status_id
         FROM
            (SELECT backlog.id AS id, 'Task' AS type, module.code AS module, backlog.idea AS idea_title,
            backlog.sprint_id AS sprint_id, backlog.company_id AS company_id, system_entity.key AS status,
            priority.key AS priority, system_entity.id AS status_id
                FROM pt_backlog backlog
                LEFT JOIN system_entity ON system_entity.id = backlog.status_id
                LEFT JOIN pt_module module ON module.id = backlog.module_id
                LEFT JOIN system_entity priority ON priority.id = backlog.priority_id
                WHERE backlog.owner_id = 0
                    UNION
            SELECT bug.id AS id, 'Bug' AS type,  module.code AS module, bug.title AS idea_title, bug.sprint_id AS sprint_id,
            bug.company_id AS company_id, system_entity.key AS status, priority.key AS priority, system_entity.id AS status_id
                FROM pt_bug bug
                LEFT JOIN system_entity ON system_entity.id = bug.status
                LEFT JOIN pt_module module ON module.id = bug.module_id
                LEFT JOIN system_entity priority ON priority.id = bug.severity
                LEFT JOIN pt_backlog backlog  ON backlog.id = bug.backlog_id
                WHERE backlog.owner_id = 0)
                      AS sprint_details
         WHERE sprint_details.sprint_id = :sprintId
         AND sprint_details.company_id = :companyId
         ORDER BY sprint_details.type DESC, sprint_details.status_id
         LIMIT :resultPerPage OFFSET :start
    """

    private static final String QUERY_SPRINT_DETAILS_COUNT = """
      SELECT COUNT(id)
         FROM
            (SELECT backlog.id AS id, backlog.sprint_id AS sprint_id, backlog.company_id AS company_id
                FROM pt_backlog backlog
                WHERE backlog.owner_id = 0
                    UNION
            SELECT bug.id AS id, bug.sprint_id AS sprint_id, bug.company_id AS company_id
                FROM pt_bug bug
                LEFT JOIN pt_backlog backlog  ON backlog.id = bug.backlog_id
                WHERE backlog.owner_id = 0)
                      AS sprint_details
         WHERE sprint_details.sprint_id = :sprintId
         AND sprint_details.company_id = :companyId
    """

    /**
     * Get list of backlog list and count of list by sprintId, backlogStatusId and companyId
     * @param sprintId
     * @param backlogStatusId
     * @return -a map containing sprint details list and their count
     */
    private Map getSprintDetailsList(long sprintId, long companyId) {
        Map queryParams = [
                sprintId: sprintId,
                companyId: companyId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstSprintDetails = executeSelectSql(QUERY_SPRINT_DETAILS, queryParams)
        int count = executeSelectSql(QUERY_SPRINT_DETAILS_COUNT, queryParams).first().count
        Map result = [lstSprintDetails: lstSprintDetails, count: count.toInteger()]
        return result
    }

    /**
     * Wrap list of backlog in grid entity
     * @param lstSprint -list of backlog
     * @param start -starting index of the page
     * @return -list of backlog
     */
    private List wrapSprintListGrid(List<GroovyRowResult> lstSprint, int start) {
        List sprintList = []
        int counter = start + 1
        for (int i = 0; i < lstSprint.size(); i++) {
            GroovyRowResult eachRow = lstSprint[i]
            GridEntity obj = new GridEntity()    // build grid object
            obj.id = eachRow.id
            obj.cell = [
                    counter,
                    eachRow.type,
                    eachRow.module,
                    eachRow.idea_title,
                    eachRow.priority,
                    eachRow.status
            ]
            sprintList << obj
            counter++
        }
        return sprintList
    }

}
