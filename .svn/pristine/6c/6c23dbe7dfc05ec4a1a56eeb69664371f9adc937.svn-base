package com.athena.mis.projecttrack.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component("ptAcceptanceCriteriaStatusCacheUtility")
class PtAcceptanceCriteriaStatusCacheUtility extends ExtendedCacheUtility{
	@Autowired
	SystemEntityService systemEntityService

	// Sys entity type
	private static final long ENTITY_TYPE = 10711
	private static final String SORT_ON_NAME = "id"

    public static final long DEFINED_RESERVED_ID = 1042
    public static final long IN_PROGRESS_RESERVED_ID = 1043
    public static final long COMPLETED_RESERVED_ID = 1044
    public static final long BLOCKED_RESERVED_ID = 1045

	public void init() {
		List list = systemEntityService.listByType(ENTITY_TYPE,SORT_ON_NAME)
		super.setList(list)
	}


}
