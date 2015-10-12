package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Determines comment entity type (e.g- comment belongs to task/customer).
 * SQL: SELECT * FROM system_entity WHERE type = 703;
 * Entity id source: reserved system entity.
 * e.g. task = 148; customer = 149;
 * Reserved Obj: Check cache utility constants.
 **/
@Component("noteEntityTypeCacheUtility")
class NoteEntityTypeCacheUtility extends ExtendedCacheUtility {
    @Autowired
    SystemEntityService systemEntityService

    // Sys entity type
    private static final long ENTITY_TYPE = 703
    // Entity id get from reserved system entity
    public static final long COMMENT_ENTITY_TYPE_TASK = 148
    public static final long COMMENT_ENTITY_TYPE_CUSTOMER = 149
    public static final long COMMENT_ENTITY_TYPE_PT_TASK = 1094

    public static final long ENTITY_TYPE_RMS_TASK = 1181

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
