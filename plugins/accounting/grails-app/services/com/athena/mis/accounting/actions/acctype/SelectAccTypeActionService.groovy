package com.athena.mis.accounting.actions.acctype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccType
import com.athena.mis.accounting.utility.AccTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select account type (accType)  object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectAccTypeActionService'
 */
class SelectAccTypeActionService extends BaseService implements ActionIntf {

    @Autowired
    AccTypeCacheUtility accTypeCacheUtility

    private static final String NOT_FOUND_MASSAGE = "Selected account type is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select account type"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get account type (accType) object by id
     * @param parameters - parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)   // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long accTypeId = Long.parseLong(parameterMap.id.toString())
            AccType accType = (AccType) accTypeCacheUtility.read(accTypeId)   // get account type (accType) object from the accType Cache Utility
            if (!accType) {
                result.put(Tools.MESSAGE, NOT_FOUND_MASSAGE)
                return result
            }
            result.put(Tools.ENTITY, accType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, NOT_FOUND_MASSAGE)
            return result
        }
    }

    /**
     * Build a map with account type(accType) object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj  // cast map returned from execute method
            AccType accType = (AccType) executeResult.get(Tools.ENTITY)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.ENTITY, accType)
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
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.message) {
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
