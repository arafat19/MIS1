package com.athena.mis.integration.procurement.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcPurchaseOrderDetails
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get PurchaseOrderDetails object by given PO & itemId
 *  For details go through Use-Case doc named 'ReadByPurchaseOrderAndItemImplActionService'
 */
class ReadByPurchaseOrderAndItemImplActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * get PurchaseOrderDetails object by given PO & itemId
     * @param params1 - poId(PurchaseOrder.id)
     * @param params2 -ItemId(Item.id)
     * @return -PurchaseOrderDetails object
     */
    public Object execute(Object params1, Object params2) {
        try {
            long purchaseOrderId = Long.parseLong(params1.toString())
            long itemId = Long.parseLong(params2.toString())
            return ProcPurchaseOrderDetails.findByPurchaseOrderIdAndItemId(purchaseOrderId, itemId, [readOnly: true])
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
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
}