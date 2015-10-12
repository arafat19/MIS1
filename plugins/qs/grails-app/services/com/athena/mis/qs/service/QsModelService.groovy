package com.athena.mis.qs.service

import com.athena.mis.BaseService
import com.athena.mis.qs.model.QsMeasurementModel
import com.athena.mis.qs.model.QsStatusModel

class QsModelService extends BaseService {

    public void createDefaultSchema() {
        executeSql(QsMeasurementModel.SQL_QS_MEASUREMENT_MODEL)
        executeSql(QsStatusModel.SQL_QS_STATUS_MODEL)
    }
}
