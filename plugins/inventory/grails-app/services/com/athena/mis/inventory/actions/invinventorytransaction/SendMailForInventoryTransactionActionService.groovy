package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
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
 * Send mail with all pending report of inventory transaction of last 7 days to authorized persons.
 * For details go through Use-Case doc named 'SendMailForInventoryTransactionActionService'
 */
class SendMailForInventoryTransactionActionService extends BaseService implements ActionIntf {

    AppMailService appMailService
    MailService mailService
    ExecutorService executorService
    JasperService jasperService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String INVALID_INPUT = "Unable to sent mail due to invalid input"
    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download inventory transaction report"
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'inventoryTransaction'
    private static final String REPORT_TITLE = 'pendingInventoryTransaction'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String SUBREPORT_DIR = 'SUBREPORT_DIR'
    private static final String INVENTORY_TRANSACTION_REPORT = 'sendMailForInventoryTransaction.jasper'
    private static final String INVENTORY_IDS = "inventoryIds"
    private static final String TRANSACTION_TYPE_IN = "transactionTypeIn"
    private static final String TRANSACTION_TYPE_OUT = "transactionTypeOut"
    private static final String TRANSACTION_TYPE_CONSUMPTION = "transactionTypeConsumption"
    private static final String ENTITY_TYPE_INVENTORY = "transactionEntityTypeInventory"
    private static final String ENTITY_TYPE_SUPPLIER = "transactionEntityTypeSupplier"
    private static final String USER_HAS_NO_PROJECT = "User is not mapped with any project"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Inventory transaction mail notification not send due to absence of mail template"
    private static final String MAIL_SENDING_SUCCESS = "Mail sent successfully"
    private static final String MAIL_SUBJECT_POSTFIX = "All Pending Transactions: "
    private static final String ROLE_NOT_FOUND = "Role not found to send the mail"
    private static final String USER_NOT_FOUND = "User not found to send the mail"
    private static final String MAIL_ADDRESS = "mailAddress"
    private static final String MIME_PDF = "application/pdf"
    private static final String TRANSACTION_CODE = "SendMailForInventoryTransactionActionService"

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
            List<Long> projectIds = invSessionUtil.appSessionUtil.getUserProjectIds()
            if (projectIds.size() == 0) {
                result.put(Tools.MESSAGE, USER_HAS_NO_PROJECT)
                return result
            }
            // get ids of inventory associated with user
            List<Long> inventoryIds = invSessionUtil.getUserInventoryIds()
            String msg = sendMail(projectIds, inventoryIds)    // send mail
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
     * 4. get pending inventory transaction report as attachment
     * 5. send mail
     * @param projectIds - all project ids related to users
     * @param inventoryIds - all inventory ids related to users
     * @return -a string containing message or null value depending on method success
     */
    private String sendMail(List<Long> projectIds, List<Long> inventoryIds) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, companyId, true)
        if (!appMail) {
            return MAIL_TEMPLATE_NOT_FOUND
        }
        LinkedHashMap chkMailMap = checkMail(appMail, projectIds)
        Boolean isError = (Boolean) chkMailMap.get(Tools.IS_ERROR)
        if (isError.booleanValue()) {
            return chkMailMap.get(Tools.MESSAGE)
        }
        List<String> userMailAddress = (List<String>) chkMailMap.get(MAIL_ADDRESS)
        byte[] attachment = getInventoryTransactionReport(inventoryIds)

        executeMail(appMail, userMailAddress, attachment)
        return MAIL_SENDING_SUCCESS
    }

    /**
     *
     * @param inventoryIds - all inventory ids related to users
     * @param fromDate - start date
     * @param toDate - current date
     * @return - byte array of generated report
     */
    private byte[] getInventoryTransactionReport(List<Long> inventoryIds) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionTypeOut = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_OUT, companyId)
        SystemEntity transactionTypeConsumption = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)

        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getInventoryReportDirectory() + File.separator + REPORT_FOLDER
        String subReportDir = reportDir + File.separator
        JasperExportFormat jasperExportFormat = Tools.getFileType(REPORT_FILE_FORMAT)
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(SUBREPORT_DIR, subReportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(INVENTORY_IDS, inventoryIds)
        reportParams.put(TRANSACTION_TYPE_IN, transactionTypeIn.id)
        reportParams.put(TRANSACTION_TYPE_OUT, transactionTypeOut.id)
        reportParams.put(TRANSACTION_TYPE_CONSUMPTION, transactionTypeConsumption.id)
        reportParams.put(ENTITY_TYPE_INVENTORY, transactionEntityInventory.id)
        reportParams.put(ENTITY_TYPE_SUPPLIER, transactionEntitySupplier.id)

        JasperReportDef reportDef = new JasperReportDef(name: INVENTORY_TRANSACTION_REPORT, fileFormat: jasperExportFormat,
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
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
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
     * @param attachment -inventory transaction report
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
}
