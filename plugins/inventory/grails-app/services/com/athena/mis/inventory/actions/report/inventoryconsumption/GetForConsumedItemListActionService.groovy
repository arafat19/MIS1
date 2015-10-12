package com.athena.mis.inventory.actions.report.inventoryconsumption

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Get list of consumed items with quantity for item consumption report
 *  For details go through Use-Case doc named 'GetForConsumedItemListActionService'
 */
class GetForConsumedItemListActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private static final String LST_ITEM = "lstItem"
    private static final String EXCEPTION_MESSAGE = "Could not get the consumption list of project"
    private static final String DEFAULT_ERROR_MESSAGE = "Fail to load the consumption list of project"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get item list for grid of specific budget line item
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initPager(parameterMap)    // initialize parameters for flexGrid
            long budgetId = Long.parseLong(parameterMap.budgetId)
            long inventoryTypeId = Long.parseLong(parameterMap.inventoryTypeId.toString())
            long inventoryId = Long.parseLong(parameterMap.inventoryId)
            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate)
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate)
            List<Long> lstInventoryIds = []
            // get list of inventory by typeId(store/site) mapped with user
            if (inventoryId <= 0) {
                lstInventoryIds = invSessionUtil.getUserInventoryIdsByType(inventoryTypeId)
            } else {
                lstInventoryIds << new Long(inventoryId)
            }
            // get list of consumed items of a budget line item
            List<GroovyRowResult> lstItem = getConsumedItemListByBudgetAndInventory(budgetId, lstInventoryIds, fromDate, toDate)
            result.put(LST_ITEM, lstItem)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap item list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj    // cast map returned from execute method
            List<GroovyRowResult> lstItem = (List<GroovyRowResult>) executeResult.get(LST_ITEM)
            // wrap item list in grid entity
            List lstWrappedItem = wrapItemList(lstItem, start)
            Map gridOutput = [page: pageNumber, total: lstWrappedItem.size(), rows: lstWrappedItem]
            result.put(LST_ITEM, gridOutput)
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
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap previousResult = (LinkedHashMap) obj    // cast map returned from previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
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
     * Wrap list of items in grid entity
     * @param lstItem -list of items
     * @param start -starting index of the page
     * @return -list of wrapped items
     */
    private List wrapItemList(List<GroovyRowResult> lstItem, int start) {
        List lstWrappedItem = []
        int counter = start + 1
        GridEntity obj
        GroovyRowResult singleRow
        for (int i = 0; i < lstItem.size(); i++) {
            singleRow = lstItem[i]
            obj = new GridEntity()
            obj.id = counter
            obj.cell = [
                    counter,
                    singleRow.item_name,
                    singleRow.quantity,
                    singleRow.amount,
                    singleRow.unapproved_quantity,
                    singleRow.budget_quantity
            ]
            lstWrappedItem << obj
            counter++
        }
        return lstWrappedItem
    }

    /**
     * Get list of consumed items with quantity
     * @param budgetId -id of budget
     * @param lstInventoryId -list of inventory ids
     * @param fromDate -start date
     * @param toDate -end date
     * @return -a list of consumed items
     */
    private List<GroovyRowResult> getConsumedItemListByBudgetAndInventory(long budgetId, List<Long> lstInventoryId, Date fromDate, Date toDate) {
        String inventoryIds = Tools.buildCommaSeparatedStringOfIds(lstInventoryId)
        // pull transactionType object
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        // query for list
        String queryStr = """SELECT item.name AS item_name,to_char(coalesce(approved_quantity.quantity,0),'${Tools.DB_QUANTITY_FORMAT}') ||' '||item.unit AS quantity,
	            (to_char(coalesce(approved_quantity.amount,0),'${Tools.DB_CURRENCY_FORMAT}')) AS amount,
	            to_char(budget_details.quantity,'${Tools.DB_QUANTITY_FORMAT}') ||' '||item.unit AS budget_quantity,
                to_char(coalesce(unapproved_quantity.quantity,0),'${Tools.DB_QUANTITY_FORMAT}') ||' '||item.unit AS unapproved_quantity
                FROM inv_inventory_transaction_details iitd
                LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
                LEFT JOIN item ON item.id = iitd.item_id
                LEFT JOIN budg_budget_details budget_details ON (budget_details.budget_id=iit.budget_id AND budget_details.item_id = iitd.item_id)

                LEFT JOIN
                (
                    SELECT SUM(actual_quantity) AS quantity, SUM(actual_quantity*rate) AS amount, item_id
                    FROM vw_inv_inventory_transaction_with_details
                    WHERE approved_by > 0
                    AND budget_id =:budgetId
                    AND inventory_id IN (${inventoryIds})
                    AND transaction_type_id =:transactionTypeId
                    AND transaction_date BETWEEN :fromDate AND :toDate
                    AND is_current = true
                    GROUP BY item_id
                ) AS approved_quantity
                ON approved_quantity.item_id = iitd.item_id

                LEFT JOIN
                (
                    SELECT SUM(actual_quantity) AS quantity, item_id
                    FROM vw_inv_inventory_transaction_with_details
                    WHERE approved_by = 0
                    AND budget_id =:budgetId
                    AND inventory_id IN (${inventoryIds})
                    AND transaction_type_id =:transactionTypeId
                    AND transaction_date BETWEEN :fromDate AND :toDate
                    AND is_current = true
                    GROUP BY item_id
                ) AS unapproved_quantity
                ON unapproved_quantity.item_id = iitd.item_id

                WHERE iit.budget_id =:budgetId AND iit.inventory_id IN (${inventoryIds})
                AND iit.transaction_type_id =:transactionTypeId
                AND iitd.transaction_date BETWEEN :fromDateWithSecond AND :toDateWithSecond
                AND iitd.is_current  = true
                GROUP BY iitd.item_id, item.name, budget_details.quantity,item.unit,approved_quantity.quantity,approved_quantity.amount,unapproved_quantity.quantity
                ORDER BY item.name
        """
        Map queryParams = [
                budgetId: budgetId,
                fromDate: DateUtility.getSqlDate(fromDate),
                toDate: DateUtility.getSqlDate(toDate),
                fromDateWithSecond: DateUtility.getSqlDateWithSeconds(fromDate),
                toDateWithSecond: DateUtility.getSqlDateWithSeconds(toDate),
                transactionTypeId: transactionTypeCons.id
        ]
        List<GroovyRowResult> itemList = executeSelectSql(queryStr, queryParams)
        return itemList
    }
}
