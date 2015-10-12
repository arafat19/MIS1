package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Service class for basic AccFinancialYear CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'AccFinancialYearService'
 */
class AccFinancialYearService extends BaseService {

    static transactional = false

    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility

    public AccFinancialYear read(long id) {
        return AccFinancialYear.read(id)
    }

    /**
     * @return -list of AccFinancialYear objects
     */
    public List list() {
        return AccFinancialYear.list(sort: accFinancialYearCacheUtility.SORT_BY_ID, order: accFinancialYearCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    /**
     * Save AccFinancialYear object in database
     * @param accFinancialYear -AccFinancialYear object
     * @return -newly created AccFinancialYear object
     */
    public AccFinancialYear create(AccFinancialYear accFinancialYear) {
        AccFinancialYear newAccFinancialYear = accFinancialYear.save(false)
        return newAccFinancialYear
    }

    private static final String QUERY_UPDATE = """
                    UPDATE acc_financial_year SET
                          version=:newVersion,
                          updated_on=:updatedOn,
                          start_date=:startDate,
                          end_date=:endDate,
                          updated_by=:updatedBy
                      WHERE
                          id=:id AND
                          version=:version
                          """
    /**
     * SQL to update AccFinancialYear object in database
     * @param accFinancialYear -AccFinancialYear object
     * @return -updated AccFinancialYear object
     */
    public AccFinancialYear update(AccFinancialYear accFinancialYear) {
        Map queryParams = [
                id: accFinancialYear.id,
                newVersion: accFinancialYear.version + 1,
                version: accFinancialYear.version,
                updatedBy: accFinancialYear.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(accFinancialYear.updatedOn),
                startDate: DateUtility.getSqlDate(accFinancialYear.startDate),
                endDate: DateUtility.getSqlDate(accFinancialYear.endDate)
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update Financial Year information')
        }
        accFinancialYear.version = accFinancialYear.version + 1
        return accFinancialYear
    }

    private static final String QUERY_DELETE = """
                    DELETE FROM acc_financial_year
                      WHERE id=:id  """
    /**
     * SQL to delete AccFinancialYear object
     * @param financialYearId -AccFinancialYear.id
     * @return -boolean value
     */
    public boolean delete(long financialYearId) {
        int success = executeUpdateSql(QUERY_DELETE, [id: financialYearId])
        if (!success) {
            throw new RuntimeException('Failed to delete Financial year')
        }
        return true
    }

    /**
     * applicable only for create default financial year
     */
    public void createDefaultData(long companyId) {
        new AccFinancialYear(startDate: new Date(), endDate: new Date() + 365, companyId: companyId, createdBy: 1, createdOn: new Date(), isCurrent: true).save()
    }
}
