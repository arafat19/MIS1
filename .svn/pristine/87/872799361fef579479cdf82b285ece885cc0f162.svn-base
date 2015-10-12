package com.athena.mis.projecttrack.actions.ptbug

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.projecttrack.utility.PtBugStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Show list of bug added in my assignment and UI for update bug status
 * For details go through Use-Case doc named 'ShowMyPtBugActionService'
 */
class ShowMyPtBugActionService extends BaseService implements ActionIntf {

    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_FAILURE_MSG = "Failed to load my bug page"
    private static final String LST_BUG = "lstBug"
    private static final String GRID_OBJ = "gridObj"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get ptBug list for grid in My Bug
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
            initPager(parameterMap) // initialize parameters for flexGrid
            Map serviceReturn = listMyPtBug()
            List<GroovyRowResult> lstBug = serviceReturn.lstBug
            int count = serviceReturn.count
            result.put(LST_BUG, lstBug)
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
     * do nothing for pre condition
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }

    /**
     * Wrap ptBug list for grid in MyBug
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj   // cast map returned from execute method
            List lstBug = (List) executeResult.get(LST_BUG)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedBugs = wrapMyBugs(lstBug, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedBugs]
            result.put(GRID_OBJ, gridObj)
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
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

    /**
     * Wrap list of ptBug in grid entity for MyBug
     * @param lstBug -list of ptBug object(s)
     * @param start -starting index of the page
     * @return -list of wrapped ptBugs
     */
    private List wrapMyBugs(List<GroovyRowResult> lstBug, int start) {
        List lstWrappedBugs = []
        int counter = start + 1
        for (int i = 0; i < lstBug.size(); i++) {
            GroovyRowResult bug = lstBug[i]
            GridEntity obj = new GridEntity()
            obj.id = bug.id
            obj.cell = [
                    counter,
                    bug.id,
                    bug.title,
                    bug.step_to_reproduce,
                    bug.status,
                    bug.severity,
                    bug.type,
                    DateUtility.getLongDateForUI(bug.created_on),
                    bug.created_by,
                    bug.has_attachment
            ]
            lstWrappedBugs << obj
            counter++
        }
        return lstWrappedBugs
    }

    /**
     * Get list of bug and count
     * @return -a map containing list of bug and count
     */
    private LinkedHashMap listMyPtBug() {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity statusClosed = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.CLOSED_RESERVED_ID, companyId)

        String queryStr = """
            SELECT bug.id, bug.title, bug.step_to_reproduce, status.key AS status, severity.key AS severity, type.key AS type,
                bug.created_on, created_by.username AS created_by, bug.has_Attachment
            FROM pt_bug bug
            LEFT JOIN system_entity status ON status.id = bug.status
            LEFT JOIN system_entity severity ON severity.id = bug.severity
            LEFT JOIN system_entity type ON type.id = bug.type
            LEFT JOIN app_user created_by ON created_by.id = bug.created_by
            WHERE bug.owner_id = :ownerId
            AND bug.company_id = :companyId
            AND bug.status <> :closedStatusId
            LIMIT :resultPerPage OFFSET :start
        """

        String countQuery = """
            SELECT COUNT(bug.id)
            FROM pt_bug bug
            WHERE bug.owner_id = :ownerId
            AND bug.company_id = :companyId
            AND bug.status <> :closedStatusId
        """

        Map queryParams = [
                ownerId: ptSessionUtil.appSessionUtil.getAppUser().id,
                companyId: companyId,
                closedStatusId: statusClosed.id,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> lstBug = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(countQuery, queryParams)
        int count = countResults[0].count
        return [lstBug: lstBug, count: count]
    }
}
