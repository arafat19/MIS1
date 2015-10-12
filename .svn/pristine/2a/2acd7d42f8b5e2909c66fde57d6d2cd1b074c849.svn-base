package com.athena.mis.integration.projecttrack

import com.athena.mis.projecttrack.utility.PtAcceptanceCriteriaStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtAcceptanceCriteriaTypeCacheUtility
import com.athena.mis.projecttrack.utility.PtBacklogPriorityCacheUtility
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtBugStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtBugTypeCacheUtility
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.projecttrack.utility.PtBugSeverityCacheUtility
import com.athena.mis.projecttrack.utility.PtProjectCacheUtility
import com.athena.mis.projecttrack.utility.PtProjectModuleCacheUtility
import com.athena.mis.projecttrack.utility.PtSprintStatusCacheUtility
import org.springframework.beans.factory.annotation.Autowired

class ProjectTrackBootStrapService {

	@Autowired
	PtModuleCacheUtility ptModuleCacheUtility
	@Autowired
	PtBacklogPriorityCacheUtility ptBacklogPriorityCacheUtility
	@Autowired
	PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility
	@Autowired
    PtAcceptanceCriteriaTypeCacheUtility ptAcceptanceCriteriaTypeCacheUtility
	@Autowired
	PtAcceptanceCriteriaStatusCacheUtility ptAcceptanceCriteriaStatusCacheUtility
    @Autowired
    PtBugSeverityCacheUtility ptBugSeverityCacheUtility
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility
    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility
    @Autowired
    PtProjectModuleCacheUtility ptProjectModuleCacheUtility
    @Autowired
    PtSprintStatusCacheUtility ptSprintStatusCacheUtility
	@Autowired
	PtBugTypeCacheUtility ptBugTypeCacheUtility

	public void init () {
		initAllUtility()
	}

	private void initAllUtility() {
		ptModuleCacheUtility.init()
		ptBacklogPriorityCacheUtility.init()
		ptBacklogStatusCacheUtility.init()
		ptAcceptanceCriteriaStatusCacheUtility.init()
        ptAcceptanceCriteriaTypeCacheUtility.init()
        ptBugSeverityCacheUtility.init()
        ptBugStatusCacheUtility.init()
        ptProjectCacheUtility.init()
        ptProjectModuleCacheUtility.init()
        ptSprintStatusCacheUtility.init()
		ptBugTypeCacheUtility.init()
	}
}
