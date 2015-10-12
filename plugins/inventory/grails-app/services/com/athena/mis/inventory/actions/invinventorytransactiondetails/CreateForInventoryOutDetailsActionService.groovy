package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.*
import com.athena.mis.application.utility.*
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
 * Create new unapprovedInventoryOutDetails (unapproved child) and show in grid
 * For details go through Use-Case doc named 'CreateForInventoryOutDetailsActionService'
 */
class CreateForInventoryOutDetailsActionService extends BaseService implements ActionIntf {

    private static final String INV_OUT_SAVE_SUCCESS_MESSAGE = "Inventory Out has been saved successfully"
    private static final String INV_OUT_SAVE_FAILURE_MESSAGE = "Can not save inventory out"
    private static final String INV_OUT_DETAILS_OBJ = "inventoryOutDetails"
    private static final String INV_OUT_OBJ = "inventoryOut"
    private static final String INV_STORE_TRANSACTION_NOT_FOUND = "Inventory Transaction not found"
    private static final String ITEM_LIST = "itemList"
    private static final String STOCK_QUANTITY_MESSAGE = "No sufficient stock to inventory out"
    private static final String PROJECT_OBJ = "projectObject"


    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    VehicleCacheUtility vehicleCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    InvSysConfigurationCacheUtility invSysConfigurationCacheUtility
    @Autowired
    ValuationTypeCacheUtility valuationTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility

