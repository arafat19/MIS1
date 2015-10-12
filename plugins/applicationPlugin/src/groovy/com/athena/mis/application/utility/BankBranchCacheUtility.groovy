package com.athena.mis.application.utility

import com.athena.mis.BaseService
import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.service.BankBranchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('bankBranchCacheUtility')
class BankBranchCacheUtility extends ExtendedCacheUtility {

    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    BankBranchService bankBranchService
    @Autowired
    DistrictCacheUtility districtCacheUtility

    public static final String DEFAULT_SORT_PROPERTY = 'name'
    private static BankBranch anyBankBranch = null      // 'Any BankBranch' is a system district
    private static final int ANY_BRANCH_ID = 1

    // Following method will populate the list of ALL bankBranches from DB
    public void init() {
        List lstItems = bankBranchService.list();
        setList(lstItems)
    }

    private static final String FIELD_BANK = "bankId"
    private static final String FIELD_DISTRICT = "districtId"

    // following method returns list and count based on search query: used in BankBranch grid searching
    public Map searchByField(String fieldName, String query, List<BankBranch> branchList, BaseService baseService) {
        branchList = branchList.findAll {
            if (fieldName.equals(FIELD_BANK)) {
                String bankName = bankCacheUtility.read(it.bankId).name
                bankName ==~ /(?i).*${query}.*/
            } else if (fieldName.equals(FIELD_DISTRICT)) {
                String distName = districtCacheUtility.read(it.districtId).name
                distName ==~ /(?i).*${query}.*/
            } else {
                it.properties.get(fieldName) ==~ /(?i).*${query}.*/
            }
        }
        int end = branchList.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : branchList.size()
        List lstResult = branchList.subList(baseService.start, end)
        return [list: lstResult, count: branchList.size()]
    }

    // Return list of Bank Branches based on bank and district
    public List<BankBranch> listByBankAndDistrict(Long bankId, Long districtId) {
        List<BankBranch> lstBranches = []
        if ((bankId == null) || (districtId == null)) return lstBranches
        if ((bankId == -1) || (districtId == -1)) return lstBranches
        List lstAllBranches = list()
        for (int i = 0; i < lstAllBranches.size(); i++) {
            if ((lstAllBranches[i].bankId == bankId) && (lstAllBranches[i].districtId == districtId)) {
                lstBranches << lstAllBranches[i]
            }
        }
        return lstBranches
    }

    // Return list of Bank Branches based on district
    public boolean isValidDistrict(Long districtId) {
        if ((districtId == -1) || (districtId == null)) return false

        List lstAllBranches = list()

        for (int i = 0; i < lstAllBranches.size(); i++) {
            if (lstAllBranches[i].districtId == districtId) {
                return true
            }
        }
        return false
    }

    // Return principle bankBranch of a bank
    public BankBranch getPrincipleBankBranch(Long bankId) {
        List lstAllBranches = list()
        for (int i = 0; i < lstAllBranches.size(); i++) {
            BankBranch bankBranch = (BankBranch) lstAllBranches[i]
            if ((bankBranch.bankId == bankId) && bankBranch.isPrincipleBranch) {
                return bankBranch
            }
        }
        return null
    }


    public boolean isPrincipleBankBranch(Long bankBranchId) {
        BankBranch bankBranch = (BankBranch) read(bankBranchId)
        return bankBranch.isPrincipleBranch
    }

    public boolean isSmeServiceCenter(Long bankBranchId) {
        BankBranch bankBranch = (BankBranch) read(bankBranchId)
        return bankBranch.isSmeServiceCenter
    }
}