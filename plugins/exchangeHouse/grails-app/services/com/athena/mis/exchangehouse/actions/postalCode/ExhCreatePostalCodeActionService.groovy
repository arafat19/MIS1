package com.athena.mis.exchangehouse.actions.postalCode

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.exchangehouse.entity.ExhPostalCode
import com.athena.mis.exchangehouse.service.ExhPostalCodeService
import com.athena.mis.exchangehouse.utility.ExhPostalCodeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Create new postalCode object and show in grid
 *  For details go through Use-Case doc named 'ExhCreatePostalCodeActionService'
 */
class ExhCreatePostalCodeActionService extends BaseService implements ActionIntf{

    ExhPostalCodeService exhPostalCodeService
    @Autowired
    ExhPostalCodeCacheUtility exhPostalCodeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    private static final String POSTAL_CODE_CREATE_SUCCESS_MSG = "Postal code has been successfully saved"
    private static final String POSTAL_CODE_CREATE_FAILURE_MSG = "Postal code has not been saved"
    private static final String POSTAL_CODE_ALREADY_EXISTS = "Postal code already exists"
    private static final String POSTAL_CODE = "postalCode"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Get parameters from UI and build postalCode object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */

    public  Object executePreCondition(Object parameters, Object obj){

        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            ExhPostalCode postalCode = buildPostalCodeObject(parameterMap)   // build postalCode object
            ExhPostalCode duplicatePostalCode = exhPostalCodeService.readByCodeAndCompanyId(postalCode.code, postalCode.companyId)
            if(duplicatePostalCode) {
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
            result.put(Tools.MESSAGE, POSTAL_CODE_CREATE_FAILURE_MSG)
            return result
        }
    }


    public  Object executePostCondition(Object parameters, Object obj){
        return null

    }

    /**
     * Save postalCode object in DB and update cache utility accordingly
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public  Object execute(Object parameters, Object obj){
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            ExhPostalCode postalCode = (ExhPostalCode) preResult.get(POSTAL_CODE)
            ExhPostalCode savedPostalCode = exhPostalCodeService.create(postalCode)
            exhPostalCodeCacheUtility.add(savedPostalCode, exhPostalCodeCacheUtility.SORT_ON_CODE, exhPostalCodeCacheUtility.SORT_ORDER_ASCENDING)
            result.put(POSTAL_CODE, savedPostalCode)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, POSTAL_CODE_CREATE_FAILURE_MSG)
            return result
        }

    }

    /**
     * Show newly created postalCode object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public  Object buildSuccessResultForUI(Object obj){

        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            ExhPostalCode postalCode = (ExhPostalCode) executeResult.get(POSTAL_CODE)
            GridEntity object = new GridEntity()    //build grid object
            object.id = postalCode.id
            object.cell = [
                    Tools.LABEL_NEW,
                    postalCode.code
            ]
            result.put(Tools.MESSAGE, POSTAL_CODE_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, POSTAL_CODE_CREATE_SUCCESS_MSG)
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
            result.put(Tools.MESSAGE, POSTAL_CODE_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, POSTAL_CODE_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build postalCode object
     * @param parameterMap -serialized parameters from UI
     * @return -new postalCode object
     */
    private ExhPostalCode buildPostalCodeObject(GrailsParameterMap parameterMap){
        ExhPostalCode postalCode = new ExhPostalCode(parameterMap)
        postalCode.companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        return postalCode
    }

}
