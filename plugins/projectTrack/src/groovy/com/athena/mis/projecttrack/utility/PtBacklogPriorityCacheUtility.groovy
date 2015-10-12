package com.athena.mis.projecttrack.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('ptBacklogPriorityCacheUtility')
class PtBacklogPriorityCacheUtility extends ExtendedCacheUtility {

	@Autowired
	SystemEntityService systemEntityService

	// Sys entity type
	private static final long ENTITY_TYPE = 10706
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
