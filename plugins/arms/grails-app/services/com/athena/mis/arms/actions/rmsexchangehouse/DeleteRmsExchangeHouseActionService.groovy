package com.athena.mis.arms.actions.rmsexchangehouse

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.service.RmsExchangeHouseCurrencyPostingService
import com.athena.mis.arms.service.RmsExchangeHouseService
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.utility.RmsExchangeHouseCacheUtility
import com.athena.mis.arms.utility.UserRmsExchangeHouseCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Delete ExchangeHouse object from DB
 *  For details go through Use-Case doc named 'DeleteRmsExchangeHouseActionService'
 */
class DeleteRmsExchangeHouseActionService extends BaseService implements ActionIntf {

    RmsExchangeHouseService rmsExchangeHouseService
    RmsExchangeHouseCurrencyPostingService rmsExchangeHouseCurrencyPostingService
    RmsTaskService rmsTaskService
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    UserRmsExchangeHouseCacheUtility userRmsExchangeHouseCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete exchange house"
    private static final String EXCHANGE_HOUSE_DELETE_SUCCESS_MSG = "Exchange house has been successfully deleted"
    private static final String EXH_HOUSE_HAS_ASSOCIATION = "Exchange house has association with exh currency posting"
    private static final String EXH_HOUSE_HAS_ASSOCIATION_WITH_TASK = "task(s) associated with this exchange house"
    private static
    final String EXH_HOUSE_HAS_ASSOCIATION_WITH_APP_USER = "user(s) has association with this exchange house"

    /**
     * Checking pre condition before deleting the ExchangeHouse object
     * 1. Check validity for input
     * 2. Check existence of ExchangeHouse object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long exchangeHouseId = Long.parseLong(params.id)
            // Get ExchangeHouse object from cache utility
            RmsExchangeHouse oldExchangeHouse = (RmsExchangeHouse) rmsExchangeHouseCacheUtility.read(exchangeHouseId)
            if (!oldExchangeHouse) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            Map associationResult = hasAssociation(exchangeHouseId)
            boolean hasAssociation = (boolean) associationResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, associationResult.get(Tools.MESSAGE))
                return result
            }

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
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Delete ExchangeHouse object from DB
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long exchangeHouseId = Long.parseLong(parameterMap.id)
            rmsExchangeHouseService.delete(exchangeHouseId)          // Delete ExchangeHouse object from DB
            // Delete ExchangeHouse object from cache utility
            rmsExchangeHouseCacheUtility.delete(exchangeHouseId)
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
     * Build success message for delete
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        result.put(Tools.MESSAGE, EXCHANGE_HOUSE_DELETE_SUCCESS_MSG)
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
     * Check exhHouse association with relevant domains
     * @param exhHouseId - exhHouseId
     * @return-- a map containing hasAssociation(true/false) and relevant message
     */
    private Map hasAssociation(long exhHouseId) {

        Map hasAssociationResult = new LinkedHashMap()
        hasAssociationResult.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        int count = rmsExchangeHouseCurrencyPostingService.countByExchangeHouseId(exhHouseId)
        if (count > 0) {
            hasAssociationResult.put(Tools.MESSAGE, EXH_HOUSE_HAS_ASSOCIATION)
            hasAssociationResult.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)
            return hasAssociationResult
        }
        int countOfTask = rmsTaskService.countByExchangeHouseId(exhHouseId)
        if (countOfTask > 0) {
            hasAssociationResult.put(Tools.MESSAGE, countOfTask.toString() + EXH_HOUSE_HAS_ASSOCIATION_WITH_TASK)
            hasAssociationResult.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)
            return hasAssociationResult
        }
        int countByExhHouseId = userRmsExchangeHouseCacheUtility.countByExchangeHouseId(exhHouseId)
        if (countByExhHouseId > 0) {
            hasAssociationResult.put(Tools.MESSAGE, countByExhHouseId.toString() + EXH_HOUSE_HAS_ASSOCIATION_WITH_APP_USER)
            hasAssociationResult.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)
            return hasAssociationResult
        }
        return hasAssociationResult
    }
}
