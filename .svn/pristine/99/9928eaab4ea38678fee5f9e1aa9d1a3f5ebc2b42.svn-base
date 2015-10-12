package com.athena.mis.accounting.actions.accsubaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccSubAccount
import com.athena.mis.accounting.utility.AccSubAccountCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Class to get list of all accSubAccount object(s) associated with specific COA(ChartOfAccount)
 *  For details go through Use-Case doc named 'GetAccSubAccountByCoaIdActionService'
 */
class GetAccSubAccountByCoaIdActionService extends BaseService implements ActionIntf {

    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility

    private static final String LST_ACC_SUB_ACCOUNT = "lstAccSubAccount"
    private static final String FAILURE_MESSAGE = "Failed to get sub-account list"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check existence of required parameter
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE);
            GrailsParameterMap parameters = (GrailsParameterMap) params
            if (!parameters.coaId) { //Check existence of required parameter
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Get list of all accSubAccount object(s) associated with specific COA(ChartOfAccount)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -list of accSubAccount object(s) & boolean value (true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params

            long coaId = Long.parseLong(parameters.coaId.toString())
            List<AccSubAccount> lstAccSubAccount = accSubAccountCacheUtility.searchByCoaIdAndCompany(coaId)
            result.put(LST_ACC_SUB_ACCOUNT, lstAccSubAccount)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
     * do nothing for buildSuccessResultForUI operation
     */
    public Object buildSuccessResultForUI(Object storeResult) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError(true) & relevant error message
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
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }
}
