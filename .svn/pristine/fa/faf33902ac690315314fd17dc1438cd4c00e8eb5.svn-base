package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.utility.Tools
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

/**
 * Reset password of user and related properties
 * For details go through Use-Case doc named 'ResetPasswordActionService'
 */
class ResetPasswordActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass());

    AppUserService appUserService
    SpringSecurityService springSecurityService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility

    private static final String USER_NOT_FOUND_MSG = 'User not found or password already been reset'
    private static final String DEFAULT_FAILURE_MESSAGE = 'Could not reset password'
    private static final String SUCCESS_MESSAGE = 'Password has been reset successfully'
    private static final String APP_USER = 'appUser'
    private static final String TIME_EXPIRED_MSG = 'Time to reset password has expired'
    private static final String CODE_MISMATCH_MSG = 'Security code mismatched, please check mail for security code'
    private static final String PASSWORD_MISMATCH_MSG = 'Password mismatched, please type again'
    private static final String INVALID_PASS_LENGTH = 'Password must have at least 8 characters'
    private static final String INVALID_COMBINATION = 'Password should contain combination of letters, numbers & special character'
    private static final String PASSWORD_PATTERN = """^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\\W]))|((?=.*[a-z])(?=.*[\\d])(?=.*[\\W]))|((?=.*[A-Z])(?=.*[\\d])(?=.*[\\W]))).*\$"""

    /**
     * Get parameters from UI and check pre condition
     * 1. check required parameter
     * 2. check if user exists or not
     * 3. check security code
     * 4. check time validation for reset password
     * 5. check password confirmation
     * 6. check length of password
     * 7. check password combination
     * @param params -serialized parameters from UI
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
            if ((!parameterMap.link) || (!parameterMap.code) || (!parameterMap.password) || (!parameterMap.retypePassword)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            String link = parameterMap.link
            HttpServletRequest request = (HttpServletRequest) obj
            long companyId = getCompanyId(request)
            AppUser user = appUserService.findByPasswordResetLinkAndCompanyId(link, companyId)
            // check if user exists or not
            if (!user) {
                result.put(Tools.MESSAGE, USER_NOT_FOUND_MSG)
                return result
            }
            // check security code
            String code = parameterMap.code
            if (!code.equals(user.passwordResetCode)) {
                result.put(Tools.MESSAGE, CODE_MISMATCH_MSG)
                return result
            }
            // check time validation for reset password
            Date currentDate = new Date()
            if (currentDate.compareTo(user.passwordResetValidity) > 0) {
                result.put(Tools.MESSAGE, TIME_EXPIRED_MSG)
                return result
            }
            String password = parameterMap.password
            String retypePassword = parameterMap.retypePassword
            // check password confirmation
            if (!password.equals(retypePassword)) {
                result.put(Tools.MESSAGE, PASSWORD_MISMATCH_MSG)
                return result
            }
            // check length of password
            if (password.length() < 8) {
                result.put(Tools.MESSAGE, INVALID_PASS_LENGTH)
                return result
            }
            // check password combination
            if (!password.matches(PASSWORD_PATTERN)) {
                result.put(Tools.MESSAGE, INVALID_COMBINATION)
                return result
            }
            result.put(APP_USER, user)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MESSAGE)
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
     * @param parameters -serialized parameters from UI
     * @param obj -map returned from executePreCondition method
     * @return -a map contains isError(true/false) depending on method success and related message
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            AppUser user = (AppUser) preResult.get(APP_USER)
            cleanPasswordResetProperties(user, parameterMap)    // set password reset link, code and validity null
            updatePasswordResetProperties(user) // update properties for reset password
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SUCCESS_MESSAGE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Set properties of AppUser for reset password
     * @param user -object of AppUser
     * @param params -serialized parameters from UI
     */
    private void cleanPasswordResetProperties(AppUser user, GrailsParameterMap params) {
        user.password = springSecurityService.encodePassword(params.password)
        user.passwordResetCode = null
        user.passwordResetValidity = null
        user.passwordResetLink = null
    }

    private static final String UPDATE_USER = """
        UPDATE app_user
        SET
            password=:password,
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
                password: user.password,
                passwordResetLink: user.passwordResetLink,
                passwordResetCode: user.passwordResetCode,
                passwordResetValidity: user.passwordResetValidity,
                id: user.id,
                version: user.version
        ]
        int updateCount = executeUpdateSql(UPDATE_USER, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while reset password')
        }
        user.version = user.version + 1
        appUserCacheUtility.update(user, appUserCacheUtility.SORT_ON_NAME, appUserCacheUtility.SORT_ORDER_ASCENDING)
        return updateCount
    }
}
