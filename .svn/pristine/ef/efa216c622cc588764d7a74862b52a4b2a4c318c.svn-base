package com.athena.mis.sarb.actions.province

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.sarb.entity.SarbProvince
import com.athena.mis.sarb.utility.SarbProvinceCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
/**
 *  Select of province and show in grid
 *  For details go through Use-Case doc named 'SelectSarbProvinceActionService'
 */
class SelectSarbProvinceActionService extends BaseService implements ActionIntf {


    @Autowired
    SarbProvinceCacheUtility sarbProvinceCacheUtility

    private static final String PROVINCE_NOT_FOUND_MESSAGE ="Province not found message"
    private static final String DEFAULT_FAILURE_MESSAGE="Failed to select Province"

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
     * Get province object by id
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
            long provinceId = Long.parseLong(parameterMap.id)
            SarbProvince province = (SarbProvince) sarbProvinceCacheUtility.read(provinceId)    // get province object
            // check whether the province object exists or not
            if(!province){
                result.put(Tools.MESSAGE,PROVINCE_NOT_FOUND_MESSAGE)
            }
            result.put(Tools.ENTITY,province)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROVINCE_NOT_FOUND_MESSAGE)
            return result
        }
    }

    /**
     * Build a map with province object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public  Object buildSuccessResultForUI(Object obj){
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            SarbProvince province = (SarbProvince) executeResult.get(Tools.ENTITY)
            result.put(Tools.ENTITY, province)
            result.put(Tools.VERSION, province.version.toInteger())
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
