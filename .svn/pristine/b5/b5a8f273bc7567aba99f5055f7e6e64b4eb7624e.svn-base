/*  get inventory list according to their type(site/store) & project */
package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class GetInventoryListByInvTypeAndProjectActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    InvSessionUtil invSessionUtil

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to load inventory list"
    private static final String INVENTORY_LIST = "inventoryList"

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            long inventoryTypeId
            long projectId

            List userInventoryList = []
            if ((parameterMap.projectId.equals(Tools.EMPTY_SPACE)) && (!parameterMap.inventoryTypeId.equals(Tools.EMPTY_SPACE))) {//All project and Specific InventoryType
                inventoryTypeId = Long.parseLong(parameterMap.inventoryTypeId.toString())
                userInventoryList = invSessionUtil.getUserInventoriesByType(inventoryTypeId)
            } else if ((!parameterMap.projectId.equals(Tools.EMPTY_SPACE)) && (!parameterMap.inventoryTypeId.equals(Tools.EMPTY_SPACE))) {//Specific project and Specific InventoryType
                projectId = Long.parseLong(parameterMap.projectId.toString())
                inventoryTypeId = Long.parseLong(parameterMap.inventoryTypeId.toString())
                userInventoryList = invSessionUtil.getUserInventoriesByTypeAndProject(inventoryTypeId, projectId)
            }

            result.put(INVENTORY_LIST, userInventoryList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
            List inventoryList = (List) receiveResult.get(INVENTORY_LIST)
            result = [inventoryList: Tools.listForKendoDropdown(inventoryList, null, Tools.ALL)]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
}