package com.athena.mis.sarb.actions.taskmodel

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.sarb.model.SarbTaskModel
import com.athena.mis.sarb.service.SarbTaskModelService
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class ShowSarbTaskDetailsActionService extends BaseService implements ActionIntf {

    SarbTaskModelService sarbTaskModelService
    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Response of reference could not found"
    private static final String RESPONSE = "response"
    private static final String SARB_REF_NO = "sarbRefNo"
    private static final String DEFAULT_RESPONSE = "No reference found"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get responseOfReference & sarbRefNo from DB
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
                return result
            }
            long id = Long.parseLong(parameterMap.id)
            SarbTaskModel sarbTaskModel = sarbTaskModelService.read(id)
            String responseOfReference = sarbTaskModel.responseOfReference
            String sarbRefNo = sarbTaskModel.sarbRefNo
            if (responseOfReference) {
                result.put(RESPONSE, responseOfReference)
            } else {
                result.put(RESPONSE, DEFAULT_RESPONSE)
            }
            result.put(SARB_REF_NO, sarbRefNo)
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
     * @param obj -map returned from execute method
     * @return -a map containing  objects necessary for show page
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            result.put(RESPONSE, executeResult.get(RESPONSE))
            result.put(SARB_REF_NO, executeResult.get(SARB_REF_NO))
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
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    public Object buildFailureResultForUI(Object obj) {

        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!preResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
                return result
            }
            result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
}
