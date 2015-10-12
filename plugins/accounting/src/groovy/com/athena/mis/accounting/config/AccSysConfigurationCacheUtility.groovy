package com.athena.mis.accounting.config

import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.SysConfigurationService
import com.athena.mis.integration.accounting.AccountingPluginConnector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('accSysConfigurationCacheUtility')
class AccSysConfigurationCacheUtility implements AccSysConfigurationIntf {

    @Autowired
    SysConfigurationService sysConfigurationService
    @Autowired
    AccountingPluginConnector accountingImplService
    @Autowired
    AccSessionUtil accSessionUtil

    private List<SysConfiguration> lstAll = []


    public void init() {
        List list = sysConfigurationService.listByPlugin(accountingImplService.id)
        if (list) {
            lstAll = list;
        }
    }

    public SysConfiguration readByKeyAndCompanyId(String key) {
        long companyId = accSessionUtil.appSessionUtil.getAppUser().companyId
        for (int i = 0; i < lstAll.size(); i++) {
            if ((lstAll[i].key.equals(key)) && (lstAll[i].companyId == companyId)) {
                return lstAll[i]
            }
        }
        return null
    }
}
