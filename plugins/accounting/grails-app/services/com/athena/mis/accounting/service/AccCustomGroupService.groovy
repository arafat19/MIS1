package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccCustomGroup
import com.athena.mis.accounting.utility.AccCustomGroupCacheUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class AccCustomGroupService extends BaseService {

    static transactional = false

    @Autowired
    AccCustomGroupCacheUtility accCustomGroupCacheUtility


    public List list() {
        return AccCustomGroup.list(sort: accCustomGroupCacheUtility.SORT_BY_NAME, order: accCustomGroupCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    public AccCustomGroup create(AccCustomGroup accCustomGroup) {
        AccCustomGroup newAccCustomGroup = accCustomGroup.save(false)
        return newAccCustomGroup
    }

    private static final String QUERY_UPDATE = """
                        UPDATE acc_custom_group SET
                            version=:newVersion,
                            name=:name,
                            description=:description,
                            is_active=:isActive
                        WHERE id=:id AND
                          version=:version
                          """
    public AccCustomGroup update(AccCustomGroup accCustomGroup) {
        Map queryParams = [
                id: accCustomGroup.id,
                version: accCustomGroup.version,
                newVersion: accCustomGroup.version + 1,
                name: accCustomGroup.name,
                description: accCustomGroup.description,
                isActive: accCustomGroup.isActive
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update custom group')
        }
        accCustomGroup.version = accCustomGroup.version + 1
        return accCustomGroup
    }

    private static final String QUERY_DELETE ="""
                    DELETE FROM acc_custom_group
                      WHERE
                          id=:id
                          """
    public boolean delete(long id) {
        int success = executeUpdateSql(QUERY_DELETE, [id: id])
        if (!success) {
            throw new RuntimeException('Custom group delete failed')
        }
        return true
    }

    public boolean delete(AccCustomGroup accCustomGroup) {
        accCustomGroup.delete()
        return true
    }
}