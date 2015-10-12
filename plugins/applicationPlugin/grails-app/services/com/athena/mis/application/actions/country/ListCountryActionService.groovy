package com.athena.mis.application.actions.country

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.service.CountryService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show list of country for grid
 *  For details go through Use-Case doc named 'ListCountryActionService'
 */
class ListCountryActionService extends BaseService implements ActionIntf {

    CountryService countryService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    CountryCacheUtility countryCacheUtility

    private static final String SHOW_COUNTRY_FAILURE_MESSAGE = "Failed to load country page"
    private static final String COUNTRY_LIST = "countryList"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. initialize parameters for flexGrid
     * 2. get country list
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing country list & count necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)
            List<GroovyRowResult> countryList = listOfCountries()
            int count = countryCacheUtility.count()
            result.put(COUNTRY_LIST, countryList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_COUNTRY_FAILURE_MESSAGE)
            return null
        }
    }
    /**
     * Wrap country list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            Map executeResult = (Map) obj
            List<GroovyRowResult> countryList = (List<GroovyRowResult>) executeResult.get(COUNTRY_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List<GroovyRowResult> resultList = wrapListInGridEntityList(countryList, start)
            Map output = [page: pageNumber, total: count, rows: resultList]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_COUNTRY_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, SHOW_COUNTRY_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_COUNTRY_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Wrap list of country in grid entity
     * @param countryList -list of country object(s)
     * @param start -starting index of the page
     * @return -list of wrapped country
     */
    private List wrapListInGridEntityList(List<GroovyRowResult> countryList, int start) {
        List<GroovyRowResult> lstCountry = [] as List
        int counter = start + 1
        for (int i = 0; i < countryList.size(); i++) {
            GridEntity obj = new GridEntity()
            GroovyRowResult eachRow = countryList[i]
            obj.id = countryList[i].id
            obj.cell = [
                    counter,
                    eachRow.id,
                    eachRow.name,
                    eachRow.code,
                    eachRow.isd_code,
                    eachRow.nationality,
                    eachRow.currency_name
            ]
            lstCountry << obj
            counter++
        }
        return lstCountry
    }

    private static final String QUERY_COUNTRY_LIST =
            """
            SELECT co.id,co.name,co.code,co.isd_code,co.nationality, cu.name currency_name
                FROM country as co
                LEFT JOIN currency cu ON cu.id = co.currency_id
                WHERE co.company_id = :companyId
                LIMIT :resultPerPage OFFSET :start
        """
    // It will return list of Countries
    private List<GroovyRowResult> listOfCountries() {
        Map queryParams = [
                resultPerPage: resultPerPage,
                start: start,
                companyId: appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> lstCountries = executeSelectSql(QUERY_COUNTRY_LIST, queryParams)
        return lstCountries
    }

}