package com.athena.mis.arms.utility

import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

@Component("rmsSessionUtil")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
class RmsSessionUtil implements Serializable {

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    AppUserEntityService appUserEntityService
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    UserRmsExchangeHouseCacheUtility userRmsExchangeHouseCacheUtility


    List<RmsExchangeHouse> lstExchangeHouse = []         // List of RmsExchangeHouse that is mapped with loggedIn user
    List<Long> lstExchangeHouseIds = []                  // List of RmsExchangeHouse Ids that is mapped with loggedIn user

    private RmsExchangeHouse userExchangeHouse = null   // Object of RmsExchangeHouse that is mapped with loggedIn user
    private Long userExchangeHouseId = null             // id of RmsExchangeHouse that is mapped with loggedIn user

    public void init() {
        AppUser user = appSessionUtil.getAppUser()
        lstExchangeHouse = userRmsExchangeHouseCacheUtility.listUserExchangeHouse(user.id)
        lstExchangeHouseIds = Tools.getIds(lstExchangeHouse)
    }

    //get list of ExchangeHouse mapped with user
    public List<RmsExchangeHouse> getUserExchangeHouses() {
        return lstExchangeHouse
    }

    // get list of ExchangeHouse ids mapped with user
    public List<Long> getUserExchangeHouseIds() {
        return lstExchangeHouseIds
    }

    // get object of ExchangeHouse mapped with user
    public RmsExchangeHouse getUserExchangeHouse() {
        if (userExchangeHouse) return userExchangeHouse
        userExchangeHouse = null
        if(lstExchangeHouse.size() > 0) {
            userExchangeHouse = lstExchangeHouse[0]
        }
        return userExchangeHouse
    }

    // Retrieve the ExchangeHouse ID which is associated with user
    public long getUserExchangeHouseId() {
        if (userExchangeHouseId) return userExchangeHouseId.longValue()
        userExchangeHouseId = new Long(0L)
        if (lstExchangeHouseIds.size() > 0) {
            userExchangeHouseId = lstExchangeHouseIds[0]
        }
        return userExchangeHouseId.longValue()
    }
}
