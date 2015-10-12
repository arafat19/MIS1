package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * BankService is used to handle only CRUD related object manipulation (e.g. read, create, update, delete etc.)
 */
class BankService extends BaseService {

    @Autowired
    BankCacheUtility bankCacheUtility

    static transactional = false;

/**
 * Pull object in readOnly mode
 * @param id
 * @return retrieved object
 */

    public read(long id) {
        Bank bank = Bank.read(id)
        return bank
    }

    /**
     * @return - list of bank
     */
    @Transactional(readOnly = true)
    public List<Bank> list() {
        List<Bank> lstEntity = Bank.list(
                sort: bankCacheUtility.DEFAULT_SORT_PROPERTY,
                order: bankCacheUtility.SORT_ORDER_ASCENDING,
                readOnly: true
        )
        return lstEntity
    }

    /**
     * Save bank object into DB
     * @param bank -bank object
     * @return -saved newBank object
     */
    public Bank create(Bank bank) {
        Bank newBank = bank.save()
        return newBank
    }

    public static final String UPDATE_QUERY = """
        UPDATE bank SET
            version=:newVersion,
            name=:name,
            code=:code,
            is_system_bank=:isSystemBank,
            updated_on=:updatedOn,
            updated_by=:updatedBy
        WHERE
            id=:id AND
            version=:oldVersion
    """

    /**
     * Update bank object in DB
     * @param bank -Bank object
     * @return -an integer containing the value of update count
     */
    public Integer update(Bank bank) {
        Map queryParams = [
                newVersion: bank.version + 1,
                name: bank.name,
                code: bank.code,
                id: bank.id,
                isSystemBank: bank.isSystemBank,
                updatedBy: bank.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(bank.updatedOn),
                oldVersion: bank.version
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Bank")
        }

        bank.version = bank.version + 1
        return (new Integer(updateCount));
    }

    private static final String DELETE_QUERY = """
                    DELETE FROM bank WHERE id=:id
                 """

    /**
     * Delete bank object from DB
     * @param id -id of bank object
     * @return -an integer containing the value of update count
     */
    public int delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete bank information')
        }
        return deleteCount;
    }

    /**
     * Used to create Bank
     * @param name - bank name to check duplicate
     * @return - duplicate count
     */
    public int countByName(String name) {
        int count = Bank.countByName(name)
        return count
    }
    /**
     * Used to update Bank
     * @param name - bank name to check duplicate
     * @return - duplicate count
     */
    public int countByNameAndIdNotEqual(String name, long id) {
        int count = Bank.countByNameAndIdNotEqual(name, id)
        return count
    }

    /**
     * Used to create Bank
     * @param name - bank code to check duplicate
     * @return - duplicate count
     */
    public int countByCode(String code) {
        int count = Bank.countByCode(code)
        return count
    }
    /**
     * Used to update Bank
     * @param name - bank code to check duplicate
     * @return - duplicate count
     */
    public int countByCodeAndIdNotEqual(String code, long id) {
        int count = Bank.countByCodeAndIdNotEqual(code, id)
        return count
    }
    /**
     * Used to create Bank
     * @param isSystemBank -  only one bank can be system bank with respect to company
     * @param companyId -companyId
     * @return - count depends on systemBank
     */
    public int countByIsSystemBankAndCompanyId(boolean isSystemBank, long companyId) {
        int count = Bank.countByIsSystemBankAndCompanyId(isSystemBank, companyId)
        return count
    }
    /**
     * Used to update Bank
     * @param isSystemBank -  only one bank can be system bank with respect to company
     * @param companyId -companyId
     * @param id - id of the object
     * @return - count depends on systemBank
     */
    public int countByIsSystemBankAndCompanyIdAndIdNotEqual(boolean isSystemBank, long companyId, long id) {
        int count = Bank.countByIsSystemBankAndCompanyIdAndIdNotEqual(isSystemBank, companyId, id)
        return count
    }

    public void createDefaultData(long companyId) {
        new Bank(version: 0, name: 'Southeast Bank Ltd', code: 'SEBL', companyId: companyId, isSystemBank: true, createdBy: 1, createdOn: new Date(), updatedBy: 0, updatedOn: null).save(flush: true)
        new Bank(version: 0, name: 'Dutch Bangla Bank Ltd', code: 'DBBL', companyId: companyId, isSystemBank: false, createdBy: 1, createdOn: new Date(), updatedBy: 0, updatedOn: null).save(flush: true)
    }
}
