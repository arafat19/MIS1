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
 *  Create new ExchangeHouseCurrencyPosting object
 *  For details go through Use-Case doc named 'CreateRmsExchangeHouseCurrencyPostingActionService'
 */
class CreateRmsExchangeHouseCurrencyPostingActionService extends BaseService implements ActionIntf {

    RmsExchangeHouseCurrencyPostingService rmsExchangeHouseCurrencyPostingService
    RmsExchangeHouseService rmsExchangeHouseService
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String EXH_HOUSE_CUR_POSTING_OBJ = "exhHouseCurPosting"
    private static final String EXH_HOUSE_CUR_POSTING_CREATE_FAILURE_MSG = "Exchange house currency posting has not been saved"
    private static final String EXH_HOUSE_CUR_POSTING_CREATE_SUCCESS_MSG = "Exchange house currency posting has been successfully saved"

    /**
     * Get parameters from UI and build ExchangeHouseCurrencyPosting object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // build ExchangeHouseCurrencyPosting object
            RmsExchangeHouseCurrencyPosting exhHouseCurPosting = buildExhHouseCurPostingObject(parameterMap)
            result.put(EXH_HOUSE_CUR_POSTING_OBJ, exhHouseCurPosting)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXH_HOUSE_CUR_POSTING_CREATE_FAILURE_MSG)
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
     * Save ExchangeHouseCurrencyPosting object in DB
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
            RmsExchangeHouseCurrencyPosting exhHouseCurPosting = (RmsExchangeHouseCurrencyPosting) preResult.get(EXH_HOUSE_CUR_POSTING_OBJ)
            RmsExchangeHouseCurrencyPosting savedCurPostingObj = rmsExchangeHouseCurrencyPostingService.create(exhHouseCurPosting)    // save new RmsExchangeHouseCurrencyPosting object in DB
            updateExchangeHouseBalance(exhHouseCurPosting.exchangeHouseId)
            result.put(EXH_HOUSE_CUR_POSTING_OBJ, savedCurPostingObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXH_HOUSE_CUR_POSTING_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build grid object to show a single row (newly created object) in grid
     * 1. Get ExchangeHouse object by id
     * 2. Get AppUser object by id for createdBy
     * 3. build success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(true/false) depending on method success
     */
    Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj               // cast map returned from execute method
            RmsExchangeHouseCurrencyPosting exhHouseCurPosting = (RmsExchangeHouseCurrencyPosting) executeResult.get(EXH_HOUSE_CUR_POSTING_OBJ)
            long exchangeHouseId = exhHouseCurPosting.exchangeHouseId
            // Get ExchangeHouse object by id
            RmsExchangeHouse rmsExchangeHouse = (RmsExchangeHouse) rmsExchangeHouseCacheUtility.read(exchangeHouseId)
            long createdById = exhHouseCurPosting.createdBy
            AppUser createdBy = (AppUser) appUserCacheUtility.read(createdById)           // Get AppUser object by id for createdBy
            GridEntity object = new GridEntity()            // build grid object
            object.id = exhHouseCurPosting.id
            object.cell = [
                    Tools.LABEL_NEW,
                    exhHouseCurPosting.id,
                    rmsExchangeHouse.name,
                    exhHouseCurPosting.amount,
                    createdBy.username,
                    DateUtility.getLongDateForUI(exhHouseCurPosting.createdOn),
                    Tools.EMPTY_SPACE,
                    DateUtility.getLongDateForUI(exhHouseCurPosting.updatedOn)
            ]

            result.put(Tools.MESSAGE, EXH_HOUSE_CUR_POSTING_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXH_HOUSE_CUR_POSTING_CREATE_FAILURE_MSG)
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
                LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, EXH_HOUSE_CUR_POSTING_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXH_HOUSE_CUR_POSTING_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build new ExchangeHouseCurrencyPosting object
     * @param parameterMap -serialized parameters from UI
     * @return -new ExchangeHouseCurrencyPosting object
     */
    private RmsExchangeHouseCurrencyPosting buildExhHouseCurPostingObject(GrailsParameterMap parameterMap) {
        RmsExchangeHouseCurrencyPosting exhHouseCurPosting = new RmsExchangeHouseCurrencyPosting(parameterMap)
        exhHouseCurPosting.companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        exhHouseCurPosting.createdOn = new Date()
        exhHouseCurPosting.createdBy = rmsSessionUtil.appSessionUtil.getAppUser().id
        exhHouseCurPosting.updatedOn = null
        exhHouseCurPosting.updatedBy = 0L
        return exhHouseCurPosting
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
