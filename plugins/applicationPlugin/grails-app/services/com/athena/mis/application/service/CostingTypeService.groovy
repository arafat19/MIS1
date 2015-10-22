package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.CostingType
import com.athena.mis.application.utility.CostingTypeCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

class CostingTypeService extends BaseService {

    static transactional = false
    static final String SORT_ON_ID = "id";

    @Autowired
    CostingTypeCacheUtility costingTypeCacheUtility

    public List list() {
        return CostingType.list(sort: SORT_ON_ID, order: costingTypeCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    public CostingType read(long id) {
        return CostingType.read(id)
    }

    private static final String QUERY_CREATE = """
            INSERT INTO costing_type(id,version,name,description,company_id,created_on,created_by,updated_by,updated_on)
            VALUES (NEXTVAL('costing_type_id_seq'),:version,:name,:description,:companyId,:createdOn,:createdBy,:updatedBy,null);
            """
    /**
     * Create new costingType
     * @param costingType -costingType  object
     * @return -create vehicle if saved successfully, otherwise throw RuntimeException
     */
    public CostingType create(CostingType costingType) {

        Map queryParams = [
                version    : costingType.version,
                name       : costingType.name,
                description: costingType.description,
                companyId  : costingType.companyId,
                createdBy  : costingType.createdBy,
                createdOn  : DateUtility.getSqlDateWithSeconds(costingType.createdOn),
                updatedBy  : costingType.updatedBy
        ]

        List result = executeInsertSql(QUERY_CREATE, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert costing Type information')
        }

        int costingTypeId = (int) result[0][0]
        costingType.id = costingTypeId
        return costingType
    }

    private static final String DELETE_QUERY = """
                    DELETE FROM costing_type
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
            throw new RuntimeException('Error occurred while delete costing type information')
        }
        return deleteCount;
    }

    private static final String UPDATE_QUERY = """
                    UPDATE costing_type SET
                          version=:newVersion,
                          name=:name,
                          description=:description,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy
                    WHERE
                          id=:id AND
                          version=:version
    """

    /**
     * Update designation
     * @param designation - designation object
     * @return- newly updated object of selected designation
     */
    public int update(CostingType costingType) {

        Map queryParams = [
                id: costingType.id,
                newVersion: costingType.version + 1,
                version: costingType.version,
                name: costingType.name,
                description: costingType.description,
                updatedBy: costingType.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(costingType.updatedOn)
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating costing type information')
        }
        costingType.version = costingType.version + 1
        return updateCount;
    }

    public int countByNameIlike(String name) {
        int count = CostingType.countByNameIlike(name)
        return count
    }


    public int countByNameAndIdNotEqual(String name, long id) {
        int count = CostingType.countByNameAndIdNotEqual(name, id)
        return count
    }
}
