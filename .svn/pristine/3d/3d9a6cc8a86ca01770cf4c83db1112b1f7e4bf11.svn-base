package com.athena.mis.integration.projecttrack

import com.athena.mis.PluginConnector

public abstract class ProjectTrackPluginConnector extends PluginConnector {
	//init Account SysConfiguration
	public abstract void initBacklogPriorityCacheUtility()

	public abstract void initBacklogStatusCacheUtility()

	public abstract void initAcceptanceCriteriaStatusCacheUtility()

	public abstract void initBugSeverityCacheUtility()

	public abstract void initBugStatusCacheUtility()

	public abstract void initBugTypeCacheUtility()

	public abstract void initAcceptanceCriteriaTypeCacheUtility()

	public abstract initSprintStatusCacheUtility()

	public abstract void bootStrap(boolean isSchema, boolean isData)

	public abstract List listBacklogPriority ()

	public abstract List listBacklogStatus ()
    public abstract List listSprintStatus()

	public abstract List listAcceptanceCriteriaStatus ()

    public abstract List listBugSeverity()

    public abstract List listBugStatus()

	public abstract List listBugType()

    public abstract List listAcceptanceCriteriaType()

    //read ptProject object by id
    public abstract Object readPtProject(long id)

    // get reserved system entity of backlog priority type
    public abstract Object readByReservedBacklogPriorityType(long reservedId, long companyId)

    // get reserved system entity of backlog status type
    public abstract Object readByReservedBacklogStatusType(long reservedId, long companyId)

    // get reserved system entity of sprint status type
    public abstract Object readByReservedSprintStatusType(long reservedId, long companyId)

    // get reserved system entity of acceptance criteria status type
    public abstract Object readByReservedAcceptanceCriteriaStatusType(long reservedId, long companyId)

    // get reserved system entity of bug severity type
    public abstract Object readByReservedBugSeverityType(long reservedId, long companyId)

    // get reserved system entity of bug status type
    public abstract Object readByReservedBugStatusType(long reservedId, long companyId)

    // get reserved system entity of bug type
    public abstract Object readByReservedBugType(long reservedId, long companyId)

    // get reserved system entity of acceptance criteria type
    public abstract Object readByReservedAcceptanceCriteriaType(long reservedId, long companyId)

    // read object of task(backlog)
    public abstract Object readTask(long id)
}
