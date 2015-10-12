package com.athena.mis.application.actions.systementity

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntityType
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.utility.SystemEntityTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for system entity CRUD and list of system entity for grid
 *  For details go through Use-Case doc named 'ShowSystemEntityActionService'
 */
class ShowSystemEntityActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    SystemEntityService systemEntityService
    @Autowired
    SystemEntityTypeCacheUtility systemEntityTypeCacheUtility

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load system entity information page"
    private static final String SYSTEM_ENTITY_LIST = "systemEntityList"
    private static final String INVALID_INPUT_MSG = "Failed to load page due to invalid input"
    private static final String SYSTEM_ENTITY_TYPE_NOT_FOUND = "System entity type not found"
    private static final String SYSTEM_ENTITY_TYPE_ID = "systemEntityTypeId"
    private static final String SYSTEM_ENTITY_NAME = "systemEntityName"

    /**
     * 1. Check required params
     * 2. Get system entity type
     * 3. Check the existence of system entity type object
     * @param parameters - parameters from UI
     * @param obj - N/A
     * @return - a map containing hasAccess(true/false), isError(true/false) depending on method success
     * map contains system entity type id
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.systemEntityTypeId) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }

            long systemEntityTypeId = Long.parseLong(params.systemEntityTypeId.toString())
            SystemEntityType systemEntityType = (SystemEntityType) systemEntityTypeCacheUtility.read(systemEntityTypeId)
            if (!systemEntityType) {
                result.put(Tools.MESSAGE, SYSTEM_ENTITY_TYPE_NOT_FOUND)
                return result
            }
            result.put(SYSTEM_ENTITY_TYPE_ID, systemEntityTypeId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Get system entity list
     * 1. Get system entity type id
     * 2. Get system entity type object
     * 3. Get list & count of system entity
     * @param params - parameters from UI
     * @param obj - map from executePreCondition method
     * @return - a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)  // default value

            LinkedHashMap preResult = (LinkedHashMap) obj
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)
            long systemEntityTypeId = Long.parseLong(preResult.get(SYSTEM_ENTITY_TYPE_ID).toString())
            SystemEntityType systemEntityType = (SystemEntityType) systemEntityTypeCacheUtility.read(systemEntityTypeId)

            // Get map of system entity list and count
            Map serviceReturn = systemEntityService.list(systemEntityTypeId, this)

            List<GroovyRowResult> lstSysEntity = serviceReturn.systemEntityList
            int count = serviceReturn.count

            result.put(SYSTEM_ENTITY_LIST, lstSysEntity)
            result.put(SYSTEM_ENTITY_TYPE_ID, systemEntityTypeId)
            result.put(SYSTEM_ENTITY_NAME, systemEntityType.name)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return null
        }
    }

    /**
     * Wrap the list of system entity in the grid entity
     * @param lstSysEntity - list of system entity
     * @param start - starting index of the page
     * @return - list of wrapped system entity
     */
    private List wrapListInGridEntityList(List<GroovyRowResult> lstSysEntity, int start) {
        List systemEntityList = []
        int counter = start + 1
        for (int i = 0; i < lstSysEntity.size(); i++) {
            GroovyRowResult systemEntity = lstSysEntity[i]
            GridEntity obj = new GridEntity()
            obj.id = systemEntity.id
            obj.cell = [
                    counter,
                    systemEntity.id,
                    systemEntity.key,
                    systemEntity.value,
                    systemEntity.is_active ? Tools.YES : Tools.NO,
                    systemEntity.reserved_id > 0 ? Tools.YES : Tools.NO
            ]
            systemEntityList << obj
            counter++
        }
        return systemEntityList
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap system entity list in gird entity
     * @param obj - map returned from execute method
     * @return - a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            Map executeResult = (Map) obj
            List lstSysEntity = (List) executeResult.get(SYSTEM_ENTITY_LIST)
            int count = (int) executeResult.count
            List resultList = wrapListInGridEntityList(lstSysEntity, start)
            Map output = [page: pageNumber, total: count, rows: resultList]
            result.put(SYSTEM_ENTITY_LIST, output)
            result.put(SYSTEM_ENTITY_TYPE_ID, executeResult.get(SYSTEM_ENTITY_TYPE_ID))
            result.put(SYSTEM_ENTITY_NAME, executeResult.get(SYSTEM_ENTITY_NAME))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }
}
