package com.athena.mis.inventory.actions.report.inventoryproduction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvProductionLineItem
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvInventoryTypeCacheUtility
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Show UI for inventory production report
 * For details go through Use-Case doc named 'ShowForInventoryProductionActionService'
 */
class ShowForInventoryProductionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    InvInventoryCacheUtility inventoryCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility inventoryTypeCacheUtility
    @Autowired
    InvProductionLineItemCacheUtility productionLineItemCacheUtility

    private static final String FAILURE_MSG = "Fail to generate inventory production"
    private static final String TRANSACTION_MAP = "transactionMap"
    private static final String LST_RAW_MATERIAL = "lstRawMaterial"
    private static final String LST_FINISHED_PRODUCT = "lstFinishedProduct"
    private static final String TRANSACTION_NOT_EXISTS = "Inventory transaction does not exists by this ID"
    private static final String INVALID_ID = "Invalid transaction Id"
    private static final String NOT_PRODUCTION_ID = "This transaction ID is not transaction ID of production"
    private static final String HAS_BEEN_REVERSED = "This production transaction has been reversed"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * If navigated from approved production page then
     * 1. check input validation
     * 2. check if inventory transaction(inventory production) object exists or not
     * 3. check if the inventory transaction object is production or not
     * 4. check if production has been reversed
     * 5. get list of raw materials and finished products
     * 6. get total amount of raw materials and total quantity of finished products
     * 7. get rate of finished product per unit
     * 8. build inventory transaction map with necessary properties
     * @param parameters -parameter (transactionId -id of inventory transaction object)
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success and other necessary objects
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (params.transactionId) {
                long transactionId
                // checking input validation
                try {
                    transactionId = Long.parseLong(params.transactionId.toString())
                } catch (Exception e) {
                    result.put(Tools.MESSAGE, INVALID_ID)
                    log.error(e.getMessage())
                    return result
                }
                // checking existence of inventory transaction object(inventory production)
                InvInventoryTransaction trProduction = invInventoryTransactionService.read(transactionId)
                if (!trProduction) {
                    result.put(Tools.MESSAGE, TRANSACTION_NOT_EXISTS)
                    return result
                }
                // check if the transaction is production or not
                if (trProduction.invProductionLineItemId <= 0) {
                    result.put(Tools.MESSAGE, NOT_PRODUCTION_ID)
                    return result
                }
                // check if production has been reversed
                if (trProduction.itemCount <= 0) {
                    result.put(Tools.MESSAGE, HAS_BEEN_REVERSED)
                    return result
                }
                // get list of raw materials
                List<GroovyRowResult> lstRawMaterial = getRawMaterialByInvTransactionId(trProduction.id)
                // get list of finished products
                List<GroovyRowResult> lstFinishedProduct = getFinishedProductByInvTransactionId(trProduction.id)
                // get total amount of raw materials
                double totalAmount = 0d
                for (int i = 0; i < lstRawMaterial.size(); i++) {
                    totalAmount = totalAmount + lstRawMaterial[i].total_amount
                }
                // get total quantity of finished products
                double totalFinishProductQuantity = 0d
                for (int i = 0; i < lstFinishedProduct.size(); i++) {
                    totalFinishProductQuantity = totalFinishProductQuantity + lstFinishedProduct[i].quantity
                }
                // get rate of finished product per unit
                double ratePerUnit = totalAmount / totalFinishProductQuantity
                // build inventory transaction map with necessary properties
                LinkedHashMap transactionMap = buildTransactionMap(trProduction, totalAmount, ratePerUnit)
                result.put(TRANSACTION_MAP, transactionMap)
                result.put(LST_RAW_MATERIAL, lstRawMaterial as JSON)
                result.put(LST_FINISHED_PRODUCT, lstFinishedProduct  as JSON)
                result.put(Tools.IS_ERROR, Boolean.FALSE)
            }
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
     * Do nothing for build success result for UI
     */
    public Object buildSuccessResultForUI(Object result) {
        return null
    }

    /**
     * Do nothing for build failure result for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Build inventory transaction map with necessary properties
     * @param inventoryTransaction - object of InvInventoryTransaction
     * @param totalAmount -total amount of raw materials
     * @param ratePerUnit - rate per unit of finished product
     * @return -a map of with inventory transaction properties
     */
    private LinkedHashMap buildTransactionMap(InvInventoryTransaction inventoryTransaction, double totalAmount, double ratePerUnit) {
        InvInventory invInventory = (InvInventory) inventoryCacheUtility.read(inventoryTransaction.inventoryId)
        SystemEntity inventoryType = (SystemEntity) inventoryTypeCacheUtility.read(inventoryTransaction.inventoryTypeId)
        InvProductionLineItem invProductionLineItem = (InvProductionLineItem) productionLineItemCacheUtility.read(inventoryTransaction.invProductionLineItemId)

        LinkedHashMap transactionMap = [
                transactionId: inventoryTransaction.id,
                transactionDate: DateUtility.getDateFormatAsString(inventoryTransaction.transactionDate),
                inventoryName: inventoryType.key + Tools.COLON + invInventory.name,
                productionLineItem: invProductionLineItem.name,
                totalAmount: Tools.makeAmountWithThousandSeparator(totalAmount),
                ratePerUnit: Tools.makeAmountWithThousandSeparator(ratePerUnit)
        ]
        return transactionMap
    }

    /**
     * Get list of raw materials
     * @param invTransactionId -id of InventoryTransaction (production) object
     * @return -a list of raw materials
     */
    private List<GroovyRowResult> getRawMaterialByInvTransactionId(long invTransactionId) {

        String queryStr = """
            SELECT iitd.id, item.name AS raw_material,
                   iitd.rate,
                   to_char(iitd.rate,'${Tools.DB_CURRENCY_FORMAT}') AS str_rate,
                   iitd.actual_quantity AS quantity,
                   to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') || ' ' || item.unit AS str_quantity,
                   (iitd.actual_quantity*iitd.rate) AS total_amount,
                   to_char((iitd.actual_quantity*iitd.rate),'${Tools.DB_CURRENCY_FORMAT}') AS str_total_amount
            FROM inv_inventory_transaction_details iitd
                LEFT JOIN inv_inventory_transaction trCon ON trCon.id = iitd.inventory_transaction_id
                LEFT JOIN item ON item.id = iitd.item_id
            WHERE trCon.id =:invTransactionId AND
                  trCon.inv_production_line_item_id > 0 AND
                  iitd.is_current = true
            ORDER BY raw_material
        """
        Map queryParams = [
                invTransactionId: invTransactionId
        ]
        List<GroovyRowResult> rawMaterialList = executeSelectSql(queryStr, queryParams)
        return rawMaterialList
    }

    /**
     * Get list of finished products
     * @param invTransactionId -id of InventoryTransaction (production) object
     * @return -a list of finished products
     */
    private List<GroovyRowResult> getFinishedProductByInvTransactionId(long invTransactionId) {

        String queryStr = """
            SELECT iitd.id, iitd.item_id, item.name AS finished_product,
                   iitd.rate,
                   to_char(iitd.rate,'${Tools.DB_CURRENCY_FORMAT}') AS str_rate,
                   iitd.actual_quantity AS quantity,
                   to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') || ' ' || item.unit AS str_quantity,
                   iitd.overhead_cost AS overhead_cost,
                   to_char(iitd.overhead_cost,'${Tools.DB_CURRENCY_FORMAT}') AS str_overhead_cost,
                   (iitd.actual_quantity * iitd.rate) AS total_amount,
                   to_char((iitd.actual_quantity * iitd.rate),'${Tools.DB_CURRENCY_FORMAT}') AS str_total_amount
            FROM inv_inventory_transaction_details iitd
                LEFT JOIN inv_inventory_transaction trProd ON trProd.id = iitd.inventory_transaction_id
                LEFT JOIN item ON item.id = iitd.item_id
            WHERE trProd.transaction_id =:invTransactionId AND
                  iitd.inventory_transaction_id = trProd.id AND
                  trProd.inv_production_line_item_id > 0 AND
                  iitd.is_current = true
            ORDER BY finished_product
        """
        Map queryParams = [
                invTransactionId: invTransactionId
        ]

        List<GroovyRowResult> finishedProductList = executeSelectSql(queryStr, queryParams)
        return finishedProductList
    }
}

