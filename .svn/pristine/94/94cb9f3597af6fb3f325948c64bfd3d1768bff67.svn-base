package com.athena.mis.projecttrack.service

import com.athena.mis.BaseService
import com.athena.mis.projecttrack.entity.PtFlow
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

class PtFlowService extends BaseService {

    @Autowired
    PtSessionUtil ptSessionUtil

    /**
     * read single PtFlow object and return the object
     * @param id - PtFlow id
     * @return PtFlow object
     */
    public PtFlow read(long id) {
        PtFlow flow = PtFlow.read(id)
        return flow
    }

    /**
     * Create PtFlow object
     * @param PtFlow -PtFlow object
     * @return -PtFlow object
     */
    public PtFlow create(PtFlow flow) {
        PtFlow newFlow = flow.save(false)
        return newFlow
    }

    private static final String UPDATE_QUERY =
            """
			UPDATE pt_flow SET
				  version=:newVersion,
				  flow=:flow,
                  updated_on=:updatedOn,
                  updated_by=:updatedBy
			WHERE
				  id=:id AND
				  version=:version
	"""

    /**
     * update PtFlow object in DB
     * @param PtFlow - PtFlow Object
     * @return - int containing no of update count
     */
    public int update(PtFlow ptFlow) {
        Map queryParams = [
                id: ptFlow.id,
                newVersion: ptFlow.version + 1,
                version: ptFlow.version,
                flow: ptFlow.flow,
                updatedBy: ptFlow.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(ptFlow.updatedOn)
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update acceptance criteria information')
        }
        ptFlow.version = ptFlow.version + 1
        return updateCount;
    }

    private static final String DELETE_QUERY =
            """
        DELETE FROM pt_flow
        WHERE
            id=:id
    """

    /**
     * Delete ptFlow object from DB
     * @param id -id of ptFlow object
     * @return -an integer containing the value of delete count
     */
    public int delete(long id) {
        Map queryParams = [
                id: id
        ]

        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete ptFlow information')
        }
        return deleteCount;
    }

    public List<PtFlow> findAllByCompanyIdAndBacklogId(BaseService baseService, long companyId, long backlogId) {
        List<PtFlow> lstFlow = PtFlow.findAllByCompanyIdAndBacklogId(companyId, backlogId, [max: baseService.resultPerPage, offset: baseService.start, sort: baseService.sortColumn, order: baseService.sortOrder, readOnly: true])
        return lstFlow
    }

    public int countByCompanyIdAndBacklogId(long companyId, long backlogId) {
        int count = PtFlow.countByCompanyIdAndBacklogId(companyId, backlogId)
        return count
    }

}
