package com.athena.mis.accounting.actions.accgroup

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccGroup
import com.athena.mis.accounting.utility.AccGroupCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

// search acc group
class SearchAccGroupActionService extends BaseService implements ActionIntf {

    @Autowired
    AccGroupCacheUtility accGroupCacheUtility

    protected final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load account group list"
    private static final String ACC_GROUP_LIST = "accGroupList"
    private static final String COUNT = "count"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get search result list of accGroup object(s)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains accGroupList and count
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initSearch(params)
            Map searchResult = accGroupCacheUtility.search(queryType, query, this)
            List accGroupList = searchResult.list
            int count = searchResult.count
            result.put(ACC_GROUP_LIST, accGroupList)
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

    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap accGroupObjectList to show on grid
     * @param obj -a map contains accGroupObjectList and count
     * @return -wrapped accGroupObjectList to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<AccGroup> accGroupList = (List<AccGroup>) executeResult.get(ACC_GROUP_LIST)
            int count = (int) executeResult.get(COUNT)
            List accGroupListWrap = wrapListInGridEntityList(accGroupList, start)
            return [page: pageNumber, total: count, rows: accGroupListWrap]
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
     * wrappedAccGroupObjectList for grid
     * @param accGroupList -list of accGroup objects
     * @param start -start index
     * @return -wrappedAccGroupObjectList
     */
    private List wrapListInGridEntityList(List<AccGroup> accGroupList, int start) {
        List accGroups = [] as List
        int counter = start + 1
        for (int i = 0; i < accGroupList.size(); i++) {
            GridEntity obj = new GridEntity()
            obj.id = accGroupList[i].id
            obj.cell = [
                    counter,
                    accGroupList[i].id,
                    accGroupList[i].name ? accGroupList[i].name : Tools.EMPTY_SPACE,
                    accGroupList[i].description ? accGroupList[i].description : Tools.EMPTY_SPACE,
                    accGroupList[i].isActive ? Tools.YES : Tools.NO,
                    accGroupList[i].isReserved ? Tools.YES : Tools.NO
            ]
            accGroups << obj
            counter++
        }
        return accGroups
    }
}
