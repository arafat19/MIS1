package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

/**
 *  Show login UI panel after customer activation
 *  For details go through Use-Case doc named 'ExhActivateCustomerUserActionService'
 */
class ExhActivateCustomerUserActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static String APP_USER_ACTIVATION_SUCCESS_MESSAGE = "Your account has been activated. Please sign in."
    private static String APP_USER_ACTIVATION_FAILURE_MESSAGE = "Your account could not be activated. Try again"
    private static String APP_USER_NOT_FOUND = "User not found"
    private static String APP_USER_ALREADY_ACTIVATED = "Your account already activated"
    private static String APP_USER_INVALID_ACTIVATION_REQUEST = "Invalid activation request"
    private static String APP_USER = "appUser"

    AppUserService appUserService

    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility

    /**
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)                // set default
            String activationLink = params.link
            HttpServletRequest request = (HttpServletRequest) obj
            if (!activationLink || activationLink.isEmpty()) {     // check required parameter
                result.put(Tools.MESSAGE, APP_USER_INVALID_ACTIVATION_REQUEST)
                return result
            }

            long companyId = getCompanyId(request)
            AppUser appUser = appUserService.findByActivationLinkAndCompanyId(activationLink, companyId)
            // get activation link from cache
            if (!appUser) {               // check appUser
                result.put(Tools.MESSAGE, APP_USER_NOT_FOUND)
                return result
            }

            if (appUser.isActivatedByMail) {        // check already activated
                result.put(Tools.MESSAGE, APP_USER_ALREADY_ACTIVATED)
                return result
            }
            result.put(APP_USER, appUser)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APP_USER_ACTIVATION_FAILURE_MESSAGE)
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
        Company company = companyCacheUtility.readByWebUrl(fullUrl)
        if (company) return company.id
        // if company not found try to retrieve url without www
        fullUrl = Tools.getFullUrl(request, true)
        company = companyCacheUtility.readByWebUrl(fullUrl)
        return company.id
    }

    /**
     * Update appUser in DB and appUser Cache Utility
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for executePostCondition
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj      // cast map returned from executePreCondition method
            AppUser appUser = (AppUser) preResult.get(APP_USER)
            appUser.enabled = true            // appUser activated/enabled
            appUser.isActivatedByMail = true    // set enabled flag
            appUserService.update(appUser)            // update appUser in DB
            // update appUser in Cache and keep the data sorted
            appUserCacheUtility.update(appUser, appUserCacheUtility.SORT_ON_NAME, appUserCacheUtility.SORT_ORDER_ASCENDING)
            result.put(APP_USER, appUser)
            result.put(Tools.MESSAGE, APP_USER_ACTIVATION_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(APP_USER_ACTIVATION_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APP_USER_ACTIVATION_FAILURE_MESSAGE)
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
     * do nothing for buildSuccessResultForUI operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * do nothing for buildFailureResultForUI operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}
