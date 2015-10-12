package com.athena.mis.inventory.actions.report.inventorystatus

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
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
 *  Class to get list of inventory quantity stock and amount(value) status of item(s)
 *  For details go through Use-Case doc named 'ListForInventoryStatusWithQuantityAndValueActionService'
 */
class ListForInventoryStatusWithQuantityAndValueActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    private static final String INVENTORY_STATUS_LIST = "inventoryStatusList"
    private static final String COUNT = "count"
    private static final String INVENTORY_STOCK_LIST_GRID = "gridOutput"
    private static final String EXCEPTION_MESSAGE = "Inventory Status not found."
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to get inventory status"
    private static final String INVALID_INPUT = "Failed to get inventory status due to invalid input"
    private static final String NO_INVENTORY_MAPPED = "User is not mapped with any inventory"

    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility

    /**
     * check required parameters
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if ((!parameterMap.toDate) || (!parameterMap.fromDate)) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get list of inventory stock quantity and amount(value) status of item(s)
     *      -if specific project is given then get InventoryQuantityStockAndAmountStatusList by given project and date range
     *      -else select all projects then : pull all user mapped projectIds & then get InventoryQuantityStockAndAmountStatusList
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())

            List<Long> projectIds = []
            if (parameterMap.projectId.equals(Tools.EMPTY_SPACE)) {
                //if no specific project is selected then get user-mapped projectIds
                List<Long> tempProjectIdList = invSessionUtil.appSessionUtil.getUserProjectIds()
                if (tempProjectIdList.size() <= 0) { //if tempList is null then set 0 at main list, So that cache don't update
                    projectIds << new Long(0)
                } else {  //if tempList is not null then set tempProjectIdList at main list
                    projectIds = tempProjectIdList
                }
            } else { //if specific project is given then put given projectId in list
                long projectId = Long.parseLong(parameterMap.projectId.toString())
                projectIds << new Long(projectId)
            }

            if (!parameterMap.rp) {
                parameterMap.rp = 20
                parameterMap.page = 1
            }
            initPager(parameterMap)

            // assign default value -1 for 'ALL'-(projects/inventories/inventory types)
            long projectId = parameterMap.projectId.equals(Tools.EMPTY_SPACE) ? -1L : Long.parseLong(parameterMap.projectId.toString())
            long inventoryId = parameterMap.inventoryId.equals(Tools.EMPTY_SPACE) ? -1L : Long.parseLong(parameterMap.inventoryId.toString())
            long inventoryTypeId = parameterMap.inventoryTypeId.equals(Tools.EMPTY_SPACE) ? -1L : Long.parseLong(parameterMap.inventoryTypeId.toString())
            // get inventory ids
            List<Long> lstInventoryIds = []
            if (inventoryTypeId < 0 && inventoryId < 0) {
                lstInventoryIds = projectId > 0 ? invSessionUtil.getUserInventoryIdsByProject(projectId) : invSessionUtil.getUserInventoryIds()
            } else if (inventoryTypeId > 0 && inventoryId < 0) {
                lstInventoryIds = projectId > 0 ? invSessionUtil.getUserInventoryIdsByTypeAndProject(inventoryTypeId, projectId) : invSessionUtil.getUserInventoryIdsByType(inventoryTypeId)
            } else if (inventoryTypeId > 0 && inventoryId > 0) {
                lstInventoryIds << new Long(inventoryId)
            }
            // check if user is mapped with any inventory or not
            if (lstInventoryIds.size() <= 0) {
                result.put(Tools.MESSAGE, NO_INVENTORY_MAPPED)
                return result
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

            //get inventory-status-list based on projectId list and date range
            LinkedHashMap inventoryStatusMap = getInventoryStatusList(projectIds, fromDate, toDate, lstInventoryIds, itemTypeIds, companyId)
            if (inventoryStatusMap.count.toInteger() <= 0) {
                result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
                return result
            }
            result.put(INVENTORY_STATUS_LIST, inventoryStatusMap.inventoryStatusList)
            result.put(COUNT, inventoryStatusMap.count)
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
     * Build a map with all necessary objects for grid view
     * @param obj -map returned from execute method
     * @return -a map containing Wrapped InventoryQuantityAndAmountStockStatusList for grid &
     *              isError(True/False) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap returnResult = (LinkedHashMap) obj
            List<GroovyRowResult> inventoryStatusList = (List<GroovyRowResult>) returnResult.get(INVENTORY_STATUS_LIST)

            //wrap inventoryQuantityAndAmountStockStatusList for grid
            List inventoryStockListWrap = wrapInventoryStatusGridEntityList(inventoryStatusList, this.start)
            int count = Integer.parseInt(returnResult.get(COUNT).toString())
            Map gridOutput = [page: pageNumber, total: count, rows: inventoryStockListWrap]
            result.put(INVENTORY_STOCK_LIST_GRID, gridOutput)
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrapped-inventoryQuantityAndAmountStockStatusList for grid
     * @param inventoryStatusList -List of GroovyRowResult
     * @param start -start index
     * @return -WrappedInventoryQuantityAndAmountStockStatusList
     */
    private List wrapInventoryStatusGridEntityList(List<GroovyRowResult> inventoryStatusList, int start) {
        List inventoryIns = [] as List
        int counter = start + 1
        GridEntity obj
        GroovyRowResult singleRow
        for (int i = 0; i < inventoryStatusList.size(); i++) {
            singleRow = inventoryStatusList[i]
            obj = new GridEntity()
            obj.id = singleRow.item_id
            obj.cell = [
                    counter,
                    singleRow.item_name,
                    singleRow.total_pre_stock_quantity,
                    singleRow.total_pre_stock_amount,
                    singleRow.received_quantity,
                    singleRow.received_amount,
                    singleRow.total_production_quantity,
                    singleRow.total_production_amount,
                    singleRow.total_budget_consume_quantity,
                    singleRow.total_budget_consume_amount,
                    singleRow.total_prod_consume_quantity,
                    singleRow.total_prod_consume_amount,
                    singleRow.total_stock_quantity,
                    singleRow.total_stock_amount
            ]
            inventoryIns << obj
            counter++
        }
        return inventoryIns
    }

    /**
     * method to get a map that contains list of inventoryQuantityAndAmountStockStatusList and count
     *
     * @param projectIds -list of projectIds(Project.id)
     * @param dateFrom -Start date
     * @param dateTo -end date
     * @param lstInventoryIds -list of inventory ids
     * @param itemTypeIds -list of item type ids
     * @param companyId -id of company
     * @return -map contains list of inventoryQuantityAndAmountStockStatus and count
     */
    private LinkedHashMap getInventoryStatusList(List<Long> projectIds, Date fromDate, Date toDate, List<Long> lstInventoryIds, List<Long> itemTypeIds, long companyId) {
        String projectIdStr = Tools.buildCommaSeparatedStringOfIds(projectIds)
        String strInventoryIds = Tools.buildCommaSeparatedStringOfIds(lstInventoryIds)
        String strItemTypeIds = Tools.buildCommaSeparatedStringOfIds(itemTypeIds)
        Date dbFromDate = DateUtility.getSqlDate(fromDate)
        Date dbToDate = DateUtility.getSqlDate(toDate)

        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionTypeProduction = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)
        SystemEntity transactionTypeConsumption = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)
        SystemEntity itemCategoryFixedAsset = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.FIXED_ASSET, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)

        String queryStr = """
            SELECT iitd.item_id AS item_id,item.name AS item_name,
        
            to_char(COALESCE(received_supplier.received_quantity,0), '${Tools.DB_QUANTITY_FORMAT}') ||' '|| item.unit AS received_quantity,
            to_char(COALESCE(received_supplier.received_amount,0), '${Tools.DB_CURRENCY_FORMAT}')  AS received_amount,
        
            to_char(COALESCE(budget_consumption.total_budget_consume_quantity,0), '${Tools.DB_QUANTITY_FORMAT}') ||' '|| item.unit AS total_budget_consume_quantity,
            to_char(COALESCE(budget_consumption.total_budget_consume_amount,0), '${Tools.DB_CURRENCY_FORMAT}') AS total_budget_consume_amount,

            to_char(COALESCE(prod_consumption.total_prod_consume_quantity,0), '${Tools.DB_QUANTITY_FORMAT}') ||' '|| item.unit AS total_prod_consume_quantity,
            to_char(COALESCE(prod_consumption.total_prod_consume_amount,0), '${Tools.DB_CURRENCY_FORMAT}') AS total_prod_consume_amount,

            to_char(COALESCE(production.total_production_quantity,0), '${Tools.DB_QUANTITY_FORMAT}') ||' '|| item.unit AS total_production_quantity,
            to_char(COALESCE(production.total_production_amount,0), '${Tools.DB_CURRENCY_FORMAT}') AS total_production_amount,

            to_char(
                    (
                      COALESCE(pre_received_supplier.pre_received_quantity,0) + COALESCE(received_supplier.received_quantity,0) +
                      COALESCE(pre_production.pre_total_production_quantity,0) + COALESCE(production.total_production_quantity,0) -
                      COALESCE(pre_budget_consumption.pre_total_budget_consume_quantity,0) - COALESCE(budget_consumption.total_budget_consume_quantity,0) -
                      COALESCE(pre_prod_consumption.pre_total_prod_consume_quantity,0) - COALESCE(prod_consumption.total_prod_consume_quantity,0)), '${Tools.DB_QUANTITY_FORMAT}') ||' '|| item.unit
            AS total_stock_quantity,

            to_char(
                    (
                      COALESCE(pre_received_supplier.pre_received_amount,0) + COALESCE(received_supplier.received_amount,0) +
                      COALESCE(pre_production.pre_total_production_amount,0) + COALESCE(production.total_production_amount,0) -
                      COALESCE(pre_budget_consumption.pre_total_budget_consume_amount,0) - COALESCE(budget_consumption.total_budget_consume_amount,0) -
                      COALESCE(pre_prod_consumption.pre_total_prod_consume_amount,0) - COALESCE(prod_consumption.total_prod_consume_amount,0)), '${Tools.DB_CURRENCY_FORMAT}')
            AS total_stock_amount,

            to_char((COALESCE(pre_received_supplier.pre_received_quantity,0) + COALESCE(pre_production.pre_total_production_quantity,0) -
                    COALESCE(pre_budget_consumption.pre_total_budget_consume_quantity,0) - COALESCE(pre_prod_consumption.pre_total_prod_consume_quantity,0)),
                    '${Tools.DB_QUANTITY_FORMAT}') ||' '|| item.unit AS total_pre_stock_quantity,

            to_char((COALESCE(pre_received_supplier.pre_received_amount,0) + COALESCE(pre_production.pre_total_production_amount,0) -
                    COALESCE(pre_budget_consumption.pre_total_budget_consume_amount,0) - COALESCE(pre_prod_consumption.pre_total_prod_consume_amount,0)),
                    '${Tools.DB_CURRENCY_FORMAT}') AS total_pre_stock_amount

            FROM vw_inv_inventory_transaction_with_details iitd

            FULL OUTER JOIN
            (
              SELECT item_id,
                     SUM(actual_quantity) AS received_quantity,
                     SUM(actual_quantity*rate) AS received_amount
              FROM vw_inv_inventory_transaction_with_details
              LEFT JOIN item ON item.id = vw_inv_inventory_transaction_with_details.item_id
              WHERE project_id IN (${projectIdStr}) AND
                    transaction_type_id = :transactionTypeIn AND
                    transaction_entity_type_id = :entityTypeSupplier AND
                    approved_by > 0 AND
                    is_current = true AND
                    item.item_type_id IN (${strItemTypeIds}) AND
                    inventory_id IN (${strInventoryIds}) AND
                    transaction_date BETWEEN :dbFromDate AND :dbToDate
              GROUP BY item_id
            ) received_supplier
            ON received_supplier.item_id = iitd.item_id

            FULL OUTER JOIN
            (
            SELECT item_id,
                    SUM(actual_quantity) AS pre_received_quantity,
                    SUM(actual_quantity*rate) AS pre_received_amount
                    FROM vw_inv_inventory_transaction_with_details
                    LEFT JOIN item ON item.id = vw_inv_inventory_transaction_with_details.item_id
                    WHERE project_id IN (${projectIdStr}) AND
            transaction_type_id = :transactionTypeIn AND
            transaction_entity_type_id = :entityTypeSupplier AND
            approved_by > 0 AND
            is_current = true AND
            item.item_type_id IN (${strItemTypeIds}) AND
            inventory_id IN (${strInventoryIds}) AND
            transaction_date < :dbFromDate
                    GROUP BY item_id
            ) pre_received_supplier
            ON pre_received_supplier.item_id = iitd.item_id

            FULL OUTER JOIN
            (
              SELECT item_id,
                     SUM(actual_quantity) AS total_budget_consume_quantity,
                     SUM(actual_quantity*rate) AS total_budget_consume_amount
              FROM vw_inv_inventory_transaction_with_details
              LEFT JOIN item ON item.id = vw_inv_inventory_transaction_with_details.item_id
              WHERE project_id IN (${projectIdStr}) AND
                    transaction_type_id = :transactionTypeConsumption AND
                    approved_by > 0 AND
                    is_current=true AND
                    budget_id > 0 AND
                    item.item_type_id IN (${strItemTypeIds}) AND
                    inventory_id IN (${strInventoryIds}) AND
                    transaction_date BETWEEN :dbFromDate AND :dbToDate
              GROUP BY item_id
            ) budget_consumption
            ON budget_consumption.item_id = iitd.item_id

            FULL OUTER JOIN
            (
            SELECT item_id,
                    SUM(actual_quantity) AS pre_total_budget_consume_quantity,
                    SUM(actual_quantity*rate) AS pre_total_budget_consume_amount
                    FROM vw_inv_inventory_transaction_with_details
                    LEFT JOIN item ON item.id = vw_inv_inventory_transaction_with_details.item_id
            WHERE project_id IN (${projectIdStr}) AND
                  transaction_type_id = :transactionTypeConsumption AND
                  approved_by > 0 AND
                  is_current=true AND
                  budget_id > 0 AND
                  item.item_type_id IN (${strItemTypeIds}) AND
                  inventory_id IN (${strInventoryIds}) AND
                  transaction_date < :dbFromDate
             GROUP BY item_id
            ) pre_budget_consumption
            ON pre_budget_consumption.item_id = iitd.item_id
        
            FULL OUTER JOIN
            (
              SELECT item_id,
                     SUM(actual_quantity) AS total_prod_consume_quantity,
                     SUM(actual_quantity*rate) AS total_prod_consume_amount
              FROM vw_inv_inventory_transaction_with_details
              LEFT JOIN item ON item.id = vw_inv_inventory_transaction_with_details.item_id
              WHERE project_id IN (${projectIdStr}) AND
                    transaction_type_id = :transactionTypeConsumption AND
                    approved_by > 0 AND
                    is_current=true AND 
                    inv_production_line_item_id > 0 AND
                    item.item_type_id IN (${strItemTypeIds}) AND
                    inventory_id IN (${strInventoryIds}) AND
                    transaction_date BETWEEN :dbFromDate AND :dbToDate
              GROUP BY item_id
            ) prod_consumption
            ON prod_consumption.item_id = iitd.item_id

            FULL OUTER JOIN
            (
            SELECT item_id,
                    SUM(actual_quantity) AS pre_total_prod_consume_quantity,
                    SUM(actual_quantity*rate) AS pre_total_prod_consume_amount
                    FROM vw_inv_inventory_transaction_with_details
                    LEFT JOIN item ON item.id = vw_inv_inventory_transaction_with_details.item_id
            WHERE project_id IN (${projectIdStr}) AND
                  transaction_type_id = :transactionTypeConsumption AND
                  approved_by > 0 AND
                  is_current=true AND
                  inv_production_line_item_id > 0 AND
                  item.item_type_id IN (${strItemTypeIds}) AND
                  inventory_id IN (${strInventoryIds}) AND
                  transaction_date < :dbFromDate
             GROUP BY item_id
            ) pre_prod_consumption
            ON pre_prod_consumption.item_id = iitd.item_id

            FULL OUTER JOIN
            (
             SELECT item_id,
                     SUM(actual_quantity) AS total_production_quantity,
                     SUM(actual_quantity*rate) AS total_production_amount
              FROM vw_inv_inventory_transaction_with_details
              LEFT JOIN item ON item.id = vw_inv_inventory_transaction_with_details.item_id
              WHERE project_id IN (${projectIdStr}) AND
                    transaction_type_id = :transactionTypeProduction AND
                    approved_by > 0 AND
                    is_current=true AND
                    item.item_type_id IN (${strItemTypeIds}) AND
                    inventory_id IN (${strInventoryIds}) AND
                    transaction_date BETWEEN :dbFromDate AND :dbToDate
              GROUP BY item_id
            ) production
            ON production.item_id = iitd.item_id

            FULL OUTER JOIN
            (
            SELECT item_id,
                    SUM(actual_quantity) AS pre_total_production_quantity,
                    SUM(actual_quantity*rate) AS pre_total_production_amount
                    FROM vw_inv_inventory_transaction_with_details
                    LEFT JOIN item ON item.id = vw_inv_inventory_transaction_with_details.item_id
            WHERE project_id IN (${projectIdStr}) AND
                  transaction_type_id = :transactionTypeProduction  AND
                  approved_by > 0 AND
                  is_current=true AND
                  item.item_type_id IN (${strItemTypeIds}) AND
                  inventory_id IN (${strInventoryIds}) AND
                  transaction_date < :dbFromDate
             GROUP BY item_id
            ) pre_production
            ON pre_production.item_id = iitd.item_id
        
            LEFT JOIN item ON item.id = iitd.item_id
            WHERE iitd.approved_by > 0 AND
                  iitd.is_current = true AND
                  item.category_id <> :itemCategoryFixedAsset AND
                  iitd.transaction_entity_type_id NOT IN (:transactionEntityTypeId) AND
                  iitd.project_id IN (${projectIdStr}) AND
                  item.item_type_id IN (${strItemTypeIds}) AND
                  inventory_id IN (${strInventoryIds}) AND
                  iitd.transaction_date BETWEEN :dbFromDate AND :dbToDate
            GROUP BY iitd.item_id, item.name, item.unit,
                     pre_received_supplier.pre_received_quantity, pre_received_supplier.pre_received_amount,
                     received_supplier.received_quantity, received_supplier.received_amount,
                     pre_budget_consumption.pre_total_budget_consume_quantity, pre_budget_consumption.pre_total_budget_consume_amount,
                     budget_consumption.total_budget_consume_quantity, budget_consumption.total_budget_consume_amount,
                     pre_prod_consumption.pre_total_prod_consume_quantity, pre_prod_consumption.pre_total_prod_consume_amount,
                     prod_consumption.total_prod_consume_quantity, prod_consumption.total_prod_consume_amount,
                     pre_production.pre_total_production_quantity, pre_production.pre_total_production_amount,
                     production.total_production_quantity, production.total_production_amount
            ORDER BY item.name
            LIMIT :resultPerPage OFFSET :start
        """
        Map queryParams = [
                dbFromDate: dbFromDate,
                dbToDate: dbToDate,
                itemCategoryFixedAsset: itemCategoryFixedAsset.id,
                transactionEntityTypeId: transactionEntityInventory.id,
                transactionTypeProduction: transactionTypeProduction.id,
                transactionTypeConsumption: transactionTypeConsumption.id,
                transactionTypeIn: transactionTypeIn.id,
                entityTypeSupplier: transactionEntitySupplier.id,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> inventoryStatusList = executeSelectSql(queryStr, queryParams)

        String queryCount = """
            SELECT COUNT(DISTINCT(iitd.item_id)) AS count
            FROM vw_inv_inventory_transaction_with_details iitd
            LEFT JOIN item ON item.id = iitd.item_id
            WHERE iitd.transaction_date BETWEEN :dbFromDate AND :dbToDate
              AND iitd.transaction_entity_type_id NOT IN (:transactionEntityTypeId)
              AND item.category_id <> :itemCategoryFixedAsset
              AND iitd.approved_by > 0
              AND iitd.is_current = TRUE
              AND iitd.project_id IN (${projectIdStr})
              AND item.item_type_id IN (${strItemTypeIds})
              AND inventory_id IN (${strInventoryIds})
        """
        List<GroovyRowResult> resultCount = executeSelectSql(queryCount, queryParams)
        int total = resultCount[0].count ? resultCount[0].count as int : 0
        return [inventoryStatusList: inventoryStatusList, count: total]
    }
}
