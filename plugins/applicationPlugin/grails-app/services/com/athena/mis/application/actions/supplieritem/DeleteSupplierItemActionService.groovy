package com.athena.mis.application.actions.supplieritem

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SupplierItem
import com.athena.mis.application.service.SupplierItemService
import com.athena.mis.application.service.SupplierService
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.application.utility.SupplierItemCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete supplierItem object from DB as well as from cache
 *  For details go through Use-Case doc named 'DeleteSupplierItemActionService'
 */
class DeleteSupplierItemActionService extends BaseService implements ActionIntf {

    SupplierService supplierService
    SupplierItemService supplierItemService
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    SupplierItemCacheUtility supplierItemCacheUtility

    private static final String DELETE_SUCCESS_MESSAGE = "Supplier item has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Fail to delete supplier item"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String SUPPLIER_ITEM_NOT_FOUND = "Supplier item not found"
    private static final String SUPPLIER_ITEM_OBJ = "supplierItem"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria to delete supplier-item object
     *      1) Check existence of required parameter
     *      2) Check existence of supplierItem object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing supplierItem object for execute method
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if ((!parameterMap.id)) { //check existence of parameter
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            long supplierItemId = Long.parseLong(parameterMap.id.toString())
            SupplierItem supplierItem = (SupplierItem) supplierItemCacheUtility.read(supplierItemId)
            if (!supplierItem) { //check existence of supplierItem object
                result.put(Tools.MESSAGE, SUPPLIER_ITEM_NOT_FOUND)
                return result
            }

            result.put(SUPPLIER_ITEM_OBJ, supplierItem)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * delete supplierItem object from DB & cache; Also decrease itemCount from Supplier domain
     * @param parameters -N/A
     * @param obj -supplierItem Object send from executePreCondition
     * @return -Boolean value (true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            SupplierItem supplierItem = (SupplierItem) preResult.get(SUPPLIER_ITEM_OBJ)
            supplierItemService.delete(supplierItem.id) //delete from DB
            supplierItemCacheUtility.delete(supplierItem.id) //delete from cache
            Supplier supplier = (Supplier) supplierCacheUtility.read(supplierItem.supplierId)
            supplier.itemCount = supplier.itemCount - 1
            supplierService.updateForSupplierItem(supplier)//decrease itemCount in DB
            //decrease itemCount in cache
            supplierCacheUtility.update(supplier, supplierCacheUtility.SORT_ON_NAME, supplierCacheUtility.SORT_ORDER_ASCENDING)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DELETE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
     * contains delete success message to show on UI
     * @param obj -N/A
     * @return -a map contains boolean value(true) and delete success message to show on UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return [deleted: Boolean.TRUE.booleanValue(), message: DELETE_SUCCESS_MESSAGE]
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
                result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }
}
