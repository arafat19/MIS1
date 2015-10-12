package com.athena.mis.application.actions.currency

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.service.CurrencyService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new currency object and show in grid
 *  For details go through Use-Case doc named 'CreateCurrencyActionService'
 */
class CreateCurrencyActionService extends BaseService implements ActionIntf {

    CurrencyService currencyService
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String CURRENCY_CREATE_SUCCESS_MSG = "Currency has been successfully saved"
    private static final String CURRENCY_CREATE_FAILURE_MSG = "Currency has not been saved"
    private static final String CURRENCY_OBJECT = "currency"
    private static final String NAME_EXIST_MESSAGE = "Same currency name already exists"
    private static final String SYMBOL_EXIST_MESSAGE = "Same currency symbol already exists"
    private static final String SORT_ON_NAME = "name"

    private final Logger log = Logger.getLogger(getClass());
    /**
     * 1. Build currency object
     * 2. Existing check of name, code
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing currency object necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            Currency currency = buildCurrencyObject(parameterMap)

            int countName = currencyCacheUtility.countByName(currency.name)
            if (countName > 0) {
                result.put(Tools.MESSAGE, NAME_EXIST_MESSAGE)
                return result
            }
            int countCode = currencyCacheUtility.countBySymbol(currency.symbol)
            if (countCode > 0) {
                result.put(Tools.MESSAGE, SYMBOL_EXIST_MESSAGE)
                return result
            }

            result.put(CURRENCY_OBJECT, currency)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CURRENCY_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Save currency object in DB and update cache utility accordingly
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
            Currency currency = (Currency) preResult.get(CURRENCY_OBJECT)
            Currency currencyServiceReturn = currencyService.create(currency)
            currencyCacheUtility.add(currencyServiceReturn, SORT_ON_NAME, ASCENDING_SORT_ORDER)
            result.put(CURRENCY_OBJECT, currencyServiceReturn)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(CURRENCY_CREATE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CURRENCY_CREATE_FAILURE_MSG)
            return result
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. Show newly created currency object in grid
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
            Currency currency = (Currency) receiveResult.get(CURRENCY_OBJECT)
            GridEntity object = new GridEntity()
            object.id = currency.id
            object.cell = [
                    Tools.LABEL_NEW,
                    currency.id,
                    currency.name,
                    currency.symbol
            ]
            result.put(Tools.MESSAGE, CURRENCY_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CURRENCY_CREATE_FAILURE_MSG)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap receivedResult = (LinkedHashMap) obj
                String receivedMessage = receivedResult.get(Tools.MESSAGE)
                if (receivedMessage) {
                    result.put(Tools.MESSAGE, receivedMessage)
                    return result
                }
            }
            result.put(Tools.MESSAGE, CURRENCY_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CURRENCY_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build Currency object
     * @param params -serialized parameters from UI
     * @return -new Currency object
     */
    private Currency buildCurrencyObject(GrailsParameterMap params) {
        Currency currency = new Currency(params)
        currency.companyId = appSessionUtil.getCompanyId()
        currency.version = 0

        currency.createdOn = new Date()
        currency.createdBy = appSessionUtil.getAppUser().id
        currency.updatedOn = null
        currency.updatedBy = 0L
        return currency
    }
}


