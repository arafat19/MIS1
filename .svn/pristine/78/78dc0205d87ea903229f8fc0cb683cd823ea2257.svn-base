package com.athena.mis.accounting.actions.acctier3

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccTier2
import com.athena.mis.accounting.entity.AccTier3
import com.athena.mis.accounting.utility.AccTier2CacheUtility
import com.athena.mis.accounting.utility.AccTier3CacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Class to get list of AccTier3 object(s) associated with specific AccTier2 object
 *  For details go through Use-Case doc named 'GetTier3ListByAccTier2IdActionService'
 */
class GetTier3ListByAccTier2IdActionService extends BaseService implements ActionIntf {


    @Autowired
    AccTier2CacheUtility accTier2CacheUtility
    @Autowired
    AccTier3CacheUtility accTier3CacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String ERROR_MESSAGE = "Failed to load Tier3 list"
    private static final String ACC_TIER3_LIST = "accTier3List"
    private static final String ACC_TIER2_ID = "accTier2Id"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"

    /**
     * Check existence of required parameter
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            // check here the required params are present
            if (!parameterMap.accTier2Id) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

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
     * Get list of AccTier3 object(s) associated with specific AccTier2 object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -list of AccTier3 object(s) & boolean value (true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            int accTier2Id = Integer.parseInt(parameterMap.accTier2Id.toString())
            List<AccTier3> accTier3List = accTier3CacheUtility.listByAccTier2Id(accTier2Id)
            result.put(ACC_TIER3_LIST, Tools.listForKendoDropdown(accTier3List,null,null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            log.error(ex.getMessage())
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
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }
}
