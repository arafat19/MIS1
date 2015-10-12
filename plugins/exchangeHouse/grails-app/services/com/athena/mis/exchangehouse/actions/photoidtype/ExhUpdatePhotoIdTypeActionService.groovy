package com.athena.mis.exchangehouse.actions.photoidtype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.exchangehouse.entity.ExhPhotoIdType
import com.athena.mis.exchangehouse.service.ExhPhotoIdTypeService
import com.athena.mis.exchangehouse.utility.ExhPhotoIdTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ExhUpdatePhotoIdTypeActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass());

    private static String UPDATE_FAILURE_MSG = "Photo id type could not be updated"
    private static String UPDATE_SUCCESS_MSG = "Photo id type has been updated successfully"
    private static final String NOT_FOUND_MSG = "Selected photo id type not found"
    private static final String PHOTO_ID_TYPE_OBJ = "photoIdTypeObj"

    ExhPhotoIdTypeService exhPhotoIdTypeService

    @Autowired
    ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)                            // set default
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            if (!parameterMap.id) {                                             // check required parameters
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long photoIdTypeId = Long.parseLong(parameterMap.id.toString())
            ExhPhotoIdType oldPhotoIdType = (ExhPhotoIdType) exhPhotoIdTypeCacheUtility.read(photoIdTypeId)
            // get bank object from cache utitlity

            if (!oldPhotoIdType) {
                // check whether selected bank object exists or not
                result.put(Tools.MESSAGE, NOT_FOUND_MSG)
                return result
            }

            ExhPhotoIdType exhPhotoIdType = buildPhotoIdType(parameterMap, oldPhotoIdType)
            // build bank object for update

            result.put(PHOTO_ID_TYPE_OBJ, exhPhotoIdType)
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
            ExhPhotoIdType exhPhotoIdType = (ExhPhotoIdType) preResult.get(PHOTO_ID_TYPE_OBJ)
            int updateCount = exhPhotoIdTypeService.update(exhPhotoIdType)
            exhPhotoIdTypeCacheUtility.update(exhPhotoIdType, exhPhotoIdTypeCacheUtility.DEFAULT_SORT_PROPERTY, exhPhotoIdTypeCacheUtility.SORT_ORDER_ASCENDING)
            result.put(PHOTO_ID_TYPE_OBJ, exhPhotoIdType)
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

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj                   // cast map returned from execute method
            ExhPhotoIdType photoIdType = (ExhPhotoIdType) executeResult.get(PHOTO_ID_TYPE_OBJ)
            // get bank object from executeResult
            GridEntity object = new GridEntity()                                // build grid entity object
            object.id = photoIdType.id
            object.cell = [
                    Tools.LABEL_NEW,
                    photoIdType.id,
                    photoIdType.name,
                    photoIdType.code,
                    photoIdType.isSecondary ? Tools.YES : Tools.NO
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, photoIdType.version)
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

    private ExhPhotoIdType buildPhotoIdType(GrailsParameterMap parameterMap, ExhPhotoIdType oldPhotoIdType) {
        ExhPhotoIdType newPhotoIdType = new ExhPhotoIdType(parameterMap)
        oldPhotoIdType.name = newPhotoIdType.name
        oldPhotoIdType.code = newPhotoIdType.code
        oldPhotoIdType.isSecondary = newPhotoIdType.isSecondary
        return oldPhotoIdType
    }
}
