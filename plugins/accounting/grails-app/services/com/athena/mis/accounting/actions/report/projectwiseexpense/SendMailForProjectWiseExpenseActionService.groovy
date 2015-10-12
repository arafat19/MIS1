package com.athena.mis.accounting.actions.report.projectwiseexpense

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccGroup
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccGroupCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppMailService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.plugin.mail.MailService
import groovy.sql.GroovyRowResult
import groovy.text.SimpleTemplateEngine
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

import java.util.concurrent.ExecutorService

/**
 * Send mail with Project wise expense report of current date to last 7 days to authorized persons.
 * For details go through Use-Case doc named 'SendMailForProjectWiseExpenseActionService'
 */
class SendMailForProjectWiseExpenseActionService extends BaseService implements ActionIntf {

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
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String INVALID_INPUT = "Unable to sent mail due to invalid input"
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download project wise expense report"
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'projectWiseExpense'
    private static final String REPORT_TITLE = 'projectWiseExpense'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String PROJECT_WISE_EXPENSE_REPORT = 'projectWiseExpense.jasper'
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String PROJECT_IDS = "projectIds"
    private static final String COA_IDS = "coaIds"
    private static final String GROUP_ID_BANK = "groupIdBank"
    private static final String GROUP_ID_CASH = "groupIdCash"
    private static final String COA_NAME = "coaName"
    private static final String USER_HAS_NO_PROJECT = "User is not mapped with any project"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String COMPANY_ID = "companyId"
    private static final String POSTED_BY_PARAM = "postedByParam"
    private static final String LABEL_ALL_BANK = "All Bank"
    private static final String LABEL_ALL_CASH = "All Cash"
    private static final String LABEL_ALL_BANK_CASH = "All Bank & Cash"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Project wise expense mail notification not send due to absence of mail template"
    private static final String MAIL_SENDING_SUCCESS = "Mail sent successfully"
    private static final String MAIL_SUBJECT_POSTFIX = "ProjectWiseExpense: "
    private static final String ROLE_NOT_FOUND = "Role not found to send the mail"
    private static final String USER_NOT_FOUND = "User not found to send the mail"
    private static final String MAIL_ADDRESS = "mailAddress"
    private static final String MIME_PDF = "application/pdf"
    private static final String TRANSACTION_CODE = "SendMailForProjectWiseExpenseActionService"

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
     * Get desired report providing all required parameters
     * @param parameters -N/A
     * @param obj - N/A
     * @return -a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            List<Long> projectIds = accSessionUtil.appSessionUtil.getUserProjectIds()

            if (projectIds.size() == 0) {
                result.put(Tools.MESSAGE, USER_HAS_NO_PROJECT)
                return result
            }

            List<Long> groupIds = accGroupCacheUtility.listOfAccGroupBankCashId()

            Date toDate = new Date()
            Date fromDate = toDate - DateUtility.DATE_RANGE_THIRTY     // get previous 7th date from current date

