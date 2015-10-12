package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Company
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

import javax.servlet.http.HttpServletRequest

/** Determine if New User Registration link should visible
 * */
class GetSysConfigUserRegistrationActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired
    CompanyCacheUtility companyCacheUtility

    /**
     * Do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Returns value if system configuration exists
     * 1. check if exh module installed
     * 2. check through interface, if New User Registration is enabled in Exh plugin
     * @param parameters - N/A
     * @param obj - N/A
     * @return - true or false
     */
    public Object execute(Object parameters, Object obj) {
        try {

            Boolean isRegistrationEnabled = Boolean.FALSE
            if (!exchangeHouseImplService) {
                return isRegistrationEnabled
            }
            HttpServletRequest request = (HttpServletRequest) obj
            long companyId = getCompanyId(request)
            isRegistrationEnabled = exchangeHouseImplService.isNewUserRegistrationEnabled(companyId)
            return isRegistrationEnabled
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
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
     * Do nothing for build failure result
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     *
     * @param request - HttpServletRequest
     * @return - companyId of given url
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

}
