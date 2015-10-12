package com.athena.mis.budget.service

import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgBudgetDetails
import com.athena.mis.utility.DateUtility

class BudgetDetailsService extends BaseService {

    static transactional = false

    /**
     * Method to read budget details object by id
     * @param id - budget details id
     * @return - budget details object
     */
    public BudgBudgetDetails read(long id) {
        BudgBudgetDetails budgetDetails = BudgBudgetDetails.read(id)
        return budgetDetails
    }

    private static final String BUDG_DETAILS_CREATE_QUERY = """
            INSERT INTO budg_budget_details(id, version, budget_id, comments, created_by, created_on, rate, item_id, project_id,
            quantity,total_consumption, is_consumed_against_fixed_asset, updated_by, updated_on, company_id, is_up_to_date)
            VALUES (NEXTVAL('budg_budget_details_id_seq'), :version, :budgetId, :comments, :createdBy, :createdOn, :rate, :itemId,
            :projectId, :quantity, :totalConsumption, :isConsumedAgainstFixedAsset, :updatedBy, :updatedOn, :companyId, :isUpToDate);
    """

    /**
     * Method to create budget details object
     * @param budgetDetails - budgetDetails object
     * @return - newly created budget details object
     */
    public BudgBudgetDetails create(BudgBudgetDetails budgetDetails) {
        Map queryParams = [
                version: 0,
                budgetId: budgetDetails.budgetId,
                comments: budgetDetails.comments,
                createdBy: budgetDetails.createdBy,
                rate: budgetDetails.rate,
                itemId: budgetDetails.itemId,
                projectId: budgetDetails.projectId,
                quantity: budgetDetails.quantity,
                totalConsumption: budgetDetails.totalConsumption,
                isConsumedAgainstFixedAsset: budgetDetails.isConsumedAgainstFixedAsset,
                updatedBy: budgetDetails.updatedBy,
                updatedOn: null,
                createdOn: DateUtility.getSqlDateWithSeconds(budgetDetails.createdOn),
                companyId: budgetDetails.companyId,
                isUpToDate: budgetDetails.isUpToDate
        ]
        List result = executeInsertSql(BUDG_DETAILS_CREATE_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert budget details information')
        }
        int budgetDetailsId = (int) result[0][0]
        budgetDetails.id = budgetDetailsId

        return budgetDetails
    }


    private static final String BUDG_DETAILS_UPDATE_QUERY = """
            UPDATE budg_budget_details SET
                  quantity=:quantity,
                  total_consumption=:totalConsumption,
                  is_consumed_against_fixed_asset=:isConsumedAgainstFixedAsset,
                  rate=:rate,
                  comments=:comments,
                  updated_on=:updatedOn,
                  updated_by=:updatedBy,
                  version=:newVersion
            WHERE
                  id=:id AND
                  version=:version
    """

    /**
     * Method to update budget details
     * @param budgetDetails - budget details object
     * @return - updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(BudgBudgetDetails budgetDetails) {
        Map queryParams = [
                id: budgetDetails.id,
                version: budgetDetails.version,
                newVersion: budgetDetails.version + 1,
                quantity: budgetDetails.quantity,
                totalConsumption: budgetDetails.totalConsumption,
                isConsumedAgainstFixedAsset: budgetDetails.isConsumedAgainstFixedAsset,
                comments: budgetDetails.comments,
                updatedOn: DateUtility.getSqlDateWithSeconds(budgetDetails.updatedOn),
                updatedBy: budgetDetails.updatedBy,
                rate: budgetDetails.rate
        ]

        int updateCount = executeUpdateSql(BUDG_DETAILS_UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred at budgetDetailsService.update')
        }
        return updateCount
    }

    private static final String DELETE_QUERY = """
        DELETE FROM budg_budget_details
        WHERE
        id=:id
    """

    /**
     * Method to delete budget details object
     * @param id - budgetDetails.id
     * @return -  deleteCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException('Budget update failed on delete Budget-Details')
        }
        return deleteCount
    }

    /**
     * get count of budget details by budget id and item id
     * @param budgetId -id of budget
     * @param itemId -id of item
     * @return -an integer containing the value of count
     */
    public int countByBudgetIdAndItemId(long budgetId, long itemId) {
        int count = BudgBudgetDetails.countByBudgetIdAndItemId(budgetId, itemId)
        return count
    }

    /**
     * get budget details object by budget id and item id
     * @param budgetId -id of budget
     * @param itemId -id of item
     * @return -an object of BudgBudgetDetails
     */
    public BudgBudgetDetails findByBudgetIdAndItemId(long budgetId, long itemId) {
        BudgBudgetDetails budgetDetails = BudgBudgetDetails.findByBudgetIdAndItemId(budgetId, itemId)
        return budgetDetails
    }

    /**
     * get count of budget details by budget id
     * @param budgetId -id of budget
     * @return -an integer containing the value of count
     */
    public int countByBudgetId(long budgetId) {
        int count = BudgBudgetDetails.countByBudgetId(budgetId)
        return count
    }
}
