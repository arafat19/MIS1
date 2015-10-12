package com.athena.mis.integration.budget

import com.athena.mis.budget.utility.BudgetScopeCacheUtility
import com.athena.mis.budget.utility.BudgetTaskStatusCacheUtility
import com.athena.mis.budget.utility.ProjectBudgetScopeCacheUtility
import org.springframework.beans.factory.annotation.Autowired

class BudgetBootStrapService {
    @Autowired
    BudgetTaskStatusCacheUtility budgetTaskStatusCacheUtility
    @Autowired
    BudgetScopeCacheUtility budgetScopeCacheUtility
    @Autowired
    ProjectBudgetScopeCacheUtility projectBudgetScopeCacheUtility

    public void init() {
        initAllUtility()
    }

    private void initAllUtility() {
        budgetTaskStatusCacheUtility.init()
        budgetScopeCacheUtility.init()
        projectBudgetScopeCacheUtility.init()
    }
}
