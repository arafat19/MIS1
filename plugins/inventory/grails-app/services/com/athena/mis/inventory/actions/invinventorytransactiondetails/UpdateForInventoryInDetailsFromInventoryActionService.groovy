package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update inventory transaction details(Inventory In from Inventory) object and grid data
 *  For details go through Use-Case doc named 'UpdateForInventoryInDetailsFromInventoryActionService'
 */
class UpdateForInventoryInDetailsFromInventoryActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String UPDATE_INVENTORY_TRANSACTION_SUCCESS_MESSAGE = "Inventory-In details has been updated successfully"
    private static final String UPDATE_INVENTORY_TRANSACTION_FAILURE_MESSAGE = "Inventory-In details could not be updated, please try again"
    private static final String INVENTORY_TRANSACTION_DETAILS_OBJ = "inventoryTransactionDetails"
    private static final String LST_ITEM = "lstItem"
    private static final String STOCK_QUANTITY_MESSAGE = "Not sufficient stock to update"
    private static final String APPROVED_INV_TRANSACTION_UPDATE_PROHIBITED = "Approved inventory transaction can not be updated"
    private static final String ACTUAL_QTY_BIGGER = "Actual Quantity can't be bigger than Inventory out Quantity"

    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * Check pre condition and build inventory transaction details(Inventory In From Inventory) object with parameters from UI for update
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long inDetailsId = Long.parseLong(params.id.toString())
            int version = Integer.parseInt(params.version.toString())
            // get inventory transaction details(Inventory In From Inventory) object
            InvInventoryTransactionDetails oldInvInTransactionDetails = invInventoryTransactionDetailsService.read(inDetailsId)
            // check if inventory transaction details object exists or not
            if (!oldInvInTransactionDetails || (oldInvInTransactionDetails.version != version)) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            // check if inventory transaction details is approved or not
            if (oldInvInTransactionDetails.approvedBy > 0) {
                result.put(Tools.MESSAGE, APPROVED_INV_TRANSACTION_UPDATE_PROHIBITED)
                return result
            }
            // build inventory transaction details(Inventory In From Inventory) object
            InvInventoryTransactionDetails newInvInTransactionDetails = buildInvTransactionDetails(params, oldInvInTransactionDetails)
            InvInventoryTransactionDetails invOutTransactionDetails = invInventoryTransactionDetailsService.read(newInvInTransactionDetails.transactionDetailsId)

            // checking if IN quantity is bigger than OUT quantity
            if (newInvInTransactionDetails.actualQuantity > invOutTransactionDetails.actualQuantity) {
                result.put(Tools.MESSAGE, ACTUAL_QTY_BIGGER)
                return result
            }

            // if New Quantity is less than Old Quantity :-> check available stock
            if (newInvInTransactionDetails.actualQuantity < oldInvInTransactionDetails.actualQuantity) {
                double availableQuantity = getConsumableStock(newInvInTransactionDetails.inventoryId, newInvInTransactionDetails.itemId)
                availableQuantity = availableQuantity - oldInvInTransactionDetails.actualQuantity + newInvInTransactionDetails.actualQuantity
                if (availableQuantity < 0) {
                    result.put(Tools.MESSAGE, STOCK_QUANTITY_MESSAGE)
                    return result
                }
            }

            result.put(INVENTORY_TRANSACTION_DETAILS_OBJ, newInvInTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_INVENTORY_TRANSACTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update inventory transaction details(Inventory In From Inventory) object in DB
     * @param params -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method

            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_TRANSACTION_DETAILS_OBJ)
            // update inventory transaction details(Inventory In From Inventory) object
            invInventoryTransactionDetailsService.update(invInventoryTransactionDetails)
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(invInventoryTransactionDetails.inventoryTransactionId)
            // get unacknowledged item list of inventory transaction details out object
            List<GroovyRowResult> itemList = getItemListOfInventoryOut(invInventoryTransaction.transactionId)

            result.put(INVENTORY_TRANSACTION_DETAILS_OBJ, invInventoryTransactionDetails)
            result.put(LST_ITEM, itemList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_INVENTORY_TRANSACTION_FAILURE_MESSAGE)
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
     * Show updated inventory transaction details(Inventory In From Inventory) object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method

            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) executeResult.get(INVENTORY_TRANSACTION_DETAILS_OBJ)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(invInventoryTransactionDetails.createdBy)
            AppUser updatedBy = (AppUser) appUserCacheUtility.read(invInventoryTransactionDetails.updatedBy)
            Item item = (Item) itemCacheUtility.read(invInventoryTransactionDetails.itemId)
            GridEntity object = new GridEntity()    // build grid entity object
            object.id = invInventoryTransactionDetails.id
            object.cell = [
                    Tools.LABEL_NEW,
                    invInventoryTransactionDetails.id,
                    item.name,
                    Tools.formatAmountWithoutCurrency(invInventoryTransactionDetails.suppliedQuantity) + Tools.SINGLE_SPACE + item.unit,
                    Tools.formatAmountWithoutCurrency(invInventoryTransactionDetails.actualQuantity) + Tools.SINGLE_SPACE + item.unit,
                    Tools.formatAmountWithoutCurrency(invInventoryTransactionDetails.shrinkage) + Tools.SINGLE_SPACE + item.unit,
                    createdBy.username,
                    updatedBy.username
            ]

            result.put(Tools.ENTITY, object)
            result.put(LST_ITEM, Tools.listForKendoDropdown(executeResult.get(LST_ITEM),null,null))
            result.put(Tools.MESSAGE, UPDATE_INVENTORY_TRANSACTION_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_INVENTORY_TRANSACTION_FAILURE_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, UPDATE_INVENTORY_TRANSACTION_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_INVENTORY_TRANSACTION_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build inventory transaction details(Inventory In From Inventory) object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldInvInventoryTransactionDetails -old inventory transaction details(Inventory In From Inventory) object
     * @return -updated inventory transaction details(Inventory In From Inventory) object
     */
    private InvInventoryTransactionDetails buildInvTransactionDetails(GrailsParameterMap parameterMap, InvInventoryTransactionDetails oldInvInventoryTransactionDetails) {

        AppUser user = invSessionUtil.appSessionUtil.getAppUser()
        double actualQuantity = Double.parseDouble(parameterMap.actualQuantity)

        InvInventoryTransactionDetails invInventoryTransactionDetails = new InvInventoryTransactionDetails()

        invInventoryTransactionDetails.id = oldInvInventoryTransactionDetails.id
        invInventoryTransactionDetails.version = Integer.parseInt(parameterMap.version.toString())
        invInventoryTransactionDetails.acknowledgedBy = oldInvInventoryTransactionDetails.acknowledgedBy
        invInventoryTransactionDetails.actualQuantity = actualQuantity
        invInventoryTransactionDetails.approvedBy = oldInvInventoryTransactionDetails.approvedBy
        invInventoryTransactionDetails.comments = parameterMap.comments ? parameterMap.comments : null
        invInventoryTransactionDetails.createdBy = oldInvInventoryTransactionDetails.createdBy
        invInventoryTransactionDetails.createdOn = oldInvInventoryTransactionDetails.createdOn
        invInventoryTransactionDetails.fifoQuantity = oldInvInventoryTransactionDetails.fifoQuantity
        invInventoryTransactionDetails.inventoryId = oldInvInventoryTransactionDetails.inventoryId
        invInventoryTransactionDetails.inventoryTransactionId = oldInvInventoryTransactionDetails.inventoryTransactionId
        invInventoryTransactionDetails.inventoryTypeId = oldInvInventoryTransactionDetails.inventoryTypeId
        invInventoryTransactionDetails.lifoQuantity = oldInvInventoryTransactionDetails.lifoQuantity
        invInventoryTransactionDetails.itemId = oldInvInventoryTransactionDetails.itemId
        invInventoryTransactionDetails.mrfNo = oldInvInventoryTransactionDetails.mrfNo
        invInventoryTransactionDetails.rate = oldInvInventoryTransactionDetails.rate
        invInventoryTransactionDetails.shrinkage = oldInvInventoryTransactionDetails.suppliedQuantity - actualQuantity
        invInventoryTransactionDetails.stackMeasurement = oldInvInventoryTransactionDetails.stackMeasurement
        invInventoryTransactionDetails.suppliedQuantity = oldInvInventoryTransactionDetails.suppliedQuantity
        invInventoryTransactionDetails.supplierChalan = oldInvInventoryTransactionDetails.supplierChalan
        invInventoryTransactionDetails.transactionDate = oldInvInventoryTransactionDetails.transactionDate
        invInventoryTransactionDetails.transactionDetailsId = oldInvInventoryTransactionDetails.transactionDetailsId
        invInventoryTransactionDetails.updatedBy = user.id
        invInventoryTransactionDetails.updatedOn = new Date()
        invInventoryTransactionDetails.vehicleId = oldInvInventoryTransactionDetails.vehicleId
        invInventoryTransactionDetails.vehicleNumber = oldInvInventoryTransactionDetails.vehicleNumber

        invInventoryTransactionDetails.adjustmentParentId = oldInvInventoryTransactionDetails.adjustmentParentId
        invInventoryTransactionDetails.approvedOn = oldInvInventoryTransactionDetails.approvedOn
        invInventoryTransactionDetails.isIncrease = oldInvInventoryTransactionDetails.isIncrease
        invInventoryTransactionDetails.transactionTypeId = oldInvInventoryTransactionDetails.transactionTypeId
        invInventoryTransactionDetails.isCurrent = oldInvInventoryTransactionDetails.isCurrent
        invInventoryTransactionDetails.fixedAssetId = oldInvInventoryTransactionDetails.fixedAssetId
        invInventoryTransactionDetails.fixedAssetDetailsId = oldInvInventoryTransactionDetails.fixedAssetDetailsId
        invInventoryTransactionDetails.overheadCost = oldInvInventoryTransactionDetails.overheadCost

        return invInventoryTransactionDetails
    }

    private static final String CONSUME_STOCK_QUERY = """
        SELECT consumeable_stock
        FROM vw_inv_inventory_consumable_stock
        WHERE inventory_id=:inventoryId
            AND item_id=:itemId
    """

    /**
     * Get consumable stock
     * @param inventoryId -id of inventory
     * @param itemId -id of item
     * @return -a double variable containing the value of stock
     */
    private double getConsumableStock(long inventoryId, long itemId) {
        Map queryParams = [inventoryId: inventoryId, itemId: itemId]
        List<GroovyRowResult> result = executeSelectSql(CONSUME_STOCK_QUERY, queryParams)
        if (result.size() > 0) {
            return (double) result[0].consumeable_stock
        }
        return 0.0d
    }

    private static final String LST_INV_OUT_QUERY = """
                SELECT iitd.id, iitd.version, (item.name ||'( ' ||iitd.actual_quantity || ' ' || item.unit || ')') AS name,
                iitd.actual_quantity AS quantity, item.unit
                FROM inv_inventory_transaction_details iitd
                LEFT JOIN item ON item.id = iitd.item_id
                WHERE iitd.inventory_transaction_id=:inventoryTransactionId
                AND iitd.acknowledged_by <= 0
                AND iitd.is_current = true
                AND item.is_individual_entity = false
                ORDER BY item.name ASC
    """

    /**
     * Get item list of inventory out transaction details
     * @param inventoryTransactionId -id of inventory transaction out object
     * @return -list of unacknowledged item of inventory transaction out
     */
    private List<GroovyRowResult> getItemListOfInventoryOut(long inventoryTransactionId) {
        Map queryParams = [inventoryTransactionId: inventoryTransactionId]
        List<GroovyRowResult> itemList = executeSelectSql(LST_INV_OUT_QUERY, queryParams)
        return itemList
    }
}