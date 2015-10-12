package com.athena.mis.application.actions.company

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Class to get search list of company to show on grid
 *  For details go through Use-Case doc named 'SearchCompanyActionService'
 */
class SearchCompanyActionService extends BaseService implements ActionIntf {
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to search company grid list"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * check access permission to get companyList
     * @param parameters -N/A
     * @param obj -N/A
     * @return -boolean value of access permission(true/false)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            //Only developmentUser has right to get companyObjectList
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
     * get search list of companyObjects
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains companyList and count
     */
    public Object execute(Object parameters, Object obj = null) {
        LinkedHashMap result
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if ((!params.sortname) || (params.sortname.toString().equals(ID))) {
                params.sortname = companyCacheUtility.SORT_ON_NAME
                params.sortorder = companyCacheUtility.SORT_ORDER_ASCENDING
            }
            initSearch(params)
            List<GroovyRowResult> companyList = search()
            int count = count()
            return [companyList: companyList, count: count]
        } catch (Exception ex) {
            log.error(ex.getMessage())
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
     * wrap companyObjectList to show on grid
     * @param obj -a map contains companyObjectList and count
     * @return -wrapped companyObjectList to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<GroovyRowResult> companyList = (List<GroovyRowResult>) executeResult.companyList
            int count = (int) executeResult.count
            List company = wrapListInGridEntityList(companyList, start)
            return [page: pageNumber, total: count, rows: company]
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * wrappedCompanyObjectList for grid
     * @param companyList - list of company objects
     * @param start -start index
     * @return -wrappedCompanyObjectList
     */
    private List wrapListInGridEntityList(List<GroovyRowResult> companyList, int start) {
        List companies = [] as List
        int counter = start + 1
        for (int i = 0; i < companyList.size(); i++) {
            GridEntity obj = new GridEntity()
            GroovyRowResult companyEachRow = companyList[i]
            obj.id = companyEachRow.id
            obj.cell = [
                    counter,
                    companyEachRow.id,
                    companyEachRow.name,
                    companyEachRow.code,
                    companyEachRow.web_url,
                    companyEachRow.email,
                    companyEachRow.country_name
            ]
            companies << obj
            counter++
        }
        return companies
    }

    // It will return list of Company
    private List<GroovyRowResult> search() {
        String strQuery =
                """
                SELECT com.id, com.name, com.code, com.web_url, com.email, cou.name as country_name
                FROM company com
                LEFT JOIN country cou ON cou.id = com.country_id
                WHERE ${queryType} ilike :query
                AND com.id =:companyId
                LIMIT :resultPerPage OFFSET :start
            """

        Map queryParams = [
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                resultPerPage: resultPerPage,
                start: start,
                companyId: appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> lstCompanyDetails = executeSelectSql(strQuery, queryParams)
        return lstCompanyDetails
    }


    private int count() {
        String queryCount =
                """
               SELECT COUNT(com.id)
               FROM company com
               LEFT JOIN country cou ON cou.id = com.country_id
               WHERE ${queryType} ilike :query
               AND com.id =:companyId
            """

        Map queryParams = [
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                companyId: appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> result = executeSelectSql(queryCount, queryParams)
        int count = (int) result[0][0]
        return count
    }


}
