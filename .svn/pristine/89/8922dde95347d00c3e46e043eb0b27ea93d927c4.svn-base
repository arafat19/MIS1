package com.athena.mis.arms.actions.rmsexchangehouse

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Country
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.service.RmsExchangeHouseService
import com.athena.mis.arms.utility.RmsExchangeHouseCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Create new ExchangeHouse object
 *  For details go through Use-Case doc named 'CreateRmsExchangeHouseActionService'
 */
class CreateRmsExchangeHouseActionService extends BaseService implements ActionIntf {

    RmsExchangeHouseService rmsExchangeHouseService
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    CountryCacheUtility countryCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private final static String NAME_EXIST_MESSAGE = "Same name already exists"
    private final static String CODE_EXIST_MESSAGE = "Same code already exists"
    private static final String EXCHANGE_HOUSE_OBJ = "exchangeHouse"
    private static final String EXCHANGE_HOUSE_CREATE_FAILURE_MSG = "Exchange house has not been saved"
    private static final String EXCHANGE_HOUSE_CREATE_SUCCESS_MSG = "Exchange house has been successfully saved"

    /**
     * Get parameters from UI and build ExchangeHouse object
     * 1. Build ExchangeHouse object with new parameters
     * 2. Check existence of name
     * 3. Check existence of code
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            RmsExchangeHouse exchangeHouse = buildExchangeHouse(parameterMap)     // build ExchangeHouse object
            int countName = rmsExchangeHouseCacheUtility.countByNameIlike(exchangeHouse.name)
            if (countName > 0) {
                result.put(Tools.MESSAGE, NAME_EXIST_MESSAGE)
                return result
            }
            int countCode = rmsExchangeHouseCacheUtility.countByCode(exchangeHouse.code)
            if (countCode > 0) {
                result.put(Tools.MESSAGE, CODE_EXIST_MESSAGE)
                return result
            }
            result.put(EXCHANGE_HOUSE_OBJ, exchangeHouse)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCHANGE_HOUSE_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Save ExchangeHouse object in DB
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from executePreCondition method
            RmsExchangeHouse exchangeHouse = (RmsExchangeHouse) preResult.get(EXCHANGE_HOUSE_OBJ)
            // save new ExchangeHouse object in DB
            RmsExchangeHouse savedExchangeHouseObj = rmsExchangeHouseService.create(exchangeHouse)
            // save new ExchangeHouse object in cache utility
            rmsExchangeHouseCacheUtility.add(savedExchangeHouseObj, rmsExchangeHouseCacheUtility.SORT_ON_NAME, rmsExchangeHouseCacheUtility.SORT_ORDER_ASCENDING)
            result.put(EXCHANGE_HOUSE_OBJ, savedExchangeHouseObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCHANGE_HOUSE_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build grid object to show a single row (newly created object) in grid
     * 1. Get country object by id
     * 2. build success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(true/false) depending on method success
     */
    Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj               // cast map returned from execute method
            RmsExchangeHouse exchangeHouse = (RmsExchangeHouse) executeResult.get(EXCHANGE_HOUSE_OBJ)
            long countryId = exchangeHouse.countryId
            Country country = (Country) countryCacheUtility.read(countryId)
            GridEntity object = new GridEntity()                            // build grid object
            object.id = exchangeHouse.id
            object.cell = [
                    Tools.LABEL_NEW,
                    exchangeHouse.id,
                    exchangeHouse.name,
                    exchangeHouse.code,
                    country.name,
                    exchangeHouse.balance
            ]
            result.put(Tools.MESSAGE, EXCHANGE_HOUSE_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCHANGE_HOUSE_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj               // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, EXCHANGE_HOUSE_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCHANGE_HOUSE_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build new ExchangeHouse object
     * @param parameterMap -serialized parameters from UI
     * @return -new ExchangeHouse object
     */
    private RmsExchangeHouse buildExchangeHouse(GrailsParameterMap parameterMap) {
        RmsExchangeHouse exchangeHouse = new RmsExchangeHouse(parameterMap)
        exchangeHouse.companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        return exchangeHouse
    }
}
