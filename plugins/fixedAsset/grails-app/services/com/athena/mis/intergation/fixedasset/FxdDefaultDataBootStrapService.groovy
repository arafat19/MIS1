package com.athena.mis.intergation.fixedasset

import com.athena.mis.PluginConnector
import com.athena.mis.application.service.RequestMapService
import com.athena.mis.application.service.RoleFeatureMappingService
import com.athena.mis.fixedasset.service.FxdMaintenanceTypeService
import org.hibernate.SessionFactory

class FxdDefaultDataBootStrapService {
    SessionFactory sessionFactory
    RequestMapService requestMapService
    RoleFeatureMappingService roleFeatureMappingService
    FxdMaintenanceTypeService fxdMaintenanceTypeService

    public void init() {
        roleFeatureMappingService.createRoleFeatureMapForFixedAssetPlugin()
        flushSession()
        requestMapService.createRequestMapForFixedAsset()
        flushSession()
        requestMapService.appendRoleInRequestMap(PluginConnector.FIXED_ASSET_ID)
        fxdMaintenanceTypeService.createDefaultData()
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
