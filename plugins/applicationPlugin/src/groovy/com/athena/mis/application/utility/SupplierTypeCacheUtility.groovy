package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
/**
 * Supplier type used to populate supplier type drop-down in create supplier(application plugin).
 * SQL: SELECT * FROM system_entity WHERE type = 704;
 * Entity id range: 1801 to 1900.
 * e.g. material-provider = 1801; service-provider = 1802; material-service-provider = 1803;
 * Reserved Obj: N/A.
 **/
@Component('supplierTypeCacheUtility')
class SupplierTypeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    // Sys entity type
    private static final long ENTITY_TYPE = 704

    public static final long MATERIAL_PROVIDER = 1801

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

