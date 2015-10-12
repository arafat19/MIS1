package com.athena.mis.application.actions.appgroup

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppGroup
import com.athena.mis.application.utility.AppGroupCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
/**
 *  Select user group(appGroup) object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectAppGroupActionService'
 */
class SelectAppGroupActionService extends BaseService implements ActionIntf {

    @Autowired
    AppGroupCacheUtility appGroupCacheUtility

    private static final String GROUP_NOT_FOUND_MASSAGE = "Selected Group is not found"

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to select Group"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. pull group object
     * 2. check group existence
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing user group list necessary for buildSuccessResultForUI
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long groupId = Long.parseLong(parameterMap.id.toString())
            AppGroup appGroup = (AppGroup) appGroupCacheUtility.read(groupId)
            if (!appGroup) {
                result.put(Tools.MESSAGE, GROUP_NOT_FOUND_MASSAGE)
                return result
            }
            result.put(Tools.ENTITY, appGroup)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, GROUP_NOT_FOUND_MASSAGE)
            return result
        }
    }
    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get group object
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AppGroup appGroup = (AppGroup) executeResult.get(Tools.ENTITY)
            result.put(Tools.ENTITY, appGroup)
            result.put(Tools.VERSION, appGroup.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap receivedResult = (LinkedHashMap) obj
                String receivedMessage = receivedResult.get(Tools.MESSAGE)
                if (receivedMessage) {
                    result.put(Tools.MESSAGE, receivedMessage)
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
}
