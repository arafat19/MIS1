/* update selected inventory
* - placed in application plugin */
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
import com.athena.mis.inventory.service.InvInventoryService
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvInventoryTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UpdateInvInventoryActionService extends BaseService implements ActionIntf {

    InvInventoryService invInventoryService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String INVENTORY_UPDATE_FAILURE_MESSAGE = "Inventory could not be updated"
    private static final String INVENTORY_UPDATE_SUCCESS_MESSAGE = "Inventory has been updated successfully"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Input validation fail"
    private static final String INTERNAL_SERVER_ERROR = "Internal server error"
    private static final String INVENTORY_ALREADY_EXIST = "Same inventory name already exist"
    private static final String INVENTORY = "invInventory"

    private final Logger log = Logger.getLogger(getClass())


    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            if (!invSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            }
            // check parameterMap
            if ((!parameterMap.id) || (!parameterMap.version)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            long id = Long.parseLong(parameterMap.id.toString())
            InvInventory oldInventory = invInventoryService.read(id)

            InvInventory newInventory = new InvInventory(parameterMap)

            oldInventory.name = newInventory.name
            oldInventory.description = newInventory.description
            oldInventory.isFactory = newInventory.isFactory

            // checks input validation
            oldInventory.validate()
            if (newInventory.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            int duplicateCount = checkUniqueInventoryNameForUpdate(oldInventory)
            if (duplicateCount > 0) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, INVENTORY_ALREADY_EXIST)
                return result
            }

            result.put(INVENTORY, oldInventory)
            result.put(Tools.IS_ERROR, Boolean.FALSE)

            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INTERNAL_SERVER_ERROR)
            return result
        }
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvInventory invInventory = (InvInventory) preResult.get(INVENTORY)
            invInventoryService.update(invInventory)
            invInventoryCacheUtility.update(invInventory, invInventoryCacheUtility.SORT_ON_NAME, invInventoryCacheUtility.SORT_ORDER_ASCENDING)
            result.put(INVENTORY, invInventory)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(INVENTORY_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INTERNAL_SERVER_ERROR)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }


    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            InvInventory serviceReturn = (InvInventory) receiveResult.get(INVENTORY)
            GridEntity object = new GridEntity()
            object.id = serviceReturn.id
            Project project = (Project) projectCacheUtility.read(serviceReturn.projectId)
            SystemEntity systemEntity = (SystemEntity) invInventoryTypeCacheUtility.read(serviceReturn.typeId)
            object.cell = [Tools.LABEL_NEW,
                    project.name,
                    serviceReturn.name,
                    serviceReturn.description,
                    serviceReturn.isFactory ? Tools.YES : Tools.NO,
                    systemEntity.key
            ]
            Map resultMap = [entity: object, version: serviceReturn.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, INVENTORY_UPDATE_SUCCESS_MESSAGE)
            result.put(INVENTORY, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INVENTORY_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }


    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, INVENTORY_UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INTERNAL_SERVER_ERROR)
            return result
        }
    }

    private static final String UNIQUE_INV_NAME_CHECK_QUERY = """
            SELECT COUNT(id) AS count
            FROM inv_inventory
            WHERE name ilike :name
              AND type_id =:typeId
              AND company_id =:companyId
              AND id <>:inventoryId
            """
    private int checkUniqueInventoryNameForUpdate(InvInventory inventory) {
        Map queryParams = [
                name: inventory.name,
                typeId: inventory.typeId,
                companyId: inventory.companyId,
                inventoryId: inventory.id
        ]
        List results = executeSelectSql(UNIQUE_INV_NAME_CHECK_QUERY, queryParams)
        int count = results[0].count
        return count
    }

}
