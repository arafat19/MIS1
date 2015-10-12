package com.athena.mis.projecttrack.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by user on 2/24/14.
 */
@Component("ptSeverityCacheUtility")
class PtBugSeverityCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SystemEntityService systemEntityService

    // System entity type
    private static final long ENTITY_TYPE = 10709
	private static final String SORT_ON_NAME = "id"

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
