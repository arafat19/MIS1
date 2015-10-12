package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Country
import com.athena.mis.application.utility.CountryCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 * CountryService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class CountryService extends BaseService {

    static transactional = false

    @Autowired
    CountryCacheUtility countryCacheUtility

    /**
     * Pull country object
     * @return - list of country
     */
    public List list() {
        return Country.list(sort: countryCacheUtility.SORT_ON_NAME, order: countryCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }
    /**
     * Pull country object
     * @param id - country id
     * @return - country object
     */
    public Country read(long id) {
        return Country.read(id)
    }
    /**
     * Create country
     * @param country - country object
     * @return - newly created country object
     */
    public Country create(Country country) {
        Country newCountry = country.save(false)
        return newCountry
    }

    private static final String QUERY_STR = """
                        DELETE FROM country
                          WHERE id=:id
                      """
    /**
     * Delete country
     * @param id - selected country id
     * @return -an int value(e.g- 1 for success and 0 for failure)
     */
    public int delete(long id) {
        Map queryParams = [
                id: id
        ]
        int deleteCount = executeUpdateSql(QUERY_STR, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("Error occurred while delete country information")
        }
        return deleteCount
    }

    private static final String UPDATE_QUERY = """
          UPDATE country SET
              version = version+1,
              name = :countryName,
              code = :countryCode,
              isd_code = :isdCode,
              nationality = :nationality,
              currency_id = :currencyId,
              phone_number_pattern = :phoneNumberPattern,
              updated_on=:updatedOn,
              updated_by=:updatedBy
         WHERE
          id=:id AND
          version=:version
    """
    /**
     * Update country
     * @param country - country object
     * @return- newly updated object of selected country
     */
    public int update(Country country) {

        Map queryParams = [
                id: country.id,
                version: country.version,
                countryName: country.name,
                countryCode: country.code,
                isdCode: country.isdCode,
                nationality: country.nationality,
                currencyId: country.currencyId,
                phoneNumberPattern: country.phoneNumberPattern,
                updatedBy: country.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(country.updatedOn)
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update country information')
        }
        country.version = country.version + 1
        return updateCount
    }

    /**
     * applicable only for create default country
     */
    public void createDefaultData(long companyId) {
        new Country(name: "Bangladesh", code: "BD", phoneNumberPattern: "^[0-9]{11}\$", isdCode: "+88", currencyId: 1, nationality: "Bangladeshi", companyId: companyId, createdBy: 1, createdOn: new Date(), updatedBy: 0, updatedOn: null).save()
    }
}
