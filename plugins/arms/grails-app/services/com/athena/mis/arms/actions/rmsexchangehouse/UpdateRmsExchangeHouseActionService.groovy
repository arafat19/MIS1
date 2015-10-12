package com.athena.mis.arms.actions.rmsexchangehouse

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Country
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.service.RmsExchangeHouseService
import com.athena.mis.arms.utility.RmsExchangeHouseCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Update ExchangeHouse object
 *  For details go through Use-Case doc named 'UpdateRmsExchangeHouseActionService'
 */
class UpdateRmsExchangeHouseActionService extends BaseService implements ActionIntf {

    RmsExchangeHouseService rmsExchangeHouseService
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    CountryCacheUtility countryCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String OBJ_NOT_FOUND = "Selected exchange house not found"
    private final static String NAME_EXIST_MESSAGE = "Same name already exists"
    private final static String CODE_EXIST_MESSAGE = "Same code already exists"
    private static final String EXCHANGE_HOUSE_OBJ = "exchangeHouse"
    private static final String EXCHANGE_HOUSE_UPDATE_FAILURE_MESSAGE = "Exchange house could not be updated"
    private static final String EXCHANGE_HOUSE_UPDATE_SUCCESS_MESSAGE = "Exchange house has been updated successfully"

    /**
     * Get parameters from UI and build ExchangeHouse object for update
     * 1. Check validity for input
     * 2. Check existence of ExchangeHouse object
     * 3. Build ExchangeHouse object with new parameters
     * 4. Check existence of name
     * 5. Check existence of code
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long exchangeHouseId = Long.parseLong(parameterMap.id)
            // Get ExchangeHouse object from DB
            RmsExchangeHouse oldExchangeHouse = rmsExchangeHouseService.read(exchangeHouseId)
             if (!oldExchangeHouse) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            // Build ExchangeHouse object
            RmsExchangeHouse exchangeHouse = buildExchangeHouseObject(parameterMap, oldExchangeHouse)

            int countName = rmsExchangeHouseCacheUtility.countByNameIlikeAndIdNotEqual(exchangeHouse.name, exchangeHouse.id)
            if (countName > 0) {
                result.put(Tools.MESSAGE, NAME_EXIST_MESSAGE)
                return result
            }
            int countCode = rmsExchangeHouseCacheUtility.countByCodeAndIdNotEqual(exchangeHouse.code, exchangeHouse.id)
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
            result.put(Tools.MESSAGE, EXCHANGE_HOUSE_UPDATE_FAILURE_MESSAGE)
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
     * Update ExchangeHouse object in DB
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj               // cast map returned from executePreCondition method
            RmsExchangeHouse exchangeHouse = (RmsExchangeHouse) preResult.get(EXCHANGE_HOUSE_OBJ)
            rmsExchangeHouseService.update(exchangeHouse)            // update new ExchangeHouse object in DB
            // update new ExchangeHouse object in cache utility
            rmsExchangeHouseCacheUtility.update(exchangeHouse, rmsExchangeHouseCacheUtility.SORT_ON_NAME, rmsExchangeHouseCacheUtility.SORT_ORDER_ASCENDING)
            result.put(EXCHANGE_HOUSE_OBJ, exchangeHouse)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCHANGE_HOUSE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build grid object to show a single row (updated object) in grid
     * 1. Get country object by id
     * 2. build success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(true/false) depending on method success
     */
    Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj                   // cast map returned from execute method
            RmsExchangeHouse exchangeHouse = (RmsExchangeHouse) executeResult.get(EXCHANGE_HOUSE_OBJ)
            long countryId = exchangeHouse.countryId
            Country country = (Country) countryCacheUtility.read(countryId)     // Pull Country object by id
            GridEntity object = new GridEntity()                                // build grid object
            object.id = exchangeHouse.id
            object.cell = [
                    Tools.LABEL_NEW,
                    exchangeHouse.id,
                    exchangeHouse.name,
                    exchangeHouse.code,
                    country.name,
                    exchangeHouse.balance
            ]
            result.put(Tools.MESSAGE, EXCHANGE_HOUSE_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, exchangeHouse.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCHANGE_HOUSE_UPDATE_FAILURE_MESSAGE)
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
                LinkedHashMap preResult = (LinkedHashMap) obj                       // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, EXCHANGE_HOUSE_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCHANGE_HOUSE_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Make existing/old object Up-to-date with new parameters
     * @param parameterMap -serialized parameters from UI
     * @param oldExchangeHouse -old ExchangeHouse object
     * @return -updated ExchangeHouse object
     */
    private RmsExchangeHouse buildExchangeHouseObject(GrailsParameterMap parameterMap, RmsExchangeHouse oldExchangeHouse) {
        RmsExchangeHouse newExchangeHouse = new RmsExchangeHouse(parameterMap)
        oldExchangeHouse.name = newExchangeHouse.name
        oldExchangeHouse.code = newExchangeHouse.code
        oldExchangeHouse.countryId = newExchangeHouse.countryId
        return oldExchangeHouse
    }
}
