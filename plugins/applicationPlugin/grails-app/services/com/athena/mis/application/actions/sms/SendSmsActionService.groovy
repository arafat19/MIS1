package com.athena.mis.application.actions.sms

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Sms
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.SmsCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Send sms to recipients
 *  For details go through Use-Case doc named 'SendSmsActionService'
 */
class SendSmsActionService extends BaseService implements ActionIntf {

    private static final String SHOW_FAILURE_MESSAGE = "SMS has not been send"
    private static final String SHOW_SUCCESS_MESSAGE = "SMS has been sent successfully"
    private static final String NO_ACCESS_MESSAGE = "Log in user is not authorized to send this sms"
    private static final String LST_SMS = "lstSms"
    private static final String NOT_FOUND_MSG = "The selected sms is not active"
    private static final String RECIPIENTS_NOT_FOUND = 'Recipients not found'
    private static final String RECIPIENT = 'recipient'
    private static final String CONTENT = 'content'
    private static final String UTF_8 = "UTF-8"

    private Logger log = Logger.getLogger(getClass())

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    SmsCacheUtility smsCacheUtility

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
            if (!appSessionUtil.getAppUser().isConfigManager) {
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
            long companyId = appSessionUtil.getCompanyId()
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
     * Send sms to recipients
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing isError(true/false) & relevant message depending on method success
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            List<Sms> lstSms = (List<Sms>) preResult.get(LST_SMS)
            String msg = sendSms(lstSms)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Send sms
     * @param lstSms -list of objects of sms
     * @return -a string containing null value or error message depending on method success
     */
    private String sendSms(List<Sms> lstSms) {
        String msg = null
        for (int i = 0; i < lstSms.size(); i++) {
            Sms sms = lstSms[i]
            if ((!sms.recipients) || (sms.recipients.length() == 0)) {
                msg = RECIPIENTS_NOT_FOUND
                return msg
            }

            // First evaluate content
            Binding binding = new Binding()
            GroovyShell shell = new GroovyShell(binding)
            String content = shell.evaluate(sms.body)
            String encodedContent= URLEncoder.encode(content,UTF_8);

            String phoneNumbers = sms.recipients
            for (String currentPhoneNumber : phoneNumbers.split(Tools.COMA)) {
                currentPhoneNumber = currentPhoneNumber.trim()
                // now evaluate full sms url
                binding = new Binding()
                binding.setVariable(RECIPIENT, currentPhoneNumber)
                binding.setVariable(CONTENT, encodedContent)
                shell = new GroovyShell(binding)
                String strSms = shell.evaluate(sms.url)
                strSms.toURL().text
            }
        }
        return msg
    }
}
