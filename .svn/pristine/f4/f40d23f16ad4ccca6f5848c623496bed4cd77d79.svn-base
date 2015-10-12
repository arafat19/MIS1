package com.athena.mis.exchangehouse.config

import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.SysConfigurationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('exhSysConfigurationCacheUtility')
class ExhSysConfigurationCacheUtility implements ExhSysConfigurationIntf {

    @Autowired
    SysConfigurationService sysConfigurationService

    private List<SysConfiguration> lstAll = []

    public void init() {
        List list = sysConfigurationService.listByPlugin(PluginConnector.EXCHANGE_HOUSE_ID)
        if (list) {
            lstAll = list;
        }
    }

    public SysConfiguration readByKeyAndCompanyId(String key, long companyId) {
        for (int i = 0; i < lstAll.size(); i++) {
            if ((lstAll[i].key.equals(key)) && (lstAll[i].companyId == companyId)) {
                return lstAll[i]
            }
        }
        return null
    }
}
