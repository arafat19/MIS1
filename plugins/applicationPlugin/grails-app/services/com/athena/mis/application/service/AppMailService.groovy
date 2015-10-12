package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.*
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import grails.plugin.mail.MailService
import grails.util.Environment
import groovy.sql.GroovyRowResult
import groovy.text.SimpleTemplateEngine
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.beans.factory.annotation.Autowired

import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import java.util.concurrent.ExecutorService

// AppMailService is used to send auto mail in different events

class AppMailService extends BaseService {

    static transactional = false

    MailService mailService
    ExecutorService executorService
    GrailsApplication grailsApplication
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String MAIL_ADDRESS = "mailAddress"
    private static final String ROLE_NOT_FOUND = "Role not found to send the mail"
    private static final String USER_NOT_FOUND = "User not found to send the mail"
    public final String SORT_ON_ID = "id"

    /**
     * @return -list of appMail object
     */
    public List<AppMail> list() {
        List<AppMail> mailList = AppMail.list([max: resultPerPage, start: start, readOnly: true])
        return mailList
    }

    public int count() {
        int count = AppMail.count()
        return count
    }

    public AppMail read(long id) {
        AppMail appMail = AppMail.read(id)
        return appMail
    }

    //it is used to inventory consumption
    public Boolean sendMailForInventoryConsumptionApproval(long projectId, AppMail appMail, String inventoryName, String itemName, String quantityUnit, String createdOn) {
        LinkedHashMap chkMailMap = checkMail(appMail, projectId)
        Boolean isError = (Boolean) chkMailMap.get(Tools.IS_ERROR)
        if (isError.booleanValue()) {
            return Boolean.FALSE
        }

        Map parameters = [inventoryName: inventoryName, itemName: itemName, quantityUnit: quantityUnit, createdOn: createdOn]
        buildMailBody(appMail, parameters)
        List<String> userMailAddress = (List<String>) chkMailMap.get(MAIL_ADDRESS)
        executeMail(appMail, userMailAddress)
        return Boolean.TRUE
    }

    //Send mail when new Indent is created
    public Boolean sendMailForIndentApproval(Project project, AppMail appMail, long indentId, String createdOn, String createdBy) {
        LinkedHashMap chkMailMap = checkMail(appMail, project.id)
        Boolean isError = (Boolean) chkMailMap.get(Tools.IS_ERROR)
        if (isError.booleanValue()) {
            return Boolean.FALSE
        }

        Map parameters = [projectName: project.name, indentId: indentId, createdOn: createdOn, createdBy: createdBy]
        buildMailBody(appMail, parameters)
        List<String> userMailAddress = (List<String>) chkMailMap.get(MAIL_ADDRESS)
        executeMail(appMail, userMailAddress)
        return Boolean.TRUE
    }

    //it is used to inventory consumption
    public Boolean sendMailForAccIouSlip(long projectId, AppMail appMail, long accIouSlipId, String projectName, String employeeName, String designation, String totalAmount, String createdOn) {
        LinkedHashMap chkMailMap = checkMail(appMail, projectId)
        Boolean isError = (Boolean) chkMailMap.get(Tools.IS_ERROR)
        if (isError.booleanValue()) {
            return Boolean.FALSE
        }

        Map parameters = [accIouSlipId: accIouSlipId, projectName: projectName, employeeName: employeeName, designation: designation, totalAmount: totalAmount, createdOn: createdOn]
        buildMailBody(appMail, parameters)
        List<String> userMailAddress = (List<String>) chkMailMap.get(MAIL_ADDRESS)
        executeMail(appMail, userMailAddress)
        return Boolean.TRUE
    }

    public Boolean sendMailForResetPassword(AppMail appMail, AppUser user, String resetLink) {
        Company company = (Company) companyCacheUtility.read(user.companyId)
        Map parameters = [userName: user.username, loginId: user.loginId,
                link: resetLink, securityCode: user.passwordResetCode, company: company.name]
        if (PluginConnector.isPluginInstalled(PluginConnector.EXCHANGE_HOUSE)) {
            checkAndSendMailForExchangeHouse(appMail, user.loginId, parameters)
        } else {
            buildMailBody(appMail, parameters)
            executeMail(appMail, user.loginId)
        }
        return Boolean.TRUE
    }


