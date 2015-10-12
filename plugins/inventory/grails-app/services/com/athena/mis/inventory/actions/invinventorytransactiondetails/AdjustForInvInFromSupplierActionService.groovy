package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.Vehicle
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ValuationTypeCacheUtility
import com.athena.mis.application.utility.VehicleCacheUtility
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
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
 *  Adjust approved inventory transaction details object (Inventory In From Supplier)
 *  For details go through Use-Case doc named 'AdjustForInvInFromSupplierActionService'
 */
class AdjustForInvInFromSupplierActionService extends BaseService implements ActionIntf {

    private static final String ADJUSTMENT_SAVE_SUCCESS_MESSAGE = "Adjustment has been saved successfully"
    private static final String ADJUSTMENT_SAVE_FAILURE_MESSAGE = "Can not saved the adjustment"
    private static final String INVENTORY_CURRENT_OBJ = "currentDetails"
    private static final String INVENTORY_REVERSE_ADJ_OBJ = "reverseAdjDetails"
    private static final String INVENTORY_ADJ_DETAILS_OBJ = "adjDetails"
    private static final String INVALID_ENTRY = "Error occurred due to invalid input"
    private static final String ADJ_QTY_LOWER_ZERO = "Adjustment quantity can't be lower than or equal zero"
    private static final String NOT_APPROVED = "Adjustment(s) not required for unapproved inventory transaction"
    private static final String INV_INVENTORY_TRANSACTION_NOT_FOUND = "Inventory transaction not found"
    private static final String UNAVAILABLE_STOCK_ERROR = "Unavailable stock for the adjustment"
    private static final String ADJUSTMENT_QTY_ERROR = "Adjustable quantity not found"
    private static final String ADJUSTMENT_PARENT_ID = "adjustmentParentId"
    private static final String ADJ_COMMENTS_NOT_FOUND = "Adjustment comments not found"
    private static final String ADJUSTMENT_NOT_ALLOWED = "Adjustment not allowed for this item"

    private final Logger log = Logger.getLogger(getClass())

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
    @Autowired
    ValuationTypeCacheUtility valuationTypeCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility

