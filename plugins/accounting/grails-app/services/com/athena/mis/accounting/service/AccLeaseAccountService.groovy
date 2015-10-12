package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccLeaseAccount
import com.athena.mis.accounting.utility.AccLeaseAccountCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Service class for basic AccLeaseAccount CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'AccLeaseAccountService'
 */
class AccLeaseAccountService extends BaseService {

    @Autowired
    AccLeaseAccountCacheUtility accLeaseAccountCacheUtility

    static transactional = false

    /**
     * get list of AccLeaseAccount objects
     * @return -list of AccLeaseAccount objects
     */
    public List list() {
        return AccLeaseAccount.list(sort: accLeaseAccountCacheUtility.SORT_BY_INSTITUTION, order: accLeaseAccountCacheUtility.SORT_ORDER_ASCENDING);
    }

    /**
     * get AccLeaseAccount object by id
     * @param id -AccLeaseAccount.id
     * @return -AccLeaseAccount object
     */
    public AccLeaseAccount read(long id) {
        AccLeaseAccount accLeaseAccount = AccLeaseAccount.read(id)
        return accLeaseAccount
    }

    /**
     * Save AccLeaseAccount object in database
     * @param accLeaseAccount -AccLeaseAccount object
     * @return -newly created AccLeaseAccount object
     */
    public AccLeaseAccount create(AccLeaseAccount accLeaseAccount) {
        AccLeaseAccount newAccLeaseAccount = accLeaseAccount.save(false)
        return newAccLeaseAccount
    }

    private static final String QUERY_UPDATE = """
                            UPDATE acc_lease_account
                            SET
                                  version=:newVersion,
                                  institution=:institution,
                                  interest_rate=:interestRate,
                                  no_of_installment=:noOfInstallment,
                                  installment_volume=:installmentVolume,
                                  amount=:amount,
                                  item_id=:itemId,
                                  updated_by=:updatedBy,
                                  start_date=:startDate,
                                  end_date=:endDate,
                                  updated_on=:updatedOn
                            WHERE
                                  id=:id AND
                                  version=:version
                                  """
    /**
     * SQL to update AccLeaseAccount object in database
     * @param accLeaseAccount -AccLeaseAccount object
     * @return -updated AccLeaseAccount object
     */
    public int update(AccLeaseAccount accLeaseAccount) {
        Map queryParams = [
                id: accLeaseAccount.id,
                version: accLeaseAccount.version,
                newVersion: accLeaseAccount.version + 1,
                institution: accLeaseAccount.institution,
                interestRate: accLeaseAccount.interestRate,
                noOfInstallment: accLeaseAccount.noOfInstallment,
                installmentVolume: accLeaseAccount.installmentVolume,
                amount: accLeaseAccount.amount,
                itemId: accLeaseAccount.itemId,
                updatedBy: accLeaseAccount.updatedBy,
                startDate: DateUtility.getSqlDate(accLeaseAccount.startDate),
                endDate: DateUtility.getSqlDate(accLeaseAccount.endDate),
                updatedOn: DateUtility.getSqlDateWithSeconds(accLeaseAccount.updatedOn)
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating Lease Account information')
        }
        return updateCount
    }

    private static final String QUERY_DELETE = """
                     DELETE FROM acc_lease_account
                       WHERE
                           id=:id
                           """
    /**
     * Delete AccLeaseAccount object by id
     * @param id -AccLeaseAccount.id
     * @return -boolean value
     */
    public boolean delete(long id) {
        int updateCount = executeUpdateSql(QUERY_DELETE, [id: id])
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to delete Lease Account')
        }
        return true
    }
}
