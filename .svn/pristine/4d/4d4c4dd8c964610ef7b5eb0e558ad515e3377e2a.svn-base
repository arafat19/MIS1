package com.athena.mis.application.actions.supplieritem

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SupplierItem
import com.athena.mis.application.service.SupplierItemService
import com.athena.mis.application.utility.*
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for supplier-item CRUD and list of supplierItem for grid
 *  For details go through Use-Case doc named 'ShowSupplierItemActionService'
 */
class ShowSupplierItemActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    SupplierItemService supplierItemService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    SupplierItemCacheUtility supplierItemCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to load supplier's item"
    private static final String SUPPLIER_NOT_FOUND = "Supplier not found"
    private static final String INVALID_INPUT_ERROR = "Fail to load supplier's item page due to invalid supplier"
    private static final String SUPPLIER = "supplier"
    private static final String SUPPLIER_ITEMS_LIST = "supplierItemList"
    private static final String SERVICE_RETURN = "serviceReturn"

    /**
     * check different criteria to show supplier-item page
     *          1) check existence of required parameter
     *          2) check existence of SUPPLIER object
     * @param parameters -parameter from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.supplierId) {//check existence of required parameter
                result.put(Tools.MESSAGE, INVALID_INPUT_ERROR)
                return result
            }

            long supplierId = Long.parseLong(params.supplierId.toString())
            Supplier supplier = (Supplier) supplierCacheUtility.read(supplierId)
            if (!supplier) { //check existence of supplier object
                result.put(Tools.MESSAGE, SUPPLIER_NOT_FOUND)
                return result
            }
            result.put(SUPPLIER, supplier)
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
     * get list of supplierItem Objects
     * @param parameters -parameters from UI
     * @param obj -a map contains supplier object from preCondition
     * @return -a map contains supplierItem List and count
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj

            initPager(params)

            Supplier supplier = (Supplier) preResult.get(SUPPLIER)
            Map serviceReturn = listBySupplierId(supplier.id)

            result.put(SERVICE_RETURN, serviceReturn)
            result.put(SUPPLIER, supplier)
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
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map receiveResult = (LinkedHashMap) obj
            Supplier supplier = (Supplier) receiveResult.get(SUPPLIER)
            Map serviceReturn = (Map) receiveResult.get(SERVICE_RETURN)
            List supplierItemList = (List) serviceReturn.supplierItemList
            int total = serviceReturn.count
            List gridEntityList = wrapInGridEntityList(supplierItemList, this.start)
            Map gridObject = [page: pageNumber, total: total, rows: gridEntityList]
            result.put(SUPPLIER, supplier)
            result.put(SUPPLIER_ITEMS_LIST, gridObject)
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
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * @param supplierId -Supplier.id
     * @return -a map contains supplierItemList and count
     */
    private LinkedHashMap listBySupplierId(long supplierId) {
        long companyId = appSessionUtil.getCompanyId()
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
