package com.athena.mis.application.actions.district

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.District
import com.athena.mis.application.service.DistrictService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class CreateDistrictActionService extends BaseService implements ActionIntf {

    DistrictService districtService
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String DISTRICT = "district"
    private static String DISTRICT_CREATE_FAILURE_MSG = "District has not been saved"
    private static String DISTRICT_CAN_NOT_BE_GLOBAL = "Global district already exists"
    private static String DISTRICT_CREATE_SUCCESS_MSG = "District has been successfully saved"
    private static final String DISTRICT_NAME_MUST_BE_UNIQUE = "Same district name aleray exist"

    private final Logger log = Logger.getLogger(getClass())

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.IS_VALID, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)

            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            District districtInstance = buildDistrictObject(parameterMap)

            int duplicateName = districtService.countByNameIlike(districtInstance.name)
            if (duplicateName > 0) {
                result.put(Tools.MESSAGE, DISTRICT_NAME_MUST_BE_UNIQUE)
                return result
            }

            if(districtInstance.isGlobal){
                int duplicateIsGlobal = districtService.countByIsGlobal(true)
                if (duplicateIsGlobal > 0) {
                    result.put(Tools.MESSAGE, DISTRICT_CAN_NOT_BE_GLOBAL)
                    return result
                }
            }

            //check district district input validation
            districtInstance.validate()

            if (districtInstance.hasErrors()) {
                result.put(Tools.IS_VALID, new Boolean(false))
            } else {
                result.put(Tools.IS_VALID, new Boolean(true))
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(DISTRICT, districtInstance)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DISTRICT_CREATE_FAILURE_MSG)
            return result
        }
    }



    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            District projectInstance = (District) preResult.get(DISTRICT)
            District districtServiceReturn = districtService.create(projectInstance)
            districtCacheUtility.add(districtServiceReturn, districtCacheUtility.DEFAULT_SORT_PROPERTY, districtCacheUtility.SORT_ORDER_ASCENDING)
            result.put(DISTRICT, projectInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DISTRICT_CREATE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DISTRICT_CREATE_FAILURE_MSG)
            return result
        }
    }


    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj   // cast map returned from execute method
            District district = (District) receiveResult.get(DISTRICT)
            GridEntity object = new GridEntity()
            object.id = district.id
            object.cell = [
                    Tools.LABEL_NEW,
                    district.id,
                    district.name
            ]
            result.put(Tools.MESSAGE, DISTRICT_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DISTRICT_CREATE_FAILURE_MSG)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj  // // cast map returned from execute method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DISTRICT_CREATE_FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DISTRICT_CREATE_FAILURE_MSG)
            log.error(ex.getMessage())
            return result
        }
    }

    /**
     * Get newly built district object
     * @param parameterMap - serialize parameters from UI
     * @return - newly built district object
     */
    private District buildDistrictObject(GrailsParameterMap parameterMap) {
        AppUser user = appSessionUtil.getAppUser()
        District district =  new District()
        district.id = 0
        district.version = 0
        district.name = parameterMap.name.toString()
        district.isGlobal = Boolean.parseBoolean(parameterMap.isGlobal.toString())
        district.createdOn = new Date()
        district.createdBy = user.id
        district.updatedOn = null
        district.updatedBy = 0
        district.companyId = user.companyId
        return district
    }
}
