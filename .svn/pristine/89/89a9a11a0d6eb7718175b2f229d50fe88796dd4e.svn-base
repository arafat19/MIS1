package com.athena.mis.integration.qsmeasurement

import com.athena.mis.qs.service.QsModelService
import org.springframework.transaction.annotation.Transactional

class QsSchemaUpdateBootStrapService {

    QsModelService qsModelService

    @Transactional
    public void init() {
        qsModelService.createDefaultSchema()
    }
}
