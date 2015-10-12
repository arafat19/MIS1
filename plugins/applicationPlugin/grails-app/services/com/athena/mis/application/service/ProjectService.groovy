package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 * ProjectService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class ProjectService extends BaseService {

    static transactional = false

    @Autowired
    ProjectCacheUtility projectCacheUtility

    /**
     * Pull project object
     * @return - list of project
     */
    public List list() {
        return Project.list(sort: projectCacheUtility.SORT_ON_NAME, order: projectCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }


    private static final String INSERT_QUERY = """
            INSERT INTO project(id, version, code, description, name, created_on, created_by,  updated_on,
                                    updated_by, company_id, is_approve_in_from_supplier,
                                    is_approve_in_from_inventory, is_approve_inv_out,
                                    is_approve_consumption, is_approve_production, content_count, start_date, end_date)
            VALUES (NEXTVAL('project_id_seq'),:version, :code, :description, :name, :createdOn, :createdBy, null, :updatedBy,
                :companyId, :isApproveInFromSupplier, :isApproveInFromInventory, :isApproveInvOut, :isApproveConsumption,
                :isApproveProduction, :contentCount, :startDate, :endDate);
    """

    /**
     * Create Project object in DB
     * @param project -object of Project
     * @return -saved object of Project
     */
    public Project create(Project project) {
        Map queryParams = [
                version: project.version,
                companyId: project.companyId,
                name: project.name,
                code: project.code,
                description: project.description,
                createdBy: project.createdBy,
                createdOn: DateUtility.getSqlDateWithSeconds(project.createdOn),
                updatedBy: project.updatedBy,
                isApproveInFromSupplier: project.isApproveInFromSupplier,
                isApproveInFromInventory: project.isApproveInFromInventory,
                isApproveInvOut: project.isApproveInvOut,
                isApproveConsumption: project.isApproveConsumption,
                isApproveProduction: project.isApproveProduction,
                contentCount: project.contentCount,
                startDate:  DateUtility.getSqlDate(project.startDate),
                endDate:  DateUtility.getSqlDate(project.endDate)
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while creating Project')
        }

        int projectId = (int) result[0][0]
        project.id = projectId
        return project
    }

    private static final String UPDATE_QUERY = """
                    UPDATE project SET
                          version= :newVersion,
                          name=:name,
                          code=:code,
                          start_date =:startDate,
                          end_date =:endDate,
                          description=:description,
                          is_approve_in_from_supplier=:isApproveInFromSupplier,
                          is_approve_in_from_inventory=:isApproveInFromInventory,
                          is_approve_inv_out=:isApproveInvOut,
                          is_approve_consumption=:isApproveConsumption,
                          is_approve_production=:isApproveProduction,
                          updated_by =:updatedBy,
                          updated_on =:updatedOn
                       WHERE
                          id=:id AND
                          version=:version
    """

    /**
     * Updates Project object in DB
     * @param project -object of Project
     * @return -updated object of Project
     */
    public int update(Project project) {

        Map queryParams = [
                newVersion: project.version + 1,
                name: project.name,
                code: project.code,
                description: project.description,
                id: project.id,
                version: project.version,
                isApproveInFromSupplier: project.isApproveInFromSupplier,
                isApproveInFromInventory: project.isApproveInFromInventory,
                isApproveInvOut: project.isApproveInvOut,
                isApproveConsumption: project.isApproveConsumption,
                isApproveProduction: project.isApproveProduction,
                updatedBy: project.updatedBy,
                startDate:  DateUtility.getSqlDateWithSeconds(project.startDate),
                endDate:  DateUtility.getSqlDateWithSeconds(project.endDate),
                updatedOn: DateUtility.getSqlDateWithSeconds(project.updatedOn)
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("error occurred at projectService.update")
        }
        return updateCount
    }

    private static final String DELETE_QUERY = """
                    DELETE FROM project
                      WHERE
                          id=:id AND
                          version=:version
    """

    /**
     * Deletes a project
     * @param project - project object
     * @return - boolean value(true for success & false for failure)
     */
    public Boolean delete(Project project) {

        Map queryParams = [
                id: project.id,
                version: project.version
        ]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("error occurred at projectService.delete")
        }
        return Boolean.TRUE;
    }

    /**
     * Get object of Project
     * @param id -id of Project
     * @return -object of Project
     */
    public Project read(long id) {
        return Project.read(id)
    }

    /**
     * Get object of Project by name
     * @param name -name of Project
     * @return -object of Project
     */
    public Project findByName(String name) {
        Project project = Project.findByName(name, [readOnly: true])
        return project
    }

    private static final String UPDATE_CONTENT_COUNT_QUERY = """
        UPDATE project
        SET content_count = content_count + :contentCount,
            version = version + 1
        WHERE
            id=:id
    """

    // update content count for project during create, update and delete content
    public Integer updateContentCountForProject(long projectId, int count){
        Map queryParams = [
                contentCount: count,
                id: projectId
        ]
        int updateCount = executeUpdateSql(UPDATE_CONTENT_COUNT_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Content count updated for Project")
        }
        return (new Integer(updateCount))
    }

    /**
     * applicable only for create default project
     */
    public void createDefaultData(long companyId) {
        new Project(name: "Dhaka Flyover", code: "DF", description: 'A Flyover all over the dhaka', companyId: companyId, createdBy: 1, createdOn: new Date(),contentCount:0, startDate: new Date(), endDate: new Date() + DateUtility.DATE_RANGE_THIRTY).save()
        new Project(name: "School Construction", code: "SC", description: 'Construction of 4 storied school building', companyId: companyId, createdBy: 1, createdOn: new Date(),contentCount:0, startDate: new Date(), endDate: new Date() + DateUtility.DATE_RANGE_THIRTY).save()
        new Project(name: "Roads Construction", code: "RC", description: 'Construction of Dhaka Chittagang 6 Lane Highway', companyId: companyId, createdBy: 1, createdOn: new Date(),contentCount:0, startDate: new Date(), endDate: new Date() + DateUtility.DATE_RANGE_THIRTY).save()
    }
}
