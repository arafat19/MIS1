package com.athena.mis.accounting.actions.acctier3

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccTier3
import com.athena.mis.accounting.utility.AccTier1CacheUtility
import com.athena.mis.accounting.utility.AccTier2CacheUtility
import com.athena.mis.accounting.utility.AccTier3CacheUtility
import com.athena.mis.accounting.utility.AccTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Class to get search result list of AccTier3 object(s) to show on grid
 *  For details go through Use-Case doc named 'SearchAccTier3ActionService'
 */
class SearchAccTier3ActionService extends BaseService implements ActionIntf {

    @Autowired
    AccTypeCacheUtility accTypeCacheUtility
    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccTier2CacheUtility accTier2CacheUtility
    @Autowired
    AccTier3CacheUtility accTier3CacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to search Tier-3 list"
    private static final String ACC_TIER3_LIST = "accTier3List"

    private Logger log = Logger.getLogger(getClass())

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get search result list of accTier3 object(s)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains accTier3List and count
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initSearch(params)
            List<AccTier3> accTier3List = accTier3CacheUtility.list()
            Map searchResult = accTier3CacheUtility.searchByField(queryType, query, accTier3List, this)
            accTier3List = searchResult.list
            int count = searchResult.count
            result.put(ACC_TIER3_LIST, accTier3List)
            result.put(Tools.COUNT, count)
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
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap accTier3ObjectList to show on grid
     * @param obj -a map contains accTier3ObjectList and count
     * @return -wrapped accTier3ObjectList to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<AccTier3> accTier3List = (List<AccTier3>) executeResult.get(ACC_TIER3_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List accTier3ListWrap = wrapListInGridEntityList(accTier3List, start)
            return [page: pageNumber, total: count, rows: accTier3ListWrap]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null, isError: Boolean.TRUE, message: DEFAULT_ERROR_MESSAGE]
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
     * wrappedAccTier3Object for grid
     * @param accTier3List -list of accTier3 objects
     * @param start -start index
     * @return -wrappedAccTier3Object
     */
    private List wrapListInGridEntityList(List<AccTier3> accTier3List, int start) {
        List accTier3s = [] as List
        int counter = start + 1
        for (int i = 0; i < accTier3List.size(); i++) {
            String accTypeName = accTypeCacheUtility.read(accTier3List[i].accTypeId)
            String accTier1Name = accTier1CacheUtility.read(accTier3List[i].accTier1Id)
            String accTier2Name = accTier2CacheUtility.read(accTier3List[i].accTier2Id)
            GridEntity obj = new GridEntity()
            obj.id = accTier3List[i].id
            obj.cell = [counter,
                    accTier3List[i].id,
                    accTier3List[i].name ? accTier3List[i].name : Tools.EMPTY_SPACE,
                    accTypeName ? accTypeName : Tools.EMPTY_SPACE,
                    accTier1Name ? accTier1Name : Tools.EMPTY_SPACE,
                    accTier2Name ? accTier2Name : Tools.EMPTY_SPACE,
                    accTier3List[i].isActive ? Tools.YES : Tools.NO
            ]
            accTier3s << obj
            counter++
        }
        return accTier3s
    }
}