package com.athena.mis.arms.service

import com.athena.mis.BaseService
import com.athena.mis.arms.entity.RmsTransactionDay
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

class RmsTransactionDayService extends BaseService{

    @Autowired
    RmsSessionUtil rmsSessionUtil

    /**
     * list rmsTransactionDay with baseService criteria
     */
    public List list(BaseService baseService, long companyId) {
        return RmsTransactionDay.findAllByCompanyId(companyId, [limit: baseService.resultPerPage,
                start: baseService.start,sort: baseService.sortColumn, order: baseService.sortOrder,
                readOnly: true]);
    }

    /**
     * count all rmsTransactionDay
     */
    public int count(long companyId) {
        return RmsTransactionDay.countByCompanyId(companyId);
    }

    /**
     * save rmsTransactionDay in DB
     */
    public RmsTransactionDay create(RmsTransactionDay rmsTransactionDay){
        rmsTransactionDay.save(flush: true)
        return rmsTransactionDay
    }

    /**
     * find rmsTransactionDay by Id
     */
    public RmsTransactionDay read(long id) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        RmsTransactionDay rmsTransactionDay = RmsTransactionDay.findByIdAndCompanyId(id, companyId, [readOnly: true])
        return rmsTransactionDay
    }

    private static final String UPDATE_QUERY = """
        UPDATE rms_transaction_day SET
            version=:newVersion,
            transaction_date=:transactionDate,
            closed_by=:closedBy,
            closed_On=:closedOn,
            opened_by=:openedBy,
            opened_On=:openedOn,
            company_id=:companyId
        WHERE
            id=:id AND
            version=:version
    """

    /**
     * update RmsTransactionDay for closing transaction day
     */
    public int updateForClose(RmsTransactionDay rmsTransactionDay){
        Map queryParams = [
                id: rmsTransactionDay.id,
                newVersion: rmsTransactionDay.version + 1,
                version: rmsTransactionDay.version,
                transactionDate: DateUtility.getSqlDate(rmsTransactionDay.transactionDate),
                openedBy: rmsTransactionDay.openedBy,
                openedOn: DateUtility.getSqlDateWithSeconds(rmsTransactionDay.openedOn),
                closedBy: rmsTransactionDay.closedBy,
                closedOn: DateUtility.getSqlDateWithSeconds(rmsTransactionDay.closedOn),
                companyId: rmsTransactionDay.companyId
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating transaction day')
        }
        return updateCount
    }

    /**
     * update RmsTransactionDay for re-opening transaction day
     */
    public int updateForReOpen(RmsTransactionDay rmsTransactionDay){
        Map queryParams = [
                id: rmsTransactionDay.id,
                newVersion: rmsTransactionDay.version + 1,
                version: rmsTransactionDay.version,
                transactionDate: DateUtility.getSqlDate(rmsTransactionDay.transactionDate),
                openedBy: rmsTransactionDay.openedBy,
                openedOn: DateUtility.getSqlDateWithSeconds(rmsTransactionDay.openedOn),
                closedBy: rmsTransactionDay.closedBy,
                closedOn: rmsTransactionDay.closedOn,
                companyId: rmsTransactionDay.companyId
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating transaction day')
        }
        return updateCount
    }
}
