package com.athena.mis.exchangehouse.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhAgent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('exhUserAgentCacheUtility')
class ExhUserAgentCacheUtility extends ExtendedCacheUtility {

    @Autowired
    AppUserEntityService appUserEntityService
    @Autowired
    ExhAgentCacheUtility exhAgentCacheUtility

    public void init() {
        List list = appUserEntityService.listByType(AppUserEntityTypeCacheUtility.AGENT);
        setList(list)
    }

    public List<ExhAgent> listUserAgents(long userId) {
        List<AppUserEntity> lstUserAgent = (List<AppUserEntity>) super.list()
        if(!lstUserAgent) return []
        List<ExhAgent> lstAgents = []
        for (int i = 0; i < lstUserAgent.size(); i++) {
            if (lstUserAgent[i].appUserId == userId) {
                lstAgents << exhAgentCacheUtility.read(lstUserAgent[i].entityId)
            }
        }
        return lstAgents
    }
}
