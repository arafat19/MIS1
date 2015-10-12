/* create inventory that placed in application plugin */
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

class CreateInvInventoryActionService extends BaseService implements ActionIntf {

    InvInventoryService invInventoryService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String INPUT_VALIDATION_ERROR = "Error occurred for invalid input"
    private static final String INVENTORY_CREATE_SUCCESS_MSG = "Inventory has been successfully saved"
    private static final String INVENTORY_CREATE_FAILURE_MSG = "Inventory has not been saved"
    private static final String INVENTORY_ALREADY_EXIST = "Same inventory name already exist"
    private static final String INVENTORY = "invInventory"

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            InvInventory invInventory = (InvInventory) obj
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!invSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            if (!params.projectId) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            invInventory.companyId = invSessionUtil.appSessionUtil.getCompanyId()

            int duplicateCount = checkUniqueInventoryName(invInventory)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, INVENTORY_ALREADY_EXIST)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(INVENTORY, invInventory)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvInventory invInventory = (InvInventory) preResult.get(INVENTORY)
            InvInventory newInventory = invInventoryService.create(invInventory)
            invInventoryCacheUtility.add(invInventory, invInventoryCacheUtility.SORT_ON_NAME, invInventoryCacheUtility.SORT_ORDER_ASCENDING)
            result.put(INVENTORY, newInventory)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(INVENTORY_CREATE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INVENTORY_CREATE_FAILURE_MSG)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        //there are not post condition
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj

            InvInventory invInventory = (InvInventory) executeResult.get(INVENTORY)
            GridEntity object = new GridEntity()
            Project project = (Project) projectCacheUtility.read(invInventory.projectId)
            SystemEntity systemEntity = (SystemEntity) invInventoryTypeCacheUtility.read(invInventory.typeId)

            object.id = invInventory.id
            object.cell = [Tools.LABEL_NEW,
                    project.name,    //ProjectName
                    invInventory.name,  //Location
                    invInventory.description ? invInventory.description : Tools.EMPTY_SPACE,
                    invInventory.isFactory ? Tools.YES : Tools.NO,
                    systemEntity.key  //Store OR Site OR something else
            ]

            Map resultMap = [entity: object, version: invInventory.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, INVENTORY_CREATE_SUCCESS_MSG)
            result.put(INVENTORY, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INVENTORY_CREATE_FAILURE_MSG)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map receiveResult = (Map) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, INVENTORY_CREATE_FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INVENTORY_CREATE_FAILURE_MSG)
            log.error(ex.getMessage())
            return result
        }
    }

    private static final String UNIQUE_INV_NAME_QUERY = """
            SELECT COUNT(id) AS count
            FROM inv_inventory
            WHERE name ilike :name
              AND type_id =:typeId
              AND company_id =:companyId
            """

    private int checkUniqueInventoryName(InvInventory invInventory) {
        Map queryParams = [
                name: invInventory.name,
                typeId: invInventory.typeId,
                companyId: invInventory.companyId
        ]
        List results = executeSelectSql(UNIQUE_INV_NAME_QUERY, queryParams)
        int count = results[0].count
        return count
    }
}