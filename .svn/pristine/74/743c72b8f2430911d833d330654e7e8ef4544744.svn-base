package com.athena.mis.application.actions.item

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Class to get search result list of itemObject (Category : Non-Inventory) to show on grid
 *  For details go through Use-Case doc named 'SearchItemCategoryNonInvActionService'
 */
class SearchItemCategoryNonInvActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to search item list"

    /**
     * check access permission to search itemList
     * @param parameters -N/A
     * @param obj -N/A
     * @return -boolean value of access permission(true/false)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            //Only admin has right to search itemObject List
            if (appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * get search result list of itemObjects
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains itemList and count
     */
    public Object execute(Object params, Object obj = null) {
        try {
            if ((!params.sortname) || (params.sortname.toString().equals(ID))) {
                params.sortname = itemCacheUtility.NAME
                params.sortorder = itemCacheUtility.SORT_ORDER_ASCENDING
            }
            initSearch(params)
            Map searchResult = itemCacheUtility.searchCategoryWiseItemList(queryType, query, itemCategoryCacheUtility.NON_INVENTORY, this)
            List<Item> itemList = searchResult.list
            int count = searchResult.count
            return [itemList: itemList, count: count]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap itemObject list to show on grid
     * @param obj -a map contains itemObject list and count
     * @return -wrapped itemObject list to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<Item> itemList = (List<Item>) executeResult.itemList
            int count = (int) executeResult.count
            List inGridEntityList = wrapListInGridEntityList(itemList, start)
            return [page: pageNumber, total: count, rows: inGridEntityList]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null]
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * wrappedItemObject list for grid
     * @param itemList -list of item objects
     * @param start -start index
     * @return -wrappedItemObjectList
     */
    private List wrapListInGridEntityList(List<Item> itemList, int start) {
        List itemLists = [] as List
        int counter = start + 1
        for (int i = 0; i < itemList.size(); i++) {
            ItemType itemType = (ItemType) itemTypeCacheUtility.read(itemList[i].itemTypeId)
            Item item = itemList[i]
            GridEntity obj = new GridEntity()
            obj.id = item.id
            obj.cell = [
                    counter,
                    item.id,
                    item.name,
                    item.code,
                    item.unit,
                    itemType.name
            ]
            itemLists << obj
            counter++
        }
        return itemLists
    }
}

