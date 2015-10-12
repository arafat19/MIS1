package com.athena.mis.sarb.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.sarb.utility.SarbSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * list sarb transaction summary
 * for details go through use-case named "ListSarbTransactionSummaryActionService"
 */
class ListSarbTransactionSummaryActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())
    private static final String FAILED_TO_LOAD = "Failed to load Sarb Transaction Summary"
    private static final String GRID_OBJ = "gridObj"

    @Autowired
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired
    SarbSessionUtil sarbSessionUtil
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    /**
     * do nothing for pre-condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for post-condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initPager(params)
            Date startDate = DateUtility.parseMaskedFromDate(params.fromDate)
            Date endDate = DateUtility.parseMaskedToDate(params.toDate)
            String fromDate = DateUtility.getFromDateWithSecond(startDate)
            String toDate = DateUtility.getToDateWithSecond(endDate)
            long companyId = sarbSessionUtil.appSessionUtil.getCompanyId()
            List<Long> lstTaskStatus = exchangeHouseImplService.listTaskStatusForSarb()
            List<Long> lstExcludingStatus = exchangeHouseImplService.listTaskStatusForExcludingSarb()
            List<GroovyRowResult> lstTransaction = listSarbTransactionSummary(lstTaskStatus, lstExcludingStatus, companyId, fromDate, toDate)
            int count = countSarbTransactionSummary(lstTaskStatus, lstExcludingStatus, companyId, fromDate, toDate)
            List lstWrappedResult = wrapTransactionSummary(lstTransaction, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedResult]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, gridObj)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_LOAD)
            return result
        }
    }

    /**
     * do nothing for build success
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj
            String msg = preResult.get(Tools.MESSAGE)
            if (msg) {
                result.put(Tools.MESSAGE, msg)
            } else {
                result.put(Tools.MESSAGE, FAILED_TO_LOAD)
            }
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_LOAD)
            return result
        }
    }

    private List wrapTransactionSummary(List<GroovyRowResult> lstTransaction, int start) {
        List lstWrappedResult = []
        int counter = start + 1
        Currency currency = currencyCacheUtility.getLocalCurrency()
        for (int i = 0; i < lstTransaction.size(); i++) {
            GroovyRowResult transaction = lstTransaction[i]
            GridEntity obj = new GridEntity()
            obj.id = counter
            obj.cell = [
                    counter,
                    DateUtility.getDateFormatAsString(transaction.created_on),
                    transaction.tot_secl,
                    Double.parseDouble(transaction.tot_amount_secl.toString()).round(2) + Tools.SINGLE_SPACE + currency.symbol,
                    transaction.tot_accepted,
                    Double.parseDouble(transaction.tot_amount_accepted.toString()).round(2) + Tools.SINGLE_SPACE + currency.symbol,
                    transaction.tot_rejected,
                    Double.parseDouble(transaction.tot_amount_rejected.toString()).round(2) + Tools.SINGLE_SPACE + currency.symbol
            ]
            lstWrappedResult << obj
            counter++
        }
        return lstWrappedResult
    }

    private List<GroovyRowResult> listSarbTransactionSummary(List<Long> lstTaskStatus, List<Long> lstExcludingStatus, long companyId, String fromDate, String toDate) {
        String strTaskStatus = Tools.PARENTHESIS_START + Tools.buildCommaSeparatedStringOfIds(lstTaskStatus) + Tools.PARENTHESIS_END
        String strExcludingTaskStatus = Tools.PARENTHESIS_START + Tools.buildCommaSeparatedStringOfIds(lstExcludingStatus) + Tools.PARENTHESIS_END
        String SQL = """
                        select created_on, sum(task_secl) tot_secl, sum(amount_secl) as tot_amount_secl,
                        sum(task_accepted) as tot_accepted, sum(amount_accepted) as tot_amount_accepted,
                        sum(task_rejected) as tot_rejected, sum(amount_rejected) as tot_amount_rejected
                        FROM(
                        select date(created_on) as created_on,
                        case when current_status IN ${strTaskStatus} then 1 else 0 end AS task_secl,
                        case when current_status IN ${strTaskStatus} then amount_in_local_currency else 0 end AS amount_secl,
                        case when is_accepted_by_sarb = true AND is_cancelled = false then 1 else 0 end AS task_accepted,
                        case when is_accepted_by_sarb = true AND is_cancelled = false then amount_in_local_currency else 0 end AS amount_accepted,
                        case when is_accepted_by_sarb = false AND is_submitted_to_sarb = true then 1 else 0 end AS task_rejected,
                        case when is_accepted_by_sarb = false AND is_submitted_to_sarb = true then amount_in_local_currency else 0 end AS amount_rejected
                        from vw_sarb_task_model
                        where created_on between '${fromDate}' and '${toDate}'
                        AND current_status NOT IN ${strExcludingTaskStatus}
                        and company_id = :companyId
                        ) task
                        GROUP BY task.created_on
                        offset ${start} limit ${resultPerPage}
                    """
        Map queryParams = [companyId: companyId]
        List<GroovyRowResult> lstTransactionSummary = executeSelectSql(SQL, queryParams)
        return lstTransactionSummary
    }

    private int countSarbTransactionSummary(List<Long> lstTaskStatus, List<Long> lstExcludingStatus, long companyId, String fromDate, String toDate) {
        String strTaskStatus = Tools.PARENTHESIS_START + Tools.buildCommaSeparatedStringOfIds(lstTaskStatus) + Tools.PARENTHESIS_END
        String strExcludingTaskStatus = Tools.PARENTHESIS_START + Tools.buildCommaSeparatedStringOfIds(lstExcludingStatus) + Tools.PARENTHESIS_END
        String SQL = """
                        select count(id)
                        FROM(
                        select id, date(created_on) created_on
                        from vw_sarb_task_model
                        where created_on between '${fromDate}' and '${toDate}'
                        AND current_status NOT IN ${strExcludingTaskStatus} AND current_status IN ${strTaskStatus}
                        and company_id = :companyId
                        ) task
                        GROUP BY task.created_on
                    """
        Map queryParams = [companyId: companyId]
        List<GroovyRowResult> lstTransactionSummary = executeSelectSql(SQL, queryParams)
        return lstTransactionSummary.size()
    }
}