    /**
     * Checking pre condition before doing adjustment of transaction details object (Inventory In From Supplier)
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
            if (!parameterMap.id || !parameterMap.actualQuantity) {
                result.put(Tools.MESSAGE, INVALID_ENTRY)
                return result
            }

            long adjustmentParentId = Long.parseLong(parameterMap.id.toString())
            double actualQuantity = Double.parseDouble(parameterMap.actualQuantity.toString())
            String adjComments = parameterMap.adjComments.toString()
            int version = Integer.parseInt(parameterMap.version.toString())
            // check actual quantity for adjustment
            if (actualQuantity <= 0) {
                result.put(Tools.MESSAGE, ADJ_QTY_LOWER_ZERO)
                return result
            }
            // check if comments for adjustment exists or not
            if (adjComments.size() == 0) {
                result.put(Tools.MESSAGE, ADJ_COMMENTS_NOT_FOUND)
                return result
            }
            // get inventory transaction details object
            InvInventoryTransactionDetails currentTransactionDetails = invInventoryTransactionDetailsService.read(adjustmentParentId)
            // check if inventory transaction details object exists or not
            if (!currentTransactionDetails || currentTransactionDetails.version != version || !currentTransactionDetails.isCurrent) {
                result.put(Tools.MESSAGE, INV_INVENTORY_TRANSACTION_NOT_FOUND)
                return result
            }
            // adjustment can not be done for unapproved transaction details object
            if (currentTransactionDetails.approvedBy <= 0) {
                result.put(Tools.MESSAGE, NOT_APPROVED)
                return result
            }
            // adjustment is not applicable for items which are individual entity
            Item item = (Item) itemCacheUtility.read(currentTransactionDetails.itemId)
            if (item.isIndividualEntity) {
                result.put(Tools.MESSAGE, ADJUSTMENT_NOT_ALLOWED)
                return result
            }
            // adjustment can not be done if actual quantity is same
            double adjustmentQuantity = actualQuantity - currentTransactionDetails.actualQuantity
            if (adjustmentQuantity == 0) {
                result.put(Tools.MESSAGE, ADJUSTMENT_QTY_ERROR)
                return result
            }
            // check available stock(real stock)
            if (adjustmentQuantity < 0) {
                double availableStock = getAvailableStock(currentTransactionDetails.inventoryId, currentTransactionDetails.itemId)
                if (Math.abs(adjustmentQuantity) > availableStock) {
                    result.put(Tools.MESSAGE, UNAVAILABLE_STOCK_ERROR)
                    return result
                }
            }
            // copy current transaction details object for adjustment and reverse adjustment
            InvInventoryTransactionDetails reverseTransactionDetails = copyForReverseAdjustment(currentTransactionDetails, adjComments, invSessionUtil.appSessionUtil.getAppUser())
            InvInventoryTransactionDetails adjTransactionDetails = copyForAdjustment(currentTransactionDetails, actualQuantity, adjComments, invSessionUtil.appSessionUtil.getAppUser())
            // update necessary properties of current transaction details object for adjustment (isCurrent = false)
            currentTransactionDetails.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
            currentTransactionDetails.updatedOn = new Date()
            currentTransactionDetails.isCurrent = false

            result.put(INVENTORY_ADJ_DETAILS_OBJ, adjTransactionDetails)
            result.put(INVENTORY_REVERSE_ADJ_OBJ, reverseTransactionDetails)
            result.put(INVENTORY_CURRENT_OBJ, currentTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ADJUSTMENT_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Create reverse inventory transaction details object of current object (isCurrent = false)
     * Create adjustment object of current inventory transaction details object (isCurrent = true)
     * Update storeIn quantity of item in PO details
     * Set isCurrent false of current inventory transaction details object
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    //default value
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            InvInventoryTransactionDetails reverseAdjDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_REVERSE_ADJ_OBJ)
            InvInventoryTransactionDetails adjDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_ADJ_DETAILS_OBJ)
            InvInventoryTransactionDetails currentDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_CURRENT_OBJ)
            // calculate rate of item for reverse adjustment
            double rate = calculateValuationForOut(reverseAdjDetails.actualQuantity, reverseAdjDetails.inventoryId, reverseAdjDetails.itemId)
            reverseAdjDetails.rate = rate
            // create new inventory transaction details object for adjustment (isCurrent = true)
            InvInventoryTransactionDetails newAdjDetails = invInventoryTransactionDetailsService.create(adjDetails)
            reverseAdjDetails.approvedOn = DateUtility.addOneSecond(reverseAdjDetails.approvedOn)     // required for transaction report
            // create reverse inventory transaction details object (isCurrent = false)
            InvInventoryTransactionDetails newReverseAdjDetails = invInventoryTransactionDetailsService.create(reverseAdjDetails)
            Object purchaseOrderDetails = procurementImplService.readPODetails(newAdjDetails.transactionDetailsId)
            purchaseOrderDetails.storeInQuantity = purchaseOrderDetails.storeInQuantity - newReverseAdjDetails.actualQuantity + newAdjDetails.actualQuantity
            // update storeIn quantity of item in PO details
            Integer updatePODetails = (Integer) procurementImplService.updateStoreInQuantityForPODetails(purchaseOrderDetails)
            if (updatePODetails.intValue() <= 0) {
                throw new RuntimeException('Failed to update PO Details')
            }
            // set isCurrent false of current inventory transaction details object
            setIsCurrentTransaction(currentDetails)
            result.put(INVENTORY_CURRENT_OBJ, currentDetails)
            result.put(INVENTORY_ADJ_DETAILS_OBJ, newAdjDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ADJUSTMENT_SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ADJUSTMENT_SAVE_FAILURE_MESSAGE)
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
     * Show adjusted inventory transaction details object(Inventory In From Supplier) in grid
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

            InvInventoryTransactionDetails adjDetails = (InvInventoryTransactionDetails) executeResult.get(INVENTORY_ADJ_DETAILS_OBJ)
            InvInventoryTransactionDetails currentDetails = (InvInventoryTransactionDetails) executeResult.get(INVENTORY_CURRENT_OBJ)

            Item item = (Item) itemCacheUtility.read(adjDetails.itemId)
            String transactionDate = DateUtility.getLongDateForUI(adjDetails.transactionDate)
            String approvedOn = DateUtility.getLongDateForUI(adjDetails.approvedOn)
            AppUser approvedBy = (AppUser) appUserCacheUtility.read(adjDetails.approvedBy)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(adjDetails.createdBy)
            Vehicle vehicle = (Vehicle) vehicleCacheUtility.read(adjDetails.vehicleId)
            GridEntity object = new GridEntity()    // build grid entity object
            object.id = adjDetails.id
            object.cell = [
                    Tools.LABEL_NEW,
                    adjDetails.id,
                    item.name,
                    Tools.formatAmountWithoutCurrency(adjDetails.suppliedQuantity) + Tools.SINGLE_SPACE + item.unit,
                    Tools.formatAmountWithoutCurrency(adjDetails.actualQuantity) + Tools.SINGLE_SPACE + item.unit,
                    Tools.formatAmountWithoutCurrency(adjDetails.shrinkage) + Tools.SINGLE_SPACE + item.unit,
                    transactionDate,
                    approvedOn,
                    adjDetails.supplierChalan,
                    vehicle.name,
                    approvedBy.username,
                    createdBy.username
            ]

            result.put(Tools.MESSAGE, ADJUSTMENT_SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(ADJUSTMENT_PARENT_ID, currentDetails.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ADJUSTMENT_SAVE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, ADJUSTMENT_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ADJUSTMENT_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    private static final String AVL_STOCK_QUERY = """
            SELECT available_stock FROM vw_inv_inventory_stock
            WHERE inventory_id=:inventoryId
            AND item_id=:itemId
    """

    /**
     * Get available stock
     * @param inventoryId -id of inventory
     * @param itemId -id of item
     * @return -a double variable containing the value of stock
     */
    private double getAvailableStock(long inventoryId, long itemId) {
        Map queryParams = [inventoryId: inventoryId, itemId: itemId]
        List<GroovyRowResult> result = executeSelectSql(AVL_STOCK_QUERY, queryParams)
        if (result.size() > 0) {
            return (double) result[0].available_stock
        }
        return 0.0d
    }

