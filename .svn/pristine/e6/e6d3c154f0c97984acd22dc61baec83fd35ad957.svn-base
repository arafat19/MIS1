package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.exchangehouse.utility.ExhPaidByCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Show list of cashier wise remittance summary for Admin.
 * For details go through Use-Case doc named 'ExhListCashierWiseReportSummaryAdminActionService'
 */
class ExhListCashierWiseReportSummaryAdminActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass());

    @Autowired
    ExhPaidByCacheUtility exhPaidByCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    private static final String DEFAULT_ERROR_MESSAGE = "Can not load cashier wise remittance summary report"
    private static final String FROM_DATE_NOT_AVAILABLE = "From date is not available"
    private static final String TO_DATE_NOT_AVAILABLE = "To date is not available"

    private static final String SERVICE_RETURN_OBJ = "serviceReturn"
    private static final String GRID_OUT_PUT = "gridOutput"

    /**
     * Check necessary parameters
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            if (!(DateUtility.parseMaskedDate(parameterMap.formDate))) {
                result.put(Tools.MESSAGE, FROM_DATE_NOT_AVAILABLE)
                return result
            }
            if (!(DateUtility.parseMaskedDate(parameterMap.toDate))) {
                result.put(Tools.MESSAGE, TO_DATE_NOT_AVAILABLE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Get list of cashier wise remittance summary for grid
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            Date formDate = DateUtility.parseMaskedFromDate(params.formDate)
            Date toDate = DateUtility.parseMaskedToDate(params.toDate)

            initPager(params)                  // initialize params for flexGrid

            LinkedHashMap serviceReturn = listCashierWiseRemittanceSummaryReport(formDate, toDate)     // get list of remittance summary report
            result.put(SERVICE_RETURN_OBJ, serviceReturn)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }

    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap remittance summary list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj      // cast map returned from execute method
            LinkedHashMap serviceReturn = (LinkedHashMap) executeResult.get(SERVICE_RETURN_OBJ)
            List<GroovyRowResult> listOfTasks = (List<GroovyRowResult>) serviceReturn.taskList
            List taskInfoList = wrapTasksInGrid(listOfTasks, start)           // wrap listOfTask

            int count = (int) serviceReturn.count
            Map gridOutput = [page: pageNumber, total: count, rows: taskInfoList]

            result.put(GRID_OUT_PUT, gridOutput)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of tasks result in grid entity
     * @param listOfTasks
     * @param start -starting index of the page
     * @return -list of wrapped taskInfos
     */
    private List wrapTasksInGrid(List<GroovyRowResult> listOfTasks, int start) {
        List taskInfos = []
        Integer counter = start + 1;
        String createdOnStr
        for (int i = 0; i < listOfTasks.size(); i++) {
            GridEntity obj = new GridEntity()
            createdOnStr = DateUtility.getDateFormatAsString(listOfTasks[i].created_on)
            double online_minus_comm = listOfTasks[i].online_minus_comm ? listOfTasks[i].online_minus_comm : 0

            double sum_rem_stg = listOfTasks[i].sum_rem_stg ? listOfTasks[i].sum_rem_stg : 0
            double tot_comm = listOfTasks[i].tot_comm ? listOfTasks[i].tot_comm : 0
            double tot_dis = listOfTasks[i].tot_dis ? listOfTasks[i].tot_dis : 0
            double taka_equivalent = listOfTasks[i].taka_equivalent ? listOfTasks[i].taka_equivalent : 0
            double sum_tot_due = listOfTasks[i].sum_tot_due ? listOfTasks[i].sum_tot_due : 0

            obj.cell = [counter, createdOnStr, online_minus_comm.round(2),
                    sum_rem_stg.round(2), tot_comm.round(2), tot_dis.round(2),
                    taka_equivalent.round(2),
                    sum_tot_due.round(2)
            ]
            taskInfos << obj
            counter++
        }
        return taskInfos
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap returnResult = (LinkedHashMap) obj        // cast map returned from execute method

            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (returnResult.message) {
                result.put(Tools.MESSAGE, returnResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Get list of remittance summary between dates
     * @param fromDate
     * @param toDate
     * @return a map containing taskList & total
     */
    private LinkedHashMap listCashierWiseRemittanceSummaryReport(Date fromDate, Date toDate) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity paidByOnlineObj = (SystemEntity) exhPaidByCacheUtility.readByReservedAndCompany(exhPaidByCacheUtility.PAID_BY_ID_ONLINE, companyId)

        fromDate = DateUtility.setFirstHour(fromDate)
        toDate = DateUtility.setLastHour(toDate)

        SystemEntity exhCanceledTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_CANCELLED_TASK, companyId)
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
        SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
        SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

        long cancelledStatus = exhCanceledTaskSysEntityObject.id
        long paidByOnline = paidByOnlineObj.id

        String query = """
        select a.created_on, a.sum_rem_stg, a.tot_comm, a.tot_dis, a.taka_equivalent, a.sum_tot_due, COALESCE(d.online_minus_comm,0) AS online_minus_comm
        from
            (select
            date(created_on) as created_on,
            sum(amount_in_local_currency) as sum_rem_stg,
            sum(discount) as tot_dis,
            sum(regular_fee)as tot_comm,
            sum(amount_in_foreign_currency) as taka_equivalent,
            sum(amount_in_local_currency)+sum(regular_fee)-sum(discount) as sum_tot_due
            from exh_task task
            where task.current_status IN('${exhNewTaskSysEntityObject.id}',
                '${exhSentToBankSysEntityObject.id}',
                '${exhSentToOtherBankSysEntityObject.id}',
                '${exhResolvedByOtherBankSysEntityObject.id}')
            and task.created_on between '${DateUtility.getDBDateFormatWithSecond(fromDate)}' and '${DateUtility.getDBDateFormatWithSecond(toDate)}'
            group by date(created_on) order by date(created_on)) a
        FULL OUTER JOIN
            (
            select
            (sum(amount_in_local_currency)+sum(regular_fee)-sum(discount))-sum(regular_fee) online_minus_comm,
            date(exh_task.created_on) as created_on
            from exh_task
            where
            exh_task.paid_by=${paidByOnline} and exh_task.current_status
            IN('${exhNewTaskSysEntityObject.id}',
                '${exhSentToBankSysEntityObject.id}',
                '${exhSentToOtherBankSysEntityObject.id}',
                '${exhResolvedByOtherBankSysEntityObject.id}')
            and exh_task.created_on between '${DateUtility.getDBDateFormatWithSecond(fromDate)}' and '${DateUtility.getDBDateFormatWithSecond(toDate)}'
            group by date(exh_task.created_on) order by date(created_on)) as d ON a.created_on=d.created_on
        LIMIT ${resultPerPage} OFFSET ${start}
        """
        List<GroovyRowResult> taskList = executeSelectSql(query)
        String count_query = """
        SELECT count(a.created_on)AS count
        FROM
            (select sum(amount_in_local_currency) sum_rem_stg , date(created_on) created_on
            from exh_task
            where (date(created_on) between '${DateUtility.getDBDateFormatWithSecond(fromDate)}' and '${DateUtility.getDBDateFormatWithSecond(toDate)}')
            AND current_status IN('${exhNewTaskSysEntityObject.id}',
                '${exhSentToBankSysEntityObject.id}',
                '${exhSentToOtherBankSysEntityObject.id}',
                '${exhResolvedByOtherBankSysEntityObject.id}')
            group by date(created_on)) as a
        """
        List<GroovyRowResult> count_result = executeSelectSql(count_query)
        int total = count_result[0].count
        return [taskList: taskList, count: total]
    }

}