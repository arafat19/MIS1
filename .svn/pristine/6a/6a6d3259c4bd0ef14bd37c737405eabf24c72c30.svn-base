package com.athena.mis.exchangehouse.actions.currencyconversion

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhCurrencyConversion
import com.athena.mis.exchangehouse.service.ExhCurrencyConversionService
import com.athena.mis.exchangehouse.utility.ExhCurrencyConversionCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ExhUpdateCurrencyConversionActionService extends BaseService implements ActionIntf {

    private static final String CURRENCY_CONVERSION_UPDATE_FAILURE_MESSAGE = "Currency conversion could not be updated"
    private static final String CURRENCY_CONVERSION_UPDATE_SUCCESS_MESSAGE = "Currency conversion has been updated successfully"
    private static final String DUPLICATE_CURRENCY_CONVERSION = "Currency Conversion already exists"
    private static final String HAS_ACCESS = "hasAccess"
    private static final String IS_VALID = "isValid"
    private static final String CURRENCY_CONVERSION = 'currencyConversion'
    private static final String SORT_COLUMN = "fromCurrency"

    private final Logger log = Logger.getLogger(getClass())

    ExhCurrencyConversionService exhCurrencyConversionService
    @Autowired
    ExhCurrencyConversionCacheUtility exhCurrencyConversionCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    public Object executePreCondition(Object parameters, Object obj) {
        Map output = new HashMap();
        try {
            if (exhSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                output.put(HAS_ACCESS, new Boolean(true))
            } else {
                output.put(HAS_ACCESS, new Boolean(false))
                // return at this point, no further execution
                return output
            }

            Long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            ExhCurrencyConversion currencyConversion = (ExhCurrencyConversion) obj;
            currencyConversion.userId = exhSessionUtil.appSessionUtil.getAppUser().id
            currencyConversion.companyId = companyId
            currencyConversion.updatedBy = exhSessionUtil.appSessionUtil.getAppUser().id
            currencyConversion.updatedOn = new Date()
            currencyConversion.createdOn = new Date()

            // checks input validation
            currencyConversion.validate()

            if (currencyConversion.hasErrors()) {
                output.put(IS_VALID, new Boolean(false))
                // return at this point, no further execution
                return output;
            } else {
                output.put(IS_VALID, new Boolean(true))
            }

            ExhCurrencyConversion existingObject = exhCurrencyConversionCacheUtility.readByCurrencies(currencyConversion.fromCurrency,
                    currencyConversion.toCurrency)

            if ((existingObject) && (existingObject.id != currencyConversion.id)) {
                output.put(Tools.IS_ERROR, new Boolean(true))
                output.put(Tools.MESSAGE, DUPLICATE_CURRENCY_CONVERSION)
            } else {
                output.put(Tools.IS_ERROR, new Boolean(false))
            }

            output.put(CURRENCY_CONVERSION, currencyConversion)
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            output.put(Tools.MESSAGE,CURRENCY_CONVERSION_UPDATE_FAILURE_MESSAGE)
            return output
        }

    }

    /**
     * Executes a currencyConversion update operation
     *
     * @param parameters request parameters
     * @param obj CurrencyConversion entity
     * @return CurrencyConversion object if updated successfully, null otherwise
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {

        try {
            ExhCurrencyConversion currencyConversionInstance = (ExhCurrencyConversion) obj
            Integer updateCount = exhCurrencyConversionService.update(currencyConversionInstance)
            if (updateCount > 0) {
                exhCurrencyConversionCacheUtility.update(currencyConversionInstance, SORT_COLUMN, exhCurrencyConversionCacheUtility.SORT_ORDER_ASCENDING)
            }
            return updateCount
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(CURRENCY_CONVERSION_UPDATE_FAILURE_MESSAGE)
            return new Integer(0)
        }
    }

    /**
     * No post condition for update currencyConversion
     *
     * @param paramters
     * @param obj
     * @return
     */
    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    /**
     * Builds success result map to returned to the UI layer
     *
     * @param obj CurrencyConversion instance
     * @return result map wrapped within a grid entity
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result
        try {
//            ExhCurrencyConversion currencyConversionServiceReturn = (ExhCurrencyConversion) obj;
            result = [isError: false, message: CURRENCY_CONVERSION_UPDATE_SUCCESS_MESSAGE];
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [isError: true, message: CURRENCY_CONVERSION_UPDATE_FAILURE_MESSAGE]
            return result
        }
    }

    /**
     * Builds UI specific object on failure;
     *
     * @param obj Object to be used to determine building of UI result
     * @return Object to be used for rendering at UI level
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result
        try {
            String message = null
            if (obj != null) {
                message = obj.message
            } else {
                message = CURRENCY_CONVERSION_UPDATE_FAILURE_MESSAGE
            }
            result = [isError: true, entity: null, message: message];
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [isError: true, entity: null, message: CURRENCY_CONVERSION_UPDATE_FAILURE_MESSAGE];
            return result
        }
    }

}

