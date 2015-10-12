package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Show remittance summary report for UI level.
 * For details go through Use-Case doc named 'ExhRemittanceSummaryReportActionService'
 */
class ExhRemittanceSummaryReportActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    CurrencyCacheUtility currencyCacheUtility

    /**
     * do nothing pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get remittance summary report for UI
     * list contains such columns are totalTask, totalAmount(BDT), totalAmount(Local)
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all report necessary for UI
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map params
        try {
            params = (Map) parameters
            Date startDate = DateUtility.parseMaskedDate(params.startDate)
            Date endDate = DateUtility.parseMaskedDate(params.endDate)
            Map serviceReturn = (Map) getRemittanceSummary(startDate, endDate)      // get count of task report
            return serviceReturn
        } catch (Exception e) {
            log.error(e.getMessage())
            return null
        }
    }

    /**
     * do nothing success operation
     */
    public Object buildSuccessResultForUI(Object result) {
        return null
    }

    /**
     * do nothing failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Get remittance summary between dates
     * @param startDate
     * @param endDate
     * @return a map containing new task, send to bank & send to others bank report
     */
    private Map getRemittanceSummary(Date startDate, Date endDate) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
        SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
        SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

        String queryNewStatus = """
            SELECT
                COUNT(id) AS count,
                SUM(amount_in_foreign_currency) as total_foreign_amount,
                SUM(amount_in_local_currency) as total_local_amount
            FROM
                exh_task task
            WHERE
                (created_on BETWEEN '${DateUtility.getFromDateWithSecond(startDate)}' AND '${DateUtility.getToDateWithSecond(endDate)}') AND
                current_status IN (${exhNewTaskSysEntityObject.id},
                                        ${exhSentToBankSysEntityObject.id},
                                        ${exhSentToOtherBankSysEntityObject.id},
                                        ${exhResolvedByOtherBankSysEntityObject.id})
                AND company_id = '${companyId}'
        """
        String querySentToBankStatus = """
            SELECT
                COUNT(id) AS count,
                SUM(amount_in_foreign_currency) as total_foreign_amount,
                SUM(amount_in_local_currency) as total_local_amount
            FROM
                exh_task task
            WHERE
                (created_on BETWEEN '${DateUtility.getFromDateWithSecond(startDate)}' AND '${DateUtility.getToDateWithSecond(endDate)}') AND
                current_status='${exhSentToBankSysEntityObject.id}' AND
                company_id = '${companyId}'
            """
        String querySentToOtherBankStatus = """
            SELECT
                COUNT(id) AS count,
                SUM(amount_in_foreign_currency) as total_foreign_amount,
                SUM(amount_in_local_currency) as total_local_amount
            FROM
                exh_task task
            WHERE
                (created_on BETWEEN '${DateUtility.getFromDateWithSecond(startDate)}' AND '${DateUtility.getToDateWithSecond(endDate)}') AND
                current_status IN (${exhSentToOtherBankSysEntityObject.id},${exhResolvedByOtherBankSysEntityObject.id})
                AND company_id = '${companyId}'
            """

        List lstTaskList = executeSelectSql(queryNewStatus)
        List sentTaskList = executeSelectSql(querySentToBankStatus)
        List sentToOtherTaskList = executeSelectSql(querySentToOtherBankStatus)
        String localCurrencyName = currencyCacheUtility.localCurrency.symbol
        return [newTaskList: lstTaskList, sentTaskList: sentTaskList,
                sentToOtherTaskList: sentToOtherTaskList, localCurrencyName: localCurrencyName]
    }
}
