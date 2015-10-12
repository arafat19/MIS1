package com.athena.mis.exchangehouse.actions.postalCode

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.exchangehouse.entity.ExhPostalCode
import com.athena.mis.exchangehouse.service.ExhPostalCodeService
import com.athena.mis.exchangehouse.utility.ExhPostalCodeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update postalCode object and show in grid
 *  For details go through Use-Case doc named 'ExhUpdatePostalCodeActionService'
 */
class ExhUpdatePostalCodeActionService extends BaseService implements ActionIntf{

    @Autowired
    ExhPostalCodeCacheUtility exhPostalCodeCacheUtility
    ExhPostalCodeService exhPostalCodeService


    private static final String OBJ_NOT_FOUND="Postal code not found"
    private static final String POSTAL_CODE="postalCode"
    private static final String POSTAL_CODE_UPDATE_FAILURE_MESSAGE ="Postal code could not be updated"
    private static final String POSTAL_CODE_UPDATE_SUCCESS_MESSAGE="Postal code successfully updated"
    private static final String POSTAL_CODE_ALREADY_EXISTS = "Postal code already exists"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Get parameters from UI and build postalCode object for update
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */

    @Transactional(readOnly = true)
    public  Object executePreCondition(Object parameters, Object obj){

        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // check required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long postalCodeId = Long.parseLong(parameterMap.id)
            ExhPostalCode oldPostalCode = exhPostalCodeService.read(postalCodeId) // get postalCode object
            // check whether selected postalCode object exists or not
            if (!oldPostalCode) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            ExhPostalCode postalCode = buildPostalCode(parameterMap, oldPostalCode)  // build postalCode object for update
            boolean duplicate = exhPostalCodeService.checkDuplicateCode(postalCode.code, postalCode.id, postalCode.companyId)
            if(duplicate) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, POSTAL_CODE_ALREADY_EXISTS)
                return result
            }
            result.put(POSTAL_CODE, postalCode)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, POSTAL_CODE_UPDATE_FAILURE_MESSAGE)
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
     * Update postalCode object in DB & update cache utility accordingly
     * This function is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public  Object execute(Object parameters, Object obj){
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            ExhPostalCode postalCode = (ExhPostalCode) preResult.get(POSTAL_CODE)
            exhPostalCodeService.update(postalCode)
            exhPostalCodeCacheUtility.update(postalCode, exhPostalCodeCacheUtility.SORT_ON_CODE, exhPostalCodeCacheUtility.SORT_ORDER_ASCENDING)
            result.put(POSTAL_CODE, postalCode)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, POSTAL_CODE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Show updated postalCode object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public  Object buildSuccessResultForUI(Object obj){
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            ExhPostalCode postalCode = (ExhPostalCode) executeResult.get(POSTAL_CODE)
            GridEntity object = new GridEntity()    // build grid entity object
            object.id = postalCode.id
            object.cell = [
                    Tools.LABEL_NEW,
                    postalCode.code
            ]
            result.put(Tools.MESSAGE, POSTAL_CODE_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, POSTAL_CODE_UPDATE_SUCCESS_MESSAGE)
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
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, POSTAL_CODE_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, POSTAL_CODE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build postalCode object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldPostalCode -old oldPostalCode object
     * @return -updated oldPostalCode object
     */
    private ExhPostalCode buildPostalCode(GrailsParameterMap parameterMap, oldPostalCode){
        ExhPostalCode postalCode = new ExhPostalCode(parameterMap)
        postalCode.id = oldPostalCode.id
        postalCode.version = oldPostalCode.version
        postalCode.companyId = oldPostalCode.companyId
        return postalCode
    }
}
