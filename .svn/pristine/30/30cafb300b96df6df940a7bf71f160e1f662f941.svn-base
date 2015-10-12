package com.athena.mis.inventory.actions.report.itemstock

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


/**
 *  Get StockDetailsList of an item (in all inventory) for grid
 *  For details go through Use-Case doc named 'GetForStockDetailsListByItemIdActionService'
 */
class GetForStockDetailsListByItemIdActionService extends BaseService implements ActionIntf {

    @Autowired
    ItemCacheUtility itemCacheUtility

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to get Item Stock Details list."
    private static final String ITEM_STOCK_DETAILS_LIST = "itemStockDetailsList"
    private static final String ITEM_ID = "itemId"
    private static final String LABEL_TOTAL = "<b>Total</b>"
    private static final String BOLD_START = "<b>"
    private static final String BOLD_END = "</b>"

    protected final Logger log = Logger.getLogger(getClass())

    /**
     * check existence and validity of parameter
     * Get list of itemStock and wrap for grid
     * @param parameters -itemId send from UI
     * @param obj -N/A
     * @return -a map containing itemId and isError(True/False) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.itemId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

            long itemId
            try {
                itemId = Long.parseLong(params.itemId.toString())
            } catch (Exception ex) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

            result.put(ITEM_ID, itemId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * do nothing for executePostCondition operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get StockDetailsList of an item(in all inventory) and wrap for grid
     * @param parameters -N/A
     * @param obj -itemId send from executePreCondition method
     * @return -wrappedItemStockDetailsList for grid
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj

            long itemId = (long) preResult.get(ITEM_ID)

            //get itemStockDetails list
            List<GroovyRowResult> inventoryStockList = getInventoryStockListByItemId(itemId)

            //wrap stockDetailsList for grid
            List itemStockDetailsList = wrapItemStockDetails(inventoryStockList, itemId)
            result.put(ITEM_STOCK_DETAILS_LIST, itemStockDetailsList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * @param obj -map returned from execute method
     * @return -a map containing Wrapped ItemStockDetailsList for grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        Map gridOutput
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List itemStockDetailsList = (List) executeResult.get(ITEM_STOCK_DETAILS_LIST)
            gridOutput = [page: pageNumber, total: itemStockDetailsList.size(), rows: itemStockDetailsList]

            result.put(ITEM_STOCK_DETAILS_LIST, gridOutput)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            gridOutput = [page: pageNumber, total: 0, rows: []]
            result.put(ITEM_STOCK_DETAILS_LIST, gridOutput)
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
        Map gridOutput = [page: pageNumber, total: 0, rows: []]
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.message) {
                result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            result.put(ITEM_STOCK_DETAILS_LIST, gridOutput)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            result.put(ITEM_STOCK_DETAILS_LIST, gridOutput)
            return result
        }
    }

    /**
     * Wrapped-ItemStockDetailsList for grid
     * @param inventoryStockList -List of GroovyRowResult
     * @params itemId -item.id
     * @return -WrappedItemStockDetailsList
     */
    private List wrapItemStockDetails(List<GroovyRowResult> inventoryStockList, long itemId) {
        List itemStocks = [] as List
        Item item = (Item) itemCacheUtility.read(itemId)

        GroovyRowResult inventory
        GridEntity obj
        int counter = 1
        double total_inventory_quantity = 0
        double total_unapproved_quantity_plus = 0
        double total_unapproved_quantity_minus = 0

        int inventoryCount = inventoryStockList.size()
        if (inventoryCount > 0) {
            //row title for Inventory stock
            for (int i = 0; i < inventoryCount; i++) {
                obj = new GridEntity()
                inventory = inventoryStockList[i]
                obj.id = counter
                obj.cell = [counter, inventory.key, inventory.name,
                        Tools.formatAmountWithoutCurrency(inventory.inventory_quantity) + Tools.SINGLE_SPACE + item.unit,
                        Tools.formatAmountWithoutCurrency(inventory.unapproved_quantity_plus) + Tools.SINGLE_SPACE + item.unit,
                        Tools.formatAmountWithoutCurrency(inventory.unapproved_quantity_minus) + Tools.SINGLE_SPACE + item.unit
                ]
                itemStocks << obj
                counter++
                total_inventory_quantity = total_inventory_quantity + inventory.inventory_quantity
                total_unapproved_quantity_plus = total_unapproved_quantity_plus + inventory.unapproved_quantity_plus
                total_unapproved_quantity_minus = total_unapproved_quantity_minus + inventory.unapproved_quantity_minus
            }
            //add total inventory quantity
            obj = new GridEntity()
            obj.id = counter++
            obj.cell = [Tools.EMPTY_SPACE, Tools.EMPTY_SPACE, LABEL_TOTAL,
                    BOLD_START + Tools.formatAmountWithoutCurrency(total_inventory_quantity) + Tools.SINGLE_SPACE + item.unit + BOLD_END,
                    BOLD_START + Tools.formatAmountWithoutCurrency(total_unapproved_quantity_plus) + Tools.SINGLE_SPACE + item.unit + BOLD_END,
                    BOLD_START + Tools.formatAmountWithoutCurrency(total_unapproved_quantity_minus) + Tools.SINGLE_SPACE + item.unit + BOLD_END
            ]
            itemStocks << obj
        }
        //if inventory quantity available then add empty row
        if (inventoryCount > 0) {
            obj = new GridEntity()
            obj.id = counter++
            obj.cell = [Tools.EMPTY_SPACE, Tools.EMPTY_SPACE, Tools.EMPTY_SPACE, Tools.EMPTY_SPACE, Tools.EMPTY_SPACE, Tools.EMPTY_SPACE]
            itemStocks << obj
        }
        return itemStocks

    }

