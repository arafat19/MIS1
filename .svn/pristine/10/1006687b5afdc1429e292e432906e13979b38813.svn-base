package com.athena.mis.inventory.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('invTransactionEntityTypeCacheUtility')
class InvTransactionEntityTypeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    // SystemEntity type (Inventory Transaction Entity Type)
    private static final long ENTITY_TYPE = 351;

    // Entity id get from reserved system entity
    public final long ENTITY_TYPE_INVENTORY = 423
    public final long ENTITY_TYPE_SUPPLIER = 424
    public final long ENTITY_TYPE_NONE = 425
    public final long ENTITY_TYPE_CUSTOMER = 426

    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE)
        super.setList(list)
    }
}
