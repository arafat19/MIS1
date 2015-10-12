package com.athena.mis.budget.service

import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgTask
import com.athena.mis.utility.DateUtility

class BudgTaskService extends BaseService {

    static transactional = false
    public static final String SORT_ON_NAME = "name"
    public final String SORT_ORDER_ASCENDING = 'asc'


    public List list() {
        return BudgTask.list([max: resultPerPage, start: start, sort: SORT_ON_NAME, order: SORT_ORDER_ASCENDING, readOnly: true])
    }

    /**
     * Method to read budgTask object by id
     * @param id - budgTask id
     * @return - budgTask object
     */
    public BudgTask read(long id) {
        BudgTask budgTask = BudgTask.read(id)
        return budgTask
    }

    private static final String INSERT_QUERY = """
            INSERT INTO budg_task(id, version, company_id, created_by, created_on, end_date,
                                    name, start_date, budget_id, status_id, updated_by, updated_on)
            VALUES (NEXTVAL('budg_task_id_seq'),:version, :companyId, :createdBy,
                    :createdOn, :endDate, :name, :startDate, :budgetId, :statusId, :updatedBy, null);
    """

    /**
     * Create new BudgTask
     * @param budgTask - BudgTask object
     * @return - newly created budgTask object
     */
    public BudgTask create(BudgTask budgTask) {
        Map queryParams = [
                version: budgTask.version,
                companyId: budgTask.companyId,
                name: budgTask.name,
                budgetId: budgTask.budgetId,
                statusId: budgTask.statusId,
                createdBy: budgTask.createdBy,
                createdOn: DateUtility.getSqlDateWithSeconds(budgTask.createdOn),
                updatedBy: budgTask.updatedBy,
                startDate: DateUtility.getSqlDate(budgTask.startDate),
                endDate: DateUtility.getSqlDate(budgTask.endDate)
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while creating budget task information')
        }

        int budgTaskId = (int) result[0][0]
        budgTask.id = budgTaskId
        return budgTask
    }

    private static final String BUDG_TYPE_UPDATE_QUERY = """
        UPDATE budg_task SET
            version=:newVersion,
            name=:name,
            status_id =:statusId,
            start_date =:startDate,
            end_date =:endDate,
            updated_on=:updatedOn,
            updated_by=:updatedBy
        WHERE
            id=:id AND
            version=:version
        """

    /**
     * Method to update budgTask  object
     * @param budgTask - object of budgTask
     * @return - updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(BudgTask budgTask) {
        Map queryParams = [
                id: budgTask.id,
                version: budgTask.version,
                newVersion: budgTask.version + 1,
                name: budgTask.name,
                statusId: budgTask.statusId,
                startDate: DateUtility.getSqlDateWithSeconds(budgTask.startDate),
                endDate: DateUtility.getSqlDateWithSeconds(budgTask.endDate),
                updatedBy: budgTask.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(budgTask.updatedOn)
        ]
        int updateCount = executeUpdateSql(BUDG_TYPE_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Error occurred at budget task update")
        }
        return updateCount
    }

    private static final String UPDATE_STATUS_QUERY = """
        UPDATE budg_task SET
            version=:newVersion,
            status_id =:statusId,
            updated_on=:updatedOn,
            updated_by=:updatedBy
        WHERE
            id=:id
        """

    public int updateStatus(BudgTask budgTask) {
        Map queryParams = [
                id: budgTask.id,
                newVersion: budgTask.version + 1,
                statusId: budgTask.statusId,
                updatedBy: budgTask.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(budgTask.updatedOn)
        ]
        int updateCount = executeUpdateSql(UPDATE_STATUS_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Error occurred at task status update")
        }
        return updateCount
    }


    private static final String DELETE_QUERY = """
        DELETE FROM budg_task
        WHERE
        id=:id
        """

    /**
     * Method to delete budg task object from db
     * @param id - budg task id
     * @return- if deleteCount <= 0 then throw exception to rollback transaction; otherwise return true
     */
    public Boolean delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("Fail to delete budg task")
        }
        return Boolean.TRUE
    }


    public int countByNameIlikeAndBudgetIdAndCompanyId(String name, long budgetId, long companyId) {
        int budgTaskCount = BudgTask.countByNameIlikeAndBudgetIdAndCompanyId(name, budgetId, companyId)
        return budgTaskCount
    }

    public int countByNameIlikeAndBudgetIdAndCompanyIdAndIdNotEqual(String name, long budgetId, long companyId, long id) {
        int budgTaskCount = BudgTask.countByNameIlikeAndBudgetIdAndCompanyIdAndIdNotEqual(name, budgetId, companyId, id)
        return budgTaskCount
    }
}
