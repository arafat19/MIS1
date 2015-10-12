package com.athena.mis.budget.service

import com.athena.mis.BaseService
import com.athena.mis.budget.model.BudgetDetailsItemModel
import com.athena.mis.budget.model.BudgetProjectItemModel
import com.athena.mis.budget.model.BudgetProjectModel
import com.athena.mis.budget.model.BudgetStatusModel

class BudgetModelService extends BaseService {

    public void createDefaultSchema() {
        executeSql(BudgetDetailsItemModel.SQL_BUDG_BUDGET_DETAILS_ITEM_MODEL)
        executeSql(BudgetProjectModel.SQL_BUDG_BUDGET_PROJECT_MODEL)
        executeSql(BudgetProjectItemModel.SQL_BUDGET_PROJECT_ITEM_MODEL)
        executeSql(BudgetStatusModel.SQL_BUDG_BUDGET_STATUS_MODEL)
    }
}
