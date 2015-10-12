package com.athena.mis.accounting.actions.acctier1

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccTier1
import com.athena.mis.accounting.utility.AccTier1CacheUtility
import com.athena.mis.accounting.utility.AccTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
/**
 *  Search tier1 and show specific list of tier1 for grid
 *  For details go through Use-Case doc named 'SearchAccTier1ActionService'
 */
class SearchAccTier1ActionService extends BaseService implements ActionIntf {

    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility

    protected final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load account Tier-1 list"
    private static final String LST_TIER1 = "lstTier1"
    private static final String COUNT = "count"

    public Object executePreCondition(Object parameters, Object obj) {
        // do nothing for pre operation
        return null
    }
    /**
     * Get tier1 list for grid through specific search
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initSearch(params)         // initialize parameters for flexGrid

            List<AccTier1> lstTier1 = accTier1CacheUtility.list()
            Map searchResult = accTier1CacheUtility.searchByField(queryType, query, lstTier1, this)
            lstTier1 = searchResult.list
            int count = searchResult.count

            result.put(LST_TIER1, lstTier1)
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
     * Wrap tier1 list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<AccTier1> lstTier1 = (List<AccTier1>) executeResult.get(LST_TIER1)
            int count = (int) executeResult.get(COUNT)
            List accTier1ListWrap = wrapListInGridEntityList(lstTier1, start)
            result = [page: pageNumber, total: count, rows: accTier1ListWrap]
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
                LinkedHashMap preResult = (LinkedHashMap) obj  // cast map returned from previous method
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
     * Wrap list of tier1 in grid entity
     * @param lstTier1 -list of tier1 object(s)
     * @param start -starting index of the page
     * @return -list of wrapped tier1
     */
    private List wrapListInGridEntityList(List<AccTier1> lstTier1, int start) {
        List accTier1s = [] as List
        int counter = start + 1
        String accTypeName
        for (int i = 0; i < lstTier1.size(); i++) {
            accTypeName = accTypeCacheUtility.read(lstTier1[i].accTypeId)

            GridEntity obj = new GridEntity()
            obj.id = lstTier1[i].id
            obj.cell = [
                    counter,
                    lstTier1[i].id,
                    lstTier1[i].name ? lstTier1[i].name : Tools.EMPTY_SPACE,
                    accTypeName ? accTypeName : Tools.EMPTY_SPACE,
                    lstTier1[i].isActive ? Tools.YES : Tools.NO
            ]
            accTier1s << obj
            counter++
        }
        return accTier1s
    }
}