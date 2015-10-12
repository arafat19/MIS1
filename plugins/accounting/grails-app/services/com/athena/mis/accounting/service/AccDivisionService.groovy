package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccDivision
import com.athena.mis.accounting.utility.AccDivisionCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Service class for basic AccDivision CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'AccDivisionService'
 */
class AccDivisionService extends BaseService {
    static transactional = false

    @Autowired
    AccDivisionCacheUtility accDivisionCacheUtility

    /**
     * @return -list of AccDivision objects
     */
    public List list() {
        return AccDivision.list(sort: accDivisionCacheUtility.NAME, order: accDivisionCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    /**
     * Save AccDivision object in database
     * @param accDivision -AccDivision object
     * @return -newly created AccDivision object
     */
    public AccDivision create(AccDivision accDivision) {
        AccDivision newAccDivision = accDivision.save(false)
        return newAccDivision
    }

    private static final String UPDATE_QUERY = """
                    UPDATE acc_division SET
                          version=:newVersion,
                          name=:name,
                          is_active=:isActive,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy
                      WHERE
                          id=:id AND
                          version=:version
                          """
    /**
     * SQL to update AccDivision object in database
     * @param accDivision -AccDivision object
     * @return -updated AccDivision object
     */
    public AccDivision update(AccDivision accDivision) {
        Map queryParams = [
                newVersion: accDivision.version + 1,
                name: accDivision.name,
                isActive: accDivision.isActive,
                updatedBy: accDivision.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(accDivision.updatedOn),
                id: accDivision.id,
                version: accDivision.version
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update AccDivision')
        }
        accDivision.version = accDivision.version + 1
        return accDivision
    }

    private static final String QUERY_DELETE = """
                    DELETE FROM acc_division
                      WHERE id=:id
                    """
    /**
     * Delete AccDivision object by id
     * @param id -AccDivision.id
     * @return -boolean value
     */
    public boolean delete(long id) {
        int success = executeUpdateSql(QUERY_DELETE, [id: id])
        if (success <= 0) {
            throw new RuntimeException('Failed to delete AccDivision')
        }
        return true
    }

    /**
     * Delete AccDivision object
     * @param accDivision -AccDivision
     * @return -boolean value
     */
    public boolean delete(AccDivision accDivision) {
        accDivision.delete()
        return true
    }

}
