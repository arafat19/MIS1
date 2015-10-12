package com.athena.mis.arms.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by user on 3/16/14.
 */
@Component("rmsProcessTypeCacheUtility")
class RmsProcessTypeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    // System entity type
    public static final long ENTITY_TYPE = 11713
    public static final String SORT_ON_NAME = "id"

    // Reserved System Entity id
    public static final long ISSUE = 1150
    public static final long FORWARD = 1151
    public static final long PURCHASE = 1152

    /**
     * initialised cache utility
     * 1. pull system entity objects by entity type.
     * 2. store all objects into in-memory list.
     */
    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE,SORT_ON_NAME)
        super.setList(list)
    }
}
