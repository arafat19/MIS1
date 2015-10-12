package com.athena.mis.projecttrack.actions.ptbacklog

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.entity.PtModule
import com.athena.mis.projecttrack.service.PtAcceptanceCriteriaService
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.utility.PtBacklogPriorityCacheUtility
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Create new PtBacklog object and show in grid
 *  For details go through Use-Case doc named 'CreatePtBacklogActionService'
 */
class CreatePtBacklogActionService extends BaseService implements ActionIntf {

    PtBacklogService ptBacklogService
    SystemEntityService systemEntityService
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

    private static final String BACKLOG_CREATE_SUCCESS_MSG = "Backlog has been successfully saved"
    private static final String BACKLOG_CREATE_FAILURE_MSG = "Backlog has not been saved"
    private static final String BACKLOG = "backlog"

    /**
     * Get parameters from UI and build PtBacklog object
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
            PtBacklog backlog = buildBacklogObject(parameterMap)   // build backlog object
            result.put(BACKLOG, backlog)
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
     * Save ptBacklog object in DB
     * This method is in transactional block and will roll back in case of any exception
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
            PtBacklog backlog = (PtBacklog) preResult.get(BACKLOG)
            PtBacklog savedBacklogObj = ptBacklogService.create(backlog)  // save new backlog object in DB
            result.put(BACKLOG, savedBacklogObj)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }

    /**
     * Show newly created ptBacklog object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            PtBacklog backlog = (PtBacklog) executeResult.get(BACKLOG)
            PtModule module = (PtModule) ptModuleCacheUtility.read(backlog.moduleId)
            SystemEntity priority = (SystemEntity) ptBacklogPriorityCacheUtility.read(backlog.priorityId)
            int ptAcceptanceCriteriaCount = ptAcceptanceCriteriaService.countByCompanyIdAndBacklogId(backlog.companyId, backlog.id)
            AppUser appUser = (AppUser) appUserCacheUtility.read(backlog.createdBy)
            GridEntity object = new GridEntity()    //build grid object
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

    /**
     * Build ptBacklog object
     * @param parameterMap -serialized parameters from UI
     * @return -new ptBacklog object
     */
    private PtBacklog buildBacklogObject(GrailsParameterMap parameterMap) {
        PtBacklog backlog = new PtBacklog(parameterMap)
        backlog.version = 0
        backlog.ownerId = 0L
        backlog.hours = 0f
        backlog.sprintId = 0L
        backlog.idea = "As a " + backlog.actor + " I want to " + backlog.purpose + " so that " + backlog.benefit
        // generated
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        long backlogDefinedReservedId = ptBacklogStatusCacheUtility.DEFINED_RESERVED_ID   // as default
        SystemEntity backlogDefined = (SystemEntity) ptBacklogStatusCacheUtility.readByReservedAndCompany(backlogDefinedReservedId, companyId)
        backlog.statusId = backlogDefined.id

        backlog.companyId = companyId
        backlog.createdOn = new Date()
        backlog.createdBy = ptSessionUtil.appSessionUtil.getAppUser().id
        return backlog
    }
}
