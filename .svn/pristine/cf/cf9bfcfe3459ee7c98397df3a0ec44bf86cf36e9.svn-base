package com.athena.mis.integration.budget

import com.athena.mis.budget.service.BudgetModelService
import org.springframework.transaction.annotation.Transactional

class BudgSchemaUpdateBootStrapService {

    BudgetModelService budgetModelService

    @Transactional
    public void init() {
        budgetModelService.createDefaultSchema()
    }
}
