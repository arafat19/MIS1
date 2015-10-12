package com.athena.mis.arms.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.arms.entity.RmsExchangeHouse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("userRmsExchangeHouseCacheUtility")
class UserRmsExchangeHouseCacheUtility extends ExtendedCacheUtility {

    @Autowired
    AppUserEntityService appUserEntityService
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility

    public final String SORT_ON_ID = "id";

    public void init() {
        List list = appUserEntityService.listByType(AppUserEntityTypeCacheUtility.EXCHANGE_HOUSE);
        setList(list)
    }

    public List<RmsExchangeHouse> listUserExchangeHouse(long userId) {
        List<AppUserEntity> lstUserExchangeHouse = (List<AppUserEntity>) list()
        List<RmsExchangeHouse> lstExchangeHouse = []
        for (int i = 0; i < lstUserExchangeHouse.size(); i++) {
            if (lstUserExchangeHouse[i].appUserId == userId) {
                lstExchangeHouse << rmsExchangeHouseCacheUtility.read(lstUserExchangeHouse[i].entityId)
            }
        }
        return lstExchangeHouse
    }

    public int countByExchangeHouseId(long exchangeHouseId) {
        List<AppUserEntity> lstUserExchangeHouse = (List<AppUserEntity>) list()
        int count = 0
        for (int i = 0; i < lstUserExchangeHouse.size(); i++) {
            if (lstUserExchangeHouse[i].entityId == exchangeHouseId) {
                count++
            }
        }
        return count
    }
}
