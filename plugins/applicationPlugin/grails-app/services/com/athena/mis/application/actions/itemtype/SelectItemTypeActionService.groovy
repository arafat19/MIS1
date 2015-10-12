package com.athena.mis.application.actions.itemtype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select item type object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectItemTypeActionService'
 */
class SelectItemTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String OBJ_NOT_FOUND_MASSAGE = "Selected item type not found"
    private static final String INVALID_INPUT_MSG = "Error occurred due to invalid input"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select item type"
    /**
     * 1. check user access as Admin role
     * @param params -N/A
     * @param obj - N/A
     * @return  - a map containing isAccess(true/false) depending on method success &  relevant message.
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            if (appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }
    /**
     * 1. Check existence of required parameters
     * 2. pull item type list from cache utility
     * 3. check item type existence
     * @param params -serialize parameters from UI
     * @param obj -N/A
     * @return - item type list and relevant message
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            //Check existence of required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }

            long itemTypeId = Long.parseLong(parameterMap.id.toString())
            ItemType itemType = (ItemType) itemTypeCacheUtility.read(itemTypeId)
            if (!itemType) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.ENTITY, itemType)
            result.put(Tools.VERSION, itemType.version)
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
     * do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }
    /**
     * Build failure result in case of any error
     * @param obj -N/A
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map previousResult = (Map) obj
                result.put(Tools.MESSAGE, previousResult.message)
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            }
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
}
