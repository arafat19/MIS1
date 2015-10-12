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
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get list of supplierItem Object to show on grid
 *  For details go through Use-Case doc named 'ListSupplierItemActionService'
 */
class ListSupplierItemActionService extends BaseService implements ActionIntf {

    SupplierItemService supplierItemService
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    SupplierItemCacheUtility supplierItemCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = "Fail to get list of supplier's item"
    private static final String SUPPLIER_ITEM_LIST = "supplierItemList"
    private static final String COUNT = "count"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get list of supplierItem Objects
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains supplierItem List and count
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            initPager(parameterMap)
            long supplierId = Long.parseLong(parameterMap.supplierId.toString())

            Map serviceReturn = listBySupplierId(supplierId)

            List returnResult = (List) serviceReturn.supplierItemList
            int total = (int) serviceReturn.count

            result.put(COUNT, total)
            result.put(SUPPLIER_ITEM_LIST, returnResult)
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
     * @param obj -a map contains supplierItemObjectList and count
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
    private LinkedHashMap listBySupplierId(long supplierId) {
        long companyId = appSessionUtil.getCompanyId()
//        List<SupplierItem> supplierItemList = supplierItemService.findAllBySupplierIdAndCompanyId(supplierId, companyId)
        List<SupplierItem> supplierItemList = supplierItemCacheUtility.getAllSupplierItemListBySupplierId(supplierId, this)
        int count = supplierItemCacheUtility.countBySupplierId(supplierId)
        return [supplierItemList: supplierItemList, count: count]
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
