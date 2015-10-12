package com.athena.mis.projecttrack.actions.ptbug

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.entity.PtBug
import com.athena.mis.projecttrack.service.PtBugService
import com.athena.mis.projecttrack.utility.PtBugSeverityCacheUtility
import com.athena.mis.projecttrack.utility.PtBugStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtBugTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Update ptBug object
 *  For details go through Use-Case doc named 'UpdatePtBugForMyTaskActionService'
 */
class UpdatePtBugForMyTaskActionService extends BaseService implements ActionIntf {

    PtBugService ptBugService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility
    @Autowired
    PtBugSeverityCacheUtility ptBugSeverityCacheUtility
    @Autowired
    PtBugTypeCacheUtility ptBugTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String OBJ_NOT_FOUND = "Selected bug not found"
    private static final String BUG_OBJ = "ptBug"
    private static final String BUG_UPDATE_FAILURE_MESSAGE = "Bug could not be updated"
    private static final String BUG_UPDATE_SUCCESS_MESSAGE = "Bug has been updated successfully"

    /**
     * Get parameters from UI and build ptBug object for update
     * 1. Check validity for input
     * 2. Check existence of PtBug object
     * 3. Build ptBug object with new parameters
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long bugId = Long.parseLong(parameterMap.id.toString())
            PtBug oldBug = ptBugService.read(bugId)                         // Get ptBug object from DB
            if (!oldBug) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            long oldStatus = oldBug.status
            PtBug bug = buildBugObject(parameterMap, oldBug)              // Build ptBug object
            if (bug.status != oldStatus) {
                bug.statusUpdatedBy = bug.updatedBy
                bug.statusUpdatedOn = new Date()
            }

            SystemEntity fixedStatus = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.FIXED_RESERVED_ID, oldBug.companyId)
            if (fixedStatus.id == bug.status) {
                oldBug.fixedOn = new Date()
                oldBug.fixedBy = bug.updatedBy
            }

            result.put(BUG_OBJ, bug)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUG_UPDATE_FAILURE_MESSAGE)
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
     * Update ptBug object in DB
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            PtBug bug = (PtBug) preResult.get(BUG_OBJ)
            ptBugService.update(bug)                                  // update new ptBug object in DB
            result.put(BUG_OBJ, bug)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUG_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build grid object to show a single row (updated object) in grid
     * 1. Get status key by id(status)
     * 2. Get severity key by id(severity)
     * 3. Get type key by id(type)
     * 4. Get appUser by id
     * 5. build success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj                   // cast map returned from execute method
            PtBug bug = (PtBug) executeResult.get(BUG_OBJ)
            long statusId = bug.status
            long severityId = bug.severity
            long typeId = bug.type
            long createdById = bug.createdBy
            SystemEntity status = (SystemEntity) ptBugStatusCacheUtility.read(statusId)
            // Pull SystemEntity object by id for status
            SystemEntity severity = (SystemEntity) ptBugSeverityCacheUtility.read(severityId)
            // Pull SystemEntity object by id for severity
            SystemEntity type = (SystemEntity) ptBugTypeCacheUtility.read(typeId)
            // Pull SystemEntity object by id for type
            AppUser appUser = (AppUser) appUserCacheUtility.read(createdById)
            GridEntity object = new GridEntity()                                // build grid object
            object.id = bug.id
            object.cell = [
                    Tools.LABEL_NEW,
                    bug.id,
                    bug.title,
                    bug.stepToReproduce,
                    status.key,
                    severity.key,
                    type.key,
                    DateUtility.getLongDateForUI(bug.createdOn),
                    appUser.username,
                    bug.hasAttachment
            ]
            result.put(Tools.MESSAGE, BUG_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, bug.version.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUG_UPDATE_FAILURE_MESSAGE)
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, BUG_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUG_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Make existing/old object Up-to-date with new parameters
     * @param parameterMap -serialized parameters from UI
     * @param oldBug -old ptBug object
     * @return -updated ptBug object
     */
    private PtBug buildBugObject(GrailsParameterMap parameterMap, PtBug oldBug) {
        oldBug.note = parameterMap.note
        oldBug.status = Long.parseLong(parameterMap.status.toString())
        oldBug.updatedOn = new Date()
        oldBug.updatedBy = ptSessionUtil.appSessionUtil.getAppUser().id
        oldBug.fixedOn = null
        oldBug.fixedBy = 0L
        return oldBug
    }
}
