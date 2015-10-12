package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Child class to show UI of approved inventory item(s) for consumption
 *  For details go through Use-Case doc named 'ShowForApprovedInventoryConsumptionDetailsActionService'
 */
class ShowForApprovedInventoryConsumptionDetailsActionService extends BaseService implements ActionIntf {

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService

    private static final String FAILURE_MSG = "Fail to load Item(s) of Inventory-Consumption Transaction."
    private static final String INV_TRANSACTION_MAP = "inventoryTransactionMap"
    private static final String INV_CONSUMPTION_NOT_FOUND = "Inventory-Consumption Transaction not found"
    private static final String INV_TRANSACTION_OBJ = "inventoryTransaction"
    private static final String COUNT = "count"
    private static final String INV_TRANSACTION_DETAILS_LIST = "inventoryTransactionDetailsList"
    private static final String SORT_NAME = "iitd.transaction_date, iitd.id"
    private static final String SORT_ORDER = "asc"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * @Params parameters -N/A
     * @Params obj -N/A
     *
     * @return -a map containing all objects necessary for execute
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long inventoryTransactionId = Long.parseLong(parameterMap.inventoryTransactionId.toString())
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(inventoryTransactionId)

            if (!invInventoryTransaction) {
                result.put(Tools.MESSAGE, INV_CONSUMPTION_NOT_FOUND)
                return result
            }

