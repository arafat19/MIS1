package com.athena.mis.projecttrack.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component("ptBugStatusCacheUtility")
class PtBugStatusCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    // System entity type
    private static final long ENTITY_TYPE = 10710
	private static final String SORT_ON_NAME = "id"

    public static final long SUBMITTED_RESERVED_ID = 1049
    public static final long RE_OPENED_RESERVED_ID = 1050
    public static final long FIXED_RESERVED_ID = 1051
    public static final long CLOSED_RESERVED_ID = 1052

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
