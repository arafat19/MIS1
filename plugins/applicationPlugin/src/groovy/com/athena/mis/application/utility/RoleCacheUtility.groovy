package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.RoleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('roleCacheUtility')
class RoleCacheUtility extends ExtendedCacheUtility {

    @Autowired
    RoleService roleService

    public static final String AUTHORITY = "authority"
    public static final String NAME = "name"

    public void init() {
        List list = roleService.list();
        super.setList(list)
    }
}
