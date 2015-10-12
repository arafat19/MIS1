package com.athena.mis.procurement.actions.indentdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcIndentDetails
import com.athena.mis.procurement.service.IndentDetailsService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Select specific object of selected Indent Details at grid row and show for UI
 *  For details go through Use-Case doc named 'SelectIndentDetailsActionService'
 */
class SelectIndentDetailsActionService extends BaseService implements ActionIntf {

    private static final String DETAILS_NOT_FOUND_MSG = "Indent details not found"
    private static final String ERROR_MESSAGE = "Failed to select indent details"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred for invalid input"
    private static final String INDENT_DETAILS_OBJ = "indentDetailsObj"

    private final Logger log = Logger.getLogger(getClass())

    IndentDetailsService indentDetailsService
    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }
    /**
     * Get indent details object by id
     * @param parameters -serialized parameters from UI
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
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            long id = Long.parseLong(parameterMap.id.toString())

            ProcIndentDetails indentDetails = indentDetailsService.read(id)
            if (indentDetails == null) {
                result.put(Tools.MESSAGE, DETAILS_NOT_FOUND_MSG)
                return result
            }

            result.put(INDENT_DETAILS_OBJ, indentDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
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
     * Show selected object on the form
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            ProcIndentDetails indentDetails = (ProcIndentDetails) receiveResult.get(INDENT_DETAILS_OBJ)

            result.put(INDENT_DETAILS_OBJ, indentDetails)
            result.put(Tools.VERSION, indentDetails.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
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
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DETAILS_NOT_FOUND_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }
}
