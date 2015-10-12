package com.athena.mis.application.actions.itemtype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for item type CRUD and list of item type for grid
 *  For details go through Use-Case doc named 'ShowItemTypeActionService'
 */
class ShowItemTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load item type page"
    private static final String ITEM_CATEGORY_LIST = "itemCategoryList"
    private static final String ITEM_TYPE_LIST = "itemTypeList"
    private static final String COUNT = "count"

    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility
    /**
     * 1. check user access as Admin role
     * @param params -N/A
     * @param obj - N/A
     * @return - a map containing isAccess(true/false) depending on method success &  relevant message.
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
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
     * 1. initialize params for pagination of Flexi-grid
     * 2. pull item type list from cache utility and sort them
     * @param params -serialize parameters from UI
     * @param obj -N/A
     * @return - item type list
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initPager(params)
            int count = itemTypeCacheUtility.count()
            List itemTypeList = itemTypeCacheUtility.list(this)
            result.put(ITEM_TYPE_LIST, itemTypeList)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Wrap item type list for grid
     * @param obj -map returned from execute method
     * @return -a map containing item type & item category list necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap executeResult = (LinkedHashMap) obj

            List<ItemType> itemTypeList = (List<ItemType>) executeResult.get(ITEM_TYPE_LIST)
            int count = (int) executeResult.get(COUNT)

            List itemCategoryList = itemCategoryCacheUtility.listByIsActive()

            List wrappedItemTypeList = wrapListInGridEntity(itemTypeList, start)
            Map output = [page: pageNumber, total: count, rows: wrappedItemTypeList]
            result.put(ITEM_TYPE_LIST, output)
            result.put(ITEM_CATEGORY_LIST, itemCategoryList)
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
     * @param obj -N/A
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
     * Wrap list of item type in grid entity
     * @param itemList -list of item type
     * @param start -starting index of the page
     * @return -list of wrapped item type
     */
    private List wrapListInGridEntity(List<ItemType> itemList, int start) {
        List lstItemType = [] as List
        int counter = start + 1
        for (int i = 0; i < itemList.size(); i++) {
            ItemType itemType = (ItemType) itemTypeCacheUtility.read(itemList[i].id)
            SystemEntity itemCategory = (SystemEntity) itemCategoryCacheUtility.read(itemType.categoryId)
            GridEntity obj = new GridEntity()
            obj.id = itemType.id
            obj.cell = [
                    counter,
                    itemType.id,
                    itemCategory.key,
                    itemType.name
            ]
            lstItemType << obj
            counter++
        }
        return lstItemType
    }
}


