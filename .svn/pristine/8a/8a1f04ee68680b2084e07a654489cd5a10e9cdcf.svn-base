package com.athena.mis.application.actions.item

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Class to get list of itemObject (Category : Non-Inventory) to show on grid
 *  For details go through Use-Case doc named 'ListItemCategoryNonInvActionService'
 */
class ListItemCategoryNonInvActionService extends BaseService implements ActionIntf {

    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility

    protected final Logger log = Logger.getLogger(getClass())

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load item list"
    private static final String ITEM_LIST = "itemList"
    private static final String COUNT = "count"

    /**
     * check access permission to get itemList
     * @param parameters -N/A
     * @param obj -N/A
     * @return -boolean value of access permission(true/false)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            //Only admin has right to get itemObject List
            if (appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            return result
        }
    }

    /**
     * get list of itemObjects
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -a map contains itemList and count
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if ((!params.sortname) || (params.sortname.toString().equals(ID))) {
                params.sortname = itemCacheUtility.NAME
                params.sortorder = ASCENDING_SORT_ORDER
            }
            initPager(params)
            SystemEntity itemNonInvSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.NON_INVENTORY, appSessionUtil.getCompanyId())
            Map lstResult = itemCacheUtility.listItemByCategoryId(itemNonInvSysEntityObject.id, this)
            List itemList = lstResult.list
            int count = lstResult.count
            result.put(ITEM_LIST, itemList)
            result.put(COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
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
     * wrap itemObject list to show on grid
     * @param obj -a map contains itemObject list and count
     * @return -wrapped itemObject list to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<Item> itemList = (List<Item>) executeResult.get(ITEM_LIST)
            int count = (int) executeResult.get(COUNT)
            List inGridEntityList = wrapListInGridEntityList(itemList, start)
            return [page: pageNumber, total: count, rows: inGridEntityList]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null, isError: Boolean.TRUE, message: PAGE_LOAD_ERROR_MESSAGE]
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
