package com.athena.mis.application.actions.supplieritem

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SupplierItem
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.application.utility.SupplierItemCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select specific supplierItem object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectSupplierItemActionService'
 */
class SelectSupplierItemActionService extends BaseService implements ActionIntf {

    private static final String SUPPLIER_ITEM_NOT_FOUND_MESSAGE = "Supplier's Item not found on server"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred for invalid input"
    private static final String SUPPLIER_ITEM_OBJ = "supplierItem"
    private static final String SUPPLIER = "supplier"
    private static final String SUPPLIER_ITEMS_LIST = "supplierItemList"
    private static final String ITEMS_ENTITY = "item"

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    SupplierItemCacheUtility supplierItemCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get supplierItem object by id
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            // check here for required params are present
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            long id = Long.parseLong(parameterMap.id.toString())
            SupplierItem supplierItem = (SupplierItem) supplierItemCacheUtility.read(id)
            if (!supplierItem) { //check existence of supplierItem object
                result.put(Tools.MESSAGE, SUPPLIER_ITEM_NOT_FOUND_MESSAGE)
                return result
            }

            // Get supplier information
            Supplier supplier = (Supplier) supplierCacheUtility.read(supplierItem.supplierId)

            result.put(SUPPLIER_ITEM_OBJ, supplierItem)
            result.put(SUPPLIER, supplier)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SUPPLIER_ITEM_NOT_FOUND_MESSAGE)
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
     * Build a map with necessary objects to show on UI
     * @param obj -a map contains supplier and supplierItem object
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            SupplierItem supplierItem = (SupplierItem) receiveResult.get(SUPPLIER_ITEM_OBJ)
            Supplier supplier = (Supplier) receiveResult.get(SUPPLIER)
            Item item = (Item) itemCacheUtility.read(supplierItem.itemId)

            //get unassigned itemList to show on drop-down
            List<GroovyRowResult> itemList = getUnassignedItemListForUpdate(supplierItem.supplierId, item.itemTypeId, supplierItem.itemId)
            result.put(Tools.ENTITY, supplierItem)
            result.put(Tools.VERSION, supplierItem.version)
            result.put(SUPPLIER_ITEMS_LIST, Tools.listForKendoDropdown(itemList,null,null))
            result.put(SUPPLIER, supplier)
            result.put(ITEMS_ENTITY, item)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SUPPLIER_ITEM_NOT_FOUND_MESSAGE)
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
                result.put(Tools.MESSAGE, SUPPLIER_ITEM_NOT_FOUND_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SUPPLIER_ITEM_NOT_FOUND_MESSAGE)
            return result
        }
    }

    /**
     * get unAssigned itemList to show on drop-down
     * @param supplierId -supplier.id
     * @param itemTypeId -ItemType.id
     * @param itemId -item.id
     * @return -list of groovyRowResult
     */
    private List<GroovyRowResult> getUnassignedItemListForUpdate(long supplierId, long itemTypeId, long itemId) {
        String queryStr = """SELECT *
                        FROM item
                        WHERE id NOT IN (SELECT supplier_item.item_id
                                    FROM supplier_item
                                    WHERE supplier_item.supplier_id = ${supplierId}
                                    AND item_id <> ${itemId})
                        AND item_type_id = ${itemTypeId}
                        ORDER BY name ASC
                   """
        List<GroovyRowResult> materialList = executeSelectSql(queryStr)
        return materialList
    }
}
