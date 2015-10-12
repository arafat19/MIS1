package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccTier2
import com.athena.mis.accounting.utility.AccTier2CacheUtility
import org.springframework.beans.factory.annotation.Autowired

class AccTier2Service extends BaseService {

    static transactional = false

    @Autowired
    AccTier2CacheUtility accTier2CacheUtility

    public List list() {
        return AccTier2.list(sort: accTier2CacheUtility.SORT_BY_NAME, order: accTier2CacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    public AccTier2 create(AccTier2 accTier2) {
        AccTier2 newAccTier2 = accTier2.save(false)
        return newAccTier2
    }

    private static final String QUERY_UPDATE = """
                        UPDATE acc_tier2 SET
                            version=:newVersion,
                            name=:name,
                            acc_type_id=:accTypeId,
                            acc_tier1id=:accTier1Id,
                            is_active=:isActive
                        WHERE id=:id AND
                          version=:version
                          """
    public AccTier2 update(AccTier2 accTier2) {
        Map queryParams = [
                id: accTier2.id,
                version: accTier2.version,
                newVersion: accTier2.version + 1,
                accTier1Id: accTier2.accTier1Id,
                name: accTier2.name,
                accTypeId: accTier2.accTypeId,
                isActive: accTier2.isActive
        ]

        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update AccTier-2')
        }
        accTier2.version = accTier2.version + 1
        return accTier2
    }
    private static final String QUERY_DELETE = """
                    DELETE FROM acc_tier2
                      WHERE
                          id=:id
                          """
    public boolean delete(int id) {
        int deleteCount = executeUpdateSql(QUERY_DELETE, [id: id])
        if (deleteCount <= 0) {
            throw new RuntimeException('Failed to delete AccTier-2')
        }
        return true
    }

    /**
     * applicable only for create default tier-2
     */
    public void createDefaultData(long companyId) {
        new AccTier2(name: 'Tier 2 for Asset', accTypeId: 1, accTier1Id: 1, isActive: true, companyId: companyId).save()
        new AccTier2(name: 'Tier 2 for Liabilities', accTypeId: 2, accTier1Id: 2, isActive: true, companyId: companyId).save()
        new AccTier2(name: 'Tier 2 for Income', accTypeId: 3, accTier1Id: 3, isActive: true, companyId: companyId).save()
        new AccTier2(name: 'Tier 2 for Expense', accTypeId: 4, accTier1Id: 4, isActive: true, companyId: companyId).save()
    }
}
