package com.athena.mis.application.actions.country

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Country
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.service.CountryService
import com.athena.mis.application.service.CurrencyService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update new country object and show in grid
 *  For details go through Use-Case doc named 'UpdateCountryActionService'
 */
class UpdateCountryActionService extends BaseService implements ActionIntf {

    CountryService countryService
    CurrencyService currencyService
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private final static String COUNTRY_UPDATE_SUCCESS_MESSAGE = " Country has been updated successfully"
    private final static String COUNTRY_UPDATE_FAILURE_MESSAGE = "Country could not be updated"
    private final static String COUNTRY_OBJECT = "country"
    private final static String OBJ_NOT_FOUND = "Selected country not found"
    private final static String NAME_EXIST_MESSAGE = "Same country name already exists "
    private final static String CODE_EXIST_MESSAGE = "Same country code already exists "
    private final static String ISD_CODE_EXIST_MESSAGE = "Same ISD code already exists "
    private final static String NATIONALITY_EXIST_MESSAGE = "Same nationality already exists"
    /**
     * 1. pull country object from cache utility
     * 2. Check country existence
     * 3. build country object
     * 4. check uniqueness for name, code, isd-code, nationality
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
            Country oldCountry = countryService.read(countryId)
            if (!oldCountry) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            Country country = buildCountryObject(oldCountry, parameterMap)

            int countName = countryCacheUtility.countByNameAndIdNotEqual(country.name, country.id)
            if (countName > 0) {
                result.put(Tools.MESSAGE, NAME_EXIST_MESSAGE)
                return result
            }

            int countCode = countryCacheUtility.countByCodeAndIdNotEqual(country.code, country.id)
            if (countCode > 0) {
                result.put(Tools.MESSAGE, CODE_EXIST_MESSAGE)
                return result
            }

            int countIsdCode = countryCacheUtility.countByIsdCodeAndIdNotEqual(country.isdCode, country.id)
            if (countIsdCode > 0) {
                result.put(Tools.MESSAGE, ISD_CODE_EXIST_MESSAGE)
                return result
            }

            int countNationality = countryCacheUtility.countByNationalityAndIdNotEqual(country.nationality, country.id)
            if (countNationality > 0) {
                result.put(Tools.MESSAGE, NATIONALITY_EXIST_MESSAGE)
                return result
            }

            result.put(COUNTRY_OBJECT, country)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, COUNTRY_UPDATE_FAILURE_MESSAGE)
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
     * Update country object in DB and update cache utility accordingly
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Country country = (Country) preResult.get(COUNTRY_OBJECT)
            countryService.update(country)
            countryCacheUtility.update(country, countryCacheUtility.SORT_ON_NAME, countryCacheUtility.SORT_ORDER_ASCENDING)
            result.put(COUNTRY_OBJECT, country)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, COUNTRY_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * 1. Show newly updated country object in grid
     * 2. Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            Country country = (Country) executeResult.get(COUNTRY_OBJECT)
            Currency currency = currencyService.read(country.currencyId)
            GridEntity object = new GridEntity()
            object.id = country.id
            object.cell = [
                    Tools.LABEL_NEW,
                    country.id,
                    country.name,
                    country.code,
                    country.isdCode,
                    country.nationality,
                    currency.name
            ]
            result.put(Tools.MESSAGE, COUNTRY_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, country.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, COUNTRY_UPDATE_FAILURE_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, COUNTRY_UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, COUNTRY_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Build country object
     * @param params -serialized parameters from UI
     * @return -new country object
     */
    private Country buildCountryObject(Country oldCountry, GrailsParameterMap parameterMap) {
        Country newCountry = new Country(parameterMap)
        oldCountry.code = newCountry.code
        oldCountry.isdCode = newCountry.isdCode
        oldCountry.name = newCountry.name
        oldCountry.nationality = newCountry.nationality
        oldCountry.currencyId = Long.parseLong(parameterMap.currencyId.toString())
        oldCountry.updatedOn = new Date()
        oldCountry.updatedBy = appSessionUtil.getAppUser().id
        if (parameterMap.phoneNumberPattern) {
            oldCountry.phoneNumberPattern = newCountry.phoneNumberPattern      // for super admin
        }
        return oldCountry
    }
}
