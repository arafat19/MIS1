package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.application.utility.ValuationTypeCacheUtility
import com.athena.mis.inventory.entity.*
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.*
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
 *  Class for adjustment of existing approved InvProductionWithConsumption
 *  For details go through Use-Case doc named 'AdjustForInvProductionWithConsumptionActionService'
 */
class AdjustForInvProductionWithConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility
    @Autowired
    InvProductionDetailsCacheUtility invProductionDetailsCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ValuationTypeCacheUtility valuationTypeCacheUtility

    private static final String PRODUCTION_ADJUST_SUCCESS_MSG = "Production has been adjusted successfully"
    private static final String PRODUCTION_ITEM_NOT_FOUND = "Production line item not found"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to adjust production"
    private static final String PRODUCTION_OBJECT_NOT_FOUND = "Production not found"
    private static final String INV_TRANSACTION_CONSUMP = "invTransactionConsump"
    private static final String INV_TRANSACTION_PROD = "invTransactionProd"
    private static final String LST_NEW_PRODUCTION_DETAILS = "lstNewProductionDetails"
    private static final String LST_NEW_CONSUMP_DETAILS = "lstNewConsumpDetails"
    private static final String LST_UN_CHANGED_CONSUMP_DETAILS = "lstUnChangedConsumpDetails"
    private static final String LST_ADJUSTMENT_SET_CONSUMP = "lstAdjustmentSetConsump"
    private static final String LST_ADJUSTMENT_SET_PRODUCTION = "lstAdjustmentSetProduction"
    private static final String GRID_OBJECT = "gridObject"
    private static final String PRODUCTION_NOT_APPROVED = "Production items are not approved."
    private static final String PRODUCTION_RAW_NOT_FOUND = "Production raw material not found."
    private static final String PRODUCTION_FINISHED_NOT_FOUND = "Production finished material not found."
    private static final String ADJ_COMMENTS_NOT_FOUND = "Adjustment comments not found"
    private static final String ADJUSTMENT_NOT_ALLOWED = "Adjustment not allowed for this item"
    private static final String PRODUCTION_ADJUST_FAILURE_MSG = "Production has not been adjusted"

    /**
     * validate different criteria to adjust existing approvedInvProductionWithConsumption. Such as :
     *      Check adjustmentComments
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
                result.put(Tools.MESSAGE, ADJ_COMMENTS_NOT_FOUND)
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
            Long lineItemId = Long.parseLong(params.productionLineItemId.toString())
            InvProductionLineItem productionLineItem = (InvProductionLineItem) invProductionLineItemCacheUtility.read(lineItemId)
            if (!productionLineItem) {
                result.put(Tools.MESSAGE, PRODUCTION_ITEM_NOT_FOUND)
                return result
            }

            List<InvInventoryTransactionDetails> lstNewConsumpDetails = []         // for new raw materials (consumption)
            List<InvInventoryTransactionDetails> lstUnChangedConsumpDetails = []   // for unchanged raw materials (consumption) ; used to calculate prod rate
            List lstAdjustmentSetConsump = []                                      // List of Map [newDetails,reverseDetails,adjustmentDetails]
            List<InvInventoryTransactionDetails> lstNewProductionDetails = []      // for new production
            List lstAdjustmentSetProduction = []                                   // List of Map [newDetails,reverseDetails,adjustmentDetails]

            // Read the Grid Objects for Consumption
            JSONElement gridModelRaw = JSON.parse(params.gridModelRawMaterial.toString())
            List lstRawMaterialsRows = (List) gridModelRaw.rows

            for (int i = 0; i < lstRawMaterialsRows.size(); i++) {
                Map eachRelatedAdjustments
                Object eachDetails = lstRawMaterialsRows[i]

                long itemId = Long.parseLong(eachDetails.cell[0].toString())
                double adjustedQuantity = Double.parseDouble(eachDetails.cell[3].toString())
                long transactionDetailsId = Long.parseLong(eachDetails.cell[4].toString())    // -1 for new details

                if (transactionDetailsId <= 0) {    // New item added in Raw materials
                    Item item = (Item) itemCacheUtility.read(itemId)
                    if (item.isIndividualEntity) {
                        result.put(Tools.MESSAGE, ADJUSTMENT_NOT_ALLOWED)
                        return result
                    }
                    String msg = checkAvailableStockForConsumption(oldConsumption.inventoryId, item, adjustedQuantity)
                    if (msg) {
                        result.put(Tools.MESSAGE, msg)
                        return result
                    }
                    InvInventoryTransactionDetails newConDetails = buildTransactionDetailsConsump(eachDetails, params)
                    newConDetails.inventoryTransactionId = oldConsumption.id
                    lstNewConsumpDetails << newConDetails
                    continue
                }

                InvInventoryTransactionDetails oldConsumpDetails = invInventoryTransactionDetailsService.read(transactionDetailsId)
                if (!oldConsumpDetails) {
                    result.put(Tools.MESSAGE, PRODUCTION_RAW_NOT_FOUND)
                    return result
                }

                Item item = (Item) itemCacheUtility.read(oldConsumpDetails.itemId)
                if (item.isIndividualEntity) {
                    result.put(Tools.MESSAGE, ADJUSTMENT_NOT_ALLOWED)
                    return result
                }

                // old details found. If quantity changes then create adjustment/reverse
                if (oldConsumpDetails.actualQuantity != adjustedQuantity) {
                    InvInventoryTransactionDetails reverseAdjDetails = copyForReverseAdjustment(oldConsumpDetails,adjComments, invSessionUtil.appSessionUtil.getAppUser())
                    InvInventoryTransactionDetails adjDetails = copyForAdjustment(oldConsumpDetails, adjustedQuantity, adjComments, invSessionUtil.appSessionUtil.getAppUser())
                    oldConsumpDetails.updatedOn = new Date()      // object will be updated (isCurrent=false)
                    oldConsumpDetails.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id

                    eachRelatedAdjustments = [oldDetails: oldConsumpDetails, reverseAdjDetails: reverseAdjDetails, adjDetails: adjDetails]
                    String msg = checkAvailableStockForConsumption(eachRelatedAdjustments, item)
                    if (msg) {
                        result.put(Tools.MESSAGE, msg)
                        return result
                    }
                    lstAdjustmentSetConsump << eachRelatedAdjustments
                } else {     // raw material quantity unchanged
                    lstUnChangedConsumpDetails << oldConsumpDetails
                }
            }

            oldConsumption.comments = adjComments      // comment will be copied to all adjusted/Rev.Adjusted children & 2 parents
            oldConsumption.itemCount = oldConsumption.itemCount + lstNewConsumpDetails.size()
            oldConsumption.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
            oldConsumption.updatedOn = new Date()

            // Read the Grid Objects for Production
            JSONElement gridModelFinished = JSON.parse(params.gridModelFinishedProduct.toString())
            List lstFinishedMaterialsRows = (List) gridModelFinished.rows


            for (int i = 0; i < lstFinishedMaterialsRows.size(); i++) {
                Map eachRelatedAdjustments

                Object eachDetails = lstFinishedMaterialsRows[i]

                double adjustedQuantity = Double.parseDouble(eachDetails.cell[3].toString())
                long transactionDetailsId = Long.parseLong(eachDetails.cell[4].toString())    // -1 for new details

                if (transactionDetailsId <= 0) {   // New item added in Finished Products
                    InvInventoryTransactionDetails newProdDetails = buildTransactionDetailsProd(eachDetails, params)
                    newProdDetails.inventoryTransactionId = oldProduction.id
                    lstNewProductionDetails << newProdDetails

                    Item item = (Item) itemCacheUtility.read(newProdDetails.itemId)
                    if (item.isIndividualEntity) {
                        result.put(Tools.MESSAGE, ADJUSTMENT_NOT_ALLOWED)
                        return result
                    }
                    continue
                }

                InvInventoryTransactionDetails oldProductionDetails = invInventoryTransactionDetailsService.read(transactionDetailsId)
                if (!oldProductionDetails) {
                    result.put(Tools.MESSAGE, PRODUCTION_FINISHED_NOT_FOUND)
                    return result
                }

                Item item = (Item) itemCacheUtility.read(oldProductionDetails.itemId)
                if (item.isIndividualEntity) {
                    result.put(Tools.MESSAGE, ADJUSTMENT_NOT_ALLOWED)
                    return result
                }

                // old details found. do reverse for all production (changed Prod + unchanged Prod)
                // unchanged Prod reverse needed to change overall rate
                // intelligent checking avoided for now to avoid programming complexity (e.g. none of raw/finished changed)

                InvInventoryTransactionDetails reverseAdjDetails = copyForReverseAdjustment(oldProductionDetails, adjComments, invSessionUtil.appSessionUtil.getAppUser())
                InvInventoryTransactionDetails adjDetails = copyForAdjustment(oldProductionDetails, adjustedQuantity, adjComments, invSessionUtil.appSessionUtil.getAppUser())

                InvProductionDetails invProductionDetails = (InvProductionDetails) invProductionDetailsCacheUtility.getProdDetailsByLineItemAndItemId(productionLineItem.id, item.id)
                adjDetails.overheadCost = invProductionDetails.overheadCost

                oldProductionDetails.updatedOn = new Date()      // object will be updated (isCurrent=false)
                oldProductionDetails.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id

                eachRelatedAdjustments = [oldDetails: oldProductionDetails, reverseAdjDetails: reverseAdjDetails, adjDetails: adjDetails]

                if (adjustedQuantity < oldProductionDetails.actualQuantity) {    // check availability if quantity decreases
                    String msg = checkAvailableStockForProduction(eachRelatedAdjustments, item)
                    if (msg) {
                        result.put(Tools.MESSAGE, msg)
                        return result
                    }
                }

                lstAdjustmentSetProduction << eachRelatedAdjustments
            }

            oldProduction.comments = adjComments     // comment will be copied to all adjusted/Rev.Adjusted children & 2 parents
            oldProduction.itemCount = oldProduction.itemCount + lstNewProductionDetails.size()
            oldProduction.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
            oldProduction.updatedOn = new Date()

            result.put(INV_TRANSACTION_CONSUMP, oldConsumption)
            result.put(LST_NEW_CONSUMP_DETAILS, lstNewConsumpDetails)
            result.put(LST_UN_CHANGED_CONSUMP_DETAILS, lstUnChangedConsumpDetails)
            result.put(LST_ADJUSTMENT_SET_CONSUMP, lstAdjustmentSetConsump)
            result.put(INV_TRANSACTION_PROD, oldProduction)
            result.put(LST_NEW_PRODUCTION_DETAILS, lstNewProductionDetails)
            result.put(LST_ADJUSTMENT_SET_PRODUCTION, lstAdjustmentSetProduction)
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
     *  Method to adjustment of children(InvInventoryTransactionDetails) of both Consumption and Production
     *
     * @Params parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains Parents(invTranConsumption & invTranProduction), children(InvInventoryTransactionDetails)
     *
     * @Return -map contains isError(true/false), Parents(invTranConsumption & invTranProduction)
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvInventoryTransaction invTransactionCon = (InvInventoryTransaction) preResult.get(INV_TRANSACTION_CONSUMP)
            InvInventoryTransaction invTransactionProd = (InvInventoryTransaction) preResult.get(INV_TRANSACTION_PROD)
            List<InvInventoryTransactionDetails> lstNewConsumpDetails = (List<InvInventoryTransactionDetails>) preResult.get(LST_NEW_CONSUMP_DETAILS)
            List<InvInventoryTransactionDetails> lstUnChangedConsumpDetails = (List<InvInventoryTransactionDetails>) preResult.get(LST_UN_CHANGED_CONSUMP_DETAILS)
            List<InvInventoryTransactionDetails> lstNewProductionDetails = (List<InvInventoryTransactionDetails>) preResult.get(LST_NEW_PRODUCTION_DETAILS)
            List lstAdjustmentSetConsump = (List) preResult.get(LST_ADJUSTMENT_SET_CONSUMP)
            List lstAdjustmentSetProduction = (List) preResult.get(LST_ADJUSTMENT_SET_PRODUCTION)

            //adjust inv-production-with-consumption-transaction
            boolean success = adjustForProductionWithConsumption(
                    invTransactionCon, invTransactionProd, lstNewConsumpDetails, lstUnChangedConsumpDetails, lstNewProductionDetails,
                    lstAdjustmentSetConsump, lstAdjustmentSetProduction)
            if (!success) {
                result.put(Tools.MESSAGE, PRODUCTION_ADJUST_FAILURE_MSG)
                return result
            }
            result.put(INV_TRANSACTION_CONSUMP, invTransactionCon)
            result.put(INV_TRANSACTION_PROD, invTransactionProd)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to adjust inventory transaction')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRODUCTION_ADJUST_FAILURE_MSG)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     *  wrap InvInventoryTransaction for grid
     *
     * @Params obj -Receives map from execute which contains parents(InvInventoryTransaction) objects
     *
     * @Return -a map containing all objects necessary for grid data
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            InvInventoryTransaction inventoryTransactionCon = (InvInventoryTransaction) receiveResult.get(INV_TRANSACTION_CONSUMP)
            InvInventoryTransaction inventoryTransactionProd = (InvInventoryTransaction) receiveResult.get(INV_TRANSACTION_PROD)

            GridEntity object = new GridEntity()
            object.id = inventoryTransactionCon.id

            String transactionDate = DateUtility.getLongDateForUI(inventoryTransactionCon.transactionDate)
            InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryTransactionCon.inventoryId)
            SystemEntity invType = (SystemEntity) invInventoryTypeCacheUtility.read(inventoryTransactionCon.inventoryTypeId)
            InvProductionLineItem lineItem = (InvProductionLineItem) invProductionLineItemCacheUtility.read(inventoryTransactionCon.invProductionLineItemId)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(inventoryTransactionCon.createdBy)
            object.cell = [Tools.LABEL_NEW,
                    inventoryTransactionCon.id,
                    invType.key + Tools.COLON + inventory.name,
                    lineItem.name,
                    inventoryTransactionCon.itemCount,
                    inventoryTransactionProd.itemCount,
                    transactionDate,
                    createdBy.username
            ]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, PRODUCTION_ADJUST_SUCCESS_MSG)
            result.put(GRID_OBJECT, object)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRODUCTION_ADJUST_FAILURE_MSG)
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to adjust approved-production-with-consumption
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
            result.put(Tools.MESSAGE, PRODUCTION_ADJUST_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRODUCTION_ADJUST_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build the child object of approved-consumption (InvInventoryTransactionDetails)
     * @Param gridModelConsumption -Serialized GridObject from UI
     * @Param params -Serialized parameters from UI
     * @Return -InvInventoryTransactionDetails object (consumption)
     */
    private InvInventoryTransactionDetails buildTransactionDetailsConsump(def gridModelConsumption, GrailsParameterMap params) {

        long inventoryId = Long.parseLong(params.inventoryId.toString())
        InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
        // pull transactionType object
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        InvInventoryTransactionDetails transactionDetails = new InvInventoryTransactionDetails()

        transactionDetails.id = 0L
        transactionDetails.version = 0
        transactionDetails.inventoryId = inventory.id
        transactionDetails.inventoryTypeId = inventory.typeId
        transactionDetails.inventoryTransactionId = 0L          // set after parent save
        long itemId = Long.parseLong(gridModelConsumption.cell[0].toString())    //Positional value of Grid object
        Item item = (Item) itemCacheUtility.read(itemId)
        transactionDetails.itemId = item.id
        transactionDetails.vehicleId = 0L
        transactionDetails.vehicleNumber = null
        transactionDetails.suppliedQuantity = Double.parseDouble(gridModelConsumption.cell[3].toString())    //Positional value of Grid object
        transactionDetails.actualQuantity = Double.parseDouble(gridModelConsumption.cell[3].toString())      //Positional value of Grid object
        transactionDetails.shrinkage = 0.0d
        transactionDetails.rate = 0.0d   //set rate at service
        transactionDetails.supplierChalan = null
        transactionDetails.stackMeasurement = null
        transactionDetails.fifoQuantity = 0.0d
        transactionDetails.lifoQuantity = 0.0d
        transactionDetails.acknowledgedBy = 0L
        transactionDetails.createdOn = new Date()                           // consider new entry for save
        transactionDetails.createdBy = invSessionUtil.appSessionUtil.getAppUser().id
        transactionDetails.updatedOn = null                          // consider existing entry for update
        transactionDetails.updatedBy = 0L
        transactionDetails.comments = null
        transactionDetails.mrfNo = null
        transactionDetails.transactionDate = DateUtility.parseMaskedDate(params.transactionDate.toString())
        transactionDetails.transactionDetailsId = 0L

        transactionDetails.transactionTypeId = transactionTypeCons.id
        transactionDetails.adjustmentParentId = 0L
        transactionDetails.approvedOn = new Date()                      // approved in the event of create
        transactionDetails.approvedBy = invSessionUtil.appSessionUtil.getAppUser().id
        transactionDetails.isIncrease = false
        transactionDetails.isCurrent = true
        transactionDetails.overheadCost = 0.0d

        return transactionDetails
    }

    /**
     * Build the child object of approved-production (InvInventoryTransactionDetails)
     * @Param gridModelProduction -Serialized GridObject from UI
     * @Param params -Serialized parameters from UI
     * @Return -InvInventoryTransactionDetails object (production)
     */
    private InvInventoryTransactionDetails buildTransactionDetailsProd(def gridModelProduction, GrailsParameterMap params) {

        long inventoryId = Long.parseLong(params.inventoryId.toString())
        InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypePro = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)

        InvInventoryTransactionDetails transactionDetails = new InvInventoryTransactionDetails()

        transactionDetails.id = 0L
        transactionDetails.version = 0
        transactionDetails.inventoryId = inventory.id
        transactionDetails.inventoryTypeId = inventory.typeId
        transactionDetails.inventoryTransactionId = 0L          // set after parent save

        long itemId = Long.parseLong(gridModelProduction.cell[0].toString())                               //Positional value of Grid object
        Item item = (Item) itemCacheUtility.read(itemId)
        transactionDetails.itemId = item.id

        transactionDetails.vehicleId = 0L
        transactionDetails.vehicleNumber = null
        transactionDetails.suppliedQuantity = Double.parseDouble(gridModelProduction.cell[3].toString())    //Positional value of Grid object
        transactionDetails.actualQuantity = Double.parseDouble(gridModelProduction.cell[3].toString())      //Positional value of Grid object
        transactionDetails.shrinkage = 0.0d
        transactionDetails.rate = 0.0d                  //set rate at service
        transactionDetails.supplierChalan = null
        transactionDetails.stackMeasurement = null
        transactionDetails.fifoQuantity = 0.0d
        transactionDetails.lifoQuantity = 0.0d
        transactionDetails.acknowledgedBy = 0L
        transactionDetails.createdOn = new Date()                             // consider new entry for save
        transactionDetails.createdBy = invSessionUtil.appSessionUtil.getAppUser().id
        transactionDetails.updatedOn = null
        transactionDetails.updatedBy = 0L
        transactionDetails.comments = null
        transactionDetails.mrfNo = null
        transactionDetails.transactionDate = DateUtility.parseMaskedDate(params.transactionDate.toString())
        transactionDetails.transactionDetailsId = 0L

        transactionDetails.transactionTypeId = transactionTypePro.id
        transactionDetails.adjustmentParentId = 0L
        transactionDetails.approvedOn = new Date()                           // approved in the event of create
        transactionDetails.approvedBy = invSessionUtil.appSessionUtil.getAppUser().id
        transactionDetails.isIncrease = true
        transactionDetails.isCurrent = true

        long productionLineItemId = Long.parseLong(params.productionLineItemId.toString())
        Object invProductionDetails = invProductionDetailsCacheUtility.getProdDetailsByLineItemAndItemId(productionLineItemId, item.id)
        transactionDetails.overheadCost = invProductionDetails.overheadCost

        return transactionDetails
    }

    /**
     * check consumable stock for new quantity of Raw material
     *
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     * @Param adjustedQuantity -new adjusted quantity of raw material
     *
     * @Return -if new adjusted quantity is available then return null; otherwise return specific message
     */
    private String checkAvailableStockForConsumption(long inventoryId, Item item, double adjustedQuantity) {
        double availableStockQuantity = getAvailableStock(inventoryId, item.id)
        if (adjustedQuantity > availableStockQuantity) {
            return "Item ${item.name} is not available in stock"
        }
        return null
    }

    /**
     * check consumable stock for adjusted quantity of Raw material
     *
     * @Param eachRelatedAdjustments -map contains oldInvTransactionConsumptionDetails and adjustedInvTransactionConsumptionDetails
     * @Param item -Item object
     *
     * @Return -if new adjusted quantity is available then return null; otherwise return specific message
     */
    private String checkAvailableStockForConsumption(Map eachRelatedAdjustments, Item item) {
        InvInventoryTransactionDetails oldDetails = eachRelatedAdjustments.oldDetails
        InvInventoryTransactionDetails adjDetails = eachRelatedAdjustments.adjDetails

        double availableStockQuantity = getAvailableStock(oldDetails.inventoryId, oldDetails.itemId)
        availableStockQuantity = availableStockQuantity + oldDetails.actualQuantity     // oldDetails.actualQuantity will be reversed
        if (adjDetails.actualQuantity > availableStockQuantity) {
            return "Item ${item.name} is not available in stock"
        }
        return null
    }

    /**
     * if production quantity decrease : Check consumable stock
     *
     * @Param eachRelatedAdjustments -map contains oldInvTransactionProductionDetails and adjustedInvTransactionProductionDetails
     * @Param item -Item object
     *
     * @Return -if new adjusted quantity is available then return null; otherwise return specific message
     */
    private String checkAvailableStockForProduction(Map eachRelatedAdjustments, Item item) {
        InvInventoryTransactionDetails oldDetails = eachRelatedAdjustments.oldDetails
        InvInventoryTransactionDetails adjDetails = eachRelatedAdjustments.adjDetails
        if (adjDetails.actualQuantity > oldDetails.actualQuantity) return null
        double diff = oldDetails.actualQuantity - adjDetails.actualQuantity

        double availableStockQuantity = getAvailableStock(oldDetails.inventoryId, oldDetails.itemId)
        if (diff > availableStockQuantity) {
            return "Item ${item.name} is not available in stock"
        }
        return null
    }

    private static final String APPROVE_COUNT_QUERY = """
             SELECT COUNT(COALESCE(id,0)) AS count FROM inv_inventory_transaction_details
             WHERE inventory_transaction_id=:inventoryTransactionId AND
             approved_by > 0 AND is_current = true """

    /**
     * to get count of approved-child of an inventory-production-transaction
     * @Param inventoryTransactionId -InvInventoryTransaction.id
     * @Return int value
     */
    private int getApproveCountForProdWithCons(long inventoryTransactionId) {
        Map queryParams = [inventoryTransactionId: inventoryTransactionId]
        List<GroovyRowResult> countResult = executeSelectSql(APPROVE_COUNT_QUERY, queryParams)
        int total = countResult[0].count
        return total
    }

    /**
     *  Method to adjustment of children(InvInventoryTransactionDetails) of both Consumption and Production
     *
     * @Params inventoryTransactionCon -InvInventoryTransaction(parent)
     * @Params inventoryTransactionProd -InvInventoryTransaction(parent)
     * @Params lstNewConsumpDetails -new InvInventoryTransactionConsumptionDetails(children)
     * @Params lstUnChangedConsumpDetails -unchanged old InvInventoryTransactionConsumptionDetails(children)
     * @Params lstNewProductionDetails -new InvInventoryTransactionProductionDetails(children)
     * @Params lstAdjustmentSetConsump -list of map which contains : oldConsumptionDetails, reverseConsumptionAdjDetails, newConsumptionAdjDetails
     * @Params lstAdjustmentSetProduction -list of map which contains : oldProductionDetails, reverseProductionAdjDetails, newProductionAdjDetails
     *
     * @Return -if all transaction occurs successfully then return true otherwise return false
     */
    private boolean adjustForProductionWithConsumption(InvInventoryTransaction inventoryTransactionCon, InvInventoryTransaction inventoryTransactionProd,
                                                       List<InvInventoryTransactionDetails> lstNewConsumpDetails, List<InvInventoryTransactionDetails> lstUnChangedConsumpDetails,
                                                       List<InvInventoryTransactionDetails> lstNewProductionDetails,
                                                       List lstAdjustmentSetConsump, List lstAdjustmentSetProduction) {

        updateForProductionWithConsumption(inventoryTransactionCon)  // Only itemCount, updatedBy/On will be updated
        updateForProductionWithConsumption(inventoryTransactionProd) // Only itemCount, updatedBy/On will be updated
        // Now get the Total Costing
        double totalCosting = 0.0d
        int i = 0
        for (i = 0; i < lstNewConsumpDetails.size(); i++) {
            InvInventoryTransactionDetails newConsumptionDetails = lstNewConsumpDetails[i]
            newConsumptionDetails.rate = calculateValuationForOut(newConsumptionDetails.actualQuantity, newConsumptionDetails.inventoryId, newConsumptionDetails.itemId)
            totalCosting = totalCosting + (newConsumptionDetails.rate * newConsumptionDetails.actualQuantity)
            invInventoryTransactionDetailsService.create(newConsumptionDetails) //if new item(s) add to consume then create
        }

        //to calculate the total costing of raw-materials including changed and unchanged raw materials
        for (i = 0; i < lstUnChangedConsumpDetails.size(); i++) {
            InvInventoryTransactionDetails existingConsumptionDetails = lstUnChangedConsumpDetails[i]
            totalCosting = totalCosting + (existingConsumptionDetails.rate * existingConsumptionDetails.actualQuantity)
        }

        for (i = 0; i < lstAdjustmentSetConsump.size(); i++) {
            Map consumptionMap = (Map) lstAdjustmentSetConsump[i]
            InvInventoryTransactionDetails adjDetails = consumptionMap.adjDetails
            InvInventoryTransactionDetails reverseAdjDetails = consumptionMap.reverseAdjDetails
            InvInventoryTransactionDetails oldDetails = consumptionMap.oldDetails
            totalCosting = totalCosting + (adjDetails.rate * adjDetails.actualQuantity)
            // Save both adjustments update the previous details(isCurrent=false)
            createAdjustmentTransaction(inventoryTransactionCon, reverseAdjDetails, adjDetails, oldDetails)
        }

        // Get total quantity of production
        double totalProduction = 0.0d
        for (i = 0; i < lstNewProductionDetails.size(); i++) {
            InvInventoryTransactionDetails newProductionDetails = lstNewProductionDetails[i]
            totalProduction = totalProduction + newProductionDetails.actualQuantity
        }

        for (i = 0; i < lstAdjustmentSetProduction.size(); i++) {
            Map productionMap = (Map) lstAdjustmentSetProduction[i]
            InvInventoryTransactionDetails adjDetails = productionMap.adjDetails
            totalProduction = totalProduction + adjDetails.actualQuantity
        }

        // Now find the rate and set in each production
        double rateOfProduction = (totalCosting / totalProduction).round(2)

        for (i = 0; i < lstNewProductionDetails.size(); i++) {
            InvInventoryTransactionDetails newProductionDetails = lstNewProductionDetails[i]
            newProductionDetails.rate = rateOfProduction + newProductionDetails.overheadCost
            invInventoryTransactionDetailsService.create(newProductionDetails)
        }

        for (i = 0; i < lstAdjustmentSetProduction.size(); i++) {
            Map productionMap = (Map) lstAdjustmentSetProduction[i]
            InvInventoryTransactionDetails adjDetails = productionMap.adjDetails
            InvInventoryTransactionDetails reverseAdjDetails = productionMap.reverseAdjDetails
            InvInventoryTransactionDetails oldDetails = productionMap.oldDetails
            adjDetails.rate = rateOfProduction + adjDetails.overheadCost // take the new rate only in adjusted entry
            // Save both adjustments update the previous details(isCurrent=false)
            createAdjustmentTransaction(inventoryTransactionProd, reverseAdjDetails, adjDetails, oldDetails)
        }
        return true
    }

    private static final String UPDATE_QUERY = """
                      UPDATE inv_inventory_transaction SET
                          comments=:comments,
                          updated_on=:updatedOn,
                          transaction_date=:transactionDate,
                          updated_by= :updatedBy,
                          item_count= :itemCount,
                          version= version + 1
                      WHERE
                          id=:id AND
                          version= :version
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

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount <= 0) throw new RuntimeException('Failed to update Inventory Transaction')
        invInventoryTransaction.version = invInventoryTransaction.version + 1
        return (new Integer(updateCount));
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

    private static final String FIFO_LIST_QUERY = """
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
    public List<GroovyRowResult> getListForFIFO(long inventoryId, long itemId) {
        Map queryParams = [
                itemId: itemId,
                inventoryId: inventoryId
        ]
        List<GroovyRowResult> lstFifo = executeSelectSql(FIFO_LIST_QUERY, queryParams)
        return lstFifo
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
     * @Return List < GroovyRowResult >
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
     * Method use to create adjusted invTransactionDetails
     * @Param invInventoryTransaction -InvInventoryTransaction(parent)
     * @Param reverseAdjDetails -InvInventoryTransactionDetails(child)
     * @Param adjDetails -InvInventoryTransactionDetails(child)
     * @Param currentTransactionDetails -which transactionDetails is going to be adjusted (InvInventoryTransactionDetails(child))
     *
     * @Return -if all transaction occurs successfully the return newAdjustedInvInventoryTransactionDetails otherwise throw exception to rollback all DB transaction
     */
    private InvInventoryTransactionDetails createAdjustmentTransaction(InvInventoryTransaction invInventoryTransaction, InvInventoryTransactionDetails reverseAdjDetails,
                                                                       InvInventoryTransactionDetails adjDetails, InvInventoryTransactionDetails currentTransactionDetails) {

        InvInventoryTransactionDetails newReverseAdjDetails = null
        InvInventoryTransactionDetails newAdjDetails = null
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypePro = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        switch (invInventoryTransaction.transactionTypeId) {
            case transactionTypeCons.id:
                newReverseAdjDetails = invInventoryTransactionDetailsService.create(reverseAdjDetails)  // Create reverse-adjusted-transaction-details
                adjDetails.approvedOn = DateUtility.addOneSecond(adjDetails.approvedOn)                 // required for transaction report
                adjDetails.rate = calculateValuationForOut(adjDetails.actualQuantity, adjDetails.inventoryId, adjDetails.itemId)
                newAdjDetails = invInventoryTransactionDetailsService.create(adjDetails)                //Create newAdjustedTransactionDetails
                break;
            case transactionTypePro.id:
                newAdjDetails = invInventoryTransactionDetailsService.create(adjDetails)                  // Create newAdjustedTransactionDetails
                reverseAdjDetails.approvedOn = DateUtility.addOneSecond(reverseAdjDetails.approvedOn)     // required for transaction report
                reverseAdjDetails.rate = calculateValuationForOut(reverseAdjDetails.actualQuantity, reverseAdjDetails.inventoryId, reverseAdjDetails.itemId)
                newReverseAdjDetails = invInventoryTransactionDetailsService.create(reverseAdjDetails)    // Create reverse-adjusted-transaction-details
                break;
            default:
                throw new RuntimeException("Fail occurred at invInventoryTransactionDetailsService.createAdjustmentTransaction")
        }

        if (newAdjDetails) {
            int updateInvInventoryTransaction = setIsCurrentTransaction(currentTransactionDetails) //set isCurrent = false
        } else {
            throw new RuntimeException("Fail occurred at invInventoryTransactionDetailsService.createAdjustmentTransaction")
        }

        return newAdjDetails
    }

    private static final String UPDATE_DETAILS_QUERY = """
                      UPDATE inv_inventory_transaction_details
                      SET is_current=false,
                      version=version+1,
                      updated_by = :updatedBy,
                      updated_on = :updatedOn
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
        int updateCount = executeUpdateSql(UPDATE_DETAILS_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update inventory transaction details')
        }
        return updateCount
    }

    private static final String AVAILABLE_STOCK_QUERY = """
        SELECT available_stock
        FROM vw_inv_inventory_stock
        WHERE inventory_id=:inventoryId AND item_id=:itemId
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

    private InvInventoryTransactionDetails copyForAdjustment(InvInventoryTransactionDetails oldConsumpDetails, double newQuantity, String comments, AppUser user) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_ADJUSTMENT, companyId)

        InvInventoryTransactionDetails newDetails = new InvInventoryTransactionDetails()
        newDetails.id = 0
        newDetails.version = 0
        newDetails.inventoryTransactionId = oldConsumpDetails.inventoryTransactionId
        newDetails.transactionDetailsId = oldConsumpDetails.transactionDetailsId
        newDetails.inventoryTypeId = oldConsumpDetails.inventoryTypeId
        newDetails.inventoryId = oldConsumpDetails.inventoryId
        newDetails.approvedBy = user.id
        newDetails.itemId = oldConsumpDetails.itemId
        newDetails.vehicleId = oldConsumpDetails.vehicleId
        newDetails.vehicleNumber = oldConsumpDetails.vehicleNumber
        newDetails.suppliedQuantity = newQuantity
        newDetails.actualQuantity = newQuantity
        newDetails.shrinkage = 0
        newDetails.rate = oldConsumpDetails.rate                        // same as parents rate
        newDetails.supplierChalan = oldConsumpDetails.supplierChalan
        newDetails.stackMeasurement = oldConsumpDetails.stackMeasurement
        newDetails.fifoQuantity = 0
        newDetails.lifoQuantity = 0
        newDetails.acknowledgedBy = oldConsumpDetails.acknowledgedBy
        newDetails.createdOn = new Date()
        newDetails.createdBy = user.id
        newDetails.updatedOn = null
        newDetails.updatedBy = 0
        newDetails.comments = comments
        newDetails.mrfNo = oldConsumpDetails.mrfNo
        newDetails.transactionDate = oldConsumpDetails.transactionDate
        newDetails.fixedAssetId = oldConsumpDetails.fixedAssetId
        newDetails.fixedAssetDetailsId = oldConsumpDetails.fixedAssetDetailsId
        newDetails.transactionTypeId = transactionTypeAdj.id              // TRANSACTION_TYPE_ADJUSTMENT
        newDetails.adjustmentParentId = oldConsumpDetails.adjustmentParentId == 0 ? oldConsumpDetails.id : oldConsumpDetails.adjustmentParentId
        newDetails.approvedOn = new Date()
        newDetails.isIncrease = oldConsumpDetails.isIncrease
        newDetails.isCurrent = true
        newDetails.overheadCost = oldConsumpDetails.overheadCost
        newDetails.invoiceAcknowledgedBy = oldConsumpDetails.invoiceAcknowledgedBy
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
