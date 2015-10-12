package com.athena.mis.sarb.config

import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.SysConfigurationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("sarbSysConfigurationCacheUtility")
class SarbSysConfigurationCacheUtility implements SarbSysConfigurationIntf {

	@Autowired
	SysConfigurationService sysConfigurationService

	private List<SysConfiguration> lstAll = []

	public void init() {
		List list = sysConfigurationService.listByPlugin(PluginConnector.SARB_ID)
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

    public boolean isProductionMode(long companyId) {
        boolean result = false
        for (int i = 0; i < lstAll.size(); i++) {
            if ((lstAll[i].key.equals(SARB_IS_PRODUCTION_MODE)) && (lstAll[i].companyId == companyId)) {
                SysConfiguration isProdObj = (SysConfiguration) lstAll[i]
                int value = Integer.parseInt(isProdObj.value)
                if(value == 1) {
                    result = true
                }
                break
            }
        }
        return result
    }
}
