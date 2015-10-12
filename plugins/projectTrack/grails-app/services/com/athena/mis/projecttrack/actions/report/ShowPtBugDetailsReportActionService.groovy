package com.athena.mis.projecttrack.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Get list of Bug Details
 *  For details go through Use-Case doc named 'ShowPtBugDetailsActionService'
 */
class ShowPtBugDetailsReportActionService extends BaseService implements ActionIntf {

    PtSprintService ptSprintService
    @Autowired
    PtSessionUtil ptSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = "Failed to generate bug details report";
    private static final String GRID_OBJ = "gridObj"
    private static final String PROJECT_ID = "projectId"
    private static final String SPRINT_ID = "sprintId"

    /**
     * Do nothing for execute pre condition
     */

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Do nothing for execute pre condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Show list of bug according to backlog id
     * @param parameters -serialized parameters from UI
     * @param obj -null
     * @return- a map containing all object necessary to show in grid
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(GRID_OBJ, Boolean.FALSE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initPager(parameters)
            if (parameterMap.sprintId) {
                long sprintId = Long.parseLong(parameterMap.sprintId)
                PtSprint sprint = ptSprintService.read(sprintId)
                result.put(SPRINT_ID, sprintId.toLong())
                result.put(PROJECT_ID, sprint.projectId)
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
     * Wrap ptBug list for grid & report
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            Long sprintId = (Long) executeResult.get(SPRINT_ID)
            if (!sprintId) {
                result.put(GRID_OBJ, null)
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                return result
            }
            Map bugMap = buildBugDetailsMap(sprintId)
            List<GroovyRowResult> bugList = (List<GroovyRowResult>) bugMap.bugList
            List gridRows = wrapBugListGrid(bugList, start)
            Map gridObj = [page: pageNumber, total: bugMap.count, rows: gridRows]
            result.put(GRID_OBJ, gridObj)
            result.put(PROJECT_ID, executeResult.get(PROJECT_ID))
            result.put(SPRINT_ID, executeResult.get(SPRINT_ID))
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
     * Wrap list of ptBug in grid entity
     * @param lstPtBug -list of ptBug object(s)
     * @param start -starting index of the page
     * @return -list of wrapped ptBug
     */
    private List wrapBugListGrid(List<GroovyRowResult> lstBug, int start) {
        List bugList = []
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
            bugList << obj
            counter++
        };
        return bugList;
    }

    private static final String QUERY_SPRINT_DETAILS = """
        SELECT bug.id AS id, bug.sprint_id AS sprint_id, bug.title AS title, se.key AS status,
                au.username AS created_by, ses.key AS severity
        FROM pt_bug AS bug
            LEFT JOIN app_user au ON au.id = bug.created_by
            LEFT JOIN system_entity se ON se.id = bug.status
            LEFT JOIN system_entity ses ON ses.id = bug.severity
        WHERE bug.sprint_id = :sprintId
            AND bug.company_id = :companyId
        ORDER BY bug.title
        LIMIT :resultPerPage OFFSET :start
    """

    private static final String COUNT_QUERY = """
        SELECT count(bug.id)
        FROM pt_bug AS bug
        WHERE bug.sprint_id = :sprintId
        AND bug.company_id = :companyId
    """

    private Map buildBugDetailsMap(long sprintId) {
        long companyId = ptSessionUtil.appSessionUtil.getAppUser().companyId
        Map queryParams = [
                sprintId: sprintId,
                companyId: companyId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> bugList = executeSelectSql(QUERY_SPRINT_DETAILS, queryParams)
        int count = executeSelectSql(COUNT_QUERY, queryParams).first().count
        Map result = [bugList: bugList, count: count.toInteger()]
        return result

    }

}
