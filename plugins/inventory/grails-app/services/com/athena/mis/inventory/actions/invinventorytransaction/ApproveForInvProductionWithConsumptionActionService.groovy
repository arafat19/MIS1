/* how much raw materials is consumed against how much
  finished product(s) is approved by assigned person */
package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.application.utility.ValuationTypeCacheUtility
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to Approve an unapprovedInventoryProduction
 *  For details go through Use-Case doc named 'ApproveForInvProductionWithConsumptionActionService'
 */
class ApproveForInvProductionWithConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String APPROVE_SUCCESS_MESSAGE = "Inventory production has been approved successfully"
    private static final String APPROVE_FAILURE_MESSAGE = "Inventory production could not be approved, Please refresh the Inventory-Transaction"
    private static final String LST_CONSUMPTION_DETAILS_OBJ = "lstConsumptionDetailsObj"
    private static final String LST_PRODUCTION_DETAILS_OBJ = "lstProductionDetailsObj"
    private static final String ALREADY_APPROVED = "This inventory production is already approved"

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ValuationTypeCacheUtility valuationTypeCacheUtility

    /**
     * check existence of invInventoryTransactions (parent object- both consumption & production) which will be approved
     * check existence of invInventoryTransactionDetails (children objects- both consumptionDetails & productionDetails)
     * Checking Availability of item(s) for consumption
     *
     * @Params parameters -Receives the serialized parameters sent from UI.
     * @Params obj -N/A
     *
     * @Return -Map containing isError(true/false), Children(lstConsumptionDetailsObj & lstProductionDetailsObj)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long tranIdConsumption = Long.parseLong(params.transactionId.toString())

            // Read consumption details object(s)
            InvInventoryTransaction invTranConsumption = invInventoryTransactionService.read(tranIdConsumption)
            if (!invTranConsumption) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            //check whether the production-transaction already approved or not
            if (invTranConsumption.isApproved) {
                result.put(Tools.MESSAGE, ALREADY_APPROVED)
                return result
            }

            //get list of raw-material list(TransactionDetailsConsumption)
            List<GroovyRowResult> lstConsumptionDetailsObj = listTransactionDetailsByTranId(tranIdConsumption)
            if (lstConsumptionDetailsObj.size() < 0) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            // Read production object by transactionId
            InvInventoryTransaction invTranProduction = invInventoryTransactionService.readByTransactionId(tranIdConsumption)
            if (!invTranProduction) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            //Get production details object by production id
            List<GroovyRowResult> lstProductionDetailsObj = listTransactionDetailsByTranId(invTranProduction.id)
            if (lstProductionDetailsObj.size() < 0) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            // Checking Availability of item(s) for consumption
            for (int i = 0; i < lstConsumptionDetailsObj.size(); i++) {
                String errMsg = checkAvailableStock(lstConsumptionDetailsObj[i])
                if (errMsg) {
                    result.put(Tools.MESSAGE, errMsg)
                    return result
                }
            }

            result.put(LST_CONSUMPTION_DETAILS_OBJ, lstConsumptionDetailsObj)
            result.put(LST_PRODUCTION_DETAILS_OBJ, lstProductionDetailsObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     *  Method to approve parents(InvInventoryTransaction) & children(InvInventoryTransactionDetails) of both Consumption and Production
     *
     * @Params parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains children(InvInventoryTransactionDetails)
     *
     * @Return -map contains isError(true/false)
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            // get login userId
            long userId = invSessionUtil.appSessionUtil.getAppUser().id

            List lstConsumptionDetailsObj = (List) preResult.get(LST_CONSUMPTION_DETAILS_OBJ)
            List lstProductionDetailsObj = (List) preResult.get(LST_PRODUCTION_DETAILS_OBJ)

            boolean updateStatus = approveInvProdWithCons(lstConsumptionDetailsObj, lstProductionDetailsObj, userId)
            if (!updateStatus) {
                result.put(Tools.MESSAGE, APPROVE_FAILURE_MESSAGE)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to approve inventory production')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * @Params obj -N/A
     * @Return -a map containing approve-success message to show on UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, APPROVE_SUCCESS_MESSAGE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to approve production-with-consumption
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, APPROVE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     *  Method to check AvailableStock of raw materials for consume(at approval)
     * @Params oldRawDetails -old invTransactionDetails object which will be approved
     * @Return -if stock is available to consume at approval then return null otherwise return specific message
     */
    private String checkAvailableStock(Object oldRawDetails) {
        long itemId = Long.parseLong(oldRawDetails.item_id.toString())
        long inventoryId = Long.parseLong(oldRawDetails.inventory_id.toString())
        double actualQuantity = Double.parseDouble(oldRawDetails.actual_quantity.toString())
        double availableStockQuantity = getAvailableStock(inventoryId, itemId)
        if (actualQuantity > availableStockQuantity) {
            Item item = (Item) itemCacheUtility.read(itemId)
            return "Item ${item.name} is not available in stock"
        }
        return null
    }

    /**
     *  Method to get Children(InvInventoryTransactionDetails object) By parentId(InvTransactionId)
     * @Params invTransactionId -parentId(InvTransactionId)
     * @Return -List<GroovyRowResult>
     */
    private List<GroovyRowResult> listTransactionDetailsByTranId(long invTransactionId) {
        String queryStr = """
            SELECT id, actual_quantity, inventory_id, inventory_transaction_id, inventory_type_id,
                   item_id, rate, overhead_cost, transaction_details_id,transaction_type_id, approved_by, approved_on
            FROM inv_inventory_transaction_details
            WHERE inventory_transaction_id = ${invTransactionId}
        """
        List<GroovyRowResult> result = executeSelectSql(queryStr)
        return result
    }

    /**
     *  Method to approve inv_inventory_transaction_details (at the time of approval)
     *
     * @Params lstConsumptionDetailsObj -list of consumption-details-object
     * @Params lstProductionDetailsObj -list of production-details-object
     * @Params userId -AppUser.id(loginUserId)
     *
     * @Return -if all transaction occurs successfully the return true otherwise return false
     */
    public boolean approveInvProdWithCons(List<Object> lstConsumptionDetailsObj, List<Object> lstProductionDetailsObj, Long userId) {

        // First update parent as approved
        // Get the first child to find the parent
        Object firstConsumptionDetails = (Object) lstConsumptionDetailsObj[0]
        long consumptionId = (long) firstConsumptionDetails.inventory_transaction_id
        Object firstProductionDetails = (Object) lstProductionDetailsObj[0]
        long productionId = (long) firstProductionDetails.inventory_transaction_id

        // update both parent(SET is_approved ='true' at TransactionConsumption and TransactionProduction)
        updateProductionWithConsumptionForApproved(consumptionId, productionId)

        // get total cost for row material and set rate and then approve
        double consumptionCost = 0.0d
        for (int i = 0; i < lstConsumptionDetailsObj.size(); i++) {
            long consumptionDetailsId = lstConsumptionDetailsObj[i].id
            long inventoryId = lstConsumptionDetailsObj[i].inventory_id
            long itemId = lstConsumptionDetailsObj[i].item_id
            double actualQuantity = lstConsumptionDetailsObj[i].actual_quantity

            //get consumptionRate by calculatingValuation
            double consumptionRate = calculateValuationForOut(actualQuantity, inventoryId, itemId)

            consumptionCost = consumptionCost + (actualQuantity * consumptionRate)

            //set rate for row materials(Consumption)
            updateConsWithProdDetailsRate(consumptionDetailsId, userId, consumptionRate)
        }

        //New Logic to get Production Rate
        double totalProductionQuantity = 0.0d
        double productionCost = 0.0d
        for (int i = 0; i < lstProductionDetailsObj.size(); i++) {
            double productionQuantity = lstProductionDetailsObj[i].actual_quantity
            totalProductionQuantity = totalProductionQuantity + productionQuantity
        }
        //calculate Production Rate
        double productionRate = consumptionCost / totalProductionQuantity
        //Set property for approval at production details
        for (int i = 0; i < lstProductionDetailsObj.size(); i++) {
            long productionDetailsId = lstProductionDetailsObj[i].id
            double itemWiseRate = productionRate + lstProductionDetailsObj[i].overhead_cost
            //set rate for row materials(Production)
            updateConsWithProdDetailsRate(productionDetailsId, userId, itemWiseRate)
        }
        return true
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

    /**
     * Returns list of groovyRowResult to calculate fifoQuantity of an item
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return List < GroovyRowResult >
     */
    private List<GroovyRowResult> getListForFIFO(long inventoryId, long itemId) {

        String queryStr = """
                SELECT itd.id, itd.version, itd.actual_quantity, itd.fifo_quantity, itd.rate, itd.transaction_type_id
                FROM inv_inventory_transaction_details itd
                WHERE
                itd.is_increase = true
                AND itd.approved_by > 0
                AND itd.item_id = ${itemId}
                AND itd.inventory_id = ${inventoryId}
                AND itd.fifo_quantity < itd.actual_quantity
                ORDER BY itd.approved_on ASC
            """

        List<GroovyRowResult> lstFifo = executeSelectSql(queryStr)
        return lstFifo
    }

    /**
     * Method to update fifo_quantity of invTransactionDetails object(children)
     * @Param inventoryTransactionDetails -InventoryTransactionDetails object (child)
     * @Return int value(if return value <=0 : throw exception to rollback all DB transaction)
     */
    private int updateFifoCount(GroovyRowResult inventoryTransactionDetails) {
        String queryStr = """
        UPDATE inv_inventory_transaction_details
        SET
            version = version+1,
            fifo_quantity = ${inventoryTransactionDetails.fifo_quantity}
        WHERE
            id = ${inventoryTransactionDetails.id}
            AND version = ${inventoryTransactionDetails.version}
        """
        int updateCount = executeUpdateSql(queryStr)
        if (updateCount > 0) {
            inventoryTransactionDetails.version = inventoryTransactionDetails.version + 1
        } else {
            throw new RuntimeException('Failed to update FIFO count')
        }
        return updateCount
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

    /**
     * Returns list of groovyRowResult to calculate lifoQuantity of an item
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return List < GroovyRowResult >
     */
    private List<GroovyRowResult> getListForLIFO(long inventoryId, long itemId) {

        String queryStr = """
                SELECT itd.id, itd.version, itd.actual_quantity, itd.lifo_quantity, itd.rate, itd.transaction_type_id
                FROM inv_inventory_transaction_details itd
                WHERE
                itd.is_increase = true
                AND itd.approved_by > 0
                AND itd.item_id = ${itemId}
                AND itd.inventory_id = ${inventoryId}
                AND itd.lifo_quantity < itd.actual_quantity
                ORDER BY itd.approved_on DESC
            """

        List<GroovyRowResult> lstLifo = executeSelectSql(queryStr)
        return lstLifo
    }

    /**
     * Method to update lifo_quantity of invTransactionDetails object(children)
     * @Param inventoryTransactionDetails -InventoryTransactionDetails object (child)
     * @Return int value(if return value <=0 : throw exception to rollback all DB transaction)
     */
    private int updateLifoCount(GroovyRowResult inventoryTransactionDetails) {
        String queryStr = """
        UPDATE inv_inventory_transaction_details
        SET
            version = version+1,
            lifo_quantity = ${inventoryTransactionDetails.lifo_quantity}
        WHERE
            id = ${inventoryTransactionDetails.id}
            AND version = ${inventoryTransactionDetails.version}
        """
        int updateCount = executeUpdateSql(queryStr)
        if (updateCount > 0) {
            inventoryTransactionDetails.version = inventoryTransactionDetails.version + 1
        } else {
            throw new RuntimeException('Failed to update LIFO count')
        }
        return updateCount
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

    /**
     * Method returns available_stock of an item in an inventory using view(vw_inv_inventory_stock)
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return double value
     */
    private double getAvailableStock(long inventoryId, long itemId) {
        String queryStr = """
        SELECT available_stock FROM vw_inv_inventory_stock
        WHERE inventory_id= ${inventoryId} AND item_id = ${itemId}
        """
        List<GroovyRowResult> result = executeSelectSql(queryStr)
        if (result.size() > 0) {
            return (double) result[0].available_stock
        }
        return 0.0d
    }

    /**
     * Method to set rate, approved_by, approved_on in transactionDetails object(children)
     * @Param id -InventoryTransaction.id (Parent id)
     * @Param userId -AppUse.id (login userId)
     * @Param rate -calculated production-item rate
     *
     * @Return int value(if return value <=0 : throw exception to rollback all DB transaction)
     */
    private int updateConsWithProdDetailsRate(long id, long userId, double rate) {
        String queryUpdate = """
                    UPDATE inv_inventory_transaction_details
                        SET  rate = ${rate},
                             approved_by = ${userId},
                             approved_on = '${DateUtility.getDBDateFormatWithSecond(new Date())}'
                        WHERE id = (${id})
                    """
        int updateCount = executeUpdateSql(queryUpdate)
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to update approve transaction details")
        }
        return updateCount
    }

    /**
     * Method used to approve production & consumption (parent object)
     * @Param consumpId -InventoryTransaction.id (Parent consumption id)
     * @Param productionId -InventoryTransaction.id (Parent production id)
     *
     * @Return int value(if return value <=0 : throw exception to rollback all DB transaction)
     */
    private int updateProductionWithConsumptionForApproved(long consumpId, long productionId) {
        String queryUpdate = """
                    UPDATE inv_inventory_transaction
                        SET  is_approved ='true'
                        WHERE id IN (${consumpId},${productionId})
                    """
        int updateCount = executeUpdateSql(queryUpdate)
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to update approve transaction")
        }
        return updateCount
    }

    /**
     * Returns list of groovyRowResult to calculate averageQuantity of an item
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return list of GroovyRowResult
     */
    private static final String SELECT_QUERY = """
                SELECT itd.id, itd.actual_quantity, itd.rate, itd.transaction_type_id
                FROM inv_inventory_transaction_details itd
                WHERE
                itd.is_increase = true
                AND itd.approved_by > 0
                AND itd.item_id = :itemId
                AND itd.inventory_id = :inventoryId
                AND ( (itd.fifo_quantity < itd.actual_quantity) OR (itd.lifo_quantity < itd.actual_quantity) )
            """
    private List<GroovyRowResult> getListForAverage(long inventoryId, long itemId) {

        Map queryParams = [
                itemId: itemId,
                inventoryId: inventoryId
        ]
        List<GroovyRowResult> lstAverage = executeSelectSql(SELECT_QUERY, queryParams)
        return lstAverage
    }
}

