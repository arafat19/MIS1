package com.athena.mis.exchangehouse.service

import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhCurrencyConversion
import com.athena.mis.exchangehouse.utility.ExhCurrencyConversionCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ExhCurrencyConversionService extends BaseService {

    static transactional = false;

    @Autowired
    ExhCurrencyConversionCacheUtility exhCurrencyConversionCacheUtility

    @Transactional(readOnly = true)
    public List<ExhCurrencyConversion> list() {
        List<ExhCurrencyConversion> lstEntity = ExhCurrencyConversion.list(
                readOnly: true,
                sort: exhCurrencyConversionCacheUtility.DEFAULT_SORT_PROPERTY,
                order: exhCurrencyConversionCacheUtility.SORT_ORDER_ASCENDING
        )
        return lstEntity
    }

    public ExhCurrencyConversion create(ExhCurrencyConversion currencyConversion) {
        ExhCurrencyConversion newCurrencyConversion = currencyConversion.save()
        return newCurrencyConversion
    }

    /**
     * Updates a supplied currencyConversion
     *
     * @param currencyConversion CurrencyConversion to be updated
     * @return updated currencyConversion if saved successfully, otherwise throw RuntimeException
     */
    public Integer update(ExhCurrencyConversion currencyConversion) {
        String query = """UPDATE exh_currency_conversion SET
                              version=${currencyConversion.version + 1},
                              from_currency=${currencyConversion.fromCurrency},
                              to_currency=${currencyConversion.toCurrency},
                              buy_rate=${currencyConversion.buyRate},
                              sell_rate=${currencyConversion.sellRate},
                              user_id=${currencyConversion.userId},
                              updated_on='${DateUtility.getDBDateFormatWithSecond(currencyConversion.updatedOn)}',
                              updated_by=${currencyConversion.updatedBy}
                          WHERE
                              id=${currencyConversion.id} AND
                              version=${currencyConversion.version}"""

        int updateCount = executeUpdateSql(query);
        currencyConversion.version = currencyConversion.version + 1

        return (new Integer(updateCount));
    }

    /*
   create new currencyConversion
    */

    public Boolean delete(ExhCurrencyConversion currencyConversion) {
        //if currencyConversion is found in database using id then delete currencyConversion
        currencyConversion.delete()

        return new Boolean(true)
    }

    //get currencyConversion
    public ExhCurrencyConversion read(Long id) {
        ExhCurrencyConversion currencyConversion = ExhCurrencyConversion.read(id)
        return currencyConversion
    }
    //get currencyConversion
    public ExhCurrencyConversion get(Long id) {
        ExhCurrencyConversion currencyConversion = ExhCurrencyConversion.get(id)
        return currencyConversion
    }

	public void createDefaultData(long companyId) {
		com.athena.mis.application.entity.Currency aud = com.athena.mis.application.entity.Currency.findBySymbolAndCompanyId("AUD", companyId)
		com.athena.mis.application.entity.Currency bdt = com.athena.mis.application.entity.Currency.findBySymbolAndCompanyId("BDT", companyId)
		ExhCurrencyConversion currencyConversion = new ExhCurrencyConversion()
		currencyConversion.fromCurrency = aud.id
		currencyConversion.toCurrency = bdt.id
		currencyConversion.buyRate = 78.14d
		currencyConversion.sellRate = 80.14d
		currencyConversion.userId = 1
		currencyConversion.createdOn = new Date()
		currencyConversion.companyId = companyId
		currencyConversion.save()
		ExhCurrencyConversion currencyConversion2 = new ExhCurrencyConversion()
		currencyConversion2.fromCurrency = bdt.id
		currencyConversion2.toCurrency = aud.id
		currencyConversion2.buyRate = 0.0127975d
        currencyConversion2.sellRate = 0.5d
		currencyConversion2.userId = 1
		currencyConversion2.createdOn = new Date()
		currencyConversion2.companyId = companyId
		currencyConversion2.save()
	}

}
