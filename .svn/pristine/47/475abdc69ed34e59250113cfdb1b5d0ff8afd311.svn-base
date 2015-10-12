package com.athena.mis.budget.service

import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgSchema
import com.athena.mis.utility.DateUtility

/**
 * BudgSchemaService is used to handle only CRUD related object manipulation (e.g. list, read, create, delete etc.)
 */
class BudgSchemaService extends BaseService {

    static transactional = false

    /**
     * Read BudgSchema object by id
     * @param id -id of BudgSchema
     * @return -object of BudgSchema
     */
    public BudgSchema read(long id) {
        BudgSchema budgSchema = BudgSchema.read(id)
        return budgSchema
    }

    /**
     * Save BudgSchema object in DB
     * @param budgSchema -unsaved object of BudgSchema
     * @return -saved object of BudgSchema
     */
    public BudgSchema create(BudgSchema budgSchema) {
        BudgSchema savedBudgSchema = budgSchema.save()
        return savedBudgSchema
    }

    private static final String BUDG_SCHEMA_UPDATE_QUERY = """
        UPDATE budg_schema SET
            quantity=:quantity,
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
     * Update BudgSchema object in DB
     * @param budgSchema -object of BudgSchema
     * @return -an integer containing the value of update count
     * if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(BudgSchema budgSchema) {
        Map queryParams = [
                id: budgSchema.id,
                version: budgSchema.version,
                newVersion: budgSchema.version + 1,
                comments: budgSchema.comments,
                updatedOn: DateUtility.getSqlDateWithSeconds(budgSchema.updatedOn),
                updatedBy: budgSchema.updatedBy,
                quantity: budgSchema.quantity,
                rate: budgSchema.rate
        ]

        int updateCount = executeUpdateSql(BUDG_SCHEMA_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred at budgSchemaService.update')
        }
        return updateCount
    }

    private static final String DELETE_QUERY = """
        DELETE FROM budg_schema
        WHERE id=:id
    """

    /**
     * Delete BudgSchema object from DB
     * @param id -id of BudgSchema object
     * @return -an integer containing the value of delete count
     * if deleteCount <= 0 then throw exception to rollback transaction
     */
    public int delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred at budgSchemaService.delete')
        }
        return deleteCount
    }

    /**
     * Get list of budget schema by budget id & company id
     * @param budgetId -id of budget
     * @param companyId - id of company
     * @param baseService -object of BaseService
     * @return -a list of budget schema
     */
    public List<BudgSchema> findAllByBudgetIdAndCompanyId(long budgetId, long companyId, BaseService baseService) {
        List<BudgSchema> lstBudgetSchema = BudgSchema.findAllByBudgetIdAndCompanyId(budgetId, companyId, [offset: baseService.start, max: baseService.resultPerPage, sort: baseService.sortColumn, order: baseService.sortOrder, readOnly: true])
        return lstBudgetSchema
    }

    /**
     * Get count of budget schema by budget id & company id
     * @param budgetId -id of budget
     * @param companyId -id of company
     * @return -an integer containing the value of count
     */
    public int countByBudgetIdAndCompanyId(long budgetId, long companyId) {
        int count = BudgSchema.countByBudgetIdAndCompanyId(budgetId, companyId)
        return count
    }

    /**
     * Get list of budget schema by budget id, item id & company id
     * @param budgetId -id of budget
     * @param lstItemIds -list of item ids
     * @param companyId - id of company
     * @param baseService -object of BaseService
     * @return -a list of budget schema
     */
    public List<BudgSchema> findAllByBudgetIdAndItemIdInListAndCompanyId(long budgetId, List<Long> lstItemIds, long companyId, BaseService baseService) {
        List<BudgSchema> lstBudgetSchema = BudgSchema.findAllByBudgetIdAndItemIdInListAndCompanyId(budgetId, lstItemIds, companyId, [offset: baseService.start, max: baseService.resultPerPage, sort: baseService.sortColumn, order: baseService.sortOrder, readOnly: true])
        return lstBudgetSchema
    }

    /**
     * Get count of budget schema by budget id, item id & company id
     * @param budgetId -id of budget
     * @param lstItemIds -list of item ids
     * @param companyId -id of company
     * @return -an integer containing the value of count
     */
    public int countByBudgetIdAndItemIdInListAndCompanyId(long budgetId, List<Long> lstItemIds, long companyId) {
        int count = BudgSchema.countByBudgetIdAndItemIdInListAndCompanyId(budgetId, lstItemIds, companyId)
        return count
    }
}
