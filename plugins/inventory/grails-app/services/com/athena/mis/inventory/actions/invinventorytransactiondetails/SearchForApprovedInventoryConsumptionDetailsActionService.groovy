package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get search result list of approved consumed item list to show on grid
 *  For details go through Use-Case doc named 'SearchForApprovedInventoryConsumptionDetailsActionService'
 */
class SearchForApprovedInventoryConsumptionDetailsActionService extends BaseService implements ActionIntf {

    private static final String FAILURE_MSG = "Fail to load Inventory-Consumption material list"
    private static final String COUNT = "count"
    private static final String INV_TRANSACTION_DETAILS_LIST = "inventoryTransactionDetailsList"
    private static final String SORT_NAME = "iitd.approved_by"
    private static final String SORT_ORDER = "asc"
    private static final String INV_DETAILS_ID = "iitd.id"
    private static final String ACTUAL_QUANTITY = "iitd.actual_quantity"
    private static final String INVALID_INPUT = "Unable to search due to invalid input"

    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService

    private final Logger log = Logger.getLogger(getClass())

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if (!parameterMap.rp) {
                parameterMap.sortname = SORT_NAME
                parameterMap.sortorder = SORT_ORDER
            }
            long inventoryTransactionId = Long.parseLong(parameterMap.inventoryTransactionId.toString())
            int count = 0
            List inventoryTransactionDetailsListWrap = []
            List<GroovyRowResult> inventoryTransactionDetailsList = []
            initSearch(parameterMap)

            LinkedHashMap serviceReturn
            if (inventoryTransactionId > 0) {
                if (queryType == INV_DETAILS_ID || queryType == ACTUAL_QUANTITY) {
                    try {    // validation input
                        double tmp = Double.parseDouble(query.toString())
                    } catch (Exception ex) {
                        result.put(Tools.MESSAGE, INVALID_INPUT)
                        return result
                    }
                    //get search(by InvTranDetailsId OR ActualQuantity) list of approved consumed item
                    serviceReturn = searchForApprovedInvConsumptionByInvTransIdAndNumber(inventoryTransactionId)
                    inventoryTransactionDetailsList = (List<GroovyRowResult>) serviceReturn.invTransDetailsList
                    count = (int) serviceReturn.count
                } else {//searching by other than InvTranDetailsId OR ActualQuantity
                    serviceReturn = searchForApprovedInvConsumptionDetailsByInvTransId(inventoryTransactionId)
                    inventoryTransactionDetailsList = (List<GroovyRowResult>) serviceReturn.invTransDetailsList
                    count = (int) serviceReturn.count
                }
            }
            //wrap approved consumed item list for grid
            inventoryTransactionDetailsListWrap = (List) wrapInventoryTransactionDetailsList(inventoryTransactionDetailsList, start)

