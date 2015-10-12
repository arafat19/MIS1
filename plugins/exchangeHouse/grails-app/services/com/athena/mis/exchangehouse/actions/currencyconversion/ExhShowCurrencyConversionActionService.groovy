package com.athena.mis.exchangehouse.actions.currencyconversion

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.exchangehouse.entity.ExhCurrencyConversion
import com.athena.mis.exchangehouse.utility.ExhCurrencyConversionCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ExhShowCurrencyConversionActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())
    private static final String HAS_ACCESS = "hasAccess"
    private static final String LIST_CURRENCY_CONVERSION = "currencyConversionList"

    @Autowired
    ExhCurrencyConversionCacheUtility exhCurrencyConversionCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    /**
     * List currencyConversion has no pre-condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        try {
            Map outputMap = new HashMap()
            boolean hasAccess = exhSessionUtil.appSessionUtil.getAppUser().isPowerUser
            outputMap.put(HAS_ACCESS, hasAccess)
            return outputMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    /**
     * Retrieving a list of currencyConversions, it may sort and pagingate the resulting
     * currencyConversion list if requested from the browser.
     *
     * Pagination request has been revealed by invoking super's initPager method
     *
     * @param params request parameters
     * @param obj additional parameters
     * @return list of currencyConversions with count and page number for pagination
     */
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params= (GrailsParameterMap) parameters
            if (!params.rp) {
                params.rp = 15
            }
            initPager(params)
            List<ExhCurrencyConversion> currencyConversionList = exhCurrencyConversionCacheUtility.list(this)
            int count= exhCurrencyConversionCacheUtility.count()
            result.put(LIST_CURRENCY_CONVERSION,currencyConversionList)
            result.put(Tools.COUNT,count)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [currencyList: null, count: 0]
            return result
        }
    }

    /**
     * Wrapping each CurrencyConversion entity in GridEntity object (required representation of object
     * for Flexigrid), create a list of GridEntity, and then return
     *
     * @param currencyConversionList List of all CurrencyConversions
     * @param start start offset, required to set counter
     * @return List of GridEntity
     */
    private List wrapCurrencyConversionListInGridEntityList(List<ExhCurrencyConversion> currencyConversionList, int start) {
        List currencyConversions = [] as List
        try {
            int counter = start + 1
            currencyConversionList.each { ExhCurrencyConversion currencyConversion ->
                GridEntity obj = new GridEntity()
                obj.id = currencyConversion.id
                String createdOn = DateUtility.getDateTimeFormatAsString(currencyConversion.createdOn)
                String fromCurrencyName = currencyCacheUtility.read(currencyConversion.fromCurrency).name
                String toCurrencyName = currencyCacheUtility.read(currencyConversion.toCurrency).name

                obj.cell = [counter, currencyConversion.id, fromCurrencyName,
                        toCurrencyName, currencyConversion.buyRate,currencyConversion.sellRate,
                        createdOn]
                currencyConversions << obj
                counter++
            }
            return currencyConversions
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return currencyConversions
        }
    }

    /**
     * List currencyConversion has no post-condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    /**
     * Wrapping currencyConversion list into FlexiGrid equivalent row representation
     * with page number and total
     *
     * @param obj currencyConversion list to wrap in GridEntity collection
     * @return Collection of GridEntity, total and page number in a map
     */
    public Object buildSuccessResultForUI(Object currencyConversionResult) {
        Map result = null
        try {
            Map executeResult=(Map) currencyConversionResult
            List<ExhCurrencyConversion> currencyConversionList = (List<ExhCurrencyConversion>) executeResult.get(LIST_CURRENCY_CONVERSION)
            int count =(int)executeResult.get(Tools.COUNT)
            List wrappedCurrencyConversion = wrapCurrencyConversionListInGridEntityList(currencyConversionList, start)

            List<Currency> lstAllCurrency = []
            List<Currency> tempAllCurrency = currencyCacheUtility.list()

            for (int i = 0; i < tempAllCurrency.size(); i++) {
                Currency currency = new Currency()
                currency.properties = tempAllCurrency[i].properties
                currency.id = tempAllCurrency[i].id
                lstAllCurrency << currency
            }


            Map output = [page: pageNumber, total: count, rows: wrappedCurrencyConversion]
            result = [currencyList: lstAllCurrency,
                    currencyConversionListJSON: output]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [currencyList: null, currencyConversionListJSON: null]
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
        // do nothing
        return null
    }
}


