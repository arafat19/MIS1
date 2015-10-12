package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.application.utility.ValuationTypeCacheUtility
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class for recalculate all inventory valuation
 *  For details go through Use-Case doc named 'RecalculateAllInvInventoryValuationActionService'
 */
class RecalculateAllInvInventoryValuationActionService extends BaseService implements ActionIntf {

    InvInventoryTransactionService invInventoryTransactionService
    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ValuationTypeCacheUtility valuationTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String SUCCESS_MESSAGE = "All valuation have been re-calculated successfully"
    private static final String FAILURE_MESSAGE = "Failed to re-calculate valuation"
    private static final String ACCESS_DENIED = "Only development-privileged user can perform this action"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * check if user has development-uer ROLE or not.
     *          Only development user can calculate valuation
     * @param params -N/A
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!invSessionUtil.appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.MESSAGE, ACCESS_DENIED)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Method to calculate inventory valuation of which inventoryTransaction is approved
     *      1) Set fifo_quantity = 0 & lifo_quantity = 0 of all approved inventoryTransaction(s)
     *      2) Pull approved invTranDetailsList FROM InvInventoryTransactionDetails table
     *              EXCEPT transaction(s) of Inventory-In-From-Supplier
     *      3) For each individual inventoryTransactionDetails : calculate item valuation
     *
     * @param parameters -N/A
     * @param obj -N/A
     * @return -a map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            // Set fifo_quantity = 0 & lifo_quantity = 0 of all approved inventoryTransaction(s)
            setAllZero()

            //get all approved invTranDetailsList EXCEPT transaction(s) of Inventory-In-From-Supplier
            List<GroovyRowResult> lstTransactionDetails = (List<GroovyRowResult>) getAllFromTransactionDetails()

            int lstSize = lstTransactionDetails.size()
            boolean success = true
            for (int i = 0; i < lstSize; i++) {
                InvInventoryTransactionDetails transactionDetails = buildInvTransactionDetails(lstTransactionDetails[i])
                success = processReCalculateValuation(transactionDetails) //process valuation calculation
                if (!success) {
                    break
                }
            }
            result.put(Tools.IS_ERROR, new Boolean(!success))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException("Fail to recalculate all inventory valuation")
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * @param obj -N/A
     * @return -valuation success message to show on UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE);
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            log.error(ex.getMessage());
            return result
        }
    }

    private static final String INV_INVENTORY_TRANSACTION_DETAILS_SELECT_QUERY = """
            SELECT iitd.id, iitd.version, iitd.inventory_transaction_id, iitd.transaction_type_id, iitd.item_id, iitd.rate, iitd.transaction_details_id,
                   iitd.overhead_cost, iitd.adjustment_parent_id, iitd.actual_quantity, iitd.fifo_quantity, iitd.lifo_quantity, iitd.approved_on, iitd.inventory_id
            FROM inv_inventory_transaction_details iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
            WHERE iitd.approved_by > 0
            AND iit.company_id = :companyId
            AND iitd.id NOT IN (
                SELECT id FROM vw_inv_inventory_transaction_with_details
                WHERE approved_by > 0 AND
                      transaction_type_id =:transactionTypeId AND
                      transaction_entity_type_id =:transactionEntityTypeId AND
                      company_id = :companyId)
            ORDER BY approved_on ASC
    """

    /**
     * Get approved invTransactionDetailsList EXCEPT transactionType :In-From_Supplier
     *      -Only pull that fields those are required to calculate valuation
     * @return -list of invTransactionDetailsObject(GroovyRawResult)
     */
    private List<GroovyRowResult> getAllFromTransactionDetails() {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)
        Map queryParams = [
                transactionTypeId: transactionTypeIn.id,
                transactionEntityTypeId: transactionEntitySupplier.id,
                companyId : companyId
        ]
        List<GroovyRowResult> result = (List<GroovyRowResult>) executeSelectSql(INV_INVENTORY_TRANSACTION_DETAILS_SELECT_QUERY, queryParams)
        return result
    }

    /**
     * method to calculate valuation of single invTransactionDetails
     * @param transactionDetails -InvInventoryTransactionDetails
     * @return -if valuation successfully completed then return true otherwise return false
     */
    private boolean processReCalculateValuation(InvInventoryTransactionDetails transactionDetails) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionTypeOut = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_OUT, companyId)
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)
        SystemEntity transactionTypePro = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)
        SystemEntity transactionTypeAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_ADJUSTMENT, companyId)
        SystemEntity transactionTypeReAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_REVERSE_ADJUSTMENT, companyId)

        boolean valuationSuccess = false
        switch (transactionDetails.transactionTypeId) {
            case transactionTypeIn.id:                          //In-From-Inventory
                copyRateForIn(transactionDetails)
                break
            case transactionTypeOut.id:                        //Out-To-Another-Inventory
                calculateValuationForDecrease(transactionDetails)
                break
            case transactionTypeCons.id:                      //Consumption-Against-Budget
                calculateValuationForDecrease(transactionDetails)
                break
            case transactionTypePro.id:                        //Consumption-With-Production
                calculateValuationForProduction(transactionDetails)
                break
            case transactionTypeAdj.id:                           //Adjustment-Of-All-Kind-Of-Transaction
                processForAdjustmentAndReverseAdjustment(transactionDetails)
                break
            case transactionTypeReAdj.id:                         //ReverseAdjustment-Of-All-Kind-Of-Transaction
                processForAdjustmentAndReverseAdjustment(transactionDetails)
                break
            default:   //if any other transactionType found then through exception to rollback all DB transaction
                throw new RuntimeException("Fail occurred at RecalculateAllInvInventoryValuationActionService.processReCalculateValuation")
        }
        valuationSuccess = true
        return valuationSuccess
    }

    /**
     * Common method to calculate valuation for adjustment & reversAdjustment
     * @param transactionDetails -InvInventoryTransactionDetails
     */
    private void processForAdjustmentAndReverseAdjustment(InvInventoryTransactionDetails transactionDetails) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypePro = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)

        if (transactionDetails.isIncrease) { //For Adjustment
            // check if adjustment derived from production
            InvInventoryTransaction adjustmentParent = invInventoryTransactionService.read(transactionDetails.inventoryTransactionId)
            if (adjustmentParent.transactionTypeId == transactionTypePro.id) {
                // @todo: logic for production should be changed after 'immediate-parent' implementation
                // for now assuming that, there is only one production-item and adjustment may occur only once
                InvInventoryTransaction transactionCon = invInventoryTransactionService.read(adjustmentParent.transactionId)

                double totalRawMaterialAmount = getTotalAmountOfRawMaterialForAdjustment(transactionCon.id) //get total consumed amount of raw material
                double totalFinishedMaterialQuantity = transactionDetails.actualQuantity    // assuming only 1 finished product

                double itemRate = (totalRawMaterialAmount / totalFinishedMaterialQuantity) + transactionDetails.overheadCost
                transactionDetails.rate = itemRate.round(2)
                updateRate(transactionDetails)
            } else {
                copyRateFromParent(transactionDetails)
            }
        } else { //For reverseAdjustment
            calculateValuationForDecrease(transactionDetails)
        }
    }

    private static final String UPDATE_QUERY = """
        UPDATE inv_inventory_transaction_details
        SET fifo_quantity = 0,
            lifo_quantity = 0
        WHERE
            approved_by > 0
            AND inventory_transaction_id IN (SELECT id FROM inv_inventory_transaction WHERE company_id = :companyId)
    """

    /**
     *  update all fifo & lifo quantity to zero
     */
    private void setAllZero() {
        Map queryParams = [
                companyId: invSessionUtil.appSessionUtil.getCompanyId()
        ]
        executeUpdateSql(UPDATE_QUERY, queryParams)
    }

    /**
     * Method to calculation valuation in case of item decrease transaction(InvOut, InvConsumption)
     *      1) get itemObject by id
     *      2) get itemRate based on valuation of that item
     * @param trDetails -InvInventoryTransactionDetails
     * @return -N/A
     */
    private int calculateValuationForDecrease(InvInventoryTransactionDetails trDetails) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        // pull valuation type object
        SystemEntity valuationTypeFifoObj = (SystemEntity) valuationTypeCacheUtility.readByReservedAndCompany(valuationTypeCacheUtility.VALUATION_TYPE_FIFO, companyId)
        SystemEntity valuationTypeLifoObj = (SystemEntity) valuationTypeCacheUtility.readByReservedAndCompany(valuationTypeCacheUtility.VALUATION_TYPE_LIFO, companyId)
        SystemEntity valuationTypeAvgObj = (SystemEntity) valuationTypeCacheUtility.readByReservedAndCompany(valuationTypeCacheUtility.VALUATION_TYPE_AVG, companyId)

        double itemRate = 0.0d
        Item item = (Item) itemCacheUtility.read(trDetails.itemId)
        switch (item.valuationTypeId) {
            case valuationTypeFifoObj.id:
                itemRate = getRateForFifo(trDetails)   // get rate , update FIFO count
                break
            case valuationTypeLifoObj.id:
                itemRate = getRateForLifo(trDetails)  // get rate , update LIFO count
                break
            case valuationTypeAvgObj.id:
                itemRate = getRateForAverage(trDetails)         // get rate (quantity is not required here)
                break
            default:
                throw new RuntimeException('Failed to calculateValuationForDecrease due to unrecognized VALUATION_TYPE')
        }
        trDetails.rate = itemRate.round(2) //Set itemRate of invInFormInventoryDetailsObject
        updateRate(trDetails) // update itemRate of invInFormInventoryDetailsObject
    }

    /**
     * Method to copy rate from corresponding Inv-Out-Details-Object & update itemRate
     * @param inDetails -InvInventoryTransactionDetails
     * @return int value (updateCount)
     */
    private int copyRateForIn(InvInventoryTransactionDetails inDetails) {
        //get corresponding invOutDetailsObject
        InvInventoryTransactionDetails outDetails = invInventoryTransactionDetailsService.read(inDetails.transactionDetailsId)
        double itemRate = outDetails.rate //copy out-rate from corresponding invOutDetailsObject
        inDetails.rate = itemRate
        updateRate(inDetails)  //update invInDetailsObject itemRate
    }

    /**
     * Calculate valuation for Production (Set itemRate)
     *      1)get total consumed amount of raw material
     *      2)get total production amount of finished material
     *      3)calculate item rate
     *      4)update item rate
     * @param prodDetails -InvInventoryTransactionDetails
     * @return -N/A
     */
    private void calculateValuationForProduction(InvInventoryTransactionDetails prodDetails) {

        InvInventoryTransaction transactionProd = invInventoryTransactionService.read(prodDetails.inventoryTransactionId)//get production parent object
        InvInventoryTransaction transactionCon = invInventoryTransactionService.read(transactionProd.transactionId)//get consumption parent object

        double totalRawMaterialAmount = getTotalAmountOfRawMaterialForConsumption(transactionCon.id) //get total consumed amount of raw material
        double totalFinishedMaterialQuantity = getTotalQuantityOfFinishedMaterial(transactionProd.id) //get total production amount of finished material

        //Formula : itemRate = (totalRawMaterial / totalFinishedMaterial) + OverheadCost
        double itemRate = (totalRawMaterialAmount / totalFinishedMaterialQuantity) + prodDetails.overheadCost
        prodDetails.rate = itemRate.round(2)
        updateRate(prodDetails)//Set item Rate of finished product
    }

    /**
     * For : Adjustment(+) & ReverseAdjustment(+)
     *      Get itemRate from adjustmentParent and update itemRate
     * @param transactionDetails -InvInventoryTransactionDetails
     */
    private void copyRateFromParent(InvInventoryTransactionDetails transactionDetails) {
        InvInventoryTransactionDetails adjustmentParent = invInventoryTransactionDetailsService.read(transactionDetails.adjustmentParentId)
        transactionDetails.rate = adjustmentParent.rate
        updateRate(transactionDetails)
    }

    /**
     * Returns the rate for FIFO measurement also update fifo count
     * @param transactionDetails -InvInventoryTransactionDetails
     * @return -double value(fifo rate of item)
     */
    private double getRateForFifo(InvInventoryTransactionDetails transactionDetails) {
        double totalFifoAmount = 0.0d
        double inventoryOutQuantity = transactionDetails.actualQuantity     // copy the out quantity in a variable
        double fifoAvailable = 0.0d
        List<GroovyRowResult> lstFIFO = getListForFIFO(transactionDetails)
        for (int i = 0; i < lstFIFO.size(); i++) {
            GroovyRowResult trDetails = (GroovyRowResult) lstFIFO[i] //Each invTransactionDetailsObject
            fifoAvailable = trDetails.actual_quantity - trDetails.fifo_quantity
            if (inventoryOutQuantity > fifoAvailable) {
                inventoryOutQuantity = inventoryOutQuantity - fifoAvailable     //decrease outQuantity by the availableFifo
                trDetails.fifo_quantity = trDetails.actual_quantity             // take the full of actual_quantity as fifo
                updateFifoCount(trDetails)  // save This Inventory Details with updated FIFO count
                totalFifoAmount = totalFifoAmount + (trDetails.rate * fifoAvailable)    // accumulate total fifoAmount for rate calculation
            } else {
                trDetails.fifo_quantity = trDetails.fifo_quantity + inventoryOutQuantity
                updateFifoCount(trDetails)  // save This Inventory Details with updated FIFO count
                totalFifoAmount = totalFifoAmount + (trDetails.rate * inventoryOutQuantity)  // accumulate total fifoAmount for rate calculation
                break
            }
        }
        double unitPriceFifo = totalFifoAmount / transactionDetails.actualQuantity
        return unitPriceFifo.round(2)
    }

    /**
     * Returns the rate for LIFO measurement also update fifo count
     * @param transactionDetails -InvInventoryTransactionDetails
     * @return -double value(lifo rate of item)
     */
    private double getRateForLifo(InvInventoryTransactionDetails transactionDetails) {
        double totalLifoAmount = 0.0d
        double inventoryOutQuantity = transactionDetails.actualQuantity     // copy the out quantity in a variable
        double lifoAvailable = 0.0d
        List<GroovyRowResult> lstLIFO = getListForLIFO(transactionDetails)
        for (int i = 0; i < lstLIFO.size(); i++) {
            GroovyRowResult trDetails = (GroovyRowResult) lstLIFO[i]
            lifoAvailable = trDetails.actual_quantity - trDetails.lifo_quantity
            if (inventoryOutQuantity > lifoAvailable) {
                inventoryOutQuantity = inventoryOutQuantity - lifoAvailable     //decrease outQuantity by the availableLifo
                trDetails.lifo_quantity = trDetails.actual_quantity             // take the full of actual_quantity as lifo
                updateLifoCount(trDetails)  // save This Inventory Details with updated LIFO count
                totalLifoAmount = totalLifoAmount + (trDetails.rate * lifoAvailable)    // accumulate total lifoAmount for rate calculation
            } else {
                trDetails.lifo_quantity = trDetails.lifo_quantity + inventoryOutQuantity
                updateLifoCount(trDetails)  // save This Inventory Details with updated LIFO count
                totalLifoAmount = totalLifoAmount + (trDetails.rate * inventoryOutQuantity)  // accumulate total lifoAmount for rate calculation
                break
            }
        }
        double unitPriceLifo = totalLifoAmount / transactionDetails.actualQuantity
        return unitPriceLifo.round(2)
    }

    /**
     *  Returns the rate for Average measurement
     * @param trDetails -InvInventoryTransactionDetails
     * @return -double value(average rate of item)
     */
    private double getRateForAverage(InvInventoryTransactionDetails trDetails) {
        List<GroovyRowResult> lstAverage = getListForAverage(trDetails)
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

    private static final String FIFO_SELECT_QUERY = """
                SELECT id, version, actual_quantity, fifo_quantity, rate, transaction_type_id
                  FROM inv_inventory_transaction_details
                WHERE
                    is_increase = true
                    AND approved_by > 0
                    AND item_id = :itemId
                    AND inventory_id = :inventoryId
                    AND fifo_quantity < actual_quantity
                    AND approved_on < :approvedOn
                ORDER BY approved_on ASC
            """
    /**
     * get list of approved invTransactionDetailsObj of an item in an inventory
     * @Param trDetails -InvInventoryTransactionDetails
     * @Return -List of GroovyRowResult
     */
    public List<GroovyRowResult> getListForFIFO(InvInventoryTransactionDetails trDetails) {
        Map queryParams = [
                itemId: trDetails.itemId,
                inventoryId: trDetails.inventoryId,
                approvedOn: DateUtility.getSqlDateWithSeconds(trDetails.approvedOn)
        ]
        List<GroovyRowResult> lstFifo = executeSelectSql(FIFO_SELECT_QUERY, queryParams)
        return lstFifo
    }

    private static final String UPDATE_FIFO_COUNT_QUERY = """
        UPDATE inv_inventory_transaction_details
        SET
            version =:newVersion,
            fifo_quantity =:fifoQuantity
        WHERE
            id =:id
            AND version =:version
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

    private static final String LST_LIFO_SELECT_QUERY = """
                SELECT id, version, actual_quantity, lifo_quantity, rate, transaction_type_id
                  FROM inv_inventory_transaction_details
                WHERE
                    is_increase = true
                    AND approved_by > 0
                    AND item_id = :itemId
                    AND inventory_id = :inventoryId
                    AND lifo_quantity < actual_quantity
                    AND approved_on < :approvedOn
                ORDER BY approved_on DESC
            """
    /**
     * get list of approved invTransactionDetailsObj of an item in an inventory
     * @Param trDetails -InvInventoryTransactionDetails
     * @Return -List of GroovyRowResult
     */
    public List<GroovyRowResult> getListForLIFO(InvInventoryTransactionDetails trDetails) {
        Map queryParams = [
                itemId: trDetails.itemId,
                inventoryId: trDetails.inventoryId,
                approvedOn: DateUtility.getSqlDateWithSeconds(trDetails.approvedOn)
        ]
        List<GroovyRowResult> lstLifo = executeSelectSql(LST_LIFO_SELECT_QUERY, queryParams)

        return lstLifo
    }

    private static final String UPDATE_LIFO_COUNT_QUERY = """
        UPDATE inv_inventory_transaction_details
        SET
            version =:newVersion,
            lifo_quantity =:lifoQuantity
        WHERE
            id =:id
            AND version =:version
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

    private static final String LST_AVG_SELECT_QUERY = """
                SELECT id, actual_quantity, rate, transaction_type_id
                FROM inv_inventory_transaction_details
                WHERE
                is_increase = true
                AND approved_by > 0
                AND item_id =:itemId
                AND inventory_id =:inventoryId
                AND approved_on < :approvedOn
            """
    /**
     * get list of approved & isCurrent invTransactionDetailsObj of an item in an inventory
     * @param trDetails -InvInventoryTransactionDetails
     * @return -list of groovyRowResult
     */
    private List<GroovyRowResult> getListForAverage(InvInventoryTransactionDetails trDetails) {
        Map queryParams = [
                inventoryId: trDetails.inventoryId,
                itemId: trDetails.itemId,
                approvedOn: DateUtility.getSqlDateWithSeconds(trDetails.approvedOn)
        ]
        List<GroovyRowResult> lstAverage = executeSelectSql(LST_AVG_SELECT_QUERY, queryParams)
        return lstAverage
    }

    private static final String TOTAL_CONSUMP_QUERY = """
                SELECT SUM(actual_quantity*rate) AS total_amount
                FROM inv_inventory_transaction_details
                WHERE inventory_transaction_id =:invTransactionId
                AND transaction_type_id =:transactionTypeId
    """
    /**
     * Get total amount of consumed rawMaterial in a production
     * @param invTransactionId -InvInventoryTransaction.id (parent id)
     * @return -double value(totalConsumedAmount)
     */
    private double getTotalAmountOfRawMaterialForConsumption(long invTransactionId) {
        // pull transactionType object
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        Map queryParams = [
                invTransactionId: invTransactionId,
                transactionTypeId: transactionTypeCons.id
        ]
        List result = executeSelectSql(TOTAL_CONSUMP_QUERY, queryParams)
        double amount = result[0].total_amount
        return amount
    }

    private static final String SUM_ACTUAL_QUANTITY_RATE_SELECT_QUERY = """
                SELECT SUM(actual_quantity*rate) AS total_amount
                FROM inv_inventory_transaction_details
                WHERE inventory_transaction_id =:invTransactionId
                AND is_current = true
    """
    /**
     * Get total amount of consumed rawMaterial in a transaction
     * @param invTransactionId -InvInventoryTransaction.id (parent id)
     * @return -double value(totalConsumedAmount)
     */
    private double getTotalAmountOfRawMaterialForAdjustment(long invTransactionId) {
        Map queryParams = [
                invTransactionId: invTransactionId
        ]
        List result = executeSelectSql(SUM_ACTUAL_QUANTITY_RATE_SELECT_QUERY, queryParams)
        double amount = result[0].total_amount
        return amount
    }

    private static final String SUM_ACTUAL_QUANTITY_SELECT_QUERY = """
                SELECT SUM(actual_quantity) AS total_quantity
                FROM inv_inventory_transaction_details
                WHERE inventory_transaction_id =:invTransactionId
                AND transaction_type_id =:transactionTypeId"""

    /**
     * Get total amount of consumed finishedMaterial in a production
     * @param invTransactionId -InvInventoryTransaction.id (parent id)
     * @return -double value(totalConsumedAmount)
     */
    private double getTotalQuantityOfFinishedMaterial(long invTransactionId) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypePro = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)

        Map queryParams = [
                invTransactionId: invTransactionId,
                transactionTypeId: transactionTypePro.id
        ]
        List result = executeSelectSql(SUM_ACTUAL_QUANTITY_SELECT_QUERY, queryParams)
        double quantity = result[0].total_quantity
        return quantity
    }

    private static final String UPDATE_RATE_QUERY = """
        UPDATE inv_inventory_transaction_details
        SET
            rate =:rate
        WHERE
            id =:id
        """
    /**
     * Method to update rate of InventoryTransactionDetailsObj
     * @param inventoryTransactionDetails -InvInventoryTransactionDetails
     * @return -int value (update count)
     */
    private int updateRate(InvInventoryTransactionDetails inventoryTransactionDetails) {
        Map queryParams = [
                rate: inventoryTransactionDetails.rate,
                id: inventoryTransactionDetails.id
        ]
        int updateCount = executeUpdateSql(UPDATE_RATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update Rate')
        }
        return updateCount
    }

    /**
     * Build invTransactionDetailsObject from groovyRowResult
     * @param singleTransactionDetails -invInventoryTransactionDetails
     * @return -InvInventoryTransactionDetails object
     */
    private InvInventoryTransactionDetails buildInvTransactionDetails(GroovyRowResult singleTransactionDetails) {

        InvInventoryTransactionDetails transactionDetails = new InvInventoryTransactionDetails()
        transactionDetails.id = singleTransactionDetails.id
        transactionDetails.version = singleTransactionDetails.version
        transactionDetails.inventoryTransactionId = singleTransactionDetails.inventory_transaction_id
        transactionDetails.transactionTypeId = singleTransactionDetails.transaction_type_id
        transactionDetails.itemId = singleTransactionDetails.item_id
        transactionDetails.rate = singleTransactionDetails.rate
        transactionDetails.transactionDetailsId = singleTransactionDetails.transaction_details_id
        transactionDetails.overheadCost = singleTransactionDetails.overhead_cost
        transactionDetails.adjustmentParentId = singleTransactionDetails.adjustment_parent_id
        transactionDetails.actualQuantity = singleTransactionDetails.actual_quantity
        transactionDetails.fifoQuantity = singleTransactionDetails.fifo_quantity
        transactionDetails.lifoQuantity = singleTransactionDetails.lifo_quantity
        transactionDetails.approvedOn = singleTransactionDetails.approved_on
        transactionDetails.inventoryId = singleTransactionDetails.inventory_id

        return transactionDetails
    }
}
