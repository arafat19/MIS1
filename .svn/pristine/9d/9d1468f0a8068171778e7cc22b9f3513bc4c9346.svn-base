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
 * Class to get fixedAssetList by an item in an inventory for drop-down(Used in InventoryConsumptionDetails page (both in approved & unapproved))
 * For details go through Use-Case doc named 'GetFixedAssetListByInventoryIdAndItemIdActionService'
 */
class GetFixedAssetListByInventoryIdAndItemIdActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired(required = false)
    FixedAssetPluginConnector fixedAssetImplService

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to load Fixed Asset Details Item list"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String LST_FIXED_ASSET_DETAILS_ITEMS = "lstFixedAssetDetailsItems"

    /**
     * method to check required parameters
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            if (!parameterMap.itemId || !parameterMap.inventoryId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * method to get fixedAssetList by an item in an inventory
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing fixedAssetList by an item of a particular inventory
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            long inventoryId = Long.parseLong(parameterMap.inventoryId.toString())
            long itemId = Long.parseLong(parameterMap.itemId.toString())
            //get listOfFixedAsset by inventoryId and itemId
            List<GroovyRowResult> lstFixedAssetDetailsItems = fixedAssetImplService.getFixedAssetListByInvIdAndItemId(inventoryId, itemId)

            result.put(LST_FIXED_ASSET_DETAILS_ITEMS, Tools.listForKendoDropdown(lstFixedAssetDetailsItems, null, null))
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
     * @return -a map containing fixedAssetList by an item of a particular inventory for drop-down
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            List lstFixedAssetDetailsItems = (List) receiveResult.get(LST_FIXED_ASSET_DETAILS_ITEMS)
            result = [lstFixedAssetDetailsItems: lstFixedAssetDetailsItems]
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