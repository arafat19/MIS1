package com.athena.mis.projecttrack.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.utility.PtSprintStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.springframework.beans.factory.annotation.Autowired

@Transactional
class PtSprintService extends BaseService {

    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtSprintStatusCacheUtility ptSprintStatusCacheUtility

    public List<PtSprint> list() {
        List<PtSprint> sprintList = PtSprint.list([max: resultPerPage, start: start, readOnly: true])
        return sprintList
    }

    /**
     * Get count of PtSprint by projectId
     * @param projectId
     * @return -int count
     */
    public int countByProjectId(long projectId) {
        int count = PtSprint.countByProjectId(projectId)
        return count
    }

    /**
     * @return -list of ptSprint
     */
    public Map List(BaseService baseService) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        List<PtSprint> sprintList = PtSprint.withCriteria {
            eq('companyId', companyId)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            order(baseService.sortColumn, baseService.sortOrder)
            setReadOnly(true)
        }
        List counts = PtSprint.withCriteria {
            eq('companyId', companyId)
            projections { rowCount() }
        }
        int total = counts[0]
        return [ptSprintList: sprintList, count: total.toInteger()]
    }

    /**
     * Search ptSprint with criteria
     * @return - a map
     */
    public Map Search(BaseService baseService) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        List<PtSprint> sprintList = PtSprint.withCriteria {
            eq('companyId', companyId)
            ilike(baseService.queryType, Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            order(baseService.sortColumn, baseService.sortOrder)
            setReadOnly(true)
        }
        List counts = PtSprint.withCriteria {
            eq('companyId', companyId)
            ilike(baseService.queryType, Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE)
            projections { rowCount() }
        }
        int total = counts[0]
        return [ptSprintList: sprintList, count: total.toInteger()]
    }

    /**
     * get ptSprint by name
     */
    private PtSprint readBySprintName(String sprintName) {
        PtSprint ptSprint = PtSprint.findByName(sprintName, [readOnly: true])
        return ptSprint
    }

    /**
     * get ptSprint by projectId
     */
    public List<PtSprint> findAllByProjectIdAndCompanyId(long projectId) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        List<PtSprint> ptSprint = PtSprint.findAllByProjectIdAndCompanyId(projectId, companyId)
        return ptSprint
    }

    /**
     * read ptSprint object by id
     * @return -ptSprint object
     */
    public PtSprint read(long id) {
        PtSprint ptSprint = PtSprint.read(id)
        return ptSprint
    }

    /**
     * Get count of sprint object by project id and isActive and id not equal
     * @param projectId -id of project
     * @param isActive -boolean value (true/false)
     * @param id -id of sprint object
     * @return -an integer containing the value of count
     */
    public int countByProjectIdAndIsActiveAndIdNotEqual(long projectId, boolean isActive, long id) {
        int count = PtSprint.countByProjectIdAndIsActiveAndIdNotEqual(projectId, isActive, id)
        return count
    }

    /**
     * Get count of sprint object by project id and start and end date
     * @param projectId -id of project
     * @param name - sprint name
     * @return -an integer containing the value of count
     */
    public int countByProjectIdAndName(long projectId, String name) {
        int count = PtSprint.countByProjectIdAndName(projectId, name)
        return count
    }

    /**
     * Get sprint object by project id and isActive
     * @param projectId -id of project
     * @param isActive -boolean value (true/false)
     * @return -object of PtSprint
     */
    public PtSprint findByProjectIdAndIsActive(long projectId, boolean isActive) {
        PtSprint sprint = PtSprint.findByProjectIdAndIsActive(projectId, isActive)
        return sprint
    }

    /**
     * Get all sprint objects by project id and isActive
     * @param projectId -id of project
     * @param isActive -boolean value (true/false)
     * @return -list of sprint object
     */
    public List<PtSprint> findAllByProjectIdAndIsActive(long projectId, boolean isActive) {
        List<PtSprint> lstSprint = PtSprint.findAllByProjectIdAndIsActive(projectId, isActive)
        return lstSprint
    }

    private static final String INSERT_QUERY = """
        INSERT INTO pt_sprint(id, version, project_id, company_id, start_date, end_date, name, status_id, is_active)
        VALUES (NEXTVAL('pt_sprint_id_seq'), :version, :projectId, :companyId, :startDate, :endDate, :name, :statusId, :isActive);
    """

    /**
     * Save ptSprint object into DB
     * @param ptSprint -ptSprint object
     * @return -saved ptSprint object
     */
    public PtSprint create(PtSprint ptSprint) {

        Map queryParams = [
                version: ptSprint.version,
                projectId: ptSprint.projectId,
                companyId: ptSprint.companyId,
                startDate: DateUtility.getSqlDate(ptSprint.startDate),
                endDate: DateUtility.getSqlDate(ptSprint.endDate),
                name: ptSprint.name,
                statusId: ptSprint.statusId,
                isActive: ptSprint.isActive
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert ptSprint information')
        }

        int ptSprintId = (int) result[0][0]
        ptSprint.id = ptSprintId
        return ptSprint
    }

    private static final String UPDATE_QUERY = """
        UPDATE pt_sprint SET
              version=:newVersion,
              project_id=:projectId,
              start_date=:startDate,
              end_date=:endDate,
              name=:name,
              status_id=:statusId,
              is_active=:isActive
        WHERE
              id=:id AND
              version=:version
    """

    /**
     * Update ptSprint object in DB
     * @param ptSprint -ptSprint object
     * @return -an integer containing the value of update count
     */
    public int update(PtSprint ptSprint) {

        Map queryParams = [
                id: ptSprint.id,
                newVersion: ptSprint.version + 1,
                version: ptSprint.version,
                projectId: ptSprint.projectId,
                startDate: DateUtility.getSqlDate(ptSprint.startDate),
                endDate: DateUtility.getSqlDate(ptSprint.endDate),
                name: ptSprint.name,
                statusId: ptSprint.statusId,
                isActive: ptSprint.isActive
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update ptSprint information')
        }
        ptSprint.version = ptSprint.version + 1
        return updateCount
    }

    private static final String DELETE_QUERY = """
        DELETE FROM pt_sprint
           WHERE
              id=:id
    """

    /**
     * Delete ptSprint object from DB
     * @param id -id of ptSprint object
     * @return -an integer containing the value of delete count
     */
    public int delete(long id) {

        Map queryParams = [
                id: id
        ]

        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete ptSprint information')
        }
        return deleteCount
    }

    /**
     *
     * @param startDate -start date from UI.
     * @param endDate -end date from UI.
     * @param projectId - project id associated with sprint
     * @return - total number of over-lapped data range
     */
    public int countDateRangeOverLap(Date startDate, Date endDate, long projectId) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity statusDefined = (SystemEntity) ptSprintStatusCacheUtility.readByReservedAndCompany(ptSprintStatusCacheUtility.DEFINES_RESERVED_ID, companyId)
        SystemEntity statusInProgress = (SystemEntity) ptSprintStatusCacheUtility.readByReservedAndCompany(ptSprintStatusCacheUtility.IN_PROGRESS_RESERVED_ID, companyId)

        String COUNT_OVERLAP_SELECT_QUERY = """
            SELECT COUNT(id) from pt_sprint
              WHERE :endDate >= start_date AND end_date >= :startDate
              AND project_id = :projectId
              AND status_id IN (${statusDefined.id}, ${statusInProgress.id});
        """
        Map queryParams = [
                startDate: DateUtility.getSqlDate(startDate),
                endDate: DateUtility.getSqlDate(endDate),
                projectId: projectId
        ]

        List<GroovyRowResult> countResult = executeSelectSql(COUNT_OVERLAP_SELECT_QUERY, queryParams)
        int total = (int) countResult[0].count

        return total;
    }

    /**
     *
     * @param id -sprint id.
     * @param startDate -start date from UI.
     * @param endDate -end date from UI.
     * @param projectId - project id associated with sprint
     * @return - total number of over-lapped data range
     */
    public int countDateRangeOverLapForEdit(long id, Date startDate, Date endDate, long projectId) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId();
        SystemEntity statusDefined = (SystemEntity) ptSprintStatusCacheUtility.readByReservedAndCompany(ptSprintStatusCacheUtility.DEFINES_RESERVED_ID, companyId)
        SystemEntity statusInProgress = (SystemEntity) ptSprintStatusCacheUtility.readByReservedAndCompany(ptSprintStatusCacheUtility.IN_PROGRESS_RESERVED_ID, companyId)

        String COUNT_OVERLAP_FOR_EDIT = """
            SELECT COUNT(id) from pt_sprint
               WHERE :endDate >= start_date AND end_date >= :startDate
                  AND id <> :id
                  AND project_id = :projectId
                  AND status_id IN (${statusDefined.id}, ${statusInProgress.id});
        """
        Map queryParams = [
                id: id,
                startDate: DateUtility.getSqlDate(startDate),
                endDate: DateUtility.getSqlDate(endDate),
                projectId: projectId
        ]

        List<GroovyRowResult> countResult = executeSelectSql(COUNT_OVERLAP_FOR_EDIT, queryParams)
        int total = (int) countResult[0].count

        return total;
    }
}
