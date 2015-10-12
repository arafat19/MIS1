package com.athena.mis.budget.service

import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.utility.DateUtility

class BudgetService extends BaseService {

    static transactional = false

    /**
     * Method to read budget object by id
     * @param id - budget id
     * @return - budget object
     */
    public BudgBudget read(long id) {
        BudgBudget budget = BudgBudget.read(id)
        return budget
    }

    private static final String BUDG_CREATE_QUERY = """
            INSERT INTO budg_budget(id, version, project_id, budget_item, details, budget_scope_id, item_count, content_count,
            contract_rate, created_by, billable, updated_by, created_on, status, budget_quantity,unit_id, company_id,
            start_date, end_date, is_production, task_count)
            VALUES (NEXTVAL('budg_budget_id_seq'), :version, :projectId, :budgetItem, :details,
            :budgetScopeId, :itemCount, :contentCount, :contractRate, :createdBy, :billable, :updatedBy, :createdOn,
            :status, :budgetQuantity ,:unitId, :companyId, :startDate, :endDate, :isProduction, :taskCount);
    """

    /**
     * Method to create budget object
     * @param budget - budget object
     * @return - budget object
     */
    public BudgBudget create(BudgBudget budget) {
        Map queryParams = [
                version: budget.version,
                projectId: budget.projectId,
                budgetItem: budget.budgetItem,
                details: budget.details,
                budgetScopeId: budget.budgetScopeId,
                itemCount: budget.itemCount,
                contentCount: budget.contentCount,
                contractRate: budget.contractRate,
                createdBy: budget.createdBy,
                createdOn: DateUtility.getSqlDateWithSeconds(budget.createdOn),
                billable: budget.billable,
                updatedBy: budget.updatedBy,
                status: budget.status,
                budgetQuantity: budget.budgetQuantity,
                unitId: budget.unitId,
                companyId: budget.companyId,
                startDate: DateUtility.getSqlDate(budget.startDate),
                endDate: DateUtility.getSqlDate(budget.endDate),
                isProduction: budget.isProduction,
                taskCount: budget.taskCount
        ]
        List result = executeInsertSql(BUDG_CREATE_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert budget information')
        }
        int budgetId = (int) result[0][0]
        budget.id = budgetId
        return budget
    }

    private static final String BUDG_UPDATE_QUERY = """
                      UPDATE budg_budget SET
                          budget_item=:budgetItem,
                          billable=:billable,
                          details=:details,
                          budget_quantity =:budgetQuantity,
                          unit_id=:unitId,
                          contract_rate=:contractRate,
                          budget_scope_id =:budgetScopeId,
                          updated_on =:updatedOn,
                          updated_by=:updatedBy,
                          version=:newVersion,
                          start_date=:startDate,
                          end_date=:endDate
                      WHERE
                          id=:id AND
                          version=:version
    """

    /**
     * Method to update budget object
     * @param budget - budget object
     * @return - budget object
     */
    public BudgBudget update(BudgBudget budget) {
        Map queryParams = [
                id: budget.id,
                version: budget.version,
                newVersion: budget.version + 1,
                budgetItem: budget.budgetItem,
                budgetScopeId: budget.budgetScopeId,
                details: budget.details,
                budgetQuantity: budget.budgetQuantity,
                unitId: budget.unitId,
                contractRate: budget.contractRate,
                billable: budget.billable,
                updatedBy: budget.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(budget.updatedOn),
                startDate: DateUtility.getSqlDate(budget.startDate),
                endDate: DateUtility.getSqlDate(budget.endDate)
        ]
        int updateCount = executeUpdateSql(BUDG_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred at budgetService.update')
        }
        budget.version = budget.version + 1
        return budget
    }

    private static final String DELETE_QUERY = """
        DELETE FROM budg_budget
        WHERE
        id=:id
    """

    /**
     * Method to delete budget object from db
     * @param id - budget id
     * @return- if deleteCount <= 0 then throw exception to rollback transaction; otherwise return true
     */
    public Boolean delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("Fail to delete budget")
        }
        return Boolean.TRUE;
    }
}
