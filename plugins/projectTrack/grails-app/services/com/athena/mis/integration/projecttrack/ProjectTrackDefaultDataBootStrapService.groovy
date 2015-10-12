package com.athena.mis.integration.projecttrack

import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.*
import com.athena.mis.projecttrack.service.PtModuleService
import com.athena.mis.projecttrack.service.PtProjectService
import org.hibernate.SessionFactory
import org.springframework.transaction.annotation.Transactional

class ProjectTrackDefaultDataBootStrapService {

	SessionFactory sessionFactory
	SystemEntityTypeService systemEntityTypeService
	SystemEntityService systemEntityService
	ReservedSystemEntityService reservedSystemEntityService
    RoleService roleService
    RoleTypeService roleTypeService
	RequestMapService requestMapService
	RoleFeatureMappingService roleFeatureMappingService
    AppMailService appMailService
    AppUserService appUserService
    UserRoleService userRoleService
	PtModuleService ptModuleService
	PtProjectService ptProjectService

	@Transactional
	public void init() {

        Company defaultCompany = Company.list(readOnly: true)[0]    // read the only object
        long companyId = defaultCompany.id

		systemEntityTypeService.createDefaultDataForProjectTrack()
		reservedSystemEntityService.createDefaultDataForPT()
		systemEntityService.createDefaultContentEntityTypeForPT(companyId)
		systemEntityService.createDefaultDataBacklogPriorityForPT(companyId)
		systemEntityService.createDefaultDataBacklogStatusForPT(companyId)
		systemEntityService.createDefaultDataAcceptanceCriteriaStatusForPT(companyId)
		systemEntityService.createDefaultDataBugSeverityForPT(companyId)
		systemEntityService.createDefaultDataBugStatusForPT(companyId)
        systemEntityService.createDefaultDataSprintStatusForPt(companyId)
		systemEntityService.createDefaultDataBugTypeForPT(companyId)
		systemEntityService.createDefaultDataAcceptanceCriteriaTypeForPT(companyId)
		systemEntityService.createDefaultNoteEntityTypeForPT(companyId)
        appUserService.createDefaultDataForProjectTrack(companyId)
        roleTypeService.createDefaultDataForPT()
        roleService.createDefaultDataForPT(companyId)
		requestMapService.createRequestMapForProjectTrack()
		roleFeatureMappingService.createRoleFeatureMapForProjectTrackPlugin()

		flushSession()
		requestMapService.appendRoleInRequestMap(PluginConnector.PROJECT_TRACK_ID)
        userRoleService.createDefaultDataForProjectTrack()
		ptModuleService.createDefaultDataForPtModule(companyId)
		ptProjectService.createDefaultDataForPtProject(companyId)
        appMailService.createDefaultDataForProjectTrack(companyId)
	}

	private void flushSession() {
		def hibSession = sessionFactory.getCurrentSession()
		if (hibSession) {
			hibSession.flush()
		}
	}
}
