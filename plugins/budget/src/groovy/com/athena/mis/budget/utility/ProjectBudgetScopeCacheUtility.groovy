package com.athena.mis.budget.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.budget.entity.BudgProjectBudgetScope
import com.athena.mis.budget.service.ProjectBudgetScopeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("projectBudgetScopeCacheUtility")
class ProjectBudgetScopeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    ProjectBudgetScopeService projectBudgetScopeService

    static final String SORT_ON_NAME = "budgetScopeId";

    public void init() {
        List list = projectBudgetScopeService.list();
        super.setList(list)
    }

    public BudgProjectBudgetScope ProjectBudgetScopeByProjectAndBudgetScope(long budgetScopeId, long projectId) {
        List<BudgProjectBudgetScope> projectBudgetScopeList = (List) super.list()

        for (int i = 0; i < projectBudgetScopeList.size(); i++) {
            if (projectBudgetScopeList[i].budgetScopeId == budgetScopeId && projectBudgetScopeList[i].projectId == projectId) {
                return (BudgProjectBudgetScope) projectBudgetScopeList[i]
            }
        }
        return null
    }
}