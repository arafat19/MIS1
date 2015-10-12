package com.athena.mis.projecttrack.actions.ptacceptancecriteria

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.projecttrack.entity.PtAcceptanceCriteria
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.service.PtAcceptanceCriteriaService
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.utility.PtAcceptanceCriteriaStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtAcceptanceCriteriaTypeCacheUtility
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Create new AcceptanceCriteria object and show in grid
 *  For details go through Use-Case doc named 'CreatePtAcceptanceCriteriaActionService'
 */
class CreatePtAcceptanceCriteriaActionService extends BaseService implements ActionIntf {

    PtAcceptanceCriteriaService ptAcceptanceCriteriaService
    PtBacklogService ptBacklogService
    SystemEntityService systemEntityService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    PtAcceptanceCriteriaTypeCacheUtility ptAcceptanceCriteriaTypeCacheUtility
    @Autowired
    PtAcceptanceCriteriaStatusCacheUtility ptAcceptanceCriteriaStatusCacheUtility
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String CREATE_SUCCESS_MSG = "Acceptance criteria has been successfully saved"
    private static final String CREATE_FAILURE_MSG = "Acceptance criteria has not been saved"
    private static final String NOT_ALLOWED_TO_CREATE = "Not allowed to create A.C for Accepted Task"
    private static final String ACCEPTANCE_CRITERIA = "acceptanceCriteria"

    /**
     * Get parameters from UI and build AcceptanceCriteria object
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
            if (!parameterMap.backlogId || !parameterMap.type) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long backlogId = Long.parseLong(parameterMap.backlogId.toString())
            PtBacklog backlog = ptBacklogService.read(backlogId)
            SystemEntity blStatusAccepted = (SystemEntity) ptBacklogStatusCacheUtility.readByReservedAndCompany(ptBacklogStatusCacheUtility.ACCEPTED_RESERVED_ID, backlog.companyId)
            if (backlog.statusId == blStatusAccepted.id) {
                result.put(Tools.MESSAGE, NOT_ALLOWED_TO_CREATE)
                return result
            }
            PtAcceptanceCriteria acceptanceCriteria = buildAcceptanceCriteriaObject(parameterMap, backlogId)
            // build backlog object
            result.put(ACCEPTANCE_CRITERIA, acceptanceCriteria)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Save AcceptanceCriteria object in DB
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
            PtAcceptanceCriteria acceptanceCriteria = (PtAcceptanceCriteria) preResult.get(ACCEPTANCE_CRITERIA)
            PtAcceptanceCriteria savedObj = ptAcceptanceCriteriaService.create(acceptanceCriteria)
            // update task(backlog) status = Defined if task status = Completed
            PtBacklog backlog = ptBacklogService.read(acceptanceCriteria.backlogId)
            SystemEntity blStatusCompleted = (SystemEntity) ptBacklogStatusCacheUtility.readByReservedAndCompany(ptBacklogStatusCacheUtility.COMPLETED_RESERVED_ID, acceptanceCriteria.companyId)
            SystemEntity blStatusDefined = (SystemEntity) ptBacklogStatusCacheUtility.readByReservedAndCompany(ptBacklogStatusCacheUtility.DEFINED_RESERVED_ID, acceptanceCriteria.companyId)
            if (backlog.statusId == blStatusCompleted.id) {
                backlog.statusId = blStatusDefined.id
                backlog.updatedBy = ptSessionUtil.appSessionUtil.getAppUser().id
                backlog.updatedOn = new Date()
                ptBacklogService.updateBacklogStatusDefined(backlog)
            }
            // save new backlog object in DB
            result.put(ACCEPTANCE_CRITERIA, savedObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('action failed')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
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
     * Show newly created AcceptanceCriteria object in grid
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
            PtAcceptanceCriteria acceptanceCriteria = (PtAcceptanceCriteria) executeResult.get(ACCEPTANCE_CRITERIA)
            AppUser appUser = (AppUser) appUserCacheUtility.read(acceptanceCriteria.createdBy)
            String accCreatedBy = appUser ? appUser.username : Tools.EMPTY_SPACE
            SystemEntity accCriteriaType = (SystemEntity) ptAcceptanceCriteriaTypeCacheUtility.read(acceptanceCriteria.type)
            SystemEntity acceptanceCriteriaStatus = (SystemEntity) ptAcceptanceCriteriaStatusCacheUtility.read(acceptanceCriteria.statusId)
            GridEntity object = new GridEntity()    //build grid object
            object.id = acceptanceCriteria.id
            object.cell = [
                    Tools.LABEL_NEW,
                    accCriteriaType.key,
                    acceptanceCriteria.criteria,
                    acceptanceCriteriaStatus.key,
                    DateUtility.getDateTimeFormatAsString(acceptanceCriteria.createdOn),
                    accCreatedBy
            ]
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
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
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build AcceptanceCriteria object
     * @param parameterMap -serialized parameters from UI
     * @return -new AcceptanceCriteria object
     */
    private PtAcceptanceCriteria buildAcceptanceCriteriaObject(GrailsParameterMap parameterMap, long backlogId) {
        PtAcceptanceCriteria acceptanceCriteria = new PtAcceptanceCriteria(parameterMap)
        acceptanceCriteria.version = 0
        acceptanceCriteria.completedOn = null

        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        long backlogDefinedReservedId = ptAcceptanceCriteriaStatusCacheUtility.DEFINED_RESERVED_ID
        AppUser user = ptSessionUtil.appSessionUtil.getAppUser()
        SystemEntity backlogDefined = (SystemEntity) ptAcceptanceCriteriaStatusCacheUtility.readByReservedAndCompany(backlogDefinedReservedId, companyId)
        acceptanceCriteria.statusId = backlogDefined.id

        acceptanceCriteria.companyId = companyId
        acceptanceCriteria.backlogId = backlogId
        acceptanceCriteria.createdOn = new Date()
        acceptanceCriteria.createdBy = user.id
        return acceptanceCriteria
    }
}
