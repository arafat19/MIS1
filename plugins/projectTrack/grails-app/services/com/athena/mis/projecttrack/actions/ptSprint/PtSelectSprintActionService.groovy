package com.athena.mis.projecttrack.actions.ptSprint

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 *  Select ptSprint object and show in UI for editing
 *  For details go through Use-Case doc named 'PtSelectSprintActionService'
 */
class PtSelectSprintActionService extends BaseService implements ActionIntf {

    PtSprintService ptSprintService

    private final Logger log = Logger.getLogger(getClass());

    private static final String SPRINT_NOT_FOUND_MASSAGE = "Selected ptSprint is not found"
    private static final String START_DATE = "startDate"
    private static final String END_DATE = "endDate"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get ptSprint object by id
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long ptSprintId = Long.parseLong(parameterMap.id)
            PtSprint sprint = ptSprintService.read(ptSprintId)
            if (!sprint) {
                result.put(Tools.MESSAGE, SPRINT_NOT_FOUND_MASSAGE)
                return result
            }
            result.put(Tools.ENTITY, sprint)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_NOT_FOUND_MASSAGE)
            return result
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null;
    }
    /**
     * Build a map with ptSprint object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            PtSprint ptSprint = (PtSprint) executeResult.get(Tools.ENTITY)
            String startDate = DateUtility.getDateForUI(ptSprint.startDate)
            String endDate = DateUtility.getDateForUI(ptSprint.endDate)
            result.put(Tools.ENTITY, ptSprint)
            result.put(START_DATE, startDate)
            result.put(END_DATE, endDate)
            result.put(Tools.VERSION, ptSprint.version.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_NOT_FOUND_MASSAGE)
            return result
        }
    }
    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!obj) {
                result.put(Tools.MESSAGE, SPRINT_NOT_FOUND_MASSAGE)
                return result
            }
            Map preResult = (Map) obj
            result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_NOT_FOUND_MASSAGE)
            return result
        }
    }
}
