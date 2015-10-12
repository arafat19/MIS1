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
import com.athena.mis.inventory.entity.*
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvInventoryTypeCacheUtility
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
 *  Class for updating existing unapproved InvProductionWithConsumption
 *  For details go through Use-Case doc named 'UpdateForInvProductionWithConsumptionActionService'
 */
class UpdateForInvProductionWithConsumptionActionService extends BaseService implements ActionIntf {

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
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private static final String PRODUCTION_UPDATE_FAILURE_MSG = "Production has not been updated"
    private static final String PRODUCTION_UPDATE_SUCCESS_MSG = "Production has been updated successfully"
    private static final String PRODUCTION_ITEM_NOT_FOUND = "Production line item not found"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to update production"
    private static final String PRODUCTION_OBJECT_NOT_FOUND = "Production not found"
    private static final String INV_TRANSACTION_CONSUMP = "invTransactionConsump"
    private static final String INV_TRANSACTION_PROD = "invTransactionProd"
    private static final String LST_TRANSACTION_DETAILS_CONSUMP = "lstTransactionDetailsConsump"
    private static final String LST_TRANSACTION_DETAILS_PROD = "lstTransactionDetailsProd"
    private static final String GRID_OBJECT = "gridObject"
    private static final String APPROVED_PRODUCTION_UPDATE_PROHIBITED = "Approved production can not be updated"
    private static final String INVALID_INPUT_ERROR = "Invalid Input"

