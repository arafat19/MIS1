package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.ValuationTypeCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.integration.fixedasset.FixedAssetPluginConnector
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
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
 * Create new inventoryConsumption (against a budget) and show in grid
 * For details go through Use-Case doc named 'CreateForInventoryConsumptionDetailsActionService'
 */
class CreateForInventoryConsumptionDetailsActionService extends BaseService implements ActionIntf {

    AppMailService appMailService
    InvInventoryTransactionService invInventoryTransactionService
    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired(required = false)
    FixedAssetPluginConnector fixedAssetImplService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    ValuationTypeCacheUtility valuationTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private static final String TRANSACTION_CODE = "CreateForInventoryConsumptionDetailsActionService"
    private static final String INV_CONSUMPTION_NOT_FOUND = "Inventory-Consumption Transaction not found"
    private static final String SERVER_ERROR_MESSAGE = "Fail to add material for Inventory-Consumption Transaction"
    private static final String INV_CONSUMPTION_SAVE_FAILURE_MESSAGE = "Can not add material for Inventory-Consumption Transaction"
    private static final String INV_CONSUMPTION_SAVE_SUCCESS_MESSAGE = "Item has been added successfully for Inventory-Consumption Transaction"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String FIXED_ASSET_NOT_FOUND_ERROR = "Fixed asset not found"
    private static final String ITEM_LIST = "itemList"
    private static final String UNAVAILABLE_BUDGET_QUANTITY = "Item quantity exceeds its budget quantity"
    private static final String INSUFFICIENT_STOCK_QUANTITY = "Insufficient stock"
    private static final String INV_TRANSACTION_DETAILS_OBJ = "invInventoryTransactionDetails"
    private static final String INV_TRANSACTION_OBJ = "invInventoryTransaction"
    private static final String FORMAT_TWO_DECIMAL_PLACE = "%.2f"
    private static final String APPROVED = "approved"
    private static final String ERROR_SENDING_MAIL = "Inventory consumption saved but notification mail not send"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Inventory consumption saved but notification mail not send due to absence of mail template"
    private static final String MAIL_RECIPIENT_NOT_FOUND = "Inventory consumption saved but notification mail not send due to absence of recipient"
    private static final String PROJECT_OBJ = "project"
    private static final String BUDGET_OBJ = "budget"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * validate different criteria to consume item. Such as :
     *      Check existence of InvInventoryTransaction(Parent) Obj,
     *      Check existence of SysConfiguration Obj(SysConfiguration is to check weather consumption will be approved at creation or not),
     *      Check corresponding budgetItem & isConsumedAgainstFixedAsset of budgetItem
     *      Check availableStockAmount to consume,
     *
     * @Params params -Receives serialized parameters send from UI
     * @Params obj -N/A
     *
     * @Return -a map containing all objects necessary for execute (SysConfiguration, InvInventoryTransaction(Parent), InvInventoryTransactionDetails(child))
     * map -contains isError(true/false) depending on method success
     */
    @Transactional
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check here the common params are present
            if (!parameterMap.inventoryTransactionId) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            long invTransactionId = Long.parseLong(parameterMap.inventoryTransactionId.toString())
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(invTransactionId)
            if (!invInventoryTransaction) {
                result.put(Tools.MESSAGE, INV_CONSUMPTION_NOT_FOUND)
                return result
            }
            InvInventoryTransactionDetails invInventoryTransactionDetails = buildInventoryTransactionDetails(parameterMap, invInventoryTransaction)

            InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(invInventoryTransaction.inventoryId)
            Project project = (Project) projectCacheUtility.read(inventory.projectId)
            // check available stock from view
            double availableStock
            if (project.isApproveConsumption) { // if true then object will be approved at create event
                //if true then check real stock
                availableStock = getAvailableStock(invInventoryTransactionDetails.inventoryId, invInventoryTransactionDetails.itemId)
            } else { //otherwise check consumable-stock
                availableStock = getConsumableStock(invInventoryTransactionDetails.inventoryId, invInventoryTransactionDetails.itemId)
            }

