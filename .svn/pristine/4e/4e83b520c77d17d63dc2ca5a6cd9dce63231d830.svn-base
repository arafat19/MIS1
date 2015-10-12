package com.athena.mis.integration.accounting

import com.athena.mis.accounting.service.AccountingModelService
import org.springframework.transaction.annotation.Transactional

class AccSchemaUpdateBootStrapService {

    AccountingModelService accountingModelService

    @Transactional
    public void init () {
        accountingModelService.createDefaultSchema()
    }
}
