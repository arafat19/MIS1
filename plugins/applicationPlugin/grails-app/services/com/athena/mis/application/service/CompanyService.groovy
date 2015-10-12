package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Service class for basic company CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'CompanyService'
 */
class CompanyService extends BaseService {

    static transactional = false

    @Autowired
    CompanyCacheUtility companyCacheUtility

    /**
     * @return -list of company
     */
    public List list() {
        return Company.list(sort: companyCacheUtility.SORT_ON_NAME, order: companyCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    private static final String INSERT_QUERY = """
            INSERT INTO company(id, version, name, address1, address2, created_by, created_on, updated_by, code,
                 country_id, currency_id, web_url, email)
            VALUES (NEXTVAL('company_id_seq'), 0, :name, :address1, :address2, :createdBy, :createdOn,
                :updatedBy, :code, :countryId, :currencyId, :webUrl, :email)
    """

    /**
     * SQL to save company object in database
     * @param company -Company object
     * @return -newly created Company object
     */
    public Company create(Company company) {
        Map queryParams = [
                name: company.name,
                address1: company.address1,
                address2: company.address2,
                createdBy: company.createdBy,
                createdOn: DateUtility.getSqlDateWithSeconds(company.createdOn),
                updatedBy: company.updatedBy,
                code: company.code,
                countryId: company.countryId,
                currencyId: company.currencyId,
                webUrl: company.webUrl,
                email: company.email
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)
        int companyId = (int) result[0][0]
        company.id = companyId
        if (result.size() <= 0) {
            throw new RuntimeException("error occurred at companyService.create")

        }
        return company
    }

    /**
     * get specific company object from cache by id
     * @param id -Company.id
     * @return -Company object
     */

    public Company read(long id) {
        Company company = Company.read(id)
        return company
    }

    private static final String UPDATE_QUERY = """
                    UPDATE company SET
                          version=:newVersion,
                          name=:name,
                          address1=:address1,
                          address2=:address2,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy,
                          code=:code,
                          country_id=:countryId,
                          currency_id=:currencyId,
                          web_url=:webUrl,
                          email=:email
                    WHERE
                          id=:id AND
                          version=:version
    """
    /**
     * SQL to update company object in database
     * @param company -Company object
     * @return -int value(updateCount)
     */
    public int update(Company company) {
        Map queryParams = [
                newVersion: company.version + 1,
                name: company.name,
                address1: company.address1,
                address2: company.address2,
                updatedBy: company.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(company.updatedOn),
                id: company.id,
                version: company.version,
                code: company.code,
                countryId: company.countryId,
                currencyId: company.currencyId,
                webUrl: company.webUrl,
                email: company.email
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("error occurred at companyService.update")
        }
        company.version = company.version + 1
        return updateCount;
    }

    /**
     * SQL to delete company object from database
     * @param id -Company.id
     * @return -boolean value
     */
    public Boolean delete(long id) {
        String queryStr = """
                    DELETE FROM company
                      WHERE
                          id=:id
        """
        Map queryParams = [
                id: id
        ]
        int deleteCount = executeUpdateSql(queryStr, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("error occurred at companyService.delete")
        }
        return Boolean.TRUE;
    }

    /**
     * insert default data into database when application starts with bootstrap
     */
    public void createDefaultData(long companyId) {
        Company company
        if (PluginConnector.isPluginInstalled(PluginConnector.EXCHANGE_HOUSE)) {
            company = new Company(name: "Southeast Financial Services Australia PTY Ltd.", code: "SFSA", address1: 'Address line 1', countryId: 1, createdBy: 1, createdOn: new Date(), currencyId: 1, webUrl: 'http://localhost:8080')
            company.id = companyId
            company.save()
        } else if (PluginConnector.isPluginInstalled(PluginConnector.ARMS)) {
            company = new Company(name: "Southeast Bank Ltd.", code: "SEBL", address1: 'Address line 1', countryId: 1, createdBy: 1, createdOn: new Date(), currencyId: 1, webUrl: 'http://localhost:8080')
            company.id = companyId
            company.save()
        } else {
            company = new Company(name: "GANON & DUNKERLEY LTD.", code: "GDL", address1: 'Address line 1', countryId: 1, createdBy: 1, email: 'noreply@athena.com.bd', createdOn: new Date(), currencyId: 1, webUrl: 'http://localhost:8080')
            company.id = companyId
            company.save()
        }
    }
}
