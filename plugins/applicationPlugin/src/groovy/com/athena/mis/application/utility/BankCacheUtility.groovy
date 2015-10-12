package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.service.BankService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('bankCacheUtility')
class BankCacheUtility extends ExtendedCacheUtility {


    public static final String DEFAULT_SORT_PROPERTY = 'name'

    @Autowired
    BankService bankService
    @Autowired
    CompanyCacheUtility companyCacheUtility

    // Following method will populate the list of all banks from DB
    public void init() {
        List lstItems = bankService.list()
        setList(lstItems)
    }

    public Bank getSystemBank() {
        List<Bank> lstBank = list()
        Bank bank = null
        for (int i = 0; i < lstBank.size(); i++) {
            if (lstBank[i].isSystemBank.booleanValue()) {
                bank = lstBank[i]
                break
            }
        }
        return bank
    }
}
