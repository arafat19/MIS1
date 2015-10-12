package com.athena.mis.inventory.actions.report.itemstock

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


/**
 *  Show list of Item-Stock for grid
 *  For details go through Use-Case doc named 'ShowForItemStockActionService'
 */
class ShowForItemStockActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to load Item Stock."
    private static final String ITEM_STOCK_LIST = "itemStockList"
    private static final String COUNT = "count"
    private static final String SORT_COLUMN = "item.name"
    private static final String SORT_ORDER = "ASC"

    /**
     * do nothing for executePreCondition operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for executePostCondition operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get list of itemStock and wrap for grid
     * @param parameters -N/A
     * @param obj -N/A
     * @return -a map containing wrapped itemStockList for grid and isError(True/False) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            initPager(params)

            sortColumn = SORT_COLUMN
            sortOrder = SORT_ORDER
            resultPerPage = 25

            //map contains itemStock list and count
            Map serviceReturn = getItemStockList()
            List<GroovyRowResult> itemStockList = serviceReturn.itemStockList
            int count = serviceReturn.count

            //Wrap ItemWiseBudgetSummaryList for grid
            List itemStockListWrap = wrapItemStockGridEntityList(itemStockList, start)

            result.put(ITEM_STOCK_LIST, itemStockListWrap)
            result.put(COUNT, count)
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
     * @param obj -map returned from execute method
     * @return -a map containing Wrapped ItemStockList for grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List itemStockList = (List) executeResult.get(ITEM_STOCK_LIST)
            int count = (int) executeResult.get(COUNT)
            Map gridOutput = [page: pageNumber, total: count, rows: itemStockList]
            result.put(ITEM_STOCK_LIST, gridOutput)
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
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap returnResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (returnResult.message) {
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
     * Wrapped-ItemStockList for grid
     * @param itemStockList -List of GroovyRowResult
     * @param start -start index
     * @return -WrappedItemStockList
     */
    private List wrapItemStockGridEntityList(List<GroovyRowResult> itemStockList, int start) {
        List inventoryIns = [] as List
        int counter = start + 1
        GridEntity obj
        GroovyRowResult singleRow
        for (int i = 0; i < itemStockList.size(); i++) {
            singleRow = itemStockList[i]
            obj = new GridEntity()
            obj.id = singleRow.id
            obj.cell = [counter, singleRow.name, singleRow.item_quantity]
            inventoryIns << obj
            counter++
        }
        return inventoryIns
    }

    private static final String COUNT_QUERY = """
            SELECT COUNT(item.id) FROM item
          LEFT JOIN (SELECT SUM(iits.consumeable_stock) AS quantity, item_id
                FROM vw_inv_inventory_consumable_stock iits GROUP BY item_id) AS inventory_summary
                     ON inventory_summary.item_id = item.id
         WHERE (item.category_id =:categoryIdInv
           OR item.category_id =:categoryIdFxd)
           AND item.is_individual_entity = false
           AND (coalesce(inventory_summary.quantity,0)) > 0
           AND item.company_id=:companyId
        """

    /**
     * get itemStock list of all inventory of which ItemCategory is Inventory OR FixedAsset
     * according to new business logic all approved+unapproved quantity should be shown
     *
     * @return -a map contains itemStockList and count
     */
    private Map getItemStockList() {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()

        SystemEntity itemInvSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.INVENTORY, companyId)
        SystemEntity itemFxdSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.FIXED_ASSET, companyId)

        String queryStr = """
              SELECT item.id,  item.name,
              (to_char(coalesce(inventory_summary.quantity,0),'${Tools.DB_QUANTITY_FORMAT}') || ' ' ||item.unit) AS item_quantity
                    FROM item
              LEFT JOIN (SELECT SUM(iits.consumeable_stock) AS quantity, item_id
                    FROM vw_inv_inventory_consumable_stock iits GROUP BY item_id) AS inventory_summary
                         ON inventory_summary.item_id = item.id
            WHERE (item.category_id =:categoryIdInv
               OR item.category_id =:categoryIdFxd)
               AND item.is_individual_entity = false
               AND (coalesce(inventory_summary.quantity,0)) > 0
               AND item.company_id =:companyId
                ORDER BY ${sortColumn} ${sortOrder}
                  LIMIT :resultPerPage OFFSET :start
        """

        Map queryParams = [
                categoryIdInv: itemInvSysEntityObject.id,
                categoryIdFxd: itemFxdSysEntityObject.id,
                companyId: companyId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> itemStockList = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(COUNT_QUERY, queryParams)
        int count = countResults[0].count
        return [itemStockList: itemStockList, count: count]
    }
}

