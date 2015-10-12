package com.athena.mis.inventory.config

import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.application.service.SysConfigurationService
import com.athena.mis.integration.inventory.InventoryPluginConnector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('invSysConfigurationCacheUtility')
class InvSysConfigurationCacheUtility implements InvSysConfigurationIntf {

    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    SysConfigurationService sysConfigurationService
    @Autowired
    InventoryPluginConnector inventoryImplService

    List<SysConfiguration> lstAll = []

    public void init() {
        List list = sysConfigurationService.listByPlugin(inventoryImplService.id)
        if (list) {
            lstAll = list;
        }
    }

    public SysConfiguration readByKeyAndCompanyId(String key) {
        long companyId = invSessionUtil.appSessionUtil.getAppUser().companyId
        for (int i = 0; i < lstAll.size(); i++) {
            if ((lstAll[i].key.equals(key)) && (lstAll[i].companyId == companyId)) {
                return lstAll[i]
            }
        }
        return null
    }
}
