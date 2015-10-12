package com.athena.mis.projecttrack.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.projecttrack.entity.PtProject
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtProjectService
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.PtBugStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search specific list of ptBug
 *  For details go through Use-Case doc named 'ListPtBugDetailsReportActionService'
 */
class ListPtBugDetailsReportActionService extends BaseService implements ActionIntf {

    PtSprintService ptSprintService
    PtProjectService ptProjectService
    SystemEntityService systemEntityService
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = "Failed to generate bug details report";
    private static final String LIST_BUG_DETAILS = "bugDetailList"
    private static final String GRID_OBJ = "gridObj"
    private static final String BUG_NOT_FOUND = "Bug not found"
    private static final String PROJECT_NOT_FOUND = "Project not found"
    private static final String SPRINT_NOT_FOUND = "Sprint not found"
    private static final String BUG_STATUS_NOT_FOUND = "Bug status not found"
    private static final String STATUS_ID = "statusId"
    private static final String SPRINT_ID = "sprintId"
    private static final String PROJECT_ID = "projectId"

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

            long projectId = Long.parseLong(params.projectId)
            PtProject project = ptProjectService.read(projectId)
            if (!project) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND)
                return result
            }

            initPager(params)
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
     * @param parameters --serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long projectId = Long.parseLong(params.projectId.toString())
            List<Long> sprintIds = []
            if (params.sprintId.equals(Tools.EMPTY_SPACE)) {
                // get Sprint list by projectId
                List<PtSprint> lstSprint = ptSprintService.findAllByProjectIdAndCompanyId(projectId)

                if (lstSprint.size() == 0) {
                    result.put(Tools.MESSAGE, SPRINT_NOT_FOUND)
                    return result
                }
                for (int i = 0; i < lstSprint.size(); i++) {
                    sprintIds << lstSprint[i].id
                }
                result.put(SPRINT_ID, Tools.EMPTY_SPACE)
            } else {
                sprintIds << new Long(params.sprintId.toString())
                result.put(SPRINT_ID, params.sprintId)
            }
            String strSprintIds = Tools.buildCommaSeparatedStringOfIds(sprintIds)

            List<Long> bugStatusIds = []
            if (params.statusId.equals(Tools.EMPTY_SPACE)) {
                List lstBugStatus = ptBugStatusCacheUtility.list()

                if (lstBugStatus.size() == 0) {
                    result.put(Tools.MESSAGE, BUG_STATUS_NOT_FOUND)
                    return result
                }
                for (int i = 0; i < lstBugStatus.size(); i++) {
                    bugStatusIds << lstBugStatus[i].id
                }
                result.put(STATUS_ID, Tools.EMPTY_SPACE)
            } else {
                bugStatusIds << new Long(params.statusId.toString())
                result.put(STATUS_ID, params.statusId)
            }
            String statusIds = Tools.buildCommaSeparatedStringOfIds(bugStatusIds)

            Map bugMap = buildSprintDetailsMap(strSprintIds, statusIds)
            List<GroovyRowResult> bugList = (List<GroovyRowResult>) bugMap.bugList
            if (bugMap.count <= 0) {
                result.put(Tools.MESSAGE, BUG_NOT_FOUND)
                return result
            }
            result.put(LIST_BUG_DETAILS, bugList)
            result.put(Tools.COUNT, bugMap.count)
            result.put(PROJECT_ID, projectId)
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
            List<GroovyRowResult> bugList = (List<GroovyRowResult>) executeResult.get(LIST_BUG_DETAILS)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstBug = wrapBacklogListGrid(bugList, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstBug]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, gridObj)
            result.put(SPRINT_ID, executeResult.get(SPRINT_ID))
            result.put(STATUS_ID, executeResult.get(STATUS_ID))
            result.put(PROJECT_ID, executeResult.get(PROJECT_ID))
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
     *
     * @param sprintId - sprintId
     * @param bugStatusId - bug status id
     * @return - a map containing buglist and count
     */
    private Map buildSprintDetailsMap(String sprintIds, String bugStatusIds) {
        String QUERY_BUG_DETAILS = """
            SELECT bug.id AS id, bug.sprint_id AS sprint_id, bug.title AS title, se.key AS status,
                   au.username AS created_by, ses.key AS severity
               FROM pt_bug AS bug
                 JOIN app_user au ON au.id = bug.created_by
                 JOIN system_entity se ON se.id = bug.status
                 JOIN system_entity ses ON ses.id = bug.severity
            WHERE bug.sprint_id IN (${sprintIds})
            AND bug.status IN (${bugStatusIds})
            ORDER BY ${sortColumn} ${sortOrder}
            LIMIT :resultPerPage OFFSET :start
        """

        String QUERY_BUG_DETAILS_COUNT = """
            SELECT COUNT(bug.id)
               FROM pt_bug AS bug
            WHERE bug.sprint_id IN (${sprintIds})
            AND bug.status IN (${bugStatusIds})
        """
        Map params = [
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> bugList = executeSelectSql(QUERY_BUG_DETAILS, params)
        List<GroovyRowResult> bugListCount = executeSelectSql(QUERY_BUG_DETAILS_COUNT)
        int count = (int) bugListCount[0].count
        Map result = [bugList: bugList, count: count]
        return result
    }
    /**
     *
     * @param lstBug
     * @param start
     * @return - list of bug details for grid show
     */
    private List wrapBacklogListGrid(List<GroovyRowResult> lstBug, int start) {
        List ptBugList = []
        int counter = start + 1
        for (int i = 0; i < lstBug.size(); i++) {
            GroovyRowResult eachRow = lstBug[i]
            GridEntity obj = new GridEntity()    // build grid object
            obj.id = eachRow.id
            obj.cell = [
                    counter,
                    eachRow.id,
                    eachRow.sprint_id,
                    eachRow.created_by,
                    eachRow.title,
                    eachRow.severity,
                    eachRow.status
            ]
            ptBugList << obj
            counter++
        };
        return ptBugList;
    }

}
