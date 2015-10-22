package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.CostingDetails
import com.athena.mis.application.utility.CostingDetailsCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

class CostingDetailsService extends BaseService {

    static transactional = false
    static final String SORT_ON_ID = "id";

    @Autowired
    CostingDetailsCacheUtility costingDetailsCacheUtility

    public CostingDetails read(long id) {
        CostingDetails costingDetails = CostingDetails.read(id)
        return costingDetails
    }

    public List list() {
        return CostingDetails.list(sort: SORT_ON_ID, order: costingDetailsCacheUtility.SORT_ORDER_ASCENDING, readOnly: true)
    }

    private static final String QUERY_CREATE = """
            INSERT INTO costing_details(id,version,description,costing_type_id, company_id,created_on,created_by,updated_by,updated_on,costing_amount, costing_date)
            VALUES (NEXTVAL('costing_details_id_seq'),:version,:description,:costingTypeId,:companyId,:createdOn,:createdBy,:updatedBy,null,:costingAmount,:costingDate);
            """
    /**
     * Create new costingType
     * @param costingType -costingType  object
     * @return -create vehicle if saved successfully, otherwise throw RuntimeException
     */
    public CostingDetails create(CostingDetails costingDetails) {

        Map queryParams = [
                version         : costingDetails.version,
                description     : costingDetails.description,
                costingTypeId   : costingDetails.costingTypeId,
                companyId       : costingDetails.companyId,
                createdBy       : costingDetails.createdBy,
                createdOn       : DateUtility.getSqlDateWithSeconds(costingDetails.createdOn),
                updatedBy       : costingDetails.updatedBy,
                costingAmount   : costingDetails.costingAmount,
                costingDate     : DateUtility.getSqlDateWithSeconds(costingDetails.costingDate)

        ]

        List result = executeInsertSql(QUERY_CREATE, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert costing details information')
        }

        int costingTypeId = (int) result[0][0]
        costingDetails.id = costingTypeId
        return costingDetails
    }

    private static final String UPDATE_QUERY = """
                    UPDATE costing_details SET
                          version=:newVersion,
                          description=:description,
                          costing_type_id=:costingTypeId,
                          costing_amount=:costingAmount,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy,
                          costing_date=:costingDate
                    WHERE
                          id=:id AND
                          version=:version
    """

    /**
     * Update designation
     * @param designation - designation object
     * @return- newly updated object of selected designation
     */
    public int update(CostingDetails costingDetails) {

        Map queryParams = [
                id: costingDetails.id,
                newVersion: costingDetails.version + 1,
                version: costingDetails.version,
                costingTypeId: costingDetails.costingTypeId,
                description: costingDetails.description,
                costingAmount: costingDetails.costingAmount,
                updatedBy: costingDetails.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(costingDetails.updatedOn),
                costingDate: DateUtility.getSqlDateWithSeconds(costingDetails.costingDate)
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating costing details information')
        }
        costingDetails.version = costingDetails.version + 1
        return updateCount;
    }

    private static final String DELETE_QUERY = """
                    DELETE FROM costing_details
                      WHERE id=:id
    """

    /**
     * Delete designation
     * @param id - selected designation id
     * @return -an int value(e.g- 1 for success and 0 for failure)
     */
    public int delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete costing details information')
        }
        return deleteCount;
    }

    public int countByDescriptionAndIdNotEqual(String name, long id) {
        int count = CostingDetails.countByDescriptionAndIdNotEqual(name, id)
        return count
    }
}
