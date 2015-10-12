package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.AppGroup
import com.athena.mis.application.service.AppGroupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('appGroupCacheUtility')
class AppGroupCacheUtility extends ExtendedCacheUtility {

    @Autowired
    AppGroupService appGroupService

    public final String SORT_ON_NAME = "name"

    public void init() {
        List list = appGroupService.list()
        super.setList(list)
    }
}
