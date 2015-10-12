package com.athena.mis.application.actions.systementitytype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.SystemEntityType
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.service.SystemEntityTypeService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.SystemEntityTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update SystemEntityType CRUD and to show on grid list
 *  For details go through Use-Case doc named 'UpdateSystemEntityTypeActionService'
 */
class UpdateSystemEntityTypeActionService extends BaseService implements ActionIntf {

    private static final String UPDATE_FAILURE_MESSAGE = "System Entity Type could not be updated"
    private static final String UPDATE_SUCCESS_MESSAGE = "System Entity Type has been updated successfully"
    private static final String SE_TYPE_OBJ_NOT_FOUND = "System Entity Type object not found"
    private static final String SE_TYPE_OBJ = "systemEntityType"
    private static final String DUPLICATE_ENTITY_MSG = "Same system entity type name already exists"

    private final Logger log = Logger.getLogger(getClass())

    SystemEntityTypeService systemEntityTypeService
    SystemEntityService systemEntityService
    @Autowired
    SystemEntityTypeCacheUtility systemEntityTypeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * Check existence of old systemEntityType object before update
     * @param parameters -parameter send from UI
     * @param obj -N/A
     * @return -a map containing SystemEntityType object for execute method
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            long id = Long.parseLong(parameterMap.id.toString())
            SystemEntityType oldSEType = systemEntityTypeService.read(id)
            if (!oldSEType) {
                result.put(Tools.MESSAGE, SE_TYPE_OBJ_NOT_FOUND)
                return result
            }

            //build object to update
            SystemEntityType newSEType = buildSystemEntityTypeObject(parameterMap, oldSEType)

            // Check duplicate system entity type
            int duplicateCount = systemEntityTypeCacheUtility.countByNameAndIdNotEqual(newSEType.name, newSEType.id)

            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, DUPLICATE_ENTITY_MSG)
                return result
            }

            result.put(SE_TYPE_OBJ, newSEType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update SystemEntityType Object
     * @param parameters -N/A
     * @param obj -SystemEntityType Object from executePreCondition method
     * @return -a map contains SystemEntityType object for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            SystemEntityType seType = (SystemEntityType) preResult.get(SE_TYPE_OBJ)
            systemEntityTypeService.update(seType)
            systemEntityTypeCacheUtility.update(seType, systemEntityTypeCacheUtility.SORT_ON_NAME, systemEntityTypeCacheUtility.SORT_ORDER_ASCENDING)
            result.put(SE_TYPE_OBJ, seType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap updated SystemEntityType object to show on grid
     * @param obj -SystemEntityType object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            SystemEntityType seType = (SystemEntityType) executeResult.get(SE_TYPE_OBJ)
            GridEntity object = new GridEntity()
            object.id = seType.id
            long companyId = appSessionUtil.getCompanyId()
            int systemEntityCount = systemEntityService.countByTypeAndCompanyId(seType.id, companyId)
            object.cell = [
                    Tools.LABEL_NEW,
                    seType.id,
                    seType.name,
                    seType.description,
                    systemEntityCount
            ]

            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build SystemEntityType object to update
     * @param parameterMap -serialized parameters from UI
     * @param oldSystemEntityType -SystemEntityType object
     * @return -SystemEntityType object
     */
    private SystemEntityType buildSystemEntityTypeObject(GrailsParameterMap parameterMap, SystemEntityType oldSystemEntityType) {
        SystemEntityType newSystemEntityType = new SystemEntityType(parameterMap)
        oldSystemEntityType.name = newSystemEntityType.name
        oldSystemEntityType.description = newSystemEntityType.description
        return oldSystemEntityType
    }
}
