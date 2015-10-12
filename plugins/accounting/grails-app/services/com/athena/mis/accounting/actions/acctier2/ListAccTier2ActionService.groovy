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
 *  Show list of tier2 for grid
 *  For details go through Use-Case doc named 'ListAccTier2ActionService'
 */
class ListAccTier2ActionService extends BaseService implements ActionIntf {
    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccTier2CacheUtility accTier2CacheUtility
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load Tier-2 list"
    private static final String LST_TIER2 = "lstTier2"
    private static final String COUNT = "count"

    private Logger log = Logger.getLogger(getClass())

    public Object executePreCondition(Object parameters, Object obj) {
        // do nothing for pre operation
        return null;
    }
    /**
     * Get tier2 list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)         // default value
            initPager(params)        // initialize parameters for flexGrid
            int count = accTier2CacheUtility.count()       // get count total tier2
            List lstTier2 = accTier2CacheUtility.list(this)    // get sub list of tier2
            result.put(LST_TIER2, lstTier2)
            result.put(COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
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
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj        // cast map returned from execute method
            List lstTier2 = executeResult.get(LST_TIER2)
            int count = (int) executeResult.get(COUNT)
            List lstTier2Wrap = wrapListInGridEntityList(lstTier2, start)
            result = [page: pageNumber, total: count, rows: lstTier2Wrap]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [page: pageNumber, total: 0, rows: null, isError: Boolean.TRUE, message: DEFAULT_ERROR_MESSAGE]
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
     * @param lstTier2 -list of tier2 object(s)
     * @param start -starting index of the page
     * @return -list of wrapped tier2
     */
    private List wrapListInGridEntityList(List<AccTier2> lstTier2, int start) {
        List accTier2s = []
        int counter = start + 1
        String accTypeName
        for (int i = 0; i < lstTier2.size(); i++) {
            accTypeName = accTypeCacheUtility.read(lstTier2[i].accTypeId)
            String accTier1Name = accTier1CacheUtility.read(lstTier2[i].accTier1Id)
            GridEntity obj = new GridEntity()
            obj.id = lstTier2[i].id
            obj.cell = [counter,
                    lstTier2[i].id,
                    accTier1Name,
                    lstTier2[i].name,
                    accTypeName,
                    lstTier2[i].isActive ? Tools.YES : Tools.NO
            ]
            accTier2s << obj
            counter++
        }
        return accTier2s
    }
}
