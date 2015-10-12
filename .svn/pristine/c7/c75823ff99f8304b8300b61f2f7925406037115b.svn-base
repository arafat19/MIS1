package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccLc
import com.athena.mis.accounting.utility.AccLcCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Service class for basic AccLc CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'AccLcService'
 */
class AccLcService extends BaseService {

    @Autowired
    AccLcCacheUtility accLcCacheUtility

    static transactional = false

    /**
     * get list of AccLc objects
     * @return -list of AccLc objects
     */
    public List list() {
        return AccLc.list(readOnly: true, sort: accLcCacheUtility.SORT_ON_ID, order: accLcCacheUtility.SORT_ORDER_DESCENDING);
    }

    /**
     * get AccLc object by id
     * @param id -AccLc.id
     * @return -AccLc object
     */
    public AccLc read(long id) {
        return AccLc.read(id)
    }

    /**
     * Save AccLc object in database
     * @param AccLc -AccLc object
     * @return -newly created AccLc object
     */
    public AccLc create(AccLc accLc) {
        AccLc newAccLc = accLc.save(false)
        return newAccLc
    }

    private static final String QUERY_UPDATE = """
                    UPDATE acc_lc
                    SET
                          version=:newVersion,
                          lc_no=:lcNo,
                          bank=:bank,
                          amount=:amount,
                          item_id=:itemId,
                          supplier_id=:supplierId,
                          updated_by=:updatedBy,
                          updated_on=:updatedOn
                    WHERE
                          id=:id
                          """
    /**
     * SQL to update AccLc object in database
     * @param AccLc -AccLc object
     * @return -updated AccLc object
     */
    public int update(AccLc accLc) {
        Map queryParams = [
                id: accLc.id,
                version: accLc.version,
                newVersion: accLc.version + 1,
                lcNo: accLc.lcNo,
                bank: accLc.bank,
                amount: accLc.amount,
                itemId: accLc.itemId,
                supplierId: accLc.supplierId,
                updatedBy: accLc.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(accLc.updatedOn)
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating LC information')
        }
        return updateCount
    }

    private static final String QUERY_DELETE = """
                    DELETE FROM acc_lc
                      WHERE
                          id=:id
                    """
    /**
     * Delete AccLc object by id
     * @param id -AccLc.id
     * @return -boolean value
     */
    public boolean delete(long id) {
        int updateCount = executeUpdateSql(QUERY_DELETE, [id: id])
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to delete LC')
        }
        return true
    }
}
