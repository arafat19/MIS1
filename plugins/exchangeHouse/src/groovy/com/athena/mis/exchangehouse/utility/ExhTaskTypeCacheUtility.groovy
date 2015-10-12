package com.athena.mis.exchangehouse.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
/**
 *
 * SQL: SELECT * FROM system_entity WHERE type = 2004;
 * Entity id range: 2000301 to 2000320.
 * e.g. exh-task = 963; agent-task = 964; customer-task = 965;
 * Reserved Obj: N/A.
 **/
@Component("exhTaskTypeCacheUtility")
class ExhTaskTypeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    // Sys entity type
    private static final long ENTITY_TYPE = 2004;

    // Entity id get from reserved system entity
    public static final long TYPE_EXH_TASK = 963;
    public static final long TYPE_AGENT_TASK = 964;
    public static final long TYPE_CUSTOMER_TASK = 965;


    /**
     * initialised cache utility
     * 1. pull system entity objects by entity type.
     * 2. store all objects into in-memory list.
     */
    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE)
        setList(list)
    }
}
