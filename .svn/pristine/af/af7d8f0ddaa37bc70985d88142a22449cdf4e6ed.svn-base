package com.athena.mis.projecttrack.actions.ptSprint

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.entity.PtProject
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtBugService
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.PtProjectCacheUtility
import com.athena.mis.projecttrack.utility.PtSprintStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new ptSprint object and show in grid
 *  For details go through Use-Case doc named 'PtCreateSprintActionService'
 */
class PtCreateSprintActionService extends BaseService implements ActionIntf {

    PtSprintService ptSprintService
    PtBugService ptBugService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility
    @Autowired
    PtSprintStatusCacheUtility ptSprintStatusCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String SPRINT_CREATE_SUCCESS_MSG = "Sprint has been successfully saved"
    private static final String SPRINT_CREATE_FAILURE_MSG = "Sprint has not been saved"
    private static final String DATE_EXIST_MESSAGE = "This date-range over-laps another sprint"
    private static final String HAS_ACTIVE_MSG = "The selected project already has an active sprint"
    private static final String SPRINT = "ptSprint"
    private static final String DATE_FORMAT = "dd-MMM-yy"
    private static final String TO = " To "
    private static final String FROM = " From "

    /**
     * Get parameters from UI and build ptSprint object
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            PtSprint sprint = buildSprintObject(parameterMap)
            if (sprint.isActive) {
                int count = ptSprintService.countByProjectIdAndIsActiveAndIdNotEqual(sprint.projectId, true, sprint.id)
                if (count > 0) {
                    result.put(Tools.MESSAGE, HAS_ACTIVE_MSG)
                    return result
                }
            }
            //check duplicate sprint date range
            int countOverLap = ptSprintService.countDateRangeOverLap(sprint.startDate, sprint.endDate, projectId)
            if (countOverLap > 0) {
                result.put(Tools.MESSAGE, DATE_EXIST_MESSAGE)
                return result
            }
            result.put(SPRINT, sprint)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Save ptSprint object in DB
     * This method is in transactional block and will roll back in case of any exception
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
            LinkedHashMap preResult = (LinkedHashMap) obj
            PtSprint sprint = (PtSprint) preResult.get(SPRINT)
            PtSprint savedSprintObj = ptSprintService.create(sprint)
            result.put(SPRINT, savedSprintObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show newly created ptSprint object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            PtSprint sprint = (PtSprint) executeResult.get(SPRINT)
            int backlogCount = 0
            int bugCount = ptBugService.countBySprintIdAndCompanyId(sprint.id, sprint.companyId)
            SystemEntity status = (SystemEntity) ptSprintStatusCacheUtility.read(sprint.statusId)
            GridEntity object = new GridEntity()
            object.id = sprint.id
            object.cell = [
                    Tools.LABEL_NEW,
                    sprint.id,
                    sprint.name,
                    status.key,
                    sprint.isActive ? Tools.YES : Tools.NO,
                    backlogCount,
                    bugCount
            ]
            result.put(Tools.MESSAGE, SPRINT_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_CREATE_FAILURE_MSG)
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
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, SPRINT_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build ptSprint object
     * @param parameterMap -serialized parameters from UI
     * @return -new ptSprint object
     */
    private PtSprint buildSprintObject(GrailsParameterMap parameterMap) {
        PtSprint sprint = new PtSprint(parameterMap)
        sprint.startDate = DateUtility.parseMaskedDate(parameterMap.startDate.toString())
        sprint.endDate = DateUtility.parseMaskedDate(parameterMap.endDate.toString())
        PtProject ptProject = (PtProject) ptProjectCacheUtility.read(sprint.projectId)
        sprint.name = ptProject.name + FROM + sprint.startDate.format(DATE_FORMAT) + TO + sprint.endDate.format(DATE_FORMAT)
        sprint.companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        return sprint
    }
}
