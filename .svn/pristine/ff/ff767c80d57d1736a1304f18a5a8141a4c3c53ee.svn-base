package com.athena.mis.arms.actions.rmsexchangehousecurrencyposting

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.entity.RmsExchangeHouseCurrencyPosting
import com.athena.mis.arms.service.RmsExchangeHouseCurrencyPostingService
import com.athena.mis.arms.service.RmsExchangeHouseService
import com.athena.mis.arms.utility.RmsExchangeHouseCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Update ExchangeHouseCurrencyPosting object
 *  For details go through Use-Case doc named 'UpdateRmsExchangeHouseCurrencyPostingActionService'
 */
class UpdateRmsExchangeHouseCurrencyPostingActionService extends BaseService implements ActionIntf {

    RmsExchangeHouseCurrencyPostingService rmsExchangeHouseCurrencyPostingService
    RmsExchangeHouseService rmsExchangeHouseService
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String OBJ_NOT_FOUND = "Selected exchange house currency posting not found"
    private static final String EXH_HOUSE_CUR_POSTING_OBJ = "exhHouseCurPosting"
    private static final String EXH_HOUSE_CUR_POSTING_UPDATE_FAILURE_MESSAGE = "Exchange house currency posting could not be updated"
    private static final String EXH_HOUSE_CUR_POSTING_UPDATE_SUCCESS_MESSAGE = "Exchange house currency posting has been updated successfully"

    /**
     * Get parameters from UI and build ExchangeHouseCurrencyPosting object for update
     * 1. Check validity for input
     * 2. Check existence of ExchangeHouseCurrencyPosting object
     * 3. Build ExchangeHouseCurrencyPosting object with new parameters
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
            long exhHouseCurPostingId = Long.parseLong(parameterMap.id)
            // Get ExchangeHouseCurrencyPosting object from DB
            RmsExchangeHouseCurrencyPosting oldExhHouseCurPosting = rmsExchangeHouseCurrencyPostingService.read(exhHouseCurPostingId)
            if (!oldExhHouseCurPosting) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            //Build ExchangeHouseCurrencyPosting object with new parameters
            RmsExchangeHouseCurrencyPosting exhHouseCurPosting = buildExhHouseCurPostingObject(parameterMap, oldExhHouseCurPosting)

            result.put(EXH_HOUSE_CUR_POSTING_OBJ, exhHouseCurPosting)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXH_HOUSE_CUR_POSTING_UPDATE_FAILURE_MESSAGE)
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
     * Update ExchangeHouseCurrencyPosting object in DB
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
            RmsExchangeHouseCurrencyPosting exhHouseCurPosting = (RmsExchangeHouseCurrencyPosting) preResult.get(EXH_HOUSE_CUR_POSTING_OBJ)
            rmsExchangeHouseCurrencyPostingService.update(exhHouseCurPosting)        // update new ExchangeHouseCurrencyPosting object in DB
            updateExchangeHouseBalance(exhHouseCurPosting.exchangeHouseId)
            result.put(EXH_HOUSE_CUR_POSTING_OBJ, exhHouseCurPosting)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(EXH_HOUSE_CUR_POSTING_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXH_HOUSE_CUR_POSTING_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build grid object to show a single row (updated object) in grid
     * 1. Get ExchangeHouse object by id
     * 2. Get AppUser object by id for createdBy
     * 3. Get AppUser object by id for updatedBy
     * 4. build success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(true/false) depending on method success
     */
    Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj               // cast map returned from execute method
            RmsExchangeHouseCurrencyPosting exhHouseCurPosting = (RmsExchangeHouseCurrencyPosting) executeResult.get(EXH_HOUSE_CUR_POSTING_OBJ)
            long exchangeHouseId = exhHouseCurPosting.exchangeHouseId
            // Get ExchangeHouse object by id
            RmsExchangeHouse exchangeHouse = (RmsExchangeHouse) rmsExchangeHouseCacheUtility.read(exchangeHouseId)
            long createdById = exhHouseCurPosting.createdBy
            AppUser createdBy = (AppUser) appUserCacheUtility.read(createdById)       // Get AppUser object by id for createdBy
            long updatedById = exhHouseCurPosting.updatedBy
            AppUser updatedBy = (AppUser) appUserCacheUtility.read(updatedById)       // Get AppUser object by id for updatedBy
            GridEntity object = new GridEntity()            // build grid object
            object.id = exhHouseCurPosting.id
            object.cell = [
                    Tools.LABEL_NEW,
                    exhHouseCurPosting.id,
                    exchangeHouse.name,
                    exhHouseCurPosting.amount,
                    createdBy.username,
                    DateUtility.getLongDateForUI(exhHouseCurPosting.createdOn),
                    updatedBy.username,
                    DateUtility.getLongDateForUI(exhHouseCurPosting.updatedOn)
            ]
            result.put(Tools.MESSAGE, EXH_HOUSE_CUR_POSTING_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, exhHouseCurPosting.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXH_HOUSE_CUR_POSTING_UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, EXH_HOUSE_CUR_POSTING_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXH_HOUSE_CUR_POSTING_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Make existing/old object Up-to-date with new parameters
     * @param parameterMap -serialized parameters from UI
     * @param oldExhHouseCurPosting -old ExhHouseCurPosting object
     * @return -updated ExhHouseCurPosting object
     */
    private RmsExchangeHouseCurrencyPosting buildExhHouseCurPostingObject(GrailsParameterMap parameterMap, RmsExchangeHouseCurrencyPosting oldExhHouseCurPosting) {
        RmsExchangeHouseCurrencyPosting newExhHouseCurPosting = new RmsExchangeHouseCurrencyPosting(parameterMap)
        oldExhHouseCurPosting.exchangeHouseId = newExhHouseCurPosting.exchangeHouseId
        oldExhHouseCurPosting.taskId = newExhHouseCurPosting.taskId
        oldExhHouseCurPosting.amount = newExhHouseCurPosting.amount
        oldExhHouseCurPosting.updatedOn = new Date()
        oldExhHouseCurPosting.updatedBy = rmsSessionUtil.appSessionUtil.getAppUser().id
        return oldExhHouseCurPosting
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
