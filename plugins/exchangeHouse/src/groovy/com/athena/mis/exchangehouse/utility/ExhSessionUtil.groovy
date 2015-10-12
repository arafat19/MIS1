package com.athena.mis.exchangehouse.utility

import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.exchangehouse.entity.ExhAgent
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

@Component("exhSessionUtil")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
class ExhSessionUtil implements Serializable {

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ExhUserAgentCacheUtility exhUserAgentCacheUtility
    @Autowired
    ExhUserCustomerCacheUtility exhUserCustomerCacheUtility

    private ExhAgent agent = null
    private ExhCustomer customer = null

    private List<ExhCustomer> lstUserCustomer = []      // list of Object of ExhCustomer that is mapped with loggedIn user
    private List<Long> lstUserCustomerIds = []          // list of id of ExhCustomer that is mapped with loggedIn user
    private ExhCustomer exhCustomer = null              // Object of ExhCustomer that is mapped with loggedIn user
    private Long exhCustomerId = null                   // id of ExhCustomer that is mapped with loggedIn user

    private List<ExhAgent> lstUserAgent = []            // list of Object of ExhAgent that is mapped with loggedIn user
    private List<Long> lstUserAgentIds = []             // list of id of ExhAgent that is mapped with loggedIn user
    private ExhAgent exhAgent = null                    // Object of ExhAgent that is mapped with loggedIn user
    private Long exhAgentId = null                      // id of ExhAgent that is mapped with loggedIn user

    public void init() {
        AppUser appUser = appSessionUtil.getAppUser()
        lstUserCustomer = exhUserCustomerCacheUtility.listUserCustomers(appUser.id)
        lstUserAgent = exhUserAgentCacheUtility.listUserAgents(appUser.id)
        lstUserCustomerIds = Tools.getIds(lstUserCustomer)
        lstUserAgentIds = Tools.getIds(lstUserAgent)
    }

    // get object of customer mapped with user
    public ExhCustomer getUserCustomer() {
        if(exhCustomer) return exhCustomer
        if(lstUserCustomer && lstUserCustomer.size() > 0) {
            exhCustomer = lstUserCustomer[0]
            return exhCustomer
        }
        return null
    }

    public long getUserCustomerId() {
        if (exhCustomerId) return exhCustomerId.longValue()
        exhCustomerId = new Long(0L)
        if (lstUserCustomerIds && lstUserCustomerIds.size() > 0) {
            exhCustomerId = lstUserCustomerIds[0]
        }
        return exhCustomerId.longValue()
    }

    // get object of agent mapped with user
    public ExhAgent getUserAgent() {
        if(exhAgent) return exhAgent
        if(lstUserAgent && lstUserAgent.size() > 0) {
            exhAgent = lstUserAgent[0]
            return exhAgent
        }
        return null
    }

    public long getUserAgentId() {
        if (exhAgentId) return exhAgentId.longValue()
        exhAgentId = new Long(0L)
        if (lstUserAgentIds && lstUserAgentIds.size() > 0) {
            exhAgentId = lstUserAgentIds[0]
        }
        return exhAgentId.longValue()
    }
}
