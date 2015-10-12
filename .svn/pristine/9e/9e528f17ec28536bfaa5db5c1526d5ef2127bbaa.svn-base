package com.athena.mis.projecttrack.actions.ptbacklog

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.service.PtAcceptanceCriteriaService
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.service.PtBugService
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtBugStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class AcceptPtBacklogActionService extends BaseService implements ActionIntf {

    PtBacklogService ptBacklogService
    PtAcceptanceCriteriaService ptAcceptanceCriteriaService
    PtBugService ptBugService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String BACKLOG_ADD_FAILURE_MESSAGE = "Task could not be accepted"
    private static final String BACKLOG_ADD_SUCCESS_MESSAGE = "Task has been accepted successfully"
    private static final String OBJ_NOT_FOUND = "Selected Task not found"
    private static final String BACKLOG_OBJ = "backlog"
    private static final String BUG_FAILURE_MESSAGE = " bug(s) not closed"
    private static final String SPAN_START = "<span style='color:red'>"
    private static final String SPAN_END = "</span>"
    private static final String TASK = "Task"

    private static final String COUNT_QUERY_BUG = """
        SELECT COUNT(id) FROM pt_bug WHERE backlog_id =:backLogId AND status <>:bugStatusId
	"""

    /**
     * Get parameters from UI and build ptBacklog object for update
     * 1. check if id exists in parameterMap
     * 2. check if old ptBacklog object exists
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long backlogId = Long.parseLong(parameterMap.id.toString())
            PtBacklog oldBacklog = ptBacklogService.read(backlogId) // get PtBacklog object
            // check whether selected ptBacklog object exists or not
            if (!oldBacklog) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
            //checking how many bugs are remaining to be fixed
            SystemEntity ptBugStatus = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.CLOSED_RESERVED_ID, companyId)
            Map queryParam = [
                    backLogId: backlogId,
                    bugStatusId: ptBugStatus.id
            ]
            List countResultForBug = executeSelectSql(COUNT_QUERY_BUG, queryParam)
            int queryCountForBug = countResultForBug[0].count
            if (queryCountForBug > 0) {
                result.put(Tools.MESSAGE, queryCountForBug + BUG_FAILURE_MESSAGE)
                return result
            }

            result.put(BACKLOG_OBJ, oldBacklog)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BACKLOG_ADD_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update ptBacklog status as Accepted in DB
     * This function is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            PtBacklog backlog = (PtBacklog) preResult.get(BACKLOG_OBJ)
            SystemEntity acceptedStatus = (SystemEntity) ptBacklogStatusCacheUtility.readByReservedAndCompany(ptBacklogStatusCacheUtility.ACCEPTED_RESERVED_ID, backlog.companyId)
            backlog.statusId = acceptedStatus.id
            backlog.updatedOn = new Date()
            backlog.updatedBy = ptSessionUtil.appSessionUtil.getAppUser().id
            backlog.acceptedBy = ptSessionUtil.appSessionUtil.getAppUser().id
            backlog.acceptedOn = new Date()
            ptBacklogService.updateBacklogStatus(backlog)  // update backlog object in DB
            result.put(BACKLOG_OBJ, backlog)

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BACKLOG_ADD_FAILURE_MESSAGE)
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
     * Show updated ptBacklog object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            PtBacklog backlog = (PtBacklog) executeResult.get(BACKLOG_OBJ)
            Map returnResult = wrapGridObject(backlog)
            GridEntity object = new GridEntity()    // build grid entity object
            object.id = backlog.id
            object.cell = [
                    Tools.LABEL_NEW,
                    TASK,
                    backlog.idea,
                    returnResult.status,
                    returnResult.user,
                    returnResult.acCount,
                    returnResult.bugCount,
                    returnResult.unresolved > 0 ? SPAN_START + returnResult.unresolved + SPAN_END : returnResult.unresolved
            ]
            result.put(Tools.MESSAGE, BACKLOG_ADD_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, backlog.version.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BACKLOG_ADD_FAILURE_MESSAGE)
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
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, BACKLOG_ADD_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BACKLOG_ADD_FAILURE_MESSAGE)
            return result
        }
    }

    private Map wrapGridObject(PtBacklog backlog) {
        AppUser appUser = (AppUser) appUserCacheUtility.read(backlog.ownerId)
        String user = appUser ? appUser.username : Tools.EMPTY_SPACE
        SystemEntity status = (SystemEntity) ptBacklogStatusCacheUtility.read(backlog.statusId)
        // consider re-opened and submitted bugs as un-resolved bugs
        SystemEntity reOpened = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.RE_OPENED_RESERVED_ID, backlog.companyId)
        SystemEntity submitted = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.SUBMITTED_RESERVED_ID, backlog.companyId)

        List unResolvedIds = []
        unResolvedIds << reOpened.id
        unResolvedIds << submitted.id

        int countAc = ptAcceptanceCriteriaService.countByCompanyIdAndBacklogId(backlog.companyId, backlog.id)
        int countBug = ptBugService.countByCompanyIdAndBacklogId(backlog.companyId, backlog.id)
        int unresolved = ptBugService.countByCompanyIdAndBacklogIdAndStatusInList(backlog.companyId, backlog.id, unResolvedIds)

        Map result = [
                status: status.key,
                user: user,
                acCount: countAc,
                bugCount: countBug,
                unresolved: unresolved
        ]
        return result
    }
}
