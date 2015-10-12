package com.athena.mis.inventory.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
/**
 * Determines Inventory transaction type(In/Out/Consumption/Production).
 * SQL: SELECT * FROM system_entity WHERE type = 301;
 * Entity id pick from reserved system entity.
 * Reserved Obj:Check cache utility constants.
 **/
@Component('invTransactionTypeCacheUtility')
class InvTransactionTypeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    // Sys entity type
    private static final long ENTITY_TYPE = 301;
    // Entity id get from reserved system entity
    public final long TRANSACTION_TYPE_IN = 417
    public final long TRANSACTION_TYPE_OUT = 418
    public final long TRANSACTION_TYPE_CONSUMPTION = 419
    public final long TRANSACTION_TYPE_PRODUCTION = 420
    public final long TRANSACTION_TYPE_ADJUSTMENT = 421
    public final long TRANSACTION_TYPE_REVERSE_ADJUSTMENT = 422

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
