package com.athena.mis.sarb.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.sarb.service.SarbProvinceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("sarbProvinceCacheUtility")
class SarbProvinceCacheUtility extends ExtendedCacheUtility{

    @Autowired
    SarbProvinceService sarbProvinceService

    public final String SORT_ON_NAME = 'name'

    public void init() {
        List list = sarbProvinceService.list();
        super.setList(list)
    }
}
