package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.utility.Tools
import grails.plugin.mail.MailService
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

import java.util.concurrent.ExecutorService

/**
 * Send mail for test purpose.
 * For details go through Use-Case doc named 'TestAppMailActionService'
 */
class TestAppMailActionService extends BaseService implements ActionIntf {

    AppMailService appMailService
    MailService mailService
    ExecutorService executorService
    JasperService jasperService
    AppSessionUtil appSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String INVALID_INPUT = "Unable to sent mail due to invalid input"
    private static final String DEFAULT_ERROR_MESSAGE = "Fail to send mail"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Mail template not found"
    private static final String MAIL_SENDING_SUCCESS = "Mail sent successfully"
    private static final String RECIPIENTS_NOT_FOUND = "Recipient(s) not found to send the mail"
    private static final String MAIL_ADDRESS = "mailAddress"
    private static final String TRANSACTION_CODE = "TestAppMailActionService"

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
            LinkedHashMap returnResult = sendMail()    // send mail
            Boolean isError = (Boolean) returnResult.get(Tools.IS_ERROR)
            if (isError.booleanValue()) {
                result.put(Tools.MESSAGE, returnResult.message)
                return result
            }
            result.put(Tools.MESSAGE, returnResult.message)
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
     *    i. if user role ids not found, then mail sent only to recipients
     *    ii. if user role found, then mail sent to both address
     * 3. build mail body template and subject
     * 4. send mail
     * @return -a string containing message or null value depending on method success
     */
    private LinkedHashMap sendMail() {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            long companyId = appSessionUtil.getCompanyId()
            List<String> userMailAddress = []
            AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, companyId, true)
            if (!appMail) {
                result.put(Tools.MESSAGE, MAIL_TEMPLATE_NOT_FOUND)
                return result
            }
            if (!appMail.recipients) {
                result.put(Tools.MESSAGE, RECIPIENTS_NOT_FOUND)
                return result
            }
            LinkedHashMap chkMailMap = checkMail(appMail)
            Boolean isError = (Boolean) chkMailMap.get(Tools.IS_ERROR)
            if (isError.booleanValue()) {
                String recipients = appMail.recipients
                List<String> lstRecipients = recipients.split(Tools.COMA)
                for (int i = 0; i < lstRecipients.size(); i++) {
                    userMailAddress << lstRecipients[i]
                }
            } else {
                userMailAddress << chkMailMap.mailAddress
            }

            executeMail(appMail, userMailAddress)

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, MAIL_SENDING_SUCCESS)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * 1. check role ids of user
     * 2. get user mail addresses
     * @param appMail -object of AppMail
     * @return -a map containing user mail addresses, isError(true/false) and relevant message
     */
    private LinkedHashMap checkMail(AppMail appMail) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.TRUE)
        String roleIds = appMail.roleIds
        if (appMail.roleIds) {
            List<String> lstRoles = roleIds.split(Tools.COMA)
            if (lstRoles.size() <= 0) {
                return result
            }
        } else {
            return result
        }

        List<String> mailAddress = getUserMailAddress(appMail, roleIds)
        if (mailAddress.size() <= 0) {
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
    public List<String> getUserMailAddress(AppMail appMail, String roleIds) {
        String query = """
            SELECT DISTINCT au.login_id FROM app_user au
                WHERE id IN
                      (SELECT ur.user_id FROM user_role ur
                            WHERE ur.role_id IN
                                (SELECT id FROM role WHERE role_type_id IN (${roleIds})
                                    AND company_id = :companyId))

        """

        Map queryParams = [
                companyId: appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> result = executeSelectSql(query, queryParams)

        List<String> userMailAddress = []
        if (appMail.isRequiredRecipients) {
            String recipients = appMail.recipients
            List<String> lstRecipients = recipients.split(Tools.COMA)
            for (int i = 0; i < lstRecipients.size(); i++) {
                userMailAddress << lstRecipients[i]
            }
        }
        for (int i = 0; i < result.size(); i++) {
            userMailAddress << result[i][0]
        }
        return userMailAddress
    }

    /**
     * Send mail
     * @param appMail -object of AppMail
     * @param userMailAddress -list of user mail addresses
     */
    private void executeMail(AppMail appMail, List<String> userMailAddress) {
        executorService.submit({
            println "Sending mail for transaction ${userMailAddress} ..."
            mailService.sendMail {
                to userMailAddress
                subject appMail.subject
                html appMail.body
            }
            println "Mail sent successfully for ${appMail.transactionCode}"
        })
    }
}
