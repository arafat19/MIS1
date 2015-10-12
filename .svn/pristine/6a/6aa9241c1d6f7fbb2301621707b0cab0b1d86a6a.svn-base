package com.athena.mis.application.actions.supplieritem

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.SupplierItem
import com.athena.mis.application.service.SupplierItemService
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.SupplierItemCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Class to get search result list of supplierItem Object to show on grid
 *  For details go through Use-Case doc named 'SearchSupplierItemActionService'
 */
class SearchSupplierItemActionService extends BaseService implements ActionIntf {

    SupplierItemService supplierItemService
    @Autowired
    SupplierItemCacheUtility supplierItemCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = "Fail to search supplier's item"
    private static final String SUPPLIER_ITEM_LIST = "supplierItemList"
    private static final String COUNT = "count"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get search result list of supplierItem Objects
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains supplierItem List and count
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            initSearch(parameterMap)

            long supplierId = Long.parseLong(parameterMap.supplierId.toString())
            Map serviceReturn = searchByItemAndSupplierId(supplierId)

            List<SupplierItem> supplierItemList = (List<SupplierItem>) serviceReturn.supplierItemList
            int total = (int) serviceReturn.count
            result.put(SUPPLIER_ITEM_LIST, supplierItemList)
            result.put(COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap supplierItemObjectList to show on grid
     * @param obj -a map contains supplierItem objectList and count
     * @return -wrapped supplierObjectList to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(COUNT)
            List<SupplierItem> supplierItemList = (List<SupplierItem>) receiveResult.get(SUPPLIER_ITEM_LIST)
            List supplierItemListWrap = wrapInGridEntityList(supplierItemList, start)
            result = [page: pageNumber, total: count, rows: supplierItemListWrap]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * @param supplierId -Supplier.id
     * @return -a map contains supplierItemList and count
     */
    private Map searchByItemAndSupplierId(long supplierId) {
        long companyId = appSessionUtil.getCompanyId()
        List<Item> lstMatchedItems = (List<Item>) itemCacheUtility.search(itemCacheUtility.NAME, query)
        List<Long> lstMatchedItemIds = []
        for (int i = 0; i < lstMatchedItems.size(); i++) {
            lstMatchedItemIds << lstMatchedItems[i].id
        }
        List<SupplierItem> supplierItemList = []
        int count = 0
        if (lstMatchedItemIds.size() > 0) {
            supplierItemList = supplierItemService.findAllBySupplierIdAndCompanyIdAndItemIdInList(supplierId, companyId, lstMatchedItemIds)
            count = supplierItemService.countBySupplierIdAndCompanyIdAndItemIdInList(supplierId, companyId, lstMatchedItemIds)
        }
        return [supplierItemList: supplierItemList as List, count: count]
    }

    /**
     * wrappedSupplierItem Object List for grid
     * @param supplierItemList -list of supplierItem objects
     * @param start -start index
     * @return -wrappedSupplierItem Object List
     */
    private List wrapInGridEntityList(List<SupplierItem> supplierItemList, int start) {
        List lstSupplierItems = [] as List
        int counter = start + 1
        for (int i = 0; i < supplierItemList.size(); i++) {
            SupplierItem supplierItem = supplierItemList[i]
            GridEntity obj = new GridEntity()
            obj.id = supplierItem.id
            Item item = (Item) itemCacheUtility.read(supplierItem.itemId)
            ItemType itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            obj.cell = [
                    counter,
                    item.name,
                    item.code,
                    item.unit,
                    itemType.name
            ]
            lstSupplierItems << obj
            counter++
        }
        return lstSupplierItems
    }
}
