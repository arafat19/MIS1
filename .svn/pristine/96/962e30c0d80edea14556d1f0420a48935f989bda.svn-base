package com.athena.mis.integration.inventory.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get inventory transaction object by purchase order id, used in update and delete purchase order
 * For details go through Use-Case doc named 'ReadInvTransactionByPurchaseOrderIdImplActionService'
 */
class ReadInvTransactionByPurchaseOrderIdImplActionService extends BaseService implements ActionIntf {

    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private final Logger log = Logger.getLogger(getClass());

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get inventory transaction object by purchase order id
     * @param params -id of purchase order
     * @param obj -N/A
     * @return -inventory transaction object
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        try {
            long purchaseOrderId = Long.parseLong(params.toString())
            return readByPurchaseOrderId(purchaseOrderId)
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return []
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
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Do nothing for build failure result for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    private static final String INVENTORY_TRANSACTION_SELECT_QUERY = """
                SELECT *
                FROM inv_inventory_transaction
                WHERE transaction_id =:purchaseOrderId
                AND transaction_type_id =:transactionTypeId AND
                      transaction_entity_type_id =:transactionEntityTypeId
    """

    /**
     * Get inventory transaction object by purchase order id
     * @param purchaseOrderId -id of purchase order
     * @return -inventory transaction object
     */
    private InvInventoryTransaction readByPurchaseOrderId(long purchaseOrderId) {
        InvInventoryTransaction invInventoryTransaction = null
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)

        Map queryParams = [
                purchaseOrderId: purchaseOrderId,
                transactionTypeId: transactionTypeIn.id,
                transactionEntityTypeId: transactionEntitySupplier.id
        ]
        List<GroovyRowResult> resultList = executeSelectSql(INVENTORY_TRANSACTION_SELECT_QUERY, queryParams)
        if (resultList.size() > 0) {
            invInventoryTransaction = buildInventoryTransactionObj(resultList[0])
        }
        return invInventoryTransaction
    }

    /**
     * Build inventory transaction object
     * @param sqlInvInventoryTransaction -groovyRowResult with properties of inventory transaction object
     * @return -inventory transaction object
     */
    private InvInventoryTransaction buildInventoryTransactionObj(GroovyRowResult sqlInvInventoryTransaction) {
        if (!sqlInvInventoryTransaction || (sqlInvInventoryTransaction.id <= 0)) {
            return null
        }

        InvInventoryTransaction invInventoryTransaction = new InvInventoryTransaction()

        invInventoryTransaction.id = sqlInvInventoryTransaction.id
        invInventoryTransaction.version = sqlInvInventoryTransaction.version
        invInventoryTransaction.transactionTypeId = sqlInvInventoryTransaction.transaction_type_id
        invInventoryTransaction.transactionEntityTypeId = sqlInvInventoryTransaction.transaction_entity_type_id
        invInventoryTransaction.transactionEntityId = sqlInvInventoryTransaction.transaction_entity_id
        invInventoryTransaction.invProductionLineItemId = sqlInvInventoryTransaction.inv_production_line_item_id
        invInventoryTransaction.transactionId = sqlInvInventoryTransaction.transaction_id
        invInventoryTransaction.projectId = sqlInvInventoryTransaction.project_id ? sqlInvInventoryTransaction.project_id : 0L
        invInventoryTransaction.createdOn = sqlInvInventoryTransaction.created_on
        invInventoryTransaction.createdBy = sqlInvInventoryTransaction.created_by
        invInventoryTransaction.updatedOn = sqlInvInventoryTransaction.updated_on
        invInventoryTransaction.updatedBy = sqlInvInventoryTransaction.updated_by ? sqlInvInventoryTransaction.updated_by : 0L
        invInventoryTransaction.comments = sqlInvInventoryTransaction.comments
        invInventoryTransaction.inventoryTypeId = sqlInvInventoryTransaction.inventory_type_id
        invInventoryTransaction.inventoryId = sqlInvInventoryTransaction.inventory_id
        invInventoryTransaction.budgetId = sqlInvInventoryTransaction.budget_id ? sqlInvInventoryTransaction.budget_id : 0L
        invInventoryTransaction.itemCount = sqlInvInventoryTransaction.item_count
        invInventoryTransaction.companyId = sqlInvInventoryTransaction.company_id ? sqlInvInventoryTransaction.company_id : 0L
        invInventoryTransaction.transactionDate = sqlInvInventoryTransaction.transaction_date

        return invInventoryTransaction
    }
}