    /**
     * validate different criteria to create unapproved inventoryOutDetails. Such as :
     *      Check existence of InvInventoryTransaction(Parent) Obj,
     *      Check availableStockQuantity to out from inventory
     *
     * @param params -Receives serialized parameters send from UI
     * @param obj -N/A
     * @Return -a map containing all objects necessary for execute (InvInventoryTransaction(Parent), InvInventoryTransactionDetails(child))
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long inventoryTransactionId = Long.parseLong(parameterMap.inventoryTransactionId.toString())
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(inventoryTransactionId)
            if (!invInventoryTransaction) {//check existence of parent object
                result.put(Tools.MESSAGE, INV_STORE_TRANSACTION_NOT_FOUND)
                return result
            }

            //Build to create inventoryOutDetailsObject(Child object)
            InvInventoryTransactionDetails invInventoryTransactionDetails = buildInvInventoryTransactionDetailsObject(parameterMap, invInventoryTransaction)

            SysConfiguration approveConfig = invSysConfigurationCacheUtility.readByKeyAndCompanyId(invSysConfigurationCacheUtility.MIS_INVENTORY_AUTO_APPROVE_ON_OUT)
            InvInventory invInventoryObject = (InvInventory) invInventoryCacheUtility.read(invInventoryTransactionDetails.inventoryId)

            Project projectObject = (Project) projectCacheUtility.read(invInventoryObject.projectId)

            // check available stock from view
            double availableQuantity
            if (projectObject.isApproveInvOut) {//SysConfig.value = 1 means object will be approved at create event
                //if SysConfig obj not found OR value is 1 then check real stock
                availableQuantity = getAvailableStock(invInventoryTransactionDetails.inventoryId, invInventoryTransactionDetails.itemId)
            } else { //otherwise check consumable-stock
                // check available stock from view to out from inventory
                availableQuantity = getConsumableStock(invInventoryTransactionDetails.inventoryId, invInventoryTransactionDetails.itemId)
            }

            if (invInventoryTransactionDetails.actualQuantity > availableQuantity) { //if has no sufficient stock to out then return with message
                result.put(Tools.MESSAGE, STOCK_QUANTITY_MESSAGE)
                return result
            }

            result.put(PROJECT_OBJ, projectObject)
            result.put(INV_OUT_DETAILS_OBJ, invInventoryTransactionDetails)
            result.put(INV_OUT_OBJ, invInventoryTransaction)
            result.put(Tools.IS_ERROR, Boolean.FALSE)

            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INV_OUT_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Method to save unapproved-InvTransactionOutDetails (Child)
     *
     * @param parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains InvInventoryTransaction(Parent), InvInventoryTransactionDetails(child)
     *
     * @Return -a map containing all objects necessary for buildSuccessResultForUI(InvInventoryTransactionDetails(child))
     * map -contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvInventoryTransactionDetails inventoryOutDetails = (InvInventoryTransactionDetails) preResult.get(INV_OUT_DETAILS_OBJ)
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) preResult.get(INV_OUT_OBJ)

            // increase material count in parent & update it
            increaseItemCount(invInventoryTransaction.id) // itemCount++

            // check if auto approve or not
            Project projectObject = (Project) preResult.get(PROJECT_OBJ)
            InvInventoryTransactionDetails newInvInventoryTransactionDetails      //Create outDetails object

            // if config not found OR is configured to approve in create event then do inventory transaction
            if (projectObject.isApproveInvOut) {//SysConfig.value = 1 means object will be approved at create event
                //if SysConfig obj not found OR value is 1 then approve transaction at create event
                inventoryOutDetails.approvedBy = invSessionUtil.appSessionUtil.getAppUser().id
                inventoryOutDetails.approvedOn = new Date()

                //get consumptionRate by calculatingValuation
                double unitValuation = calculateValuationForOut(inventoryOutDetails.actualQuantity, inventoryOutDetails.inventoryId, inventoryOutDetails.itemId)
                inventoryOutDetails.rate = unitValuation
                newInvInventoryTransactionDetails = invInventoryTransactionDetailsService.create(inventoryOutDetails)
            } else {
                newInvInventoryTransactionDetails = invInventoryTransactionDetailsService.create(inventoryOutDetails) //only create transactionDetails
            }

            if (!newInvInventoryTransactionDetails) {
                result.put(Tools.MESSAGE, INV_OUT_SAVE_FAILURE_MESSAGE)
                return result
            }
            result.put(PROJECT_OBJ, projectObject)
            result.put(INV_OUT_DETAILS_OBJ, newInvInventoryTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to create inventory out details')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INV_OUT_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap unapprovedInventoryOur object for grid
     * @param obj -map returned from execute
     * @return -a map containing all objects necessary for show page (wrappedUnapprovedInventoryOut)
     * map -contains isError(true/false) depending on method success, available itemList for drop-down to out again
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            InvInventoryTransactionDetails inventoryOutDetails = (InvInventoryTransactionDetails) receiveResult.get(INV_OUT_DETAILS_OBJ)
            GridEntity object = new GridEntity()
            object.id = inventoryOutDetails.id
            Item item = (Item) itemCacheUtility.read(inventoryOutDetails.itemId)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(inventoryOutDetails.createdBy)
            Vehicle vehicle = (Vehicle) vehicleCacheUtility.read(inventoryOutDetails.vehicleId)
            String transactionDateStr = DateUtility.getLongDateForUI(inventoryOutDetails.transactionDate)
            object.cell = [Tools.LABEL_NEW,
                    inventoryOutDetails.id,
                    item.name,
                    Tools.formatAmountWithoutCurrency(inventoryOutDetails.actualQuantity) + Tools.SINGLE_SPACE + item.unit,
                    transactionDateStr,
                    inventoryOutDetails.mrfNo,
                    vehicle.name,
                    inventoryOutDetails.vehicleNumber,
                    createdBy.username,
                    Tools.EMPTY_SPACE
            ]
            // pull latest material list (to out again) to show on drop-down
            List itemList = listAvailableItemInInventory(inventoryOutDetails.inventoryId)
            result.put(ITEM_LIST, Tools.listForKendoDropdown(itemList,null,null))
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, INV_OUT_SAVE_SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INV_OUT_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to create outDetails object
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, INV_OUT_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INV_OUT_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Method to get itemRate based on valuation of that item
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     * @Param outQuantity -total consumed quantity
     *
     * @Return double value
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
                itemRate = getRateForFifo(outQuantity, inventoryId, itemId)   // get rate , update FIFO count
                break
            case valuationTypeLifoObj.id:
                itemRate = getRateForLifo(outQuantity, inventoryId, itemId)  // get rate , update LIFO count
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
     * Returns the rate for Average measurement
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return double value
     */
    private double getRateForAverage(long inventoryId, long itemId) {
        List<GroovyRowResult> lstAverage = getListForAverage(inventoryId, itemId)
        // Average calculation
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
     * Returns list of groovyRowResult to calculate averageQuantity of an item
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return list of GroovyRowResult
     */
    private List<GroovyRowResult> getListForAverage(long inventoryId, long itemId) {
        Map queryParams = [
                itemId: itemId,
                inventoryId: inventoryId
        ]
        List<GroovyRowResult> lstAverage = executeSelectSql(AVERAGE_LIST_QUERY, queryParams)
        return lstAverage
    }

    /**
     * Returns the rate for LIFO measurement
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     * @Param outQuantity -total consumed quantity
     *
     * @Return double value
     */
    private double getRateForLifo(double outQuantity, long inventoryId, long itemId) {
        List<GroovyRowResult> lstLIFO = getListForLIFO(inventoryId, itemId)
        double totalLifoAmount = 0.0d
        double inventoryOutQuantity = outQuantity     // copy the out quantity in a variable

        for (int i = 0; i < lstLIFO.size(); i++) {
            GroovyRowResult inventoryTransactionDetails = lstLIFO[i]
            double lifoAvailable = inventoryTransactionDetails.actual_quantity - inventoryTransactionDetails.lifo_quantity

            if (inventoryOutQuantity > lifoAvailable) {
                inventoryOutQuantity = inventoryOutQuantity - lifoAvailable
                inventoryTransactionDetails.lifo_quantity = inventoryTransactionDetails.actual_quantity
                updateLifoCount(inventoryTransactionDetails)  // save This inventory Details with updated LIFO count
                totalLifoAmount = totalLifoAmount + (inventoryTransactionDetails.rate * lifoAvailable)
            } else {
                inventoryTransactionDetails.lifo_quantity = inventoryTransactionDetails.lifo_quantity + inventoryOutQuantity
                updateLifoCount(inventoryTransactionDetails)  // save This inventory Details with updated LIFO count
                totalLifoAmount = totalLifoAmount + (inventoryTransactionDetails.rate * inventoryOutQuantity)
                break
            }
        }
        double unitPriceLifo = totalLifoAmount / outQuantity
        return unitPriceLifo.round(2)
    }

    private static final String UPDATE_LIFO_COUNT_QUERY = """
        UPDATE inv_inventory_transaction_details
        SET
            version=:newVersion,
            lifo_quantity=:lifoQuantity
        WHERE
            id=:id
            AND version=:version
        """
    /**
     * Method to update lifo_quantity of invTransactionDetails object(children)
     * @Param inventoryTransactionDetails -InventoryTransactionDetails object (child)
     * @Return int value(if return value <=0 : throw exception to rollback all DB transaction)
     */
    private int updateLifoCount(GroovyRowResult inventoryTransactionDetails) {
        Map queryParams = [
                id: inventoryTransactionDetails.id,
                version: inventoryTransactionDetails.version,
                newVersion: inventoryTransactionDetails.version + 1,
                lifoQuantity: inventoryTransactionDetails.lifo_quantity
        ]
        int updateCount = executeUpdateSql(UPDATE_LIFO_COUNT_QUERY, queryParams)
        if (updateCount > 0) {
            inventoryTransactionDetails.version = inventoryTransactionDetails.version + 1
        } else {
            throw new RuntimeException('Failed to update LIFO count')
        }
        return updateCount
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
     * Returns list of groovyRowResult to calculate lifoQuantity of an item
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return List < GroovyRowResult >
     */
    private List<GroovyRowResult> getListForLIFO(long inventoryId, long itemId) {
        Map queryParams = [
                itemId: itemId,
                inventoryId: inventoryId
        ]
        List<GroovyRowResult> lstLifo = executeSelectSql(LIFO_LIST_QUERY, queryParams)
        return lstLifo
    }

    /**
     * Returns the rate for FIFO measurement
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     * @Param outQuantity -total consumed quantity
     *
     * @Return double value
     */
    private double getRateForFifo(double outQuantity, long inventoryId, long itemId) {
        List<GroovyRowResult> lstFIFO = getListForFIFO(inventoryId, itemId)
        double totalFifoAmount = 0.0d
        double inventoryOutQuantity = outQuantity     // copy the out quantity in a variable
        for (int i = 0; i < lstFIFO.size(); i++) {
            GroovyRowResult inventoryTransactionDetails = lstFIFO[i]
            double fifoAvailable = inventoryTransactionDetails.actual_quantity - inventoryTransactionDetails.fifo_quantity

            if (inventoryOutQuantity > fifoAvailable) {
                inventoryOutQuantity = inventoryOutQuantity - fifoAvailable
                inventoryTransactionDetails.fifo_quantity = inventoryTransactionDetails.actual_quantity
                updateFifoCount(inventoryTransactionDetails)  // save This Inventory Details with updated FIFO count
                totalFifoAmount = totalFifoAmount + (inventoryTransactionDetails.rate * fifoAvailable)
            } else {
                inventoryTransactionDetails.fifo_quantity = inventoryTransactionDetails.fifo_quantity + inventoryOutQuantity
                updateFifoCount(inventoryTransactionDetails)  // save This Inventory Details with updated FIFO count
                totalFifoAmount = totalFifoAmount + (inventoryTransactionDetails.rate * inventoryOutQuantity)
                break
            }
        }
        double unitPriceFifo = totalFifoAmount / outQuantity
        return unitPriceFifo.round(2)
    }

    private static final String UPDATE_FIFO_COUNT_QUERY = """
        UPDATE inv_inventory_transaction_details
        SET
            version=:newVersion,
            fifo_quantity=:fifoQuantity
        WHERE
            id=:id
            AND version=:version
    """
    /**
     * Method to update fifo_quantity of invTransactionDetails object(children)
     * @Param inventoryTransactionDetails -InventoryTransactionDetails object (child)
     * @Return int value(if return value <=0 : throw exception to rollback all DB transaction)
     */
    private int updateFifoCount(GroovyRowResult inventoryTransactionDetails) {
        Map queryParams = [
                id: inventoryTransactionDetails.id,
                version: inventoryTransactionDetails.version,
                newVersion: inventoryTransactionDetails.version + 1,
                fifoQuantity: inventoryTransactionDetails.fifo_quantity
        ]
        int updateCount = executeUpdateSql(UPDATE_FIFO_COUNT_QUERY, queryParams)
        if (updateCount > 0) {
            inventoryTransactionDetails.version = inventoryTransactionDetails.version + 1
        } else {
            throw new RuntimeException('Failed to update FIFO count')
        }
        return updateCount
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
     * Returns list of groovyRowResult to calculate fifoQuantity of an item
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return List < GroovyRowResult >
     */
    private List<GroovyRowResult> getListForFIFO(long inventoryId, long itemId) {
        Map queryParams = [inventoryId: inventoryId, itemId: itemId]
        List<GroovyRowResult> lstFifo = executeSelectSql(LST_FIFO_QUERY, queryParams)
        return lstFifo
    }

    /**
     *  build outDetailsObject(child) to save
     * @param parameterMap -GrailsParameterMap sent from UI
     * @param inventoryTransaction -InvInventoryTransaction object(parent)
     * @return invInventoryTransactionDetails object (outDetailsObject)
     */
    private InvInventoryTransactionDetails buildInvInventoryTransactionDetailsObject(GrailsParameterMap parameterMap, InvInventoryTransaction invInventoryTransaction) {
        InvInventoryTransactionDetails invInventoryTransactionDetails = new InvInventoryTransactionDetails(parameterMap)
        invInventoryTransactionDetails.inventoryId = invInventoryTransaction.inventoryId
        invInventoryTransactionDetails.inventoryTypeId = invInventoryTransaction.inventoryTypeId
        invInventoryTransactionDetails.inventoryTransactionId = invInventoryTransaction.id     // parent id
        invInventoryTransactionDetails.rate = 0.0d      // rate will determined after calculating fifo/lifo & based on Item's valuationType
        invInventoryTransactionDetails.fifoQuantity = 0.0d
        invInventoryTransactionDetails.lifoQuantity = 0.0d
        invInventoryTransactionDetails.createdOn = new Date()
        invInventoryTransactionDetails.createdBy = invSessionUtil.appSessionUtil.getAppUser().id
        invInventoryTransactionDetails.updatedOn = null
        invInventoryTransactionDetails.updatedBy = 0L
        invInventoryTransactionDetails.acknowledgedBy = 0L
        invInventoryTransactionDetails.transactionDate = invInventoryTransaction.transactionDate
        invInventoryTransactionDetails.transactionDetailsId = 0L
        invInventoryTransactionDetails.comments = parameterMap.comments ? parameterMap.comments : Tools.EMPTY_SPACE
        invInventoryTransactionDetails.supplierChalan = parameterMap.supplierChalan ? parameterMap.supplierChalan : Tools.EMPTY_SPACE
        invInventoryTransactionDetails.itemId = Long.parseLong(parameterMap.itemId.toString())
        invInventoryTransactionDetails.transactionTypeId = invInventoryTransaction.transactionTypeId

        invInventoryTransactionDetails.adjustmentParentId = 0L
        invInventoryTransactionDetails.approvedOn = null
        invInventoryTransactionDetails.isIncrease = false
        invInventoryTransactionDetails.isCurrent = true
        invInventoryTransactionDetails.overheadCost = 0.0d
        return invInventoryTransactionDetails
    }

    private static final String CONSUMABLE_STOCK_QUERY = """
        SELECT  consumeable_stock
        FROM vw_inv_inventory_consumable_stock
        WHERE inventory_id=:inventoryId
          AND item_id=:itemId
    """
    /**
     * Method to check available stock to out from an inventory of an item using view(vw_inv_inventory_consumable_stock)
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return -double value
     */
    private double getConsumableStock(long inventoryId, long itemId) {
        Map queryParams = [inventoryId: inventoryId, itemId: itemId]
        List<GroovyRowResult> result = executeSelectSql(CONSUMABLE_STOCK_QUERY, queryParams)
        if (result.size() > 0) {
            return (double) result[0].consumeable_stock
        }
        return 0.0d
    }

    private static final String AVL_STOCK_QUERY = """
            SELECT available_stock FROM vw_inv_inventory_stock
            WHERE inventory_id=:inventoryId
            AND item_id=:itemId
        """
    /**
     * Method to check available stock(Real stock) to consume in an inventory of an item using view(vw_inv_inventory_stock)
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return -double value
     */
    private double getAvailableStock(long inventoryId, long itemId) {
        Map queryParams = [inventoryId: inventoryId, itemId: itemId]
        List<GroovyRowResult> result = executeSelectSql(AVL_STOCK_QUERY, queryParams)
        if (result.size() > 0) {
            return (double) result[0].available_stock
        }
        return 0.0d
    }

    private static final String AVAIL_ITM_IN_INV_QUERY = """
        SELECT item.id,item.name as name,item.unit as unit,vcs.consumeable_stock as curr_quantity
        FROM vw_inv_inventory_consumable_stock vcs
            LEFT JOIN item  on item.id=vcs.item_id
        WHERE vcs.inventory_id=:inventoryId
            AND vcs.consumeable_stock>0
            AND item.is_individual_entity = false
            ORDER BY item.name ASC
    """
    /**
     * Get List of available item from view(vw_inv_inventory_consumable_stock)
     * @param inventoryId -Inventory.id
     * @return - a map containing list of GroovyRowResult(itemList)
     */
    private List<GroovyRowResult> listAvailableItemInInventory(long inventoryId) {
        Map queryParams = [inventoryId: inventoryId]
        List<GroovyRowResult> itemListWithQnty = executeSelectSql(AVAIL_ITM_IN_INV_QUERY, queryParams)
        return itemListWithQnty
    }

    private static final String ITEM_COUNT_QUERY =
        """
            UPDATE inv_inventory_transaction SET
                item_count = item_count + 1,
                 version=version+1
            WHERE
                  id=:inventoryTransactionId
       """
    /**
     * method increase item count of parent
     * @Param inventoryTransactionId -InvInventoryTransaction.id(parentId)
     * @Return int -if return value <= 0 then throw exception to rollback all transaction
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
