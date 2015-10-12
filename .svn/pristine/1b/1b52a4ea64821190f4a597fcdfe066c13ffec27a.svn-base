package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
/**
 * Create Content & Entity Type mapping(e.g- content belongs to project or company)
 * SQL: SELECT * FROM system_entity WHERE type = 701;
 * Entity id source: reserved system entity.
 * e.g. appUser = 142; company = 143; project = 144
 * Reserved Obj: Check cache utility constants.
 **/
@Component('contentEntityTypeCacheUtility')
class ContentEntityTypeCacheUtility extends ExtendedCacheUtility {
    @Autowired
    SystemEntityService systemEntityService

    // Sys entity type
    private static final long ENTITY_TYPE = 701

    // Entity id get from reserved system entity
    public static final long CONTENT_ENTITY_TYPE_APPUSER = 142
    public static final long CONTENT_ENTITY_TYPE_COMPANY = 143
    public static final long CONTENT_ENTITY_TYPE_EXH_CUSTOMER = 144
    public static final long CONTENT_ENTITY_TYPE_PROJECT = 145
    public static final long CONTENT_ENTITY_TYPE_PT_BUG = 1058
    public static final long CONTENT_ENTITY_TYPE_BUDGET = 184
    public static final long CONTENT_ENTITY_TYPE_BUDG_SPRINT = 192
    public static final long CONTENT_ENTITY_TYPE_FINANCIAL_YEAR = 185

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
