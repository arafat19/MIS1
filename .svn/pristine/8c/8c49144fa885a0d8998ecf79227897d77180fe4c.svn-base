package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.service.AppUserEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("userBankBranchCacheUtility")
class UserBankBranchCacheUtility extends ExtendedCacheUtility{

    @Autowired
    AppUserEntityService appUserEntityService
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility

    public final String SORT_ON_ID = "id";

    public void init() {
        List list = appUserEntityService.listByType(AppUserEntityTypeCacheUtility.BANK_BRANCH);
        setList(list)
    }

    public List<BankBranch> listUserBankBranches(long userId) {
        List<AppUserEntity> lstUserBankBranch = (List<AppUserEntity>) list()
        List<BankBranch> lstBankBranch = []
        for (int i = 0; i < lstUserBankBranch.size(); i++) {
            if (lstUserBankBranch[i].appUserId == userId) {
                lstBankBranch << bankBranchCacheUtility.read(lstUserBankBranch[i].entityId)
            }
        }
        return lstBankBranch
    }
}
