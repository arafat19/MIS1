package com.athena.mis.accounting.actions.acctier1

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccTier1
import com.athena.mis.accounting.entity.AccType
import com.athena.mis.accounting.utility.AccTier1CacheUtility
import com.athena.mis.accounting.utility.AccTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
/**
 *  Get tier1 list by acc-type
 *  Used in onchange method of account-type drop-down
 *  - to populate tier1 drop-down list in(Chart of Account, Tier2 & Tier3)
 *  For details go through Use-Case doc named 'GetTier1ListByAccTypeIdActionService'
 */
class GetTier1ListByAccTypeIdActionService extends BaseService implements ActionIntf {

    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String ERROR_MESSAGE = "Failed to load Tier1 list"
    private static final String LST_TIER1 = "lstTier1"
    private static final String ACC_TYPE = "accType"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occured due to invalid input."
    /**
     * Get parameters from UI and build AccType object
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing AccType object
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            // check here the required params are present
            if (!parameterMap.accTypeId) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            Long accTypeId = Long.parseLong(parameterMap.accTypeId.toString())
            AccType accType = (AccType) accTypeCacheUtility.read(accTypeId)
            if (!accType) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            result.put(ACC_TYPE, accType)
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
     * Get tier1 list by acc-type for drop-down
     * @param params -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccType accType = (AccType) preResult.get(ACC_TYPE)
            List<AccTier1> accTier1List = accTier1CacheUtility.listByAccTypeId(accType.id)
            result.put(LST_TIER1, Tools.listForKendoDropdown(accTier1List,null,null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    public Object buildSuccessResultForUI(Object storeResult) {
        return null
    }
    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }

}
