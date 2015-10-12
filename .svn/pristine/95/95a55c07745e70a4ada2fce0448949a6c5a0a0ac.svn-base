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

class ExhCreateCurrencyConversionActionService extends BaseService implements ActionIntf {

    private static final String HAS_ACCESS = 'hasAccess'
    private static final String IS_VALID = "isValid"
    private static final String CURRENCY_CONVERSION = 'currencyConversion'
    ExhCurrencyConversionService exhCurrencyConversionService
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ExhCurrencyConversionCacheUtility exhCurrencyConversionCacheUtility

    private static String CURRENCY_CONVERSION_CREATE_SUCCESS_MSG = "Currency Conversion has been successfully saved"
    private static String CURRENCY_CONVERSION_CREATE_FAILURE_MSG = "Currency Conversion has not been saved"
    private static String DUPLICATE_CURRENCY_CONVERSION = "Currency Conversion already exists"
    private static String SORT_COLUMN = "fromCurrency"

    private final Logger log = Logger.getLogger(getClass())

    //implement the precondition method of action class
    /*
    Get all pre condition to save currencyConversion info
     */

    public Object executePreCondition(Object parameters, Object obj) {
        try {
            //set a map object to send all information to caller
            Map output = new HashMap()

            //now check session log
            //only admin can create new currencyConversion
            boolean hasAccess = exhSessionUtil.appSessionUtil.getAppUser().isPowerUser
            output.put(HAS_ACCESS, hasAccess)
            if (!hasAccess) {
                return output
            }

            Long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            //create a currencyConversion instance
            ExhCurrencyConversion currencyConversion = (ExhCurrencyConversion) obj;
            currencyConversion.userId = exhSessionUtil.appSessionUtil.getAppUser().id
            currencyConversion.createdOn = new Date()
            currencyConversion.companyId = companyId
            currencyConversion.updatedOn = null
            currencyConversion.updatedBy = 0L

            //check currencyConversion currencyConversion input validation
            currencyConversion.validate()
            if (currencyConversion.hasErrors()) {
                output.put(IS_VALID, new Boolean(false))
            } else {
                output.put(IS_VALID, new Boolean(true))
            }

//            CurrencyConversion existingObject = exhCurrencyConversionService.readByCurrencies(currencyConversion.fromCurrency,

            ExhCurrencyConversion existingObject = exhCurrencyConversionCacheUtility.readByCurrencies(currencyConversion.fromCurrency,
                    currencyConversion.toCurrency)

            if (existingObject) {
                output.put(Tools.IS_ERROR, new Boolean(true))
                output.put(Tools.MESSAGE, DUPLICATE_CURRENCY_CONVERSION)
            } else {
                output.put(Tools.IS_ERROR, new Boolean(false))
            }

            output.put(CURRENCY_CONVERSION, currencyConversion)
            return output

        } catch (Exception e) {
            log.error(e.getMessage())
            return null
        }
    }

    //implement the execute method of action class
    /*
    if precondition is ok. then save currencyConversion info using
    execute method
     */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            ExhCurrencyConversion objCurrencyConversion = (ExhCurrencyConversion) obj
            ExhCurrencyConversion currencyConversionServiceReturn = exhCurrencyConversionService.create(objCurrencyConversion)
            exhCurrencyConversionCacheUtility.add(currencyConversionServiceReturn, SORT_COLUMN, exhCurrencyConversionCacheUtility.SORT_ORDER_ASCENDING)
            return currencyConversionServiceReturn
        } catch (Exception e) {
            log.error(e.message)
            //@todo:rollback
            throw new RuntimeException(CURRENCY_CONVERSION_CREATE_FAILURE_MSG)
            return null
        }
    }

    //implement the executePostCondition method of action class
    /*
    if execute is ok. then executePostCondition method will be checked
     */

    public Object executePostCondition(Object parameters, Object obj) {
        //there are not post condition
        return null
    }

    //implement the buildSuccessResultForUI method of action class
    /*
    if currencyConversion build successfully then initiate success message
     */

    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result

        try {
            ExhCurrencyConversion objCurrencyConversion = (ExhCurrencyConversion) obj
            result = [isError: false, message: CURRENCY_CONVERSION_CREATE_SUCCESS_MSG];
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result = [isError: true, entity: obj, version: 0, message: CURRENCY_CONVERSION_CREATE_FAILURE_MSG]
            return result
        }
    }

    //implement the buildFailureResultForUI method of action class
    /*
    if currencyConversion build failed then initiate failure message
     */

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map preResult = (Map) obj
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, CURRENCY_CONVERSION_CREATE_FAILURE_MSG)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, true)
            result.put(Tools.MESSAGE, CURRENCY_CONVERSION_CREATE_FAILURE_MSG)
            return result
        }
    }
}

