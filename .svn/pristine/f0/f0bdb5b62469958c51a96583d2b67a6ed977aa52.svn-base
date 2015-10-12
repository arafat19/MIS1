package com.athena.mis.arms.actions.rmsexchangehousecurrencyposting

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.arms.entity.RmsExchangeHouseCurrencyPosting
import com.athena.mis.arms.service.RmsExchangeHouseCurrencyPostingService
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 *  Select ExchangeHouseCurrencyPosting object
 *  For details go through Use-Case doc named 'SelectRmsExchangeHouseCurrencyPostingActionService'
 */
class SelectRmsExchangeHouseCurrencyPostingActionService extends  BaseService implements ActionIntf {

    RmsExchangeHouseCurrencyPostingService rmsExchangeHouseCurrencyPostingService

    private final Logger log = Logger.getLogger(getClass())

    private static final String EXH_HOUSE_CUR_POSTING_NOT_FOUND_MESSAGE = "Selected exchange house currency posting is not found"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to select exchange house currency posting"

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
     * Get parameters from UI and build ExchangeHouseCurrencyPosting object for select
     * 1. Check validity for input
     * 2. Check existence of ExchangeHouseCurrencyPosting object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long exhHouseCurPostingId = Long.parseLong(parameterMap.id)
            // Get ExchangeHouseCurrencyPosting object by id
            RmsExchangeHouseCurrencyPosting exhHouseCurPosting = rmsExchangeHouseCurrencyPostingService.read(exhHouseCurPostingId)
            if (!exhHouseCurPosting) {
                result.put(Tools.MESSAGE, EXH_HOUSE_CUR_POSTING_NOT_FOUND_MESSAGE)
                return result
            }
            result.put(Tools.ENTITY, exhHouseCurPosting)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXH_HOUSE_CUR_POSTING_NOT_FOUND_MESSAGE)
            return result
        }
    }

    /**
     * Build a map with ExchangeHouseCurrencyPosting object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            RmsExchangeHouseCurrencyPosting exhHouseCurPosting = (RmsExchangeHouseCurrencyPosting) executeResult.get(Tools.ENTITY)
            result.put(Tools.ENTITY, exhHouseCurPosting)
            result.put(Tools.VERSION, exhHouseCurPosting.version)
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
