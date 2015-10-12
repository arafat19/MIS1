package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccTier1
import com.athena.mis.accounting.utility.AccTier1CacheUtility
import org.springframework.beans.factory.annotation.Autowired

class AccTier1Service extends BaseService {

    static transactional = false

    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    /**
     * @return - list of tier1
     */
    public List list() {
        return AccTier1.list(sort: accTier1CacheUtility.SORT_BY_NAME, order: accTier1CacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }
    /**
     * Create tier1 object
     * @param accTier1 -tier1 object
     * @return -tier1 object
     */
    public AccTier1 create(AccTier1 accTier1) {
        AccTier1 newAccTier1 = accTier1.save(false)
        return newAccTier1
    }

    private static final String QUERY_UPDATE = """
                        UPDATE acc_tier1 SET
                            version=:newVersion,
                            name=:name,
                            acc_type_id=:accTypeId,
                            is_active=:isActive
                        WHERE id=:id AND
                          version=:version
                          """
    /**
     * Update tier1 object
     * @param accTier1 -tier1 object
     * @return -tier1 object
     */
    public AccTier1 update(AccTier1 accTier1) {
        Map queryParams = [
                id: accTier1.id,
                version: accTier1.version,
                newVersion: accTier1.version + 1,
                name: accTier1.name,
                accTypeId: accTier1.accTypeId,
                isActive: accTier1.isActive
        ]

        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update AccTier1')
        }
        accTier1.version = accTier1.version + 1
        return accTier1
    }

    private static final String QUERY_DELETE = """
                     DELETE FROM acc_tier1
                       WHERE
                           id=:id
                           """
    /**
     * Delete tier1 object
     * @param id -id of tier1 object
     * @return -boolean value true
     */
    public boolean delete(int id) {
        int updateCount = executeUpdateSql(QUERY_DELETE, [id: id])
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to delete AccTier-1')
        }
        return true
    }

    /**
     * applicable only for create default tier-1
     */
    public void createDefaultData(companyId) {
        new AccTier1(name: 'Tier 1 for Asset', accTypeId: 1, isActive: true, companyId: companyId).save()
        new AccTier1(name: 'Tier 1 for Liabilities', accTypeId: 2, isActive: true, companyId: companyId).save()
        new AccTier1(name: 'Tier 1 for Income', accTypeId: 3, isActive: true, companyId: companyId).save()
        new AccTier1(name: 'Tier 1 for Expense', accTypeId: 4, isActive: true, companyId: companyId).save()
    }
}
