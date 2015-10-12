package com.athena.mis.projecttrack.actions.ptbacklog

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.entity.PtModule
import com.athena.mis.projecttrack.service.PtAcceptanceCriteriaService
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.utility.PtBacklogPriorityCacheUtility
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Update Backlog object and grid data
 *  For details go through Use-Case doc named 'UpdatePtBacklogActionService'
 */
class UpdatePtBacklogActionService extends BaseService implements ActionIntf {

    PtBacklogService ptBacklogService
    PtAcceptanceCriteriaService ptAcceptanceCriteriaService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtBacklogPriorityCacheUtility ptBacklogPriorityCacheUtility
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility
    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String BACKLOG_UPDATE_FAILURE_MESSAGE = "Backlog could not be updated"
    private static final String BACKLOG_UPDATE_SUCCESS_MESSAGE = "Backlog has been updated successfully"
    private static final String OBJ_NOT_FOUND = "Selected Backlog not found"
    private static final String OBJ_MODIFIED = "Backlog already modified. Try again"
    private static final String BACKLOG_OBJ = "backlog"

    /**
     * Get parameters from UI and build Backlog object for update
     * 1. check if id exists in parameterMap
     * 2. check if old Backlog object exists
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
            int backlogVersion = Integer.parseInt(parameterMap.version.toString())
            PtBacklog oldBacklog = ptBacklogService.read(backlogId) // get PtBacklog object
            // check whether selected Backlog object exists or not
            if (!oldBacklog) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            if (backlogVersion != oldBacklog.version) {
                result.put(Tools.MESSAGE, OBJ_MODIFIED)
                return result
            }
            PtBacklog backlog = buildBacklogObject(parameterMap, oldBacklog)  // build ptBacklog object for update

            result.put(BACKLOG_OBJ, backlog)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BACKLOG_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update Backlog object in DB
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
            ptBacklogService.update(backlog)  // update backlog object in DB
            result.put(BACKLOG_OBJ, backlog)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BACKLOG_UPDATE_FAILURE_MESSAGE)
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
     * Show updated Backlog object in grid
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
            PtModule module = (PtModule) ptModuleCacheUtility.read(backlog.moduleId)
            SystemEntity priority = (SystemEntity) ptBacklogPriorityCacheUtility.read(backlog.priorityId)
            int ptAcceptanceCriteriaCount = ptAcceptanceCriteriaService.countByCompanyIdAndBacklogId(backlog.companyId, backlog.id)
            AppUser appUser = (AppUser) appUserCacheUtility.read(backlog.createdBy)
            GridEntity object = new GridEntity()
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
            result.put(Tools.MESSAGE, BACKLOG_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, backlog.version.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BACKLOG_UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, BACKLOG_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BACKLOG_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build Backlog object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldBacklog -old Backlog object
     * @return -updated Backlog object
     */
    private PtBacklog buildBacklogObject(GrailsParameterMap parameterMap, PtBacklog oldBacklog) {
        PtBacklog newBacklog = new PtBacklog(parameterMap)
        oldBacklog.actor = newBacklog.actor
        oldBacklog.benefit = newBacklog.benefit
        oldBacklog.purpose = newBacklog.purpose
        oldBacklog.useCaseId = newBacklog.useCaseId
        oldBacklog.url = newBacklog.url
        // generated
        oldBacklog.idea = "As a " + newBacklog.actor + " I want to " + newBacklog.purpose + " so that " + newBacklog.benefit
        oldBacklog.moduleId = newBacklog.moduleId
        oldBacklog.priorityId = newBacklog.priorityId
        oldBacklog.updatedOn = new Date()
        oldBacklog.updatedBy = ptSessionUtil.appSessionUtil.getAppUser().id
        return oldBacklog
    }

}
