package com.athena.mis.budget.service

import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgSprintBudget
import com.athena.mis.utility.DateUtility

class BudgSprintBudgetService extends BaseService {

    static transactional = false

    /**
     * Read object of BudgSprintBudget by id
     * @param id -id of object
     * @return -BudgSprintBudget object
     */
    public BudgSprintBudget read(long id) {
        BudgSprintBudget sprintBudget = BudgSprintBudget.read(id)
        return sprintBudget
    }

    private static final String SPRINT_BUDGET_CREATE_QUERY = """
            INSERT INTO budg_sprint_budget(id, version, budget_id, created_by, created_on, sprint_id,
                                           quantity,updated_by, updated_on)
            VALUES (NEXTVAL('budg_sprint_budget_id_seq'),:version,:budgetId,:createdBy, :createdOn,:sprintId,
                                          :quantity,:updatedBy,:updatedOn);
    """

    /**
     * Save BudgSprintBudget object in DB
     * @param sprintBudget -BudgSprintBudget object
     * @return -saved object
     */
    public BudgSprintBudget create(BudgSprintBudget sprintBudget) {
        Map queryParams = [
                version: 0,
                budgetId: sprintBudget.budgetId,
                sprintId: sprintBudget.sprintId,
                quantity: sprintBudget.quantity,
                createdBy: sprintBudget.createdBy,
                updatedBy: sprintBudget.updatedBy,
                updatedOn: null,
                createdOn: DateUtility.getSqlDateWithSeconds(sprintBudget.createdOn)
        ]
        List result = executeInsertSql(SPRINT_BUDGET_CREATE_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert sprint budget information')
        }
        int sprintBudgetId = (int) result[0][0]
        sprintBudget.id = sprintBudgetId

        return sprintBudget
    }


    private static final String SPRINT_BUDGET_UPDATE_QUERY = """
            UPDATE budg_sprint_budget SET
                  quantity=:quantity,
                  updated_on=:updatedOn,
                  updated_by=:updatedBy,
                  version=:newVersion
            WHERE
                  id=:id AND
                  version=:version"""

    /**
     * Update BudgSprintBudget object in DB
     * @param sprintBudget -BudgSprintBudget object
     * @return -an integer containing the value of update count
     */
    public int update(BudgSprintBudget sprintBudget) {
        Map queryParams = [
                id: sprintBudget.id,
                version: sprintBudget.version,
                newVersion: sprintBudget.version + 1,
                quantity: sprintBudget.quantity,
                updatedOn: DateUtility.getSqlDateWithSeconds(sprintBudget.updatedOn),
                updatedBy: sprintBudget.updatedBy
        ]

        int updateCount = executeUpdateSql(SPRINT_BUDGET_UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred at budgetDetailsService.update')
        }
        return updateCount
    }

    private static final String DELETE_QUERY = """
        DELETE FROM budg_sprint_budget WHERE id=:id
    """

    /**
     * Delete BudgSprintBudget object from DB
     * @param id -id of object
     * @return -an integer containing the value of delete count
     */
    public int delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException('Update failed on delete sprint-budget')
        }
        return deleteCount
    }

    /**
     * Get count of sprint budget mapping by sprint id
     * @param sprintId -id of sprint
     * @return -an integer containing the value of count
     */
    public int countBySprintId(long sprintId) {
        int count = BudgSprintBudget.countBySprintId(sprintId)
        return count
    }

    /**
     * Get count of sprint budget mapping by sprint & budget id
     * @param sprintId - sprint id
     * @param budgetId - budget id
     * @return -an integer containing the value of count
     */
    public int countBySprintIdAndBudgetId(long sprintId, long budgetId) {
        int count = BudgSprintBudget.countBySprintIdAndBudgetId(sprintId, budgetId)
        return count
    }

    /**
     * Get count of sprint budget mapping by budget id
     * @param budgetId -id of budget
     * @return -an integer containing the value of count
     */
    public int countByBudgetId(long budgetId) {
        int count = BudgSprintBudget.countByBudgetId(budgetId)
        return count
    }
}
