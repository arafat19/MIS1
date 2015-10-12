package com.athena.mis.integration.projecttrack

import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.utility.*
import org.springframework.beans.factory.annotation.Autowired

class ProjectTrackImplService extends ProjectTrackPluginConnector {

    PtBacklogService ptBacklogService
    @Autowired
    PtBacklogPriorityCacheUtility ptBacklogPriorityCacheUtility
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility
    @Autowired
    PtAcceptanceCriteriaStatusCacheUtility ptAcceptanceCriteriaStatusCacheUtility
    @Autowired
    PtBugSeverityCacheUtility ptBugSeverityCacheUtility
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility
    @Autowired
    PtSprintStatusCacheUtility ptSprintStatusCacheUtility
    @Autowired
    PtBugTypeCacheUtility ptBugTypeCacheUtility
    @Autowired
    PtAcceptanceCriteriaTypeCacheUtility ptAcceptanceCriteriaTypeCacheUtility
    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility

    static transactional = false
    static lazyInit = false

    ProjectTrackBootStrapService projectTrackBootStrapService
    ProjectTrackDefaultDataBootStrapService projectTrackDefaultDataBootStrapService
    PtSchemaUpdateBootStrapService ptSchemaUpdateBootStrapService

    @Override
    public boolean initialize() {
        setPlugin(PROJECT_TRACK, this);
        return true
    }

    @Override
    public String getName() {
        return PROJECT_TRACK;
    }

    @Override
    public int getId() {
        return PROJECT_TRACK_ID;
    }

    //read ptProject object by id
    Object readPtProject(long id) {
        return ptProjectCacheUtility.read(id)
    }

    public void initBacklogPriorityCacheUtility() {
        ptBacklogPriorityCacheUtility.init()
    }

    public void initBacklogStatusCacheUtility() {
        ptBacklogStatusCacheUtility.init()
    }

    public void initAcceptanceCriteriaStatusCacheUtility() {
        ptAcceptanceCriteriaStatusCacheUtility.init()
    }

    public void initBugSeverityCacheUtility() {
        ptBugSeverityCacheUtility.init()
    }

    public void initBugStatusCacheUtility() {
        ptBugStatusCacheUtility.init()
    }

    public void initBugTypeCacheUtility() {
        ptBugTypeCacheUtility.init()
    }

    public void initAcceptanceCriteriaTypeCacheUtility() {
        ptAcceptanceCriteriaTypeCacheUtility.init()
    }

    public initSprintStatusCacheUtility() {
        ptSprintStatusCacheUtility.init()
    }

    public List listBacklogPriority() {
        return ptBacklogPriorityCacheUtility.listByIsActive()
    }

    public List listBacklogStatus() {
        return ptBacklogStatusCacheUtility.listByIsActive()
    }

    public List listAcceptanceCriteriaStatus() {
        return ptAcceptanceCriteriaStatusCacheUtility.listByIsActive()
    }

    public List listSprintStatus() {
        return ptSprintStatusCacheUtility.listByIsActive()
    }

    public List listBugSeverity() {
        return ptBugSeverityCacheUtility.listByIsActive()
    }

    public List listBugStatus() {
        return ptBugStatusCacheUtility.listByIsActive()
    }

    public List listBugType() {
        return ptBugTypeCacheUtility.listByIsActive()
    }

    public List listAcceptanceCriteriaType() {
        return ptAcceptanceCriteriaTypeCacheUtility.listByIsActive()
    }

    // get reserved system entity of backlog priority type
    public Object readByReservedBacklogPriorityType(long reservedId, long companyId) {
        return ptBacklogPriorityCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get reserved system entity of backlog status type
    public Object readByReservedBacklogStatusType(long reservedId, long companyId) {
        return ptBacklogStatusCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get reserved system entity of sprint status type
    public Object readByReservedSprintStatusType(long reservedId, long companyId) {
        return ptSprintStatusCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get reserved system entity of acceptance criteria status type
    public Object readByReservedAcceptanceCriteriaStatusType(long reservedId, long companyId) {
        return ptAcceptanceCriteriaStatusCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get reserved system entity of bug severity type
    public Object readByReservedBugSeverityType(long reservedId, long companyId) {
        return ptBugSeverityCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get reserved system entity of bug status type
    public Object readByReservedBugStatusType(long reservedId, long companyId) {
        return ptBugStatusCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get reserved system entity of bug type
    public Object readByReservedBugType(long reservedId, long companyId) {
        return ptBugTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    // get reserved system entity of Acceptance Criteria type
    public Object readByReservedAcceptanceCriteriaType(long reservedId, long companyId) {
        return ptAcceptanceCriteriaTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }

    public void bootStrap(boolean isSchema, boolean isData) {
        if (isData) projectTrackDefaultDataBootStrapService.init()
        if (isSchema) ptSchemaUpdateBootStrapService.init()
        projectTrackBootStrapService.init()
    }

    // read object of task(backlog)
    public Object readTask(long id) {
        return ptBacklogService.read(id)
    }
}
