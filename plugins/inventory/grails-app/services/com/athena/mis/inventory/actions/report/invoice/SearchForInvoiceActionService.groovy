package com.athena.mis.inventory.actions.report.invoice

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.*
import com.athena.mis.application.utility.*
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.*
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Search and show invoice report
 *  For details go through Use-Case doc named 'SearchForInvoiceActionService'
 */
class SearchForInvoiceActionService extends BaseService implements ActionIntf {

    InvInventoryTransactionService invInventoryTransactionService
    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    CustomerCacheUtility customerCacheUtility
    @Autowired
    VehicleCacheUtility vehicleCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService

    private static final String INVOICE_NOT_FOUND = "Chalan not found"
    private static final String FAILURE_MSG = "Fail to generate Chalan"
    private static final String INVOICE_MAP = "invoiceMap"
    private static final String INVENTORY_TRANSACTION_OBJ = "invInventoryTransaction"
    private static final String INVENTORY_TRANSACTION_DETAILS_OBJ = "invInventoryTransactionDetails"
    private static final String INCREASE = "Increase"
    private static final String DECREASE = "Decrease"
    private static final String OF = " of "
    private static final String CHALAN_REVERSED = "Chalan Reversed"
    private static final String SUPPLIER_NAME = "Supplier Name"
    private static final String CUSTOMER_NAME = "Customer Name"
    private static final String INVENTORY_NAME = "Inventory Name"
    private static final String ENTITY_NAME = "Entity Name"

    protected final Logger log = Logger.getLogger(getClass())