    private static final String SELECT_QUERY = """
                SELECT inv.id,inv.name,syst.key,
                coalesce(stock.inventory_quantity,0) AS inventory_quantity,
                coalesce(unapproved_quantity_plus.quantity,0) AS unapproved_quantity_plus,
                coalesce(unapproved_quantity_minus.quantity,0) AS unapproved_quantity_minus
                FROM inv_inventory inv
                    LEFT JOIN
                    (
                        SELECT SUM(available_stock) AS inventory_quantity ,inventory_id
                        from vw_inv_inventory_stock
                        WHERE item_id =:itemId
                        GROUP BY inventory_id
                    ) as stock
                    ON stock.inventory_id = inv.id

                    LEFT JOIN
                    (
                        SELECT SUM(iitd.actual_quantity) AS quantity, inventory_id
                        FROM inv_inventory_transaction_details iitd
                        WHERE is_increase=true and approved_by = 0  and item_id =:itemId
                        GROUP BY inventory_id
                    ) AS unapproved_quantity_plus
                    ON unapproved_quantity_plus.inventory_id = inv.id

                    LEFT JOIN
                    (
                        SELECT SUM(iitd.actual_quantity) AS quantity, inventory_id
                        FROM inv_inventory_transaction_details iitd
                        WHERE is_increase=false and approved_by = 0  and item_id =:itemId
                        GROUP BY inventory_id
                    ) AS unapproved_quantity_minus
                    ON unapproved_quantity_minus.inventory_id = inv.id

                LEFT JOIN system_entity syst ON syst.id = inv.type_id
                GROUP BY inv.id, inv.name, syst.key,stock.inventory_quantity ,unapproved_quantity_plus.quantity,unapproved_quantity_minus.quantity
                HAVING (stock.inventory_quantity >0) OR (unapproved_quantity_plus.quantity>0) OR(unapproved_quantity_minus.quantity>0)
                ORDER BY inv.name ASC
    """

    /**
     * to get stockDetailsList of an item (in all inventory)
     * @param itemId -item.id
     * @return -itemStockDetailsList(GroovyRawResult)
     */
    private List<GroovyRowResult> getInventoryStockListByItemId(long itemId) {
        Map queryParams = [
                itemId: itemId
        ]
        List<GroovyRowResult> inventoryStockList = executeSelectSql(SELECT_QUERY, queryParams)
        return inventoryStockList
    }
}

