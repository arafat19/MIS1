package com.athena.mis.accounting.actions.report.accvoucher

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccGroupCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.utility.Tools
import grails.plugin.mail.MailService
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

import java.util.concurrent.ExecutorService

/**
 * Send mail with un-posted voucher report of current date to last 7 days to authorized persons.
 * For details go through Use-Case doc named 'SendMailForUnpostedVoucherActionService'
 */
class SendMailForUnpostedVoucherActionService extends BaseService implements ActionIntf {

    AppMailService appMailService
    MailService mailService
    ExecutorService executorService
    JasperService jasperService
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String INVALID_INPUT = "Unable to sent mail due to invalid input"
    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download un-posted voucher report"
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'accVoucher'
    private static final String REPORT_TITLE = 'Unposted Voucher Report'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String UNPOSTED_VOUCHER_REPORT = 'unpostedVoucher.jasper'
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String COMPANY_ID = "companyId"
    private static final String POSTED_BY_PARAM = "postedByParam"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Unposted voucher mail notification not send due to absence of mail template"
    private static final String MAIL_SENDING_SUCCESS = "Mail sent successfully"
    private static final String MAIL_SUBJECT_POSTFIX = "AllUnpostedVoucher"
    private static final String ROLE_NOT_FOUND = "Role not found to send the mail"
    private static final String USER_NOT_FOUND = "User not found to send the mail"
    private static final String MAIL_ADDRESS = "mailAddress"
    private static final String MIME_PDF = "application/pdf"
    private static final String TRANSACTION_CODE = "SendMailForUnpostedVoucherActionService"

    /**
     *
     * @param parameters - params from UI
     * @param obj - n/a
     * @return - a map containing message and isError True/False
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.transactionCode) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get desired report and mail the report as attachment to users
     * @param parameters - N/A
     * @param obj - N/A
     * @return - a map containing message and isError(true/false)
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            /* if postedByParam  = 0 the show Only Posted Voucher
               if postedByParam  = -1 the show both Posted & Un-posted Voucher */
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }

            String msg = sendMail(postedByParam)    // send mail

            result.put(Tools.MESSAGE, msg)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Show success message
     * @param obj - N/A
     * @return - a map containing success message
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
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
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Send mail the un-posted voucher report to the user those have director, project director and CFO
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - a string of mail sending success message
     */
    private String sendMail(long postedByParam) {
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, companyId, true)
        if (!appMail) {
            return MAIL_TEMPLATE_NOT_FOUND
        }

        LinkedHashMap chkMailMap = checkMail(appMail)
        Boolean isError = (Boolean) chkMailMap.get(Tools.IS_ERROR)
        if (isError.booleanValue()) {
            return chkMailMap.get(Tools.MESSAGE)
        }

        List<String> userMailAddress = (List<String>) chkMailMap.get(MAIL_ADDRESS)
        byte[] attachment = getUnpostedVoucherReport(postedByParam)
        executeMail(appMail, userMailAddress, attachment)
        return MAIL_SENDING_SUCCESS
    }

    /**
     * Send mail
     * @param appMail -object of AppMail
     * @param userMailAddress -list of user mail addresses
     * @param fromDate -previous  date from current date
     * @param toDate -current date
     * @param attachment -unposted voucher report
     */
    private void executeMail(AppMail appMail, List<String> userMailAddress, byte[] attachment) {
        executorService.submit({
            println "Sending mail for transaction ${appMail.transactionCode} ..."
            String attachmentName = MAIL_SUBJECT_POSTFIX
            mailService.sendMail {
                multipart true
                to userMailAddress
                subject appMail.subject
                html appMail.body
                attachBytes attachmentName, MIME_PDF, attachment
            }
            println "Mail sent successfully for ${appMail.transactionCode}"
        })
    }

    /**
     * Generate report by given parameters
     * @param fromDate - start date
     * @param toDate - current date
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - byte array of generated report
     */
    private byte[] getUnpostedVoucherReport(long postedByParam) {
        Map reportParams = new LinkedHashMap()

        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        JasperExportFormat jasperExportFormat = Tools.getFileType(REPORT_FILE_FORMAT)
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)

        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        reportParams.put(COMPANY_ID, accSessionUtil.appSessionUtil.getAppUser().companyId)
        reportParams.put(POSTED_BY_PARAM, postedByParam)

        JasperReportDef reportDef = new JasperReportDef(name: UNPOSTED_VOUCHER_REPORT, fileFormat: jasperExportFormat,
                parameters: reportParams, folder: reportDir)
        /*Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        report.toByteArray()
    }

    /**
     * Give a list containing mail address of corresponding role user
     * @param appMail - object of AppMail
     * @return - a map containing mail address list and isError(true/false)
     */
    private LinkedHashMap checkMail(AppMail appMail) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.TRUE)
        String roleIds = appMail.roleIds
        List<String> lstRoles = roleIds.split(Tools.COMA)
        if (lstRoles.size() <= 0) {
            result.put(Tools.MESSAGE, ROLE_NOT_FOUND)
            return result
        }
        List<String> mailAddress = getUserMailAddress(roleIds)
        if (mailAddress.size() <= 0) {
            result.put(Tools.MESSAGE, USER_NOT_FOUND)
            return result
        }
        result.put(MAIL_ADDRESS, mailAddress)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
    }

    /**
     * list of user mail addresses
     * @param roleIds -a string containing the role ids of user
     * @return -a list containing the list of user mail addresses
     */
    public List<String> getUserMailAddress(String roleIds) {
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        String query = """
            SELECT DISTINCT au.login_id FROM app_user_entity app
            LEFT JOIN app_user au ON au.id= app.app_user_id
                WHERE app.app_user_id IN
                          (SELECT DISTINCT ur.user_id FROM user_role ur
                                WHERE ur.role_id IN
                                    (SELECT id FROM role WHERE role_type_id IN (${roleIds})
                                        AND company_id = :companyId))
        """

        Map queryParams = [
                companyId: companyId
        ]
        List<GroovyRowResult> result = executeSelectSql(query, queryParams)

        List<String> userMailAddress = []
        for (int i = 0; i < result.size(); i++) {
            userMailAddress << result[i][0]
        }
        return userMailAddress
    }

}
