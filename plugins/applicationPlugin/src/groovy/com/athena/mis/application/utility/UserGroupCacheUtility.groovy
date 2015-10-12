package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.AppGroup
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.service.AppUserEntityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('userGroupCacheUtility')
class UserGroupCacheUtility extends ExtendedCacheUtility {
    @Autowired
    AppUserEntityService appUserEntityService
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    AppGroupCacheUtility appGroupCacheUtility

    public final String SORT_ON_NAME = "id";

    public void init() {
        List list = appUserEntityService.listByType(appUserEntityTypeCacheUtility.GROUP)
        super.setList(list)
    }

    public List<AppUserEntity> listByUserId(long userId) {
        List<AppUserEntity> newUserGroupMappingList = []
        List<AppUserEntity> userGroupMappingList = list()
        int sizeOfUserGroupMapping = userGroupMappingList.size()
        AppUserEntity userGroupMapping
        for (int i = 0; i < sizeOfUserGroupMapping; i++) {
            userGroupMapping = (AppUserEntity) userGroupMappingList[i]
            if (userGroupMapping.appUserId == userId) {
                newUserGroupMappingList << userGroupMapping
            }
        }
        return newUserGroupMappingList
    }

    public List<AppGroup> listUserGroups(long userId) {
        List<AppUserEntity> lstUserGroup = (List<AppUserEntity>) super.list()
        List<AppGroup> lstGroups = []
        for (int i = 0; i < lstUserGroup.size(); i++) {
            if (lstUserGroup[i].appUserId == userId) {
                lstGroups << appGroupCacheUtility.read(lstUserGroup[i].entityId)
            }
        }
        return lstGroups
    }
}
