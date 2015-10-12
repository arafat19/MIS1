package com.athena.mis.projecttrack.actions.ptflow

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.projecttrack.entity.PtFlow
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.service.PtFlowService
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class CreatePtFlowActionService extends BaseService implements ActionIntf {
    PtFlowService ptFlowService
    PtBacklogService ptBacklogService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String CREATE_SUCCESS_MSG = "Flow has been successfully saved"
    private static final String CREATE_FAILURE_MSG = "Flow has not been saved"
    private static final String FLOW_OBJECT = "flow"

    /**
     * Get parameters from UI and build PtFlow object
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
            if (!parameterMap.backlogId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long backlogId = Long.parseLong(parameterMap.backlogId.toString())
            PtFlow ptFlow = buildFlowObject(parameterMap, backlogId)
            result.put(FLOW_OBJECT, ptFlow)
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
            PtFlow ptFlow = (PtFlow) preResult.get(FLOW_OBJECT)
            PtFlow savedObj = ptFlowService.create(ptFlow)
            // save new backlog object in DB
            result.put(FLOW_OBJECT, savedObj)
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
     * Show newly created PtFlow object in grid
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
            PtFlow flow = (PtFlow) executeResult.get(FLOW_OBJECT)
            AppUser appUser = (AppUser) appUserCacheUtility.read(flow.createdBy)
            String accCreatedBy = appUser ? appUser.username : Tools.EMPTY_SPACE
            GridEntity object = new GridEntity()    // build grid object
            object.id = flow.id
            object.cell = [
                    Tools.LABEL_NEW,
                    flow.id,
                    flow.flow,
                    DateUtility.getDateTimeFormatAsString(flow.createdOn),
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
     * Build PtFlow object
     * @param parameterMap -serialized parameters from UI
     * @return -new PtFlow object
     */
    private PtFlow buildFlowObject(GrailsParameterMap parameterMap, long backlogId) {
        PtFlow ptFlow = new PtFlow(parameterMap)

        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        AppUser user = ptSessionUtil.appSessionUtil.getAppUser()

        ptFlow.companyId = companyId
        ptFlow.backlogId = backlogId
        ptFlow.createdOn = new Date()
        ptFlow.createdBy = user.id
        return ptFlow
    }
}
