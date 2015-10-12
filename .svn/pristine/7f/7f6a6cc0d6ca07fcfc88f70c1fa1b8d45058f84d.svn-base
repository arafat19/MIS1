package com.athena.mis.application.actions.systementitytype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.SystemEntityTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  get search result list of systemEntityType for grid
 *  For details go through Use-Case doc named 'SearchSystemEntityTypeActionService'
 */
class SearchSystemEntityTypeActionService extends BaseService implements ActionIntf {

    @Autowired
    SystemEntityTypeCacheUtility systemEntityTypeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    protected final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load System Entity Type List"
    private static final String SE_TYPE_LIST = "systemEntityTypeList"
    private static final String COUNT = "count"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get search result list of systemEntityType for grid
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
            initSearch(parameterMap)

            String queryParams = Tools.PERCENTAGE + query + Tools.PERCENTAGE
            LinkedHashMap searchReturn = searchResult(queryParams)
            List lstSysEntityType = searchReturn.systemEntityTypeList
            int count = searchReturn.count

            result.put(Tools.COUNT, count)
            result.put(SE_TYPE_LIST, lstSysEntityType)
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
     * do nothing for post condition
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
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List seTypeList = (List) executeResult.get(SE_TYPE_LIST)
            int count = (int) executeResult.get(COUNT)
            List seTypeListWrap = wrapSysEntityTypeListInGrid(seTypeList, start)
            result = [page: pageNumber, total: count, rows: seTypeListWrap]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return null
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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

    /**
     * @param queryParameter -search query
     * @return -a map contains search result list of systemEntityType and count
     */
    private LinkedHashMap searchResult(String queryParameter) {
        String queryStr = """
            SELECT name,description,set.id,count(se.id) AS system_entity_count
            FROM system_entity_type set
            LEFT JOIN system_entity se on se.type = set.id AND se.company_id =:companyId
            WHERE ${queryType} ilike :queryParameter
            GROUP BY name,description,set.id
            ORDER BY set.id
            LIMIT :resultPerPage OFFSET :start
        """

        String countQuery = """
            SELECT COUNT(set.id)
            FROM system_entity_type set
            WHERE ${queryType} ilike :queryParameter
        """

        Map queryParams = [
                companyId: appSessionUtil.getCompanyId(),
                queryParameter: queryParameter,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> results = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(countQuery, queryParams)
        int count = countResults[0].count
        return [systemEntityTypeList: results, count: count]
    }
}
