package com.athena.mis.application.actions.country

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Search specific country/countries
 *  For details go through Use-Case doc named 'SearchCountryActionService'
 */
class SearchCountryActionService extends BaseService implements ActionIntf {

    @Autowired
    AppSessionUtil appSessionUtil

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load Country List"
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
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            initSearch(params)

            List<GroovyRowResult> lstOfCountries = search()
            int count = count()
            result.put(Tools.COUNT, count)
            result.put(COUNTRY_LIST, lstOfCountries)
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
     * Wrap country list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<GroovyRowResult> countryList = (List<GroovyRowResult>) executeResult.get(COUNTRY_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List<GroovyRowResult> countryListWrap = wrapListInGridEntityList(countryList, start)
            result = [page: pageNumber, total: count, rows: countryListWrap]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return null
        }
    }

    /**
     * Wrap list of country in grid entity
     * @param countryList -list of country object(s)
     * @param start -starting index of the page
     * @return -list of wrapped country
     */
    private List wrapListInGridEntityList(List<GroovyRowResult> countryList, int start) {
        List<GroovyRowResult> lstCountry = []
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
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    // It will return list of Country
    private List<GroovyRowResult> search() {
        String strQuery =
                """
                 SELECT co.id,co.name,co.code,co.isd_code,co.nationality, cu.name currency_name
                    FROM country as co
                    LEFT JOIN currency cu ON cu.id = co.currency_id
                    WHERE ${queryType} ilike :query
                      AND co.company_id = :companyId
                    LIMIT :resultPerPage OFFSET :start
            """

        Map queryParams = [
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                resultPerPage: resultPerPage,
                start: start,
                companyId: appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> lstCountryDetails = executeSelectSql(strQuery, queryParams)
        return lstCountryDetails
    }

    // It will return list of Country
    private int count() {
        String queryCount =
                """
                SELECT COUNT(co.id) FROM country  co
                    LEFT JOIN currency cu ON cu.id = co.currency_id
                    WHERE ${queryType} ilike :query
                         AND co.company_id = :companyId
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
