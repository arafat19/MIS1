package com.athena.mis.exchangehouse.actions.postalCode

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhPostalCode
import com.athena.mis.exchangehouse.service.ExhPostalCodeService
import com.athena.mis.exchangehouse.utility.ExhPostalCodeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Delete postalCode object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'ExhDeletePostalCodeActionService'
 */

class ExhDeletePostalCodeActionService extends BaseService implements ActionIntf {

    @Autowired
    ExhPostalCodeCacheUtility exhPostalCodeCacheUtility
    ExhPostalCodeService exhPostalCodeService

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE="Postal code could not be deleted"
    private static final String POSTAL_CODE_DELETE_SUCCESS_MSG ="Successfully deleted"


    /**
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public  Object executePreCondition(Object parameters, Object obj){

        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            // check required parameters
            if (!params.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long postalCodeId = Long.parseLong(params.id)
            ExhPostalCode postalCode = (ExhPostalCode) exhPostalCodeCacheUtility.read(postalCodeId)    // get postalCode object
            // check whether selected postalCode object exists or not
            if (!postalCode) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
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
     * do nothing for post operation
     */
    public  Object executePostCondition(Object parameters, Object obj){
        return null

    }

    /**
     * Delete postalCode object from DB and cache utility
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    public  Object execute(Object parameters, Object obj){

        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long postalCodeId = Long.parseLong(parameterMap.id)
            exhPostalCodeService.delete(postalCodeId)    // delete postalCode object from DB
            ExhPostalCode postalCode = (ExhPostalCode) exhPostalCodeCacheUtility.read(postalCodeId)
            exhPostalCodeCacheUtility.delete(postalCode.id)   // delete postalCode object from cache utility
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
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public  Object buildSuccessResultForUI(Object obj){

        Map result = new LinkedHashMap()
        result.put(Tools.MESSAGE, POSTAL_CODE_DELETE_SUCCESS_MSG)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
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
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
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
