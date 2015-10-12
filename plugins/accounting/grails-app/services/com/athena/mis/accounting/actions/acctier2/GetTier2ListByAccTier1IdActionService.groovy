package com.athena.mis.accounting.actions.acctier2

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccTier1
import com.athena.mis.accounting.entity.AccTier2
import com.athena.mis.accounting.utility.AccTier1CacheUtility
import com.athena.mis.accounting.utility.AccTier2CacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
 // get tier2 list by tier1 id
class GetTier2ListByAccTier1IdActionService extends BaseService implements ActionIntf {

    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccTier2CacheUtility accTier2CacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String ERROR_MESSAGE = "Failed to load Tier1 list"
    private static final String ACC_TIER2_LIST = "accTier2List"
    private static final String ACC_TIER1 = "accTier1"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input."

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            // check here the required params are present
            if (!parameterMap.accTier1Id) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            Long accTier1Id = Long.parseLong(parameterMap.accTier1Id.toString())
            AccTier1 accTier1 = (AccTier1) accTier1CacheUtility.read(accTier1Id)
            if (!accTier1) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            result.put(ACC_TIER1, accTier1)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }

    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccTier1 accTier1 = (AccTier1) preResult.get(ACC_TIER1)
            List<AccTier2> accTier2List = accTier2CacheUtility.listByAccTier1Id(accTier1.id)
            result.put(ACC_TIER2_LIST, Tools.listForKendoDropdown(accTier2List,null,null))
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
