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
 *  Show UI for item CRUD (Category : Non-Inventory) and list of Non-Inventory item(s) for grid
 *  For details go through Use-Case doc named 'ShowItemCategoryNonInvActionService'
 */
class ShowItemCategoryNonInvActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load item page"
    private static final String ITEM_LIST = "itemList"
    private static final String COUNT = "count"

    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility

    /**
     * check access permission to show non-inventory item CRUD page
     * @param parameters -N/A
     * @param obj -N/A
     * @return -boolean value of access permission(true/false)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            //Only admin has right to show non-inventory item CRUD page
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
     * get list of itemObjects
     * @param parameters -N/A
     * @param obj -N/A
     * @return -a map containing list of item object(s) for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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
     * wrap itemObject List to show on grid
     * @param obj -a map contains itemObjectList and count receives from execute method
     * @return -wrap itemObject List to show on grid
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List<Item> itemList = (List<Item>) executeResult.get(ITEM_LIST)
            int count = (int) executeResult.get(COUNT)

            List wrappedItemList = wrapListInGridEntityList(itemList, start)
            Map output = [page: pageNumber, total: count, rows: wrappedItemList]
            result.put(ITEM_LIST, output)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
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
            LinkedHashMap preResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
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
     * wrappedItemObject List for grid
     * @param itemList - list of item objects
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

