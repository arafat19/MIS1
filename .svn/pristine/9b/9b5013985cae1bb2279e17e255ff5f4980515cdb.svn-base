package com.athena.mis.projecttrack.service

import com.athena.mis.BaseService
import com.athena.mis.projecttrack.entity.PtProject
import com.athena.mis.projecttrack.utility.PtProjectCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 * PtProjectService is used to handle only CRUD related object manipulation (e.g. list, read, create, delete etc.)
 */
class PtProjectService extends BaseService {

    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility

    /**
     * @return - list of ptProject
     */
    public List list() {
        return PtProject.list(sort: ptProjectCacheUtility.SORT_ON_NAME, order: ptProjectCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    /**
     * Get ptProject object by Id
     * @param id -Id of ptProject
     * @return -object of ptProject
     */
    public PtProject read(long id) {
        PtProject project = PtProject.read(id)
        return project
    }

    private static final String INSERT_QUERY =
            """
        INSERT INTO pt_project(id, version, name, code, company_id, created_on, created_by, updated_by)
        VALUES (NEXTVAL('pt_project_id_seq'), :version, :name, :code, :companyId, :createdOn, :createdBy, :updatedBy);
    """
    /**
     * Save ptProject object into DB
     * @param project -ptProject object
     * @return -saved ptProject object
     */
    public PtProject create(PtProject project) {
        Map queryParams = [
                version: project.version,
                name: project.name,
                code: project.code,
                companyId: project.companyId,
                createdOn: DateUtility.getSqlDateWithSeconds(project.createdOn),
                createdBy: project.createdBy,
                updatedBy: project.updatedBy
        ]
        List result = executeInsertSql(INSERT_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert PtProject information')
        }

        int ptProjectId = (int) result[0][0]
        project.id = ptProjectId
        return project
    }

    private static final String UPDATE_QUERY =
            """
        UPDATE pt_project SET
            version=:newVersion,
            name=:name,
            code=:code,
            updated_on=:updatedOn,
            updated_by=:updatedBy
        WHERE
            id=:id AND
            version=:version
    """
    /**
     * Update ptProject object into DB
     * @param project -ptProject object
     * @return -an integer containing the value of update count
     */
    public int update(PtProject project) {
        Map queryParams = [
                id: project.id,
                newVersion: project.version + 1,
                version: project.version,
                name: project.name,
                code: project.code,
                updatedOn: DateUtility.getSqlDateWithSeconds(project.updatedOn),
                updatedBy: project.updatedBy
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update ptProject information')
        }
        return updateCount
    }

    private static final String DELETE_QUERY =
            """
        DELETE FROM pt_project
        WHERE
            id=:id
    """
    /**
     * Delete ptProject object from DB
     * @param id -id of ptProject object
     * @return -an integer containing the value of delete count
     */
    public int delete(long id) {
        Map queryParams = [
                id: id
        ]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete ptProject information')
        }
        return deleteCount
    }

    public void createDefaultDataForPtProject(long companyId) {
        new PtProject(version: 0, companyId: companyId, name: 'MIS', code: 'MIS', createdBy: 12L, createdOn: new Date() - 20, updatedOn: new Date() - 5, updatedBy: 0).save()
        new PtProject(version: 0, companyId: companyId, name: 'EXH(UK)', code: 'EXH(UK)', createdBy: 12L, createdOn: new Date() - 114, updatedOn: new Date() - 12, updatedBy: 0).save()
        new PtProject(version: 0, companyId: companyId, name: 'Project Track', code: 'PT', createdBy: 12L, createdOn: new Date() - 15, updatedOn: new Date(), updatedBy: 0).save()
        new PtProject(version: 0, companyId: companyId, name: 'ARMS', code: 'ARMS', createdBy: 12L, createdOn: new Date() - 25, updatedOn: new Date(), updatedBy: 0).save()
        new PtProject(version: 0, companyId: companyId, name: 'SARB', code: 'SARB', createdBy: 12L, createdOn: new Date() - 25, updatedOn: new Date(), updatedBy: 0).save()
        new PtProject(version: 0, companyId: companyId, name: 'EXH(AUS)', code: 'EXH(AUS)', createdBy: 12L, createdOn: new Date() - 25, updatedOn: new Date(), updatedBy: 0).save()
        new PtProject(version: 0, companyId: companyId, name: 'EXH(SOUTH AFRICA)', code: 'EXH(SOUTH AFRICA)', createdBy: 12L, createdOn: new Date() - 25, updatedOn: new Date(), updatedBy: 0).save()
    }
}
