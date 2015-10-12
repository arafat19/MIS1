package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Owner type used in fixed asset details create.
 * SQL: SELECT * FROM system_entity WHERE type = 551;
 * Entity id range: 551 to 600.
 * e.g. purchased = 132; rental = 133;
 * Reserved Obj: Check cache utility constants.
 **/
@Component('ownerTypeCacheUtility')
class OwnerTypeCacheUtility extends ExtendedCacheUtility {
    @Autowired
    SystemEntityService systemEntityService

    // SystemEntity type (Owner type)
    private static final long ENTITY_TYPE = 551

    // ReservedSystemEntity id
    public static final long PURCHASED = 132
    public static final long RENTAL = 133

    /**
     * initialised cache utility
     * 1. pull system entity objects by entity type.
     * 2. store all objects into in-memory list.
     */
    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE)
        super.setList(list)
    }
}



