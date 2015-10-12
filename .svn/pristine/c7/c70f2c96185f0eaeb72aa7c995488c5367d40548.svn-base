package com.athena.mis.arms.actions.rmsexchangehouse

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.utility.RmsExchangeHouseCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select ExchangeHouse object
 *  For details go through Use-Case doc named 'SelectRmsExchangeHouseActionService'
 */
class SelectRmsExchangeHouseActionService extends BaseService implements ActionIntf {

    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String EXCHANGE_HOUSE_NOT_FOUND_MESSAGE = "Selected exchange house is not found"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to select exchange house"

    /**
     * Do nothing for pre operation
     */
    Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for post operation
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get parameters from UI and build ExchangeHouse object for select
     * 1. Check validity for input
     * 2. Check existence of ExchangeHouse object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long exchangeHouseId = Long.parseLong(parameterMap.id)
            // Get ExchangeHouse object from cache utility
            RmsExchangeHouse exchangeHouse = (RmsExchangeHouse) rmsExchangeHouseCacheUtility.read(exchangeHouseId)
            if (!exchangeHouse) {
                result.put(Tools.MESSAGE, EXCHANGE_HOUSE_NOT_FOUND_MESSAGE)
                return result
            }
            result.put(Tools.ENTITY, exchangeHouse)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCHANGE_HOUSE_NOT_FOUND_MESSAGE)
            return result
        }
    }

    /**
     * Build a map with ExchangeHouse object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            RmsExchangeHouse exchangeHouse = (RmsExchangeHouse) executeResult.get(Tools.ENTITY)
            result.put(Tools.ENTITY, exchangeHouse)
            result.put(Tools.VERSION, exchangeHouse.version)
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
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!obj) {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
                return result
            }
            Map previousResult = (Map) obj
            result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
}
