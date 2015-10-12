package com.athena.mis.projecttrack.utility

import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.projecttrack.entity.PtProject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

@Component("ptSessionUtil")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
class PtSessionUtil implements Serializable {

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    AppUserEntityService appUserEntityService
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility

    List<PtProject> lstPtProjects = null    // List of pt_projects that is mapped with loggedIn user
    List<Long> lstPtProjectIds = null       // List of pt_project ids that is mapped with loggedIn user

    // get list of pt_project ids mapped with user
    public List<Long> getUserPtProjectIds() {
        if (lstPtProjectIds) return lstPtProjectIds
        AppUser user = appSessionUtil.getAppUser()
        List<AppUserEntity> lstUserPtProjectMapping = appUserEntityService.findAllByAppUserIdAndEntityTypeId(user.id, appUserEntityTypeCacheUtility.PT_PROJECT)
        List<Long> lstIds = []
        lstUserPtProjectMapping.each {
            lstIds << it.entityId
        }
        lstPtProjectIds = lstIds
        return lstPtProjectIds
    }

    // get list of pt_projects mapped with user
    public List<PtProject> getUserPtProjects() {
        if (lstPtProjects) return lstPtProjects
        AppUser user = appSessionUtil.getAppUser()
        List<AppUserEntity> lstUserPtProjectMapping = appUserEntityService.findAllByAppUserIdAndEntityTypeId(user.id, appUserEntityTypeCacheUtility.PT_PROJECT)
        List<PtProject> lstProjects = []
        lstUserPtProjectMapping.each {
            lstProjects << ptProjectCacheUtility.read(it.entityId)
        }
        lstPtProjects = lstProjects
        return lstPtProjects
    }
}