    /**
     * validate different criteria to update existing unapproved InvProductionWithConsumption. Such as :
     *      Check approval
     *      Check existence of oldConsumption object
     *      Check existence of InvProductionLineItem Obj,
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
            long consumptionId = Long.parseLong(params.idCon.toString())     // id of inventoryTransaction (CONSUMPTION)

            //For any child if approved_by > 0 AND is_current = true, then parent could not be updated
            int approveCount = getApproveCountForProdWithCons(consumptionId)
            if (approveCount > 0) {
                result.put(Tools.MESSAGE, APPROVED_PRODUCTION_UPDATE_PROHIBITED)
                return result
            }

            // check existence of InvProductionLineItem object
            Long prodLineItemId = Long.parseLong(params.productionLineItemId.toString())
            InvProductionLineItem prodLineItem = (InvProductionLineItem) invProductionLineItemCacheUtility.read(prodLineItemId)
            if (!prodLineItem) {
                result.put(Tools.MESSAGE, PRODUCTION_ITEM_NOT_FOUND)
                return result
            }

            // check object existence (only consumption)
            InvInventoryTransaction oldConsumption = invInventoryTransactionService.read(consumptionId)
            if (!oldConsumption) {
                result.put(Tools.MESSAGE, PRODUCTION_OBJECT_NOT_FOUND)
                return result
            }

            // Create inventory Transaction Objects
            InvInventoryTransaction invTransactionConsumption = buildInventoryTransactionConsumptionObject(params)
            invTransactionConsumption.createdOn = oldConsumption.createdOn       // for grid wrap
            invTransactionConsumption.createdBy = oldConsumption.createdBy       // for grid wrap
            InvInventoryTransaction oldProduction = buildInventoryTransactionProductionObject(params)

            // Create inventory-transaction-details Objects for Consumption
            JSONElement gridModelRaw = JSON.parse(params.gridModelRawMaterial.toString())
            List lstRawMaterialsRows = (List) gridModelRaw.rows

            List<InvInventoryTransactionDetails> lstInventoryTrDetailsConsumption = []
            for (int i = 0; i < lstRawMaterialsRows.size(); i++) {
                String stockError
                InvInventoryTransactionDetails newRawDetails = buildTransactionDetailsConsump(lstRawMaterialsRows[i], params)

                if (newRawDetails.id > 0) {       // existing raw details
                    InvInventoryTransactionDetails oldRawDetails = (InvInventoryTransactionDetails) invInventoryTransactionDetailsService.read(newRawDetails.id)
                    if (!oldRawDetails) {
                        result.put(Tools.MESSAGE, INVALID_INPUT_ERROR)
                        return result
                    }

                    if (oldRawDetails.actualQuantity < newRawDetails.actualQuantity) {
                        stockError = checkStockForRawMaterial(oldRawDetails, newRawDetails.actualQuantity)
                    }
                    newRawDetails.version = oldRawDetails.version  // version used to update object
                } else {     // new raw details
                    stockError = checkStockForMaterial(newRawDetails)
                    newRawDetails.inventoryTransactionId = oldConsumption.id
                    newRawDetails.updatedBy = 0L
                    newRawDetails.updatedOn = null
                }
                if (stockError) {
                    result.put(Tools.MESSAGE, stockError)
                    return result
                }
                lstInventoryTrDetailsConsumption.add(newRawDetails)
            }

            invTransactionConsumption.itemCount = lstInventoryTrDetailsConsumption.size()     // materialCount will not be updated (used to wrap grid)

            // Create inventory-transaction-details Objects for Production
            JSONElement gridModelFinished = JSON.parse(params.gridModelFinishedProduct.toString())
            List lstFinishedMaterialsRows = (List) gridModelFinished.rows

            List<InvInventoryTransactionDetails> lstInventoryTrDetailsProduction = []
            for (int i = 0; i < lstFinishedMaterialsRows.size(); i++) {
                String stockError
                InvInventoryTransactionDetails newProdDetails = buildTransactionDetailsProd(lstFinishedMaterialsRows[i], params)

                if (newProdDetails.id > 0) {       // existing Production details
                    InvInventoryTransactionDetails oldProdDetails = (InvInventoryTransactionDetails) invInventoryTransactionDetailsService.read(newProdDetails.id)
                    if (!oldProdDetails) {
                        result.put(Tools.MESSAGE, INVALID_INPUT_ERROR)
                        return result
                    }

                    if (oldProdDetails.actualQuantity > newProdDetails.actualQuantity) {
                        stockError = checkStockForProdMaterial(oldProdDetails, newProdDetails.actualQuantity)
                    }
                    newProdDetails.version = oldProdDetails.version  // version used to update object
                } else {     // new Production details
                    newProdDetails.inventoryTransactionId = oldProduction.id
                    newProdDetails.updatedBy = 0L
                    newProdDetails.updatedOn = null
                }
                if (stockError) {
                    result.put(Tools.MESSAGE, stockError)
                    return result
                }

                lstInventoryTrDetailsProduction.add(newProdDetails)
            }

            oldProduction.itemCount = lstInventoryTrDetailsProduction.size()         // materialCount will not be updated (used to wrap grid)

            result.put(INV_TRANSACTION_CONSUMP, invTransactionConsumption)
            result.put(LST_TRANSACTION_DETAILS_CONSUMP, lstInventoryTrDetailsConsumption)
            result.put(INV_TRANSACTION_PROD, oldProduction)
            result.put(LST_TRANSACTION_DETAILS_PROD, lstInventoryTrDetailsProduction)
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
     *  Method to update parents(InvInventoryTransaction) & children(InvInventoryTransactionDetails) of both Consumption and Production
     *
     * @Params parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains parents(InvInventoryTransaction) & children(InvInventoryTransactionDetails)
     *
     * @Return -map contains isError(true/false), invTranConsumption, invTranProduction
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvInventoryTransaction invTransactionCon = (InvInventoryTransaction) preResult.get(INV_TRANSACTION_CONSUMP)
            InvInventoryTransaction invTransactionProd = (InvInventoryTransaction) preResult.get(INV_TRANSACTION_PROD)
            List<InvInventoryTransactionDetails> lstConsumptionDetails = (List<InvInventoryTransactionDetails>) preResult.get(LST_TRANSACTION_DETAILS_CONSUMP)
            List<InvInventoryTransactionDetails> lstProductionDetails = (List<InvInventoryTransactionDetails>) preResult.get(LST_TRANSACTION_DETAILS_PROD)

            //Update parents(InvInventoryTransaction) & children(InvInventoryTransactionDetails) of both Consumption and Production
            String message = updateForProductionWithConsumption(invTransactionCon, invTransactionProd, lstConsumptionDetails, lstProductionDetails)
            if (message != null) {
                result.put(Tools.MESSAGE, message)
                return result
            }
            result.put(INV_TRANSACTION_CONSUMP, invTransactionCon)
            result.put(INV_TRANSACTION_PROD, invTransactionProd)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to create inventory production')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRODUCTION_UPDATE_FAILURE_MSG)
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
            AppUser updatedBy = (AppUser) appUserCacheUtility.read(inventoryTransactionCon.updatedBy)
            object.cell = [Tools.LABEL_NEW,
                    inventoryTransactionCon.id,
                    invType.key + Tools.COLON + inventory.name,
                    lineItem.name,
                    inventoryTransactionCon.itemCount,
                    inventoryTransactionProd.itemCount,
                    transactionDate,
                    createdBy.username,
                    updatedBy.username
            ]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, PRODUCTION_UPDATE_SUCCESS_MSG)
            result.put(GRID_OBJECT, object)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRODUCTION_UPDATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to update production-with-consumption
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
            result.put(Tools.MESSAGE, PRODUCTION_UPDATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRODUCTION_UPDATE_FAILURE_MSG)
            return result
        }
    }

    /**
     *  Build the parent object of consumption (InvInventoryTransaction)
     * @Param params -Serialized parameters from UI
     * @Param inventory -InvInventory object
     * @Return -InvInventoryTransaction object (consumption)
     */
    private InvInventoryTransaction buildInventoryTransactionConsumptionObject(GrailsParameterMap params) {
        InvInventoryTransaction invConsumption = new InvInventoryTransaction()
        long inventoryId = Long.parseLong(params.inventoryId.toString())
        InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)

