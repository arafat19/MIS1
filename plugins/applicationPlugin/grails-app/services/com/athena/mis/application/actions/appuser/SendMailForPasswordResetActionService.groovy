package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

/**
 * Generate password reset link and code and send mail to user
 * For details go through Use-Case doc named 'SendMailForPasswordResetActionService'
 */
class SendMailForPasswordResetActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass());

    SpringSecurityService springSecurityService
    AppMailService appMailService
    LinkGenerator grailsLinkGenerator
    AppUserService appUserService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility

    private static final String INVALID_INPUT = 'Enter your login ID'
    private static final String FAILURE_MESSAGE = 'Could not send mail to user for password reset'
    private static final String USER_NOT_FOUND_MSG = 'User not found with the given login ID'
    private static final String USER_NOT_ENABLE_MSG = 'Your account is disabled, contact with administrator'
    private static final String USER_ACC_LOCKED_MSG = 'Your account is locked, contact with administrator'
    private static final String APP_USER = 'appUser'
    private static final String SHOW_RESET_PASSWORD = 'showResetPassword'
    private static final String DATE_FORMAT = "dd_MMM_yy_hh_mm_ss"
    private static final String TRANSACTION_CODE = "SendMailForPasswordResetActionService"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Mail sending for password reset is not activated"
    private static final String SUCCESS_MESSAGE = "Please check your mail and follow the given instructions"
    private static final String EMAIL_PATTERN = """^[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\\.[a-zA-Z]{2,4}\$"""
    private static final String INVALID_EMAIL = "Invalid email address"

    /**
     * Get parameters from UI and check pre condition
     * 1. check required parameter
     * 2. check email pattern
     * 3. check if user exists or not
     * 4. check if user is enable or not
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check required parameter
            if (!parameterMap.loginId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            String loginId = parameterMap.loginId
            // check email pattern
            if (!loginId.matches(EMAIL_PATTERN)) {
                result.put(Tools.MESSAGE, INVALID_EMAIL)
                return result
            }

            HttpServletRequest request = (HttpServletRequest) obj
            long companyId = getCompanyId(request)

            AppUser user = appUserService.findByLoginIdAndCompanyId(loginId, companyId)
            // check if user exists or not
            if (!user) {
                result.put(Tools.MESSAGE, USER_NOT_FOUND_MSG)
                return result
            }
            // check if user is enable or not
            if (!user.enabled) {
                result.put(Tools.MESSAGE, USER_NOT_ENABLE_MSG)
                return result
            }
            // check if user is locked or not
            if (user.accountLocked) {
                result.put(Tools.MESSAGE, USER_ACC_LOCKED_MSG)
                return result
            }
            result.put(APP_USER, user)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * retrieve companyUser from request
     * @param request
     * @return company ID
     */

    private long getCompanyId(HttpServletRequest request) {
        String fullUrl = Tools.getFullUrl(request, false)    // retrieve url with www
        Company company = companyCacheUtility.readByWebUrl(fullUrl) // compare with www
        if (company) {
            return company.id
        }
        // if company not found try to retrieve url without www
        fullUrl = Tools.getFullUrl(request, true)
        company = companyCacheUtility.readByWebUrlWithoutWWW(fullUrl)     // compare without www
        if (company) {
            return company.id
        } else {
            return 0L
        }
    }


    /**
     * Set and update properties of AppUser for reset password
     * Send mail to user
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map contains isError(true/false) depending on method success and related message
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            Map preResult = (Map) obj   // cast map returned from executePreCondition method
            AppUser user = (AppUser) preResult.get(APP_USER)
            setPasswordResetProperties(user)    // set properties for reset password
            updatePasswordResetProperties(user) // update properties for reset password
            String msg = sendMail(user) // send mail to user
            if (msg) {
                result.put(Tools.MESSAGE, msg)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SUCCESS_MESSAGE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException(FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
     * Do nothing for build success result for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Do nothing for build failure result for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Set properties of AppUser for reset password
     * @param user -object of AppUser
     */
    private void setPasswordResetProperties(AppUser user) {
        user.passwordResetLink = springSecurityService.encodePassword(user.loginId + new Date().format(DATE_FORMAT))
        user.passwordResetCode = generateSecurityCode() // generate security code
        user.passwordResetValidity = new Date() + 1
    }

    private static final String CHAR_SET = "ABCDEFGHIJKLMNPQRSTUVWXYZ1234567891234567891234567"
    private static final int CODE_LENGTH = 5

    /**
     * Generate security code for reset password
     * @return -generated security code
     */
    private String generateSecurityCode() {
        Random random = new Random()
        char[] code = new char[CODE_LENGTH];
        for (int i = 0; i < CODE_LENGTH; i++) {
            code[i] = CHAR_SET.charAt(random.nextInt(CHAR_SET.length()));
        }
        return new String(code);
    }

    /**
     * Generate link and send mail to user
     * @param user -object of AppUser
     * @return -error message if mail template is not found else null
     */
    private String sendMail(AppUser user) {
        String link = grailsLinkGenerator.link(controller: APP_USER, action: SHOW_RESET_PASSWORD, absolute: true, params: [link: user.passwordResetLink])
        AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, user.companyId, true)
        if (!appMail) {
            return MAIL_TEMPLATE_NOT_FOUND
        }
        appMailService.sendMailForResetPassword(appMail, user, link)
        return null
    }

    private static final String UPDATE_USER = """
        UPDATE app_user
        SET
            password_reset_link=:passwordResetLink,
            password_reset_code=:passwordResetCode,
            password_reset_validity=:passwordResetValidity,
            version=version+1
        WHERE
            id=:id AND
            version=:version
    """

    /**
     * Update AppUser properties for reset password
     * Update cache utility
     * @param user -object of AppUser
     * @return -an integer containing the value of update count
     */
    private int updatePasswordResetProperties(AppUser user) {
        Map queryParams = [
                passwordResetLink: user.passwordResetLink,
                passwordResetCode: user.passwordResetCode,
                passwordResetValidity: DateUtility.getSqlDateWithSeconds(user.passwordResetValidity),
                id: user.id,
                version: user.version
        ]
        int updateCount = executeUpdateSql(UPDATE_USER, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating user information for password reset')
        }
        user.version = user.version + 1
        appUserCacheUtility.update(user, appUserCacheUtility.SORT_ON_NAME, appUserCacheUtility.SORT_ORDER_ASCENDING)
        return updateCount
    }
}
