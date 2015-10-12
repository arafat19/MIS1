package com.athena.mis.arms.actions.rmsexchangehousecurrencyposting

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.arms.entity.RmsExchangeHouseCurrencyPosting
import com.athena.mis.arms.service.RmsExchangeHouseCurrencyPostingService
import com.athena.mis.arms.service.RmsExchangeHouseService
import com.athena.mis.arms.utility.RmsExchangeHouseCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Delete ExchangeHouseCurrencyPosting object from DB
 *  For details go through Use-Case doc named 'DeleteRmsExchangeHouseCurrencyPostingActionService'
 */
class DeleteRmsExchangeHouseCurrencyPostingActionService extends BaseService implements ActionIntf {

    RmsExchangeHouseCurrencyPostingService rmsExchangeHouseCurrencyPostingService
    RmsExchangeHouseService rmsExchangeHouseService
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete exchange house currency posting"
    private static final String EXH_HOUSE_CUR_POSTING_DELETE_SUCCESS_MSG = "Exchange house currency posting has been successfully deleted"
    private static final String EXH_CURR_POSTING_OBJ = "exhHouseCurrencyPosting"

    /**
     * Checking pre condition before deleting the ExchangeHouseCurrencyPosting object
     * 1. Check validity for input
     * 2. Check existence of ExchangeHouseCurrencyPosting object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long exhHouseCurPostingId = Long.parseLong(params.id)
            RmsExchangeHouseCurrencyPosting oldExhHouseCurPosting = rmsExchangeHouseCurrencyPostingService.read(exhHouseCurPostingId)                   // Get RmsExchangeHouseCurrencyPosting object from DB
            if (!oldExhHouseCurPosting) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            result.put(EXH_CURR_POSTING_OBJ, oldExhHouseCurPosting)
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
     * Do nothing for post operation
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Delete ExchangeHouseCurrencyPosting object from DB
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult=(Map)parameters
            RmsExchangeHouseCurrencyPosting exhCurrPosting=(RmsExchangeHouseCurrencyPosting)executeResult.get(EXH_CURR_POSTING_OBJ)
            // Delete ExchangeHouseCurrencyPosting object from DB
            rmsExchangeHouseCurrencyPostingService.delete(exhCurrPosting.id)
            updateExchangeHouseBalance(exhCurrPosting.exchangeHouseId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build success message for delete
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        result.put(Tools.MESSAGE, EXH_HOUSE_CUR_POSTING_DELETE_SUCCESS_MSG)
        return result
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * update RmsExchangeHouse redundant property - balance
     * @param exhHouseId - RmsExchangeHouse.id
     */
    private void updateExchangeHouseBalance(long exhHouseId) {
        double amount = rmsExchangeHouseCurrencyPostingService.getBalanceAmount(exhHouseId)
        rmsExchangeHouseService.updateBalance(exhHouseId, amount)
        rmsExchangeHouseCacheUtility.updateBalance(exhHouseId, amount)
    }
}
