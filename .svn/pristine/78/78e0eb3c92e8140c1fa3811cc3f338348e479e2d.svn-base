package com.athena.mis.integration.accounting.actions

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.service.AccFinancialYearService
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Update financial year for content count
 * For details go through Use-Case doc named 'UpdateContentCountForFinancialYearImplActionService'
 */
class UpdateContentCountForFinancialYearImplActionService extends BaseService implements ActionIntf {

    ReadFinancialYearImplActionService readFinancialYearImplActionService
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional
    public Object execute(Object financialYearIdObj, Object countIdObj) {
        try {
            long financialYearId = Long.parseLong(financialYearIdObj.toString())
            int count = Long.parseLong(countIdObj.toString())
            return updateForContentCount(financialYearId, count)
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new Integer(0)
        }
    }

    /**
     * Do nothing for build success result for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Do nothing for build failure result for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    private static final String UPDATE_QUERY = """
        UPDATE acc_financial_year
        SET content_count = content_count + :contentCount,
            version = version + 1
        WHERE
            id=:id
    """


    /**
     * Update content count of FinancialYear
     * @param financialYearId - AccFinancialYear.id
     * @param count - content count
     * @return - an integer object of update counter
     */
    private Integer updateForContentCount(long financialYearId, int count) {
        Map queryParams = [
                contentCount: count,
                id: financialYearId
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        Object financialYear = readFinancialYearImplActionService.execute(financialYearId,null)
        accFinancialYearCacheUtility.update(financialYear,accFinancialYearCacheUtility.SORT_BY_ID, accFinancialYearCacheUtility.SORT_ORDER_ASCENDING)
        return (new Integer(updateCount))
    }

}
