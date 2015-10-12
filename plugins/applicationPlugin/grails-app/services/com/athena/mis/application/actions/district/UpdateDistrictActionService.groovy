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

class UpdateDistrictActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass());

    // constants
    private static final String UPDATE_FAILURE_MSG = "District could not be updated"
    private static final String UPDATE_SUCCESS_MSG = "District has been updated successfully"
    private static final String DISTRICT_NAME_MUST_BE_UNIQUE = "District name must be unique"
    private static final String NOT_FOUND_MSG = "Selected district not found"
    private static final String DISTRICT_OBJ = "districtObj"
    private static String DISTRICT_CAN_NOT_BE_GLOBAL = "Global district already exists"

    DistrictService districtService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    DistrictCacheUtility districtCacheUtility

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)                            // set default
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            if (!parameterMap.id) {                                             // check required parameters
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long districtId = Long.parseLong(parameterMap.id.toString())
            District oldDistrict = districtService.read(districtId)     // get district object service
            if (!oldDistrict) {
                // check whether selected district object exists or not
                result.put(Tools.MESSAGE, NOT_FOUND_MSG)
                return result
            }
            District district = buildDistrict(parameterMap, oldDistrict)             // build district object for update

            int count = districtService.countByIsGlobalAndIdNotEqual(true, district.id)
            if (count > 0) {
                result.put(Tools.MESSAGE, DISTRICT_CAN_NOT_BE_GLOBAL)
                return result
            }

            if (districtService.countByNameAndIdNotEqual(district.name, district.id) > 0) {
                result.put(Tools.MESSAGE, DISTRICT_NAME_MUST_BE_UNIQUE)
                return result
            }
            result.put(DISTRICT_OBJ, district)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        }

    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            // cast map returned from executePreCondition method
            District district = (District) preResult.get(DISTRICT_OBJ)
            int updateCount = districtService.update(district)
            districtCacheUtility.update(district, districtCacheUtility.DEFAULT_SORT_PROPERTY, districtCacheUtility.SORT_ORDER_ASCENDING)
            result.put(DISTRICT_OBJ, district)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(UPDATE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        }
    }

    /**
     do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null;
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj                   // cast map returned from execute method
            District district = (District) executeResult.get(DISTRICT_OBJ)
            GridEntity object = new GridEntity();                                // build grid entity object
            object.id = district.id;
            object.cell = [
                    Tools.LABEL_NEW,
                    district.id,
                    district.name
            ];
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, district.version)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MSG)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Builds UI specific object on failure;
     * @param obj Object to be used to determine building of UI result
     * @return Object to be used for rendering at UI level
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap objMap = (LinkedHashMap) obj
                result.put(Tools.MESSAGE, objMap.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
            return result
        }
    }

    private District buildDistrict(GrailsParameterMap parameterMap, District oldDistrict) {
        District newDistrict = new District(parameterMap)
        oldDistrict.name = newDistrict.name
        oldDistrict.isGlobal = newDistrict.isGlobal
        AppUser systemUser = appSessionUtil.getAppUser()
        oldDistrict.updatedOn = new Date()
        oldDistrict.updatedBy = systemUser.id
        return oldDistrict
    }

}

