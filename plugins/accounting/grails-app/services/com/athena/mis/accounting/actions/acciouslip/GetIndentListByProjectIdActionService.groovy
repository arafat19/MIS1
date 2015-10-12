package com.athena.mis.accounting.actions.acciouslip

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Get indent list by project id  and show in the drop down of UI
 *  For details go through Use-Case doc named 'GetIndentListByProjectIdActionService'
 */
class GetIndentListByProjectIdActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to load indent list"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String INDENT_LIST = "indentList"
    private static final String INDENT_DATE = "indent_date"

    /**
     * Get parameters from UI and populate indent drop down
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // Check required parameters from UI
            if (!parameterMap.projectId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Get indent list by projectId from procurementImplService
     * This method is in transactional boundary and will roll back in case of any exception
     * @param params -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            long projectId = Long.parseLong(parameterMap.projectId.toString())
            List<GroovyRowResult> indentList = procurementImplService.getIntendListByProjectId(projectId) // get indent list by project id

            result.put(INDENT_LIST, indentList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Show indent list
     * @param obj -map from execute method
     * @return -a map containing indent List for showing in the drop down
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj  // cast map returned form execute method
            List indentList = (List) receiveResult.get(INDENT_LIST)
            indentList = Tools.listForKendoDropdown(indentList, INDENT_DATE, null)
            result = [indentList: indentList]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
}