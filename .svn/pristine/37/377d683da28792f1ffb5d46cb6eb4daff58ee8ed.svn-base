package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get list of approved consumed item list to show on grid
 *  For details go through Use-Case doc named 'ListForApprovedInventoryConsumptionDetailsActionService'
 */
class ListForApprovedInventoryConsumptionDetailsActionService extends BaseService implements ActionIntf {

    InvInventoryTransactionService invInventoryTransactionService

    private static final String FAILURE_MSG = "Fail to load Inventory-Consumption Material List"
    private static final String INV_CONSUMPTION_NOT_FOUND = "Inventory-Consumption Transaction not found"
    private static final String COUNT = "count"
    private static final String INV_TRANSACTION_DETAILS_LIST = "inventoryTransactionDetailsList"
    private static final String SORT_NAME = "iitd.approved_by"
    private static final String SORT_ORDER = "asc"

    private final Logger log = Logger.getLogger(getClass())

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     *  wrap approvedConsumedItemList for grid
     *
     * @Params params -Receives from UI
     * @Params obj -N/A
     *
     * @return -a map containing all objects necessary for grid data
     *          map contains isError(true/false) depending on method success
     */
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

            int count = 0
            List inventoryTransactionDetailsListWrap = []

            initPager(parameterMap)

            long inventoryTransactionId = Long.parseLong(parameterMap.inventoryTransactionId.toString())
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(inventoryTransactionId)
            if (!invInventoryTransaction) {  //check existence of inventoryTransaction (parent object)
                result.put(Tools.MESSAGE, INV_CONSUMPTION_NOT_FOUND)
                return result
            }

            if (inventoryTransactionId > 0) {  //get list of Approved consumed item list
                LinkedHashMap serviceReturn = (LinkedHashMap) listForApprovedInvConsumptionDetailsByInvTransId(inventoryTransactionId)
                List<GroovyRowResult> inventoryTransactionDetailsList = (List<GroovyRowResult>) serviceReturn.invTransDetailsList
                count = (int) serviceReturn.count
                //wrap Approved consumed item list for grid
                inventoryTransactionDetailsListWrap = (List) wrapInventoryTransactionDetailsList(inventoryTransactionDetailsList, start)
            }

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
     * @param invTranDetailsList -approvedConsumedItemList
     * @param start -start index
     * @return WrappedApprovedConsumedItemList
     */
    private List wrapInventoryTransactionDetailsList(List<GroovyRowResult> invTranDetailsList, int start) {
        List inventoryTransactions = [] as List
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

    private static final String COUNT_QUERY = """
        SELECT count(iitd.id) AS count
        FROM inv_inventory_transaction_details iitd
        WHERE inventory_transaction_id= :inventoryTransactionId
          AND iitd.approved_by > 0
          AND iitd.is_current = true
    """

    /**
     * Get list of ApprovedInventoryConsumptionDetails for grid
     * @param inventoryTransactionId -InvInventoryTransaction.id (parentId)
     * @return - a map containing list of GroovyRowResult(ApprovedConsumptionDetailsInfo) &
     *              count(Total number of-ApprovedConsumptionDetails)
     */
    private LinkedHashMap listForApprovedInvConsumptionDetailsByInvTransId(long inventoryTransactionId) {
        String queryStr = """
                        SELECT
                        iitd.id, item.name AS item_name, item.unit, iitd.rate,to_char(iitd.transaction_date,'dd-Mon-yyyy') as transaction_date,
                        to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') actual_quantity, to_char(iitd.approved_on,'dd-Mon-yyyy') as approved_on,
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
                        ORDER BY iitd.transaction_date, iitd.id DESC LIMIT :resultPerPage  OFFSET :start
        """
        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List resultCount = executeSelectSql(COUNT_QUERY, queryParams)
        int count = resultCount[0].count
        return [invTransDetailsList: result, count: count]
    }
}
