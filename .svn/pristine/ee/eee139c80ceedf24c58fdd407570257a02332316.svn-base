package com.athena.mis.integration.procurement

import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.*
import org.hibernate.SessionFactory
import org.springframework.transaction.annotation.Transactional

class ProcDefaultDataBootStrapService {
    SessionFactory sessionFactory
    SystemEntityService systemEntityService
    SystemEntityTypeService systemEntityTypeService
    AppMailService appMailService
    RequestMapService requestMapService
    RoleFeatureMappingService roleFeatureMappingService

    @Transactional
    public void init() {
        Company defaultCompany = Company.list(readOnly: true)[0]    // read the only object
        long companyId = defaultCompany.id

        systemEntityTypeService.createDefaultDataForProcurement()
        systemEntityService.createDefaultPaymentMethod(companyId)
        roleFeatureMappingService.createRoleFeatureMapForProcurementPlugin()
        flushSession()
        requestMapService.createRequestMapForProcurement()
        flushSession()
        requestMapService.appendRoleInRequestMap(PluginConnector.PROCUREMENT_ID)
        appMailService.createDefaultDataForProcurement(companyId)
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
