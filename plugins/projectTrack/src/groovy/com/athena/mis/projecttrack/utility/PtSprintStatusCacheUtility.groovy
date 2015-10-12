package com.athena.mis.projecttrack.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component("ptSprintStatusCacheUtility")
class PtSprintStatusCacheUtility extends ExtendedCacheUtility {
    @Autowired
    SystemEntityService systemEntityService

    private static final long ENTITY_TYPE = 10708

    public static final long DEFINES_RESERVED_ID = 1039
    public static final long IN_PROGRESS_RESERVED_ID = 1040
    public static final long COMPLETED_RESERVED_ID = 1041
    public static final long CLOSED_RESERVED_ID = 1095

	private static final String SORT_ON_NAME = "id"

    public void init() {
        List list = systemEntityService.listByType(ENTITY_TYPE,SORT_ON_NAME)
        setList(list)
    }

}
