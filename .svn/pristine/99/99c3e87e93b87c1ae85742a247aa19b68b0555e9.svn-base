/* grid search -  placed in application plugin */
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

class SearchInvInventoryActionService extends BaseService implements ActionIntf {

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

    private final Logger log = Logger.getLogger(getClass())

    private static String PAGE_LOAD_ERROR_MESSAGE = "Failed to search inventory grid"

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
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
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
            Map searchResult = invInventoryCacheUtility.search(queryType, query, this)
            List inventoryList = searchResult.list
            int count = searchResult.count
            return [inventoryList: inventoryList, count: count]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    private List wrapListInGridEntityList(List<InvInventory> inventoryList, int start) {
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
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<InvInventory> invInventoryList = (List<InvInventory>) executeResult.inventoryList
            int count = (int) executeResult.count
            List inventoryList = wrapListInGridEntityList(invInventoryList, start)
            return [page: pageNumber, total: count, rows: inventoryList]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null]
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        return [isError: true, entity: obj, message: PAGE_LOAD_ERROR_MESSAGE]
    }
}
