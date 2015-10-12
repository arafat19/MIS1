package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
/**
 * Determines content type (e.g- content is document or image).
 * SQL: SELECT * FROM system_entity WHERE type = 702;
 * Entity id source: reserved system entity.
 * e.g. image = 147;
 **/
@Component('contentTypeCacheUtility')
class ContentTypeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    // Sys entity type
    private static final long ENTITY_TYPE = 702
    // Entity id get from reserved system entity
    public static final long CONTENT_TYPE_DOCUMENT_ID = 146
    public static final long CONTENT_TYPE_IMAGE_ID = 147

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