    /**
     * Calculate valuation (rate) of item
     * @param outQuantity -quantity of item
     * @param inventoryId -id of inventory
     * @param itemId -id of item
     * @return -a double variable containing the rate of item
     */
    private double calculateValuationForOut(double outQuantity, long inventoryId, long itemId) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        // pull valuation type object
        SystemEntity valuationTypeFifoObj = (SystemEntity) valuationTypeCacheUtility.readByReservedAndCompany(valuationTypeCacheUtility.VALUATION_TYPE_FIFO, companyId)
        SystemEntity valuationTypeLifoObj = (SystemEntity) valuationTypeCacheUtility.readByReservedAndCompany(valuationTypeCacheUtility.VALUATION_TYPE_LIFO, companyId)
        SystemEntity valuationTypeAvgObj = (SystemEntity) valuationTypeCacheUtility.readByReservedAndCompany(valuationTypeCacheUtility.VALUATION_TYPE_AVG, companyId)

        Item item = (Item) itemCacheUtility.read(itemId)
        double itemRate = 0.0d
        switch (item.valuationTypeId) {
            case valuationTypeFifoObj.id:
                itemRate = getRateForFifo(outQuantity, inventoryId, itemId)   // get rate, update FIFO quantity
                break
            case valuationTypeLifoObj.id:
                itemRate = getRateForLifo(outQuantity, inventoryId, itemId)  // get rate, update LIFO quantity
                break
            case valuationTypeAvgObj.id:
                itemRate = getRateForAverage(inventoryId, itemId)           // get rate (outQuantity is not required here)
                break
            default:
                throw new RuntimeException('Failed to calculateValuationForOut due to unrecognized VALUATION_TYPE')
        }
        return itemRate
    }

    /**
     * Get the rate for FIFO measurement and update FIFO quantity of inventory transaction details object
     * @param outQuantity -quantity of item
     * @param inventoryId -id of inventory
     * @param itemId -id of item
     * @return -a double variable containing the rate of item for FIFO measurement
     */
    private double getRateForFifo(double outQuantity, long inventoryId, long itemId) {
        // get list of item quantity to calculate FIFO quantity of an item
        List<GroovyRowResult> lstFIFO = getListForFIFO(inventoryId, itemId)
        double totalFifoAmount = 0.0d
        double inventoryOutQuantity = outQuantity     // copy the out quantity in a variable
        for (int i = 0; i < lstFIFO.size(); i++) {
            GroovyRowResult inventoryTransactionDetails = lstFIFO[i]
            double fifoAvailable = inventoryTransactionDetails.actual_quantity - inventoryTransactionDetails.fifo_quantity

            if (inventoryOutQuantity > fifoAvailable) {
                inventoryOutQuantity = inventoryOutQuantity - fifoAvailable
                inventoryTransactionDetails.fifo_quantity = inventoryTransactionDetails.actual_quantity
                updateFifoQuantity(inventoryTransactionDetails)    // update FIFO quantity of inventory transaction details object
                totalFifoAmount = totalFifoAmount + (inventoryTransactionDetails.rate * fifoAvailable)
            } else {
                inventoryTransactionDetails.fifo_quantity = inventoryTransactionDetails.fifo_quantity + inventoryOutQuantity
                updateFifoQuantity(inventoryTransactionDetails)    // update FIFO quantity of inventory transaction details object
                totalFifoAmount = totalFifoAmount + (inventoryTransactionDetails.rate * inventoryOutQuantity)
                break
            }
        }
        double unitPriceFifo = totalFifoAmount / outQuantity
        return unitPriceFifo.round(2)
    }

    private static final String LST_FIFO_QUERY = """
                SELECT itd.id, itd.version, itd.actual_quantity, itd.fifo_quantity, itd.rate, itd.transaction_type_id
                FROM inv_inventory_transaction_details itd
                WHERE
                    itd.is_increase = true
                    AND itd.approved_by > 0
                    AND itd.item_id=:itemId
                    AND itd.inventory_id=:inventoryId
                    AND itd.fifo_quantity < itd.actual_quantity
                    ORDER BY itd.approved_on ASC
    """

    /**
     * Get list of item quantity to calculate FIFO quantity of an item
     * @param inventoryId -id of inventory
     * @param itemId -id of item
     * @return -list of item quantity
     */
    private List<GroovyRowResult> getListForFIFO(long inventoryId, long itemId) {
        Map queryParams = [inventoryId: inventoryId, itemId: itemId]
        List<GroovyRowResult> lstFifo = executeSelectSql(LST_FIFO_QUERY, queryParams)
        return lstFifo
    }

    private static final String UPDATE_FIFO_QUANTITY_QUERY = """
        UPDATE inv_inventory_transaction_details
        SET
            version=:newVersion,
            fifo_quantity=:fifoQuantity
        WHERE
            id=:id
            AND version=:version
    """

    /**
     * Update FIFO quantity of inventory transaction details object
     * @param inventoryTransactionDetails -inventory transaction details object
     * @return -an integer containing the value of update count
     */
    private int updateFifoQuantity(GroovyRowResult inventoryTransactionDetails) {
        Map queryParams = [
                id: inventoryTransactionDetails.id,
                version: inventoryTransactionDetails.version,
                newVersion: inventoryTransactionDetails.version + 1,
                fifoQuantity: inventoryTransactionDetails.fifo_quantity
        ]
        int updateCount = executeUpdateSql(UPDATE_FIFO_QUANTITY_QUERY, queryParams)
        if (updateCount > 0) {
            inventoryTransactionDetails.version = inventoryTransactionDetails.version + 1
        } else {
            throw new RuntimeException('Failed to update FIFO count')
        }
        return updateCount
    }

    /**
     * Get the rate for LIFO measurement and update LIFO quantity of inventory transaction details object
     * @param outQuantity -quantity of item
     * @param inventoryId -id of inventory
     * @param itemId -id of item
     * @return -a double variable containing the rate of item for LIFO measurement
     */
    private double getRateForLifo(double outQuantity, long inventoryId, long itemId) {
        // get list of item quantity to calculate LIFO quantity of an item
        List<GroovyRowResult> lstLIFO = getListForLIFO(inventoryId, itemId)
        double totalLifoAmount = 0.0d
        double inventoryOutQuantity = outQuantity     // copy the out quantity in a variable

        for (int i = 0; i < lstLIFO.size(); i++) {
            GroovyRowResult inventoryTransactionDetails = lstLIFO[i]
            double lifoAvailable = inventoryTransactionDetails.actual_quantity - inventoryTransactionDetails.lifo_quantity

            if (inventoryOutQuantity > lifoAvailable) {
                inventoryOutQuantity = inventoryOutQuantity - lifoAvailable
                inventoryTransactionDetails.lifo_quantity = inventoryTransactionDetails.actual_quantity
                updateLifoQuantity(inventoryTransactionDetails)  // update LIFO quantity of inventory transaction details object
                totalLifoAmount = totalLifoAmount + (inventoryTransactionDetails.rate * lifoAvailable)
            } else {
                inventoryTransactionDetails.lifo_quantity = inventoryTransactionDetails.lifo_quantity + inventoryOutQuantity
                updateLifoQuantity(inventoryTransactionDetails)  // update LIFO quantity of inventory transaction details object
                totalLifoAmount = totalLifoAmount + (inventoryTransactionDetails.rate * inventoryOutQuantity)
                break
            }
        }
        double unitPriceLifo = totalLifoAmount / outQuantity
        return unitPriceLifo.round(2)
    }

    private static final String LIFO_LIST_QUERY = """
                SELECT itd.id, itd.version, itd.actual_quantity, itd.lifo_quantity, itd.rate, itd.transaction_type_id
                FROM inv_inventory_transaction_details itd
                WHERE
                itd.is_increase = true
                AND itd.approved_by > 0
                AND itd.item_id=:itemId
                AND itd.inventory_id=:inventoryId
                AND itd.lifo_quantity < itd.actual_quantity
                ORDER BY itd.approved_on DESC
    """

    /**
     * Get list of item quantity to calculate LIFO quantity of an item
     * @param inventoryId -id of inventory
     * @param itemId -id of item
     * @return -list of item quantity
     */
    private List<GroovyRowResult> getListForLIFO(long inventoryId, long itemId) {
        Map queryParams = [
                itemId: itemId,
                inventoryId: inventoryId
        ]
        List<GroovyRowResult> lstLifo = executeSelectSql(LIFO_LIST_QUERY, queryParams)
        return lstLifo
    }

    private static final String UPDATE_LIFO_QUANTITY_QUERY = """
        UPDATE inv_inventory_transaction_details
        SET
            version=:newVersion,
            lifo_quantity=:lifoQuantity
        WHERE
            id=:id
            AND version=:version
    """

    /**
     * Update LIFO quantity of inventory transaction details object
     * @param inventoryTransactionDetails -inventory transaction details object
     * @return -an integer containing the value of update count
     */
    private int updateLifoQuantity(GroovyRowResult inventoryTransactionDetails) {
        Map queryParams = [
                id: inventoryTransactionDetails.id,
                version: inventoryTransactionDetails.version,
                newVersion: inventoryTransactionDetails.version + 1,
                lifoQuantity: inventoryTransactionDetails.lifo_quantity
        ]
        int updateCount = executeUpdateSql(UPDATE_LIFO_QUANTITY_QUERY, queryParams)
        if (updateCount > 0) {
            inventoryTransactionDetails.version = inventoryTransactionDetails.version + 1
        } else {
            throw new RuntimeException('Failed to update LIFO count')
        }
        return updateCount
    }

    /**
     * Get the rate for Average measurement
     * @param inventoryId -id of inventory
     * @param itemId -id of item
     * @return -a double variable containing the rate of item for Average measurement
     */
    private double getRateForAverage(long inventoryId, long itemId) {
        // get list of item quantity to calculate Average quantity of an item
        List<GroovyRowResult> lstAverage = getListForAverage(inventoryId, itemId)
        double totQuantity = 0
        double totAmount = 0
        for (int i = 0; i < lstAverage.size(); i++) {
            GroovyRowResult inventoryTransactionDetails = lstAverage[i]
            totQuantity = totQuantity + inventoryTransactionDetails.actual_quantity
            totAmount = totAmount + (inventoryTransactionDetails.rate * inventoryTransactionDetails.actual_quantity)
        }
        double unitPriceAvg = totAmount / totQuantity
        return unitPriceAvg.round(2)
    }

    private static final String INVENTORY_TRANSACTION_DETAILS_UPDATE_QUERY = """
                      UPDATE inv_inventory_transaction_details
                      SET is_current=false,
                      version=:newVersion,
                      updated_by =:updatedBy,
                      updated_on =:updatedOn
                      WHERE id =:id
    """

    /**
     * Set isCurrent false of inventory transaction details object
     * @param invInventoryTransactionDetails -inventory transaction details object
     * @return -an integer containing the value of update count
     */
    private int setIsCurrentTransaction(InvInventoryTransactionDetails invInventoryTransactionDetails) {
        Map queryParams = [
                id: invInventoryTransactionDetails.id,
                newVersion: invInventoryTransactionDetails.version + 1,
                updatedBy: invInventoryTransactionDetails.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(invInventoryTransactionDetails.updatedOn)
        ]
        int updateCount = executeUpdateSql(INVENTORY_TRANSACTION_DETAILS_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update inventory transaction details')
        }
        return updateCount
    }

    private static final String AVERAGE_LIST_QUERY = """
                SELECT itd.id, itd.actual_quantity, itd.rate, itd.transaction_type_id
                FROM inv_inventory_transaction_details itd
                WHERE
                itd.is_increase = true
                AND itd.approved_by > 0
                AND itd.item_id=:itemId
                AND itd.inventory_id=:inventoryId
                AND ( (itd.fifo_quantity < itd.actual_quantity) OR (itd.lifo_quantity < itd.actual_quantity) )
    """

    /**
     * Get list of item quantity to calculate Average quantity of an item
     * @param inventoryId -id of inventory
     * @param itemId -id of item
     * @return -list of item quantity
     */
    private List<GroovyRowResult> getListForAverage(long inventoryId, long itemId) {
        Map queryParams = [
                itemId: itemId,
                inventoryId: inventoryId
        ]
        List<GroovyRowResult> lstAverage = executeSelectSql(AVERAGE_LIST_QUERY, queryParams)
        return lstAverage
    }

    private InvInventoryTransactionDetails copyForAdjustment(InvInventoryTransactionDetails currentTransactionDetails, double newQuantity, String comments, AppUser user) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_ADJUSTMENT, companyId)

        InvInventoryTransactionDetails newDetails = new InvInventoryTransactionDetails()
        newDetails.id = 0
        newDetails.version = 0
        newDetails.inventoryTransactionId = currentTransactionDetails.inventoryTransactionId
        newDetails.transactionDetailsId = currentTransactionDetails.transactionDetailsId
        newDetails.inventoryTypeId = currentTransactionDetails.inventoryTypeId
        newDetails.inventoryId = currentTransactionDetails.inventoryId
        newDetails.approvedBy = user.id
        newDetails.itemId = currentTransactionDetails.itemId
        newDetails.vehicleId = currentTransactionDetails.vehicleId
        newDetails.vehicleNumber = currentTransactionDetails.vehicleNumber
        newDetails.suppliedQuantity = newQuantity
        newDetails.actualQuantity = newQuantity
        newDetails.shrinkage = 0
        newDetails.rate = currentTransactionDetails.rate                        // same as parents rate
        newDetails.supplierChalan = currentTransactionDetails.supplierChalan
        newDetails.stackMeasurement = currentTransactionDetails.stackMeasurement
        newDetails.fifoQuantity = 0
        newDetails.lifoQuantity = 0
        newDetails.acknowledgedBy = currentTransactionDetails.acknowledgedBy
        newDetails.createdOn = new Date()
        newDetails.createdBy = user.id
        newDetails.updatedOn = null
        newDetails.updatedBy = 0
        newDetails.comments = comments
        newDetails.mrfNo = currentTransactionDetails.mrfNo
        newDetails.transactionDate = currentTransactionDetails.transactionDate
        newDetails.fixedAssetId = currentTransactionDetails.fixedAssetId
        newDetails.fixedAssetDetailsId = currentTransactionDetails.fixedAssetDetailsId
        newDetails.transactionTypeId = transactionTypeAdj.id              // TRANSACTION_TYPE_ADJUSTMENT
        newDetails.adjustmentParentId = currentTransactionDetails.adjustmentParentId == 0 ? currentTransactionDetails.id : currentTransactionDetails.adjustmentParentId
        newDetails.approvedOn = new Date()
        newDetails.isIncrease = currentTransactionDetails.isIncrease
        newDetails.isCurrent = true
        newDetails.overheadCost = currentTransactionDetails.overheadCost
        newDetails.invoiceAcknowledgedBy = currentTransactionDetails.invoiceAcknowledgedBy
        return newDetails
    }

    private InvInventoryTransactionDetails copyForReverseAdjustment(InvInventoryTransactionDetails oldConsumpDetails, String comments, AppUser user) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeRevAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_REVERSE_ADJUSTMENT, companyId)


        InvInventoryTransactionDetails newDetails = new InvInventoryTransactionDetails()
        newDetails.id = 0
        newDetails.version = 0
        newDetails.inventoryTransactionId = oldConsumpDetails.inventoryTransactionId
        newDetails.transactionDetailsId = 0
        newDetails.inventoryTypeId = oldConsumpDetails.inventoryTypeId
        newDetails.inventoryId = oldConsumpDetails.inventoryId
        newDetails.approvedBy = user.id
        newDetails.itemId = oldConsumpDetails.itemId
        newDetails.vehicleId = 0
        newDetails.vehicleNumber = null
        newDetails.suppliedQuantity = oldConsumpDetails.suppliedQuantity
        newDetails.actualQuantity = oldConsumpDetails.actualQuantity
        newDetails.shrinkage = oldConsumpDetails.shrinkage
        newDetails.rate = oldConsumpDetails.rate                     // same as parents rate
        newDetails.supplierChalan = null
        newDetails.stackMeasurement = null
        newDetails.fifoQuantity = 0
        newDetails.lifoQuantity = 0
        newDetails.acknowledgedBy = 0
        newDetails.createdOn = new Date()
        newDetails.createdBy = user.id
        newDetails.updatedOn = null
        newDetails.updatedBy = 0
        newDetails.comments = comments
        newDetails.mrfNo = null
        newDetails.transactionDate = oldConsumpDetails.transactionDate
        newDetails.fixedAssetId = 0
        newDetails.fixedAssetDetailsId = 0
        newDetails.transactionTypeId = transactionTypeRevAdj.id              // TRANSACTION_TYPE_REVERSE_ADJUSTMENT
        newDetails.adjustmentParentId = oldConsumpDetails.adjustmentParentId == 0 ? oldConsumpDetails.id : oldConsumpDetails.adjustmentParentId
        newDetails.approvedOn = new Date()
        newDetails.isIncrease = !(oldConsumpDetails.isIncrease)
        newDetails.isCurrent = false
        newDetails.overheadCost = oldConsumpDetails.overheadCost
        newDetails.invoiceAcknowledgedBy = oldConsumpDetails.invoiceAcknowledgedBy
        return newDetails
    }
}
