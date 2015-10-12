package com.athena.mis.sarb.actions.province

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.sarb.entity.SarbProvince
import com.athena.mis.sarb.service.SarbProvinceService
import com.athena.mis.sarb.utility.SarbProvinceCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Delete province object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteSarbProvinceActionService'
 */
class DeleteSarbProvinceActionService extends BaseService implements ActionIntf {

    @Autowired
    SarbProvinceCacheUtility sarbProvinceCacheUtility
    SarbProvinceService sarbProvinceService

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE="Province could not be deleted"
    private static final String PROVINCE_DELETE_SUCCESS_MSG ="successfully deleted"


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
            long provinceId = Long.parseLong(params.id)
            SarbProvince province = (SarbProvince) sarbProvinceCacheUtility.read(provinceId)    // get province object
            // check whether selected province object exists or not
            if (!province) {
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
     * Delete province object from DB and cache utility
     * This function is in transactional boundary and will roll back in case of any exception
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public  Object execute(Object parameters, Object obj){

        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long provinceId = Long.parseLong(parameterMap.id)
            sarbProvinceService.delete(provinceId)    // delete province object from DB
            SarbProvince province = (SarbProvince) sarbProvinceCacheUtility.read(provinceId)
            sarbProvinceCacheUtility.delete(province.id)   // delete province object from cache utility
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
        result.put(Tools.MESSAGE, PROVINCE_DELETE_SUCCESS_MSG)
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
