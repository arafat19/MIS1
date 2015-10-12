package com.athena.mis.exchangehouse.actions.currencyconversion

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.exchangehouse.entity.ExhCurrencyConversion
import com.athena.mis.exchangehouse.utility.ExhCurrencyConversionCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ExhListCurrencyConversionActionService extends BaseService implements ActionIntf {

    @Autowired
    ExhCurrencyConversionCacheUtility exhCurrencyConversionCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE='Currency conversion list could not found'
    private static final String LST_CURRENCY_CONVERSION='currencyConversionList'
/**
 * List currencyConversion has no pre-condition
 */
    public Object executePreCondition(Object parameters, Object obj) {

        Map outputMap = new HashMap();
        try {
            if (exhSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                outputMap.put("hasAccess", new Boolean(true))
            } else {
                outputMap.put("hasAccess", new Boolean(false))
            }
            return outputMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            outputMap.put(Tools.MESSAGE,DEFAULT_ERROR_MESSAGE)
            return outputMap
        }
    }

    /**
     * Retrieving a list of currencyConversions, it may sort and pagingate the resulting
     * currencyConversion list if requested from the browser.
     *
     * Pagination request has been revealed by invoking super's initPager method
     *
     * @param parameterMap request parameters
     * @param obj additional parameters
     * @return list of currencyConversions with count and page number for pagination
     */
    Object execute(Object parameterMap, Object obj) {
        LinkedHashMap result=new LinkedHashMap()
        try {
            GrailsParameterMap params=(GrailsParameterMap)parameterMap
            if (!params.rp) {
                params.rp = 15
            }
            if ((!params.sortname) || (params.sortname.toString().equals('id'))) {
                // if no sort name then sort by name/asc
                params.sortname = 'fromCurrency'
                params.sortorder = ASCENDING_SORT_ORDER
            }
            initSearch(params) // initSearch will call initPager()
            List<ExhCurrencyConversion> currencyConversionList = exhCurrencyConversionCacheUtility.list(this)
            int count = exhCurrencyConversionCacheUtility.count()
            result.put(LST_CURRENCY_CONVERSION,currencyConversionList)
            result.put(Tools.COUNT,count)
            //result = [currencyConversionList: currencyConversionList, count: count]
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            //result = [currencyConversionList: null, count: 0]
            result.put(Tools.MESSAGE,DEFAULT_ERROR_MESSAGE)
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
            return currencyConversions;
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
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result= new LinkedHashMap()
        try {
            Map executeResult= (Map) obj
            List<ExhCurrencyConversion> currencyConversionList = (List<ExhCurrencyConversion>) executeResult.get(LST_CURRENCY_CONVERSION)
           // int count = currencyConversionList.size()
            int count=(int)executeResult.get(Tools.COUNT)

            List currencyConversions = this.wrapCurrencyConversionListInGridEntityList(currencyConversionList, start)
            Map output = [page: pageNumber, total: count, rows: currencyConversions]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
        //    output = [page: pageNumber, total: 0, rows: null]
            result.put(Tools.MESSAGE,DEFAULT_ERROR_MESSAGE)
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
        return null
    }
}

