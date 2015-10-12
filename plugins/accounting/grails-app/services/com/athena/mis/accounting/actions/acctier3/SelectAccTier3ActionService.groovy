package com.athena.mis.accounting.actions.acctier3

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccTier1
import com.athena.mis.accounting.entity.AccTier2
import com.athena.mis.accounting.entity.AccTier3
import com.athena.mis.accounting.utility.AccTier1CacheUtility
import com.athena.mis.accounting.utility.AccTier2CacheUtility
import com.athena.mis.accounting.utility.AccTier3CacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select specific accTier3 object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectAccTier3ActionService'
 */
class SelectAccTier3ActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccTier2CacheUtility accTier2CacheUtility
    @Autowired
    AccTier3CacheUtility accTier3CacheUtility

    private static final String ACC_TIER3_NOT_FOUND_MASSAGE = "Selected tier-3 is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select tier-31"
    private static final String ACC_TIER1_LIST = "accTier1List"
    private static final String ACC_TIER2_LIST = "accTier2List"

    /**
     * Check different criteria to select accTier3 object
     *      1) Check existence of required parameter
     *      2) Check existence of accTier3 object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {//check existence of required parameter
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long accTier3Id = Long.parseLong(parameterMap.id.toString())
            AccTier3 accTier3 = (AccTier3) accTier3CacheUtility.read(accTier3Id)
            if (!accTier3) {//check existence of accTier3 object
                result.put(Tools.MESSAGE, ACC_TIER3_NOT_FOUND_MASSAGE)
                return result
            }

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
     * Get accTier3 object by id; Also get list of AccTier1 & AccTier2 objects for drop-down
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing accTier3 object and list of AccTier1 & AccTier2 objects for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            long accTier3Id = Long.parseLong(parameterMap.id.toString())
            AccTier3 accTier3 = (AccTier3) accTier3CacheUtility.read(accTier3Id)

            //get AccTier1 list
            List<AccTier1> accTier1List = accTier1CacheUtility.listByAccTypeIdForEdit(accTier3.accTypeId, accTier3.accTier1Id)
            //get AccTier2 list
            List<AccTier2> accTier2List = accTier2CacheUtility.listByAccTier1IdForEdit(accTier3.accTier1Id, accTier3.accTier2Id)

            result.put(Tools.ENTITY, accTier3)
            result.put(ACC_TIER1_LIST, accTier1List)
            result.put(ACC_TIER2_LIST, accTier2List)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_TIER3_NOT_FOUND_MASSAGE)
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
     * Build a map with necessary objects to show on UI
     * @param obj -map contains accTier3 object and list of AccTier1 & AccTier2 objects for drop-down
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            AccTier3 accTier3 = (AccTier3) executeResult.get(Tools.ENTITY)
            List<AccTier1> accTier1List = (List<AccTier1>) executeResult.get(ACC_TIER1_LIST)
            List<AccTier2> accTier2List = (List<AccTier2>) executeResult.get(ACC_TIER2_LIST)
            result.put(Tools.ENTITY, accTier3)
            result.put(ACC_TIER1_LIST, accTier1List)
            result.put(ACC_TIER2_LIST, accTier2List)
            result.put(Tools.VERSION, accTier3.version)
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
     * @return -a map containing isError(true) & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map previousResult = (Map) obj
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            }
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
}