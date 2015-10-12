package com.athena.mis.accounting.actions.accvoucher

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.utility.AccInstrumentTypeCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.SystemEntity
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get total amount of voucher for specific purchase order , used in update and delete purchase order details and update purchase order
 * For details go through Use-Case doc named 'GetTotalVoucherAmountByPOIdActionService'
 */
class GetTotalVoucherAmountByPOIdActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccInstrumentTypeCacheUtility accInstrumentTypeCacheUtility

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get total voucher amount by purchase order id
     * @param params -id of purchase order
     * @param obj -N/A
     * @return -total amount of voucher
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        try {
            long purchaseOrderId = Long.parseLong(params.toString())
            return getTotalAmountByPurchaseOrderId(purchaseOrderId)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return 0L
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build success result
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Do nothing for build failure result
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    private static final String SELECT_TOTAL_AMOUNT_QUERY = """
                SELECT
                COALESCE(SUM(amount),0) AS voucher_amount
                FROM acc_voucher
                WHERE instrument_id =:purchaseOrderId
                AND instrument_type_id =:instrumentTypeId
    """

    /**
     * Get total voucher amount by purchase order id
     * @param purchaseOrderId -id of purchase order
     * @return -total amount of voucher
     */
    private double getTotalAmountByPurchaseOrderId(long purchaseOrderId) {
        // pull system_entity object
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity instrumentTypePo = (SystemEntity) accInstrumentTypeCacheUtility.readByReservedAndCompany(accInstrumentTypeCacheUtility.INSTRUMENT_PO_ID, companyId)

        Map queryParams = [
                purchaseOrderId: purchaseOrderId,
                instrumentTypeId: instrumentTypePo.id
        ]
        List<GroovyRowResult> totalAmount = executeSelectSql(SELECT_TOTAL_AMOUNT_QUERY, queryParams)
        return totalAmount[0].voucher_amount
    }
}
