package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 * ChartOfAccountService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class AccChartOfAccountService extends BaseService {

    static transactional = false

    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility

    /**
     * @return - list of chart of account
     */
    public List list() {
        return AccChartOfAccount.list(sort: accChartOfAccountCacheUtility.SORT_BY_ID, order: accChartOfAccountCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    private static final String INSERT_QUERY =   """
            INSERT INTO acc_chart_of_account(id, "version", acc_custom_group_id, acc_group_id, acc_source_id,
                source_category_id, acc_type_id, code, created_by, created_on, description, is_active,
                tier1, tier2, tier3, tier4, tier5, company_id)
            VALUES ( :id, :version, :accCustomGroupId, :accGroupId, :accSourceId, :sourceCategoryId, :accTypeId,
             :code, :createdBy,:createdOn,:description,:isActive,:tier1,:tier2,:tier3,:tier4,:tier5,:companyId);
    """

    /**
     * Save chart of account object into DB
     * @param accChartOfAccount -chart of account object
     * @return -saved chart of account object
     */
    public AccChartOfAccount create(AccChartOfAccount accChartOfAccount) {

        Map queryParams = [
                id: accChartOfAccount.id,
                version: accChartOfAccount.version,
                accCustomGroupId: accChartOfAccount.accCustomGroupId,
                accGroupId: accChartOfAccount.accGroupId,
                accSourceId: accChartOfAccount.accSourceId,
                sourceCategoryId: accChartOfAccount.sourceCategoryId,
                accTypeId: accChartOfAccount.accTypeId,
                code: accChartOfAccount.code,
                createdBy: accChartOfAccount.createdBy,
                createdOn: DateUtility.getSqlDateWithSeconds(accChartOfAccount.createdOn),
                description: accChartOfAccount.description,
                isActive: accChartOfAccount.isActive,
                tier1: accChartOfAccount.tier1,
                tier2: accChartOfAccount.tier2,
                tier3: accChartOfAccount.tier3,
                tier4: accChartOfAccount.tier4,
                tier5: accChartOfAccount.tier5,
                companyId: accChartOfAccount.companyId,
        ]
        List result = executeInsertSql(INSERT_QUERY, queryParams)
        long id = (long) result[0][0]
        if (id <= 0) {
            throw new RuntimeException('Failed to save chart of account')
        }
        accChartOfAccount.id = id
        return accChartOfAccount
    }

    private static final String QUERY_UPDATE = """
                        UPDATE acc_chart_of_account SET
                            version=:newVersion,
                            acc_custom_group_id=:customGroupId,
                            acc_group_id=:groupId,
                            acc_source_id=:sourceId,
                            source_category_id=:sourceCategoryId,
                            acc_type_id=:typeId,
                            description=:description,
                            code=:code,
                            is_active=:isActive,
                            tier1=:tier1,
                            tier2=:tier2,
                            tier3=:tier3
                        WHERE id=:id AND
                             version=:version
    """

    /**
     * Update chart of account object in DB
     * @param accChartOfAccount -chart of account object
     * @return -updated chart of account object
     */
    public AccChartOfAccount update(AccChartOfAccount accChartOfAccount) {
        Map queryParams = [
                id: accChartOfAccount.id,
                version: accChartOfAccount.version,
                newVersion: accChartOfAccount.version + 1,
                customGroupId: accChartOfAccount.accCustomGroupId,
                groupId: accChartOfAccount.accGroupId,
                sourceId: accChartOfAccount.accSourceId,
                sourceCategoryId: accChartOfAccount.sourceCategoryId,
                typeId: accChartOfAccount.accTypeId,
                description: accChartOfAccount.description,
                code: accChartOfAccount.code,
                isActive: accChartOfAccount.isActive,
                tier1: accChartOfAccount.tier1,
                tier2: accChartOfAccount.tier2,
                tier3: accChartOfAccount.tier3
        ]

        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('COA update failed')
        }
        accChartOfAccount.version = accChartOfAccount.version + 1
        return accChartOfAccount
    }

    private static final String QUERY_DELETE = """
                    DELETE FROM acc_chart_of_account
                      WHERE
                          id=:id
    """

    /**
     * Delete chart of account object from DB
     * @param id -id of chart of account object
     * @return -an integer containing the value of update count
     */
    public boolean delete(long id) {
        int updateCount = executeUpdateSql(QUERY_DELETE, [id: id])
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to delete COA')
        }
        return true
    }

    /**
     *
     * @param coaId  - chart of account
     * @return - chart of account object
     */
    public AccChartOfAccount read(long coaId) {
        AccChartOfAccount accChartOfAccount = AccChartOfAccount.read(coaId)
        return accChartOfAccount
    }
}
