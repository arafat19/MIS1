package com.athena.mis.application.config

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.SysConfigurationService
import com.athena.mis.application.utility.AppSessionUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('appSysConfigurationCacheUtility')
class AppSysConfigurationCacheUtility extends ExtendedCacheUtility {

    public static final String DEFAULT_PASSWORD_EXPIRE_DURATION = "mis.application.defaultPasswordExpireDuration"

    @Autowired
    SysConfigurationService sysConfigurationService
    @Autowired
    AppSessionUtil appSessionUtil

    public void init() {
        List list = sysConfigurationService.listByPlugin(PluginConnector.APPLICATION_ID)
        super.setList(list)
    }

    public SysConfiguration readByKeyAndCompanyId(String key, long companyId) {
        List<SysConfiguration> lstMain = list(companyId)
        for (int i = 0; i < lstMain.size(); i++) {
            if (lstMain[i].key.equals(key)) {
                return lstMain[i]
            }
        }
        return null
    }

    public SysConfiguration readByKeyAndCompanyId(String key) {
        List<SysConfiguration> lstMain = list()
        for (int i = 0; i < lstMain.size(); i++) {
            if (lstMain[i].key.equals(key)) {
                return lstMain[i]
            }
        }
        return null
    }
}
