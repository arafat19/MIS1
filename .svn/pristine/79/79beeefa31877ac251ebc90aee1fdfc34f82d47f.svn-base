package com.athena.mis.procurement.actions.proctermsandcondition

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcTermsAndCondition
import com.athena.mis.procurement.service.ProcTermsAndConditionService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Select specific object of selected Terms and condition at grid row and show for UI
 *  For details go through Use-Case doc named 'SelectProcTermsAndConditionActionService'
 */
class SelectProcTermsAndConditionActionService extends BaseService implements ActionIntf {

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to edit terms and condition"
    private static final String PROC_TERMS_AND_CONDITION_NOT_FOUND = "Terms and condition not found"
    private static final String PROC_TERMS_AND_CONDITION_OBJ = "procTermsAndCondition"

    private final Logger log = Logger.getLogger(getClass());

    ProcTermsAndConditionService procTermsAndConditionService
    /**
     * 1. pull procTermsAndCondition object by procTermsAndConditionId
     * @param params - serialize parameters from UI
     * @param obj -N/A
     * @return - a map containing procTermsAndCondition object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE);

            GrailsParameterMap paramsMap = (GrailsParameterMap) params
            long procTermsAndConditionId = Long.parseLong(paramsMap.id.toString())

            ProcTermsAndCondition procTermsAndCondition = procTermsAndConditionService.read(procTermsAndConditionId)
            if (!procTermsAndCondition) {
                result.put(Tools.MESSAGE, PROC_TERMS_AND_CONDITION_NOT_FOUND)
                return result
            }
            result.put(PROC_TERMS_AND_CONDITION_OBJ, procTermsAndCondition);
            result.put(Tools.IS_ERROR, Boolean.FALSE);
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Receive terms & conditions from previous method
     * @param parameters -N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing entity
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        LinkedHashMap receiveResult = (LinkedHashMap) obj
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            ProcTermsAndCondition procTermsAndCondition = (ProcTermsAndCondition) receiveResult.get(PROC_TERMS_AND_CONDITION_OBJ)

            result.put(Tools.ENTITY, procTermsAndCondition)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null;
    }
    /**
     * Show selected object on the form
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        LinkedHashMap receiveResult = (LinkedHashMap) obj
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            ProcTermsAndCondition procTermsAndCondition = (ProcTermsAndCondition) receiveResult.get(Tools.ENTITY)
            result.put(Tools.ENTITY, procTermsAndCondition)
            result.put(Tools.VERSION, procTermsAndCondition.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
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
        LinkedHashMap failureResult = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                failureResult.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                failureResult.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return failureResult
        } catch (Exception ex) {
            log.error(ex.getMessage());
            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            failureResult.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return failureResult
        }
    }
}