              if (invInventoryTransactionDetails.actualQuantity > availableStock) {//check availability of item stock
                result.put(Tools.MESSAGE, INSUFFICIENT_STOCK_QUANTITY)
                return result
            }

            //check existence of corresponding budgetDetails object
            Object budgetDetails = (Object) budgetImplService.readBudgetDetailsByBudgetAndItem(invInventoryTransaction.budgetId, invInventoryTransactionDetails.itemId)
            if (!budgetDetails) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            //if budgetItem property of corresponding selected item is : isConsumedAgainstFixedAsset == true
            //  then fixed asset must be given
            if (budgetDetails.isConsumedAgainstFixedAsset == true
                    && (invInventoryTransactionDetails.fixedAssetId <= 0 || invInventoryTransactionDetails.fixedAssetDetailsId <= 0)) {
                result.put(Tools.MESSAGE, FIXED_ASSET_NOT_FOUND_ERROR)
                return result
            }

         /*   //check available stock in budget
            double availableBudgetQuantity = Double.parseDouble(budgetDetails.quantity.toString())
            double totalConsumedQuantity = getTotalConsumedQuantity(invInventoryTransaction.budgetId, invInventoryTransactionDetails.itemId) + invInventoryTransactionDetails.actualQuantity
            if (totalConsumedQuantity > availableBudgetQuantity) {
                result.put(Tools.MESSAGE, UNAVAILABLE_BUDGET_QUANTITY)
                return result
            }*/

