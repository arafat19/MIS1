package com.athena.mis.projecttrack.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.service.SystemEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("ptBugTypeCacheUtility")
class PtBugTypeCacheUtility extends ExtendedCacheUtility{

	@Autowired
	SystemEntityService systemEntityService

	// System entity type
	private static final long ENTITY_TYPE = 10712
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
