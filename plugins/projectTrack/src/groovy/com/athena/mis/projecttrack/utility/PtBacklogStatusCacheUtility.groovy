package com.athena.mis.projecttrack.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("ptBacklogStatusCacheUtility")
class PtBacklogStatusCacheUtility extends ExtendedCacheUtility {
    @Autowired
    SystemEntityService systemEntityService

    // Sys entity type
    private static final long ENTITY_TYPE = 10707;
    private static final String SORT_ON_ID = "id"

    public static final long DEFINED_RESERVED_ID = 1035
    public static final long IN_PROGRESS_RESERVED_ID = 1036
    public static final long COMPLETED_RESERVED_ID = 1037
    public static final long ACCEPTED_RESERVED_ID = 1038

    /**
     * initialised cache utility
     * 1. pull system entity objects by entity type.
     * 2. store all objects into in-memory list.
     */
    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE, SORT_ON_ID)
        super.setList(list)
    }

    /**
     * Get list of all status ids
     * @return -list of ids
     */
    public List<Long> listOfAllStatusIds() {
        List lstStatusIds = []
        List<SystemEntity> lstStatus = list()
        for (int i = 0; i < lstStatus.size(); i++) {
            lstStatusIds << lstStatus[i].id
        }
        return lstStatusIds
    }
}
