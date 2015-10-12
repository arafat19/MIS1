package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Calculate valuation (rate) of item in Inventory plugin(e.g- inventory, inventory-transaction, inventory-transaction-details)
 * VALUATION_TYPE_AVG is used for creating and updating of item category for fixed asset
 * SQL: SELECT * FROM system_entity WHERE type = 451;
 * Entity id source: reserved system entity.
 * e.g. fifo = 127; lifo = 128; avg = 129
 * Reserved Obj: Check cache utility constants.
 **/
@Component('valuationTypeCacheUtility')
class ValuationTypeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    // Sys entity type
    private static final long ENTITY_TYPE = 451

    // Entity id get from reserved system entity
    public final long VALUATION_TYPE_FIFO = 127
    public final long VALUATION_TYPE_LIFO = 128
    public final long VALUATION_TYPE_AVG = 129

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
