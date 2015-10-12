package com.athena.mis.projecttrack.actions.ptbacklog

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Get list of inactive backlog for grid
 *  For details go through Use-Case doc named 'ListInActiveBacklogActionService'
 */
class ListInActiveBacklogActionService extends BaseService implements ActionIntf {

    PtSprintService ptSprintService
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String BACKLOG_NOT_FOUND = "Backlog not found"
    private static final String SPRINT_NOT_FOUND = "Sprint not found"
    private static final String FAILURE_MESSAGE = "Failed to generate task details list"
    private static final String SPRINT_OBJ = "sprintObj"
    private static final String BACKLOG_LIST = "backlogList"
    private static final String GRID_OBJ = "gridObj"

    /**
     * Get parameters from UI and checks required information
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
            initPager(params)
            if (!params.sprintId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long sprintId = Long.parseLong(params.sprintId.toString())
            PtSprint sprint = ptSprintService.read(sprintId)
            if (!sprint) {
                result.put(Tools.MESSAGE, SPRINT_NOT_FOUND)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj   // cast map returned from execute method
            List<GroovyRowResult> backlogList = (List<GroovyRowResult>) executeResult.get(BACKLOG_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstBacklog = wrapBacklogListGrid(backlogList, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstBacklog]

            result.put(GRID_OBJ, gridObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
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
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from execute method
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Get list and count of backlog
     * @param sprintId - sprintId
     * @param lstStatusIds -list of status ids
     * @return -a map containing backlog list and count
     */
    private Map search(long sprintId, List<Long> lstStatusIds) {
        String strIds = Tools.buildCommaSeparatedStringOfIds(lstStatusIds)
        String queryForList = """
            (SELECT backlog.id, 'Task' AS type, backlog.idea AS title, status.key AS status, au.username AS owner,
                backlog.status_id AS status_id
            FROM pt_backlog backlog
            LEFT JOIN system_entity status ON status.id = backlog.status_id
            LEFT JOIN app_user au ON au.id = backlog.owner_id
            WHERE backlog.sprint_id = :sprintId
            AND backlog.status_id IN (${strIds})
            ORDER BY backlog.idea)

            UNION

            (SELECT bug.id AS id, 'Bug' AS type, bug.title AS title, status.key AS status, au.username AS owner,
                bug.status AS status_id
            FROM pt_bug bug
            LEFT JOIN system_entity status ON status.id = bug.status
            LEFT JOIN app_user au ON au.id = bug.owner_id
            WHERE bug.sprint_id = :sprintId
            AND bug.backlog_id = 0)
            ORDER BY type desc, status_id
            LIMIT :resultPerPage OFFSET :start
        """

        String queryForCount = """
            SELECT COUNT(id)
            FROM
                (SELECT backlog.id AS id
                FROM pt_backlog backlog
                WHERE backlog.sprint_id = :sprintId
                AND backlog.status_id IN (${strIds})
                    UNION
                SELECT bug.id AS id
                FROM pt_bug bug
                WHERE bug.sprint_id = :sprintId
                AND bug.backlog_id = 0)
            AS task_details
        """

        Map queryParams = [
                sprintId: sprintId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> backlogList = executeSelectSql(queryForList, queryParams)
        int count = (int) executeSelectSql(queryForCount, queryParams).first().count
        Map result = [backlogList: backlogList, count: count]
        return result
    }

    /**
     * Wrap list of backlog for grid
     * @param lstBacklog -list of backlog
     * @param start -start index
     * @return -list of lstBacklog for grid show
     */
    private List wrapBacklogListGrid(List<GroovyRowResult> lstBacklog, int start) {
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
                    eachRow.owner
            ]
            backlogList << obj
            counter++
        }
        return backlogList
    }
}
