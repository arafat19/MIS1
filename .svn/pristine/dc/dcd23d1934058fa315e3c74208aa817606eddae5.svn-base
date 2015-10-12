package com.athena.mis.exchangehouse.utility

import com.athena.mis.BaseService
import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.exchangehouse.entity.ExhCurrencyConversion
import com.athena.mis.exchangehouse.service.ExhCurrencyConversionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('exhCurrencyConversionCacheUtility')
class ExhCurrencyConversionCacheUtility extends ExtendedCacheUtility {

    @Autowired
    ExhCurrencyConversionService exhCurrencyConversionService
    @Autowired
    CurrencyCacheUtility currencyCacheUtility

    public static final String DEFAULT_SORT_PROPERTY = 'id'
    private static final String FIELD_FROM_CURRENCY = "fromCurrency";
    private static final String FIELD_TO_CURRENCY = "toCurrency";

    // Following method will populate the list of ALL currency conversions from DB
    public void init() {
        List lstItems = exhCurrencyConversionService.list()
        super.setList(lstItems)
    }

    /**
     * Get list of currency conversion
     */
    public ExhCurrencyConversion readByCurrencies(Integer fromCurrency, Integer toCurrency) {
        ExhCurrencyConversion currencyConversionInstance = null
        List<ExhCurrencyConversion> lstMain = (List<ExhCurrencyConversion>) list()
        int lstSize = lstMain.size()
        for (int i = 0; i < lstSize; i++) {
            if (lstMain[i].fromCurrency == fromCurrency && lstMain[i].toCurrency == toCurrency) {
                currencyConversionInstance = lstMain[i]
            }
        }
        return currencyConversionInstance
    }

    // following method return a list and count based on search query: used in BankBranch grid searching
    public Map searchByField(String fieldName, String query, List<ExhCurrencyConversion> currencyConversionList, BaseService baseService) {
        currencyConversionList = currencyConversionList.findAll {
            if (fieldName.equals(FIELD_FROM_CURRENCY)) {
                String fromCurrencyName = currencyCacheUtility.read(it.fromCurrency).name
                fromCurrencyName ==~ /(?i).*${query}.*/
            } else if (fieldName.equals(FIELD_TO_CURRENCY)) {
                String toCurrencyName = currencyCacheUtility.read(it.toCurrency).name
                toCurrencyName ==~ /(?i).*${query}.*/
            } else {
                it.properties.get(fieldName) ==~ /(?i).*${query}.*/
            }
        }
        int end = currencyConversionList.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : currencyConversionList.size()
        List lstResult = currencyConversionList.subList(baseService.start, end)
        return [list: lstResult, count: currencyConversionList.size()]
    }
}