    public String checkAndSendMailForExchangeHouse(AppMail appMail, String emailAddr, Map parameters) {
        buildMailBody(appMail, parameters)
        Environment.executeForCurrentEnvironment {
            development {
                executeMail(appMail, emailAddr)          // For Development
            }
            production {
                executeMailForSFSA(appMail, emailAddr)   // For Production
            }
        }
        return null
    }

    private LinkedHashMap checkMail(AppMail appMail, long projectId) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.TRUE)

        String roleIds = appMail.roleIds
        List<String> lstRoles = roleIds.split(Tools.COMA)

        if (lstRoles.size() <= 0) {
            result.put(Tools.MESSAGE, ROLE_NOT_FOUND)
            return result
        }

        List<String> mailAddress = getUserMailAddress(roleIds, projectId)
        if (mailAddress.size() <= 0) {
            result.put(Tools.MESSAGE, USER_NOT_FOUND)
            return result
        }

        result.put(MAIL_ADDRESS, mailAddress)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
    }

    private void buildMailBody(AppMail appMail, Map parameters) {
        SimpleTemplateEngine engine = new SimpleTemplateEngine()
        Writable template = engine.createTemplate(appMail.body).make(parameters)
        appMail.body = template.toString()
    }

    private void executeMail(AppMail appMail, List<String> userMailAddress) {
        executorService.submit({
            println "Sending mail for transaction ${appMail.transactionCode} ..."

            mailService.sendMail {
                to userMailAddress
                subject appMail.subject
                html appMail.body
            }
            println "Mail sent successfully for ${appMail.transactionCode}"
        })
    }

    private void executeMail(AppMail appMail, String userMailAddress) {
        executorService.submit({
            println "Sending mail for transaction ${appMail.transactionCode} ..."

            mailService.sendMail {
                to userMailAddress
                subject appMail.subject
                html appMail.body
            }
            println "Mail sent successfully for ${appMail.transactionCode}"
        })
    }


    private static final String MAIL_HOST = "mail.smtp.host"
    private static final String MAIL_PORT = "mail.smtp.port"
    private static final String MAIL_MIME = "text/html; charset=utf-8"

    private void executeMailForSFSA(AppMail appMail, String addressTo) {
        try {
            String host = grailsApplication.config.mis.exh.sfsa.mail.smtp.host
            String port = grailsApplication.config.mis.exh.sfsa.mail.smtp.port
            String email = grailsApplication.config.mis.exh.sfsa.mail.smtp.email
            String pwd = grailsApplication.config.mis.exh.sfsa.mail.smtp.pwd
            Company company = (Company) companyCacheUtility.read(appMail.companyId)
            String emailFrom = company.email
            if (!emailFrom) {
                emailFrom = grailsApplication.config.mis.exh.sfsa.mail.from
            }
            Properties props = new Properties()
            props.put(MAIL_HOST, host)
            props.put(MAIL_PORT, port)

            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(email, pwd)
                        }
                    })
            Message message = new MimeMessage(session)
            message.setFrom(new InternetAddress(emailFrom))
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(addressTo))
            message.setSubject(appMail.subject);
            message.setText(appMail.body)
            message.setContent(appMail.body, MAIL_MIME);
            println "Sending mail for transaction ${appMail.transactionCode} ..."
            Transport.send(message)
            println "Mail sent successfully for ${appMail.transactionCode}"
        } catch (Exception e) {
            log.error(e.getMessage())
            println('Mail Exception: ' + e.getMessage())
        }
    }

    public List<String> getMailAddresses(AppMail appMail, long projectId) {

        String roleIds = appMail.roleIds
        List<String> lstRoles = roleIds.split(Tools.COMA)

        if (lstRoles.size() <= 0) throw new RuntimeException('Role not found in Mail object')

        List<String> mailAddress = getUserMailAddress(roleIds, projectId)
        return mailAddress
    }

    public List<String> getUserMailAddress(String roleIds, long projectId) {
        long companyId = appSessionUtil.getCompanyId()
        SystemEntity appUserSysEntityObject = (SystemEntity) appUserEntityTypeCacheUtility.readByReservedAndCompany(appUserEntityTypeCacheUtility.PROJECT, companyId)

        String query = """
            SELECT au.login_id FROM app_user_entity app
            LEFT JOIN app_user au ON au.id= app.app_user_id
                WHERE app.entity_id = :projectId AND
                      app.entity_type_id = :project AND
                      app.app_user_id IN
                          (SELECT ur.user_id FROM user_role ur
                                WHERE ur.role_id IN
                                    (SELECT id FROM role WHERE role_type_id IN (${roleIds})
                                        AND company_id = :companyId))
        """

        Map queryParams = [
                projectId: projectId,
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

    public void createDefaultDataForApplication(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,is_required_recipients,recipients, controller_name, action_name)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear \${userName}, <br/>
                Your Login ID : \${loginId}
            </p>
            <p>
                To reset your password please click the link below (or copy/paste into your web browser):<br/>
                <a target="_blank" href="\${link}">\${link}</a>
            </p>
            <p>
                For security reason, you will be asked for a security code.<br/>
                Your security code is <strong>\${securityCode}</strong>
            </p>
            <p>
                <strong>Please Note, you must reset your password within 24 hours of your request.</strong>
            </p>
            <p>
                Regards,<br/>
                \${company}
            </p>
            <i>This is an auto-generated email, which does not need reply.<br/></i>
            </div>', :companyId, true, 'html', 'Your request for password reset', 'SendMailForPasswordResetActionService', null, 1, false, false,false,null, null, null);
        """, queryParams)

        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,is_required_recipients,recipients, controller_name, action_name)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
                <p>
                    Dear Concerned, <br/>
                         This is a test purpose mail .If you receive this mail,
                         it means our mail sending functionality is ok.
                </p>

                <i>This is an auto-generated email, which does not need reply.<br/>
           </div>', :companyId, true, 'html', 'Test AppMail :', 'TestAppMailActionService', null, 1, false, true, true, null, 'appMail','testAppMail');
        """, queryParams)
    }

    public void createDefaultDataForProcurement(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,is_required_recipients,recipients, controller_name, action_name)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following purchase order requires your approval for further processing. <br/>
            </p>
            <p>
                <b>PO No:</b> \${poId} <br/>
                <b>Created On:</b> \${poCreatedOn} <br/>
                <b>Created By:</b> \${poCreatedBy} <br/>
            </p>
            <p>
                The purchase order report is attached with this mail.
            </p>
            If you have already approved above PO, please ignore this mail. <br/>
            <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
            </div>', :companyId, true, 'html', 'MIS Notification-PO approval', 'SendForApprovalPurchaseOrderActionService', '-4,-5', 5, true, false,false,null, null, null);
        """, queryParams)

        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,is_required_recipients,recipients, controller_name, action_name)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following purchase request requires your approval for further processing. <br/>
            </p>
            <p>
                <b>PR No:</b> \${prId} <br/>
                <b>Created On:</b> \${prCreatedOn} <br/>
                <b>Created By:</b> \${prCreatedBy} <br/>
            </p>
            <p>
                The purchase request report is attached with this mail.
            </p>
            If you have already approved above PR, please ignore this mail. <br/>
            <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
            </div>', :companyId, true, 'html', 'MIS Notification-PR approval', 'SentMailPurchaseRequestActionService', '-4,-5', 5, true, false,false,null, null, null);
        """, queryParams)

        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,is_required_recipients,recipients, controller_name, action_name)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following purchase request has been re-opened for editing/modification. <br/>
            </p>
            <p>
                <b>PR No:</b> \${prId} <br/>
                <b>Opened On:</b> \${prOpenedOn} <br/>
                <b>Opened By:</b> \${prOpenedBy} <br/>
            </p>
            The modified/final purchase request should come to you later for further approval. <br/>
            <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
            </div>', :companyId, true, 'html', 'MIS Notification-PR Re-opened', 'UnApprovePurchaseRequestActionService', '-4,-5', 5, true, false, false, null, null, null);
        """, queryParams)

        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,is_required_recipients,recipients, controller_name, action_name)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following purchase order has been re-opened for editing/ modification. <br/>
            </p>
            <p>
                <b>PO No:</b> \${poId} <br/>
                <b>Opened On:</b> \${poOpenedOn} <br/>
                <b>Opened By:</b> \${poOpenedBy} <br/>
            </p>
            The modified/final purchase order should come to you later for further approval. <br/>
            <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
            </div>', :companyId, true, 'html', 'MIS Notification-PO Re-opened', 'UnApprovePurchaseOrderActionService', '-4,-5', 5, true, false,false,null, null, null);
        """, queryParams)

        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,is_required_recipients,recipients, controller_name, action_name)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following Indent requires your approval for further processing. <br/>
            </p>
            <p>
                <b>Indent Trace No :</b> \${indentId} <br/>
                <b>Project     :</b> \${projectName} <br/>
                <b>Created By  :</b> \${createdBy} <br/>
                <b>Created On  :</b> \${createdOn} <br/>
            </p>
            If you have already approved above Indent, please ignore this mail. <br/>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>', :companyId, true, 'html', 'MIS Notification for Indent:', 'CreateIndentActionService', '-4,-5', 5, true, false,false,null, null, null);
        """, queryParams)
    }

    public void createDefaultDataForInventory(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,is_required_recipients,recipients, controller_name, action_name)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following inventory consumption requires your approval for further processing. <br/>
            </p>
            <p>
                <b>Inventory Name:</b> \${inventoryName} <br/>
                <b>Item Name:</b> \${itemName} <br/>
                <b>Quantity:</b> \${quantityUnit} <br/>
                <b>Created On:</b> \${createdOn} <br/>
            </p>
            If you have already approved above consumption, please ignore this mail.<br/>
            <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
            </div>', :companyId, true, 'html', 'Consumption approval Notification - Site:', 'CreateForInventoryConsumptionDetailsActionService', '-6', 4, true, false,false,null, null, null);
        """, queryParams)

        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,is_required_recipients,recipients, controller_name, action_name)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following mail contains an attachment of all unapproved inventory transaction. <br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>', :companyId, true, 'html', 'All Unapproved Inventory Transaction:', 'SendMailForInventoryTransactionActionService', '-4,-5,-10', 4, true, true,false,null, 'invInventoryTransaction', 'sendMailForInventoryTransaction');
        """, queryParams)
    }

    public void createDefaultDataForAccounting(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,is_required_recipients,recipients, controller_name, action_name)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following IOU Slip requires your approval for further processing. <br/>
            </p>
            <p>
                <b>Trace No :</b> \${accIouSlipId} <br/>
                <b>Project     :</b> \${projectName} <br/>
                <b>Employee    :</b> \${employeeName} <br/>
                <b>Designation :</b> \${designation} <br/>
                <b>Total Amount:</b> \${totalAmount} <br/>
                <b>Created On  :</b> \${createdOn} <br/>
            </p>
            If you have already approved above IOU Slip, please ignore this mail. <br/>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>', :companyId, true, 'html', 'MIS Notification for IOU Slip:', 'SentNotificationAccIouSlipActionService', '-4,-5', 2, true, false,false,null,null,null);
        """, queryParams)

        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,is_required_recipients,recipients, controller_name, action_name)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following mail contains an attachment of all un-posted voucher. <br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>', :companyId, true, 'html', 'All Un-posted Voucher:', 'SendMailForUnpostedVoucherActionService', '-4,-5,-10', 2, true, true,false,null, 'accReport','sendMailForUnpostedVoucher');
        """, queryParams)

        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,is_required_recipients,recipients, controller_name, action_name)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following mail contains an attachment of Project wise expense. <br/>
            </p>
            <p>
                <b>Project     :</b> \${project} <br/>
                <b>Group       :</b> \${group} <br/>
                <b>Date Range  :</b> \${dateRange} <br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>', :companyId, true, 'html', 'Project Wise Expense:', 'SendMailForProjectWiseExpenseActionService', '-4,-5,-10', 2, true, true,false,null,'accReport','sendMailForProjectWiseExpense');
        """, queryParams)

        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id,
                  is_required_role_ids, is_manual_send,is_required_recipients,recipients, controller_name, action_name)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following mail contains an attachment of supplier wise payable. <br/>
            </p>
            <p>
                <b>Project     :</b> \${project} <br/>
                <b>Date Range  :</b> \${dateRange}  <br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>', :companyId, true, 'html', 'Supplier Wise Payable:', 'SendMailForSupplierWisePayableActionService', '-4,-5,-10', 2,
	    true, true,false,null, 'accReport', 'sendMailForSupplierWisePayable');
	    """, queryParams)
    }

    private static final String UPDATE_QUERY =
            """
                    UPDATE app_mail SET
                          version=:newVersion,
                          subject=:subject,
                          body=:body,
                          transaction_code=:transactionCode,
                          role_ids=:roleIds,
                          recipients=:recipients,
                          is_active=:isActive
                    WHERE
                          id=:id AND
                          version=:version
    """

    public int update(AppMail appMail) {

        Map queryParams = [
                id: appMail.id,
                newVersion: appMail.version + 1,
                version: appMail.version,
                subject: appMail.subject,
                body: appMail.body,
                transactionCode: appMail.transactionCode,
                roleIds: appMail.roleIds,
                recipients: appMail.recipients,
                isActive: appMail.isActive
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update mail information')
        }
        appMail.version = appMail.version + 1
        return updateCount;
    }

    public AppMail findByTransactionCodeAndIsActive(String transactionCode, Boolean boolValue) {
        AppMail appMail = AppMail.findByTransactionCodeAndIsActive(transactionCode, boolValue, [readOnly: true])
        return appMail
    }

    public AppMail findByTransactionCodeAndCompanyIdAndIsActive(String transactionCode, long companyId, Boolean boolValue) {
        AppMail appMail = AppMail.findByTransactionCodeAndCompanyIdAndIsActive(transactionCode, companyId, boolValue, [readOnly: true])
        return appMail
    }

    public AppMail findByTransactionCode(String useCase) {
        AppMail appMail = AppMail.findByTransactionCode(useCase, [readOnly: true])
        return appMail
    }

    /**
     * Method to count AppMail
     * @param companyId - company id
     * @return - an integer value of AppMail count
     */
    public int countByCompanyIdAndPluginId(long companyId, int pluginId) {
        int count = AppMail.countByCompanyIdAndPluginId(companyId, pluginId)
        return count
    }

    /**
     * Method to find the list of AppMail
     * @param companyId - company id
     * @return - a list of AppMail
     */
    public List findAllByCompanyIdAndPluginId(long companyId, int pluginId) {
        List sysConList = AppMail.findAllByCompanyIdAndPluginId(companyId, pluginId, [max: resultPerPage, offset: start, sort: SORT_ON_ID, order: ASCENDING_SORT_ORDER, readOnly: true])
        return sysConList
    }

    public void createDefaultDataForProjectTrack(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,is_required_recipients,recipients, controller_name, action_name)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, '<div>
            <p>
                Dear Concerned, <br/>
                The following Sprint is Completed. So You may Close the Sprint.<br/>
            </p>
            <p>
                <b>ID            :</b> \${id} <br/>
                <b>Name          :</b> \${name} <br/>
                <b>Start Date    :</b> \${startDate} <br/>
                <b>Completed On  :</b> \${completedOn} <br/>
                <b>Total Task(s) :</b> \${totalTask} <br/>
                <b>Total Bug(s)  :</b> \${totalBug} <br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
            </div>', :companyId, true, 'html', 'Sprint status Completed', 'PtUpdateSprintActionService',
            '${roleTypeCacheUtility.ROLE_SOFTWARE_PROJECT_ADMIN},${roleTypeCacheUtility.ROLE_SQA_ENGINEER}',
            10, true, false, false, null, null, null);
        """, queryParams)
    }

    public void createDefaultDataForExchangeHouse(long companyId) {

        String mailBody = """<div>
                <p>
                    Dear Mr/Ms \${name}, <br/>
                    Thank you for registration with us. Please activate your account.<br/>
                    To activate your user account please click the link below: <br/>
                    <a target="_blank" href="\${link}">\${link}</a>
                </p>

                If you have already activated your account, please ignore this mail. <br/>
                <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
            </div>"""
        executeInsertSql("""
            INSERT INTO "app_mail"
            ("id", "version", "body", "company_id", "is_active", "is_required_role_ids", "mime_type", "plugin_id", "subject", "transaction_code", "role_ids", "is_manual_send", "controller_name", "action_name", "is_required_recipients", "recipients")
            VALUES
            (NEXTVAL('app_mail_id_seq'), 0, '${mailBody}', '${
            companyId
        }', '1', 'FALSE', 'html', '9', 'Activate your account with SFSA', 'ExhSignupForCustomerUserActionService', '${
            roleTypeCacheUtility.ROLE_TYPE_EXH_CUSTOMER
        }', 'TRUE', null, null, 'f', null);
        """)

        String customerUserMailBody = """<div>
                <p>
                    Dear Mr/Ms \${name}, <br/>
                    Thank you for registration with us.<br/><br/>
                    Your login information :<br/>
                    Login ID : \${loginId}<br/>
                    Password : \${password}<br/> <br/>
                    Please activate your account by clicking the link below (or copy/paste into your web browser): <br/>
                    <a target="_blank" href="\${link}">\${link}</a>
                </p>

                If you have already activated your account, please ignore this mail. <br/>
                <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
            </div>"""
        executeInsertSql("""
            INSERT INTO "app_mail"
            ("id","version", "body", "company_id", "is_active", "is_required_role_ids", "mime_type", "plugin_id", "subject", "transaction_code", "role_ids", "is_manual_send", "controller_name", "action_name", "is_required_recipients", "recipients")
            VALUES
            (NEXTVAL('app_mail_id_seq'), 0, '${customerUserMailBody}', ${
            companyId
        }, '1', 'FALSE', 'html', '9', 'Activate your account with SFSA', 'ExhCreateForCustomerUserActionService', '${
            roleTypeCacheUtility.ROLE_TYPE_EXH_CASHIER
        }', 'TRUE', null, null, 'f', null);
        """)
    }

    public void createDefaultDataForDocument(long companyId) {
        String mailSubject = "Invitation to join \${categoryLabel} '\${categoryName}'"
        String categoryDetailsMailBodyForSubCategory = """<div>
            <p>
                \${message}<br/>
            </p>
            <p>
                <strong>\${categoryLabel} Details:</strong>
            </p>
            <p>
                Name: \${categoryName}, <br/>
                Description : \${categoryDescription}<br/>
            </p>
            <p>
                <strong>\${subCategoryLabel} Details:</strong>
            </p>
            <div>
               \${subCategoryDetails}
            </div>
            <p>
               Please click the  <a href="\${invitationLink}">Invitation Link</a></strong>  to be a member of this category.
            </p>
            <p>
                <strong>Please note, this invitation will expire after \${expireTime} days.</strong>
            </p>
            <i>This is an auto-generated email, which does not need reply.<br/></i>
            </div>"""
        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name,is_required_recipients)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :categoryDetailsMailBody, ${companyId}, true, 'html'
                            ,:mailSubject, 'SendInvitationDocInvitedMembersActionServiceForSubCategory', null, 13, false, false, null,null,false);
        """, [categoryDetailsMailBody: categoryDetailsMailBodyForSubCategory, mailSubject: mailSubject])

        String categoryDetailsMailBodyForCategory = """<div>
            <p>
                \${message}<br/>
            </p>
            <p>
                <strong>\${categoryLabel} Details:</strong>
            </p>
            <p>
                Name: \${categoryName}, <br/>
                Description : \${categoryDescription}<br/>
            </p>
            <p>
               Please click the  <a href="\${invitationLink}">Invitation Link</a></strong>  to be a member of this category.
            </p>
            <p>
                <strong>Please note, this invitation will expire after \${expireTime} days.</strong>
            </p>
            <i>This is an auto-generated email, which does not need reply.<br/></i>
            </div>"""
        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name,is_required_recipients)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :categoryDetailsMailBody, ${companyId}, true, 'html'
                            ,:mailSubject, 'SendInvitationDocInvitedMembersActionServiceForCategory', null, 13, false, false, null,null,false);
        """, [categoryDetailsMailBody: categoryDetailsMailBodyForCategory, mailSubject: mailSubject])

        /* ApprovedDocMemberJoinRequestActionService*/
        String categoryApproveMailSubject = "Congratulations to be a member of \${categoryLabel} '\${categoryName}'"
        String categoryApprovedMailBody =
                """ <div>
                    <p>
                    <strong>
                        Congratulations,
                        your application for the membership of following \${categoryLabel} has been approved.
                    </strong>
                    <p>
                        <strong>\${categoryLabel} Details:</strong> <br/>
                        Name: \${categoryName}, <br/>
                        Description : \${categoryDescription} <br/>
                    </p>

                        Login ID: \${loginId} <br/>
                        \${password} <br/>
                        Please click <a href="\${loginUrl}"> here </a> to login
                    </p>
                    <i>This is an auto-generated email, which does not need reply.<br/></i>
                    </div>
                """
        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name,is_required_recipients)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :categoryApprovedMailBody, ${companyId}, true, 'html'
                    ,:categoryApproveMailSubject, 'ApprovedDocMemberJoinRequestForCategoryActionService', null, 13, false, false, null,null,false);
        """, [categoryApprovedMailBody: categoryApprovedMailBody, categoryApproveMailSubject: categoryApproveMailSubject])


        String subCategoryApproveMailSubject = "Congratulations to be a member of \${subCategoryLabel} '\${subCategoryName}'"
        String subCategoryApprovedMailBody =
                """ <div>
                    <p>
                    <strong>
                        Congratulations,
                        your application for the membership of following \${subCategoryLabel} has been approved.
                    </strong>

                    <p>
                    <strong>\${subCategoryLabel} Details:</strong> <br/>
                    Name: \${subCategoryName} <br/>
                    Description :\${subCategoryDescription} <br/>
                    </p>
                    <p>
                        <strong>\${categoryLabel} Details:</strong> <br/>
                        Name: \${categoryName} <br/>
                        Description : \${categoryDescription} <br/>
                    </p>
                        Login ID: \${loginId} <br/>
                        \${password} <br/>
                        Please click <a href="\${loginUrl}"> here </a> to login
                    </p>
                    <i>This is an auto-generated email, which does not need reply.<br/></i>
                    </div>
                """
        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name,is_required_recipients)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :subCategoryApprovedMailBody, ${companyId}, true, 'html'
                    ,:subCategoryApproveMailSubject, 'ApprovedDocMemberJoinRequestForSubCategoryActionService', null, 13, false, false, null,null,false);
        """, [subCategoryApprovedMailBody: subCategoryApprovedMailBody, subCategoryApproveMailSubject: subCategoryApproveMailSubject])

        String categoryAcceptMailBody = """<div>
            <p>
            <strong>
                Congratulations,
                your membership of following \${categoryLabel} has been accepted.
            </strong>
            <p>
                <strong>\${categoryLabel} Details:</strong> <br/>
                Name: \${categoryName}, <br/>
                Description : \${categoryDescription} <br/>
            </p>
                Login ID: \${loginId} <br/>
                Please click <a href="\${loginUrl}"> here </a> to login
            </p>
            <i>This is an auto-generated email, which does not need reply.<br/></i>
            </div>"""
        executeInsertSql("""
            INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send, controller_name, action_name,is_required_recipients)
            VALUES (NEXTVAL('app_mail_id_seq'), 0, :categoryAcceptMailBody, ${companyId}, true, 'html'
                    ,:categoryApproveMailSubject, 'AcceptInvitationDocInvitedMemberActionService', null, 13, false, false, null,null,false);
        """, [categoryAcceptMailBody: categoryAcceptMailBody, categoryApproveMailSubject: categoryApproveMailSubject])
    }
}