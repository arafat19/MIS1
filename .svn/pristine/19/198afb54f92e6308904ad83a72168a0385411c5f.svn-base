package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.application.utility.ValuationTypeCacheUtility
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.entity.InvProductionLineItem
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class for reverse of existing approved InvProductionWithConsumption
 *  For details go through Use-Case doc named 'ReverseAdjustmentForInvProdWithConsumpActionService'
 */
class ReverseAdjustmentForInvProdWithConsumpActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ValuationTypeCacheUtility valuationTypeCacheUtility

    private static final String REVERSE_ADJUST_FAILURE_MSG = "Production has not been reversed."
    private static final String REVERSE_ADJUST_SUCCESS_MSG = "Production has been reversed successfully."
    private static final String PRODUCTION_ITEM_NOT_FOUND = "Production line item not found."
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to reverse production."
    private static final String PRODUCTION_OBJECT_NOT_FOUND = "Production not found."
    private static final String INV_TRANSACTION_CONSUMPTION = "invTransactionConsumption"
    private static final String INV_TRANSACTION_PROD = "invTransactionProd"
    private static final String PRODUCTION_NOT_APPROVED = "Production items are not approved."
    private static final String PRODUCTION_RAW_NOT_FOUND = "Production raw material not found."
    private static final String PRODUCTION_FINISHED_NOT_FOUND = "Production finished material not found."
    private static final String REVERSE_ADJ_COMMENTS_NOT_FOUND = "Reverse adjustment comments not found."
    private static final String LST_CURRENT_CON_DETAILS_SET = "lstCurrentConDetailsSet"
    private static final String LST_CURRENT_PROD_DETAILS_SET = "lstCurrentProdDetailsSet"
    private static final String LST_REVERSE_ADJ_CON_DETAILS_SET = "lstReverseAdjConDetailsSet"
    private static final String LST_REVERSE_ADJ_PROD_DETAILS_SET = "lstReverseAdjProdDetailsSet"
    private static final String DELETED = "deleted"
    private static final String REVERSE_NOT_ALLOWED = "Reverse Adjustment not allowed for this item"

    /**
     * validate different criteria to reverse existing approvedInvProductionWithConsumption. Such as :
     *      Check reverseAdjustmentComments
     *      Check approval
     *      Check existence of oldConsumption object
     *      Check existence of InvProductionLineItem Obj
     *      Check availableAmount to consume etc.
     *
     * Create parent objects(InvTransaction) of InvTranConsumption & invTranProduction,
     * Create children objects(InvInventoryTransactionDetails) of TranDetailsConsumption & TranDetailsProduction,
     *
     * @Params parameters -Receives the serialized parameters send from UI e.g inventoryId, productionLineItemId, productionDate etc.
     * @Params obj -N/A
     *
     * @Return -a map containing all objects necessary for execute (Parents(invTranConsumption & invTranProduction), Children(invTranConsumptionDetails & invTranProductionDetails))
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters

            long consumptionId = Long.parseLong(params.idCon.toString())            // id of inventoryTransaction (CONSUMPTION)

            String adjComments = params.adjComments.toString()
            if (!adjComments || (adjComments.length() == 0)) {
                result.put(Tools.MESSAGE, REVERSE_ADJ_COMMENTS_NOT_FOUND)
                return result
            }
            // check object existence (only consumption)
            InvInventoryTransaction oldConsumption = invInventoryTransactionService.read(consumptionId)
            if (!oldConsumption) {
                result.put(Tools.MESSAGE, PRODUCTION_OBJECT_NOT_FOUND)
                return result
            }
            // Check if all items (in consumption) are approved
            int approveCount = getApproveCountForProdWithCons(consumptionId)
            if (oldConsumption.itemCount != approveCount) {
                result.put(Tools.MESSAGE, PRODUCTION_NOT_APPROVED)
                return result
            }
            InvInventoryTransaction oldProduction = invInventoryTransactionService.readByTransactionId(oldConsumption.id)

            // check line item
            Long prodLineItemId = oldConsumption.invProductionLineItemId
            InvProductionLineItem productionLineItem = (InvProductionLineItem) invProductionLineItemCacheUtility.read(prodLineItemId)
            if (!productionLineItem) {
                result.put(Tools.MESSAGE, PRODUCTION_ITEM_NOT_FOUND)
                return result
            }

            // Read the Grid Objects for Consumption
            JSONElement gridModelRaw = JSON.parse(params.gridModelRawMaterial.toString())
            List lstRawMaterialsRows = (List) gridModelRaw.rows

            List lstCurrentConDetailsSet = []
            List lstReverseAdjConDetailsSet = []

            for (int i = 0; i < lstRawMaterialsRows.size(); i++) {
                Object eachDetails = lstRawMaterialsRows[i]

                long transactionDetailsId = Long.parseLong(eachDetails.cell[4].toString())

                InvInventoryTransactionDetails currentConsumptionDetails = invInventoryTransactionDetailsService.read(transactionDetailsId)
                if (!currentConsumptionDetails) {
                    result.put(Tools.MESSAGE, PRODUCTION_RAW_NOT_FOUND)
                    return result
                }

                Item item = (Item) itemCacheUtility.read(currentConsumptionDetails.itemId)
                if (item.isIndividualEntity) {
                    result.put(Tools.MESSAGE, REVERSE_NOT_ALLOWED)
                    return result
                }

                //Do reverse for all Consumption
                InvInventoryTransactionDetails reverseAdjConsumptionDetails = copyForReverseAdjustment(currentConsumptionDetails, adjComments, invSessionUtil.appSessionUtil.getAppUser())
                currentConsumptionDetails.updatedOn = new Date()
                currentConsumptionDetails.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
                currentConsumptionDetails.isCurrent = false

                lstCurrentConDetailsSet << currentConsumptionDetails
                lstReverseAdjConDetailsSet << reverseAdjConsumptionDetails
            }

            oldConsumption.comments = adjComments      // comment will be copied to all adjusted/Rev.Adjusted children & 2 parents
            oldConsumption.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
            oldConsumption.updatedOn = new Date()

            // Read the Grid Objects for Production
            JSONElement gridModelFinished = JSON.parse(params.gridModelFinishedProduct.toString())
            List lstFinishedMaterialsRows = (List) gridModelFinished.rows

            List lstCurrentProdDetailsSet = []
            List lstReverseAdjProdDetailsSet = []

            for (int i = 0; i < lstFinishedMaterialsRows.size(); i++) {
                Object eachDetails = lstFinishedMaterialsRows[i]

                long transactionDetailsId = Long.parseLong(eachDetails.cell[4].toString())

                InvInventoryTransactionDetails currentProductionDetails = invInventoryTransactionDetailsService.read(transactionDetailsId)
                if (!currentProductionDetails) {
                    result.put(Tools.MESSAGE, PRODUCTION_FINISHED_NOT_FOUND)
                    return result
                }

                Item item = (Item) itemCacheUtility.read(currentProductionDetails.itemId)
                if (item.isIndividualEntity) {
                    result.put(Tools.MESSAGE, REVERSE_NOT_ALLOWED)

                    return result
                }

                //Check available stock
                String msg = checkAvailableStockForProduction(currentProductionDetails, item)
                if (msg) {
                    result.put(Tools.MESSAGE, msg)
                    return result
                }
                // old details found & stock is available. Do reverse for all production
                InvInventoryTransactionDetails reverseAdjProductionDetails = copyForReverseAdjustment(currentProductionDetails, adjComments, invSessionUtil.appSessionUtil.getAppUser())

                currentProductionDetails.updatedOn = new Date()
                currentProductionDetails.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
                currentProductionDetails.isCurrent = false

                lstCurrentProdDetailsSet << currentProductionDetails
                lstReverseAdjProdDetailsSet << reverseAdjProductionDetails
            }

            oldProduction.comments = adjComments     // comment will be copied to all adjusted/Rev.Adjusted children & 2 parents
            oldProduction.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
            oldProduction.updatedOn = new Date()

            result.put(INV_TRANSACTION_CONSUMPTION, oldConsumption)
            result.put(INV_TRANSACTION_PROD, oldProduction)

            result.put(LST_CURRENT_CON_DETAILS_SET, lstCurrentConDetailsSet)
            result.put(LST_CURRENT_PROD_DETAILS_SET, lstCurrentProdDetailsSet)

            result.put(LST_REVERSE_ADJ_CON_DETAILS_SET, lstReverseAdjConDetailsSet)
            result.put(LST_REVERSE_ADJ_PROD_DETAILS_SET, lstReverseAdjProdDetailsSet)

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     *  Method to reverse children(InvInventoryTransactionDetails) of both Consumption and Production
     *
     * @Params parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains Parents(invTranConsumption & invTranProduction), children(InvInventoryTransactionDetails)
     *
     * @Return -map contains isError(true/false)
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap preResult = (LinkedHashMap) obj

            InvInventoryTransaction invTransactionCon = (InvInventoryTransaction) preResult.get(INV_TRANSACTION_CONSUMPTION)
            List lstCurrentConDetailsSet = (List) preResult.get(LST_CURRENT_CON_DETAILS_SET)
            List lstReverseAdjConDetailsSet = (List) preResult.get(LST_REVERSE_ADJ_CON_DETAILS_SET)

            InvInventoryTransaction invTransactionProd = (InvInventoryTransaction) preResult.get(INV_TRANSACTION_PROD)
            List lstCurrentProdDetailsSet = (List) preResult.get(LST_CURRENT_PROD_DETAILS_SET)
            List lstReverseAdjProdDetailsSet = (List) preResult.get(LST_REVERSE_ADJ_PROD_DETAILS_SET)

            //reverse inv-production-with-consumption-transaction
            boolean success = reverseAdjustForProductionWithConsumption(invTransactionCon, invTransactionProd,
                    lstCurrentConDetailsSet, lstReverseAdjConDetailsSet, lstCurrentProdDetailsSet, lstReverseAdjProdDetailsSet)
            if (!success) {
                result.put(Tools.MESSAGE, REVERSE_ADJUST_FAILURE_MSG)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to reverse adjustment of inventory production')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REVERSE_ADJUST_FAILURE_MSG)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * to show confirmation message that reverse operation has been done
     * @Params obj -N/A
     * @Return -a map containing confirmation message
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(DELETED, Boolean.TRUE.booleanValue())
            result.put(Tools.MESSAGE, REVERSE_ADJUST_SUCCESS_MSG)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REVERSE_ADJUST_FAILURE_MSG)
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to reverse approved-production-with-consumption
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, REVERSE_ADJUST_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REVERSE_ADJUST_FAILURE_MSG)
            return result
        }
    }

    /**
     * if production quantity decrease : Check consumable stock
     *
     * @Param eachRelatedAdjustments -map contains oldInvTransactionProductionDetails and adjustedInvTransactionProductionDetails
     * @Param item -Item object
     *
     * @Return -if new adjusted quantity is available then return null; otherwise return specific message
     */
    private String checkAvailableStockForProduction(InvInventoryTransactionDetails oldDetails, Item item) {
        double availableStockQuantity = getAvailableStock(oldDetails.inventoryId, oldDetails.itemId)
        if (availableStockQuantity < oldDetails.actualQuantity) {
            return "Item ${item.name} is not available in stock"
        }
        return null
    }

    private static final String APP_COUNT_QUERY = """
        SELECT COUNT(COALESCE(id,0)) AS count FROM inv_inventory_transaction_details
        WHERE inventory_transaction_id=:inventoryTransactionId AND
        approved_by > 0 AND is_current = true """

    /**
     * to get count of approved-child of an inventory-production-transaction
     * @Param inventoryTransactionId -InvInventoryTransaction.id
     * @Return int value
     */
    private int getApproveCountForProdWithCons(long inventoryTransactionId) {
        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId
        ]
        List<GroovyRowResult> countResult = executeSelectSql(APP_COUNT_QUERY, queryParams)
        int total = countResult[0].count
        return total
    }

    /**
     *  Method to Reverse Adjustment of children(InvInventoryTransactionDetails) of both Consumption and Production
     *
     * @Params invTransactionCon -InvInventoryTransaction(parent)
     * @Params invTransactionProd -InvInventoryTransaction(parent)
     * @Params lstCurrentConDetailsSet -list of InvInventoryTransactionConsumptionDetails(children) which will be reversed
     * @Params lstReverseAdjConDetailsSet -list of reversed adjusted InvInventoryTransactionConsumptionDetails(children)
     * @Params lstCurrentProdDetailsSet -list of InvInventoryTransactionProductionDetails(children)
     * @Params lstReverseAdjProdDetailsSet -list of reversed adjusted InvInventoryTransactionProductionDetails(children)
     *
     * @Return -if all transaction occurs successfully the return true otherwise rollback all Db transaction
     */
    public boolean reverseAdjustForProductionWithConsumption(InvInventoryTransaction invTransactionCon,
                                                             InvInventoryTransaction invTransactionProd,
                                                             List<InvInventoryTransactionDetails> lstCurrentConDetailsSet,
                                                             List<InvInventoryTransactionDetails> lstReverseAdjConDetailsSet,
                                                             List<InvInventoryTransactionDetails> lstCurrentProdDetailsSet,
                                                             List<InvInventoryTransactionDetails> lstReverseAdjProdDetailsSet) {
        int i

        updateForProductionWithConsumption(invTransactionCon)
        for (i = 0; i < lstCurrentConDetailsSet.size(); i++) {
            InvInventoryTransactionDetails returnConDetailsReverseAdj = createReverseAdjustmentTransaction(invTransactionCon, lstReverseAdjConDetailsSet[i], lstCurrentConDetailsSet[i])
        }

        updateForProductionWithConsumption(invTransactionProd)
        for (i = 0; i < lstCurrentProdDetailsSet.size(); i++) {
            InvInventoryTransactionDetails returnProdDetailsReverseAdj = createReverseAdjustmentTransaction(invTransactionProd, lstReverseAdjProdDetailsSet[i], lstCurrentProdDetailsSet[i])
        }
        return true
    }

    /**
     * Method use to create reversed invTransactionDetails
     * @Param invInventoryTransaction -InvInventoryTransaction(parent)
     * @Param reverseAdjDetails -InvInventoryTransactionDetails(child)
     * @Param currentTransactionDetails -which transactionDetails is going to be reversed (InvInventoryTransactionDetails(child))
     *
     * @Return -if all transaction occurs successfully the return newReversedInvInventoryTransactionDetails otherwise throw exception to rollback all DB transaction
     */
    private InvInventoryTransactionDetails createReverseAdjustmentTransaction(InvInventoryTransaction invInventoryTransaction, InvInventoryTransactionDetails reverseAdjDetails,
                                                                              InvInventoryTransactionDetails currentTransactionDetails) {
        InvInventoryTransactionDetails newReverseAdjDetails = null
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypePro = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        switch (invInventoryTransaction.transactionTypeId) {
            case transactionTypeCons.id:
                newReverseAdjDetails = invInventoryTransactionDetailsService.create(reverseAdjDetails)
                break;
            case transactionTypePro.id:
                reverseAdjDetails.rate = calculateValuationForOut(reverseAdjDetails.actualQuantity, reverseAdjDetails.inventoryId, reverseAdjDetails.itemId)
                newReverseAdjDetails = invInventoryTransactionDetailsService.create(reverseAdjDetails)
                break;
            default:
                throw new RuntimeException("Fail occurred at invInventoryTransactionDetailsService.createReverseAdjustmentTransaction")
        }

        if (newReverseAdjDetails) {
            int updateInvInventoryTransaction = setIsCurrentTransaction(currentTransactionDetails)  //set isCurrent = false
        } else {
            throw new RuntimeException("Fail occurred at invInventoryTransactionDetailsService.createReverseAdjustmentTransaction")
        }

        //decrease itemCount of parent transaction
        decreaseItemCount(invInventoryTransaction.id)

        return newReverseAdjDetails
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

    private static final String LIST_FIFO_QUERY = """
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
     * Returns the rate for FIFO measurement
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     * @Param outQuantity -total consumed quantity
     *
     * @Return double value
     */
    private List<GroovyRowResult> getListForFIFO(long inventoryId, long itemId) {
        Map queryParams = [
                itemId: itemId,
                inventoryId: inventoryId
        ]
        List<GroovyRowResult> lstFifo = executeSelectSql(LIST_FIFO_QUERY, queryParams)
        return lstFifo
    }

    private static final String FIFO_COUNT_QUERY = """
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
        int updateCount = executeUpdateSql(FIFO_COUNT_QUERY, queryParams)
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

    private static final String GET_LIST_LIFO_QUERY = """
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
        List<GroovyRowResult> lstLifo = executeSelectSql(GET_LIST_LIFO_QUERY, queryParams)
        return lstLifo
    }

    private static final String LIFO_COUNT_QUERY = """
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
        int updateCount = executeUpdateSql(LIFO_COUNT_QUERY, queryParams)
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

    private static final String GET_LIST_AVERAGE_QUERY = """
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
     * Returns the rate for Average measurement
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     * @Param outQuantity -total consumed quantity
     *
     * @Return double value
     */
    private List<GroovyRowResult> getListForAverage(long inventoryId, long itemId) {
        Map queryParams = [
                itemId: itemId,
                inventoryId: inventoryId
        ]
        List<GroovyRowResult> lstAverage = executeSelectSql(GET_LIST_AVERAGE_QUERY, queryParams)
        return lstAverage
    }

    private static final String AVAILABLE_STOCK_QUERY = """
        SELECT available_stock FROM vw_inv_inventory_stock
        WHERE inventory_id=:inventoryId
        AND item_id=:itemId
        """
    /**
     * Method returns Available_Stock-Amount of an item in an inventory using view(vw_inv_inventory_stock)
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return double value
     */
    private double getAvailableStock(long inventoryId, long itemId) {
        Map queryParams = [
                itemId: itemId,
                inventoryId: inventoryId
        ]
        List<GroovyRowResult> result = executeSelectSql(AVAILABLE_STOCK_QUERY, queryParams)
        if (result.size() > 0) {
            return (double) result[0].available_stock
        }
        return 0.0d
    }

    private static final String UPDATE_QUERY = """
                      UPDATE inv_inventory_transaction_details
                      SET is_current=false,
                      version=version+1,
                      updated_by =:updatedBy,
                      updated_on =:updatedOn
                      WHERE id = :id
                      """
    /**
     * Method to set isCurrent = false at inv_inventory_transaction_details (child)
     * @Param inventoryTransactionDetails -InventoryTransactionDetails object (child)
     * @Return int value(if return value <=0 : throw exception to rollback all DB transaction)
     */
    private int setIsCurrentTransaction(InvInventoryTransactionDetails invInventoryTransactionDetails) {

        Map queryParams = [
                id: invInventoryTransactionDetails.id,
                version: invInventoryTransactionDetails.version,
                updatedBy: invInventoryTransactionDetails.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(invInventoryTransactionDetails.updatedOn)
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update inventory transaction details')
        }
        return updateCount
    }

    private static final String UPDATE_TRANS_QUERY = """
                      UPDATE inv_inventory_transaction SET
                          comments=:comments,
                          updated_on=:updatedOn,
                          transaction_date=:transactionDate,
                          updated_by= :updatedBy,
                          item_count= :itemCount,
                          version=version + 1
                      WHERE
                          id=:id AND
                          version=:version
                     """
    /**
     * Method to update InvInventoryTransaction(parent)
     * @Param invInventoryTransaction -InventoryTransaction object
     * @Return int value(if return value <=0 : throw exception to rollback all DB transaction)
     */
    private Integer updateForProductionWithConsumption(InvInventoryTransaction invInventoryTransaction) {

        Map queryParams = [
                id: invInventoryTransaction.id,
                version: invInventoryTransaction.version,
                updatedBy: invInventoryTransaction.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(invInventoryTransaction.updatedOn),
                transactionDate: DateUtility.getSqlDate(invInventoryTransaction.transactionDate),
                comments: invInventoryTransaction.comments,
                itemCount: invInventoryTransaction.itemCount
        ]

        int updateCount = executeUpdateSql(UPDATE_TRANS_QUERY, queryParams);
        if (updateCount <= 0) throw new RuntimeException('Failed to update Inventory Transaction')
        invInventoryTransaction.version = invInventoryTransaction.version + 1
        return (new Integer(updateCount))
    }

    private static final String DECR_ITM_COUNT_QUERY = """
        UPDATE inv_inventory_transaction
        SET
            item_count = item_count - 1,
            version=version+1
        WHERE
            id=:inventoryTransactionId
    """

    /**
     * method decrease item count
     * @Param inventoryTransactionId -InvInventoryTransaction.id (Parent object)
     * @Return int value(if return value <=0 : throw exception to rollback all DB transaction)
     */
    private int decreaseItemCount(long inventoryTransactionId) {
        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId
        ]
        int updateCount = executeUpdateSql(DECR_ITM_COUNT_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to decrease item count")
        }
        return updateCount
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

