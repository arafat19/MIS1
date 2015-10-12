package com.athena.mis.budget.service

import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgProjectBudgetScope
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.budget.utility.ProjectBudgetScopeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.springframework.beans.factory.annotation.Autowired

class ProjectBudgetScopeService extends BaseService {

    static transactional = false

    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    ProjectBudgetScopeCacheUtility projectBudgetScopeCacheUtility

    public List list() {
        return BudgProjectBudgetScope.list(sort: projectBudgetScopeCacheUtility.SORT_ON_NAME, order: projectBudgetScopeCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    private static final String PROJECT_BUDG_TYPE_UPDATE_QUERY = """
        SELECT budget_scope_id  AS id
        FROM budg_project_budget_scope
        WHERE project_id=:projectId
           AND company_id = :companyId
    """

    public boolean update(List<Long> lstAssignedBudgetScopeIds, long projectId) {
        long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
        Map queryParams = [
                projectId: projectId,
                companyId: companyId
        ]
        // First get the current assigned role
        List<GroovyRowResult> result = executeSelectSql(PROJECT_BUDG_TYPE_UPDATE_QUERY, queryParams)

        List<Long> lstOldFeatures = []
        for (int i = 0; i < result.size(); i++) {
            lstOldFeatures << result[i].id
        }

        // Find the common elements
        List lstCommonFeatures = lstOldFeatures.intersect(lstAssignedBudgetScopeIds)
        List<Long> lstToRemove = (List<Long>) lstOldFeatures.clone()
        lstToRemove.removeAll(lstCommonFeatures)
        List<Long> lstToAdd = (List<Long>) lstAssignedBudgetScopeIds.clone()
        lstToAdd.removeAll(lstCommonFeatures)

        // If something to remove then execute sql
        if (lstToRemove.size() > 0) {
            if (countBudget(lstToRemove, projectId) > 0) {
                return false;
            }

            for (int i = 0; i < lstToRemove.size(); i++) {
                String queryToDelete = """
                DELETE FROM budg_project_budget_scope
                WHERE budget_scope_id = ${lstToRemove[i]} AND project_id = ${projectId}
                      AND company_id =  ${companyId}
                """
                int deleteCount = executeUpdateSql(queryToDelete)

                if (deleteCount > 0) {
                    BudgProjectBudgetScope projectBudgetScope = (BudgProjectBudgetScope) projectBudgetScopeCacheUtility.ProjectBudgetScopeByProjectAndBudgetScope(lstToRemove[i], projectId)
                    if (projectBudgetScope) {
                        projectBudgetScopeCacheUtility.delete(projectBudgetScope.id)
                    }
                }
            }
        }

        // If something to add then execute sql
        if (lstToAdd.size() > 0) {
            for (int i = 0; i < lstToAdd.size(); i++) {
                String queryToAdd = """
                INSERT INTO budg_project_budget_scope(id, budget_scope_id, project_id, company_id)
                VALUES (NEXTVAL('budg_project_budget_scope_id_seq'),${lstToAdd[i]} , ${projectId}, ${companyId});
                """

                List ret = executeInsertSql(queryToAdd)
                Long Id = (Long) ret[0][0]
                if (Id > 0) {
                    BudgProjectBudgetScope projectBudgetScope = new BudgProjectBudgetScope(projectId: projectId, budgetScopeId: lstToAdd[i], companyId: companyId)
                    projectBudgetScope.id = Id
                    projectBudgetScopeCacheUtility.add(projectBudgetScope, projectBudgetScopeCacheUtility.SORT_ON_NAME, projectBudgetScopeCacheUtility.SORT_ORDER_ASCENDING)
                }
            }
        }
        return true
    }

    //@todo:model use dynamic finder
    private int countBudget(List<Long> budgetScopeIdList, long projectId) {
        String budgetScopeIds = Tools.buildCommaSeparatedStringOfIds(budgetScopeIdList)
        //@todo:model use dynamic finder
        String queryCount = """
            SELECT COUNT(id) AS count
            FROM budg_budget
            WHERE budget_scope_id IN (${budgetScopeIds}) AND project_id=:projectId
                   AND company_id = :companyId
        """
        Map queryParams = [
                projectId: projectId,
                companyId: budgSessionUtil.appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> results = executeSelectSql(queryCount, queryParams);
        int count = results[0].count;
        return count;
    }
}

