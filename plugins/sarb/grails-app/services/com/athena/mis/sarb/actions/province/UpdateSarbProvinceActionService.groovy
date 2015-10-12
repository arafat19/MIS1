package com.athena.mis.sarb.actions.province

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.sarb.entity.SarbProvince
import com.athena.mis.sarb.service.SarbProvinceService
import com.athena.mis.sarb.utility.SarbProvinceCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Update of province and show in grid
 *  For details go through Use-Case doc named 'UpdateSarbProvinceActionService'
 */
class UpdateSarbProvinceActionService extends BaseService implements ActionIntf {

    @Autowired
    SarbProvinceCacheUtility sarbProvinceCacheUtility
    SarbProvinceService sarbProvinceService

    private static final String OBJ_NOT_FOUND="Province not found"
    private static final String PROVINCE ="province"
    private static final String PROVINCE_UPDATE_FAILURE_MESSAGE ="Province could not be updated"
    private static final String PROVINCE_UPDATE_SUCCESS_MESSAGE ="Province update success message"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Get parameters from UI and build province object for update
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
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
            long provinceId = Long.parseLong(parameterMap.id)
            SarbProvince oldProvince =(SarbProvince) sarbProvinceService.read(provinceId) // get province object
            // check whether selected province object exists or not
            if (!oldProvince) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            SarbProvince province = buildProvince(parameterMap, oldProvince)  // build province object for update
            result.put(PROVINCE, province)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROVINCE_UPDATE_FAILURE_MESSAGE)
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
     * Update province object in DB & update cache utility accordingly
     * This function is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public  Object execute(Object parameters, Object obj){

        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            SarbProvince province = (SarbProvince) preResult.get(PROVINCE)
            sarbProvinceService.update(province)
            sarbProvinceCacheUtility.update(province, sarbProvinceCacheUtility.SORT_ON_NAME, sarbProvinceCacheUtility.SORT_ORDER_ASCENDING)
            result.put(PROVINCE, province)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROVINCE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Show updated province object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public  Object buildSuccessResultForUI(Object obj){

        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            SarbProvince province = (SarbProvince) executeResult.get(PROVINCE)
            GridEntity object = new GridEntity()    // build grid entity object
            object.id = province.id
            object.cell = [
                    Tools.LABEL_NEW,
                    province.name
            ]
            result.put(Tools.MESSAGE, PROVINCE_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROVINCE_UPDATE_SUCCESS_MESSAGE)
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
            result.put(Tools.MESSAGE, PROVINCE_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROVINCE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build province object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldProvince -old oldProvince object
     * @return -updated oldProvince object
     */
    private SarbProvince buildProvince(GrailsParameterMap parameterMap, oldProvince){
        SarbProvince province = new SarbProvince(parameterMap)
        province.id = oldProvince.id
        province.version = oldProvince.version
        province.companyId = oldProvince.companyId
        return province
    }
}
