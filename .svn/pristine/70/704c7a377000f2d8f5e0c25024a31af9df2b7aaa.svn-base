package com.athena.mis.application.actions.currency

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show list of object(s) of currency
 *  For details go through Use-Case doc named 'ShowCurrencyActionService'
 */
class ShowCurrencyActionService extends BaseService implements ActionIntf {

    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    protected final Logger log = Logger.getLogger(getClass());
    /**
     * Check access of logged user
     * @param parameters -serialize parameters from UI
     * @param obj -N/A
     * @return - a map containing hasAccess(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        try {
            Map outputMap = new HashMap()
            if (appSessionUtil.getAppUser().isPowerUser) {
                outputMap.put(Tools.HAS_ACCESS, Boolean.TRUE)
            } else {
                outputMap.put(Tools.HAS_ACCESS, Boolean.FALSE)
            }
            return outputMap;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }
    /**
     * 1. initialize pagination for Flexi-grid
     * 2. pull currency object from cache utility & sort them
     * @param params - serialize parameters from UI
     * @param obj - N/A
     * @return - currency list
     */
    Object execute(Object params, Object obj) {
        LinkedHashMap result = null
        try {

            if (!params.rp) {
                params.rp = 15;
            }
            initPager(params)
            sortColumn = 'name'
            sortOrder = ASCENDING_SORT_ORDER

            List<Currency> currencyList = []
            List<Currency> tempCurrencies = currencyCacheUtility.list()
            tempCurrencies.each {
                currencyList << it
            }

            int count = currencyList.size()
            int end = start + resultPerPage > count ? count : start + resultPerPage
            currencyList = currencyList.subList(start, end)
            result = [currencyList: currencyList, count: count]

            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result = [currencyList: null, count: 0]
            return result
        }
    }
    /**
     * Wrap list of currency in grid entity
     * @param currencyList -list of currency object(s)
     * @param start -starting index of the page
     * @return -list of wrapped currency
     */
    private def wrapCurrencyListInGridEntityList(List<Currency> currencyList, int start) {

        def currencies = []
        def counter = start + 1
        for (int i = 0; i < currencyList.size(); i++) {
            Currency currency = currencyList[i]
            GridEntity obj = new GridEntity()
            obj.id = currency.id
            obj.cell = ["${counter}", "${currency.id}", "${currency.name}", "${currency.symbol}"]
            currencies << obj
            counter++
        };
        return currencies;
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null;
    }
    /**
     * Wrap currency list for grid
     * @param currencyResult -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object currencyResult) {
        Map output = null
        try {
            List<Currency> currencyList = (List<Currency>) currencyResult.currencyList
            int count = (int) currencyResult.count

            List currencies = (List) this.wrapCurrencyListInGridEntityList(currencyList, start)
            output = [page: pageNumber, total: count, rows: currencies]

            return [currencyListJSON: output]
        } catch (Exception ex) {
            log.error(ex.getMessage());
            output = [page: pageNumber, total: 0, rows: null]
            return output
        }
    }
    /**
     * do nothing for failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}


