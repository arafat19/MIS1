package com.athena.mis.integration.inventory.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get consumed quantity of an item of specific budget, used in update and delete budget details
 * For details go through Use-Case doc named 'GetConsumeItemByBudgetAndItemIdImplActionService'
 */
class GetConsumeItemByBudgetAndItemIdImplActionService extends BaseService implements ActionIntf {

    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
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
     * Get total consumed quantity of an item of a specific budget
     * @param params -id of budget
     * @param obj -id of item
     * @return -consumed quantity of item
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        try {
            long budgetId = Long.parseLong(params.toString())
            long itemId = Long.parseLong(obj.toString())
            return getTotalConsumedQuantity(budgetId, itemId)
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return 0
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

    private static final String SUM_QUERY = """
        SELECT coalesce(sum(iitd.actual_quantity),0) AS total
        FROM inv_inventory_transaction_details iitd
        LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
        WHERE iit.budget_id =:budgetId
        AND iitd.item_id =:itemId
        AND iit.transaction_type_id =:transactionTypeId
    """

    /**
     * Get total consumed quantity of an item of a specific budget
     * @param budgetId -id of budget
     * @param itemId -id of item
     * @return -consumed quantity of item
     */
    private double getTotalConsumedQuantity(long budgetId, long itemId) {
        // pull transactionType object
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        Map queryParams = [
                budgetId: budgetId,
                itemId: itemId,
                transactionTypeId: transactionTypeCons.id
        ]
        List<GroovyRowResult> invInventoryTransactionDetailsList = executeSelectSql(SUM_QUERY, queryParams)
        double totalConsumedQuantity = Double.parseDouble(invInventoryTransactionDetailsList[0].total.toString())
        return totalConsumedQuantity
    }
}