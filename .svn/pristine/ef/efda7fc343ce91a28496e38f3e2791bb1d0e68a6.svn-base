package com.athena.mis.projecttrack.actions.ptSprint

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 *  Get inactive Sprint list by projectId, used in inactive Story CRUD
 *  For details go through Use-Case doc named 'PtGetInActiveSprintListByProjectIdActionService'
 */
class PtGetInActiveSprintListByProjectIdActionService extends BaseService implements ActionIntf {

    PtSprintService ptSprintService

    private final Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = "Failed to get sprint list"
    private static final String SPRINT_LIST = "sprintList"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get Sprint list by projectId
     * @param params -parameter from UI
     * @param obj -N/A
     * @return -a map containing list of Sprint
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            // get Sprint list by projectId
            List<PtSprint> lstSprint = ptSprintService.findAllByProjectIdAndIsActive(projectId, false)
            List lstDropDown = Tools.listForKendoDropdown(lstSprint, null, null)
            result.put(SPRINT_LIST, lstDropDown)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
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
            LinkedHashMap previousResult = (LinkedHashMap) obj   // cast map returned from previous method
            if (!previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
                return result
            }
            result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }
}
