package com.athena.mis.projecttrack.actions.report.backlog

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.EntityNoteService
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.NoteEntityTypeCacheUtility
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.entity.PtModule
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtAcceptanceCriteriaService
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.service.PtBugService
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.PtBacklogPriorityCacheUtility
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ShowPtBacklogDetailsActionService extends BaseService implements ActionIntf {

    PtBacklogService ptBacklogService
    PtSprintService ptSprintService
    PtBugService ptBugService
    EntityNoteService entityNoteService
    PtAcceptanceCriteriaService ptAcceptanceCriteriaService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility
    @Autowired
    PtBacklogPriorityCacheUtility ptBacklogPriorityCacheUtility
    @Autowired
    NoteEntityTypeCacheUtility noteEntityTypeCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = "Failed to generate backlog details report"
    private static final String NOT_FOUND_MSG = "Backlog not found with given ID"
    private static final String BACKLOG_MAP = "backlogMap"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get object of PtBacklog
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)    // default value = false
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.backlogId) {
                return result
            }
            long backlogId = Long.parseLong(params.backlogId)
            PtBacklog backlog = ptBacklogService.read(backlogId)
            if (!backlog) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, NOT_FOUND_MSG)
                return result
            }
            Map backlogMap = buildMapForBacklog(backlog)
            result.put(BACKLOG_MAP, backlogMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
     * Do nothing for build success result
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
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
     * Build a map with necessary properties of backlog
     * @param backlog -object of PtBacklog
     * @return -a map containing the necessary properties of backlog
     */
    private Map buildMapForBacklog(PtBacklog backlog) {
        String loggedUser = ptSessionUtil.appSessionUtil.getAppUser().username
        AppUser owner = (AppUser) appUserCacheUtility.read(backlog.ownerId)
        AppUser createdBy = (AppUser) appUserCacheUtility.read(backlog.createdBy)
        AppUser acceptedBy = (AppUser) appUserCacheUtility.read(backlog.acceptedBy)
        AppUser completedBy = (AppUser) appUserCacheUtility.read(backlog.updatedBy)
        PtModule module = (PtModule) ptModuleCacheUtility.read(backlog.moduleId)
        PtSprint sprint = ptSprintService.read(backlog.sprintId)
        SystemEntity status = (SystemEntity) ptBacklogStatusCacheUtility.read(backlog.statusId)
        SystemEntity priority = (SystemEntity) ptBacklogPriorityCacheUtility.read(backlog.priorityId)
        int bugCount = ptBugService.countByBacklogIdAndCompanyId(backlog.id, backlog.companyId)
        int acCount = ptAcceptanceCriteriaService.countByCompanyIdAndBacklogId(backlog.companyId, backlog.id)
        SystemEntity noteEntityType = (SystemEntity) noteEntityTypeCacheUtility.readByReservedAndCompany(noteEntityTypeCacheUtility.COMMENT_ENTITY_TYPE_PT_TASK, backlog.companyId)
        int noteCount = entityNoteService.countByEntityTypeIdAndEntityIdAndCompanyId(noteEntityType.id, backlog.id, backlog.companyId)
        String useCaseUrl = backlog.useCaseId
        Map backlogMap = [
                id: backlog.id,
                url: backlog.url,
                useCaseUrl: useCaseUrl,
                owner: owner ? owner.username : Tools.EMPTY_SPACE,
                module: module.name,
                sprint: sprint ? sprint.name : Tools.EMPTY_SPACE,
                createdBy: createdBy.username,
                createdOn: DateUtility.getDateFormatAsString(backlog.createdOn),
                status: status.key,
                priority: priority.key,
                bugCount: bugCount,
                acCount: acCount,
                noteCount: noteCount,
                loggedUser: loggedUser,
                acceptedOn: DateUtility.getDateFormatAsString(backlog.acceptedOn),
                acceptedBy: acceptedBy,
                completedOn: DateUtility.getDateFormatAsString(backlog.completedOn),
                completedBy: completedBy,
                sprintId: backlog.sprintId
        ]
        return backlogMap
    }
}
