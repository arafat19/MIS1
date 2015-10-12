package com.athena.mis.application.actions.company

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Company
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for company CRUD and list of company for grid
 *  For details go through Use-Case doc named 'ShowCompanyActionService'
 */
class ShowCompanyActionService extends BaseService implements ActionIntf {
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load company page"
    private static final String COMPANY_LIST = "companyList"
    private static final String COUNT = "count"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * check access permission to show company page
     * @param parameters -N/A
     * @param obj -N/A
     * @return -boolean value of access permission(true/false)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            //Only developmentUser has right to get companyObjectList
            if (!appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * get list of companyObjects
     * @param parameters -N/A
     * @param obj -N/A
     * @return -a map contains companyList and count
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            initPager(params)
            Map serviceReturn = listCompany()

            List<GroovyRowResult> companyList = serviceReturn.companyList
            int count = serviceReturn.count

            result.put(COMPANY_LIST, companyList)
            result.put(COUNT, count)
            return result
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
     * @param obj -a map contains companyObjectList and count receives from execute method
     * @return -wrapped companyObjectList to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            Map executeResult = (Map) obj
            List<Company> companyList = (List<Company>) executeResult.companyList
            int count = (int) executeResult.count
            List resultCompanyList = wrapListInGridEntityList(companyList, start)
            Map output = [page: pageNumber, total: count, rows: resultCompanyList]
            result.put(COMPANY_LIST, output)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
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
            GroovyRowResult company = companyList[i]
            GridEntity obj = new GridEntity()
            obj.id = company.id
            obj.cell = [
                    counter,
                    company.id,
                    company.name,
                    company.code,
                    company.web_url,
                    company.email,
                    company.country_name
            ]
            companies << obj
            counter++
        }
        return companies
    }

    private static final String SELECT_COMPANY_QUERY = """
            SELECT com.id, com.name, com.code, com.web_url, com.email, cou.name as country_name
            FROM company com
            LEFT JOIN country cou ON cou.id = com.country_id
            WHERE com.id =:companyId
            ORDER BY com.name asc
            LIMIT :resultPerPage OFFSET :start
        """

    private static final String SELECT_COMPANY_COUNT_QUERY = """
              SELECT COUNT(com.id)
              FROM company com
              LEFT JOIN country cou ON cou.id = com.country_id
              WHERE com.id =:companyId
        """
    /**
     * get list of company  object(s) for grid
     * @return -a map contains company list & count
     */
    private Map listCompany() {
        Map queryParams = [
                companyId: appSessionUtil.getCompanyId(),
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstCompany = executeSelectSql(SELECT_COMPANY_QUERY, queryParams)
        List<GroovyRowResult> resultCount = executeSelectSql(SELECT_COMPANY_COUNT_QUERY, queryParams)
        int count = (int) resultCount[0][0]

        return [companyList: lstCompany, count: count]
    }
}
