package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.CostingDetailsService
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by user on 10/9/15.
 */
class CostingDetailsCacheUtility extends ExtendedCacheUtility {

    @Autowired
    CostingDetailsService costingDetailsService

    static final String SORT_ON_NAME = "name"

    public void init() {
        List list = costingDetailsService.list()
        super.setList(list)
    }

}
