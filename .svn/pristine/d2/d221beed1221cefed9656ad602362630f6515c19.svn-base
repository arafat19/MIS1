package com.athena.mis.application.actions.itemtype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.service.ItemTypeService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete item type object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteItemTypeActionService'
 */
class DeleteItemTypeActionService extends BaseService implements ActionIntf {

    ItemTypeService itemTypeService
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String DELETE_SUCCESS_MSG = "Item type has been successfully deleted"
    private static final String INVALID_INPUT_MSG = "Error occurred due to invalid input"
    private static final String OBJ_NOT_FOUND = "Selected item type not found"
    private static final String DELETE_FAILURE_MSG = "Item type has not been deleted"
    private static final String HAS_ASSOCIATION_ITEM = " item(s) are associated with selected item type"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * 1. check user access as Admin role
     * 2. check input validation
     * 3. pull item type object
     * 4. check for item type existence
     * 5. check association with Item
     * @param parameters - serialize parameters from UI
     * @param obj -N/A
     * @return - a map containing isError(true/false) depending on method success & relevant message.
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters
            //Check existence of required parameters
            if (!params.id) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }

            long itemTypeId = Long.parseLong(params.id.toString())
            ItemType itemType = (ItemType) itemTypeCacheUtility.read(itemTypeId)
            if (!itemType) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            //Check association with Item
            int countItem = itemCacheUtility.countByItemTypeId(itemTypeId)
            if (countItem > 0) {
                result.put(Tools.MESSAGE, countItem + HAS_ASSOCIATION_ITEM)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
            return result
        }
    }
    /**
     * Delete item type object from DB and cache utility
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -boolean value true/false depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long itemTypeId = Long.parseLong(parameterMap.id.toString())
            itemTypeService.delete(itemTypeId)
            itemTypeCacheUtility.delete(itemTypeId)
            return Boolean.TRUE
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete item type')
            return Boolean.FALSE
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return [deleted: Boolean.TRUE.booleanValue(), message: DELETE_SUCCESS_MSG]
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
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
            return result
        }
    }
}