            /* if postedByParam  = 0 the show Only Posted Voucher
               if postedByParam  = -1 the show both Posted & Un-posted Voucher */
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }

            String msg = sendMail(groupIds, projectIds,fromDate, toDate, postedByParam)    // send mail

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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * send message for success operation
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
     * 1. get mail template by transaction code
     * 2. get user email addresses
     * 3. build mail body template and subject
     * 4. get purchase order report as attachment
     * 5. send mail
     * @param purchaseOrder -object of ProcPurchaseOrder
     * @return -a string containing message or null value depending on method success
     */
    private String sendMail(List accGroupIds, List<Long> projectIds, Date fromDate, Date toDate, long postedByParam) {
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, companyId, true)
        if (!appMail) {
            return MAIL_TEMPLATE_NOT_FOUND
        }
        LinkedHashMap chkMailMap = checkMail(appMail, projectIds)
        Boolean isError = (Boolean) chkMailMap.get(Tools.IS_ERROR)
        if (isError.booleanValue()) {
            return chkMailMap.get(Tools.MESSAGE)
        }
        String startDate = DateUtility.getDateFormatAsString(fromDate)
        String endDate = DateUtility.getDateFormatAsString(toDate)
        Map paramsBody = [project: Tools.ALL, group: Tools.ALL, dateRange: startDate+Tools.TO+endDate]
        SimpleTemplateEngine engine = new SimpleTemplateEngine()
        Writable templateBody = engine.createTemplate(appMail.body).make(paramsBody)
        appMail.body = templateBody.toString()
        appMail.subject = appMail.subject + Tools.PARENTHESIS_START + startDate + Tools.TO + endDate + Tools.PARENTHESIS_END
        List<String> userMailAddress = (List<String>) chkMailMap.get(MAIL_ADDRESS)
        byte[] attachment = getProjectWiseExpenseReport(accGroupIds, projectIds, fromDate, toDate, postedByParam)

        executeMail(appMail, userMailAddress, startDate,endDate, attachment)
        return MAIL_SENDING_SUCCESS
    }
    /**
     * Generate report by given data
     * @param accGroupId -account group id
     * @param projectIds - project ids
     * @param fromDate - start date
     * @param toDate - current date
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - byte array of generated report
     */
    private byte[] getProjectWiseExpenseReport(List accGroupIds, List<Long> projectIds, Date fromDate, Date toDate, long postedByParam) {
        List coaIds = accChartOfAccountCacheUtility.listIdsByAccGroupIds(accGroupIds)

        Map reportParams = new LinkedHashMap()
        AccGroup accGroupBank = accGroupCacheUtility.readBySystemAccGroup(accGroupCacheUtility.ACC_GROUP_BANK)
        AccGroup accGroupCash = accGroupCacheUtility.readBySystemAccGroup(accGroupCacheUtility.ACC_GROUP_CASH)

        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        JasperExportFormat jasperExportFormat = Tools.getFileType(REPORT_FILE_FORMAT)
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(PROJECT_IDS, projectIds)
        reportParams.put(COA_IDS, coaIds)
        reportParams.put(GROUP_ID_BANK, accGroupBank.id)
        reportParams.put(GROUP_ID_CASH, accGroupCash.id)

        if (accGroupIds.size() > 1) {
            reportParams.put(COA_NAME, LABEL_ALL_BANK_CASH)
        } else {
            for (int i = 0; i < accGroupIds.size(); i++) {
                if (accGroupIds[i] == accGroupBank.id) {
                    reportParams.put(COA_NAME, LABEL_ALL_BANK)
                } else if (accGroupIds[i] == accGroupCash.id) {
                    reportParams.put(COA_NAME, LABEL_ALL_CASH)
                }
            }
        }
        Date startDate = DateUtility.setFirstHour(fromDate)
        Date endDate = DateUtility.setLastHour(toDate)
        reportParams.put(FROM_DATE, startDate)
        reportParams.put(TO_DATE, endDate)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        reportParams.put(COMPANY_ID, accSessionUtil.appSessionUtil.getAppUser().companyId)
        reportParams.put(POSTED_BY_PARAM, postedByParam)

        JasperReportDef reportDef = new JasperReportDef(name: PROJECT_WISE_EXPENSE_REPORT, fileFormat: jasperExportFormat,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        report.toByteArray()
    }

    /**
     * 1. check role ids of user
     * 2. get user mail addresses
     * @param appMail -object of AppMail
     * @param projectId -id of Project
     * @return -a map containing user mail addresses, isError(true/false) and relevant message
     */
    private LinkedHashMap checkMail(AppMail appMail, List<Long> projectIds) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.TRUE)
        String roleIds = appMail.roleIds
        List<String> lstRoles = roleIds.split(Tools.COMA)
        if (lstRoles.size() <= 0) {
            result.put(Tools.MESSAGE, ROLE_NOT_FOUND)
            return result
        }
        List<String> mailAddress = getUserMailAddress(roleIds, projectIds)
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
     * @param projectId -id of Project
     * @return -a list containing the list of user mail addresses
     */
    public List<String> getUserMailAddress(String roleIds, List<Long> projectIds) {
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity appUserSysEntityObject = (SystemEntity) appUserEntityTypeCacheUtility.readByReservedAndCompany(appUserEntityTypeCacheUtility.PROJECT, companyId)
        String projectIdsStr = Tools.buildCommaSeparatedStringOfIds(projectIds)
        String query = """
            SELECT DISTINCT au.login_id FROM app_user_entity app
                LEFT JOIN app_user au ON au.id= app.app_user_id
                WHERE app.entity_id IN (${projectIdsStr}) AND
                      app.entity_type_id = :project AND
                      app.app_user_id IN
                          (SELECT ur.user_id FROM user_role ur
                                WHERE ur.role_id IN
                                    (SELECT id FROM role WHERE role_type_id IN (${roleIds})
                                        AND company_id = :companyId))
        """

        Map queryParams = [
                project: appUserSysEntityObject.id,
                companyId: companyId
        ]
        List<GroovyRowResult> result = executeSelectSql(query, queryParams)

        List<String> userMailAddress = []
        for (int i = 0; i < result.size(); i++) {
            userMailAddress << result[i][0]
        }
        return userMailAddress
    }

    /**
     * Send mail
     * @param appMail -object of AppMail
     * @param userMailAddress -list of user mail addresses
     * @param fromDate -previous 7th date from current date
     * @param toDate -current date
     * @param attachment -project wise expense report
     */
    private void executeMail(AppMail appMail, List<String> userMailAddress,String fromDate, String toDate, byte[] attachment) {
        executorService.submit({
            println "Sending mail for transaction ${appMail.transactionCode} ..."
            String attachmentName = MAIL_SUBJECT_POSTFIX + fromDate + Tools.TO + toDate
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

}
