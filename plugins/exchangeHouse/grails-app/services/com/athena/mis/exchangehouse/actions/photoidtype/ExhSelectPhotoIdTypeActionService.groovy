package com.athena.mis.exchangehouse.actions.photoidtype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhPhotoIdType
import com.athena.mis.exchangehouse.utility.ExhPhotoIdTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class ExhSelectPhotoIdTypeActionService extends BaseService implements ActionIntf {

    // auto-wiring required services

    private final Logger log = Logger.getLogger(getClass())
    @Autowired
    ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        Map outputMap = new HashMap()
        try {

            if (exhSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                outputMap.put("hasAccess", new Boolean(true))
            } else {
                outputMap.put("hasAccess", new Boolean(false))
            }
            return outputMap
        } catch (Exception ex) {
            outputMap.put("hasAccess", new Boolean(false))
            log.error(ex.getMessage())
            return outputMap
        }
    }

    /**
     * Select the photoIdType by id (primary key) and returns
     *
     * @param params request parameters
     * @param obj additional parameters, not required for this action
     * @return return PhotoIdType instance and version info in a map
     */
    public Object execute(Object parameters, Object obj) {
        try {
            ExhPhotoIdType photoIdTypeInstance = (ExhPhotoIdType) exhPhotoIdTypeCacheUtility.read(Long.parseLong(parameters.id))
            return photoIdTypeInstance
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    /**
     * Select photoIdType has not post-condition
     *
     * @param paramters
     * @param obj
     * @return nothing
     */
    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }


    public Object buildSuccessResultForUI(Object photoIdTypeInstance) {
        Map result = [] as LinkedHashMap
        try {
            ExhPhotoIdType exhPhotoIdType=(ExhPhotoIdType) photoIdTypeInstance
            result = [entity: exhPhotoIdType, version: exhPhotoIdType.version]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return result
        }
    }

    /**
     * Builds UI specific object on failure;
     *
     * @param obj Object to be used to determine building of UI result
     * @return Object to be used for rendering at UI level
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = [] as LinkedHashMap
        try {
            result = [isError: true, entity: null, errors: null, message: ENTITY_NOT_FOUND_ERROR_MESSAGE]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return result
        }
    }

}
