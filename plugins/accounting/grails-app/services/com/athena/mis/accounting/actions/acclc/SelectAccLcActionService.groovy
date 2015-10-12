package com.athena.mis.accounting.actions.acclc

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccLc
import com.athena.mis.accounting.utility.AccLcCacheUtility
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.utility.Tools
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select specific accLc object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectAccLcActionService'
 */
class SelectAccLcActionService extends BaseService implements ActionIntf {

    @Autowired
    AccLcCacheUtility accLcCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility

    private static final String OBJ_NOT_FOUND_MASSAGE = "Selected LC is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select LC"
    private static final String ITEM_LIST = "itemList"
    private static final String ITEM_ENTITY = "item"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get accLc object by id
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing accLc, item object & itemType list for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {//check existence of required parameter
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long accLcId = Long.parseLong(parameterMap.id.toString())
            AccLc accLc = (AccLc) accLcCacheUtility.read(accLcId)
            if (!accLc) { //check existence of accLc object
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
                return result
            }

            //get item object to show on drop-down
            Item item = (Item) itemCacheUtility.read(accLc.itemId)
            //get itemType list to show on drop-down
            List itemList = itemCacheUtility.listByTypeForDropDown(item.itemTypeId)

            result.put(ITEM_ENTITY, item)
            result.put(Tools.ENTITY, accLc)
            result.put(ITEM_LIST, itemList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with necessary objects to show on UI
     * @param obj -map contains accLc, item object & itemType list
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AccLc accLc = (AccLc) executeResult.get(Tools.ENTITY)
            result.put(ITEM_LIST, executeResult.get(ITEM_LIST))
            result.put(ITEM_ENTITY, executeResult.get(ITEM_ENTITY))
            result.put(Tools.ENTITY, accLc)
            result.put(Tools.VERSION, accLc.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError(true) & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
}
