package com.athena.mis.inventory.utility

import com.athena.mis.BaseService
import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('invUserInventoryCacheUtility')
class InvUserInventoryCacheUtility extends ExtendedCacheUtility {

    @Autowired
    AppUserEntityService appUserEntityService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility

    private static final String QUERY_TYPE_USER = "user";
    public static final String SORT_BY_ID = "id";


    public void init() {
        List list = appUserEntityService.listByType(appUserEntityTypeCacheUtility.INVENTORY)
        super.setList(list)
    }

    //further optimization required
    public Map searchByField(String queryType, String query, BaseService baseService) {
        List<AppUserEntity> userInventoryList = super.list()
        AppUser appUser
        query = Tools.escapeForRegularExpression(query)
        userInventoryList = userInventoryList.findAll {
            if (queryType.equals(QUERY_TYPE_USER)) {
                appUser = (AppUser) appUserCacheUtility.read(it.appUserId)
                String appUserName = appUser.username
                appUserName ==~ /(?i).*${query}.*/
            }
        }
        int end = userInventoryList.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : userInventoryList.size()
        List lstResult = userInventoryList.subList(baseService.start, end)
        return [list: lstResult, count: userInventoryList.size()]
    }

    public int countByInventoryId(long inventoryId) {
        List<AppUserEntity> useInventoryList = super.list()
        int count = 0
        for (int i = 0; i < useInventoryList.size(); i++) {
            if (useInventoryList[i].entityId == inventoryId) {
                count++
            }
        }
        return count
    }

    public AppUserEntity readForUpdate(AppUserEntity userInventory) {
        List<AppUserEntity> userInventoryList = (List<AppUserEntity>) super.list()

        for (int i = 0; i < userInventoryList.size(); i++) {
            if (userInventoryList[i].appUserId == userInventory.appUserId
                    && userInventoryList[i].entityId == userInventory.entityId
                    && userInventoryList[i].entityTypeId == userInventory.entityTypeId
                    && userInventoryList[i].id != userInventory.id) {
                return userInventoryList[i]
            }
        }
        return null
    }

    public List<InvInventory> listUserInventories(long userId) {
        List<AppUserEntity> lstUserInventory = (List<AppUserEntity>) super.list()
        List<InvInventory> lstInventories = []
        for (int i = 0; i < lstUserInventory.size(); i++) {
            if (lstUserInventory[i].appUserId == userId) {
                lstInventories << invInventoryCacheUtility.read(lstUserInventory[i].entityId)
            }
        }
        return lstInventories
    }
}