package com.athena.mis.document.config

import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.SysConfigurationService
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by Asif on 4/30/14.
 */
@Component("docSysConfigurationCacheUtility")
class DocSysConfigurationCacheUtility implements DocSysConfigurationIntf {

    @Autowired
    SysConfigurationService sysConfigurationService


    private List<SysConfiguration> lstAll = []

    public void init() {
        List list = sysConfigurationService.listByPlugin(PluginConnector.DOCUMENT_ID)
        if (list) {
            lstAll = list;
        }
    }

    /**
     * Read System Configuration by key and company Id
     * @param key - Key of the System configuration
     * @param companyId - Company Id
     * @return Object of system configuration or can be null
     * */
    public SysConfiguration readByKeyAndCompanyId(String key, long companyId) {
        for (int i = 0; i < lstAll.size(); i++) {
            if ((lstAll[i].key.equals(key)) && (lstAll[i].companyId == companyId)) {
                return lstAll[i]
            }
        }
        return null
    }

}
