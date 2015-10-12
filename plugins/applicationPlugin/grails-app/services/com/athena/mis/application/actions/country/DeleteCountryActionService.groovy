package com.athena.mis.application.actions.country

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Country
import com.athena.mis.application.service.CountryService
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete country object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteCountryActionService'
 */
class DeleteCountryActionService extends BaseService implements ActionIntf {

    CountryService countryService

    @Autowired
    CountryCacheUtility countryCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String COUNTRY_DELETE_SUCCESS_MSG = "Country has been successfully deleted!"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete country"
    private static final String COUNTRY_NOT_FOUND_MESSAGE = "Selected country not found"
    private static final String HAS_ASSOCIATION_MESSAGE = " Company(s) Associated with selected Country"

    private final static COUNTRY_OBJECT = "country"
    private static final String DELETED = "deleted"
    /**
     * 1. pull country object from cache utility by country id
     * 2. check country existence
     * 3. Check company-country association
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing country object necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long countryId = Long.parseLong(parameterMap.id.toString())
            Country country = (Country) countryCacheUtility.read(countryId)
            if (!country) {
                result.put(Tools.MESSAGE, COUNTRY_NOT_FOUND_MESSAGE)
                return result
            }

            int count = checkAssociationForDelete(country)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE)
                return result
            }

            result.put(COUNTRY_OBJECT, country)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Delete country object from DB and cache utility
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -N/A
     * @param obj - object receive from pre execute method
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Country country = (Country) preResult.get(COUNTRY_OBJECT)
            countryService.delete(country.id)
            countryCacheUtility.delete(country.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(DELETED, Boolean.TRUE)
        result.put(Tools.MESSAGE, COUNTRY_DELETE_SUCCESS_MSG)
        return result
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
    private static final String QUERY_STR = """
            SELECT COUNT(id) AS count
                FROM company
                WHERE country_id=:countryId
            """
    /**
     * Get total country number
     * @param country - country object
     * @return - int value of total number of countries
     */
    private int checkAssociationForDelete(Country country) {

        Map queryParams = [
                countryId: country.id
        ]
        List results = executeSelectSql(QUERY_STR, queryParams)
        int count = results[0].count
        return count
    }
}