            result.put(INV_TRANSACTION_DETAILS_LIST, inventoryTransactionDetailsListWrap)
            result.put(COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap consumptionItemList for grid
     * @param obj -map returned from execute method
     * @return -a map containing WrappedApprovedConsumedItemList for grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(COUNT)
            List inventoryTransactionDetailsList = (List) receiveResult.get(INV_TRANSACTION_DETAILS_LIST)
            result = [page: pageNumber, total: count, rows: inventoryTransactionDetailsList]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * WrappedApprovedConsumedItemList for grid
     * @param invTranDetailsList -ApprovedConsumedItemList
     * @param start -start index
     * @return WrappedApprovedConsumedItemList
     */
    private List wrapInventoryTransactionDetailsList(List<GroovyRowResult> invTranDetailsList, int start) {
        List inventoryTransactions = []
        int counter = start + 1
        GridEntity obj
        for (int i = 0; i < invTranDetailsList.size(); i++) {
            GroovyRowResult singleRow = invTranDetailsList[i]
            obj = new GridEntity()
            obj.id = singleRow.id
            obj.cell = [counter,
                    singleRow.id,
                    singleRow.item_name,
                    singleRow.actual_quantity + Tools.SINGLE_SPACE + singleRow.unit,
                    singleRow.rate,
                    singleRow.transaction_date,
                    singleRow.approved_on,
                    singleRow.fixed_asset,
                    singleRow.fixed_asset_details,
                    singleRow.created_by,
                    singleRow.approved_by
            ]
            inventoryTransactions << obj
            counter++
        }
        return inventoryTransactions
    }

    /**
     * Get search(by InvTranDetailsId OR ActualQuantity) list of ApprovedInventoryConsumptionDetails for grid
     * @param inventoryTransactionId -InvInventoryTransaction.id (parentId)
     * @return - a map containing list of GroovyRowResult(ApprovedConsumptionDetailsInfo based on search) &
     *              count(Total number of-ApprovedConsumptionDetails based on search)
     */
    private LinkedHashMap searchForApprovedInvConsumptionByInvTransIdAndNumber(long inventoryTransactionId) {
        String queryStr = """
                        SELECT
                        iitd.id, item.name AS item_name, item.unit, iitd.rate,to_char(iitd.transaction_date,'dd-Mon-yyyy') as transaction_date,
                        to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') actual_quantity, to_char(iitd.approved_on,'dd-Mon-yyyy') as approved_on,
                        user_created_by.username as created_by, user_approved_by.username AS approved_by,
                        iitd.is_increase,iitd.transaction_type_id,
                        fixed_asset.name AS fixed_asset, fad.name AS fixed_asset_details
                        FROM inv_inventory_transaction_details iitd
                        LEFT JOIN item ON item.id = iitd.item_id
                        LEFT JOIN app_user user_approved_by ON user_approved_by.id = iitd.approved_by
                        LEFT JOIN app_user user_created_by ON user_created_by.id = iitd.created_by
                        LEFT JOIN system_entity transaction_type ON transaction_type.id = iitd.transaction_type_id
                        LEFT JOIN item fixed_asset ON fixed_asset.id = iitd.fixed_asset_id
                        LEFT JOIN fxd_fixed_asset_details fad ON fad.id = iitd.fixed_asset_details_id
                        WHERE inventory_transaction_id= :inventoryTransactionId
                          AND iitd.approved_by > 0
                          AND iitd.is_current = true
                          AND ${queryType} = ${query}
                        ORDER BY iitd.transaction_date, iitd.id DESC LIMIT :resultPerPage  OFFSET :start
                        """
        String queryCount = """
                        SELECT count(iitd.id) AS count
                        FROM inv_inventory_transaction_details iitd
                        LEFT JOIN item item ON item.id=iitd.item_id
                        WHERE inventory_transaction_id= :inventoryTransactionId
                          AND iitd.approved_by > 0
                          AND iitd.is_current = true
                          AND ${queryType} = ${query}
                        """

        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List resultCount = executeSelectSql(queryCount, queryParams)
        int count = resultCount[0].count
        return [invTransDetailsList: result, count: count]
    }

    /**
     * Get search(other than InvTranDetailsId OR ActualQuantity) list of approvedInventoryConsumptionDetails for grid
     * @param inventoryTransactionId -InvInventoryTransaction.id (parentId)
     * @return - a map containing list of GroovyRowResult(ApprovedConsumptionDetailsInfo based on search) &
     *              count(Total number of-ApprovedConsumptionDetails based on search)
     */
    private LinkedHashMap searchForApprovedInvConsumptionDetailsByInvTransId(long inventoryTransactionId) {
        String queryStr = """
                        SELECT
                        iitd.id, item.name AS item_name, item.unit, iitd.rate,to_char(iitd.transaction_date,'dd-Mon-yyyy') as transaction_date,
                        to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') AS actual_quantity,to_char(iitd.approved_on,'dd-Mon-yyyy') as approved_on,
                        user_created_by.username as created_by, user_approved_by.username AS approved_by,
                        iitd.is_increase,iitd.transaction_type_id, fixed_asset.name AS fixed_asset, fad.name AS fixed_asset_details
                        FROM inv_inventory_transaction_details iitd
                        LEFT JOIN item ON item.id = iitd.item_id
                        LEFT JOIN app_user user_approved_by ON user_approved_by.id = iitd.approved_by
                        LEFT JOIN app_user user_created_by ON user_created_by.id = iitd.created_by
                        LEFT JOIN system_entity transaction_type ON transaction_type.id = iitd.transaction_type_id
                        LEFT JOIN item fixed_asset ON fixed_asset.id = iitd.fixed_asset_id
                        LEFT JOIN fxd_fixed_asset_details fad ON fad.id = iitd.fixed_asset_details_id
                        WHERE inventory_transaction_id= :inventoryTransactionId
                          AND iitd.approved_by > 0
                          AND iitd.is_current = true
                          AND ${queryType} ILIKE :query
                        ORDER BY iitd.transaction_date DESC LIMIT :resultPerPage  OFFSET :start
                        """
        String queryCount = """
                        SELECT count(iitd.id) AS count
                        FROM inv_inventory_transaction_details iitd
                        LEFT JOIN item item ON item.id=iitd.item_id
                        WHERE inventory_transaction_id= :inventoryTransactionId
                          AND iitd.approved_by > 0
                          AND iitd.is_current = true
                          AND ${queryType} ILIKE :query
                        """

        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId,
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List resultCount = executeSelectSql(queryCount, queryParams)
        int count = resultCount[0].count
        return [invTransDetailsList: result, count: count]
    }
}

