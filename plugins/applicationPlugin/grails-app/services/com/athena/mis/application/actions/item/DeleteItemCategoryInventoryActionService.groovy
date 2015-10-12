package com.athena.mis.application.actions.item

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Item
import com.athena.mis.application.service.ItemService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.integration.accounting.AccountingPluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete item (Category: Inventory)
 *  For details go through Use-Case doc named 'DeleteItemCategoryInventoryActionService'
 */
class DeleteItemCategoryInventoryActionService extends BaseService implements ActionIntf {

    ItemService itemService
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired(required = false)
    AccountingPluginConnector accountingImplService

    private static final String ITEM_DELETE_SUCCESS_MSG = "Item has been successfully deleted"
    private static final String ITEM_DELETE_FAILURE_MSG = "Item has not been deleted"
    private static final String DEFAULT_ERROR_MESSAGE = "Could not delete item"
    private static final String HAS_ASSOCIATION_SUPPLIER_ITEM = " supplier(s) associated with selected item"
    private static final String HAS_ASSOCIATION_BUDGET_DETAILS = " budget details(s) associated with selected item"
    private static final String HAS_ASSOCIATION_PURCHASE_REQUEST = " purchase request(s) associated with selected item"
    private static final String HAS_ASSOCIATION_INVENTORY_TRANSACTION = " inventory transaction(s) associated with selected item"
    private static final String HAS_ASSOCIATION_LC = " LC(s) are associated with selected item"
    private static final String HAS_ASSOCIATION_LEASE_ACCOUNT = " Lease Account(s) are associated with selected item"
    private static final String HAS_ASSOCIATION_VOUCHER = " voucher(s) are associated with selected item"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria to delete item object
     *      1) Check access permission to delete item
     *      2) Check existence of required parameter
     *      3) Check existence of item object
     *      4) check association with different domain(s) of installed plugin(s)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            //Only admin can delete item object
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.itemId) { // check required parameter
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long itemId = Long.parseLong(params.itemId.toString())
            Item item = (Item) itemCacheUtility.read(itemId)
            if (!item) { //check existence of item object
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            //check association
            Map preResult = (Map) hasAssociation(item)
            Boolean hasAssociation = (Boolean) preResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * delete item object from DB & cache
     * @param parameters -parameter send from UI
     * @param obj -N/A
     * @return -Boolean value (true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long itemId = Long.parseLong(parameterMap.itemId.toString())
            itemService.delete(itemId) //delete from DB
            itemCacheUtility.delete(itemId)//delete from cache
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
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
        return [deleted: Boolean.TRUE.booleanValue(), message: ITEM_DELETE_SUCCESS_MSG]
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
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, ITEM_DELETE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ITEM_DELETE_FAILURE_MSG)
            return result
        }
    }

    /**
     * method to check if item has any association with different domain(s) of installed plugin(s)
     * @param item -Item object
     * @return -a map contains booleanValue(true/false) and association message
     *          based on existence of association
     */
    private LinkedHashMap hasAssociation(Item item) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)
        long itemId = item.id
        long companyId = item.companyId
        int count = 0

        count = countSupplierItem(itemId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_SUPPLIER_ITEM)
            return result
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.BUDGET)) {
            count = countBudgetDetails(itemId, companyId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_BUDGET_DETAILS)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.PROCUREMENT)) {
            count = countPurchaseRequestDetails(itemId, companyId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_PURCHASE_REQUEST)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
            count = countInventoryTransactionDetails(itemId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_INVENTORY_TRANSACTION)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
            count = countLc(itemId, companyId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_LC)
                return result
            }

            count = countLeaseAccount(itemId, companyId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_LEASE_ACCOUNT)
                return result
            }

            count = countVoucherDetails(itemId, companyId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_VOUCHER)
                return result
            }
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    /**
     * count number of item(s) mapped with supplier(s)
     * @param itemId -Item.id
     * @return -int value
     */
    private int countSupplierItem(long itemId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM supplier_item
            WHERE item_id = ${itemId}
        """

        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * count number of item(s) associated with budget_details
     * @param itemId -Item.id
     * @param companyId -Company.id
     * @return -int value
     */
    private int countBudgetDetails(long itemId, long companyId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM budg_budget_details
            WHERE item_id = ${itemId} AND
                  company_id = ${companyId}
        """

        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * count number of item(s) associated with purchase_request_details
     * @param itemId -Item.id
     * @param companyId -Company.id
     * @return -int value
     */
    private int countPurchaseRequestDetails(long itemId, long companyId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_request_details
            WHERE item_id = ${itemId} AND
                  company_id = ${companyId}
            """

        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * count number of item(s) associated with inventory_transaction_details
     * @param itemId -Item.id
     * @return -int value
     */
    public int countInventoryTransactionDetails(long itemId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction_details
            WHERE item_id = ${itemId} """

        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * count number of item(s) associated with acc_lc
     * @param itemId -Item.id
     * @param companyId -Company.id
     * @return -int value
     */
    public int countLc(long itemId, long companyId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM acc_lc
            WHERE item_id = ${itemId} AND
                  company_id = ${companyId}
            """

        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * count number of item(s) associated with acc_lease_account
     * @param itemId -Item.id
     * @param companyId -Company.id
     * @return -int value
     */
    public int countLeaseAccount(long itemId, long companyId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM acc_lease_account
            WHERE item_id = ${itemId} AND
                  company_id = ${companyId}
            """

        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * count number of item(s) associated with acc_voucher
     * @param itemId -Item.id
     * @param companyId -Company.id
     * @return -int value
     */
    public int countVoucherDetails(long itemId, long companyId) {
        String queryStr = """
            SELECT COUNT(voucher_details_id) AS count
            FROM vw_acc_voucher_with_details
            WHERE source_type_id = ${accountingImplService.getAccSourceTypeItem()} AND
                  source_id = ${itemId} AND
                  company_id = ${companyId}
            """

        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }
}
