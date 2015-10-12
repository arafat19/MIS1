/* show list of object(s) of inventory
   to populate grid first time- placed in application plugin */
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

class ShowInvInventoryActionService extends BaseService implements ActionIntf {
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load"
    private static final String LST_INVENTORY = "lstInventory"

    protected final Logger log = Logger.getLogger(getClass())

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            if (!invSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            return result
        }
    }

    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initPager(params)
            List inventoryList = invInventoryCacheUtility.list(this)
            int count = invInventoryCacheUtility.count()
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return [inventoryList: inventoryList, count: count]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return null
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            Map executeResult = (Map) obj
            List<InvInventory> inventoryList = (List<InvInventory>) executeResult.inventoryList
            int count = executeResult.count
            List resultInventoryList = wrapListInGridEntityList(inventoryList, start)
            Map lstInventory = [page: pageNumber, total: count, rows: resultInventoryList]

            result.put(LST_INVENTORY, lstInventory)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.TRUE)
        result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
        return result
    }

    private List wrapListInGridEntityList(List<InvInventory> inventoryList, int start) {
        List inventories = []
        try {
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
                obj.cell = [counter,
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
        } catch (Exception ex) {
            log.error(ex.getMessage())
            inventories = []
            return inventories
        }
    }
}
