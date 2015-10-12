package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccGroup
import com.athena.mis.accounting.utility.AccGroupCacheUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Service class for basic AccGroup CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'AccGroupService'
 */
class AccGroupService extends BaseService {

    static transactional = false

    @Autowired
    AccGroupCacheUtility accGroupCacheUtility

    /**
     * get AccGroup object by id
     * @param id -AccGroup.id
     * @return -accGroup object
     */
    public AccGroup read(long id) {
        return AccGroup.read(id)
    }

    /**
     * @return -list of AccGroup objects
     */
    public List list() {
        return AccGroup.list(sort: accGroupCacheUtility.SORT_BY_NAME, order: accGroupCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    private static final String INSERT_QUERY = """
            INSERT INTO acc_group(id, version, description, is_active, name, company_id,
                          is_reserved, system_acc_group)
            VALUES (:id, 0, :description, :isActive, :name, :companyId, :isReserved, :systemAccGroup)
    """

    /**
     * Save AccGroup object in database
     * @param accGroup -AccGroup object
     * @return -newly created AccGroup object
     */
    public AccGroup create(AccGroup accGroup) {
        Map queryParams = [
                id: accGroup.id,
                description: accGroup.description,
                isActive: accGroup.isActive,
                name: accGroup.name,
                companyId: accGroup.companyId,
                isReserved: accGroup.isReserved,
                systemAccGroup: accGroup.systemAccGroup
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException("error occurred at accGroupService.create")

        }
        return accGroup
    }

    private static final String SELECT_NEXT_VAL_ACC_GROUP = "SELECT nextval('acc_group_id_seq') as id"

    /**
     * Get id from dedicated acc group id sequence
     * @return - a long variable containing the value of id
     */
    public long getAccGroupId() {
        List results = executeSelectSql(SELECT_NEXT_VAL_ACC_GROUP)
        long accGroupId = results[0].id
        return accGroupId
    }

    private static final String QUERY_UPDATE = """
                        UPDATE acc_group SET
                            version=:newVersion,
                            description=:description,
                            name=:name,
                            is_active=:isActive
                        WHERE id=:id AND
                          version=:version
    """

    /**
     * SQL to update AccGroup object in database
     * @param accGroup -AccGroup object
     * @return -updated AccGroup object
     */
    public AccGroup update(AccGroup accGroup) {
        Map queryParams = [
                id: accGroup.id,
                version: accGroup.version,
                newVersion: accGroup.version + 1,
                description: accGroup.description,
                name: accGroup.name,
                isActive: accGroup.isActive
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update AccGroup')
        }
        accGroup.version = accGroup.version + 1
        return accGroup
    }

    private static final String QUERY_DELETE = """
                    DELETE FROM acc_group
                      WHERE
                          id=:id
    """

    /**
     * Delete AccGroup object by id
     * @param id -AccGroup.id
     * @return -boolean value
     */
    public boolean delete(long id) {
        int success = executeUpdateSql(QUERY_DELETE, [id: id])
        if (!success) {
            throw new RuntimeException('Failed to delete AccGroup')
        }
        return true
    }

    /**
     * insert default accGroup into database when application starts with bootstrap
     */
    public void createDefaultData(long companyId) {
        AccGroup accGroup1 = new AccGroup()
        accGroup1.description = "Description here"
        accGroup1.isActive = true
        accGroup1.name = "Bank"
        accGroup1.companyId = companyId
        accGroup1.isReserved = true
        accGroup1.systemAccGroup = accGroupCacheUtility.ACC_GROUP_BANK
        accGroup1.save()

        AccGroup accGroup2 = new AccGroup()
        accGroup2.description = "Description here"
        accGroup2.isActive = true
        accGroup2.name = "Cash"
        accGroup2.companyId = companyId
        accGroup2.isReserved = true
        accGroup2.systemAccGroup = accGroupCacheUtility.ACC_GROUP_CASH
        accGroup2.save()
    }
}
