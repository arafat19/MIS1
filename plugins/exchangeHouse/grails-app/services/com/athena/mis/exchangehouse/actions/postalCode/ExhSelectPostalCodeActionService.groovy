package com.athena.mis.exchangehouse.actions.postalCode

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhPostalCode
import com.athena.mis.exchangehouse.utility.ExhPostalCodeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select postalCode grid
 *  For details go through Use-Case doc named 'ExhSelectPostalCodeActionService'
 */
class ExhSelectPostalCodeActionService extends BaseService implements ActionIntf{

    @Autowired
    ExhPostalCodeCacheUtility exhPostalCodeCacheUtility

    private static final String POSTAL_CODE_NOT_FOUND_MESSAGE="Postal code not found message"
    private static final String DEFAULT_FAILURE_MESSAGE="Failed to select postal code"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * do nothing for pre operation
     */
    public  Object executePreCondition(Object parameters, Object obj){
        return null
    }

    /**
     * do nothing for post operation
     */
    public  Object executePostCondition(Object parameters, Object obj){
        return null
    }

    /**
     * Get postalCode object by id
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public  Object execute(Object parameters, Object obj){
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // check required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long postalCodeId = Long.parseLong(parameterMap.id)
            ExhPostalCode postalCode = (ExhPostalCode) exhPostalCodeCacheUtility.read(postalCodeId)    // get postalCode object
            // check whether the postalCode object exists or not
            if(!postalCode){
                result.put(Tools.MESSAGE,POSTAL_CODE_NOT_FOUND_MESSAGE)
            }
            result.put(Tools.ENTITY,postalCode)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, POSTAL_CODE_NOT_FOUND_MESSAGE)
            return result
        }

    }
    /**
     * Build a map with postalCode object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public  Object buildSuccessResultForUI(Object obj){
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            ExhPostalCode postalCode = (ExhPostalCode) executeResult.get(Tools.ENTITY)
            result.put(Tools.ENTITY, postalCode)
            result.put(Tools.VERSION, postalCode.version.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public  Object buildFailureResultForUI(Object obj){
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!obj) {
                result.put(Tools.MESSAGE, DEFAULT_FAILURE_MESSAGE)

            }
            Map previousResult = (Map) obj  // cast map returned from previous method
            result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MESSAGE)
            return result
        }
    }

}
