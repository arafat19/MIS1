package com.athena.mis.projecttrack.actions.ptbacklog

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.entity.PtModule
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtAcceptanceCriteriaService
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.PtBacklogPriorityCacheUtility
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.utility.PtSprintStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class CreateBackLogForSprintActionService extends BaseService implements ActionIntf {

    PtBacklogService ptBacklogService
    PtSprintService ptSprintService
    PtAcceptanceCriteriaService ptAcceptanceCriteriaService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility
    @Autowired
    PtBacklogPriorityCacheUtility ptBacklogPriorityCacheUtility
    @Autowired
    PtSprintStatusCacheUtility ptSprintStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass());

    private static final String OBJ_NOT_FOUND = "object not found"
    private static final String BACKLOG_OBJ = "backlog"
    private static final String BACKLOG_CREATE_FAILURE_MSG = "Backlog has not been saved"
    private static final String NOT_ALLOWED_TO_ADD = "Task can not be added to closed sprint"
    private static final String BACKLOG_CREATE_SUCCESS_MSG = "Backlog has been successfully saved"
    private static final String ALREADY_ADDED_IN_SPRINT = "Selected backlog is already added in the sprint"

    /**
     * Get parameters from UI and build PtBacklog object
     *
     * @param params -serialized parameters from UI
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
            if (!parameterMap.backLogId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long backLogId = Long.parseLong(parameterMap.backLogId.toString())

            PtBacklog oldBackLog = ptBacklogService.read(backLogId) // get PtBacklog object
            // check whether selected ptBacklog object exists or not
            if (!oldBackLog) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            SystemEntity statusClosed = (SystemEntity) ptSprintStatusCacheUtility.readByReservedAndCompany(ptSprintStatusCacheUtility.CLOSED_RESERVED_ID,oldBackLog.companyId)
            PtBacklog backLog = buildPtBackLogObject(parameterMap, oldBackLog)  // build ptBacklog object for update
            PtSprint sprint = ptSprintService.read(backLog.sprintId)
            // backlog must not added to Closed Sprint
            if(sprint.statusId == statusClosed.id){
                result.put(Tools.MESSAGE, NOT_ALLOWED_TO_ADD)
                return result
            }
            int count = ptBacklogService.countBySprintIdAndBacklogIdAndCompanyId(backLog.sprintId, backLog.id, backLog.companyId)
            if (count > 0) {
                result.put(Tools.MESSAGE, ALREADY_ADDED_IN_SPRINT)
                return result
            }
            result.put(BACKLOG_OBJ, backLog)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BACKLOG_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Update ptBacklog object in DB
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
            ptBacklogService.updateBackLogForSprint(backlog)  // update backlog object in DB
            result.put(BACKLOG_OBJ, backlog)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BACKLOG_CREATE_FAILURE_MSG)
            return result
        }
    }

    //do nothing for executePostCondition
    public Object executePostCondition(Object params, Object obj) {
        return null
    }

    /**
     * Show updated ptBacklog object For Sprint in grid
     * Pull a list of PtBacklog for dropDown
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
            GridEntity object = new GridEntity()    // build grid entity object
            PtModule module = (PtModule) ptModuleCacheUtility.read(backlog.moduleId)
            SystemEntity priority = (SystemEntity) ptBacklogPriorityCacheUtility.read(backlog.priorityId)
            int ptAcceptanceCriteriaCount = ptAcceptanceCriteriaService.countByCompanyIdAndBacklogId(backlog.companyId, backlog.id)
            AppUser appUser = (AppUser) appUserCacheUtility.read(backlog.createdBy)
            object.id = backlog.id
            object.cell = [
                    Tools.LABEL_NEW,
                    backlog.id,
                    module.code,
                    priority.key,
                    backlog.purpose,
                    backlog.benefit,
                    ptAcceptanceCriteriaCount,
                    appUser.username
            ]
            result.put(Tools.MESSAGE, BACKLOG_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, backlog.version.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BACKLOG_CREATE_FAILURE_MSG)
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
            result.put(Tools.MESSAGE, BACKLOG_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BACKLOG_CREATE_FAILURE_MSG)
            return result
        }
    }

    private PtBacklog buildPtBackLogObject(GrailsParameterMap parameterMap, PtBacklog oldBackLog) {
        PtBacklog backLog = new PtBacklog(parameterMap)
        oldBackLog.sprintId = backLog.sprintId
        oldBackLog.updatedOn = new Date()
        oldBackLog.updatedBy = ptSessionUtil.appSessionUtil.getAppUser().id
        return oldBackLog
    }
}
