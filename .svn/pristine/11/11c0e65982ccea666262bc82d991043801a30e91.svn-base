package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class BankBranchService extends BaseService {

    static transactional = true
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility

    @Transactional(readOnly = true)
    public List<BankBranch> list() {
        List<BankBranch> itemList = BankBranch.list([sort: bankBranchCacheUtility.DEFAULT_SORT_PROPERTY, order: bankBranchCacheUtility.SORT_ORDER_ASCENDING, readOnly: true])
        return itemList
    }

    public BankBranch create(BankBranch bankBranch) {
        BankBranch newBankBranch = bankBranch.save()
        return newBankBranch;
    }

    public static final String UPDATE_QUERY = """
        UPDATE bank_branch SET
            version = :newVersion,
            code = :code,
            name = :name,
            address = :address,
            bank_id = :bankId,
            district_id = :districtId,
            is_sme_service_center = :isSmeServiceCenter,
            is_principle_branch = :isPrincipleBranch,
            is_global= :isGlobal,
            updated_on=:updatedOn,
            updated_by=:updatedBy
        WHERE
            id = :id AND
            version = :version
    """
    public Integer update(BankBranch bankBranch) {
        Map queryParams = [
                id: bankBranch.id,
                version: bankBranch.version,
                newVersion: bankBranch.version + 1,
                code: bankBranch.code,
                name: bankBranch.name,
                address: bankBranch.address,
                bankId: bankBranch.bankId,
                districtId: bankBranch.districtId,
                isSmeServiceCenter: bankBranch.isSmeServiceCenter,
                isPrincipleBranch: bankBranch.isPrincipleBranch,
                isGlobal: bankBranch.isGlobal,
                updatedBy: bankBranch.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(bankBranch.updatedOn),
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Bank Branch")
        }
        bankBranch.version = bankBranch.version + 1
        return (new Integer(updateCount))
    }

    public Boolean delete(Long id) {

        BankBranch bankBranchInstance = BankBranch.get(id)
        if (bankBranchInstance == null) {
            return new Boolean(false)
        }

        bankBranchInstance.delete()
        return new Boolean(true)
    }

    public BankBranch read(long id) {
        BankBranch bankBranch = BankBranch.read(id)
        return bankBranch
    }

    public int countByCode(BankBranch bankBranch){
        int count = BankBranch.countByCode(bankBranch.code)
        return count
    }
    public int countByCodeAndIdNotEqual(BankBranch bankBranch){
        int count = BankBranch.countByCodeAndIdNotEqual(bankBranch.code,bankBranch.id)
        return count
    }
    public int countByIsGlobalAndBankIdAndDistrictId(Boolean isGlobal, long bankId,long districtId){
        int count= BankBranch.countByIsGlobalAndBankIdAndDistrictId(isGlobal,bankId,districtId)
        return count
    }
    public int countByIsGlobalAndDistrictIdAndBankIdAndIdNotEqual(Boolean isGlobal,long bankId,long districtId,long bankBranchId){
        int count= BankBranch.countByIsGlobalAndDistrictIdAndBankIdAndIdNotEqual(isGlobal,bankId,districtId,bankBranchId)
        return count
    }
    public void createDefaultData(long companyId) {
        new BankBranch(version: 0, code: 'ANY', name: 'ANY BRANCH', address: 'Bangladesh', bankId: 1L, districtId: 1L, isSmeServiceCenter: false, isPrincipleBranch: false, companyId: companyId,isGlobal: true, createdBy: 1, createdOn: new Date(), updatedBy: 0, updatedOn: null).save(flush: true)
        new BankBranch(version: 0, code: 'GUL', name: 'ANY BRANCH', address: 'ANY BRANCH, Dhaka', bankId: 2L, districtId: 1L, isSmeServiceCenter: false, isPrincipleBranch: false, companyId: companyId,isGlobal: true, createdBy: 1, createdOn: new Date(), updatedBy: 0, updatedOn: null).save(flush: true)
        new BankBranch(version: 0, code: 'BNI', name: 'Banani', address: 'Banani, Dhaka', bankId: 1L, districtId: 2L, isSmeServiceCenter: false, isPrincipleBranch: false, companyId: companyId, isGlobal: true, createdBy: 1, createdOn: new Date(), updatedBy: 0, updatedOn: null).save(flush: true)
        new BankBranch(version: 0, code: 'DBBL', name: 'Gulshan', address: 'Gulshan, Dhaka', bankId: 2L, districtId: 2L, isSmeServiceCenter: false, isPrincipleBranch: false, companyId: companyId,isGlobal: false, createdBy: 1, createdOn: new Date(), updatedBy: 0, updatedOn: null).save(flush: true)
        new BankBranch(version: 0, code: 'CHT', name: 'Chittagong', address: 'Chittagong', bankId: 1L, districtId: 3L, isSmeServiceCenter: true, isPrincipleBranch: false, companyId: companyId,isGlobal: false, createdBy: 1, createdOn: new Date(), updatedBy: 0, updatedOn: null).save(flush: true)
        new BankBranch(version: 0, code: 'PB', name: 'Principal Branch', address: 'Dhaka', bankId: 1L, districtId: 2L, isSmeServiceCenter: false, isPrincipleBranch: true, companyId: companyId,isGlobal: false, createdBy: 1, createdOn: new Date(), updatedBy: 0, updatedOn: null).save(flush: true)
    }
}
