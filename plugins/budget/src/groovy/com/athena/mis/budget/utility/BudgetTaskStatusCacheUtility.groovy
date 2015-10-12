package com.athena.mis.budget.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('budgetTaskStatusCacheUtility')
class BudgetTaskStatusCacheUtility extends ExtendedCacheUtility {
    @Autowired
    SystemEntityService systemEntityService

    private static final String SORT_ON_ID = "id"

    // Sys entity type
    private static final long ENTITY_TYPE = 3721

    public static final long DEFINED_RESERVED_ID = 389
    public static final long IN_PROGRESS_RESERVED_ID = 390
    public static final long COMPLETED_RESERVED_ID = 391

    /**
     * initialised cache utility
     * 1. pull system entity objects by entity type.
     * 2. store all objects into in-memory list.
     */
    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE, SORT_ON_ID)
        super.setList(list)
    }

}
