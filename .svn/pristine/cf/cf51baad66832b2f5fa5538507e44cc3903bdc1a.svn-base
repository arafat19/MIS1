package com.athena.mis.inventory.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("invProductionItemTypeCacheUtility")
class InvProductionItemTypeCacheUtility extends ExtendedCacheUtility {
    @Autowired
    SystemEntityService systemEntityService

    // SystemEntity type (Inventory Production Item type)
    private static final long ENTITY_TYPE = 151

    // Entity id get from reserved system entity
    public static final int TYPE_RAW_MATERIAL_ID = 415
    public static final int TYPE_FINISHED_MATERIAL_ID = 416

    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE)
        super.setList(list)
    }
}