            result.put(INV_TRANSACTION_OBJ, invInventoryTransaction)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * method to get approvedConsumptionList and wrap list for grid
     *
     * @param parameters -serialized parameter sent from UI
     * @param obj -receives from executePreCondition
     *
     * @return -a map containing all objects necessary for show page (WrappedApprovedConsumptionList)
     *          inventoryTransactionMap to show basic information on level
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.rp) {
                parameterMap.sortname = SORT_NAME
                parameterMap.sortorder = SORT_ORDER
            }
            int count = 0
            List inventoryTransactionDetailsListWrap = []
            initPager(parameters)
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) preResult.get(INV_TRANSACTION_OBJ)
            Map inventoryTransactionMap = buildInventoryOutDetailsMap(invInventoryTransaction)
            LinkedHashMap serviceReturn = (LinkedHashMap) listForApprovedInvConsumptionDetailsByInvTransId(invInventoryTransaction.id)
            List<GroovyRowResult> inventoryTransactionDetailsList = (List<GroovyRowResult>) serviceReturn.invTransDetailsList
            count = (int) serviceReturn.count
            inventoryTransactionDetailsListWrap = (List) wrapInventoryTransactionDetailsList(inventoryTransactionDetailsList, this.start)
            result.put(INV_TRANSACTION_MAP, inventoryTransactionMap)
            result.put(INV_TRANSACTION_DETAILS_LIST, inventoryTransactionDetailsListWrap)
            result.put(COUNT, count)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Wrap list of approvedConsumptionDetailsList for gird
     *
     * @param invInventoryTransactionDetailsList -list of approvedConsumptionDetails object(s)
     * @param start -starting index of the page
     *
     * @return -list of wrappedApprovedConsumptionDetails
     */
    private List wrapInventoryTransactionDetailsList(List<GroovyRowResult> invInventoryTransactionDetailsList, int start) {
        List inventoryTransactions = [] as List
        int counter = start + 1
        GridEntity obj
        for (int i = 0; i < invInventoryTransactionDetailsList.size(); i++) {
            GroovyRowResult singleRow = invInventoryTransactionDetailsList[i]
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

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap approvedProductionDetailsList for grid
     *
     * @param obj -map returned from execute method
     *
     * @return -a map containing all objects necessary for show page (wrappedApprovedConsumptionDetails)
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(COUNT)
            List inventoryTransactionDetailsList = (List) receiveResult.get(INV_TRANSACTION_DETAILS_LIST)
            Map inventoryTransactionDetailsListMap = [page: pageNumber, total: count, rows: inventoryTransactionDetailsList]

            result.put(INV_TRANSACTION_DETAILS_LIST, inventoryTransactionDetailsListMap)
            result.put(INV_TRANSACTION_MAP, receiveResult.get(INV_TRANSACTION_MAP))
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
     * @param invInventoryTransaction -parent object of consumption
     * @return -a map containing information to show on level and list of consumable item(s) for drop-down
     */
    private Map buildInventoryOutDetailsMap(InvInventoryTransaction invInventoryTransaction) {
        InvInventory invInventory = (InvInventory) invInventoryCacheUtility.read(invInventoryTransaction.inventoryId)
        List<GroovyRowResult> itemList = getItemListForConsumption(invInventoryTransaction.inventoryId, invInventoryTransaction.budgetId)
        List newItemList = buildItemList(itemList)
        Object budget = budgetImplService.readBudget(invInventoryTransaction.budgetId)
        return [itemList: newItemList,
                inventory: invInventory,
                inventoryTransaction: invInventoryTransaction,
                budgetItem: budget.budgetItem
        ]
    }

    /**
     * @param itemList -consumable item list
     * @return -a map containing consumable item list
     */
    private List buildItemList(List<GroovyRowResult> itemList) {
        List newItemList = []
        Map newItem
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList[i].quantity > 0) {
                newItem = [
                        id: itemList[i].id,
                        name: itemList[i].name,
                        unit: itemList[i].unit,
                        quantity: itemList[i].quantity,
                        isConsumedAgainstFixedAsset: itemList[i].is_consumed_against_fixed_asset
                ]
                newItemList << newItem
            }
        }
        return Tools.listForKendoDropdown(newItemList,null,null)
    }

    private static final String QUERY_COUNT = """
            SELECT count(iitd.id) AS count
                FROM inv_inventory_transaction_details iitd
            WHERE inventory_transaction_id= :inventoryTransactionId
              AND iitd.approved_by > 0
              AND iitd.is_current = true
    """
    /**
     * Get list of approvedInventoryConsumptionDetails for grid
     * @param inventoryTransactionId -InvInventoryTransaction.id (parentId)
     * @return - a map containing list of GroovyRowResult(ApprovedConsumptionDetailsInfo) &
     *              count(Total number of-ApprovedConsumptionDetails)
     */
    private LinkedHashMap listForApprovedInvConsumptionDetailsByInvTransId(long inventoryTransactionId) {
        String query = """
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
                        ORDER BY iitd.transaction_date, iitd.id DESC
                        LIMIT :resultPerPage  OFFSET :start
                        """
        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> result = executeSelectSql(query, queryParams)
        List resultCount = executeSelectSql(QUERY_COUNT, queryParams)
        int count = resultCount[0].count
        return [invTransDetailsList: result, count: count]
    }

    private static final String LST_CONSUM_QUERY = """
            SELECT
                vcs.item_id as id, item.name, item.unit, vcs.consumeable_stock as quantity,
                budget_details.is_consumed_against_fixed_asset
            FROM vw_inv_inventory_consumable_stock vcs
                LEFT JOIN budg_budget_details budget_details ON vcs.item_id = budget_details.item_id
                LEFT JOIN item ON item.id = vcs.item_id
            WHERE vcs.inventory_id=:inventoryId
                AND budget_details.budget_id=:budgetId
                AND vcs.consumeable_stock > 0
                AND item.is_individual_entity = false
                ORDER BY item.name
    """
    /**
     * Get List of Consumable stock of available item from view(vw_inv_inventory_consumable_stock)
     *
     * @param inventoryId -Inventory.id
     * @param budgetId -Budget.id
     *
     * @return - a map containing list of GroovyRowResult(itemList)
     */
    private List<GroovyRowResult> getItemListForConsumption(long inventoryId, long budgetId) {
        Map queryParams = [
                inventoryId: inventoryId,
                budgetId: budgetId
        ]
        List<GroovyRowResult> materialList = executeSelectSql(LST_CONSUM_QUERY, queryParams)
        return materialList
    }
}