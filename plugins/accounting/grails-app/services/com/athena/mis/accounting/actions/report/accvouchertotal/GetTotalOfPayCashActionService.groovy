package com.athena.mis.accounting.actions.report.accvouchertotal

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccVoucherTypeCacheUtility
import com.athena.mis.application.entity.Sms
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.SmsCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get sum of voucher amount (Total of Pay Cash) on current date and send sms
 * For details go through Use-Case doc named 'GetTotalOfPayCashActionService'
 */
class GetTotalOfPayCashActionService extends BaseService implements ActionIntf {

    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    SmsCacheUtility smsCacheUtility

    private static final String NOT_FOUND_MSG = "The selected sms is not active"
    private static final String NO_ACCESS_MESSAGE = "Log in user is not authorized to send this sms"
    private static final String LST_SMS = "lstSms"
    private static final String SHOW_FAILURE_MESSAGE = "SMS has not been send"
    private static final String SHOW_SUCCESS_MESSAGE = "SMS has been sent successfully"
    private static final String ASCII_HYPHEN = '%2D'
    private static final String ASCII_SINGLE_DOT = '%2E'
    private static final String ASCII_COLON = '%3A'
    private static final String RECIPIENTS_NOT_FOUND = 'Recipients not found'
    private static final String RECIPIENT = 'recipient'
    private static final String CONTENT = 'content'
    private static final String CURRENT_DATE = 'currentDate'
    private static final String POSTED_AMOUNT = 'postedAmount'
    private static final String UN_POSTED_AMOUNT = 'unPostedAmount'
    private static final String CRETURN = "%0D"
    private static final String RETURN_CHAR = "[\\r\\n]"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. check if user is authorized to send sms or not
     * 2. check required parameters
     * 3. get list of objects of sms by transactionCode, companyId & isActive = true
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)  // default value
            if (!accSessionUtil.appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                result.put(Tools.MESSAGE, NO_ACCESS_MESSAGE)
                return result
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.transactionCode) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            String transactionCode = params.transactionCode
            long companyId = accSessionUtil.appSessionUtil.getCompanyId()
            List<Sms> lstSms = smsCacheUtility.listByTransactionCodeAndCompanyIdAndIsActive(transactionCode, companyId, true)
            if (lstSms.size() <= 0) {
                result.put(Tools.MESSAGE, NOT_FOUND_MSG)
                return result
            }
            result.put(LST_SMS, lstSms)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * 1. get total amount of posted voucher on current date (Total Of Pay Cash)
     * 2. Get total amount of un-posted voucher on current date (Total Of Pay Cash)
     * 3. send sms to recipients
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing isError(true/false) & relevant message depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            List<Sms> lstSms = (List<Sms>) preResult.get(LST_SMS)

            Date currentDate = new Date()
            long companyId = accSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity voucherTypePayCash = (SystemEntity) accVoucherTypeCacheUtility.readByReservedAndCompany(accVoucherTypeCacheUtility.PAYMENT_VOUCHER_CASH_ID, companyId)
            String totalPosted = getTotalPosted(currentDate, voucherTypePayCash.id)
            String totalUnPosted = getTotalUnPosted(currentDate, voucherTypePayCash.id)

            String msg = sendSms(lstSms, totalPosted, totalUnPosted, currentDate)
            if (msg) {
                result.put(Tools.MESSAGE, msg)
                return result
            }
            result.put(Tools.MESSAGE, SHOW_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
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
     * Do nothing for build success result
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
            LinkedHashMap executeResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Get total amount of posted voucher on current date (Total Of Pay Cash)
     * FOR sms character 'DB_QUANTITY_FORMAT' is used instead of 'DB_CURRENCY_FORMAT'
     * @param currentDate -new date
     * @param voucherType -system entity id of voucher type pay cash object
     * @return -total sum of amount of posted voucher
     */
    private String getTotalPosted(Date currentDate, long voucherType) {
        String queryStr = """SELECT TO_CHAR(COALESCE(SUM(amount),0),'${Tools.DB_QUANTITY_FORMAT}') AS total_amount
                FROM acc_voucher
                WHERE voucher_type_id =:voucherTypeId
                AND is_voucher_posted = true
                AND created_on BETWEEN :currentDate AND :currentDate"""
        Map queryParams = [
                voucherTypeId: voucherType,
                currentDate: DateUtility.getSqlDateWithSeconds(currentDate)
        ]
        List result = executeSelectSql(queryStr, queryParams)
        String amount = result[0].total_amount
        return amount
    }

    /**
     * Get total amount of un-posted voucher on current date (Total Of Pay Cash)
     * FOR sms character 'DB_QUANTITY_FORMAT' is used instead of 'DB_CURRENCY_FORMAT'
     * @param currentDate -new date
     * @param voucherType -system entity id of voucher type pay cash object
     * @return -total sum of amount of un-posted voucher
     */
    private String getTotalUnPosted(Date currentDate, long voucherType) {
        String queryStr = """SELECT TO_CHAR(COALESCE(SUM(amount),0),'${Tools.DB_QUANTITY_FORMAT}') AS total_amount
                FROM acc_voucher
                WHERE voucher_type_id =:voucherTypeId
                AND is_voucher_posted = false
                AND created_on BETWEEN :currentDate AND :currentDate"""
        Map queryParams = [
                voucherTypeId: voucherType,
                currentDate: DateUtility.getSqlDateWithSeconds(currentDate)
        ]
        List result = executeSelectSql(queryStr, queryParams)
        String amount = result[0].total_amount
        return amount
    }

    /**
     * Send sms
     * @param lstSms -list of objects of sms
     * @param totalPosted -total amount of posted voucher
     * @param totalUnPosted -total amount of un-posted voucher
     * @param currentDate -current date
     * @return -a string containing null value or error message depending on method success
     */
    private String sendSms(List<Sms> lstSms, String totalPosted, String totalUnPosted, Date currentDate) {
        String msg = null
        for (int i = 0; i < lstSms.size(); i++) {
            Sms sms = lstSms[i]
            if ((!sms.recipients) || (sms.recipients.length() == 0)) {
                msg = RECIPIENTS_NOT_FOUND
                return msg
            }
            String strCurrentDate = DateUtility.getDateForSMS(currentDate)

            String phoneNumbers = sms.recipients
            for (String currentPhoneNumber : phoneNumbers.split(Tools.COMA)) {
                currentPhoneNumber = currentPhoneNumber.trim()

                // First evaluate content
                Binding binding = new Binding()
                binding.setVariable(CURRENT_DATE, strCurrentDate)
                binding.setVariable(POSTED_AMOUNT, totalPosted)
                binding.setVariable(UN_POSTED_AMOUNT, totalUnPosted)
                GroovyShell shell = new GroovyShell(binding)
                String content = shell.evaluate(sms.body)
                content = content.replace(Tools.SINGLE_SPACE, Tools.PLUS)
                content = content.replace(Tools.SINGLE_DOT, ASCII_SINGLE_DOT)
                content = content.replace(Tools.HYPHEN, ASCII_HYPHEN)
                content = content.replace(Tools.COLON, ASCII_COLON)
                content = content.replaceAll(RETURN_CHAR, CRETURN);
                // now evaluate full sms url
                binding = new Binding()
                binding.setVariable(RECIPIENT, currentPhoneNumber)
                binding.setVariable(CONTENT, content)
                shell = new GroovyShell(binding)
                String strSms = shell.evaluate(sms.url)
                strSms.toURL().text
            }
        }
        return msg
    }
}
