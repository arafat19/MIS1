package com.athena.mis.projecttrack.actions.ptbacklog

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.entity.PtBug
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

/**
 *  Update PtBacklog object ownerId by AppUser.id and update grid
 *  For details go through Use-Case doc named 'AddMyPtBacklogActionService'
 */
class AddMyPtBacklogActionService extends BaseService implements ActionIntf {

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

    private static final String BACKLOG_ADD_FAILURE_MESSAGE = "Backlog could not be added"
    private static final String BACKLOG_ADD_SUCCESS_MESSAGE = "Backlog has been added successfully"
    private static final String OBJ_NOT_FOUND = "Selected Backlog not found"
    private static final String BACKLOG_OBJ = "backlog"
    private static final String BACKLOG_IDS = "backlogIds"
    private static final String SPAN_START = "<span style='color:red'>"
    private static final String SPAN_END = "</span>"
    private static final String TASK = "Task"
    private static final String OPEN_BACKLOG_MSG = "Open backlog can not be added to my task"

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
            List backlogIds = (List<String>) parameterMap.ids.split(Tools.UNDERSCORE)

            // check required parameter
            if (backlogIds.size() == 0) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            List<PtBacklog> lstBacklog = checkBacklogExistence(backlogIds)
            if (lstBacklog.size() == 0) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            for (int i = 0; i < lstBacklog.size(); i++) {
                PtBacklog backlog = lstBacklog[i]
                if (backlog.sprintId == 0) {
                    result.put(Tools.MESSAGE, OPEN_BACKLOG_MSG)
                    return result
                }
            }
            result.put(BACKLOG_OBJ, lstBacklog)
            result.put(BACKLOG_IDS, (List<Long>) backlogIds)
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
     * Update ptBacklog object ownerId by AppUser.id in DB
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
            List<Long> backlogIds = (List<Long>) preResult.get(BACKLOG_IDS)
            int count = ptBacklogService.addToMyBacklog(backlogIds)  // update backlog object in DB
            if (count <= 0) {
                result.put(BACKLOG_OBJ, BACKLOG_ADD_FAILURE_MESSAGE)
                return result
            }
            List<PtBacklog> lstBacklog = (List<PtBacklog>) preResult.get(BACKLOG_OBJ)
            giveBacklogOwnerIdToBugsOfBacklog(lstBacklog) // assign backlog owner id to bug owner id
            result.put(BACKLOG_OBJ, lstBacklog)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            throw new RuntimeException('Backlog could not be added')
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
    @Transactional(readOnly = true)
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        List<GridEntity> entity = []
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            List<PtBacklog> lstBacklog = (List<PtBacklog>) executeResult.get(BACKLOG_OBJ)
            for (int i = 0; i < lstBacklog.size(); i++) {
                Map returnResult = buildObject(lstBacklog[i])
                GridEntity object = new GridEntity()    // build grid entity object
                object.id = lstBacklog[i].id
                object.cell = [
                        Tools.LABEL_NEW,
                        TASK,
                        lstBacklog[i].idea,
                        returnResult.status,
                        returnResult.user,
                        returnResult.acCount,
                        returnResult.bugCount,
                        returnResult.unresolved > 0 ? SPAN_START + returnResult.unresolved + SPAN_END : returnResult.unresolved
                ]
                entity << object
            }

            result.put(Tools.MESSAGE, BACKLOG_ADD_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, entity)
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

    private Map buildObject(PtBacklog backlog) {
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

    /**
     * Check backlog existence
     * @param backlogIds - List of ids of different backlogs
     * @return - list of backlog(s)
     */
    private List<PtBacklog> checkBacklogExistence(List backlogIds) {
        List lstBacklog = []
        for (int i = 0; i < backlogIds.size(); i++) {
            long backlogId = Long.parseLong(backlogIds[i].toString())
            PtBacklog backlog = ptBacklogService.read(backlogId) // get PtBacklog object
            // check whether selected ptBacklog object exists or not
            if (!backlog) {
                return lstBacklog
            }
            backlog.ownerId = ptSessionUtil.appSessionUtil.getAppUser().id  // change the owner_id by AppUser.id
            lstBacklog << backlog
        }
        return lstBacklog
    }

    private void giveBacklogOwnerIdToBugsOfBacklog(List<PtBacklog> lstBacklog) {
        for (int i = 0; i < lstBacklog.size(); i++) {
            PtBacklog backlog = lstBacklog[i]
            List<PtBug> lstPtBug = ptBugService.findAllByBacklogIdAndCompanyId(backlog.id, backlog.companyId)
            for (int j = 0; j < lstPtBug.size(); j++) {
                PtBug ptBug = lstPtBug[j]
                ptBug.ownerId = backlog.ownerId
            }
        }

    }
}
