package com.athena.mis.accounting.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.accounting.service.AccLeaseAccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('accLeaseAccountCacheUtility')
class AccLeaseAccountCacheUtility extends ExtendedCacheUtility {
    @Autowired
    AccLeaseAccountService accLeaseAccountService

    public static final String SORT_BY_INSTITUTION = 'institution'

    public void init() {
        List list = accLeaseAccountService.list()
        super.setList(list)
    }
}
