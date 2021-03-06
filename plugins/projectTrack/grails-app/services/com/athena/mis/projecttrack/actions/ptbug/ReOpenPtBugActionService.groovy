package com.athena.mis.projecttrack.actions.ptbug

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.entity.PtBug
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.service.PtBugService
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.*
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ReOpenPtBugActionService extends BaseService implements ActionIntf {

    PtBugService ptBugService
    PtBacklogService ptBacklogService
    PtSprintService ptSprintService
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtBugSeverityCacheUtility ptBugSeverityCacheUtility
    @Autowired
    PtSprintStatusCacheUtility ptSprintStatusCacheUtility
    @Autowired
    PtBugTypeCacheUtility ptBugTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private static final String BUG_REOPENED_SUCCESS_MESSAGE = "Bug has been re-opened successfully"
    private static final String BUG_REOPENED_FAILURE_MESSAGE = "Bug could not be re-opened"
    private static final String BUG_CAN_NOT_RE_OPENED = "Submitted bug can not be re-opened"
    private static final String BUG_ALREADY_RE_OPENED = "Selected bug already re-opened"
    private static final String OBJ_NOT_FOUND = "Selected bug not found"
    private static final String BUG_OBJ = "bugObj"
    private static final String BUG = "Bug"

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
            long bugId = Long.parseLong(parameterMap.id.toString())
            PtBug oldBug = ptBugService.read(bugId) // get PtBug object

            // is bug already re-opened checking
            SystemEntity reOpenedSubmitted = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.RE_OPENED_RESERVED_ID, oldBug.companyId)
            if (reOpenedSubmitted.id == oldBug.status) {
                result.put(Tools.MESSAGE, BUG_ALREADY_RE_OPENED)
                return result
            }

            // is the bug status submitted checking
            SystemEntity statusSubmitted = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.SUBMITTED_RESERVED_ID, oldBug.companyId)
            if (statusSubmitted.id == oldBug.status) {
                result.put(Tools.MESSAGE, BUG_CAN_NOT_RE_OPENED)
                return result
            }

            // check whether selected ptBug object exists or not
            if (!oldBug) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            result.put(BUG_OBJ, oldBug)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUG_REOPENED_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }

    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            PtBug ptBug = (PtBug) preResult.get(BUG_OBJ)

            SystemEntity reOpenedStatus = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.RE_OPENED_RESERVED_ID, ptBug.companyId)
            ptBug.statusUpdatedBy = ptSessionUtil.appSessionUtil.getAppUser().id
            ptBug.statusUpdatedOn = new Date()
            ptBug.status = reOpenedStatus.id

            if (ptBug.backlogId > 0) {
                PtBacklog ptBacklog = ptBacklogService.read(ptBug.backlogId) // get PtBacklog object
                SystemEntity acceptedBacklogStatus = (SystemEntity) ptBacklogStatusCacheUtility.readByReservedAndCompany(ptBacklogStatusCacheUtility.ACCEPTED_RESERVED_ID, ptBacklog.companyId)
                SystemEntity completedBacklog = (SystemEntity) ptBacklogStatusCacheUtility.readByReservedAndCompany(ptBacklogStatusCacheUtility.COMPLETED_RESERVED_ID, ptBacklog.companyId)
                if (acceptedBacklogStatus.id == ptBacklog.statusId || completedBacklog.id == ptBacklog.statusId) {
                    SystemEntity backlogStatus = (SystemEntity) ptBacklogStatusCacheUtility.readByReservedAndCompany(ptBacklogStatusCacheUtility.DEFINED_RESERVED_ID, ptBacklog.companyId)
                    ptBacklog.statusId = backlogStatus.id
                    ptBacklog.updatedOn = new Date()
                    ptBacklog.updatedBy = ptSessionUtil.appSessionUtil.getAppUser().id
                    ptBacklogService.updateBacklogStatusForReOpen(ptBacklog)  // update backlog object in DB
                }
            }

            PtSprint ptSprint = ptSprintService.read(ptBug.sprintId)
            // if sprint is already is closed then bug will be orphan bug(bug.sprintId = 0)
            SystemEntity closedStatus = (SystemEntity) ptSprintStatusCacheUtility.readByReservedAndCompany(ptSprintStatusCacheUtility.CLOSED_RESERVED_ID, ptBug.companyId)
            if (closedStatus.id == ptSprint.statusId) {
                ptBug.sprintId = 0L
            }
            ptBug.updatedOn = new Date()
            ptBug.updatedBy = ptSessionUtil.appSessionUtil.getAppUser().id
            ptBug.closedBy = 0L
            ptBug.fixedBy = 0L
            ptBug.closedOn = null
            ptBug.fixedOn = null

            int updateCount = ptBugService.updateBugSprint(ptBug)  // update bug object in DB
            if (updateCount <= 0) {
                result.put(Tools.MESSAGE, BUG_REOPENED_FAILURE_MESSAGE)
                return result
            }
            result.put(BUG_OBJ, ptBug)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            throw new RuntimeException('Error occurred while update bug information')
            result.put(Tools.MESSAGE, BUG_REOPENED_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build grid object to show a single row (newly created object) in grid
     * 1. 1. Get status key by id(status)
     * 2. Get severity key by id(severity)
     * 3. Get type key by id(type)
     * 4. Get appUser by id
     * 5. build success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj               // cast map returned from execute method
            PtBug bug = (PtBug) executeResult.get(BUG_OBJ)
            SystemEntity status = (SystemEntity) ptBugStatusCacheUtility.read(bug.status)
            GridEntity object = new GridEntity()                            // build grid object
            object.id = bug.id
            if (bug.backlogId > 0) {
                SystemEntity severity = (SystemEntity) ptBugSeverityCacheUtility.read(bug.severity)
                SystemEntity type = (SystemEntity) ptBugTypeCacheUtility.read(bug.type)
                object.cell = [
                        Tools.LABEL_NEW,
                        bug.id,
                        bug.title,
                        bug.stepToReproduce,
                        status.key,
                        severity.key,
                        type.key,
                        DateUtility.getLongDateForUI(bug.createdOn),
                        ptSessionUtil.appSessionUtil.getAppUser().username,
                        bug.hasAttachment ? Tools.YES : Tools.NO
                ]
            } else {
                AppUser owner = (AppUser) appUserCacheUtility.read(bug.ownerId)
                object.cell = [
                        Tools.LABEL_NEW,
                        BUG,
                        bug.title,
                        status.key,
                        owner.username,
                        Tools.EMPTY_SPACE,
                        Tools.EMPTY_SPACE,
                        Tools.EMPTY_SPACE
                ]
            }
            result.put(Tools.MESSAGE, BUG_REOPENED_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUG_REOPENED_FAILURE_MESSAGE)
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
                LinkedHashMap preResult = (LinkedHashMap) obj               // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, BUG_REOPENED_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUG_REOPENED_FAILURE_MESSAGE)
            return result
        }
    }

}