    /**
     * Check and get required parameters from UI
     * Check input validation
     * Get inventory transaction and transaction details object
     * Check if user has access to the inventory of the invoice
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            // check required parameter
            if (!params.invoiceNo) {
                result.put(Tools.MESSAGE, INVOICE_NOT_FOUND)
                return result
            }
            long inventoryTransactionId
            // check input validation
            try {
                inventoryTransactionId = Long.parseLong(params.invoiceNo.toString())
            } catch (Exception ex) {
                log.error(ex.getMessage())
                result.put(Tools.MESSAGE, INVOICE_NOT_FOUND)
                return result
            }
            // get inventory transaction details object
            InvInventoryTransactionDetails invInventoryTransactionDetails = invInventoryTransactionDetailsService.read(inventoryTransactionId)
            // check if inventory transaction details object exists or not
            if (!invInventoryTransactionDetails) {
                result.put(Tools.MESSAGE, INVOICE_NOT_FOUND)
                return result
            }
            // get inventory transaction object
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(invInventoryTransactionDetails.inventoryTransactionId)
            // check if user has access to the inventory of the invoice
            boolean hasAccessInventory = invInventoryCacheUtility.hasAccessInventory(invInventoryTransactionDetails.inventoryId)
            if (!hasAccessInventory) {
                result.put(Tools.MESSAGE, INVOICE_NOT_FOUND)
                return result
            }

            result.put(INVENTORY_TRANSACTION_OBJ, invInventoryTransaction)
            result.put(INVENTORY_TRANSACTION_DETAILS_OBJ, invInventoryTransactionDetails)
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
     * Build a map with necessary information to show invoice
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) preResult.get(INVENTORY_TRANSACTION_OBJ)
            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_TRANSACTION_DETAILS_OBJ)
            // build a map for show invoice
            LinkedHashMap invoiceMap = buildInvoiceMap(invInventoryTransaction, invInventoryTransactionDetails)
            result.put(INVOICE_MAP, invoiceMap)
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
     * Do nothing for success operation
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
     * Build a map with necessary information of inventory transaction and transaction details object to show invoice
     * @param invInventoryTransaction -object of InvInventoryTransaction
     * @param invInventoryTransactionDetails -object of InvInventoryTransactionDetails
     * @return -a map with necessary information to show invoice
     */
    private LinkedHashMap buildInvoiceMap(InvInventoryTransaction invInventoryTransaction, InvInventoryTransactionDetails invInventoryTransactionDetails) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_ADJUSTMENT, companyId)
        SystemEntity transactionTypeReAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_REVERSE_ADJUSTMENT, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)
        SystemEntity transactionEntityCustomer = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_CUSTOMER, companyId)
        SystemEntity transactionEntityNone = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_NONE, companyId)

        // get entity name (supplier name for inv in form supplier, from inventory name for inv in from inventory etc.)
        String entityName = Tools.EMPTY_SPACE
        String lblEntityName = ENTITY_NAME

        if (invInventoryTransaction.transactionEntityTypeId == transactionEntitySupplier.id) {
            Supplier supplier = (Supplier) supplierCacheUtility.read(invInventoryTransaction.transactionEntityId)
            entityName = supplier.name
            lblEntityName = SUPPLIER_NAME
        }
        if (invInventoryTransaction.transactionEntityTypeId == transactionEntityCustomer.id) {
            Customer customer = (Customer) customerCacheUtility.read(invInventoryTransaction.transactionEntityId)
            entityName = customer.fullName
            lblEntityName = CUSTOMER_NAME
        }
        if (invInventoryTransaction.transactionEntityTypeId == transactionEntityInventory.id) {
            InvInventory fromInventory = (InvInventory) invInventoryCacheUtility.read(invInventoryTransaction.transactionEntityId)
            entityName = fromInventory.name
            lblEntityName = INVENTORY_NAME
        }
        if (invInventoryTransaction.transactionEntityTypeId == transactionEntityNone.id) {
            entityName = Tools.NOT_APPLICABLE
        }

        Item item = (Item) itemCacheUtility.read(invInventoryTransactionDetails.itemId)
        Vehicle vehicle = (Vehicle) vehicleCacheUtility.read(invInventoryTransactionDetails.vehicleId)
        InvInventory invInventory = (InvInventory) invInventoryCacheUtility.read(invInventoryTransactionDetails.inventoryId)

        String actualQuantity = invInventoryTransactionDetails.actualQuantity.toString() + Tools.SINGLE_SPACE + item.unit

        String adjustmentFrom = Tools.NOT_APPLICABLE
        String currentAdjustment

        if (invInventoryTransactionDetails.transactionTypeId == transactionTypeAdj.id
                || invInventoryTransactionDetails.transactionTypeId == transactionTypeReAdj.id) {
            if (invInventoryTransactionDetails.isIncrease) {
                actualQuantity = actualQuantity + Tools.SINGLE_SPACE + Tools.PARENTHESIS_START + INCREASE + Tools.PARENTHESIS_END
            } else {
                actualQuantity = actualQuantity + Tools.SINGLE_SPACE + Tools.PARENTHESIS_START + DECREASE + Tools.PARENTHESIS_END
            }

            adjustmentFrom = invInventoryTransactionDetails.adjustmentParentId.toString()
        }

        if (!invInventoryTransactionDetails.isCurrent) {
            GroovyRowResult currentAdjustmentObj
            if (invInventoryTransactionDetails.adjustmentParentId > 0) {
                currentAdjustmentObj = getCurrentAdjustmentOfTransDetails(invInventoryTransactionDetails.adjustmentParentId)
            } else {
                currentAdjustmentObj = getCurrentAdjustmentOfTransDetails(invInventoryTransactionDetails.id)
            }
            currentAdjustment = currentAdjustmentObj ? currentAdjustmentObj.id : CHALAN_REVERSED
        } else {
            currentAdjustment = invInventoryTransactionDetails.id
        }

        String rate = invInventoryTransactionDetails.rate.toString() + Tools.PER + item.unit
        double totalActualCost = (invInventoryTransactionDetails.actualQuantity * invInventoryTransactionDetails.rate)

        Object budget = budgetImplService.readBudget(invInventoryTransaction.budgetId)

        SystemEntity transactionType = (SystemEntity) invTransactionTypeCacheUtility.read(invInventoryTransactionDetails.transactionTypeId)
        String transactionTypeName = transactionType.key

        if (invInventoryTransactionDetails.transactionTypeId == transactionTypeAdj.id
                || invInventoryTransactionDetails.transactionTypeId == transactionTypeReAdj.id) {
            SystemEntity transactionTypeOfParentTrans = (SystemEntity) invTransactionTypeCacheUtility.read(invInventoryTransaction.transactionTypeId)
            transactionTypeName = transactionTypeName + OF + transactionTypeOfParentTrans.key
        }

        SystemEntity inventoryType = (SystemEntity) invInventoryTypeCacheUtility.read(invInventoryTransactionDetails.inventoryTypeId)
        String inventoryName = inventoryType.key + Tools.COLON + Tools.SINGLE_SPACE + invInventory.name
        String approvedOn = invInventoryTransactionDetails.approvedOn ? DateUtility.getDateFormatAsString(invInventoryTransactionDetails.approvedOn) : Tools.EMPTY_SPACE
        AppUser approvedBy = invInventoryTransactionDetails.approvedBy > 0 ? (AppUser) appUserCacheUtility.read(invInventoryTransactionDetails.approvedBy) : null
        String isCurrent = invInventoryTransactionDetails.isCurrent

        LinkedHashMap invoiceMap = [
                invoiceNo: invInventoryTransactionDetails.id,
                transactionDate: DateUtility.getDateFormatAsString(invInventoryTransactionDetails.transactionDate),
                transactionType: transactionTypeName,
                inventoryName: inventoryName,
                entityName: entityName,
                lblEntityName: lblEntityName,
                itemName: item.name,
                itemUnit: item.unit,
                itemCode: item.code,
                itemQuantity: actualQuantity,
                itemRate: rate,
                totalCost: Tools.makeAmountWithThousandSeparator(totalActualCost),
                vehicleName: vehicle ? vehicle.name : Tools.EMPTY_SPACE,
                vehicleNo: invInventoryTransactionDetails.vehicleNumber ? invInventoryTransactionDetails.vehicleNumber : Tools.EMPTY_SPACE,
                referenceChalan: invInventoryTransactionDetails.supplierChalan ? invInventoryTransactionDetails.supplierChalan : Tools.EMPTY_SPACE,
                stackMeasurement: invInventoryTransactionDetails.stackMeasurement ? invInventoryTransactionDetails.stackMeasurement : Tools.EMPTY_SPACE,
                budgetItem: budget ? budget.budgetItem : Tools.EMPTY_SPACE,
                adjustmentFrom: adjustmentFrom,
                currentAdjustment: currentAdjustment,
                approvedOn: approvedOn,
                approvedBy: approvedBy ? approvedBy.username : Tools.EMPTY_SPACE,
                comments: invInventoryTransactionDetails.comments ? invInventoryTransactionDetails.comments : Tools.EMPTY_SPACE,
                isCurrent: invInventoryTransactionDetails.isCurrent
        ]
        return invoiceMap
    }

    private static final String CURR_ADJ_TRANS_QUERY = """
            SELECT id
            FROM inv_inventory_transaction_details
            WHERE adjustment_parent_id=:transDetailsId
            AND is_current = true
    """

    /**
     * Get current object's id of adjusted inventory transaction details object
     * @param transDetailsId - id of inventory transaction details object
     * @return -id of current object
     */
    private GroovyRowResult getCurrentAdjustmentOfTransDetails(long transDetailsId) {
        Map queryParams = [
                transDetailsId: transDetailsId
        ]
        List<GroovyRowResult> result = executeSelectSql(CURR_ADJ_TRANS_QUERY, queryParams)
        if (result.size() > 0) {
            return result[0]
        }
        return null
    }
}