package com.athena.mis.exchangehouse.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
/**
 *
 * SQL: SELECT * FROM system_entity WHERE type = 2001;
 * Entity id range: 2000001 to 2000100.
 * e.g. cash = 950; online = 951; card = 952;
 * Reserved Obj: N/A.
 **/
@Component("exhPaidByCacheUtility")
class ExhPaidByCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    // Sys entity type
    private static final long ENTITY_TYPE = 2001;
    // Entity id get from reserved system entity
    public static final long PAID_BY_ID_CASH = 950
    public static final long PAID_BY_ID_ONLINE = 951
    public static final long PAID_BY_ID_PAY_POINT = 952

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
