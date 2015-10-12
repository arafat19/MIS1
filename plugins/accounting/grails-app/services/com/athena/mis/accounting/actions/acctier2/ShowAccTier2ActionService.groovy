package com.athena.mis.accounting.actions.acctier2

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccTier2
import com.athena.mis.accounting.utility.AccTier1CacheUtility
import com.athena.mis.accounting.utility.AccTier2CacheUtility
import com.athena.mis.accounting.utility.AccTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for tier2 CRUD and list of tier2 for grid
 *  For details go through Use-Case doc named 'UpdateAccTier2ActionService'
 */
class ShowAccTier2ActionService extends BaseService implements ActionIntf {

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load tier-2 page"
    private static final String LST_TIER2 = "lstTier2"
    private static final String COUNT = "count"

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    AccTier2CacheUtility accTier2CacheUtility
    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get tier2 list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initPager(params)                // initialize parameters for flexGrid
            int count = accTier2CacheUtility.count()    // get count total tier2
            List accTier2List = accTier2CacheUtility.list(this)  // get sub list of tier2
            result.put(LST_TIER2, accTier2List)
            result.put(COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return null
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    /**
     * Wrap tier2 list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj            // cast map returned from execute method
            List accTier2List = (List) executeResult.get(LST_TIER2)
            int count = (int) executeResult.get(COUNT)
            List resultList = wrapListInGridEntityList(accTier2List, start)
            Map output = [page: pageNumber, total: count, rows: resultList]
            result.put(LST_TIER2, output)
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
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of tier2 in grid entity
     * @param lstEmployees -list of tier2 object(s)
     * @param start -starting index of the page
     * @return -list of wrapped tier2
     */
    private List wrapListInGridEntityList(List<AccTier2> lstTier2, int start) {
        List accTier2s = [] as List
        int counter = start + 1
        String accTypeName
        for (int i = 0; i < lstTier2.size(); i++) {
            accTypeName = accTypeCacheUtility.read(lstTier2[i].accTypeId)
            String accTier1Name = accTier1CacheUtility.read(lstTier2[i].accTier1Id)
            GridEntity obj = new GridEntity();
            obj.id = lstTier2[i].id
            obj.cell = [counter,
                    lstTier2[i].id,
                    accTier1Name,
                    lstTier2[i].name,
                    accTypeName,
                    lstTier2[i].isActive ? Tools.YES : Tools.NO
            ]
            accTier2s << obj
            counter++;
        }
        return accTier2s;
    }
}
