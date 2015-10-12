package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvProductionDetailsCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Common Class to get information of a inventory-production-details (both for approved OR unapproved) and show information on UI
 *  For details go through Use-Case doc named 'SelectForInvProductionWithConsumptionActionService'
 */
class SelectForInvProductionWithConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String PRODUCTION_NOT_FOUND_MASSAGE = "Selected inventory production not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select inventory production"
    private static final String ENTITY_CONSUMPTION = "entityConsumption"
    private static final String ENTITY_PRODUCTION = "entityProduction"
    private static final String LST_CONSUMPTION_DETAILS = "lstConsumptionDetails"
    private static final String LST_PRODUCTION_DETAILS = "lstProductionDetails"
    private static final String GRID_MODEL_CONSUMPTION = "gridModelConsumption"
    private static final String GRID_MODEL_PRODUCTION = "gridModelProduction"
    private static final String LST_RAW_MATERIALS = "lstRawMaterials"
    private static final String LST_FINISHED_PRODUCTS = "lstFinishedProducts"
    private static final String LST_INVENTORIES = "lstInventories"
    private static final String VERSION_CON = 'versionCon'
    private static final String VERSION_PROD = 'versionProd'
    private static final String TRANSACTION_DATE = 'transactionDate'

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    InvProductionDetailsCacheUtility invProductionDetailsCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * @Params params -Receives from UI
     * @Params obj -N/A
     *
     * @Return -a map containing all objects necessary for execute (Parents(invTranConsumption & invTranProduction), Children(invTranConsumptionDetails & invTranProductionDetails))
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            Long consumptionId = Long.parseLong(parameterMap.id.toString())

            InvInventoryTransaction consumptionTransaction = (InvInventoryTransaction) invInventoryTransactionService.read(consumptionId)
            if (!consumptionTransaction) {   //Check existence of consumptionTransaction
                result.put(Tools.MESSAGE, PRODUCTION_NOT_FOUND_MASSAGE)
            }

            // Retrieve productionTransaction object By consumption id
            InvInventoryTransaction productionTransaction = (InvInventoryTransaction) readProductionByConsumptionId(consumptionTransaction.id)
            if (!productionTransaction) {    //Check existence of productionTransaction
                result.put(Tools.MESSAGE, PRODUCTION_NOT_FOUND_MASSAGE)
            }

            //Get list of consumption and production details(children)
            //get list of invTransactionDetails(rawItems/finishedItems) to show on raw/finished item grid
            List<GroovyRowResult> lstConsumptionDetails = listTransactionDetails(consumptionTransaction)
            List<GroovyRowResult> lstProductionDetails = listTransactionDetails(productionTransaction)

            result.put(ENTITY_CONSUMPTION, consumptionTransaction)
            result.put(ENTITY_PRODUCTION, productionTransaction)
            result.put(LST_CONSUMPTION_DETAILS, lstConsumptionDetails)
            result.put(LST_PRODUCTION_DETAILS, lstProductionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRODUCTION_NOT_FOUND_MASSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     *  Method to get information from parents(InvInventoryTransaction) & children(InvInventoryTransactionDetails) to show on UI
     * @Params obj -Receives map from executePreCondition which contains parents(InvInventoryTransaction) & children(InvInventoryTransactionDetails)
     * @Return -map contains isError(true/false), relevant data to show information on UI
     */
    @Transactional(readOnly = true)
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map receiveResult = (LinkedHashMap) obj

            InvInventoryTransaction consumptionTransaction = (InvInventoryTransaction) receiveResult.get(ENTITY_CONSUMPTION)
            InvInventoryTransaction productionTransaction = (InvInventoryTransaction) receiveResult.get(ENTITY_PRODUCTION)
            List<GroovyRowResult> lstConsumptionDetails = (List<GroovyRowResult>) receiveResult.get(LST_CONSUMPTION_DETAILS)
            List<GroovyRowResult> lstProductionDetails = (List<GroovyRowResult>) receiveResult.get(LST_PRODUCTION_DETAILS)

            //Wrap consumptionDetailsList for consumption-grid
            List lstConsumption = wrapRawMaterials(lstConsumptionDetails)
            //Wrap productionDetailsList for production-grid
            List lstProduction = wrapFinishedProducts(lstProductionDetails)

            Map gridModelConsumption = [page: 1, total: lstConsumption.size(), rows: lstConsumption]
            Map gridModelProduction = [page: 1, total: lstProduction.size(), rows: lstProduction]

            result.put(GRID_MODEL_CONSUMPTION, gridModelConsumption)
            result.put(GRID_MODEL_PRODUCTION, gridModelProduction)
            result.put(ENTITY_CONSUMPTION, consumptionTransaction)
            result.put(ENTITY_PRODUCTION, productionTransaction)
            result.put(VERSION_CON, consumptionTransaction.version)
            result.put(VERSION_PROD, productionTransaction.version)

            long productionLineItemId = consumptionTransaction.invProductionLineItemId
            //get list of both type of item(material & finishedProduct)
            Map bothMaterials = invProductionDetailsCacheUtility.getBothMaterialsByLineItem(productionLineItemId)

            long inventoryId = consumptionTransaction.inventoryId
            //get raw-material list and consumableStock quantity
            List lstRawMaterials = listMaterialForInventoryProduction(inventoryId, bothMaterials.lstRaw)

            //get finished-material list
            List lstFinishedMaterials = getFinishedList(bothMaterials.lstFinished)
            result.put(LST_RAW_MATERIALS, Tools.listForKendoDropdown(lstRawMaterials,"name_quantity",null))
            result.put(LST_FINISHED_PRODUCTS, Tools.listForKendoDropdown(lstFinishedMaterials,null,null))

            // get inventory list based on type for drop-down
            InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(consumptionTransaction.inventoryId)
            List lstInventories = invSessionUtil.getUserInventoriesByType(inventory.typeId)
            result.put(LST_INVENTORIES, lstInventories)

            // get transaction date to show
            String transactionDate = DateUtility.getDateForUI(consumptionTransaction.transactionDate)
            result.put(TRANSACTION_DATE, transactionDate)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to select production-with-consumption
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

    /**
     * Method to get list of finished-material(s) to show on drop-down
     * @Param lstFinished -list of finished-material item's id
     * @Return latMaterials -list of finished-material(s)
     */
    private List getFinishedList(List<Long> lstFinished) {
        List latMaterials = []
        for (int i = 0; i < lstFinished.size(); i++) {
            latMaterials << itemCacheUtility.read(lstFinished[i].longValue())
        }
        return latMaterials
    }

    /**
     * Method to wrap raw-materialDetailsObjects to show on raw-materials grid
     * @Param lstTransactionDetails -list of invTransactionDetails (raw-material)
     * @Return -ListOfWrappedInvTransactionDetails(raw-material)
     */
    private List wrapRawMaterials(List<GroovyRowResult> lstTransactionDetails) {
        List lstDetails = []
        GroovyRowResult singleRow
        for (int i = 0; i < lstTransactionDetails.size(); i++) {
            singleRow = lstTransactionDetails[i]
            GridEntity obj = new GridEntity()
            obj.id = singleRow.details_id
            obj.cell = [
                    singleRow.item_id,
                    singleRow.material_name,
                    singleRow.quantity_unit,
                    singleRow.quantity,
                    singleRow.details_id
            ]
            lstDetails << obj
        }
        return lstDetails
    }

    /**
     * Method to wrap finished-materialDetailsObjects to show on finished-materials grid
     * @Param lstTransactionDetails -list of invTransactionDetails (finished-material)
     * @Return -ListOfWrappedInvTransactionDetails(finished-material)
     */
    private List wrapFinishedProducts(List<GroovyRowResult> lstTransactionDetails) {
        List lstDetails = []
        GroovyRowResult singleRow
        for (int i = 0; i < lstTransactionDetails.size(); i++) {
            singleRow = lstTransactionDetails[i]
            GridEntity obj = new GridEntity()
            obj.id = singleRow.details_id
            obj.cell = [
                    singleRow.item_id,
                    singleRow.material_name,
                    singleRow.quantity_unit,
                    singleRow.quantity,
                    singleRow.details_id,
                    singleRow.overhead_cost
            ]
            lstDetails << obj
        }
        return lstDetails
    }

    private static final String READ_PRODUCT_QUERY = """
        SELECT id, version FROM inv_inventory_transaction
        WHERE transaction_type_id = :typeProduction
        AND transaction_id = :transactionId
        """

    /**
     * Method to get productionObject By consumption id
     * @Param consumptionId -InvInventoryTransaction.id
     * @Return -InvInventoryTransaction object exists then return InvInventoryTransaction object
     *          otherwise return null
     */
    private InvInventoryTransaction readProductionByConsumptionId(long consumptionId) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypePro = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)

        Map queryParams = [
                typeProduction: transactionTypePro.id,
                transactionId: consumptionId
        ]
        List result = executeSelectSql(READ_PRODUCT_QUERY, queryParams)
        InvInventoryTransaction productionTransaction = new InvInventoryTransaction()
        if (result && result.size() > 0) {
            productionTransaction.id = result[0].id
            productionTransaction.version = result[0].version
            return productionTransaction
        }
        return null
    }

    /**
     * Method to get list of invTransactionDetails(rawItems/finishedItems) to show on raw/finished item grid
     * @Param invInventoryTransaction -invInventoryTransaction object(consumption/production)
     * @Return List<GroovyRowResult> -list of GroovyRowResult contains : raw-material(s)/finished-material(s) information to show on grid
     */
    private List<GroovyRowResult> listTransactionDetails(InvInventoryTransaction invInventoryTransaction) {
        String queryStr = """
        SELECT details.id details_id, details.inventory_transaction_id AS parent_id, details.item_id, material.name material_name, details.rate,
        (to_char(details.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') || ' ' || material.unit ) AS quantity_unit, (details.rate * details.actual_quantity) AS total,
        details.actual_quantity quantity, details.comments, details.overhead_cost overhead_cost
        FROM inv_inventory_transaction_details details
        LEFT JOIN item material ON material.id  = details.item_id
        WHERE details.inventory_transaction_id = :inventoryTransactionId
        AND is_current= TRUE
        """
        Map queryParams = [inventoryTransactionId: invInventoryTransaction.id]
        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        return result
    }

    /**
     * Method to get list of rawItems to show on drop-down
     * @Param inventoryId -InvInventory.id
     * @Param lstItemIds -list of item's id
     * @Return GroovyRowResult -list of GroovyRowResult contains : raw-material(s) information & consumable_stock
     */
    private List listMaterialForInventoryProduction(long inventoryId, List lstItemIds) {
        String queryStr = """
            SELECT
                material.id AS id,
                material.name AS name,
                material.name || '(' || summary.consumeable_stock || ')' AS name_quantity,
                material.unit AS unit,
                coalesce(summary.consumeable_stock,0) AS quantity
            FROM item  material
            LEFT JOIN vw_inv_inventory_consumable_stock summary ON summary.item_id = material.id
            WHERE material.id IN  (${Tools.buildCommaSeparatedStringOfIds(lstItemIds)})
            AND summary.inventory_id = :inventoryId
            AND material.is_individual_entity = false
            ORDER BY material.name ASC
        """
        Map queryParams = [inventoryId: inventoryId]
        List<GroovyRowResult> materialListWithQnty = executeSelectSql(queryStr, queryParams)
        return materialListWithQnty
    }
}