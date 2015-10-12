package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccSubAccount
import com.athena.mis.accounting.utility.AccSubAccountCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


/**
 *  Service class for basic AccSubAccount CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'AccSubAccountService'
 */
class AccSubAccountService extends BaseService {

    static transactional = false

    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility

    /**
     * get AccSubAccount object by id
     * @param id -AccSubAccount.id
     * @return -AccSubAccount object
     */
    public AccSubAccount read(long id) {
        return (AccSubAccount) accSubAccountCacheUtility.read(id)
    }

    /**
     * get list of AccSubAccount objects
     * @return -list of AccSubAccount objects
     */
    public List list() {
        return AccSubAccount.list(sort: accSubAccountCacheUtility.SORT_ON_DESCRIPTION, order: accSubAccountCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    /**
     * Save AccSubAccount object in database
     * @param accSubAccount -AccSubAccount object
     * @return -newly created AccSubAccount object
     */
    public AccSubAccount create(AccSubAccount accSubAccount) {
        AccSubAccount newAccSubAccount = accSubAccount.save(false)
        return newAccSubAccount
    }

    private static final String QUERY_UPDATE = """
                    UPDATE acc_sub_account SET
                          version=:newVersion,
                          description=:description,
                          coa_id=:coaId,
                          is_active=:isActive,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy
                      WHERE
                          id=:id AND
                          version=:version
                          """
    /**
     * SQL to update AccSubAccount object in database
     * @param accSubAccount -AccSubAccount object
     * @return -updated AccSubAccount object
     */
    public AccSubAccount update(AccSubAccount accSubAccount) {
        Map queryParams = [newVersion: accSubAccount.version + 1,
                description: accSubAccount.description,
                coaId: accSubAccount.coaId,
                id: accSubAccount.id,
                version: accSubAccount.version,
                isActive: accSubAccount.isActive,
                updatedBy: accSubAccount.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(accSubAccount.updatedOn)
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update sub account')
        }
        accSubAccount.version = accSubAccount.version + 1
        return accSubAccount
    }

    private static final String QUERY_DELETE = """
                    DELETE FROM acc_sub_account
                      WHERE
                          id=:id
                          """
    /**
     * Delete AccSubAccount object by id
     * @param id -AccSubAccount.id
     * @return -boolean value
     */
    public boolean delete(long id) {
        int success = executeUpdateSql(QUERY_DELETE, [id: id])
        if (!success) {
            throw new RuntimeException('Failed to delete sub account')
        }
        return true
    }
}
