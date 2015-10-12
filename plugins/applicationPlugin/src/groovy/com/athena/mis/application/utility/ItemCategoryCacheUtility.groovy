package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Determines item category (e.g- item belongs to inventory/non-inventory/fixed-asset).
 * SQL: SELECT * FROM system_entity WHERE type = 705;
 * Entity id SOURCE: reserved system entity.
 * e.g. inventory = 150; non-inventory = 151; fixed asset = 152
 * Reserved Obj: Check cache utility constants.
 **/
@Component('itemCategoryCacheUtility')
class ItemCategoryCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    // Sys entity type
    private static final long ENTITY_TYPE = 705

    // Entity id get from reserved system entity
    public static final long INVENTORY = 150
    public static final long NON_INVENTORY = 151
    public static final long FIXED_ASSET = 152

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


