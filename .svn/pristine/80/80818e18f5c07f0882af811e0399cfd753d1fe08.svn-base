package com.athena.mis.application.actions.systementitytype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.service.SystemEntityTypeService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.SystemEntityTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for systemEntityType CRUD and list of systemEntityType for grid
 *  For details go through Use-Case doc named 'ShowSystemEntityTypeActionService'
 */
class ShowSystemEntityTypeActionService extends BaseService implements ActionIntf {

    SystemEntityTypeService systemEntityTypeService
    @Autowired
    SystemEntityTypeCacheUtility systemEntityTypeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load system-entity-type page"
    private static final String SYSTEM_ENTITY_TYPE_LIST = "systemEntityTypeList"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get systemEntityType list for grid
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)

            Map serviceReturn = lstSystemEntityType()
            List<GroovyRowResult> lstSysEntityType = serviceReturn.systemEntityTypeList
            int count = serviceReturn.count

            result.put(SYSTEM_ENTITY_TYPE_LIST, lstSysEntityType)
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
     * do nothing for pre condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap systemEntityType list for grid
     * @param obj -map returned from execute method
     * @return -a map containing Wrap systemEntityType list for grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            Map executeResult = (Map) obj
            List seTypeList = (List) executeResult.get(SYSTEM_ENTITY_TYPE_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List resultSeTypeList = wrapSysEntityTypeListInGrid(seTypeList, start)
            Map output = [page: pageNumber, total: count, rows: resultSeTypeList]
            result.put(SYSTEM_ENTITY_TYPE_LIST, output)
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

    /**
     *
     * @param seTypeList -list of GroovyRowResult
     * @param start -starting index of the page
     * @return -wrapped systemEntityTypeList
     */
    private List wrapSysEntityTypeListInGrid(List<GroovyRowResult> seTypeList, int start) {
        List lstSysEntityTypes = []
        int counter = start + 1
        for (int i = 0; i < seTypeList.size(); i++) {
            GroovyRowResult systemEntityType = seTypeList[i]
            GridEntity obj = new GridEntity()
            obj.id = systemEntityType.id
            obj.cell = [
                    counter,
                    systemEntityType.id,
                    systemEntityType.name,
                    systemEntityType.description,
                    systemEntityType.system_entity_count
            ]
            lstSysEntityTypes << obj
            counter++
        }
        return lstSysEntityTypes
    }

    private static final String SYSTEM_ENTITY_TYPE_SELECT_QUERY = """
            SELECT name,description,set.id,count(se.id) AS system_entity_count
            FROM system_entity_type set
            LEFT JOIN system_entity se on se.type = set.id AND se.company_id =:companyId
            GROUP BY name,description,set.id
            ORDER BY set.id
            LIMIT :resultPerPage OFFSET :start
    """

    /**
     * @return -a map contains list of systemEntityType and count
     */
    private LinkedHashMap lstSystemEntityType() {
        Map queryParams = [
                companyId: appSessionUtil.getCompanyId(),
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> systemEntityTypeList = executeSelectSql(SYSTEM_ENTITY_TYPE_SELECT_QUERY, queryParams)
        int count = systemEntityTypeService.countSystemEntityType()
        return [systemEntityTypeList: systemEntityTypeList, count: count]
    }
}
