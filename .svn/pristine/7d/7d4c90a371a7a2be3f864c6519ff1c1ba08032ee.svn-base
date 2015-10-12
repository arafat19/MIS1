package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.Vehicle
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.application.utility.VehicleCacheUtility
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update inventory transaction details(Inventory In from Supplier) object and grid data
 *  For details go through Use-Case doc named 'UpdateForInventoryInDetailsFromSupplierActionService'
 */
class UpdateForInventoryInDetailsFromSupplierActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String UPDATE_INVENTORY_TRANSACTION_SUCCESS_MESSAGE = "Inventory-In details has been updated successfully"
    private static final String UPDATE_INVENTORY_TRANSACTION_FAILURE_MESSAGE = "Inventory-In details could not be updated, please try again"
    private static final String INVENTORY_TRANSACTION_DETAILS_OBJ = "inventoryTransactionDetails"
    private static final String LST_ITEM = "lstItem"
    private static final String PURCHASE_REQUEST_DETAILS_OBJ = "purchaseRequestDetails"
    private static final String ACTUAL_QTY_BIGGER = "Actual quantity can't be bigger than supplied quantity"
    private static final String APPROVED_INV_TRANSACTION_UPDATE_PROHIBITED = "Approved inventory transaction cannot be edited"
    private static final String STOCK_QUANTITY_MESSAGE = "Not sufficient stock to inventory IN"

    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    VehicleCacheUtility vehicleCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * Check pre condition and build inventory transaction details(Inventory In From Supplier) object with parameters from UI for update
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            // check required parameters
            if (!params.supplierChalan) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long invTransactionDetailsId = Long.parseLong(params.id.toString())
            int version = Integer.parseInt(params.version.toString())
            // get inventory transaction details(Inventory In From Supplier) object
            InvInventoryTransactionDetails oldInvInventoryTransactionDetails = invInventoryTransactionDetailsService.read(invTransactionDetailsId)
            // check if inventory transaction details object exists or not
            if (!oldInvInventoryTransactionDetails || (oldInvInventoryTransactionDetails.version != version)) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            // check if inventory transaction details is approved or not
            if (oldInvInventoryTransactionDetails.approvedBy > 0) {
                result.put(Tools.MESSAGE, APPROVED_INV_TRANSACTION_UPDATE_PROHIBITED)
                return result
            }

            double suppliedQuantity = Double.parseDouble(params.suppliedQuantity.toString())
            double actualQuantity = Double.parseDouble(params.actualQuantity.toString())
            // checking if IN quantity is Bigger than supplied quantity
            if (actualQuantity > suppliedQuantity) {
                result.put(Tools.MESSAGE, ACTUAL_QTY_BIGGER)
                return result
            }
            // build inventory transaction details(Inventory In From Supplier) object
            InvInventoryTransactionDetails invInventoryTransactionDetails = buildInventoryTransaction(params, oldInvInventoryTransactionDetails, suppliedQuantity, actualQuantity)
            // check PO validation
            Object purchaseOrderDetails = procurementImplService.readPODetails(invInventoryTransactionDetails.transactionDetailsId)
            if (!purchaseOrderDetails) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            // if New Quantity is less than Old Quantity :-> check available stock
            if (invInventoryTransactionDetails.actualQuantity < oldInvInventoryTransactionDetails.actualQuantity) {
                double availableQuantity = getConsumableStock(invInventoryTransactionDetails.inventoryId, invInventoryTransactionDetails.itemId)
                availableQuantity = availableQuantity - oldInvInventoryTransactionDetails.actualQuantity + invInventoryTransactionDetails.actualQuantity
                if (availableQuantity < 0) {
                    result.put(Tools.MESSAGE, STOCK_QUANTITY_MESSAGE)
                    return result
                }
            }
            // set storeIn quantity of PO details
            double storeInQuantity = purchaseOrderDetails.storeInQuantity - oldInvInventoryTransactionDetails.actualQuantity
            purchaseOrderDetails.storeInQuantity = storeInQuantity + invInventoryTransactionDetails.actualQuantity

            result.put(INVENTORY_TRANSACTION_DETAILS_OBJ, invInventoryTransactionDetails)
            result.put(PURCHASE_REQUEST_DETAILS_OBJ, purchaseOrderDetails)
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
     * Update inventory transaction details(Inventory In From Supplier) object in DB
     * This method is in transactional boundary and will roll back in case of any exception
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
            Object purchaseOrderDetails = preResult.get(PURCHASE_REQUEST_DETAILS_OBJ)
            // update inventory transaction details(Inventory In From Supplier) object
            invInventoryTransactionDetailsService.update(invInventoryTransactionDetails)
            // update storeIn quantity of item in PO details
            Integer updatePODetails = (Integer) procurementImplService.updateStoreInQuantityForPODetails(purchaseOrderDetails)
            if (updatePODetails.intValue() <= 0) {
                throw new RuntimeException('Failed to update PO Details')
            }
            List<GroovyRowResult> lstItem
            // get list of item from PO details
            lstItem = procurementImplService.listPOItemByPurchaseOrder(purchaseOrderDetails.purchaseOrderId)
            result.put(INVENTORY_TRANSACTION_DETAILS_OBJ, invInventoryTransactionDetails)
            result.put(LST_ITEM, lstItem)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(UPDATE_INVENTORY_TRANSACTION_FAILURE_MESSAGE)
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
     * Show updated inventory transaction details(Inventory In From Supplier) object in grid
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

            Item item = (Item) itemCacheUtility.read(invInventoryTransactionDetails.itemId)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(invInventoryTransactionDetails.createdBy)
            AppUser updatedBy = (AppUser) appUserCacheUtility.read(invInventoryTransactionDetails.updatedBy)
            Vehicle vehicle = (Vehicle) vehicleCacheUtility.read(invInventoryTransactionDetails.vehicleId)
            String transactionDate = DateUtility.getLongDateForUI(invInventoryTransactionDetails.transactionDate)
            GridEntity object = new GridEntity()    // build grid entity object
            object.id = invInventoryTransactionDetails.id
            object.cell = [
                    Tools.LABEL_NEW,
                    invInventoryTransactionDetails.id,
                    item.name,
                    Tools.formatAmountWithoutCurrency(invInventoryTransactionDetails.suppliedQuantity) + Tools.SINGLE_SPACE + item.unit,
                    Tools.formatAmountWithoutCurrency(invInventoryTransactionDetails.actualQuantity) + Tools.SINGLE_SPACE + item.unit,
                    Tools.formatAmountWithoutCurrency(invInventoryTransactionDetails.shrinkage) + Tools.SINGLE_SPACE + item.unit,
                    transactionDate,
                    invInventoryTransactionDetails.supplierChalan,
                    vehicle.name,
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
     * Build inventory transaction details(Inventory In From Supplier) object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldInvInventoryTransactionDetails -old inventory transaction details(Inventory In From Supplier) object
     * @param suppliedQuantity -supplied quantity from supplier
     * @param actualQuantity -in quantity in inventory
     * @return -updated inventory transaction details(Inventory In From Supplier) object
     */
    private InvInventoryTransactionDetails buildInventoryTransaction(GrailsParameterMap parameterMap, InvInventoryTransactionDetails oldInvInventoryTransactionDetails, double suppliedQuantity, double actualQuantity) {

        AppUser user = invSessionUtil.appSessionUtil.getAppUser()

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
        invInventoryTransactionDetails.mrfNo = parameterMap.mrfNo ? parameterMap.mrfNo : null
        invInventoryTransactionDetails.rate = oldInvInventoryTransactionDetails.rate
        invInventoryTransactionDetails.shrinkage = suppliedQuantity - actualQuantity
        invInventoryTransactionDetails.stackMeasurement = parameterMap.stackMeasurement ? parameterMap.stackMeasurement : null
        invInventoryTransactionDetails.suppliedQuantity = suppliedQuantity
        invInventoryTransactionDetails.supplierChalan = parameterMap.supplierChalan ? parameterMap.supplierChalan : null
        invInventoryTransactionDetails.transactionDate = DateUtility.parseMaskedFromDate(parameterMap.transactionDate)
        invInventoryTransactionDetails.transactionDetailsId = oldInvInventoryTransactionDetails.transactionDetailsId
        invInventoryTransactionDetails.updatedBy = user.id
        invInventoryTransactionDetails.updatedOn = new Date()
        invInventoryTransactionDetails.vehicleId = Long.parseLong(parameterMap.vehicleId)
        invInventoryTransactionDetails.vehicleNumber = parameterMap.vehicleNumber ? parameterMap.vehicleNumber : null
        invInventoryTransactionDetails.fixedAssetId = oldInvInventoryTransactionDetails.fixedAssetId
        invInventoryTransactionDetails.fixedAssetDetailsId = oldInvInventoryTransactionDetails.fixedAssetDetailsId
        invInventoryTransactionDetails.transactionTypeId = oldInvInventoryTransactionDetails.transactionTypeId
        invInventoryTransactionDetails.adjustmentParentId = oldInvInventoryTransactionDetails.adjustmentParentId
        invInventoryTransactionDetails.approvedOn = null
        invInventoryTransactionDetails.isIncrease = oldInvInventoryTransactionDetails.isIncrease
        invInventoryTransactionDetails.isCurrent = oldInvInventoryTransactionDetails.isCurrent
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
}