package com.athena.mis.integration.qsmeasurement

import com.athena.mis.PluginConnector
import com.athena.mis.application.service.RequestMapService
import com.athena.mis.application.service.RoleFeatureMappingService
import org.hibernate.SessionFactory

class QsDefaultDataBootStrapService {
    SessionFactory sessionFactory
    RequestMapService requestMapService
    RoleFeatureMappingService roleFeatureMappingService

    public void init() {
        roleFeatureMappingService.createRoleFeatureMapForQSPlugin()
        flushSession()
        requestMapService.createRequestMapForQs()
        flushSession()
        requestMapService.appendRoleInRequestMap(PluginConnector.QS_ID)
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
