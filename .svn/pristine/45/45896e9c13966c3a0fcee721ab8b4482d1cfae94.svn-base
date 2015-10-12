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
 *  Create new designation object and show in grid
 *  For details go through Use-Case doc named 'CreateCountryActionService'
 */
class CreateCountryActionService extends BaseService implements ActionIntf {

    CountryService countryService
    CurrencyService currencyService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    CountryCacheUtility countryCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private final static String COUNTRY_CREATE_SUCCESS_MESSAGE = "Country has been successfully saved"
    private final static String COUNTRY_CREATE_FAILURE_MESSAGE = "Country has not been saved"
    private final static String COUNTRY_OBJECT = "country"
    private final static String NAME_EXIST_MESSAGE = "Same country name already exists"
    private final static String CODE_EXIST_MESSAGE = "Same country code already exists"
    private final static String ISD_CODE_EXIST_MESSAGE = "Same ISD code already exists"
    private final static String NATIONALITY_EXIST_MESSAGE = "Same nationality already exists"
    private static final String DEFAULT_PHONE_NUMBER_PATTERN = '[0-9]{11}'
    /**
     * 1. Build country object
     * 2. Existing check of name, code, isd-code, nationality
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing designation object necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            Country country = buildCountryObject(parameterMap)

            int countName = countryCacheUtility.countByName(country.name)

            if (countName > 0) {
                result.put(Tools.MESSAGE, NAME_EXIST_MESSAGE)
                return result
            }

            int countCode = countryCacheUtility.countByCode(country.code)
            if (countCode > 0) {
                result.put(Tools.MESSAGE, CODE_EXIST_MESSAGE)
                return result
            }

            int countIsdCode = countryCacheUtility.countByIsdCode(country.isdCode)
            if (countIsdCode > 0) {
                result.put(Tools.MESSAGE, ISD_CODE_EXIST_MESSAGE)
                return result
            }

            int countNationality = countryCacheUtility.countByNationality(country.nationality)
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
            result.put(Tools.MESSAGE, COUNTRY_CREATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }
    /**
     * Save country object in DB and update cache utility accordingly
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
            Country newCountry = countryService.create(country)
            countryCacheUtility.add(newCountry, countryCacheUtility.SORT_ON_NAME, countryCacheUtility.SORT_ORDER_ASCENDING)
            result.put(COUNTRY_OBJECT, newCountry)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(COUNTRY_CREATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, COUNTRY_CREATE_SUCCESS_MESSAGE)
            return result
        }
    }
    /**
     * 1. Show newly created country object in grid
     * 2. Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            Country country = (Country) receiveResult.get(COUNTRY_OBJECT)
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
            result.put(Tools.MESSAGE, COUNTRY_CREATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, COUNTRY_CREATE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, COUNTRY_CREATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, COUNTRY_CREATE_FAILURE_MESSAGE)

            return result
        }
    }
    /**
     * Build country object
     * @param params -serialized parameters from UI
     * @return -new country object
     */
    private Country buildCountryObject(GrailsParameterMap params) {
        Country country = new Country(params)
        country.companyId = appSessionUtil.getCompanyId()
        long currencyId = Long.parseLong(params.currencyId.toString())
        Currency currency = currencyService.read(currencyId)
        country.currencyId = currency.id
        if (params.phoneNumberPattern) {
            country.phoneNumberPattern = params.phoneNumberPattern     // for super admin
        } else {
            country.phoneNumberPattern = DEFAULT_PHONE_NUMBER_PATTERN      // for admin
        }

        country.createdOn = new Date()
        country.createdBy = appSessionUtil.getAppUser().id
        country.updatedOn = null
        country.updatedBy = 0L

        return country
    }
}
