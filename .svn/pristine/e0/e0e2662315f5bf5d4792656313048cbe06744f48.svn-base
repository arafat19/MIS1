package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.utility.ExhPaymentMethodCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.exchangehouse.utility.ExhTaskTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Search customer task and show specific list of task for grid for admin
 *  For details go through Use-Case doc named 'SearchCustomerTaskForAdminActionService'
 */
class SearchCustomerTaskForAdminActionService extends BaseService implements ActionIntf {
    private  Logger log = Logger.getLogger(getClass())

    private static final String AMOUNT_IN_FOREIGN_CURRENCY = 'amount_in_foreign_currency'
    private static final String NAME = 'name'

    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ExhTaskTypeCacheUtility exhTaskTypeCacheUtility


    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get task(s) for grid through specific search
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result
        try {
            GrailsParameterMap params=(GrailsParameterMap) parameters
            if (!params.sortname) {
                params.sortname = NAME           // set sort by 'name'
                params.sortorder = ASCENDING_SORT_ORDER          // set default sort order 'asc'
            }

            if (params.qtype.toString().equals(Tools.ID)) {
                Long.parseLong(params.query) // check if task ID can be parsed
            }

            initSearch(params)                  // initialize parameters for flexGrid

            Date startDate = DateUtility.parseMaskedFromDate(params.createdDateFrom)        // set date format ie  "dd/MM/yyyy"
            Date endDate = DateUtility.parseMaskedToDate(params.createdDateTo)              // set date format ie  "dd/MM/yyyy"
            Long outletBankId = params.outletBankId.toLong()
            SystemEntity taskStatus = (SystemEntity)exhTaskStatusCacheUtility.read(Long.parseLong(params.taskStatus))  // get task status by id

            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            String queryOnStr = null
            if (queryType.equals(AMOUNT_IN_FOREIGN_CURRENCY)) {
                queryOnStr = " ${AMOUNT_IN_FOREIGN_CURRENCY}=${Double.parseDouble(query)}"
            } else {
                queryOnStr = " ${queryType} ILIKE '${Tools.PERCENTAGE + query + Tools.PERCENTAGE}'"
            }

            List<ExhTask> taskList = getTaskList(companyId, taskStatus, outletBankId, startDate, endDate, queryOnStr)     // get list of task
            int count = getCount(companyId, taskStatus, outletBankId, startDate, endDate, queryOnStr)
            result = [taskList: taskList, count: count]
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            // what about error message?
            result = [taskList: null, count: 0]
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
     * Wrap task list for grid
     * @param taskResult -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object taskResult) {
        LinkedHashMap output
        try {
            List<ExhTask> taskList = (List<ExhTask>) taskResult.taskList
            int count = (int) taskResult.count
            List tasks = this.wrapTaskListInGridEntityList(taskList, start)
            output = [page: pageNumber, total: count, rows: tasks]
            return output
        } catch (Exception e) {
            log.error(e.getMessage())
            output = [page: pageNumber, total: 0, rows: null]
            return output
        }

    }

    /**
     * do nothing for build failure result for UI
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Wrap list of task in grid entity
     * @param taskList -list of task object(s)
     * @param start -starting index of the page
     * @return -list of wrapped tasks
     */
    private List wrapTaskListInGridEntityList(List<ExhTask> taskList, int start) {
        List tasks = []
        int counter = start + 1

        String payMethod
        Double temp_total_due
        Double amount_gbp
        double total_due
        for (ExhTask task in taskList) {
            GridEntity obj = new GridEntity()   // build grid entity object
            obj.id = task.id
            payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key    // get payment method ie 'Cash Collection' or 'Bank Deposit'
            amount_gbp = task.amountInLocalCurrency
            temp_total_due = amount_gbp + task.regularFee - task.discount
            total_due = temp_total_due.round(2)

            obj.cell = [counter, task.id, task.refNo,
                    task.amountInForeignCurrency, amount_gbp, total_due,
                    task.customerName, task.beneficiaryName, payMethod, task.regularFee, task.discount]
            tasks << obj
            counter++
        }
        return tasks
    }


    /**
     * Get list of task & count of task which task type is CUSTOMER_TASK
     * @param companyId - AppUser.companyId
     * @param status - a status can be CANCELLED, NEW_TASK, SENT_TO_BANK, SENT_TO_OTHER_BANK or RESOLVED_BY_OTHER_BANK
     * @param outletBankId
     * @param fromDate -from date
     * @param toDate   -to date
     * @param queryOnStr   -query type as string
     * @return -task list
     */
    private List<ExhTask> getTaskList(long companyId, SystemEntity status, long outletBankId, Date fromDate, Date toDate, String queryOnStr) {
        SystemEntity customerTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_CUSTOMER_TASK, companyId)

        String QUERY_TASK = """
            SELECT * FROM exh_task
            WHERE
                exh_task.company_id = ${companyId}
                AND task_type_id=${customerTaskObj.id}
                AND exh_task.current_status = ${status.id}
                AND exh_task.outlet_bank_id = ${outletBankId}
                AND exh_task.created_on BETWEEN '${fromDate}' AND '${toDate}'
                AND  ${queryOnStr}
                ORDER BY ${queryType} ${sortOrder} LIMIT ${resultPerPage} OFFSET ${start}
        """
        return getEntityList(QUERY_TASK, ExhTask.class)
    }

    /**
     * Get count of task
     * @param companyId - AppUser.companyId
     * @param status - a status can be CANCELLED, NEW_TASK, SENT_TO_BANK, SENT_TO_OTHER_BANK or RESOLVED_BY_OTHER_BANK
     * @param outletBankId
     * @param fromDate -from date
     * @param toDate   -to date
     * @param queryOnStr   -query type as string
     * @return count
     */
    private int getCount(long companyId, SystemEntity status, long outletBankId, Date fromDate, Date toDate, String queryOnStr) {
        SystemEntity customerTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_CUSTOMER_TASK, companyId)

        String QUERY_COUNT_TASK = """
            SELECT COUNT(exh_task.id) FROM exh_task
            WHERE
                exh_task.company_id = ${companyId}
                AND task_type_id=${customerTaskObj.id}
                AND exh_task.current_status = ${status.id}
                AND exh_task.outlet_bank_id = ${outletBankId}
                AND exh_task.created_on BETWEEN '${fromDate}' AND '${toDate}'
                AND ${queryOnStr}
        """
        List<GroovyRowResult> countResult = executeSelectSql(QUERY_COUNT_TASK)
        return countResult[0].count
    }
}

