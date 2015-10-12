package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.Vehicle
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.VehicleCacheUtility
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.inventory.config.InvSysConfigurationCacheUtility
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new inventory transaction details(Inventory In From Supplier) to receive item from supplier and show in grid
 *  For details go through Use-Case doc named 'CreateForInventoryInDetailsFromSupplierActionService'
 */
class CreateForInventoryInDetailsFromSupplierActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String INVENTORY_IN_SAVE_SUCCESS_MESSAGE = "Inventory-In from supplier has been saved successfully"
    private static final String INVENTORY_IN_SAVE_FAILURE_MESSAGE = "Can not saved Inventory-In from supplier"
    private static final String INVENTORY_IN_DETAILS_OBJ = "inventoryInDetails"
    private static final String INVENTORY_IN_OBJ = "inventoryTransactionDetails"
    private static final String PURCHASE_ORDER_DETAILS_OBJ = "purchaseOrderDetails"
    private static final String ACTUAL_QTY_BIGGER = "Actual quantity can't be bigger than supplied quantity"
    private static final String INV_INVENTORY_TRANSACTION_NOT_FOUND = "Inventory Transaction not found"
    private static final String PURCHASE_ORDER_DETAILS_NOT_FOUND = "No such item within the selected purchase order"
    private static final String LST_ITEM = "lstItem"

    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    InvInventoryTransactionService invInventoryTransactionService
    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvSysConfigurationCacheUtility invSysConfigurationCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    VehicleCacheUtility vehicleCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility

    /**
     * Checking pre condition and building inventory transaction details object with parameters from UI
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check required parameters
            if (!parameterMap.supplierChalan) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long inventoryTransactionId = Long.parseLong(parameterMap.inventoryTransactionId.toString())
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(inventoryTransactionId)
            // check if inventory transaction object exists or not
            if (!invInventoryTransaction) {
                result.put(Tools.MESSAGE, INV_INVENTORY_TRANSACTION_NOT_FOUND)
                return result
            }

            double suppliedQuantity = Double.parseDouble(parameterMap.suppliedQuantity.toString())
            double actualQuantity = Double.parseDouble(parameterMap.actualQuantity.toString())
            // checking if IN quantity is Bigger than supplied quantity
            if (actualQuantity > suppliedQuantity) {
                result.put(Tools.MESSAGE, ACTUAL_QTY_BIGGER)
                return result
            }
            // build inventory transaction details object(Inv In From Supplier)
            InvInventoryTransactionDetails invInventoryTransactionDetails = buildInvInventoryTransactionDetailsObject(parameterMap, invInventoryTransaction, suppliedQuantity, actualQuantity)

            InvInventory invInventoryObject = (InvInventory) invInventoryCacheUtility.read(invInventoryTransactionDetails.inventoryId)
            Project projectObject = (Project) projectCacheUtility.read(invInventoryObject.projectId)

            // check project with auto approval mapping
            if(projectObject.isApproveInFromSupplier){
                long userId = invSessionUtil.appSessionUtil.getAppUser().id
                invInventoryTransactionDetails.approvedBy = userId
                invInventoryTransactionDetails.approvedOn = new Date()
            }

            Object purchaseOrderDetails = procurementImplService.readPODetailsByPurchaseOrderAndItem(invInventoryTransaction.transactionId, invInventoryTransactionDetails.itemId)
            // check PO details validation
            if (!purchaseOrderDetails) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_DETAILS_NOT_FOUND)
                return result
            }

            invInventoryTransactionDetails.transactionDetailsId = purchaseOrderDetails.id
            // set storeIn quantity of PO details
            double quantity = purchaseOrderDetails.storeInQuantity + invInventoryTransactionDetails.actualQuantity
            purchaseOrderDetails.storeInQuantity = quantity
            invInventoryTransactionDetails.rate = purchaseOrderDetails.rate

            result.put(INVENTORY_IN_DETAILS_OBJ, invInventoryTransactionDetails)
            result.put(INVENTORY_IN_OBJ, invInventoryTransaction)
            result.put(PURCHASE_ORDER_DETAILS_OBJ, purchaseOrderDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)

            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INVENTORY_IN_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Create inventory transaction details object (Inventory In From Supplier)
     * Increase item count in inventory transaction object
     * Update storeIn quantity of item in PO details
     * This method is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_IN_DETAILS_OBJ)
            Object purchaseOrderDetailsInstance = preResult.get(PURCHASE_ORDER_DETAILS_OBJ)
            // create the inventoryIn transaction details object
            InvInventoryTransactionDetails newInvInventoryTransactionDetails = invInventoryTransactionDetailsService.create(invInventoryTransactionDetails)
            // increase item count in inventory transaction object(parent object)
            increaseItemCount(newInvInventoryTransactionDetails.inventoryTransactionId)
            // update storeIn quantity of item in PO details
            Integer updatePurchaseOrderDetails = (Integer) procurementImplService.updateStoreInQuantityForPODetails(purchaseOrderDetailsInstance)
            if (updatePurchaseOrderDetails.intValue() <= 0) {
                throw new RuntimeException('Failed to update PO Details')
            }
            List<GroovyRowResult> lstItem
            // get list of item from PO details
            lstItem = procurementImplService.listPOItemByPurchaseOrder(purchaseOrderDetailsInstance.purchaseOrderId)

            result.put(LST_ITEM, Tools.listForKendoDropdown(lstItem,null,null))
            result.put(INVENTORY_IN_DETAILS_OBJ, newInvInventoryTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(INVENTORY_IN_SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INVENTORY_IN_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show newly created inventory transaction details object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) executeResult.get(INVENTORY_IN_DETAILS_OBJ)
            Item item = (Item) itemCacheUtility.read(invInventoryTransactionDetails.itemId)
            AppUser user = (AppUser) appUserCacheUtility.read(invInventoryTransactionDetails.createdBy)
            Vehicle vehicle = (Vehicle) vehicleCacheUtility.read(invInventoryTransactionDetails.vehicleId)
            String transactionDate = DateUtility.getLongDateForUI(invInventoryTransactionDetails.transactionDate)
            GridEntity object = new GridEntity()
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
                    user.username,
                    Tools.EMPTY_SPACE
            ]

            result.put(Tools.MESSAGE, INVENTORY_IN_SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(LST_ITEM, executeResult.get(LST_ITEM))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INVENTORY_IN_SAVE_FAILURE_MESSAGE)
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
            LinkedHashMap previousResult = (LinkedHashMap) obj   // cast map returned from previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, INVENTORY_IN_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INVENTORY_IN_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build inventory transaction details object (Inventory In From Supplier)
     * @param parameterMap -serialized parameters from UI
     * @param invInventoryTransaction -inventory transaction object(parent object)
     * @param suppliedQuantity -supplied quantity from supplier
     * @param actualQuantity -in quantity in inventory
     * @return -new inventory transaction details object (Inventory In From Supplier)
     */
    private InvInventoryTransactionDetails buildInvInventoryTransactionDetailsObject(GrailsParameterMap parameterMap, InvInventoryTransaction invInventoryTransaction, double suppliedQuantity, double actualQuantity) {

        AppUser user = invSessionUtil.appSessionUtil.getAppUser()
        InvInventoryTransactionDetails invInventoryTransactionDetails = new InvInventoryTransactionDetails()

        invInventoryTransactionDetails.version = 0
        invInventoryTransactionDetails.acknowledgedBy = 0L
        invInventoryTransactionDetails.actualQuantity = actualQuantity
        invInventoryTransactionDetails.approvedBy = 0L
        invInventoryTransactionDetails.comments = parameterMap.comments ? parameterMap.comments : null
        invInventoryTransactionDetails.createdBy = user.id
        invInventoryTransactionDetails.createdOn = new Date()
        invInventoryTransactionDetails.fifoQuantity = 0.0d
        invInventoryTransactionDetails.inventoryId = invInventoryTransaction.inventoryId
        invInventoryTransactionDetails.inventoryTransactionId = invInventoryTransaction.id
        invInventoryTransactionDetails.inventoryTypeId = invInventoryTransaction.inventoryTypeId
        invInventoryTransactionDetails.lifoQuantity = 0.0d
        invInventoryTransactionDetails.itemId = Long.parseLong(parameterMap.itemId)
        invInventoryTransactionDetails.mrfNo = parameterMap.mrfNo ? parameterMap.mrfNo : null
        invInventoryTransactionDetails.rate = 0.0d
        invInventoryTransactionDetails.shrinkage = suppliedQuantity - actualQuantity
        invInventoryTransactionDetails.stackMeasurement = parameterMap.stackMeasurement ? parameterMap.stackMeasurement : null
        invInventoryTransactionDetails.suppliedQuantity = suppliedQuantity
        invInventoryTransactionDetails.supplierChalan = parameterMap.supplierChalan ? parameterMap.supplierChalan : null
        invInventoryTransactionDetails.transactionDate = DateUtility.parseMaskedFromDate(parameterMap.transactionDate)
        invInventoryTransactionDetails.fixedAssetId = 0L
        invInventoryTransactionDetails.fixedAssetDetailsId = 0L
        invInventoryTransactionDetails.transactionDetailsId = 0L
        invInventoryTransactionDetails.updatedBy = 0L
        invInventoryTransactionDetails.updatedOn = null
        invInventoryTransactionDetails.vehicleId = Long.parseLong(parameterMap.vehicleId)
        invInventoryTransactionDetails.vehicleNumber = parameterMap.vehicleNumber ? parameterMap.vehicleNumber : null
        invInventoryTransactionDetails.adjustmentParentId = 0L
        invInventoryTransactionDetails.approvedOn = null
        invInventoryTransactionDetails.isIncrease = true
        invInventoryTransactionDetails.transactionTypeId = invInventoryTransaction.transactionTypeId
        invInventoryTransactionDetails.isCurrent = true
        invInventoryTransactionDetails.overheadCost = 0.0d
        return invInventoryTransactionDetails
    }

    private static final String ITEM_COUNT_QUERY = """
        UPDATE inv_inventory_transaction SET
            item_count = item_count + 1,
             version=version+1
        WHERE
              id=:inventoryTransactionId
    """

    /**
     * Increase item count of inventory transaction object(parent object)
     * @param inventoryTransactionId -id of inventory transaction object
     * @return -an integer containing the value of update count
     */
    private int increaseItemCount(long inventoryTransactionId) {
        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId
        ]
        int updateCount = executeUpdateSql(ITEM_COUNT_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to increase item count")
        }
        return updateCount
    }
}
