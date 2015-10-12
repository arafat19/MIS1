package com.athena.mis.inventory.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.service.InvInventoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("invInventoryCacheUtility")
class InvInventoryCacheUtility extends ExtendedCacheUtility {

    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvInventoryService inventoryService
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    static final String SORT_ON_NAME = "name";

    public void init() {
        List list = inventoryService.list();
        super.setList(list)
    }

    public List<InvInventory> listByType(long invType) {
        List<InvInventory> inventoryList = (List<InvInventory>) super.list()
        List<InvInventory> newInventoryList = []
        for (int i = 0; i < inventoryList.size(); i++) {
            if (inventoryList[i].typeId == invType) {
                newInventoryList << inventoryList[i]
            }
        }
        return newInventoryList
    }

    public boolean hasAccessInventory(long inventoryId) {
        try {
            List<Long> inventoryIds = invSessionUtil.getUserInventoryIds()

            if (!inventoryIds) return false
            long tempInventoryId = 0
            for (int i = 0; i < inventoryIds.size(); i++) {
                tempInventoryId = inventoryIds[i].longValue()
                if (tempInventoryId == inventoryId)
                    return true
            }
            return false
        } catch (Exception ex) {
            return false
        }
    }
}
