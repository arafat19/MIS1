package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Create User & Entity Type mapping(e.g- user with project, user with inventory)
 * SQL: SELECT * FROM system_entity WHERE type = 651;
 * Entity id source: reserved system entity(pluginId + sequence).
 * e.g. customer = 136; bankBranch = 137; project = 138
 * Reserved Obj: Check cache utility constants.
 **/
@Component('appUserEntityTypeCacheUtility')
class AppUserEntityTypeCacheUtility extends ExtendedCacheUtility {
    @Autowired
    SystemEntityService systemEntityService

    // Sys entity type
    private static final long ENTITY_TYPE = 651

    // Entity id get from reserved system entity
    public static final long CUSTOMER = 136     //exhCustomer
    public static final long BANK_BRANCH = 137
    public static final long PROJECT = 138
    public static final long PT_PROJECT = 1059
    public static final long INVENTORY = 139
    public static final long GROUP = 140
    public static final long AGENT = 141
    public static final long EXCHANGE_HOUSE = 1186

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
