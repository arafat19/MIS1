
package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.*
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.entity.InvProductionLineItem
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
 *  Class for producing new inventory item(s) by consuming existing item(s) based of InventoryProductionLineItem
 *  For details go through Use-Case doc named 'CreateForInvProductionWithConsumptionActionService'
 */
class CreateForInvProductionWithConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
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
    @Autowired
    InvProductionDetailsCacheUtility invProductionDetailsCacheUtility
    @Autowired
    ValuationTypeCacheUtility valuationTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private static final String PRODUCTION_CREATE_FAILURE_MSG = "Production has not been saved"
    private static final String PRODUCTION_CREATE_SUCCESS_MSG = "Production has been successfully saved"
    private static final String PRODUCTION_ITEM_NOT_FOUND = "Production line item not found"
    private static final String INVENTORY_NOT_FOUND = "Selected inventory not found"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to create production"
    private static final String INVENTORY_TRANSACTION_CONSUMP = "invTransactionConsump"
    private static final String INVENTORY_TRANSACTION_PROD = "invTransactionProd"
    private static final String LST_TRANSACTION_DETAILS_CONSUMP = "lstTransactionDetailsConsump"
    private static final String LST_TRANSACTION_DETAILS_PROD = "lstTransactionDetailsProd"
    private static final String GRID_OBJECT = "gridObject"
    private static final String PROJECT_OBJ = "project"

    /**
     * validate different criteria to produce new item(s) by consuming one or more raw materials. Such as :
     *      Check existence of InvInventory Obj,
     *      Check existence of InvProductionLineItem Obj,
     *      Check real stock if approved while create, otherwise check consumable stock etc.
     *
     * Create parent objects(InvTransaction) of InvTranConsumption & invTranProduction,
     * Create children objects(InvInventoryTransactionDetails) of TranDetailsConsumption & TranDetailsProduction,
     *
     * @param parameters -Receives the serialized parameters send from UI e.g inventoryId, productionLineItemId, productionDate etc.
     * @param obj -N/A
     *
     * @return -a map containing all objects necessary for execute (Parents(invTranConsumption & invTranProduction), Children(invTranConsumptionDetails & invTranProductionDetails))
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long companyId = invSessionUtil.appSessionUtil.getCompanyId()

            // check existence of Inventory object
            long inventoryId = Long.parseLong(params.inventoryId.toString())
            InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
            if (!inventory) {
                result.put(Tools.MESSAGE, INVENTORY_NOT_FOUND)
                return result
            }

            // check existence of InvProductionLineItem object
            Long lineItemId = Long.parseLong(params.productionLineItemId.toString())
            InvProductionLineItem prodLineItem = (InvProductionLineItem) invProductionLineItemCacheUtility.read(lineItemId)
            if (!prodLineItem) {
                result.put(Tools.MESSAGE, PRODUCTION_ITEM_NOT_FOUND)
                return result
            }
            // get Project object by projectId of inventory
            Project project = (Project) projectCacheUtility.read(inventory.projectId)
            // Create store Transaction (Parent) Objects (Both consumption & production)
            InvInventoryTransaction invTransactionConsumption = buildInvTransactionConsumptionObject(params, inventory, companyId)
            InvInventoryTransaction invTransactionProduction = buildInvTransactionProductionObject(params, inventory, companyId)

            // Create store-transaction-details (Child) Objects for Consumption
            JSONElement gridModelRaw = JSON.parse(params.gridModelRawMaterial.toString())
            List lstRawMaterialsRows = (List) gridModelRaw.rows

            //Put all consumptionDetailsObjects in list
            List<InvInventoryTransactionDetails> lstTransactionDetailsConsumption = []
            String availableStock
            for (int i = 0; i < lstRawMaterialsRows.size(); i++) {
                if (project.isApproveProduction) {   // if true then object will be approved at create event
                    availableStock = checkAvailableStock(lstRawMaterialsRows[i], inventoryId)   // if true then check real stock
                } else {    // otherwise check consumable-stock
                    availableStock = checkConsumableStock(lstRawMaterialsRows[i], inventoryId)
                }
                if (availableStock) {
                    result.put(Tools.MESSAGE, availableStock)
                    return result
                }

                // Build InvInventoryTransactionDetails(Consumption) object
                InvInventoryTransactionDetails transactionDetailsConsumption = buildTransactionDetailsConsump(lstRawMaterialsRows[i], params, companyId)
                lstTransactionDetailsConsumption.add(transactionDetailsConsumption)
            }
            invTransactionConsumption.itemCount = lstTransactionDetailsConsumption.size()

            // Create store-transaction-details Objects for Production
            JSONElement gridModelFinished = JSON.parse(params.gridModelFinishedProduct.toString())
            List lstFinishedMaterialsRows = (List) gridModelFinished.rows

            //Put all productionDetailsObjects in list
            List<InvInventoryTransactionDetails> lstTransactionDetailsProduction = []
            for (int i = 0; i < lstFinishedMaterialsRows.size(); i++) {
                // Build InvInventoryTransactionDetails(Production) object
                InvInventoryTransactionDetails transactionDetailsProduction = buildTransactionDetailsProd(lstFinishedMaterialsRows[i], params, companyId)
                lstTransactionDetailsProduction.add(transactionDetailsProduction)
            }
            invTransactionProduction.itemCount = lstTransactionDetailsProduction.size()

            result.put(PROJECT_OBJ, project)
            result.put(INVENTORY_TRANSACTION_CONSUMP, invTransactionConsumption)
            result.put(LST_TRANSACTION_DETAILS_CONSUMP, lstTransactionDetailsConsumption)
            result.put(INVENTORY_TRANSACTION_PROD, invTransactionProduction)
            result.put(LST_TRANSACTION_DETAILS_PROD, lstTransactionDetailsProduction)
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
     * Method to save parents(InvInventoryTransaction) & children(InvInventoryTransactionDetails) of both Consumption and Production
     *
     * @param parameters -N/A
     * @param obj -Receives map from executePreCondition which contains parents(InvInventoryTransaction) & children(InvInventoryTransactionDetails)
     *
     * @return -map contains isError(true/false), invTranConsumption, invTranProduction
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            InvInventoryTransaction invTransactionCon = (InvInventoryTransaction) receiveResult.get(INVENTORY_TRANSACTION_CONSUMP)
            InvInventoryTransaction invTransactionProd = (InvInventoryTransaction) receiveResult.get(INVENTORY_TRANSACTION_PROD)
            List<InvInventoryTransactionDetails> lstConsumptionDetails = (List<InvInventoryTransactionDetails>) receiveResult.get(LST_TRANSACTION_DETAILS_CONSUMP)
            List<InvInventoryTransactionDetails> lstProductionDetails = (List<InvInventoryTransactionDetails>) receiveResult.get(LST_TRANSACTION_DETAILS_PROD)

            Project project = (Project) receiveResult.get(PROJECT_OBJ)
            if (project.isApproveProduction) {
                invTransactionCon.isApproved = true
                invTransactionProd.isApproved = true
            }
            //Create parents(InvInventoryTransaction) & children(InvInventoryTransactionDetails) of both Consumption and Production
            boolean success = createForProductionWithConsumption(invTransactionCon, invTransactionProd, lstConsumptionDetails, lstProductionDetails, project)
            if (!success) {
                result.put(Tools.MESSAGE, PRODUCTION_CREATE_FAILURE_MSG)
                return result
            }
            result.put(INVENTORY_TRANSACTION_CONSUMP, invTransactionCon)
            result.put(INVENTORY_TRANSACTION_PROD, invTransactionProd)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRODUCTION_CREATE_FAILURE_MSG)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap InvInventoryTransaction for grid
     *
     * @param obj -Receives map from execute which contains parents(InvInventoryTransaction) objects
     *
     * @return -a map containing all objects necessary for grid data
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            InvInventoryTransaction invTransactionCon = (InvInventoryTransaction) receiveResult.get(INVENTORY_TRANSACTION_CONSUMP)
            InvInventoryTransaction invTransactionProd = (InvInventoryTransaction) receiveResult.get(INVENTORY_TRANSACTION_PROD)

            GridEntity object = new GridEntity()
            object.id = invTransactionCon.id

            String transactionDate = DateUtility.getLongDateForUI(invTransactionCon.transactionDate)
            InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(invTransactionCon.inventoryId)
            SystemEntity invType = (SystemEntity) invInventoryTypeCacheUtility.read(invTransactionCon.inventoryTypeId)
            InvProductionLineItem lineItem = (InvProductionLineItem) invProductionLineItemCacheUtility.read(invTransactionCon.invProductionLineItemId)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(invTransactionCon.createdBy)
            object.cell = [
                    Tools.LABEL_NEW,
                    invTransactionCon.id,
                    invType.key + Tools.COLON + inventory.name,
                    lineItem.name,
                    invTransactionCon.itemCount,
                    invTransactionProd.itemCount,
                    transactionDate,
                    createdBy.username,
                    Tools.EMPTY_SPACE
            ]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, PRODUCTION_CREATE_SUCCESS_MSG)
            result.put(GRID_OBJECT, object)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRODUCTION_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to create production-with-consumption
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
            result.put(Tools.MESSAGE, PRODUCTION_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRODUCTION_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build the parent object of consumption (InvInventoryTransaction)
     * @param params -Serialized parameters from UI
     * @param inventory -InvInventory object
     * @param companyId -id of company
     * @return -InvInventoryTransaction object (consumption)
     */
    private InvInventoryTransaction buildInvTransactionConsumptionObject(GrailsParameterMap params, InvInventory inventory, long companyId) {
        SystemEntity transactionTypeConsumption = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)
        SystemEntity transactionEntityNone = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_NONE, companyId)

        InvInventoryTransaction invConsumption = new InvInventoryTransaction()
        invConsumption.version = 0
        invConsumption.transactionTypeId = transactionTypeConsumption.id
        invConsumption.transactionEntityTypeId = transactionEntityNone.id
        invConsumption.transactionEntityId = 0L
        invConsumption.invProductionLineItemId = Long.parseLong(params.productionLineItemId.toString())
        invConsumption.transactionId = 0L
        invConsumption.projectId = inventory.projectId
        invConsumption.inventoryId = inventory.id
        invConsumption.inventoryTypeId = inventory.typeId
        invConsumption.createdBy = invSessionUtil.appSessionUtil.getAppUser().id
        invConsumption.createdOn = new Date()
        invConsumption.updatedBy = 0L
        invConsumption.comments = (params.comments.toString().length() > 0) ? params.comments : null
        invConsumption.budgetId = 0L
        invConsumption.itemCount = 0
        invConsumption.transactionDate = DateUtility.parseMaskedDate(params.transactionDate.toString())
        invConsumption.companyId = companyId
        return invConsumption
    }

    /**
     * Build the parent object of production (InvInventoryTransaction)
     * @param params -Serialized parameters from UI
     * @param inventory -InvInventory object
     * @param companyId -id of company
     * @return -InvInventoryTransaction object (production)
     */
    private InvInventoryTransaction buildInvTransactionProductionObject(GrailsParameterMap params, InvInventory inventory, long companyId) {
        SystemEntity transactionTypeProduction = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)
        SystemEntity transactionEntityNone = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_NONE, companyId)

        InvInventoryTransaction storeProduction = new InvInventoryTransaction()
        storeProduction.version = 0
        storeProduction.transactionTypeId = transactionTypeProduction.id
        storeProduction.transactionEntityTypeId = transactionEntityNone.id
        storeProduction.transactionEntityId = 0L
        storeProduction.invProductionLineItemId = Long.parseLong(params.productionLineItemId.toString())
        storeProduction.transactionId = 0L        // set the consumption object id after save
        storeProduction.projectId = inventory.projectId
        storeProduction.inventoryId = inventory.id
        storeProduction.inventoryTypeId = inventory.typeId
        storeProduction.createdBy = invSessionUtil.appSessionUtil.getAppUser().id
        storeProduction.createdOn = new Date()
        storeProduction.updatedBy = 0L
        storeProduction.comments = (params.comments.toString().length() > 0) ? params.comments : null
        storeProduction.budgetId = 0L
        storeProduction.itemCount = 0
        storeProduction.transactionDate = DateUtility.parseMaskedDate(params.transactionDate.toString())
        storeProduction.companyId = companyId
        return storeProduction
    }

    /**
     * Build the parent object of consumption (InvInventoryTransactionDetails)
     * @param gridModelConsumption -Serialized GridObject from UI
     * @param params -Serialized parameters from UI
     * @param companyId -id of company
     * @return -InvInventoryTransactionDetails object (consumption)
     */
    private InvInventoryTransactionDetails buildTransactionDetailsConsump(def gridModelConsumption, GrailsParameterMap params, long companyId) {
        long inventoryId = Long.parseLong(params.inventoryId.toString())
        InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
        // pull transactionType object
        SystemEntity transactionTypeConsumption = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        InvInventoryTransactionDetails transactionDetails = new InvInventoryTransactionDetails()

        transactionDetails.version = 0
        transactionDetails.inventoryId = inventory.id
        transactionDetails.inventoryTypeId = inventory.typeId
        transactionDetails.inventoryTransactionId = 0L          // set after parent save
        long itemId = Long.parseLong(gridModelConsumption.cell[0].toString()) //Positional value of Grid object
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
        transactionDetails.createdOn = new Date()
        transactionDetails.createdBy = invSessionUtil.appSessionUtil.getAppUser().id
        transactionDetails.updatedBy = 0L
        transactionDetails.comments = null
        transactionDetails.mrfNo = null
        transactionDetails.transactionDate = DateUtility.parseMaskedDate(params.transactionDate)
        transactionDetails.transactionDetailsId = 0L
        transactionDetails.overheadCost = 0.0d
        transactionDetails.transactionTypeId = transactionTypeConsumption.id
        transactionDetails.adjustmentParentId = 0L
        transactionDetails.approvedOn = null
        transactionDetails.approvedBy = 0L
        transactionDetails.isIncrease = false
        transactionDetails.isCurrent = true

        return transactionDetails
    }

    /**
     * Build the parent object of production (InvInventoryTransactionDetails)
     * @param gridModelProduction -Serialized GridObject from UI
     * @param params -Serialized parameters from UI
     * @return -InvInventoryTransactionDetails object (production)
     */
    private InvInventoryTransactionDetails buildTransactionDetailsProd(def gridModelProduction, GrailsParameterMap params, long companyId) {
        long inventoryId = Long.parseLong(params.inventoryId.toString())
        InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
        SystemEntity transactionTypeProduction = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)

        InvInventoryTransactionDetails transactionDetails = new InvInventoryTransactionDetails()

        transactionDetails.version = 0
        transactionDetails.inventoryId = inventory.id
        transactionDetails.inventoryTypeId = inventory.typeId
        transactionDetails.inventoryTransactionId = 0L          // set after parent save

        long itemId = Long.parseLong(gridModelProduction.cell[0].toString())   //Positional value of Grid object
        Item item = (Item) itemCacheUtility.read(itemId)
        transactionDetails.itemId = item.id

        transactionDetails.vehicleId = 0L
        transactionDetails.vehicleNumber = null
        transactionDetails.suppliedQuantity = Double.parseDouble(gridModelProduction.cell[3].toString()) //Positional value of Grid object
        transactionDetails.actualQuantity = Double.parseDouble(gridModelProduction.cell[3].toString())   //Positional value of Grid object
        transactionDetails.shrinkage = 0.0d
        transactionDetails.rate = 0.0d
        transactionDetails.supplierChalan = null
        transactionDetails.stackMeasurement = null
        transactionDetails.fifoQuantity = 0.0d
        transactionDetails.lifoQuantity = 0.0d
        transactionDetails.acknowledgedBy = 0L
        transactionDetails.createdOn = new Date()
        transactionDetails.createdBy = invSessionUtil.appSessionUtil.getAppUser().id
        transactionDetails.updatedBy = 0L
        transactionDetails.comments = null
        transactionDetails.mrfNo = null
        transactionDetails.transactionDate = DateUtility.parseMaskedDate(params.transactionDate)
        transactionDetails.transactionDetailsId = 0L
        transactionDetails.transactionTypeId = transactionTypeProduction.id
        transactionDetails.adjustmentParentId = 0L
        transactionDetails.approvedOn = null
        transactionDetails.approvedBy = 0L
        transactionDetails.isIncrease = true
        transactionDetails.isCurrent = true

        long productionLineItemId = Long.parseLong(params.productionLineItemId.toString())
        Object invProductionDetails = invProductionDetailsCacheUtility.getProdDetailsByLineItemAndItemId(productionLineItemId, item.id)
        transactionDetails.overheadCost = invProductionDetails.overheadCost

        return transactionDetails
    }

    /**
     * Method to check consumable raw-material stock to consume in an inventory of a particular item
     * @param rawMaterialsRow -GridObject data receive from UI
     *      rawMaterialsRow.cell[0] (itemId) Which item is to be consumed
     *      rawMaterialsRow.cell[3] (quantity) total quantity to be consumed
     * @param inventoryId -Inventory.id
     *
     * @return -if Stock is not available then return specific message otherwise return null
     */
    private String checkConsumableStock(Object rawMaterialsRow, long inventoryId) {
        long itemId = Long.parseLong(rawMaterialsRow.cell[0].toString())  //Positional value of Grid object
        double quantity = Double.parseDouble(rawMaterialsRow.cell[3].toString()) //Positional value of Grid object
        double consumableStockQuantity = getConsumableStock(inventoryId, itemId)
        if (quantity > consumableStockQuantity) {
            Item item = (Item) itemCacheUtility.read(itemId)
            return "Insufficiant stock of ${item.name}"
        }
        return null
    }

    /**
     * Method to create parents(InvInventoryTransaction) & children(InvInventoryTransactionDetails) of both Consumption and Production
     *
     * @param storeTransactionCon -TransactionTypeConsumptionObject (Parent)
     * @param storeTransactionProd -TransactionTypeProductionObject (Parent)
     * @param lstConsumptionDetails -TransactionTypeConsumptionDetailsObjectList (Children)
     * @param lstProductionDetails -TransactionTypeProductionDetailsObjectList (Children)
     * @param project -Project object
     * @return -if all transactions occur successfully then return TRUE otherwise return FALSE
     */
    private boolean createForProductionWithConsumption(InvInventoryTransaction storeTransactionCon, InvInventoryTransaction storeTransactionProd,
                                                       List<InvInventoryTransactionDetails> lstConsumptionDetails, List<InvInventoryTransactionDetails> lstProductionDetails,
                                                       Project project) {
        // save the parent object for consumption then save children
        invInventoryTransactionService.create(storeTransactionCon)
        // calculate consumption cost for auto approved
        double consumptionCost = 0.0d
        for (int i = 0; i < lstConsumptionDetails.size(); i++) {
            if (project.isApproveProduction) {
                double consumptionRate = calculateValuationForItem(lstConsumptionDetails[i].actualQuantity, lstConsumptionDetails[i].inventoryId, lstConsumptionDetails[i].itemId)
                lstConsumptionDetails[i].rate = consumptionRate
                lstConsumptionDetails[i].approvedBy = lstConsumptionDetails[i].createdBy
                lstConsumptionDetails[i].approvedOn = new Date()

                consumptionCost = consumptionCost + (lstConsumptionDetails[i].actualQuantity * consumptionRate)
            }
            lstConsumptionDetails[i].inventoryTransactionId = storeTransactionCon.id   //Set parentId as the inventoryTransactionId of child
            invInventoryTransactionDetailsService.create(lstConsumptionDetails[i])
        }

        // save the parent object for production then save children
        storeTransactionProd.transactionId = storeTransactionCon.id    // transaction id of consumption object
        invInventoryTransactionService.create(storeTransactionProd)

        // calculate production cost for auto approved
        double totalProductionQuantity = 0.0d
        double productionRate = 0.0d
        if (project.isApproveProduction) {
            for (int i = 0; i < lstProductionDetails.size(); i++) {
                double productionQuantity = lstProductionDetails[i].actualQuantity
                totalProductionQuantity = totalProductionQuantity + productionQuantity
            }
            productionRate = consumptionCost / totalProductionQuantity
        }

        for (int i = 0; i < lstProductionDetails.size(); i++) {
            if (project.isApproveProduction) {
                double rate = productionRate + lstProductionDetails[i].overheadCost
                lstProductionDetails[i].rate = rate
                lstProductionDetails[i].approvedBy = lstProductionDetails[i].createdBy
                lstProductionDetails[i].approvedOn = new Date()
            }
            lstProductionDetails[i].inventoryTransactionId = storeTransactionProd.id  //Set parentId as the inventoryTransactionId of child
            invInventoryTransactionDetailsService.create(lstProductionDetails[i])
        }
        return true
    }

    private static final String CONSUMABLE_STOCK_QUERY = """
        SELECT consumeable_stock
        FROM vw_inv_inventory_consumable_stock
        WHERE inventory_id=:inventoryId
        AND item_id=:itemId
    """

    /**
     * Method returns Consumable-Stock-Amount of an item in an inventory using view(vw_inv_inventory_consumable_stock)
     * @param inventoryId -Inventory.id
     * @param itemId -Item.id
     *
     * @return double value
     */
    private double getConsumableStock(long inventoryId, long itemId) {
        Map queryParams = [
                inventoryId: inventoryId,
                itemId: itemId
        ]
        List<GroovyRowResult> result = executeSelectSql(CONSUMABLE_STOCK_QUERY, queryParams)
        if (result.size() > 0) {
            return (double) result[0].consumeable_stock
        }
        return 0.0d
    }

    /**
     * Method to check AvailableStock of raw materials for consume(at approval)
     * @param rawMaterialsRow -GridObject data receive from UI
     *      rawMaterialsRow.cell[0] (itemId) Which item is to be consumed
     *      rawMaterialsRow.cell[3] (quantity) total quantity to be consumed
     * @param inventoryId -Inventory.id
     * @return -if stock is available to consume at approval then return null otherwise return specific message
     */
    private String checkAvailableStock(Object rawMaterialsRow, long inventoryId) {
        long itemId = Long.parseLong(rawMaterialsRow.cell[0].toString())  //Positional value of Grid object
        double quantity = Double.parseDouble(rawMaterialsRow.cell[3].toString()) //Positional value of Grid object
        double availableStockQuantity = getAvailableStock(inventoryId, itemId)
        if (quantity > availableStockQuantity) {
            Item item = (Item) itemCacheUtility.read(itemId)
            return "Item ${item.name} is not available in stock"
        }
        return null
    }

    /**
     * Method returns available_stock of an item in an inventory using view(vw_inv_inventory_stock)
     * @param inventoryId -Inventory.id
     * @param itemId -Item.id
     *
     * @return double value
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
     * Method to get itemRate based on valuation of that item
     * @param inventoryId -Inventory.id
     * @param itemId -Item.id
     * @param outQuantity -total consumed quantity
     *
     * @return double value
     */
    private double calculateValuationForItem(double outQuantity, long inventoryId, long itemId) {
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
                throw new RuntimeException('Failed to calculateValuationForItem due to unrecognized VALUATION_TYPE')
        }
        return itemRate
    }

    /**
     * Returns the rate for FIFO measurement
     * @param inventoryId -Inventory.id
     * @param itemId -Item.id
     * @param outQuantity -total consumed quantity
     *
     * @return double value
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
     * @param inventoryId -Inventory.id
     * @param itemId -Item.id
     *
     * @return List < GroovyRowResult >
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
     * @param inventoryTransactionDetails -InventoryTransactionDetails object (child)
     * @return int value(if return value <=0 : throw exception to rollback all DB transaction)
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
     * @param inventoryId -Inventory.id
     * @param itemId -Item.id
     * @param outQuantity -total consumed quantity
     *
     * @return double value
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
     * @param inventoryId -Inventory.id
     * @param itemId -Item.id
     *
     * @return List < GroovyRowResult >
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
     * @param inventoryTransactionDetails -InventoryTransactionDetails object (child)
     * @return int value(if return value <=0 : throw exception to rollback all DB transaction)
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
     * @param inventoryId -Inventory.id
     * @param itemId -Item.id
     *
     * @return double value
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

    /**
     * Returns list of groovyRowResult to calculate averageQuantity of an item
     * @param inventoryId -Inventory.id
     * @param itemId -Item.id
     *
     * @return list of GroovyRowResult
     */
    private List<GroovyRowResult> getListForAverage(long inventoryId, long itemId) {

        Map queryParams = [
                itemId: itemId,
                inventoryId: inventoryId
        ]
        List<GroovyRowResult> lstAverage = executeSelectSql(SELECT_QUERY, queryParams)
        return lstAverage
    }
}