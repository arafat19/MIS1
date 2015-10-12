package com.athena.mis.application.actions.requestmap

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for request map CRUD and list of request map for grid
 *  For details go through Use-Case doc named 'ShowRequestMapActionService'
 */
class ShowRequestMapActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load request Map page"
    private static final String MODULE_LIST = "moduleList"

    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * Check if user has access to update request map or not
     * @param parameters - N/A
     * @param obj - N/A
     * @return - a map contains hasAccess(true/false), isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            // only development admin role type user can show request map
            if (appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Get list of modules
     * @param params - N/A
     * @param obj - N/A
     * @return - a map containing module list, isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)  // default value
            List ModuleList = listPlugins()
            result.put(MODULE_LIST,Tools.listForKendoDropdown(ModuleList,null,null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return null
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get module list for drop down
     * @param obj - map returned from execute method
     * @return - a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            result.put(MODULE_LIST, receivedResult.get(MODULE_LIST))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return receivedResult
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            log.error(ex.getMessage())
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Get list of all plugins
     * @return - plugins list
     */
    private List listPlugins() {
        List lstPlugins = []
        Collection<PluginConnector> allPlugins = (Collection<PluginConnector>) PluginConnector.getAllPlugins().sort { it.name }
        allPlugins.each {
            if (it.id != PluginConnector.DEVELOPMENT_ID) {
                lstPlugins.add([id: it.id, name: it.name])
            }
        }
        return lstPlugins
    }
}
