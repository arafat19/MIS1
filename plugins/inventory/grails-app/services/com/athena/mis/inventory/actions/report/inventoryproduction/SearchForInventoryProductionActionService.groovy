package com.athena.mis.inventory.actions.report.inventoryproduction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.entity.InvProductionLineItem
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
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Search and show specific inventory production report
 *  For details go through Use-Case doc named 'SearchForInventoryProductionActionService'
 */
class SearchForInventoryProductionActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvInventoryCacheUtility inventoryCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility inventoryTypeCacheUtility
    @Autowired
    InvProductionLineItemCacheUtility productionLineItemCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private static final String FAILURE_MSG = "Fail to generate inventory production"
    private static final String TRANSACTION_PARENT = "transactionParent"
    private static final String TRANSACTION_MAP = "transactionMap"
    private static final String LST_RAW_MATERIAL = "lstRawMaterial"
    private static final String LST_FINISHED_PRODUCT = "lstFinishedProduct"
    private static final String TRANSACTION_NOT_EXISTS = "Inventory transaction does not exists by this ID"
    private static final String INVALID_ID = "Invalid transaction Id"
    private static final String NOT_PRODUCTION_ID = "Transaction ID is not associated with production"
    private static final String HAS_BEEN_REVERSED = "This production transaction has been reversed"
    private static final String PRODUCTION_ID = "Production ID"
    private static final String TRACE_CHALAN_NO = "Trace/Chalan No"
    private static final String UNKNOWN_ID_TYPE = "Unknown ID Type"
    private static final String NOT_APPROVED = "Transaction is not approved"

    /**
     * Check input validation
     * Check existence of transaction object
     * Check if the transaction is production or not
     * Check if production is approved or not
     * Check if production has been reversed
     * If search by trace/chalan no then check existence of parent object and validation of parent object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long transactionId = 0L
            // checking input validation
            try {
                transactionId = Long.parseLong(params.transactionId.toString())
            } catch (Exception e) {
                result.put(Tools.MESSAGE, INVALID_ID)
                return result
            }
            String IdType = params.IdType.toString()
            InvInventoryTransaction transactionParent = null
            long companyId = invSessionUtil.appSessionUtil.getCompanyId()
            switch (IdType) {
                case PRODUCTION_ID:
                    // checking existence of inventory transaction object(inventory production)
                    transactionParent = invInventoryTransactionService.read(transactionId)
                    if ((!transactionParent) || (transactionParent.companyId != companyId)) {
                        result.put(Tools.MESSAGE, TRANSACTION_NOT_EXISTS)
                        return result
                    }
                    // check if the transaction is production or not
                    if (transactionParent.invProductionLineItemId <= 0) {
                        result.put(Tools.MESSAGE, NOT_PRODUCTION_ID)
                        return result
                    }
                    // check if production is approved or not
                    if (!transactionParent.isApproved) {
                        result.put(Tools.MESSAGE, NOT_APPROVED)
                        return result
                    }
                    // check if production has been reversed
                    if (transactionParent.itemCount <= 0) {
                        result.put(Tools.MESSAGE, HAS_BEEN_REVERSED)
                        return result
                    }
                    result.put(TRANSACTION_PARENT, transactionParent)
                    break
                case TRACE_CHALAN_NO:
                    // checking existence of inventory transaction details object(inventory production details)
                    InvInventoryTransactionDetails transactionDetails = invInventoryTransactionDetailsService.read(transactionId)
                    if (!transactionDetails) {
                        result.put(Tools.MESSAGE, TRANSACTION_NOT_EXISTS)
                        return result
                    }
                    // pull transaction type object
                    SystemEntity transactionTypePro = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)
                    SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)
                    SystemEntity transactionTypeAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_ADJUSTMENT, companyId)

                    // check if transaction details is other than production
                    if (
                            (transactionDetails.transactionTypeId != transactionTypeCons.id) &&
                                    (transactionDetails.transactionTypeId != transactionTypePro.id) &&
                                    (transactionDetails.transactionTypeId != transactionTypeAdj.id)
                    ) {
                        result.put(Tools.MESSAGE, NOT_PRODUCTION_ID)
                        return result
                    }
                    // check if production details is approved or not
                    if (transactionDetails.approvedBy <= 0) {
                        result.put(Tools.MESSAGE, NOT_APPROVED)
                        return result
                    }
                    // check if production details has been reversed
                    if (!transactionDetails.isCurrent) {
                        result.put(Tools.MESSAGE, HAS_BEEN_REVERSED)
                        return result
                    }
                    // pull the parent object
                    transactionParent = invInventoryTransactionService.read(transactionDetails.inventoryTransactionId)
                    // check if parent object exists or not
                    if ((!transactionParent) || (transactionParent.companyId != companyId)) {
                        result.put(Tools.MESSAGE, TRANSACTION_NOT_EXISTS)
                        return result
                    }
                    // check if the transaction(parent) is production or not
                    if (transactionParent.invProductionLineItemId <= 0) {
                        result.put(Tools.MESSAGE, NOT_PRODUCTION_ID)
                        return result
                    }
                    result.put(TRANSACTION_PARENT, transactionParent)
                    break
                default:
                    result.put(Tools.MESSAGE, UNKNOWN_ID_TYPE)
                    return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Check if transaction parent is production or consumption
     * Get list of raw materials and finished products
     * Get total amount of raw materials and total quantity of finished products
     * Get rate of finished product per unit
     * Build inventory transaction map with necessary properties
     * @param parameters -parameter (transactionId -id of inventory transaction object)
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            InvInventoryTransaction transactionParent = (InvInventoryTransaction) preResult.get(TRANSACTION_PARENT)
            // pull transactionType object
            long companyId = invSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

            long transactionParentId = 0L
            // check if transaction parent is production or consumption
            if (transactionParent.transactionTypeId == transactionTypeCons.id) {
                transactionParentId = transactionParent.id
            } else {  //   transactionParent is PRODUCTION parent
                transactionParentId = transactionParent.transactionId
            }
            // get list of raw materials
            List<GroovyRowResult> lstRawMaterial = getRawMaterialByInvTransactionId(transactionParentId)
            // get list of finished products
            List<GroovyRowResult> lstFinishedProduct = getFinishedProductByInvTransactionId(transactionParentId)
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
            LinkedHashMap transactionMap = buildTransactionMap(transactionParentId, transactionParent, totalAmount, ratePerUnit)
            result.put(TRANSACTION_MAP, transactionMap)
            result.put(LST_RAW_MATERIAL, lstRawMaterial  as JSON)
            result.put(LST_FINISHED_PRODUCT, lstFinishedProduct  as JSON)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
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
     * Do nothing for post operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap previousResult = (LinkedHashMap) obj   // cast map returned from previous method
            result.put(Tools.IS_ERROR, previousResult.get(Tools.IS_ERROR))
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Build inventory transaction map with necessary properties
     * @param transactionParentId -id of transaction parent object
     * @param inventoryTransaction - object of InvInventoryTransaction
     * @param totalAmount -total amount of raw materials
     * @param ratePerUnit - rate per unit of finished product
     * @return -a map of with inventory transaction properties
     */
    private LinkedHashMap buildTransactionMap(long transactionParentId, InvInventoryTransaction inventoryTransaction, double totalAmount, double ratePerUnit) {
        InvInventory invInventory = (InvInventory) inventoryCacheUtility.read(inventoryTransaction.inventoryId)
        SystemEntity inventoryType = (SystemEntity) inventoryTypeCacheUtility.read(inventoryTransaction.inventoryTypeId)
        InvProductionLineItem invProductionLineItem = (InvProductionLineItem) productionLineItemCacheUtility.read(inventoryTransaction.invProductionLineItemId)

        LinkedHashMap transactionMap = [
                transactionId: transactionParentId,
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

