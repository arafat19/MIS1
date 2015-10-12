package com.athena.mis.application.actions.designation

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Designation
import com.athena.mis.application.utility.DesignationCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
/**
 *  Select specific object of designation
 *  For details go through Use-Case doc named 'SelectDesignationActionService'
 */
class SelectDesignationActionService extends BaseService implements ActionIntf {

    @Autowired
    DesignationCacheUtility designationCacheUtility

    private static final String OBJ_NOT_FOUND_MASSAGE = "Selected designation not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select designation"
    private static final String INVALID_INPUT_MASSAGE = "Failed to select designation due to invalid input"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. check input validation
     * 2. pull designation list from cache utility
     * 3. check designation existence
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing designation list & count necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */

    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MASSAGE)
                return result
            }

            long designationId = Long.parseLong(parameterMap.id.toString())
            Designation designation = (Designation) designationCacheUtility.read(designationId)
            if (!designation) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
            }
            result.put(Tools.ENTITY, designation)
            result.put(Tools.VERSION, designation.version)
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
     * do nothing for failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}

