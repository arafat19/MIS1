package com.athena.mis.projecttrack.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("ptAcceptanceCriteriaTypeCacheUtility")
class PtAcceptanceCriteriaTypeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    // System entity type
    private static final long ENTITY_TYPE = 10718
    private static final String SORT_ON_ID = "id"

    public static final long PRE_CONDITION_RESERVED_ID = 1078
    public static final long BUSINESS_LOGIC_RESERVED_ID = 1079
    public static final long POST_CONDITION_RESERVED_ID = 1080
    public static final long OTHERS_RESERVED_ID = 1096

    /**
     * Initialized cache utility
     * 1. pull system entity objects by entity type.
     * 2. store all objects into in-memory list.
     */
    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE,SORT_ON_ID)
        super.setList(list)
    }
}
