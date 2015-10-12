package com.athena.mis.budget.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.budget.entity.BudgBudgetScope
import com.athena.mis.budget.service.BudgetScopeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("budgetScopeCacheUtility")
class BudgetScopeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    BudgetScopeService budgetScopeService

    static final String SORT_BY_NAME = "name";

    public void init() {
        List list = budgetScopeService.list();
        super.setList(list)
    }

    public List<BudgBudgetScope> listByIds(List<Long> ids) {
        List<BudgBudgetScope> returnBudgetScopeList = []
        BudgBudgetScope budgetScope
        for (int i = 0; i < ids.size(); i++) {
            budgetScope = (BudgBudgetScope) super.read(ids[i])
            returnBudgetScopeList << budgetScope
        }
        return returnBudgetScopeList
    }
}
