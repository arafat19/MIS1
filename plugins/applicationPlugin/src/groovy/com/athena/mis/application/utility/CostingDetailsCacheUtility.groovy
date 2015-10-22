package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.CostingDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by user on 10/9/15.
 */
@Component('costingDetailsCacheUtility')
class CostingDetailsCacheUtility extends ExtendedCacheUtility {

    @Autowired
    CostingDetailsService costingDetailsService

    static final String SORT_ON_NAME = "name"

    public void init() {
        List list = costingDetailsService.list()
        super.setList(list)
    }

}
