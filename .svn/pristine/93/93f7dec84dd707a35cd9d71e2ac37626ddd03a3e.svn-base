package com.athena.mis.projecttrack.actions.ptflow

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.projecttrack.entity.PtFlow
import com.athena.mis.projecttrack.service.PtFlowService
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class UpdatePtFlowActionService extends BaseService implements ActionIntf {

    PtFlowService ptFlowService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String UPDATE_FAILURE_MESSAGE = "Flow could not be updated"
    private static final String UPDATE_SUCCESS_MESSAGE = "Flow has been updated successfully"
    private static final String OBJ_NOT_FOUND = "Selected flow not found"
    private static final String OBJ_MODIFIED = "Flow already modified. Try again"
    private static final String FLOW_OBJ = "flow"

    /**
     * Get parameters from UI and build AcceptanceCriteria object for update
     * 1. check if id exists in parameterMap
     * 2. check if old AcceptanceCriteria object exists
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
            long flowId = Long.parseLong(parameterMap.id.toString())
            int flowVersion = Integer.parseInt(parameterMap.version.toString())
            // get PtFlow object
            PtFlow oldFlow = (PtFlow) ptFlowService.read(flowId)
            // check whether selected PtFlow object exists or not
            if (!oldFlow) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            if (flowVersion != oldFlow.version) {
                result.put(Tools.MESSAGE, OBJ_MODIFIED)
                return result
            }
            // build PtFlow object for update
            PtFlow ptFlow = buildPtFlowObject(parameterMap, oldFlow)

            result.put(FLOW_OBJ, ptFlow)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update AcceptanceCriteria object in DB
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
            PtFlow ptFlow = (PtFlow) preResult.get(FLOW_OBJ)
            ptFlowService.update(ptFlow)  // update PtFlow object in DB
            result.put(FLOW_OBJ, ptFlow)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
     * Show updated PtFlow object in grid
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
            PtFlow ptFlow = (PtFlow) executeResult.get(FLOW_OBJ)
            GridEntity object = new GridEntity()    // build grid entity object
            AppUser appUser = (AppUser) appUserCacheUtility.read(ptFlow.createdBy)
            String accCreatedBy = appUser ? appUser.username : Tools.EMPTY_SPACE
            object.id = ptFlow.id
            object.cell = [
                    Tools.LABEL_NEW,
                    ptFlow.id,
                    ptFlow.flow,
                    DateUtility.getDateTimeFormatAsString(ptFlow.createdOn),
                    accCreatedBy
            ]
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, ptFlow.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build PtFlow object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldFlow -old PtFlow object
     * @return -updated PtFlow object
     */
    private PtFlow buildPtFlowObject(GrailsParameterMap parameterMap, PtFlow oldFlow) {
        PtFlow ptFlow = new PtFlow(parameterMap)
        oldFlow.flow = ptFlow.flow
        oldFlow.updatedBy = ptSessionUtil.appSessionUtil.getAppUser().id
        oldFlow.updatedOn = new Date()
        return oldFlow
    }
}
