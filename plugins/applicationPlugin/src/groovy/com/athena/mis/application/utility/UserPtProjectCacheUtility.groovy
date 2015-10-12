package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.service.AppUserEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('userPtProjectCacheUtility')
class UserPtProjectCacheUtility extends ExtendedCacheUtility  {
    @Autowired
    AppUserEntityService appUserEntityService
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    public final String SORT_ON_ID = "id";

    public void init() {
        List list = appUserEntityService.listByType(appUserEntityTypeCacheUtility.PT_PROJECT)
        super.setList(list)
    }

    //Get log in User project Ids
    public List<Long> listUserProjectIds() {
        List<AppUserEntity> userProjectList = (List<AppUserEntity>) super.list()
        List<Long> projectIds = []
        AppUser sessionUser = appSessionUtil.getAppUser()
        for (int i = 0; i < userProjectList.size(); i++) {
            if (userProjectList[i].appUserId == sessionUser.id) {
                projectIds << userProjectList[i].entityId
            }
        }
        return projectIds
    }
}
