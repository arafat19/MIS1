package com.athena.mis.application.actions.currency

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.service.CurrencyService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update new currency object and show in grid
 *  For details go through Use-Case doc named 'UpdateCurrencyActionService'
 */
class UpdateCurrencyActionService extends BaseService implements ActionIntf {

    private static final String CURRENCY_UPDATE_FAILURE_MESSAGE = "Currency could not be updated"
    private static final String CURRENCY_UPDATE_SUCCESS_MESSAGE = "Currency has been updated successfully"
    private static final String CURRENCY_OBJ = "currencyObj"
    private static final String SORT_ON_NAME = "name"
    private static final String CURRENCY_NOT_EXIST = "Currency does not exist"
    private static final String OBJ_CHANGED_MSG = "Selected currency has been changed by other user"
    private static final String NAME_EXIST_MESSAGE = "Same currency name already exists"
    private static final String SYMBOL_EXIST_MESSAGE = "Same currency symbol already exists"

    private final Logger log = Logger.getLogger(getClass());

    CurrencyService currencyService
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * 1. Check user access
     * 2. receive object from controller
     * 3. pull previous state of currency object
     * 4. check input validation
     * @param params -N/A
     * @param obj -N/A
     * @return -a map containing currency object necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)

            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long currencyId = Long.parseLong(parameterMap.id.toString())
            int version = Integer.parseInt(parameterMap.version.toString())

            Currency oldCurrency = currencyService.read(currencyId)
            if (!oldCurrency) { //check existence of object
                result.put(Tools.MESSAGE, CURRENCY_NOT_EXIST)
                return result
            }
            //build currency object
            Currency newCurrency = (Currency) buildCurrencyForUpdate(oldCurrency, parameterMap)
            if ((!oldCurrency) || (oldCurrency.version != version)) {
                result.put(Tools.MESSAGE, OBJ_CHANGED_MSG)
                return result
            }
            int countName = currencyCacheUtility.countByNameAndIdNotEqual(newCurrency.name, oldCurrency.id)
            if (countName > 0) {
                result.put(Tools.MESSAGE, NAME_EXIST_MESSAGE)
                return result
            }
            int countCode = currencyCacheUtility.countBySymbolAndIdNotEqual(newCurrency.symbol,oldCurrency.id)
            if (countCode > 0) {
                result.put(Tools.MESSAGE, SYMBOL_EXIST_MESSAGE)
                return result
            }
            result.put(CURRENCY_OBJ, newCurrency)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CURRENCY_UPDATE_FAILURE_MESSAGE)
            return result
        }

    }
    /**
     * Update currency object in DB and update cache utility accordingly
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -int value for update count
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Currency currencyInstance = (Currency) preResult.get(CURRENCY_OBJ)
            currencyService.update(currencyInstance)
            currencyCacheUtility.update(currencyInstance, SORT_ON_NAME, ASCENDING_SORT_ORDER)
            result.put(CURRENCY_OBJ, currencyInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CURRENCY_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null;
    }
    /**
     * Get currency object
     * @param obj - object receive from execute method
     * @return - currency object as grid entity
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            Currency currency = (Currency) executeResult.get(CURRENCY_OBJ)
            GridEntity object = new GridEntity()
            object.id = currency.id
            object.cell = [
                    Tools.LABEL_NEW,
                    currency.id,
                    currency.name,
                    currency.symbol
            ]
            result.put(Tools.MESSAGE, CURRENCY_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, currency.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CURRENCY_UPDATE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, CURRENCY_UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CURRENCY_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build Currency object
     * @param params -serialized parameters from UI
     * @return -new Currency object
     */
    private Currency buildCurrencyForUpdate(Currency oldCurrency, GrailsParameterMap parameterMap) {
        Currency newCurrency = new Currency(parameterMap)
        oldCurrency.name = newCurrency.name
        oldCurrency.symbol = newCurrency.symbol
        oldCurrency.updatedOn = new Date()
        oldCurrency.updatedBy = appSessionUtil.getAppUser().id
        return oldCurrency
    }

}

