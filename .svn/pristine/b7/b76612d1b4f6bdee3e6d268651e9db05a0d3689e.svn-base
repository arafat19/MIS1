package com.athena.mis.budget.actions.budgsprint

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for sprint CRUD and list of sprint for grid
 *  For details go through Use-Case doc named 'ShowBudgSprintActionService'
 */
class ShowBudgSprintActionService extends BaseService implements ActionIntf {

    @Autowired
    BudgSessionUtil budgSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_FAILURE_MSG = "Failed to load sprint page"
    private static final String LST_SPRINT = "lstSprint"
    private static final String GRID_OBJ = "gridObj"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get sprint list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)
            Map serviceReturn = listSprint()
            List<GroovyRowResult> lstSprint = (List<GroovyRowResult>) serviceReturn.lstSprint
            Integer count = (Integer) serviceReturn.count
            result.put(LST_SPRINT, lstSprint)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap sprint list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj   // cast map returned from execute method
            List<GroovyRowResult> lstSprint = (List<GroovyRowResult>) executeResult.get(LST_SPRINT)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedSprint = wrapSprintList(lstSprint, start)
            Map gridOutput = [page: pageNumber, total: count, rows: lstWrappedSprint]
            result.put(GRID_OBJ, gridOutput)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
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
            if (!obj) {
                result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
                return result
            }
            LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from execute method
            result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

    /**
     * wrap list of sprint in grid entity
     * @param lstSprint -list of sprint objects
     * @param start -starting index of the page
     * @return -list of wrapped sprints
     */
    private List wrapSprintList(List<GroovyRowResult> lstSprint, int start) {
        List lstWrappedSprint = []
        int counter = start + 1
        for (int i = 0; i < lstSprint.size(); i++) {
            GroovyRowResult sprint = lstSprint[i]
            GridEntity obj = new GridEntity()    // build grid object
            obj.id = sprint.id
            obj.cell = [
                    counter,
                    sprint.id,
                    sprint.name,
                    sprint.is_active ? Tools.YES : Tools.NO,
                    sprint.budget_count
            ]
            lstWrappedSprint << obj
            counter++
        }
        return lstWrappedSprint
    }

    private static final String SELECT_QUERY = """
            SELECT sprint.id AS id,sprint.name AS name ,sprint.is_active AS is_active,
                (SELECT COUNT(sprint_budget.id) FROM budg_sprint_budget sprint_budget WHERE sprint_budget.sprint_id = sprint.id) AS budget_count
            FROM budg_sprint sprint
            WHERE  sprint.company_id = :companyId
            ORDER BY sprint.id
            LIMIT :resultPerPage OFFSET :start
    """

    private static final String QUERY_FOR_COUNT = """
        SELECT COUNT(sprint.id) AS count
            FROM budg_sprint sprint
            WHERE  sprint.company_id = :companyId
    """

    /**
     * @return -a map contains list of sprint and count
     */
    private LinkedHashMap listSprint() {

        Map queryParams = [
                companyId: budgSessionUtil.appSessionUtil.getCompanyId(),
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> sprintList = executeSelectSql(SELECT_QUERY, queryParams)
        List countResults = executeSelectSql(QUERY_FOR_COUNT, queryParams)
        int count = countResults[0].count
        return [lstSprint: sprintList, count: count]
    }
}
