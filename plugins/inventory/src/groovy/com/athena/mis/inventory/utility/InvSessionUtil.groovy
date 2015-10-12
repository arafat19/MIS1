package com.athena.mis.inventory.utility

import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component("invSessionUtil")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
class InvSessionUtil implements Serializable {

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    InvUserInventoryCacheUtility invUserInventoryCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private List<InvInventory> lstInventories = null    // List of Inventories that is mapped with loggedIn user
    private List<Long> lstInventoryIds = null           // List of Inventory ids that is mapped with loggedIn user

    // Initialize the values
    @Transactional
    public void init() {
        AppUser user = appSessionUtil.getAppUser()
        lstInventories = invUserInventoryCacheUtility.listUserInventories(user.id)
        lstInventoryIds = Tools.getIds(lstInventories)
    }

    // get list of inventory ids mapped with user
    public List<Long> getUserInventoryIds() {
        return lstInventoryIds
    }

    // get list of inventories mapped with user
    public List<InvInventory> getUserInventories() {
        return lstInventories
    }

    // get list of inventories mapped with user by type
    public List<InvInventory> getUserInventoriesByType(long typeId) {
        List<InvInventory> lstInventoryByType = lstInventories.findAll { it.typeId == typeId }
        return lstInventoryByType
    }

    // get list of inventory ids mapped with user by type
    public List<Long> getUserInventoryIdsByType(long inventoryType) {
        List<Long> lstInventoryIdsByType = []
        for (int i = 0; i < lstInventories.size(); i++) {
            if (lstInventories[i].typeId == inventoryType) {
                lstInventoryIdsByType << lstInventories[i].id
            }
        }
        return lstInventoryIdsByType
    }

    // get list of inventory ids mapped with user by project
    public List<Long> getUserInventoryIdsByProject(long projectId) {
        List<Long> lstInventoryIdsByProject = []
        for (int i = 0; i < lstInventories.size(); i++) {
            if (lstInventories[i].projectId == projectId) {
                lstInventoryIdsByProject << lstInventories[i].id
            }
        }
        return lstInventoryIdsByProject
    }

    // get list of inventory ids mapped with user by type and project
    public List<Long> getUserInventoryIdsByTypeAndProject(long inventoryType, long projectId) {
        List<Long> lstInventoryIdsByTypeAndProject = []
        for (int i = 0; i < lstInventories.size(); i++) {
            if (lstInventories[i].projectId == projectId && lstInventories[i].typeId == inventoryType) {
                lstInventoryIdsByTypeAndProject << lstInventories[i].id
            }
        }
        return lstInventoryIdsByTypeAndProject
    }

    // get list of inventories mapped with user by type and project
    public List<InvInventory> getUserInventoriesByTypeAndProject(long inventoryTypeId, long projectId) {
        List<InvInventory> lstInventoriesByTypeAndProject = []
        for (int i = 0; i < lstInventories.size(); i++) {
            if ((lstInventories[i].projectId == projectId) && (lstInventories[i].typeId == inventoryTypeId)) {
                lstInventoriesByTypeAndProject << lstInventories[i]
            }
        }
        return lstInventoriesByTypeAndProject
    }
}
