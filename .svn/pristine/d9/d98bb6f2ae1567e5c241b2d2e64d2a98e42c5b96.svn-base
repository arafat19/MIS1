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
// select specific object of acc tier2
class SelectAccTier2ActionService extends BaseService implements ActionIntf {

    private static final String ACC_TIRE2_NOT_FOUND_MASSAGE = "Selected tier-2 is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select tier-2"
    private static final String LST_TIER1 = "lstTier1"

    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccTier2CacheUtility accTier2CacheUtility


    private final Logger log = Logger.getLogger(getClass())

    public Object executePreCondition(Object parameters, Object obj) {
        // do nothing for pre operation
        return null
    }

    /**
     * Get tier2 object by id
     * @param parameters - parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)     // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            Long accTier2Id = Long.parseLong(parameterMap.id.toString())
            AccTier2 accTier2 = (AccTier2) accTier2CacheUtility.read(accTier2Id)       // get tier2 object
            if (accTier2) {
                List<AccTier1> accTier1List = accTier1CacheUtility.listByAccTypeIdForEdit(accTier2.accTypeId, accTier2.accTier1Id)
                result.put(Tools.ENTITY, accTier2)
                result.put(LST_TIER1, accTier1List)
            } else {

                result.put(Tools.MESSAGE, ACC_TIRE2_NOT_FOUND_MASSAGE)
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TIRE2_NOT_FOUND_MASSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with tier2 object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj      // cast map returned from execute method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            AccTier2 accTier2 = (AccTier2) executeResult.get(Tools.ENTITY)         // get tier2 object
            List<AccTier1> accTier1List = (List<AccTier1>) executeResult.get(LST_TIER1)   // get tier1 list
            result.put(Tools.ENTITY, accTier2)
            result.put(LST_TIER1, accTier1List)
            result.put(Tools.VERSION, accTier2.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj      // cast map returned from previous method
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
}
