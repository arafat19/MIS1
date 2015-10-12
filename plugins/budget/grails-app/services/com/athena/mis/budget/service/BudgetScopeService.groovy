package com.athena.mis.budget.service

import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgBudgetScope
import com.athena.mis.budget.utility.BudgetScopeCacheUtility
import org.springframework.beans.factory.annotation.Autowired

class BudgetScopeService extends BaseService {

    @Autowired
    BudgetScopeCacheUtility budgetScopeCacheUtility

    static transactional = false

    /**
     * Method to get list of budget scope
     * @return - budgetScope list
     */
    public List list() {
        return BudgBudgetScope.list(sort: budgetScopeCacheUtility.SORT_BY_NAME,
                order: budgetScopeCacheUtility.SORT_ORDER_ASCENDING, readOnly: true)
    }

    /**
     * Method to create Budget Scope
     * @param budgetScope - object of budget scope
     * @return - newly saved budget scope
     */
    public BudgBudgetScope create(BudgBudgetScope budgetScope) {
        BudgBudgetScope savedBudgetScope = budgetScope.save();
        return savedBudgetScope;
    }

    private static final String BUDG_SCOPE_UPDATE_QUERY = """
        UPDATE budg_budget_scope SET
            version=:newVersion,
            name=:name
        WHERE
            id=:id AND
            version=:version
        """

    /**
     * Method to update budget scope object
     * @param budgetScope - object of budget scope
     * @return - updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(BudgBudgetScope budgetScope) {
        Map queryParams = [
                newVersion: budgetScope.version + 1,
                name: budgetScope.name,
                id: budgetScope.id,
                version: budgetScope.version
        ]
        int updateCount = executeUpdateSql(BUDG_SCOPE_UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Error occurred at budgetScopeService.update")
        }
        return updateCount
    }

    private static final String BUDG_SCOPE_DELETE_QUERY = """
        DELETE FROM budg_budget_scope
                 WHERE id=:id
        """

    /**
     * Method to delete budget scope object
     * @param id -BudgetScope.id
     * @return -if deleteCount <= 0 then throw exception to rollback transaction; otherwise return true
     */
    public Boolean delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(BUDG_SCOPE_DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("Error occurred at budgetScopeService.delete")
        }
        return Boolean.TRUE
    }

    /**
     * Create Default data when it is called by BudgDefaultDataBootStrapService
     */
    public void createDefaultData(long companyId) {
        new BudgBudgetScope(name: 'Concreting', companyId: companyId).save()
        new BudgBudgetScope(name: 'Road-Works', companyId: companyId).save()
        new BudgBudgetScope(name: 'Miscellaneous Works', companyId: companyId).save()
        new BudgBudgetScope(name: 'Equipment Budget', companyId: companyId).save()
    }
}