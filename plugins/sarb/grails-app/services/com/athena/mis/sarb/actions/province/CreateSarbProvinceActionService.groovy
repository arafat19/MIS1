package com.athena.mis.sarb.actions.province

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.sarb.entity.SarbProvince
import com.athena.mis.sarb.service.SarbProvinceService
import com.athena.mis.sarb.utility.SarbProvinceCacheUtility
import com.athena.mis.sarb.utility.SarbSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Creat of province and show in grid
 *  For details go through Use-Case doc named 'CreateSarbProvinceActionService'
 */
class CreateSarbProvinceActionService extends BaseService implements ActionIntf {


    @Autowired
    SarbProvinceCacheUtility sarbProvinceCacheUtility
    @Autowired
    SarbSessionUtil sarbSessionUtil
    SarbProvinceService sarbProvinceService

    private static final String PROVINCE_CREATE_SUCCESS_MSG = "Province has been successfully saved"
    private static final String PROVINCE_CREATE_FAILURE_MSG = "Province has not been saved"
    private static final String PROVINCE = "province"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Get parameters from UI and build province object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public  Object executePreCondition(Object parameters, Object obj){

        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            SarbProvince province = buildProvinceObject(parameterMap)   // build province object
            result.put(PROVINCE, province)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROVINCE_CREATE_FAILURE_MSG)
            return result
        }

    }
    /**
     * Do Nothing for execute post condition
     */
    public  Object executePostCondition(Object parameters, Object obj){
        return null

    }

    /**
     * Save province object in DB and update cache utility accordingly
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
            SarbProvince province = (SarbProvince) preResult.get(PROVINCE)
            SarbProvince savedProvince = sarbProvinceService.create(province)
            sarbProvinceCacheUtility.add(savedProvince,sarbProvinceCacheUtility.SORT_ON_NAME, sarbProvinceCacheUtility.SORT_ORDER_ASCENDING)
            result.put(PROVINCE, savedProvince)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROVINCE_CREATE_FAILURE_MSG)
            return result
        }

    }

    /**
     * Show newly created province object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public  Object buildSuccessResultForUI(Object obj){
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            SarbProvince province = (SarbProvince) executeResult.get(PROVINCE)
            GridEntity object = new GridEntity()    //build grid object
            object.id = province.id
            object.cell = [
                    Tools.LABEL_NEW,
                    province.name
            ]
            result.put(Tools.MESSAGE, PROVINCE_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROVINCE_CREATE_SUCCESS_MSG)
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
            result.put(Tools.MESSAGE, PROVINCE_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROVINCE_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build province object
     * @param parameterMap -serialized parameters from UI
     * @return -new province object
     */
    private SarbProvince buildProvinceObject(GrailsParameterMap parameterMap){
        SarbProvince province = new SarbProvince(parameterMap)
        province.companyId = sarbSessionUtil.appSessionUtil.getCompanyId()
        return province
    }
}
