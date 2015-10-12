package com.athena.mis.sarb.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("sarbTaskReviseStatusCacheUtility")
class SarbTaskReviseStatusCacheUtility extends ExtendedCacheUtility{

    @Autowired
    SystemEntityService systemEntityService

    public static final long ENTITY_TYPE = 12723

    public static final long MOVED_FOR_CANCEL = 1298
    public static final long MOVED_FOR_REPLACE = 1299
    public static final long MOVED_FOR_REFUND = 12100

    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE)
        super.setList(list)
    }
}
