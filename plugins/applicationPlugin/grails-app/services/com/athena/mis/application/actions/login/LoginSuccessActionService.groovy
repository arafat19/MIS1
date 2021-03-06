package com.athena.mis.application.actions.login

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

//  check whether fit for logging or not
class LoginSuccessActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String FAILED_TO_LOGIN = "Failed to login. Try again"
    private static final String APP_USER = "appUser"
    private static final String APP_USER_MAP = "userInfoMap"
    private static final String REQUEST = "request"
    private static final String USER_HAS_NO_ROLE = "User has no role."
    private static final String USER_HAS_NO_AGENT = "User with role 'AGENT' is not associated with any agent."
    private static final String USER_IP_MIS_MATCH = "User IP address did not match."
    private static final String INVALID_ID_PASS = "Invalid ID or Password."
    private static final String IS_EXPIRED = "isExpired"

    AppUserService appUserService
    SpringSecurityService springSecurityService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(IS_EXPIRED, Boolean.FALSE)
            HttpServletRequest request = (HttpServletRequest) obj
            def userDetails = springSecurityService.principal
            AppUser appUser = appUserService.read(userDetails.id)
            // check url
            String msg = checkServerName(request, appUser)
            if (msg) {
                result.put(Tools.MESSAGE, msg)
                return result
            }
            // check IP address(if any)
            msg = checkIP(request, appUser)
            if (msg) {
                result.put(Tools.MESSAGE, msg)
                return result
            }
            // check password expire date
            Map userInfoMap = checkExpireDate(appUser)
            if (userInfoMap) {
                result.put(IS_EXPIRED, Boolean.TRUE)
                result.put(APP_USER_MAP, userInfoMap)
                return result
            }
            result.put(REQUEST, request)
            result.put(APP_USER, appUser)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_LOGIN)
            return result
        }
    }

    private String checkIP(HttpServletRequest request, AppUser appUser) {
        if (appUser.ipAddress && (appUser.ipAddress.length() > 0)) {
            String remoteIP = request.getRemoteAddr()
            if (!appUser.ipAddress.equals(remoteIP)) {
                return USER_IP_MIS_MATCH
            }
        }
        return null
    }

    private static final String OPENING_WWW = '//www.'
    private static final String DOUBLE_SLASH = '//'

    private String checkServerName(HttpServletRequest request, AppUser appUser) {
        Company company = (Company) companyCacheUtility.read(appUser.companyId)
        String requestUrl = Tools.getFullUrl(request, true)
        String companyUrl = company.webUrl.replace(OPENING_WWW, DOUBLE_SLASH)
        if (!requestUrl.equalsIgnoreCase(companyUrl)) {
            return INVALID_ID_PASS
        }
        return null
    }

    public Object executePostCondition(Object sessionObj, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj
            AppUser user = (AppUser) preResult.get(APP_USER)
            HttpServletRequest request = (HttpServletRequest) preResult.get(REQUEST)
            HttpSession session = (HttpSession) sessionObj
            println session.id
            if (appSessionUtil.appUser) {   // check if session already exists
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                return result
            }
            appSessionUtil.init(user, request, session)
            // check if user has any role
            if (appSessionUtil.getUserRoleIds().size() == 0) {
                result.put(Tools.MESSAGE, USER_HAS_NO_ROLE)
                return result
            }
            // check if user with ROLE_AGENT id mapped with any Agent
            //@todo: need to be implemented through exh interface after sessionUtil split
         /*   boolean hasAgentRole = appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_EXH_AGENT)
            if (hasAgentRole && (sessionUtility.exhAgent == 0L)) {
                result.put(Tools.MESSAGE, USER_HAS_NO_AGENT)
                return result
            }*/

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_LOGIN)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    private Map checkExpireDate(AppUser user) {
        Date currentDate = new Date()
        if ((!user.isDisablePasswordExpiration) && (currentDate > user.nextExpireDate)) {
            Map userInfoMap = [
                    userName: user.username,
                    userId: user.id,
                    expireDate: DateUtility.getDateFormatAsString(user.nextExpireDate)
            ]
            return userInfoMap
        }
        return null
    }
}
