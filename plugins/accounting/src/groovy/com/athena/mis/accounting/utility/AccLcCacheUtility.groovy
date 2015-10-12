package com.athena.mis.accounting.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.accounting.service.AccLcService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('accLcCacheUtility')
class AccLcCacheUtility extends ExtendedCacheUtility {
    @Autowired
    AccLcService accLcService

    public static final String SORT_ON_ID = "id"
    public static final String SORT_ORDER_ASCENDING = 'asc'

    public void init() {
        List list = accLcService.list()
        super.setList(list)
    }
}