            result.put(PROJECT_OBJ, project)
            result.put(BUDGET_OBJ, budgetDetails)
            result.put(INV_TRANSACTION_DETAILS_OBJ, invInventoryTransactionDetails)
            result.put(INV_TRANSACTION_OBJ, invInventoryTransaction)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Method to save InvTransactionConsumptionDetails
     *
     * @param parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains SysConfiguration, InvInventoryTransaction(Parent), InvInventoryTransactionDetails(child))
     * @Return -a map containing all objects necessary for executePostCondition(SysConfiguration, InvInventoryTransaction(Parent), InvInventoryTransactionDetails(child))
     * map -contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj

            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) preResult.get(INV_TRANSACTION_OBJ)
            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) preResult.get(INV_TRANSACTION_DETAILS_OBJ)
            // increase parent item count
            int updateItemCount = increaseItemCount(invInventoryTransactionDetails.inventoryTransactionId)

            // check if auto approve or not
            Project project = (Project) preResult.get(PROJECT_OBJ)
            InvInventoryTransactionDetails newInvTransactionDetails

            if (project.isApproveConsumption) {//if true then object will be approved at create event
                invInventoryTransactionDetails.approvedBy = invSessionUtil.appSessionUtil.getAppUser().id
                invInventoryTransactionDetails.approvedOn = new Date()

                //get consumptionRate by calculatingValuation
                double unitValuation = calculateValuationForOut(invInventoryTransactionDetails.actualQuantity, invInventoryTransactionDetails.inventoryId, invInventoryTransactionDetails.itemId)
                invInventoryTransactionDetails.rate = unitValuation
                newInvTransactionDetails = invInventoryTransactionDetailsService.create(invInventoryTransactionDetails)//create+approve transactionDetails

                // increase total consumption at budget details
                double totalConsumedQuantity = getTotalConsumedQuantity(invInventoryTransaction.budgetId, invInventoryTransactionDetails.itemId)
                int updateConsumption = increaseTotalConsumption(invInventoryTransaction.budgetId, invInventoryTransactionDetails.itemId, totalConsumedQuantity )

            } else {
                newInvTransactionDetails = invInventoryTransactionDetailsService.create(invInventoryTransactionDetails) //only create transactionDetails
            }

            if (!newInvTransactionDetails) {
                result.put(Tools.MESSAGE, INV_CONSUMPTION_SAVE_FAILURE_MESSAGE)
                return result
            }

            result.put(PROJECT_OBJ, project)
            result.put(INV_TRANSACTION_DETAILS_OBJ, newInvTransactionDetails)
            result.put(INV_TRANSACTION_OBJ, invInventoryTransaction)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to create inventory consumption')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Method to send mail depending on SysConfiguration
     *
     * @param parameters -N/A
     * @Params obj -Receives map from execute which contains SysConfiguration, InvInventoryTransaction(Parent), InvInventoryTransactionDetails(child))
     * @Return -a map containing all objects necessary for executePostCondition(SysConfiguration, InvInventoryTransaction(Parent), InvInventoryTransactionDetails(child))
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePostCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            Project project = (Project) executeResult.get(PROJECT_OBJ)
            // if true then approve in create event and don't send email
            if (project.isApproveConsumption) {
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                return result
            }
            InvInventoryTransactionDetails newInvInventoryTransactionDetails = (InvInventoryTransactionDetails) executeResult.get(INV_TRANSACTION_DETAILS_OBJ)
            AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, project.companyId, true)
            if (!appMail) {  //if no mailBody found to sent then return
                result.put(Tools.MESSAGE, MAIL_TEMPLATE_NOT_FOUND)
                return result
            }

            InvInventory invInventory = (InvInventory) invInventoryCacheUtility.read(newInvInventoryTransactionDetails.inventoryId)
            Item item = (Item) itemCacheUtility.read(newInvInventoryTransactionDetails.itemId)
            appMail.subject = appMail.subject + Tools.SINGLE_SPACE + invInventory.name
            //@todo:mirza >> send newInvInventoryTransactionDetails object to following method when plugin integration is done
            String strCreatedOn = DateUtility.getDateTimeFormatAsString(newInvInventoryTransactionDetails.createdOn)
            String quantityUnit = String.format(FORMAT_TWO_DECIMAL_PLACE, newInvInventoryTransactionDetails.actualQuantity) + Tools.SINGLE_SPACE + item.unit
            Boolean mailSend = appMailService.sendMailForInventoryConsumptionApproval(invInventory.projectId, appMail, invInventory.name, item.name, quantityUnit, strCreatedOn)
            if (!mailSend) {
                result.put(Tools.MESSAGE, MAIL_RECIPIENT_NOT_FOUND)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_SENDING_MAIL)
            return result
        }
    }

    /**
     * Wrap unapprovedConsumption object for grid
     * @param obj -map returned from executePostCondition
     * @return -a map containing all objects necessary for show page (wrappedUnapprovedConsumption)
     * map -contains isError(true/false) depending on method success, itemList for drop-down to consume again
     */
    @Transactional
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Project project = (Project) receiveResult.get(PROJECT_OBJ)
            if (project.isApproveConsumption) {
                result.put(APPROVED, true)
            }

            InvInventoryTransactionDetails invTransactionDetails = (InvInventoryTransactionDetails) receiveResult.get(INV_TRANSACTION_DETAILS_OBJ)
            GridEntity object = new GridEntity()
            object.id = invTransactionDetails.id
            Item item = (Item) itemCacheUtility.read(invTransactionDetails.itemId)
            Item fixedAsset = (Item) itemCacheUtility.read(invTransactionDetails.fixedAssetId)
            Object fixedAssetDetails = fixedAssetImplService.getFixedAssetDetailsById(invTransactionDetails.fixedAssetDetailsId)
            String actualQuantity = Tools.formatAmountWithoutCurrency(invTransactionDetails.actualQuantity) + Tools.SINGLE_SPACE + item.unit
            String transactionDate = DateUtility.getLongDateForUI(invTransactionDetails.transactionDate)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(invTransactionDetails.createdBy)
            object.cell = [
                    Tools.LABEL_NEW,
                    invTransactionDetails.id,
                    item.name,
                    actualQuantity,
                    transactionDate,
                    fixedAsset ? fixedAsset.name : Tools.EMPTY_SPACE,
                    fixedAssetDetails ? fixedAssetDetails.name : Tools.EMPTY_SPACE,
                    createdBy.username,
                    Tools.EMPTY_SPACE
            ]
            result.put(Tools.ENTITY, object)
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) receiveResult.get(INV_TRANSACTION_OBJ)

            //get itemList for drop-down to consume again
            List<GroovyRowResult> itemList = getItemListForConsumption(invInventoryTransaction.inventoryId, invInventoryTransaction.budgetId)
            result.put(ITEM_LIST, Tools.listForKendoDropdown(itemList,null,null))
            result.put(Tools.MESSAGE, INV_CONSUMPTION_SAVE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to create consumptionDetails object
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, INV_CONSUMPTION_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     *  build consumptionDetailsObject(child) to save
     * @param parameterMap -GrailsParameterMap sent from UI
     * @param inventoryTransaction -InvInventoryTransaction object(parent)
     * @return invInventoryTransactionDetails object
     */
    private InvInventoryTransactionDetails buildInventoryTransactionDetails(GrailsParameterMap parameterMap, InvInventoryTransaction inventoryTransaction) {

        AppUser user = invSessionUtil.appSessionUtil.getAppUser()
        InvInventoryTransactionDetails invTransactionDetails = new InvInventoryTransactionDetails()

        double actualQuantity = Double.parseDouble(parameterMap.actualQuantity.toString())

        invTransactionDetails.version = 0
        invTransactionDetails.acknowledgedBy = 0L
        invTransactionDetails.actualQuantity = actualQuantity
        invTransactionDetails.approvedBy = 0L
        invTransactionDetails.comments = parameterMap.comments ? parameterMap.comments : null
        invTransactionDetails.createdBy = user.id
        invTransactionDetails.createdOn = new Date()
        invTransactionDetails.fifoQuantity = 0.0d
        invTransactionDetails.inventoryId = inventoryTransaction.inventoryId
        invTransactionDetails.inventoryTransactionId = inventoryTransaction.id
        invTransactionDetails.inventoryTypeId = inventoryTransaction.inventoryTypeId
        invTransactionDetails.lifoQuantity = 0.0d
        invTransactionDetails.itemId = Long.parseLong(parameterMap.itemId.toString())
        invTransactionDetails.mrfNo = null
        invTransactionDetails.rate = 0.0d
        invTransactionDetails.shrinkage = 0.0d
        invTransactionDetails.stackMeasurement = null
        invTransactionDetails.suppliedQuantity = actualQuantity
        invTransactionDetails.supplierChalan = null
        invTransactionDetails.transactionDate = DateUtility.parseMaskedFromDate(parameterMap.transactionDate.toString())
        invTransactionDetails.transactionDetailsId = 0L
        invTransactionDetails.updatedBy = 0L
        invTransactionDetails.updatedOn = null
        invTransactionDetails.vehicleId = 0L
        invTransactionDetails.vehicleNumber = null

        String fixedAssetId = parameterMap.fixedAssetId ? parameterMap.fixedAssetId : 0
        invTransactionDetails.fixedAssetId = Long.parseLong(fixedAssetId)

        String fixedAssetDetailsId = parameterMap.fixedAssetDetailsId ? parameterMap.fixedAssetDetailsId : 0
        invTransactionDetails.fixedAssetDetailsId = Long.parseLong(fixedAssetDetailsId)

        invTransactionDetails.adjustmentParentId = 0L
        invTransactionDetails.approvedOn = null
        invTransactionDetails.isIncrease = false
        invTransactionDetails.isCurrent = true
        invTransactionDetails.transactionTypeId = inventoryTransaction.transactionTypeId
        invTransactionDetails.overheadCost = 0.0d

        return invTransactionDetails
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

    private static final String ITEM_LST_CONSUM_QUERY = """
            SELECT
                vcs.item_id as id, item.name, item.unit, vcs.consumeable_stock as quantity,
                budget_details.is_consumed_against_fixed_asset
            FROM vw_inv_inventory_consumable_stock vcs
                LEFT JOIN budg_budget_details budget_details ON vcs.item_id = budget_details.item_id
                LEFT JOIN item ON item.id = vcs.item_id
            WHERE vcs.inventory_id=:inventoryId
                AND budget_details.budget_id=:budgetId
                AND vcs.consumeable_stock > 0
                AND item.is_individual_entity = false
                ORDER BY item.name
        """
    /**
     * Get List of Consumable stock of available item from view(vw_inv_inventory_consumable_stock)
     *
     * @param inventoryId -Inventory.id
     * @param budgetId -Budget.id
     *
     * @return - a map containing list of GroovyRowResult(itemList)
     */
    public List<GroovyRowResult> getItemListForConsumption(long inventoryId, long budgetId) {
        Map queryParams = [inventoryId: inventoryId, budgetId: budgetId]
        List<GroovyRowResult> materialList = executeSelectSql(ITEM_LST_CONSUM_QUERY, queryParams)
        return materialList
    }

    private static final String CONSUMABLE_STOCK_QUERY = """
        SELECT consumeable_stock
        FROM vw_inv_inventory_consumable_stock
        WHERE inventory_id=:inventoryId
            AND item_id=:itemId
    """
    /**
     * Method to check consumable stock to consume in an inventory of an item using view(vw_inv_inventory_consumable_stock)
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

    private static final String TOTAL_APPROVED_CONSUMED_QUANTITY_QUERY = """
            SELECT  coalesce(sum(iitd.actual_quantity),0) AS total
        FROM inv_inventory_transaction_details iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
        WHERE iit.budget_id=:budgetId
            AND iitd.item_id=:itemId
            AND iit.transaction_type_id=:transactionTypeId
            AND iitd.approved_by > 0
            AND iitd.is_current = true
    """
    /**
     * Method to check totalConsumedAmount of selected item against budget
     * @Param budgetId -Budget.id
     * @Param itemId -Item.id
     *
     * @Return -double value (totalConsumedQuantity)
     */
    private double getTotalConsumedQuantity(long budgetId, long itemId) {
        // pull transactionType object
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        Map queryParams = [
                transactionTypeId: transactionTypeCons.id,
                budgetId: budgetId,
                itemId: itemId
        ]
        List<GroovyRowResult> invInventoryTransactionDetailsList = executeSelectSql(TOTAL_APPROVED_CONSUMED_QUANTITY_QUERY, queryParams)
        double totalConsumedQuantity = Double.parseDouble(invInventoryTransactionDetailsList[0].total.toString())
        return totalConsumedQuantity
    }

    /**
     * Method to get itemRate based on valuation of that item
     * @Param inventoryId -Inventory.id
     * @Param itemId -Item.id
     * @Param outQuantity -total consumed quantity
     *
     * @Return double value(rate)
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

    private static final String INCREASE_ITEM_COUNT_QUERY = """
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
        int updateCount = executeUpdateSql(INCREASE_ITEM_COUNT_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException("Fail to increase item count")
        }
        return updateCount
    }

    private static final String INCREASE_TOTAL_CONSUMPTION_QUERY = """
        UPDATE budg_budget_details SET
            total_consumption = :totalConsumedQuantity,
            version=version+1
        WHERE
            budget_id = :budgetId
            AND item_id = :itemId
    """
    /**
     * method increase total consumption of parent
     * @Param budgetId -
     * @Param itemId -
     * @Return int -
     */
    private int increaseTotalConsumption(long budgetId, long itemId, double totalConsumedQuantity) {
        Map queryParams = [
                budgetId: budgetId,
                itemId: itemId,
                totalConsumedQuantity: totalConsumedQuantity
        ]
        int updateTotalConsumption = executeUpdateSql(INCREASE_TOTAL_CONSUMPTION_QUERY, queryParams)

        if (updateTotalConsumption <= 0) {
            throw new RuntimeException("Fail to increase total consumption")
        }
        return updateTotalConsumption
    }
}