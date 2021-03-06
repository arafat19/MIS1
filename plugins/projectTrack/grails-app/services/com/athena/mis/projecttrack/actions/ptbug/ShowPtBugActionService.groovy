package com.athena.mis.projecttrack.actions.ptbug

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.service.PtBugService
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtBugStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Get list of Bug
 * For details go through Use-Case doc named 'ShowPtBugActionService'
 */
class ShowPtBugActionService extends BaseService implements ActionIntf {

    PtBugService ptBugService
    SystemEntityService systemEntityService
    PtBacklogService ptBacklogService
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String BUG_STATUS_SUBMITTED = "submittedBugId"
    private static final String LST_BUGS = "lstBugs"
    private static final String DEFAULT_FAILURE_MSG_SHOW_PT_BUG = "Failed to load bug page"
    private static final String GRID_OBJ = "gridObj"
    private static final String BACKLOG_ID = "backlogId"
    private static final String LOGGED_USER = "loggedUser"
    private static final String CURRENT_BACKLOG_STATUS = "backlogStatus"
    private static final String IS_ADMIN = "roleAdmin"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * 1. Get Bug list by backlogId and companyId for grid
     * 2. Get count of total Bug by backlogId and companyId
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap();
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initPager(parameterMap)                // initialize parameters
            long backlogId = Long.parseLong(parameterMap.backlogId.toString())
            PtBacklog backlog = (PtBacklog) ptBacklogService.read(backlogId)
            SystemEntity currentStatus = (SystemEntity) ptBacklogStatusCacheUtility.read(backlog.statusId)

            Map serviceReturn = list(backlogId)

            List<GroovyRowResult> lstBugs = serviceReturn.bugList        // get list of Bug from DB
            int count = (Integer) serviceReturn.count

            // get count of total Bug from DB
            result.put(LST_BUGS, lstBugs)
            result.put(Tools.COUNT, count)
            result.put(BACKLOG_ID, backlogId.toLong())
            result.put(CURRENT_BACKLOG_STATUS, currentStatus.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PT_BUG)
            return result
        }
    }

    /**
     * Wrap Bug list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj                   // cast map returned from execute method
            List<GroovyRowResult> lstBugs = (List<GroovyRowResult>) executeResult.get(LST_BUGS)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            Long backlogId = (Long) executeResult.get(BACKLOG_ID)
            List lstWrappedBugs = wrapBug(lstBugs, start)         // Wrap list of PtBug in grid entity
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedBugs]
            long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
            String loggedUser = ptSessionUtil.appSessionUtil.getAppUser().username
            boolean isAdmin = ptSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_SOFTWARE_PROJECT_ADMIN)
            SystemEntity bugStatusSubmitted = systemEntityService.findByReservedIdAndCompanyId(ptBugStatusCacheUtility.SUBMITTED_RESERVED_ID, companyId)
            result.put(BUG_STATUS_SUBMITTED, bugStatusSubmitted.id)
            result.put(BACKLOG_ID, backlogId)
            result.put(GRID_OBJ, gridObj)
            result.put(CURRENT_BACKLOG_STATUS, executeResult.get(CURRENT_BACKLOG_STATUS))
            result.put(LOGGED_USER, loggedUser)
            result.put(IS_ADMIN, isAdmin)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PT_BUG)
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PT_BUG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_PT_BUG)
            return result
        }
    }

    /**
     * Wrap list of Bug in grid entity
     * @param lstBugs -list of Bug object(s)
     * @param start -starting index of the page
     * @return -list of wrapped Bugs
     */
    private List wrapBug(List<GroovyRowResult> lstBugs, int start) {
        List lstWrappedBugs = []
        int counter = start + 1
        for (int i = 0; i < lstBugs.size(); i++) {
            GroovyRowResult groovyRowResult = lstBugs[i]
            GridEntity obj = new GridEntity()
            obj.id = groovyRowResult.id
            obj.cell = [
                    counter,
                    groovyRowResult.id,
                    groovyRowResult.title,
                    groovyRowResult.step_to_reproduce,
                    groovyRowResult.status,
                    groovyRowResult.severity,
                    groovyRowResult.type,
                    DateUtility.getLongDateForUI(groovyRowResult.created_on),
                    groovyRowResult.created_by,
                    groovyRowResult.has_attachment ? Tools.YES : Tools.NO
            ]
            lstWrappedBugs << obj
            counter++
        }
        return lstWrappedBugs
    }
    private static final String QUERY_STR = """
            SELECT bug.id, bug.version, bug.title title, bug.step_to_reproduce step_to_reproduce, status.key status, severity.key severity,
                         type.key as type, bug.created_on created_on, au.username created_by, bug.has_attachment has_attachment
            FROM pt_bug bug
                LEFT JOIN app_user au ON bug.created_by = au.id
                LEFT JOIN system_entity status ON bug.status = status.id
                LEFT JOIN system_entity severity ON bug.severity = severity.id
                LEFT JOIN system_entity type ON bug.type = type.id
            WHERE bug.company_id = :companyId
            AND bug.backlog_id = :backlogId
            ORDER BY title
            LIMIT :resultPerPage OFFSET :start
        """

    private static final String QUERY_FOR_COUNT = """
        SELECT COUNT(bug.id) AS count
        FROM pt_bug bug
            LEFT JOIN app_user au ON bug.created_by = au.id
            LEFT JOIN system_entity status ON bug.status = status.id
            LEFT JOIN system_entity severity ON bug.severity = severity.id
            LEFT JOIN system_entity type ON bug.type = type.id
        WHERE bug.company_id = :companyId
        AND bug.backlog_id = :backlogId
    """
    /**
     * Get list of Bug object by backlogId and companyId
     * @param baseService
     * @return -list of Bugs list
     */
    private LinkedHashMap list(long backlogId) {
        Map queryParams = [
                resultPerPage: resultPerPage,
                start: start,
                companyId: ptSessionUtil.appSessionUtil.getCompanyId(),
                backlogId: backlogId
        ]
        List<GroovyRowResult> lstBugs = executeSelectSql(QUERY_STR, queryParams)
        List countResults = executeSelectSql(QUERY_FOR_COUNT, queryParams)
        int count = countResults[0].count
        return [bugList: lstBugs, count: count]
    }
}
