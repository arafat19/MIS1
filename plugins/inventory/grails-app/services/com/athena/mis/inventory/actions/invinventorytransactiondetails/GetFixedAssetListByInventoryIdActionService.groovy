package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.integration.fixedasset.FixedAssetPluginConnector
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Class to get fixedAssetList in an inventory for drop-down(Used in InventoryConsumptionDetails page (both in approved & unapproved))
 * For details go through Use-Case doc named 'GetFixedAssetListByInventoryIdActionService'
 */

class GetFixedAssetListByInventoryIdActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired(required = false)
    FixedAssetPluginConnector fixedAssetImplService

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to load fixed asset list"
    private static final String LST_FIXED_ASSET_ITEMS = "lstFixedAssetItems"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * method to get fixedAssetList in an inventory
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing fixedAssetList of a particular inventory
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long inventoryId = Long.parseLong(parameterMap.inventoryId.toString())

            //get listOfFixedAsset by inventoryId
            List<GroovyRowResult> lstFixedAssetItems = fixedAssetImplService.getFixedAssetListByInvId(inventoryId)

            result.put(LST_FIXED_ASSET_ITEMS, Tools.listForKendoDropdown(lstFixedAssetItems, null, null))
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
        return null
    }

    /**
     * @param obj -map returned from execute method
     * @return -a map containing fixedAssetList of a particular inventory for drop-down
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            List lstFixedAssetItems = (List) receiveResult.get(LST_FIXED_ASSET_ITEMS)
            result = [lstFixedAssetItems: lstFixedAssetItems]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
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