package com.athena.mis.application.actions.systementity

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search system entity and show specific list of system entity for grid
 *  For details go through Use-Case doc named 'SearchSystemEntityActionService'
 */
class SearchSystemEntityActionService extends BaseService implements ActionIntf {

    SystemEntityService systemEntityService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load System Entity Information list"
    private static final String SYSTEM_ENTITY_LIST = "systemEntityList"
    private static final String SYSTEM_ENTITY_TYPE_NOT_FOUND = "System entity type not found"

    /**
     * Check if user has access to search system entity or not
     * @param parameters - N/A
     * @param obj - N/A
     * @return - a map containing hasAccess(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)   // default value
            // only development role type user can search system entity
            if (appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * Get system entity list for grid through specific search
     * @param params - parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for buildSuccessResultForUI
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long systemEntityTypeId = Long.parseLong(parameterMap.systemEntityTypeId.toString())
            // check the existence of system entity type
            if (systemEntityTypeId < 0) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, SYSTEM_ENTITY_TYPE_NOT_FOUND)
                return result
            }
            initSearch(parameterMap)// initialize parameters for flexGrid

            String queryParams = Tools.PERCENTAGE + query + Tools.PERCENTAGE
            // get map of system entity list and it's count by search keyword
            LinkedHashMap searchReturn = systemEntityService.searchResult(this, queryParams, systemEntityTypeId)
            List lstSysEntity = searchReturn.systemEntityList
            int count = searchReturn.count

            result.put(SYSTEM_ENTITY_LIST, lstSysEntity)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return null
        }
    }

    /**
     * Wrap list of system entity in grid entity
     * @param lstSysEntity - list of system entity
     * @param start - starting index of the page
     * @return -list of wrapped system entity
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
        try {
            Map executeResult = (Map) obj
            List lstSysEntity = (List) executeResult.systemEntityList
            int count = (int) executeResult.count

            List resultList = wrapSystemEntityListInGridEntity(lstSysEntity, start)
            return [page: pageNumber, total: count, rows: resultList]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null]
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }

}
