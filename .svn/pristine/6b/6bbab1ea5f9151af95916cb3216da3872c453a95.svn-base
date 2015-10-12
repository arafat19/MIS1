package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 * CurrencyService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class CurrencyService extends BaseService {

    static transactional = false
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * Pull currency object
     * @return - list of currency
     */
    public List<Currency> list() {
        List<Currency> currencyList = Currency.list(sort: currencyCacheUtility.DEFAULT_SORT_PROPERTY, order: currencyCacheUtility.SORT_ORDER_DESCENDING)
        return currencyList
    }

    private static final String INSERT_QUERY =
        """
            INSERT INTO currency(id, version,is_to_currency,name,symbol,company_id, created_on, created_by, updated_on, updated_by)
            VALUES (NEXTVAL('currency_id_seq'),:version,:isToCurrency,:name,:symbol,:companyId, :createdOn, :createdBy, :updatedOn, :updatedBy);
        """
    //read currency id from Currency
    public Currency read(long id) {
        return Currency.read(id)
    }

    //create a Currency
    public Currency create(Currency currency) {
        Map queryParams = [
                version: currency.version,
                isToCurrency: currency.isToCurrency,
                name: currency.name,
                symbol: currency.symbol,
                companyId: currency.companyId,
                createdOn: DateUtility.getSqlDateWithSeconds(currency.createdOn),
                createdBy: currency.createdBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(currency.updatedOn),
                updatedBy: currency.updatedBy
        ]
        List result = executeInsertSql(INSERT_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert currency')
        }
        int currencyId = (int) result[0][0]
        currency.id = currencyId
        return currency
    }

    //read to Currency
    public Currency readToCurrency() {
        Currency toCurrency = Currency.findByIsToCurrency(Boolean.TRUE, [readOnly: true])
        return toCurrency;
    }

    private static final String UPDATE_QUERY = """
          UPDATE currency SET
              version=:newVersion,
              name=:name,
              symbol=:symbol,
              updated_on=:updatedOn,
              updated_by=:updatedBy
          WHERE
              id=:id AND
              version=:version
    """
/**
 * Update supplied currency
 * @param currency Currency to be updated
 * @return updated currency if saved successfully, otherwise throw RuntimeException
 */
    public Integer update(Currency currency) {
        Map queryParams = [
                name: currency.name,
                symbol: currency.symbol,
                id: currency.id,
                version: currency.version,
                newVersion: currency.version + 1,
                updatedOn: DateUtility.getSqlDateWithSeconds(currency.updatedOn),
                updatedBy: currency.updatedBy
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        currency.version = currency.version + 1
        return (new Integer(updateCount))
    }

    /**
     * Delete Currency
     * @param currencyId - primary key of the currency being deleted
     * @return - boolean value(true for success & false for failure)
     */
    public Boolean delete(Long id) {

        Currency currencyInstance = Currency.get(id)
        if (currencyInstance == null) {
            return new Boolean(false)
        }

        currencyInstance.delete()
        return new Boolean(true)
    }

    // return currency object against symbol
    public Currency findBySymbolAndCompanyId(String symbol){
        long companyId = appSessionUtil.getCompanyId()
       return Currency.findBySymbolAndCompanyId(symbol, companyId)
    }

    /**
     * applicable only for create default currency
     */
    public void createDefaultData(long companyId) {
        Currency currency1 = new Currency(name: "Bangladesh, Taka", symbol: "BDT", isToCurrency: Boolean.TRUE, companyId: companyId, createdBy: 1, createdOn: new Date(), updatedBy: 0, updatedOn: null)
        currency1.save(flush: true)

        Currency currency2 = new Currency(name: 'Australian Dollar', symbol: 'AUD', isToCurrency: Boolean.FALSE, companyId: companyId, createdBy: 1, createdOn: new Date(), updatedBy: 0, updatedOn: null)
        currency2.save(flush: true)
    }
}