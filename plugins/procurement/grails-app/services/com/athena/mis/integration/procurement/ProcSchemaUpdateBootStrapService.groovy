package com.athena.mis.integration.procurement

import com.athena.mis.procurement.service.ProcurementModelService
import org.springframework.transaction.annotation.Transactional

class ProcSchemaUpdateBootStrapService {

    ProcurementModelService procurementModelService

    @Transactional
    public void init() {
        procurementModelService.createDefaultSchema()
    }
}
