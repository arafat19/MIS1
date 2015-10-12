package com.athena.mis.fixedasset.service

import com.athena.mis.BaseService
import com.athena.mis.fixedasset.model.FixedAssetDetailsModel
import com.athena.mis.fixedasset.model.FixedAssetTraceModel
import com.athena.mis.fixedasset.model.FxdMaintenanceModel

class FixedAssetModelService extends BaseService {

    public void createDefaultSchema() {
        executeSql(FixedAssetDetailsModel.SQL_FIXED_ASSET_DETAILS_MODEL)
        executeSql(FixedAssetTraceModel.SQL_FIXED_ASSET_TRACE_MODEL)
        executeSql(FxdMaintenanceModel.SQL_FXD_MAINTENANCE_MODEL)
    }
}
