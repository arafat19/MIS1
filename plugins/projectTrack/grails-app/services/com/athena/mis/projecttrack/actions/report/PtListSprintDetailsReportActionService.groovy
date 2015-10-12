package com.athena.mis.projecttrack.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.service.PtSprintService
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
class PtListSprintDetailsReportActionService extends BaseService implements ActionIntf {

    PtSprintService ptSprintService
    PtBacklogService ptBacklogService
    @Autowired
    PtSessionUtil ptSessionUtil

    private Logger log = Logger.getLogger(getClass())

    private static final String SPRINT_NOT_FOUND = "Sprint not found"
    private static final String FAILURE_MESSAGE = "Failed to generate sprint details report";
    private static final String SPRINT_OBJ = "sprintObj"
    private static final String SPRINT_DETAIL_LIST = "sprintDetailList"
    private static final String GRID_OBJ = "gridObj"
    private static final String SPRINT_ID = "sprintId"

    /**
     * Get parameters from UI and checks required information
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)          // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.sprintId) {
                result.put(Tools.MESSAGE, SPRINT_NOT_FOUND)
                return result
            }
            long sprintId = Long.parseLong(params.sprintId)
            PtSprint sprint = ptSprintService.read(sprintId)
            if (!sprint) {
                result.put(Tools.MESSAGE, SPRINT_NOT_FOUND)
                return result
            }
            if (!params.rp) {
                params.rp = 15
                params.page = 1
            }
            initPager(params)
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
     * Get list and count of sprint details
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            LinkedHashMap preResult = (LinkedHashMap) obj
            PtSprint sprint = (PtSprint) preResult.get(SPRINT_OBJ)
            long companyId = sprint.companyId
            String ownerId = ''
            if (params.hasOwner.equals(Tools.TRUE)) {
                ownerId = 'backlog.owner_id > 0'
            } else {
                ownerId = 'backlog.owner_id = 0'
            }
            Map serviceResult = getSprintDetailsList(sprint.id, companyId, ownerId)
            List<GroovyRowResult> sprintDetailList = serviceResult.lstSprintDetails
            int count = serviceResult.count
            result.put(SPRINT_DETAIL_LIST, sprintDetailList)
            result.put(Tools.COUNT, count.toInteger())
            result.put(SPRINT_ID, sprint.id.toLong())
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
     * Wrap sprint details list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {

        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            List<GroovyRowResult> sprintDetailList = (List<GroovyRowResult>) executeResult.get(SPRINT_DETAIL_LIST)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            Long sprintId = (Long) executeResult.get(SPRINT_ID)
            List sprintDtlLst = wrapSprintListGrid(sprintDetailList, start)
            Map gridObj = [page: pageNumber, total: count, rows: sprintDtlLst]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, gridObj)
            result.put(SPRINT_ID, sprintId)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
                return result
            }
            LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from execute method
            result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Get list of backlog list and count of list by sprintId, backlogStatusId and companyId
     * @param sprintId
     * @param backlogStatusId
     * @return -a map containing sprint details list and their count
     */
    private Map getSprintDetailsList(long sprintId, long companyId, String ownerId) {

    String query_sprint_details = """
       SELECT id, type, module, idea_title, status, priority, status_id
         FROM
            (SELECT backlog.id AS id, 'Task' AS type, module.code AS module, backlog.idea AS idea_title,
            backlog.sprint_id AS sprint_id, backlog.company_id AS company_id, system_entity.key AS status,
            priority.key AS priority, system_entity.id AS status_id
                FROM pt_backlog backlog
                LEFT JOIN system_entity ON system_entity.id = backlog.status_id
                LEFT JOIN pt_module module ON module.id = backlog.module_id
                LEFT JOIN system_entity priority ON priority.id = backlog.priority_id
                WHERE ${ownerId}
                    UNION
            SELECT bug.id AS id, 'Bug' AS type,  module.code AS module, bug.title AS idea_title, bug.sprint_id AS sprint_id,
            bug.company_id AS company_id, system_entity.key AS status, priority.key AS priority, system_entity.id AS status_id
                FROM pt_bug bug
                LEFT JOIN system_entity ON system_entity.id = bug.status
                LEFT JOIN pt_module module ON module.id = bug.module_id
                LEFT JOIN system_entity priority ON priority.id = bug.severity
                LEFT JOIN pt_backlog backlog  ON backlog.id = bug.backlog_id
                WHERE ${ownerId})
                      AS sprint_details
         WHERE sprint_details.sprint_id = :sprintId
         AND sprint_details.company_id = :companyId
         ORDER BY sprint_details.type DESC, sprint_details.status_id
         LIMIT :resultPerPage OFFSET :start
    """

    String query_sprint_details_count = """
      SELECT COUNT(id)
         FROM
            (SELECT backlog.id AS id, backlog.sprint_id AS sprint_id, backlog.company_id AS company_id
                FROM pt_backlog backlog
                WHERE ${ownerId}
                    UNION
            SELECT bug.id AS id, bug.sprint_id AS sprint_id, bug.company_id AS company_id
                FROM pt_bug bug
                LEFT JOIN pt_backlog backlog  ON backlog.id = bug.backlog_id
                  WHERE ${ownerId})
                      AS sprint_details
         WHERE sprint_details.sprint_id = :sprintId
         AND sprint_details.company_id = :companyId
    """
        Map queryParams = [
                sprintId: sprintId,
                companyId: companyId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstSprintDetails = executeSelectSql(query_sprint_details, queryParams)
        int count = executeSelectSql(query_sprint_details_count, queryParams).first().count
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
        };
        return sprintList;
    }

}
