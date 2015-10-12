package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.integration.fixedasset.FixedAssetPluginConnector
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Common action class to select invTranConsumptionDetails object (Child) for both approved & unapproved and show in UI for editing
 *  For details go through Use-Case doc named 'SelectForInventoryConsumptionDetailsActionService'
 */
class SelectForInventoryConsumptionDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired(required = false)
    FixedAssetPluginConnector fixedAssetImplService
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService

    private static final String INV_CONSUMPTION_NOT_FOUND_MESSAGE = "Item of Inventory-Consumption Transaction not found"
    private static final String SERVER_ERROR_MESSAGE = "Can't get Item of Inventory-Consumption Transaction Details"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String INV_CONSUMPTION_DETAILS_OBJ = "invInventoryTransactionDetails"
    private static final String ITEM_LIST = "itemList"
    private static final String TRANSACTION_DATE = "transactionDate"
    private static final String LST_FIXED_ASSET_ITEMS = "lstFixedAssetItems"
    private static final String IS_CONSUMED_AGAINST_FIXED_ASSET = "isConsumedAgainstFixedAsset"
    private static final String LST_FIXED_ASSET_DETAILS_ITEMS = "lstFixedAssetDetailsItems"

    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get invTranConsumptionDetails object (Child) by id
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {// check for required params are present
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            long id = Long.parseLong(parameterMap.id.toString())
            InvInventoryTransactionDetails invInventoryTransactionDetails = invInventoryTransactionDetailsService.read(id)
            if (!invInventoryTransactionDetails) {//Check existence of object
                result.put(Tools.MESSAGE, INV_CONSUMPTION_NOT_FOUND_MESSAGE)
                return result
            }

            // Now pull the parent
            InvInventoryTransaction inventoryTransaction = invInventoryTransactionService.read(invInventoryTransactionDetails.inventoryTransactionId)
            long itemId = invInventoryTransactionDetails.fixedAssetId
            long fixedAssetDetailsId = invInventoryTransactionDetails.fixedAssetDetailsId

            // now check budgetDetails if isConsumedAgainstFixedAsset
            Object budgetDetails = budgetImplService.readBudgetDetailsByBudgetAndItem(inventoryTransaction.budgetId, invInventoryTransactionDetails.itemId)
            Boolean isConsumedAgainstFixedAsset = (Boolean) budgetDetails.isConsumedAgainstFixedAsset

            List<GroovyRowResult> lstFixedAssetItems = []
            List<GroovyRowResult> lstFixedAssetDetailsItems = []
            if (isConsumedAgainstFixedAsset.booleanValue()) {//if isConsumedAgainstFixedAsset=TRUE then pull fixedAssetList
                //get fixedAssetList of an inventory
                lstFixedAssetItems = fixedAssetImplService.getFixedAssetListByInvId(invInventoryTransactionDetails.inventoryId)
                if (fixedAssetDetailsId > 0) {
                    long inventoryId = invInventoryTransactionDetails.inventoryId
                    //get fixedAssetList by an item of an inventory
                    lstFixedAssetDetailsItems = fixedAssetImplService.getFixedAssetListByInvIdAndItemId(inventoryId, itemId)
                }
            }
            result.put(IS_CONSUMED_AGAINST_FIXED_ASSET, isConsumedAgainstFixedAsset)
            result.put(LST_FIXED_ASSET_ITEMS, lstFixedAssetItems)
            result.put(LST_FIXED_ASSET_DETAILS_ITEMS, lstFixedAssetDetailsItems)
            result.put(INV_CONSUMPTION_DETAILS_OBJ, invInventoryTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with invTranConsumptionDetails object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) receiveResult.get(INV_CONSUMPTION_DETAILS_OBJ)
            //get consumableStock of item in an inventory using view
            double consumableStock = getConsumableStock(invInventoryTransactionDetails.inventoryId, invInventoryTransactionDetails.itemId)
            double availableQuantity = consumableStock + invInventoryTransactionDetails.actualQuantity
            Item item = (Item) itemCacheUtility.read(invInventoryTransactionDetails.itemId)
            Map itemList = [id: item.id, name: item.name, unit: item.unit, quantity: availableQuantity.round(4)]
            List lstItems = [itemList]
            String transactionDate = DateUtility.getDateForUI(invInventoryTransactionDetails.transactionDate)

            result.put(ITEM_LIST, Tools.listForKendoDropdown(lstItems,null,null))
            result.put(Tools.ENTITY, invInventoryTransactionDetails)
            result.put(Tools.VERSION, invInventoryTransactionDetails.version)
            result.put(TRANSACTION_DATE, transactionDate)
            result.put(LST_FIXED_ASSET_ITEMS, receiveResult.get(LST_FIXED_ASSET_ITEMS))
            result.put(LST_FIXED_ASSET_DETAILS_ITEMS, receiveResult.get(LST_FIXED_ASSET_DETAILS_ITEMS))
            result.put(IS_CONSUMED_AGAINST_FIXED_ASSET, receiveResult.get(IS_CONSUMED_AGAINST_FIXED_ASSET))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, INV_CONSUMPTION_NOT_FOUND_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    private static final String CONSUME_STOCK_QUERY = """
        SELECT consumeable_stock
        FROM vw_inv_inventory_consumable_stock
        WHERE inventory_id=:inventoryId
          AND item_id=:itemId
    """
    /**
     * Method to get consumableStock of an item in an inventory using view(vw_inv_inventory_consumable_stock)
     * @param inventoryId -InvInventory.id
     * @param itemId -Item.id
     * @return -double value
     */
    private double getConsumableStock(long inventoryId, long itemId) {
        Map queryParams = [
                inventoryId: inventoryId,
                itemId: itemId
        ]
        List<GroovyRowResult> result = executeSelectSql(CONSUME_STOCK_QUERY, queryParams)
        if (result.size() > 0) {
            return (double) result[0].consumeable_stock
        }
        return 0.0d
    }
}
