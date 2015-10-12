package com.athena.mis.budget.service

import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgSprint
import com.athena.mis.budget.entity.BudgSprintBudget
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult

/**
 * BudgSprintService is used to handle only CRUD related object manipulation (e.g. list, read, create, delete etc.)
 */
class BudgSprintService extends BaseService {

    static transactional = false

    /**
     * Read BudgSprint object by id
     * @param id -id of BudgSprint
     * @return -object of BudgSprint
     */
    public BudgSprint read(long id) {
        BudgSprint budgSprint = BudgSprint.read(id)
        return budgSprint
    }

    private static final String INSERT_QUERY = """
        INSERT INTO budg_sprint(id, version, project_id, company_id, start_date, end_date, name, created_by, created_on,
        updated_by, updated_on, is_active)
        VALUES (NEXTVAL('budg_sprint_id_seq'), :version, :projectId, :companyId, :startDate, :endDate, :name, :createdBy,
        :createdOn, :updatedBy, :updatedOn, :isActive);
    """

    /**
     * Save BudgSprint object in DB
     * @param budgSprint -unsaved object of BudgSprint
     * @return -saved object of BudgSprint
     */
    public BudgSprint create(BudgSprint budgSprint) {
        Map queryParams = [
                version: budgSprint.version,
                projectId: budgSprint.projectId,
                companyId: budgSprint.companyId,
                startDate: DateUtility.getSqlDate(budgSprint.startDate),
                endDate: DateUtility.getSqlDate(budgSprint.endDate),
                name: budgSprint.name,
                createdBy: budgSprint.createdBy,
                createdOn: DateUtility.getSqlDateWithSeconds(budgSprint.createdOn),
                updatedBy: budgSprint.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(budgSprint.updatedOn),
                isActive: budgSprint.isActive
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred at budgSprintService.create')
        }
        int sprintId = (int) result[0][0]
        budgSprint.id = sprintId
        return budgSprint
    }

    private static final String BUDG_SPRINT_UPDATE_QUERY = """
        UPDATE budg_sprint SET
            name=:name,
            start_date=:startDate,
            end_date=:endDate,
            updated_on=:updatedOn,
            updated_by=:updatedBy,
            version=:newVersion
        WHERE
            id=:id AND
            version=:version
    """

    /**
     * Update BudgSprint object in DB
     * @param budgSprint -object of BudgSprint
     * @return -an integer containing the value of update count
     * if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(BudgSprint budgSprint) {
        Map queryParams = [
                id: budgSprint.id,
                version: budgSprint.version,
                newVersion: budgSprint.version + 1,
                name: budgSprint.name,
                startDate: DateUtility.getSqlDate(budgSprint.startDate),
                endDate: DateUtility.getSqlDate(budgSprint.endDate),
                updatedOn: DateUtility.getSqlDateWithSeconds(budgSprint.updatedOn),
                updatedBy: budgSprint.updatedBy
        ]

        int updateCount = executeUpdateSql(BUDG_SPRINT_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred at budgSprintService.update')
        }
        return updateCount
    }

    private static final String DELETE_QUERY = """
        DELETE FROM budg_sprint
        WHERE id=:id
    """

    /**
     * Delete BudgSprint object from DB
     * @param id -id of BudgSprint object
     * @return -an integer containing the value of delete count
     * if deleteCount <= 0 then throw exception to rollback transaction
     */
    public int delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred at budgSprintService.delete')
        }
        return deleteCount
    }

    /**
     * Get list of sprint by company id
     * @param companyId -id of company
     * @param baseService -object of BaseService
     * @return -a list of sprint
     */
    public List<BudgSprint> findAllByCompanyId(long companyId, BaseService baseService) {
        List<BudgSprint> lstSprint = BudgSprint.findAllByCompanyId(companyId, [offset: baseService.start, max: baseService.resultPerPage, sort: baseService.sortColumn, order: baseService.sortOrder, readOnly: true])
        return lstSprint
    }

    /**
     * Get count of sprint by company id
     * @param companyId -id of company
     * @return -an integer containing the value of count
     */
    public int countByCompanyId(long companyId) {
        int count = BudgSprint.countByCompanyId(companyId)
        return count
    }

    /**
     * Get list of sprint by search through specific key word
     * @param query -search key word
     * @param companyId -id of company
     * @param baseService -object of BaseService
     * @return -a list of sprint
     */
    public List<BudgSprint> findAllByNameIlikeAndCompanyId(String query, long companyId, BaseService baseService) {
        String name = Tools.PERCENTAGE + query + Tools.PERCENTAGE
        List<BudgSprint> lstSprint = BudgSprint.findAllByNameIlikeAndCompanyId(name, companyId, [offset: baseService.start, max: baseService.resultPerPage, sort: baseService.sortColumn, order: baseService.sortOrder, readOnly: true])
        return lstSprint
    }

    /**
     * Get count of sprint by search through specific key word
     * @param query -search key word
     * @param companyId -id of company
     * @return -an integer containing the value of count
     */
    public int countByNameIlikeAndCompanyId(String query, long companyId) {
        String name = Tools.PERCENTAGE + query + Tools.PERCENTAGE
        int count = BudgSprint.countByNameIlikeAndCompanyId(name, companyId)
        return count
    }

    private static final String QUERY_FOR_DATE_RANGE = """
        SELECT COUNT(id) from budg_sprint
        WHERE :endDate >= start_date AND end_date >= :startDate
        AND project_id = :projectId;
    """

    /**
     * Check date range for sprint create
     * @param startDate -start date
     * @param endDate -end date
     * @param projectId -id of project
     * @return -an integer containing the value of count
     */
    public int checkDateRange(Date startDate, Date endDate, long projectId) {
        Map queryParams = [
                startDate: DateUtility.getSqlDate(startDate),
                endDate: DateUtility.getSqlDate(endDate),
                projectId: projectId
        ]

        List<GroovyRowResult> countResult = executeSelectSql(QUERY_FOR_DATE_RANGE, queryParams)
        int count = (int) countResult[0].count
        return count
    }

    private static final String CHECK_DATE_RANGE_FOR_UPDATE = """
        SELECT COUNT(id) from budg_sprint
        WHERE :endDate >= start_date AND end_date >= :startDate
        AND id <> :id
        AND project_id = :projectId;
    """

    /**
     * Check date range for sprint update
     * @param id -id of sprint object
     * @param startDate -start date
     * @param endDate -end date
     * @param projectId -id of project
     * @return -an integer containing the value of count
     */
    public int checkDateRangeForUpdate(long id, Date startDate, Date endDate, long projectId) {
        Map queryParams = [
                id: id,
                startDate: DateUtility.getSqlDate(startDate),
                endDate: DateUtility.getSqlDate(endDate),
                projectId: projectId
        ]

        List<GroovyRowResult> countResult = executeSelectSql(CHECK_DATE_RANGE_FOR_UPDATE, queryParams)
        int count = (int) countResult[0].count
        return count
    }

    public int countBySprintId(long sprintId) {
        int count = BudgSprintBudget.countBySprintId(sprintId)
        return count
    }
}
