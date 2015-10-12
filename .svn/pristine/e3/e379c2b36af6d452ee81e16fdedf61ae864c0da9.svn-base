package com.athena.mis.accounting.actions.report.accvouchertotal

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccVoucherTypeCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get sum of amount of acc voucher (Total Of Pay Cheque) on cheque date
 * For details go through Use-Case doc named 'GetTotalOfPayChequeOnChequeDateActionService'
 */
class GetTotalOfPayChequeOnChequeDateActionService extends BaseService implements ActionIntf {

    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String TOTAL_POSTED = "totalPosted"
    private static final String TOTAL_UNPOSTED = "totalUnPosted"
    private static final String FAILURE_MSG = "Fail to get total of Pay-Cash Voucher."

    private Logger log = Logger.getLogger(getClass())

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get posted acc voucher amount total (Total Of PayCheque)
     * Get un-posted acc voucher amount total (Total Of PayCheque)
     * @param parameters - N/A
     * @param obj - N/A
     * @return - Map containing all necessary strings
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Date currentDate = new Date()
            SystemEntity voucherTypePayBank = (SystemEntity) accVoucherTypeCacheUtility.readByReservedAndCompany(accVoucherTypeCacheUtility.PAYMENT_VOUCHER_BANK_ID, accSessionUtil.appSessionUtil.getCompanyId())

            String totalPosted = getTotalPosted(currentDate, voucherTypePayBank.id)
            String totalUnPosted = getTotalUnPosted(currentDate, voucherTypePayBank.id)
            result.put(TOTAL_POSTED, totalPosted)
            result.put(TOTAL_UNPOSTED, totalUnPosted)
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
     * do nothing for build success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * do nothing for build failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Give amount total of posted acc voucher for SMS
     * FOR sms character 'DB_QUANTITY_FORMAT' is used instead of 'DB_CURRENCY_FORMAT'
     * @param currentDate - new date
     * @return - total sum of amount of posted voucher
     */
    private String getTotalPosted(Date currentDate, long voucherType) {
        String queryStr = """SELECT TO_CHAR(COALESCE(SUM(amount),0),'${Tools.DB_QUANTITY_FORMAT}') AS total_amount
                FROM acc_voucher
                WHERE voucher_type_id =:voucherTypeId
                AND is_voucher_posted = true
                AND cheque_date =:currentDate"""

        Map queryParams = [
                voucherTypeId: voucherType,
                chequeDate: DateUtility.getSqlDateWithSeconds(currentDate)
        ]

        List result = executeSelectSql(queryStr, queryParams)
        String amount = result[0].total_amount
        return amount
    }

    /**
     * Give amount total of un-posted acc voucher for SMS
     * FOR sms character 'DB_QUANTITY_FORMAT' is used instead of 'DB_CURRENCY_FORMAT'
     * @param currentDate - new date
     * @return - total sum of amount of un-posted voucher
     */
    private String getTotalUnPosted(Date currentDate, long voucherType) {
        String queryStr = """SELECT TO_CHAR(COALESCE(SUM(amount),0),'${Tools.DB_QUANTITY_FORMAT}') AS total_amount
                FROM acc_voucher
                WHERE voucher_type_id =:voucherTypeId
                AND is_voucher_posted = false
                AND cheque_date =:currentDate"""

        Map queryParams = [
                voucherTypeId: voucherType,
                chequeDate: DateUtility.getSqlDateWithSeconds(currentDate)
        ]
        List result = executeSelectSql(queryStr, queryParams)
        String amount = result[0].total_amount
        return amount
    }
}