        invConsumption.id = Long.parseLong(params.idCon.toString())
        invConsumption.version = Integer.parseInt(params.versionCon.toString())
        invConsumption.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
        invConsumption.updatedOn = new Date()
        invConsumption.comments = (params.comments.toString().length() > 0) ? params.comments : null
        invConsumption.transactionDate = DateUtility.parseMaskedDate(params.transactionDate.toString())
        invConsumption.invProductionLineItemId = Long.parseLong(params.productionLineItemId.toString())
        invConsumption.inventoryId = inventory.id
        invConsumption.inventoryTypeId = inventory.typeId

        return invConsumption
    }

    /**
     * Build the parent object of production (InvInventoryTransaction)
     * @Param params -Serialized parameters from UI
     * @Param inventory -InvInventory object
     * @Return -InvInventoryTransaction object (production)
     */
    private InvInventoryTransaction buildInventoryTransactionProductionObject(GrailsParameterMap params) {
        InvInventoryTransaction invProduction = new InvInventoryTransaction()
        long inventoryId = Long.parseLong(params.inventoryId.toString())
        InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)

        invProduction.id = Long.parseLong(params.idProd.toString())
        invProduction.version = Integer.parseInt(params.versionProd.toString())
        invProduction.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
        invProduction.updatedOn = new Date()
        invProduction.comments = (params.comments.toString().length() > 0) ? params.comments : null
        invProduction.transactionDate = DateUtility.parseMaskedDate(params.transactionDate.toString())
        invProduction.invProductionLineItemId = Long.parseLong(params.productionLineItemId.toString())
        invProduction.inventoryId = inventory.id
        invProduction.inventoryTypeId = inventory.typeId

        return invProduction
    }

    /**
     * Build the child object of consumption (InvInventoryTransactionDetails)
     * @Param gridModelConsumption -Serialized GridObject from UI
     * @Param params -Serialized parameters from UI
     * @Return -InvInventoryTransactionDetails object (consumption)
     */
    private InvInventoryTransactionDetails buildTransactionDetailsConsump(def gridModelConsumption, GrailsParameterMap params) {

        long inventoryId = Long.parseLong(params.inventoryId.toString())
        // pull transactionType object
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
        InvInventoryTransactionDetails transactionDetails = new InvInventoryTransactionDetails()

        transactionDetails.id = Long.parseLong(gridModelConsumption.cell[4].toString())      // set id for existing details otherwise -1
        transactionDetails.version = 0
        transactionDetails.inventoryId = inventory.id
        transactionDetails.inventoryTypeId = inventory.typeId
        transactionDetails.inventoryTransactionId = 0L          // set after parent save
        long itemId = Long.parseLong(gridModelConsumption.cell[0].toString())             //Positional value of Grid object
        Item item = (Item) itemCacheUtility.read(itemId)
        transactionDetails.itemId = item.id
        transactionDetails.vehicleId = 0L
        transactionDetails.vehicleNumber = null
        transactionDetails.suppliedQuantity = Double.parseDouble(gridModelConsumption.cell[3].toString())  //Positional value of Grid object
        transactionDetails.actualQuantity = Double.parseDouble(gridModelConsumption.cell[3].toString())    //Positional value of Grid object
        transactionDetails.shrinkage = 0.0d
        transactionDetails.rate = 0.0d   //set rate at approval
        transactionDetails.supplierChalan = null
        transactionDetails.stackMeasurement = null
        transactionDetails.fifoQuantity = 0.0d
        transactionDetails.lifoQuantity = 0.0d
        transactionDetails.acknowledgedBy = 0L
        transactionDetails.createdOn = new Date()                           // consider new entry for save
        transactionDetails.createdBy = invSessionUtil.appSessionUtil.getAppUser().id
        transactionDetails.updatedOn = new Date()                            // consider existing entry for update
        transactionDetails.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
        transactionDetails.comments = null
        transactionDetails.mrfNo = null
        transactionDetails.transactionDate = DateUtility.parseMaskedDate(params.transactionDate.toString())
        transactionDetails.transactionDetailsId = 0L

        transactionDetails.transactionTypeId = transactionTypeCons.id
        transactionDetails.adjustmentParentId = 0L
        transactionDetails.approvedOn = null
        transactionDetails.approvedBy = 0L
        transactionDetails.isIncrease = false
        transactionDetails.isCurrent = true
        transactionDetails.overheadCost = 0.0d

        return transactionDetails
    }

    /**
     * Build the child object of production (InvInventoryTransactionDetails)
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

        transactionDetails.id = Long.parseLong(gridModelProduction.cell[4].toString())      // set id for existing details otherwise -1
        transactionDetails.version = 0
        transactionDetails.inventoryId = inventory.id
        transactionDetails.inventoryTypeId = inventory.typeId
        transactionDetails.inventoryTransactionId = 0L          // set after parent save

        long itemId = Long.parseLong(gridModelProduction.cell[0].toString())               //Positional value of Grid object
        Item item = (Item) itemCacheUtility.read(itemId)
        transactionDetails.itemId = item.id

        transactionDetails.vehicleId = 0L
        transactionDetails.vehicleNumber = null
        transactionDetails.suppliedQuantity = Double.parseDouble(gridModelProduction.cell[3].toString()) //Positional value of Grid object
        transactionDetails.actualQuantity = Double.parseDouble(gridModelProduction.cell[3].toString())   //Positional value of Grid object
        transactionDetails.shrinkage = 0.0d
        transactionDetails.rate = 0.0d // set at approval
        transactionDetails.supplierChalan = null
        transactionDetails.stackMeasurement = null
        transactionDetails.fifoQuantity = 0.0d
        transactionDetails.lifoQuantity = 0.0d
        transactionDetails.acknowledgedBy = 0L
        transactionDetails.createdOn = new Date()                             // consider new entry for save
        transactionDetails.createdBy = invSessionUtil.appSessionUtil.getAppUser().id
        transactionDetails.updatedOn = new Date()                            // consider existing entry for update
        transactionDetails.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
        transactionDetails.comments = null
        transactionDetails.mrfNo = null
        transactionDetails.transactionDate = DateUtility.parseMaskedDate(params.transactionDate.toString())
        transactionDetails.transactionDetailsId = 0L

        transactionDetails.transactionTypeId = transactionTypePro.id
        transactionDetails.adjustmentParentId = 0L
        transactionDetails.approvedOn = null
        transactionDetails.approvedBy = 0L
        transactionDetails.isIncrease = true
        transactionDetails.isCurrent = true

        long productionLineItemId = Long.parseLong(params.productionLineItemId.toString())
        InvProductionDetails invProductionDetails = InvProductionDetails.findByProductionLineItemIdAndMaterialId(productionLineItemId, item.id, [readOnly: true])
        transactionDetails.overheadCost = invProductionDetails.overheadCost

        return transactionDetails
    }

    /**
     * to check stock of newly added raw-material
     * @Param newTransDetails -InvInventoryTransactionDetails(Child)
     *
     * @Return -if Stock is not available then return specific message otherwise return null
     */
    private String checkStockForMaterial(InvInventoryTransactionDetails newTransDetails) {
        double consumableStockQuantity = getConsumableStock(newTransDetails.inventoryId, newTransDetails.itemId)
        if (newTransDetails.actualQuantity > consumableStockQuantity) {
            Item item = (Item) itemCacheUtility.read(newTransDetails.itemId)
            return "Insufficiant stock of ${item.name}"
        }
        return null
    }

    /**
     * to check stock of existing raw details When(oldRawDetails.actualQuantity < newRawDetails.actualQuantity)
     * @Param oldRawDetails -InvInventoryTransactionDetails(Child)
     * @Param newQuantity - newQuantity of oldRawDetails (InvInventoryTransactionDetails)
     *
     * @Return -if Stock is not available then return specific message otherwise return null
     */
    private String checkStockForRawMaterial(InvInventoryTransactionDetails oldRawDetails, double newQuantity) {
        long itemId = oldRawDetails.itemId
        long inventoryId = oldRawDetails.inventoryId
        double oldQuantity = oldRawDetails.actualQuantity
        double consumableStockQuantity = getConsumableStock(inventoryId, itemId)
        consumableStockQuantity = consumableStockQuantity + oldQuantity - newQuantity
        if (consumableStockQuantity < 0) {
            Item item = (Item) itemCacheUtility.read(itemId)
            return "Insufficiant stock of ${item.name}"
        }
        return null
    }

    /**
     * to check stock of existing Production details (When oldProdDetails.actualQuantity > newProdDetails.actualQuantity)
     * @Param oldProdDetails -InvInventoryTransactionDetails(Child)
     * @Param newQuantity - newQuantity of oldProdDetails (InvInventoryTransactionDetails)
     *
     * @Return -if Stock is not available then return specific message otherwise return null
     */
    private String checkStockForProdMaterial(InvInventoryTransactionDetails oldProdDetails, double newQuantity) {
        long itemId = oldProdDetails.itemId
        long inventoryId = oldProdDetails.inventoryId
        double consumableStockQuantity = getConsumableStock(inventoryId, itemId)
        consumableStockQuantity = consumableStockQuantity - oldProdDetails.actualQuantity + newQuantity
        if (consumableStockQuantity < 0) {
            Item item = (Item) itemCacheUtility.read(itemId)
            return "Insufficiant stock of ${item.name}"
        }
        return null
    }

    private static final String APPROVED_COUNT_QUERY = """
        SELECT COUNT(COALESCE(id,0)) AS count FROM inv_inventory_transaction_details
        WHERE inventory_transaction_id=:inventoryTransactionId AND
        approved_by > 0 AND is_current = true """

    /**
     * Method to get count of approved and isCurrent children
     * @Param inventoryTransactionId -InvInventoryTransaction.id
     *
     * @Return -(int)total
     */
    private int getApproveCountForProdWithCons(long inventoryTransactionId) {
        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId
        ]
        List<GroovyRowResult> countResult = executeSelectSql(APPROVED_COUNT_QUERY, queryParams)
        int total = countResult[0].count
        return total
    }

    /**
     * Method to update parents(InvInventoryTransaction) & children(InvInventoryTransactionDetails) of both Consumption and Production
     *
     * @Param storeTransactionCon -TransactionTypeConsumptionObject (Parent)
     * @Param storeTransactionProd -TransactionTypeProductionObject (Parent)
     * @Param lstConsumptionDetails -TransactionTypeConsumptionDetailsObjectList (Children)
     * @Param lstProductionDetails -TransactionTypeProductionDetailsObjectList (Children)
     *
     * @Return -if all transactions occur successfully then return TRUE otherwise return FALSE
     */
    private String updateForProductionWithConsumption(InvInventoryTransaction inventoryTransactionCon, InvInventoryTransaction inventoryTransactionProd,
                                                      List<InvInventoryTransactionDetails> lstConsumptionDetails, List<InvInventoryTransactionDetails> lstProductionDetails) {
        updateForProductionWithConsumption(inventoryTransactionCon)
        updateForProductionWithConsumption(inventoryTransactionProd)

        for (int i = 0; i < lstConsumptionDetails.size(); i++) {
            InvInventoryTransactionDetails consumptionDetails = lstConsumptionDetails[i]
            if (consumptionDetails.id <= 0) { //if new consumed item then create
                invInventoryTransactionDetailsService.create(consumptionDetails)
            } else { //if old consumed item then update only quantity
                updateOnlyQuantity(consumptionDetails)
            }
        }

        for (int i = 0; i < lstProductionDetails.size(); i++) {
            InvInventoryTransactionDetails productionDetails = lstProductionDetails[i]
            if (productionDetails.id <= 0) { //if new finished-item OR produced-item then create
                invInventoryTransactionDetailsService.create(productionDetails)
            } else { //if old finished-item OR produced-item then update only quantity
                updateOnlyQuantity(productionDetails)
            }
        }
        return null
    }

    private static final String CONSUME_STOCK_QUERY = """
        SELECT consumeable_stock
        FROM vw_inv_inventory_consumable_stock
        WHERE inventory_id=:inventoryId
        AND item_id=:itemId
    """

    /**
     * Method returns Consumable-Stock-Amount of an item in an inventory using view(vw_inv_inventory_consumable_stock)
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     *
     * @Return double value
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

    //update Quantity, updatedBy,updatedOn of inv_inventory_transaction_details
    /**
     * update Quantity, updatedBy,updatedOn of inv_inventory_transaction_details of child (InvInventoryTransactionDetails)
     * @Param invInventoryTransactionDetails -invInventoryTransactionDetails Object (Child)
     * @Return -if transaction occurs successfully then return int value; Otherwise throws exception
     */
    private int updateOnlyQuantity(InvInventoryTransactionDetails invInventoryTransactionDetails) {
        String query = """UPDATE inv_inventory_transaction_details
                        SET  version = version+1,
                             actual_quantity = :actualQuantity,
                             supplied_quantity = :suppliedQuantity,
                             updated_by = :updatedBy,
                             updated_on = '${DateUtility.getDBDateFormatWithSecond(invInventoryTransactionDetails.updatedOn)}'
                        WHERE id = :id
                        """

        Map queryParams = [
                id: invInventoryTransactionDetails.id,
                actualQuantity: invInventoryTransactionDetails.actualQuantity,
                suppliedQuantity: invInventoryTransactionDetails.suppliedQuantity,
                updatedBy: invInventoryTransactionDetails.updatedBy
        ]

        int updateCount = executeUpdateSql(query, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException("Fail to update inventory transaction details")
        }
        return updateCount
    }

    /**
     * update parents(InvInventoryTransaction)
     * @Param invInventoryTransaction -invInventoryTransaction Object (Parent)
     * @Return -if transaction occurs successfully then return int value; Otherwise throws exception
     */
    private Integer updateForProductionWithConsumption(InvInventoryTransaction invInventoryTransaction) {
        String query = """UPDATE inv_inventory_transaction SET
                          comments=:comments,
                          updated_on='${DateUtility.getDBDateFormatWithSecond(invInventoryTransaction.updatedOn)}',
                          transaction_date='${DateUtility.getDBDateFormat(invInventoryTransaction.transactionDate)}',
                          updated_by= :updatedBy,
                          item_count= :itemCount,
                          version=${invInventoryTransaction.version + 1}
                      WHERE
                          id=${invInventoryTransaction.id} AND
                          version=${invInventoryTransaction.version}"""

        Map queryParams = [
                updatedBy: invInventoryTransaction.updatedBy,
                comments: invInventoryTransaction.comments,
                itemCount: invInventoryTransaction.itemCount
        ]

        int updateCount = executeUpdateSql(query, queryParams);
        if (updateCount <= 0) throw new RuntimeException('Failed to update Inventory Transaction')
        invInventoryTransaction.version = invInventoryTransaction.version + 1
        return (new Integer(updateCount));
    }
}
