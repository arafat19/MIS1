package com.athena.mis.application.actions.supplier

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.service.SupplierService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete supplier object from DB as well as from cache
 *  For details go through Use-Case doc named 'DeleteSupplierActionService'
 */
class DeleteSupplierActionService extends BaseService implements ActionIntf {

    private static final String DELETE_SUCCESS_MESSAGE = "Supplier has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Supplier could not be deleted, please refresh the page"
    private static final String SUPPLIER_HAS_ITEMS = "Supplier has association with one or more items"
    private static final String HAS_ASSOCIATION_PURCHASE_ORDER = " purchase order is associated with selected supplier"
    private static final String HAS_ASSOCIATION_INVENTORY_TRANSACTION = " inventory transaction is associated with selected supplier"
    private static final String HAS_ASSOCIATION_VOUCHER_DETAILS = " voucher information is associated with this supplier"
    private static final String HAS_ASSOCIATION_LC = " LC(s) is associated with this supplier"

    private final Logger log = Logger.getLogger(getClass())

    SupplierService supplierService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * Check different criteria to delete supplier object
     *      1) Check access permission to delete supplier
     *      2) Check existence of supplier object
     *      3) Check if supplier has any item(s)
     *      4) check association with different domain(s) of installed plugin(s)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            //Only admin can delete supplier object
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long supplierId = Long.parseLong(params.id.toString())
            Supplier supplier = (Supplier) supplierCacheUtility.read(supplierId)
            if (!supplier) { //check existence of supplier object
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            if (supplier.itemCount > 0) { //check if supplier has any associated item(s)
                result.put(Tools.MESSAGE, SUPPLIER_HAS_ITEMS)
                return result
            }

            //check association
            Map associationResult = (Map) hasAssociation(supplier)
            Boolean hasAssociation = (Boolean) associationResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {//if association found then return with message
                result.put(Tools.MESSAGE, associationResult.get(Tools.MESSAGE))
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * delete supplier object from DB & cache
     * @param parameters -parameter send from UI
     * @param obj -N/A
     * @return -Boolean value (true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long supplierId = Long.parseLong(params.id.toString())
            Boolean result = supplierService.delete(supplierId) //delete from DB
            supplierCacheUtility.delete(supplierId) //delete from cache
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete supplier')
            return Boolean.FALSE
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * method to check if supplier has any association with different domain(s) of installed plugin(s)
     * @param supplier -Supplier object
     * @return -a map contains booleanValue(true/false) and association message
     *          based on existence of association
     */
    private LinkedHashMap hasAssociation(Supplier supplier) {
        LinkedHashMap result = new LinkedHashMap()
        long supplierId = supplier.id
        int count = 0
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        if (PluginConnector.isPluginInstalled(PluginConnector.PROCUREMENT)) {
            count = countPurchaseOrder(supplierId)
            if (count.intValue() > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_PURCHASE_ORDER)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
            count = countInventoryTransaction(supplierId)
            if (count.intValue() > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_INVENTORY_TRANSACTION)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
            count = countVoucherDetails(supplierId)
            if (count.intValue() > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_VOUCHER_DETAILS)
                return result
            }

            count = countLc(supplierId)
            if (count.intValue() > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_LC)
                return result
            }
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    private static final String QUERY_PROC_PURCHASE_ORDER = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_order
            WHERE supplier_id = :supplierId AND
                  company_id = :companyId
            """
    /**
     * count number of PO of a specific supplier
     * @param supplierId -Supplier.id
     * @return -int value
     */
    private int countPurchaseOrder(long supplierId) {
        Map queryParams = [
                supplierId: supplierId,
                companyId: appSessionUtil.getCompanyId()
        ]
        List results = executeSelectSql(QUERY_PROC_PURCHASE_ORDER, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_INV_INVENTORY_TRANSACTION = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction
            WHERE transaction_entity_type_id = :transactionEntityTypeId
              AND transaction_entity_id = :supplierId
              AND company_id = :companyId """
    /**
     * count number of In-From-Supplier transaction(InvInventoryTransaction) of a specific supplier
     * @param supplierId -Supplier.id
     * @return -int value
     */
    private int countInventoryTransaction(long supplierId) {
        Map queryParams = [
                transactionEntityTypeId: inventoryImplService.getTransactionEntityTypeIdSupplier(),
                supplierId: supplierId,
                companyId: appSessionUtil.getCompanyId()
        ]
        List results = executeSelectSql(QUERY_INV_INVENTORY_TRANSACTION, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_ACC_VOUCHER_DETAILS = """
            SELECT COUNT(id) AS count
            FROM acc_voucher_details
            WHERE source_type_id = :sourceTypeId
            AND source_id = :supplierId
            """
    /**
     * count number of voucher(s) related with a specific supplier
     * @param supplierId -Supplier.id
     * @return -int value
     */
    private int countVoucherDetails(long supplierId) {
        Map queryParams = [
                sourceTypeId: accountingImplService.getAccSourceTypeSupplier(),
                supplierId: supplierId
        ]
        List results = executeSelectSql(QUERY_ACC_VOUCHER_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_ACC_LC = """
            SELECT COUNT(id) AS count
            FROM acc_lc
            WHERE supplier_id = :supplierId AND
                  company_id = :companyId
            """
    /**
     * count number of LC(s) related with a specific supplier
     * @param supplierId -Supplier.id
     * @return -int value
     */
    private int countLc(long supplierId) {
        Map queryParams = [
                supplierId: supplierId,
                companyId: appSessionUtil.getCompanyId()
        ]
        List results = executeSelectSql(QUERY_ACC_LC, queryParams)
        int count = results[0].count
        return count
    }
}
