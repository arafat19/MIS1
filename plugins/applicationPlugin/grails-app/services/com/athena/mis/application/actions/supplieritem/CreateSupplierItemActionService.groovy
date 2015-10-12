package com.athena.mis.application.actions.supplieritem

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SupplierItem
import com.athena.mis.application.service.SupplierItemService
import com.athena.mis.application.service.SupplierService
import com.athena.mis.application.utility.*
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create supplier-item object and to show on grid list
 *  For details go through Use-Case doc named 'CreateSupplierItemActionService'
 */
class CreateSupplierItemActionService extends BaseService implements ActionIntf {

    private static final String SAVE_SUCCESS_MESSAGE = "Supplier-Item has been saved successfully"
    private static final String MATERIAL_ALREADY_EXISTS = "Supplier-Item with same material already exists"
    private static final String SAVE_FAILURE_MESSAGE = "Fail to save supplier item"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String SUPPLIER_NOT_FOUND = "Supplier not found"
    private static final String ITEM_NOT_FOUND = "Item not found"
    private static final String SUPPLIER_ITEM_OBJ = "supplierItem"

    private final Logger log = Logger.getLogger(getClass())

    SupplierService supplierService
    SupplierItemService supplierItemService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    SupplierItemCacheUtility supplierItemCacheUtility

    /**
     * Check different criteria for creating new supplier-item object
     *      1) Check existence of supplier & selected item object
     *      2) Check existence of same supplierItem object
     *      3) Validate supplierItem object
     * @param params -serialized parameters send from UI
     * @param obj -N/A
     * @return -a map containing supplierItem object for execute method
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            long supplierId = Long.parseLong(parameterMap.supplierId.toString())
            Supplier supplier = (Supplier) supplierCacheUtility.read(supplierId)
            if (!supplier) {//check existence of supplier object
                result.put(Tools.MESSAGE, SUPPLIER_NOT_FOUND)
                return result
            }

            long itemId = Long.parseLong(parameterMap.itemId.toString())
            Item item = (Item) itemCacheUtility.read(itemId)
            if (!item) {//check existence of item object
                result.put(Tools.MESSAGE, ITEM_NOT_FOUND)
                return result
            }

            SupplierItem supplierItem = buildObject(parameterMap)

            //check existence of same supplierItem object
            SupplierItem existingSupplierItem = supplierItemCacheUtility.readByItemIdAndSupplierId(supplierItem.itemId, supplierItem.supplierId)
            if (existingSupplierItem) {
                result.put(Tools.MESSAGE, MATERIAL_ALREADY_EXISTS)
                return result
            }

            // checks input validation
            supplierItem.validate()
            if (supplierItem.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            result.put(SUPPLIER_ITEM_OBJ, supplierItem)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Save supplierItem object in DB as well as in cache; also increase itemCount in Supplier domain
     * @param parameters -N/A
     * @param obj -supplierItem Object send from executePreCondition
     * @return -newly created supplierItem object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            SupplierItem supplierItem = (SupplierItem) preResult.get(SUPPLIER_ITEM_OBJ)
            SupplierItem newSupplierItemInstance = supplierItemService.create(supplierItem)//save in DB
            //save in cache and keep the data sorted
            supplierItemCacheUtility.add(newSupplierItemInstance, supplierItemCacheUtility.SORT_ON_NAME, supplierItemCacheUtility.SORT_ORDER_ASCENDING)
            Supplier supplier = (Supplier) supplierCacheUtility.read(supplierItem.supplierId)
            supplier.itemCount = supplier.itemCount + 1
            supplierService.updateForSupplierItem(supplier)//update itemCount of supplier domain in DB
            //update itemCount of supplier domain in cache and keep the data sorted
            supplierCacheUtility.update(supplier, supplierCacheUtility.SORT_ON_NAME, supplierCacheUtility.SORT_ORDER_ASCENDING)
            result.put(SUPPLIER_ITEM_OBJ, newSupplierItemInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap newly created supplierItem object to show on grid
     * @param obj -newly created supplierItem object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            SupplierItem supplierItemInstance = (SupplierItem) receiveResult.get(SUPPLIER_ITEM_OBJ)
            GridEntity object = new GridEntity()
            object.id = supplierItemInstance.id
            Item item = (Item) itemCacheUtility.read(supplierItemInstance.itemId)
            ItemType itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            object.cell = [
                    Tools.LABEL_NEW,
                    item.name,
                    item.code,
                    item.unit,
                    itemType.name
            ]

            result.put(Tools.MESSAGE, SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build SupplierItem object
     * @param parameterMap -serialized parameters from UI
     * @return -new SupplierItem object
     */
    private SupplierItem buildObject(GrailsParameterMap params) {
        SupplierItem supplierItem = new SupplierItem(params)
        supplierItem.companyId = appSessionUtil.getCompanyId()
        supplierItem.createdOn = new Date()
        supplierItem.createdBy = appSessionUtil.getAppUser().id
        supplierItem.updatedOn = null
        supplierItem.updatedBy = 0
        return supplierItem
    }
}
