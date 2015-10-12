package com.athena.mis.integration.procurement.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcPurchaseOrderDetails
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update store_in_quantity of  proc_purchase_order_details
 *  For details go through Use-Case doc named 'UpdateStoreInQuantityPODImplActionService'
 */
class UpdateStoreInQuantityPODImplActionService extends BaseService implements ActionIntf {


    private final Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * update store_in_quantity of proc_purchase_order_details
     * @param params1 -N/A
     * @param obj -ProcPurchaseOrderDetails object
     * @return -int value
     */
    @Transactional
    public Object execute(Object params1, Object obj) {
        try {
            ProcPurchaseOrderDetails purchaseOrderDetails = (ProcPurchaseOrderDetails) obj
            return updateStoreInQuantity(purchaseOrderDetails)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return new Integer(0)
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
        return false
    }

    /**
     * Do nothing for failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    private static final String PROC_PURCHASE_ORDER_DETAILS_UPDATE_QUERY = """
                    UPDATE proc_purchase_order_details SET
                          store_in_quantity =:storeInQuantity,
                          version = :newVersion
                      WHERE
                            id=:id
                      AND version =:version
                    """
    /**
     * Update store in quantity send by supplier
     * @param purchaseOrderDetailsInstance -ProcPurchaseOrderDetails
     * @return int value
     */
    private Integer updateStoreInQuantity(ProcPurchaseOrderDetails purchaseOrderDetailsInstance) {
        Map queryParams = [
                id: purchaseOrderDetailsInstance.id,
                version: purchaseOrderDetailsInstance.version,
                newVersion: purchaseOrderDetailsInstance.version + 1,
                storeInQuantity: purchaseOrderDetailsInstance.storeInQuantity
        ]
        int updateCount = executeUpdateSql(PROC_PURCHASE_ORDER_DETAILS_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to update store transaction summary")
        }
        return new Integer(updateCount);
    }
}
