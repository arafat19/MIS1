package com.athena.mis.inventory.actions.report.itemreceivedstock

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get stock list of inventory item(s) received from supplier
 *  For details go through Use-Case doc named 'ListForItemReceivedStockActionService'
 */
class ListForItemReceivedStockActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility

    private static final String ITEM_RECEIVED_STOCK_LIST = "itemReceivedStockList"
    private static final String COUNT = "count"
    private static final String ITEM_RECEIVED_STOCK_LIST_GRID = "gridOutput"
    private static final String EXCEPTION_MESSAGE = "Can't not search item received stock"
    private static final String INVALID_INPUT_MESSAGE = "Can't not get item received stock due to invalid input"
    private static final String DEFAULT_ERROR_MESSAGE = "Fail to load item received stock"
    private static final String ITEM_RECEIVED_STOCK_NOT_FOUND = "Item Received Stock not found"
    private static final String NO_SUPPLIER_FOUND = "No supplier found"
    private static final String NO_PROJECT_FOUND = "User has no associated project"
    private static final String SORT_NAME = "supplier.name, item.name"
    private static final String SORT_ORDER = "asc"

    /**
     * Checking existence of required parameters send from UI
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if ((!parameterMap.fromDate) || (!parameterMap.toDate)) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MESSAGE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for executePostCondition operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * method to get stock list of inventory item(s) received from supplier
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing item-receive list and isError(True/False) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.rp) {
                parameterMap.sortname = SORT_NAME
                parameterMap.sortorder = SORT_ORDER
                parameterMap.rp = 20
                parameterMap.page = 1
            }
            initPager(parameterMap)

            Date fromDate = DateUtility.parseMaskedDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedDate(parameterMap.toDate.toString())

            List<Long> lstSupplierIds = []
            List supplierList
            if (parameterMap.supplierId.equals(Tools.EMPTY_SPACE)) {//if no specific supplier is selected then get all supplierList of login user company
                supplierList = supplierCacheUtility.list()
                if (supplierList.size() == 0) {//if no supplier found then return with message
                    result.put(Tools.MESSAGE, NO_SUPPLIER_FOUND)
                    return result
                } else {//get supplierIds
                    for (int i = 0; i < supplierList.size(); i++) {
                        lstSupplierIds << supplierList[i].id
                    }
                }
            } else { //if specific supplier is selected
                long supplierId = Long.parseLong(parameterMap.supplierId.toString())
                lstSupplierIds << new Long(supplierId)
            }

            List<Long> lstProjectIds = []
            List projectList
            if (parameterMap.projectId.equals(Tools.EMPTY_SPACE)) {//if no specific project is selected then get user-mapped projectList
                projectList = invSessionUtil.appSessionUtil.getUserProjects()
                if (projectList.size() == 0) {//if no user-mapped project found then return with message
                    result.put(Tools.MESSAGE, NO_PROJECT_FOUND)
                    return result
                } else {//get supplierIds
                    for (int i = 0; i < projectList.size(); i++) {
                        lstProjectIds << projectList[i].id
                    }
                }
            } else {//if specific project is selected
                long projectId = Long.parseLong(parameterMap.projectId.toString())
                lstProjectIds << new Long(projectId)
            }

            long companyId = invSessionUtil.appSessionUtil.getCompanyId()
            List<Long> itemTypeIds = []
            if (parameterMap.itemTypeId.equals(Tools.EMPTY_SPACE)) {
                //if no specific item type is selected then get all item types
                SystemEntity itemCategoryInventory = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.INVENTORY, companyId)
                itemTypeIds = itemTypeCacheUtility.listIdsByCategoryId(itemCategoryInventory.id)
            } else { //if specific item type is given then put given item type id in list
                long itemTypeId = Long.parseLong(parameterMap.itemTypeId.toString())
                itemTypeIds << new Long(itemTypeId)
            }

            //get item-received-list-from-supplier
            LinkedHashMap itemReceivedQtyMap = listItemReceivedStock(lstProjectIds, lstSupplierIds, fromDate, toDate, itemTypeIds)
            int count = Integer.parseInt(itemReceivedQtyMap.count.toString())
            if (count <= 0) {
                result.put(Tools.MESSAGE, ITEM_RECEIVED_STOCK_NOT_FOUND)
                return result
            }
            result.put(ITEM_RECEIVED_STOCK_LIST, itemReceivedQtyMap.listReceivedStock)
            result.put(COUNT, itemReceivedQtyMap.count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            return result
        }
    }

    /**
     * Build a map with all necessary objects for grid view
     * @param obj -map returned from execute method
     * @return -a map containing Wrapped ItemReceivedStockList for grid &
     *              isError(True/False) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap returnResult = (LinkedHashMap) obj
            List<GroovyRowResult> itemReceivedStockList = (List<GroovyRowResult>) returnResult.get(ITEM_RECEIVED_STOCK_LIST)

            List itemReceivedQtyListWrap = wrapItemReceivedStockGridEntityList(itemReceivedStockList, start)
            int count = Integer.parseInt(returnResult.get(COUNT).toString())
            Map gridOutput = [page: pageNumber, total: count, rows: itemReceivedQtyListWrap]
            result.put(ITEM_RECEIVED_STOCK_LIST_GRID, gridOutput)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous method, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap returnResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (returnResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, returnResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            return result
        }
    }

    /**
     * Wrapped-ItemReceivedStockList for grid
     * @param itemReceivedStockList -List of GroovyRowResult
     * @param start -start index
     * @return -Wrapped ItemReceivedStockList
     */
    private List wrapItemReceivedStockGridEntityList(List<GroovyRowResult> itemReceivedStockList, int start) {
        List inventoryIns = [] as List
        int counter = start + 1
        GridEntity obj
        GroovyRowResult singleRow
        for (int i = 0; i < itemReceivedStockList.size(); i++) {
            singleRow = itemReceivedStockList[i]
            obj = new GridEntity()
            obj.id = counter
            obj.cell = [
                    counter,
                    singleRow.supplier_name,
                    singleRow.item_name,
                    singleRow.received_quantity,
                    singleRow.total_amount
            ]
            inventoryIns << obj
            counter++
        }
        return inventoryIns
    }

    /**
     * method to get a map that contains list of Item(s)ReceivedFromSupplier and count
     *
     * @param lstProjectIds -list of projectIds(Project.id)
     * @param lstSupplierIds -list of supplierIds(Supplier.id)
     * @param fromDate -Start date
     * @param dateTo -end date
     * @param lstItemTypeIds -list of itemTypeIds(ItemType.id)
     * @return -map contains list of ItemReceivedStockFromSupplierList and count
     */
    private Map listItemReceivedStock(List<Long> lstProjectIds, List<Long> lstSupplierIds, Date fromDate, Date toDate, List<Long> lstItemTypeIds) {
        String projectIds = Tools.buildCommaSeparatedStringOfIds(lstProjectIds)
        String supplierIds = Tools.buildCommaSeparatedStringOfIds(lstSupplierIds)
        String itemTypeIds = Tools.buildCommaSeparatedStringOfIds(lstItemTypeIds)
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)

        String queryStr = """
            SELECT supplier.name AS supplier_name, item.name AS item_name,
                to_char(coalesce(SUM(iitd.actual_quantity),0),'${Tools.DB_QUANTITY_FORMAT}') || ' ' || item.unit AS received_quantity,
                to_char(coalesce(SUM(iitd.actual_quantity*rate),0),'${Tools.DB_CURRENCY_FORMAT}') AS total_amount
                FROM inv_inventory_transaction_details iitd
                LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
                LEFT JOIN supplier ON supplier.id = iit.transaction_entity_id
                LEFT JOIN item ON item.id = iitd.item_id
                WHERE iit.transaction_type_id =:transactionTypeId
                    AND iit.transaction_entity_type_id =:transactionEntityTypeId
                    AND iit.transaction_entity_id IN (${supplierIds})
                    AND iit.project_id IN (${projectIds})
                    AND item.item_type_id IN (${itemTypeIds})
                    AND iitd.approved_by > 0
                    AND iitd.is_current=true
                    AND iitd.transaction_date BETWEEN :fromDate AND :toDate
            GROUP BY supplier.name, item.name, item.unit
            ORDER BY ${sortColumn} ${sortOrder}
            LIMIT :resultPerPage OFFSET :start
        """

        String queryCount = """
        SELECT COUNT(*) AS count FROM
        (
           SELECT COUNT(iit.transaction_entity_id) FROM inv_inventory_transaction_details iitd
           LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
           LEFT JOIN supplier ON supplier.id = iit.transaction_entity_id
           LEFT JOIN item ON item.id = iitd.item_id
           WHERE iit.transaction_type_id =:transactionTypeId
             AND iit.transaction_entity_type_id =:transactionEntityTypeId
             AND iit.transaction_entity_id IN (${supplierIds})
             AND iit.project_id IN (${projectIds})
             AND item.item_type_id IN (${itemTypeIds})
             AND iitd.approved_by > 0
             AND iitd.is_current=true
             AND iitd.transaction_date BETWEEN :fromDate AND :toDate
           GROUP BY supplier.name, item.name, item.unit
        ) as temp
        """

        Map queryParams = [
                transactionTypeId: transactionTypeIn.id,
                transactionEntityTypeId: transactionEntitySupplier.id,
                fromDate: DateUtility.getSqlDate(fromDate),
                toDate: DateUtility.getSqlDate(toDate),
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> listReceivedStock = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(queryCount, queryParams)
        int count = countResults[0].count
        return [listReceivedStock: listReceivedStock, count: count]
    }
}
