package com.athena.mis.application.actions.systementity

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 *  Show list of system entity for grid
 *  For details go through Use-Case doc named 'ListSystemEntityActionService'
 */
class ListSystemEntityActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    SystemEntityService systemEntityService

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load system entity page"
    private static final String SYSTEM_ENTITY_LIST = "systemEntityList"
    private static final String SYSTEM_ENTITY_TYPE_NOT_FOUND = "System entity type not found"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get system entity list for grid
     * @param params - parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for buildSuccessResultForUI
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap) // initialize parameters for flexGrid

            long systemEntityTypeId = Long.parseLong(parameterMap.systemEntityTypeId.toString())
            // checking is system entity type id or not
            if (systemEntityTypeId < 0) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, SYSTEM_ENTITY_TYPE_NOT_FOUND)
                return result
            }
            // get map of system entity list and it's total count
            Map serviceReturn = systemEntityService.list(systemEntityTypeId, this)

            List<GroovyRowResult> lstSysEntity = serviceReturn.systemEntityList
            int count = serviceReturn.count

            result.put(SYSTEM_ENTITY_LIST, lstSysEntity)
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
     * Wrap list of system entity in grid entity
     * @param lstSysEntity - list of system entity
     * @param start - starting index of the page
     * @return - list of wrapped system entity
     */
    private List wrapSystemEntityListInGridEntity(List<GroovyRowResult> lstSysEntity, int start) {
        List systemEntityList = [] as List
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
     * Wrap system entity list for grid
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
            List resultList = wrapSystemEntityListInGridEntity(lstSysEntity, start)
            Map output = [page: pageNumber, total: count, rows: resultList]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     *  Build failure result in case of any error
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
