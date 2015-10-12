package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

/**
 * Show UI for reset password
 * For details go through Use-Case doc named 'ShowForResetPasswordActionService'
 */
class ShowForResetPasswordActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass());

    private static final String USER_NOT_FOUND_MSG = 'Invalid link or password already reset'
    private static final String DEFAULT_FAILURE_MESSAGE = 'Could not load page for reset password'
    private static final String APP_USER = 'appUser'
    private static final String USER_INFO_MAP = 'userInfoMap'
    private static final String TIME_EXPIRED_MSG = 'Time to reset password has expired'
    private static final String PASSWORD_RESET_LINK = "passwordResetLink"
    private static final String USERNAME = "username"

    AppUserService appUserService
    @Autowired
    CompanyCacheUtility companyCacheUtility

    /**
     * Get parameters from UI and check pre condition
     * 1. check required parameter
     * 2. check if user exists or not
     * 3. check time validation for reset password
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
            if (!parameterMap.link) {
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
            // check time validation for reset password
            Date currentDate = new Date()
            if (currentDate.compareTo(user.passwordResetValidity) > 0) {
                result.put(Tools.MESSAGE, TIME_EXPIRED_MSG)
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
     * Build map with user info for show in UI
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            AppUser user = (AppUser) preResult.get(APP_USER)
            Map userInfoMap = buildUserInfoMap(user)    // build map with user info for show in UI
            result.put(USER_INFO_MAP, userInfoMap)
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
     * @return -a map containing isError = true, relevant error message and user information
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(USER_INFO_MAP, buildUserInfoMap(null))
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
     * Build map with user information
     * @param user -object of AppUser
     * @return -a map containing user information
     */
    private Map buildUserInfoMap(AppUser user) {
        Map userInfoMap = new LinkedHashMap()
        userInfoMap.put(PASSWORD_RESET_LINK, user ? user.passwordResetLink : Tools.EMPTY_SPACE)
        userInfoMap.put(USERNAME, user ? user.username : Tools.EMPTY_SPACE)
        return userInfoMap
    }
}
