package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccTier3
import com.athena.mis.accounting.utility.AccTier3CacheUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Service class for basic AccTier3 CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'AccTier3Service'
 */
class AccTier3Service extends BaseService {

    static transactional = false

    @Autowired
    AccTier3CacheUtility accTier3CacheUtility

    /**
     * get list of AccTier3 objects
     * @return -list of AccTier3 objects
     */
    public List list() {
        return AccTier3.list(sort: accTier3CacheUtility.SORT_BY_NAME, order: accTier3CacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    /**
     * Save AccTier3 object in database
     * @param accTier3 -AccTier3 object
     * @return -newly created AccTier3 object
     */
    public AccTier3 create(AccTier3 accTier3) {
        AccTier3 newAccTier3 = accTier3.save(false)
        return newAccTier3
    }

    private static final String QUERY_UPDATE = """
                        UPDATE acc_tier3 SET
                            version=:newVersion,
                            name=:name,
                            acc_tier1id =:accTier1Id,
                            acc_tier2id =:accTier2Id,
                            acc_type_id=:accTypeId,
                            is_active=:isActive
                        WHERE id=:id AND
                          version=:version
                          """
    /**
     * SQL to update AccTier3 object in database
     * @param accTier3 -AccTier3 object
     * @return -updated AccTier3 object
     */
    public AccTier3 update(AccTier3 accTier3) {
        Map queryParams = [
                id: accTier3.id,
                version: accTier3.version,
                newVersion: accTier3.version + 1,
                name: accTier3.name,
                accTier1Id: accTier3.accTier1Id,
                accTier2Id: accTier3.accTier2Id,
                accTypeId: accTier3.accTypeId,
                isActive: accTier3.isActive
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update AccTier-3')
        }
        accTier3.version = accTier3.version + 1
        return accTier3
    }

    private static final String QUERY_DELETE = """
                    DELETE FROM acc_tier3
                      WHERE
                          id=:id
                          """
    /**
     * Delete AccTier3 object by id
     * @param id -AccTier3.id
     * @return -boolean value
     */
    public boolean delete(int id) {
        int deleteCount = executeUpdateSql(QUERY_DELETE, [id: id])
        if (deleteCount <= 0) {
            throw new RuntimeException('Failed to delete AccTier-3')
        }
        return true
    }

    /**
     * applicable only for create default tier-3
     */
    public void createDefaultData(long companyId) {
        new AccTier3(name: 'Tier 3 for Asset', accTypeId: 1, accTier1Id: 1, accTier2Id: 1, isActive: true, companyId: companyId).save()
        new AccTier3(name: 'Tier 3 for Liabilities', accTypeId: 2, accTier1Id: 2, accTier2Id: 2, isActive: true, companyId: companyId).save()
        new AccTier3(name: 'Tier 3 for Income', accTypeId: 3, accTier1Id: 3, accTier2Id: 3, isActive: true, companyId: companyId).save()
        new AccTier3(name: 'Tier 3 for Expense', accTypeId: 4, accTier1Id: 4, accTier2Id: 4, isActive: true, companyId: companyId).save()
    }
}
