package com.athena.mis.integration.sarb

import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.*
import com.athena.mis.sarb.service.SarbProvinceService
import grails.transaction.Transactional
import org.hibernate.SessionFactory

class SarbDefaultDataBootStrapService {

    SessionFactory sessionFactory
    SysConfigurationService sysConfigurationService
    RequestMapService requestMapService
    RoleFeatureMappingService roleFeatureMappingService
    SarbProvinceService sarbProvinceService
    SystemEntityTypeService systemEntityTypeService
    SystemEntityService systemEntityService

	@Transactional
	public void init() {
        Company defaultCompany = Company.list(readOnly: true)[0]    // read the only object
        long companyId = defaultCompany.id

		sysConfigurationService.createDefaultSarbSysConfig(companyId)
        requestMapService.createRequestMapForSARB()
        roleFeatureMappingService.createRoleFeatureMapForSARBPlugin()
        flushSession()
        requestMapService.appendRoleInRequestMap(PluginConnector.SARB_ID)
        sarbProvinceService.createDefaultDataForSarbProvince(companyId)
        systemEntityTypeService.createDefaultDataForSarb()
        systemEntityService.createDefaultDataForSarbTaskReviseStatus(companyId)
	}
    /**
     * Flush the current session when necessary to apply GORM
     */
    private void flushSession() {
        def hibSession = sessionFactory.getCurrentSession()
        if (hibSession) {
            hibSession.flush()
        }
    }
}
