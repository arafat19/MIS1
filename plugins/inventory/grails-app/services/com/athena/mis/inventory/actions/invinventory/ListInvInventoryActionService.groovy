/*  retrieve list of object(s) of inventory
   when refresh button in UI fired - placed in application plugin */
package com.athena.mis.inventory.actions.invinventory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvInventoryTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class ListInvInventoryActionService extends BaseService implements ActionIntf {

    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static String PAGE_LOAD_ERROR_MESSAGE = "Failed to load inventory grid"

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            if (!invSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    public Object execute(Object params, Object obj = null) {
        try {
            if ((!params.sortname) || (params.sortname.toString().equals(ID))) {
                params.sortname = invInventoryCacheUtility.SORT_ON_NAME
                params.sortorder = invInventoryCacheUtility.SORT_ORDER_ASCENDING
            }
            initSearch(params)
            List inventoryList = invInventoryCacheUtility.list(this)
            int count = invInventoryCacheUtility.count()
            return [inventoryList: inventoryList, count: count]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<InvInventory> inventoryList = (List<InvInventory>) executeResult.inventoryList
            int count = (int) executeResult.count
            List resultInventoryList = wrapListInGridEntityList(inventoryList, start)
            return [page: pageNumber, total: count, rows: resultInventoryList]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null]
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    private def wrapListInGridEntityList(List<InvInventory> inventoryList, int start) {
        List inventories = []
        int counter = start + 1
        InvInventory inventory
        GridEntity obj
        Project project
        SystemEntity systemEntity

        for (int i = 0; i < inventoryList.size(); i++) {
            inventory = inventoryList[i]
            obj = new GridEntity()
            obj.id = inventory.id
            project = (Project) projectCacheUtility.read(inventory.projectId)
            systemEntity = (SystemEntity) invInventoryTypeCacheUtility.read(inventory.typeId)
            obj.cell = [
                    counter,
                    project.name,    //ProjectName
                    inventory.name,  //Location
                    inventory.description,
                    inventory.isFactory ? Tools.YES : Tools.NO,
                    systemEntity.key  //Store OR Site OR something else
            ]
            inventories << obj
            counter++
        }
        return inventories
    }
}

