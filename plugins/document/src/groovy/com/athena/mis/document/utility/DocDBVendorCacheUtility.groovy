package com.athena.mis.document.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Used as a drop down in Database Vendor (used in Document plugins)
 * SQL: SELECT * FROM system_entity WHERE type = 13722;
 **/
@Component('docDBVendorCacheUtility')
class DocDBVendorCacheUtility extends ExtendedCacheUtility {
    @Autowired
    SystemEntityService systemEntityService

    // Sys entity type
    private static final long ENTITY_TYPE = 13722;

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




















