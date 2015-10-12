package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.District
import com.athena.mis.application.service.DistrictService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('districtCacheUtility')
class DistrictCacheUtility extends ExtendedCacheUtility {

//    private static List<District> lstAllValid

    private static District anyDistrict = null      // 'Any district' is a system district
    private static final int ANY_DISTRICT_ID = -10
    public static final String DEFAULT_SORT_PROPERTY = 'name'

    @Autowired
    DistrictService districtService
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility

    // Following method will populate the list of ALL districts from DB
    public void init() {
        List lstItems = districtService.list();
        setList(lstItems)
    }


    public List<District> listDistrictOfValidBranches() {
        List<District> lstDistrictAll = list()
        List<District> lstDistrictValid = []
        boolean isValidDistrict
        for (int i = 1; i < lstDistrictAll.size(); i++) {
            isValidDistrict = bankBranchCacheUtility.isValidDistrict(lstDistrictAll[i].id)
            if (isValidDistrict) {
                lstDistrictValid << lstDistrictAll[i]
            }
        }
        return lstDistrictValid
    }

    public List<District> listByBankId(long bankId) {
        List<District> lstDistrict = []
        if (bankId == 0) {
            lstDistrict = (List<District>) list()
            return lstDistrict
        }
        List<BankBranch> lstBankBranchAll = bankBranchCacheUtility.list()
        List<BankBranch> lstBankBranchFiltered = lstBankBranchAll.findAll { it.bankId == bankId }
        List<Long> lstDistrictIds = lstBankBranchFiltered.collect { it.districtId }
        lstDistrictIds.unique(true)
        lstDistrictIds.each {
            lstDistrict << read(it)
        }
        sort(lstDistrict,DEFAULT_SORT_PROPERTY,SORT_ORDER_ASCENDING)
        return lstDistrict
    }

}


