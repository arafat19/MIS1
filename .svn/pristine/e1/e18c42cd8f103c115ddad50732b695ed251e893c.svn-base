package com.athena.mis.accounting.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.accounting.service.AccIpcService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('accIpcCacheUtility')
class AccIpcCacheUtility extends ExtendedCacheUtility {
    @Autowired
    AccIpcService accIpcService

    public static final String SORT_BY_ID = 'id'

    public void init() {
        List list = accIpcService.list()
        super.setList(list)
    }
}
