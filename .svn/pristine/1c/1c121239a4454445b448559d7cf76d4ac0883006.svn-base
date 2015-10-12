package com.athena.mis.application.actions.item

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.utility.Tools
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Class to get all itemList filtered by specific itemType to show on drop-down
 *      -> used in AccLc & AccLeaseAccount CRUD
 *  For details go through Use-Case doc named 'GetItemListSupplierItemActionService'
 */
class GetItemListByItemTypeActionService extends BaseService implements ActionIntf {

    @Autowired
    ItemCacheUtility itemCacheUtility

    private static final String FAILURE_MESSAGE = "Failed to get item list"
    private static final String ITEM_LIST = "itemList"

    /**
     * do nothing at preCondition
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * do nothing at postCondition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get all itemList filtered by specific itemType to show on drop-down
     * @param params-parameters send from UI
     * @param obj -N/A
     * @return -a map containing itemList
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            if (!parameterMap.itemTypeId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long itemTypeId = Long.parseLong(parameterMap.itemTypeId.toString())
            List<Item> lstItem = itemCacheUtility.listByTypeForDropDown(itemTypeId)
            result.put(ITEM_LIST, Tools.listForKendoDropdown(lstItem,null,null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing at buildSuccessResultForUI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            }
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }
}
