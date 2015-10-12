package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppSysConfigurationCacheUtility
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

/**
 * Reset expired password of user and related properties
 * For details go through Use-Case doc named 'ResetExpiredPasswordActionService'
 */
class ResetExpiredPasswordActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass());

    SpringSecurityService springSecurityService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    AppSysConfigurationCacheUtility appSysConfigurationCacheUtility

    private static final String USER_NOT_FOUND_MSG = 'User not found'
    private static final String DEFAULT_FAILURE_MESSAGE = 'Could not change password'
    private static final String SUCCESS_MESSAGE = 'Password changed successfully, please login again'
    private static final String APP_USER = 'appUser'
    private static final String SAME_EXPIRED_MSG = 'Previous and new password can not be same'
    private static final String OLD_PASSWORD_MISMATCH_MSG = 'Previous password mismatched, please type again'
    private static final String PASSWORD_MISMATCH_MSG = 'Password mismatched, please type again'
    private static final String INVALID_PASS_LENGTH = 'Password must have at least 8 characters'
    private static
    final String INVALID_COMBINATION = 'Password should contain combination of letters, numbers & special character'
    private static final String PASSWORD_PATTERN = """^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\\W]))|((?=.*[a-z])(?=.*[\\d])(?=.*[\\W]))|((?=.*[A-Z])(?=.*[\\d])(?=.*[\\W]))).*\$"""

    /**
     * Get parameters from UI and check pre condition
     * 1. check required parameter
     * 2. check if user exists or not
     * 3. check previous password for confirmation
     * 4. check previous and new password, both can not be same
     * 5. check password confirmation
     * 6. check length of password
     * 7. check password combination
     * @param params -serialized parameters from UI
     * @param obj -request
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
            if ((!parameterMap.userName) || (!parameterMap.userId) || (!parameterMap.oldPassword) || (!parameterMap.password) || (!parameterMap.retypePassword)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            HttpServletRequest request = (HttpServletRequest) obj
            long companyId = getCompanyId(request)
            long userId = Long.parseLong(parameterMap.userId.toString())
            AppUser user = (AppUser) appUserCacheUtility.read(userId, companyId)
            // check if user exists or not
            if (!user) {
                result.put(Tools.MESSAGE, USER_NOT_FOUND_MSG)
                return result
            }
            String oldPassword = parameterMap.oldPassword
            String encodedOldPassword = springSecurityService.encodePassword(parameterMap.oldPassword)
            String password = parameterMap.password
            String retypePassword = parameterMap.retypePassword
            // check old password confirmation
            if (!encodedOldPassword.equals(user.password)) {
                result.put(Tools.MESSAGE, OLD_PASSWORD_MISMATCH_MSG)
                return result
            }
            // check old and new password
            if (oldPassword.equals(password)) {
                result.put(Tools.MESSAGE, SAME_EXPIRED_MSG)
                return result
            }
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
            setPasswordResetProperties(user, parameterMap)    // set password reset properties
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
     * Set properties of AppUser for reset password
     * @param user -object of AppUser
     * @param params -serialized parameters from UI
     */
    private void setPasswordResetProperties(AppUser user, GrailsParameterMap params) {
        user.password = springSecurityService.encodePassword(params.password)
        SysConfiguration sysConfig = (SysConfiguration) appSysConfigurationCacheUtility.readByKeyAndCompanyId(appSysConfigurationCacheUtility.DEFAULT_PASSWORD_EXPIRE_DURATION, user.companyId)
        if (sysConfig) {
            user.nextExpireDate = new Date() + Integer.parseInt(sysConfig.value)
        } else {
            user.nextExpireDate = new Date()
        }
    }

    private static final String UPDATE_USER = """
        UPDATE app_user
        SET
            password=:password,
            next_expire_date=:nextExpireDate,
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
                nextExpireDate: DateUtility.getSqlDateWithSeconds(user.nextExpireDate),
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
