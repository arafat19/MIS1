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
 *  Search item type and show specific list of project for grid
 *  For details go through Use-Case doc named 'SearchItemTypeActionService'
 */
class SearchItemTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to search item type list"
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
     * 2. pull item type list from cache utility
     * @param params -serialize parameters from UI
     * @param obj -N/A
     * @return - item type list
     */
    public Object execute(Object params, Object obj = null) {
        try {
            if ((!params.sortname) || (params.sortname.toString().equals(ID))) {
                params.sortname = itemTypeCacheUtility.NAME
                params.sortorder = ASCENDING_SORT_ORDER
            }
            initSearch(params)
            Map searchResult = itemTypeCacheUtility.search(queryType, query, this)
            List<ItemType> itemTypeList = searchResult.list
            int count = searchResult.count
            return [itemList: itemTypeList, count: count]
        } catch (Exception ex) {
            log.error(ex.getMessage())
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
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<ItemType> itemTypeList = (List<ItemType>) executeResult.itemList
            int count = (int) executeResult.count
            List wrappedItemTypeList = wrapListInGridEntity(itemTypeList, start)
            return [page: pageNumber, total: count, rows: wrappedItemTypeList]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null]
        }
    }
    /**
     * Build failure result in case of any error
     * @param obj -N/A
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.TRUE)
        result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
        return result
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

