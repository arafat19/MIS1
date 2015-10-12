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
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Search customer task and show specific list of task for grid for cashier
 *  For details go through Use-Case doc named 'SearchCustomerTaskForCashierActionService'
 */
class SearchCustomerTaskForCashierActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ExhTaskTypeCacheUtility exhTaskTypeCacheUtility


    private static final String AMOUNT_IN_FOREIGN_CURRENCY = 'amount_in_foreign_currency'
    private static final String NAME = 'name'

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get task list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    Object execute(Object params, Object obj) {
        LinkedHashMap result
        try {
            GrailsParameterMap parameterMap=(GrailsParameterMap)params
            if (!parameterMap.sortname) {
                parameterMap.sortname = NAME                  // set sort by 'name'
                parameterMap.sortorder = ASCENDING_SORT_ORDER      // set default sort order ie 'asc'
            }

            if (parameterMap.qtype.toString().equals(Tools.ID)) {
                Long.parseLong(parameterMap.query)
            }
            initSearch(parameterMap)                  // initialize parameters for flexGrid

            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()

            String queryOnStr = null
            if (queryType.equals(AMOUNT_IN_FOREIGN_CURRENCY)) {
                queryOnStr = " ${AMOUNT_IN_FOREIGN_CURRENCY}=${Double.parseDouble(query)}"
            } else {
                queryOnStr = " ${queryType} ILIKE '${Tools.PERCENTAGE + query + Tools.PERCENTAGE}'"
            }

            List<ExhTask> taskList = getTaskList(companyId, queryOnStr)      // get list of task(s)
            int count = getCount(companyId, queryOnStr)

            result = [taskList: taskList, count: count]
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
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
            List<ExhTask> taskList = (List<ExhTask>) taskResult.taskList       // cast object returned from execute method
            int count = (int) taskResult.count
            List tasks = warpTaskList(taskList, start)
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
     * @param lstTask -list of task object(s)
     * @param start -starting index of the page
     * @return -list of wrapped tasks
     */
    private List warpTaskList(List<ExhTask> lstTask, int start) {
        List tasks = []
        if (lstTask == null) return tasks
        int counter = start + 1
        String payMethod
        String status
        Double temp_total_due
        Double amount_gbp
        double total_due
        for (ExhTask task in lstTask) {
            GridEntity obj = new GridEntity()
            obj.id = task.id
            payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key    // get payment method i.e. Cash Collection or Bank Deposit
            amount_gbp = task.amountInLocalCurrency
            temp_total_due = amount_gbp + task.regularFee - task.discount
            total_due = temp_total_due.round(2)
            status = exhTaskStatusCacheUtility.read(task.currentStatus).key

            obj.cell = [counter, task.id, task.refNo, task.currentStatus,
                    task.amountInForeignCurrency, amount_gbp, total_due,
                    task.customerName, task.beneficiaryName, payMethod, status]
            tasks << obj
            counter++
        }
        return tasks
    }

    /**
     * @param companyId
     * @param queryOnStr - a type of query
     * @return -count of task
     */
    private int getCount(long companyId, String queryOnStr) {
        SystemEntity customerTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_CUSTOMER_TASK, companyId)
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhStatusUnApprovedSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_UN_APPROVED, companyId)

        String QUERY_COUNT_TASK = """
            SELECT COUNT(exh_task.id) FROM exh_task
            WHERE
                exh_task.company_id = ${companyId}
                AND task_type_id=${customerTaskObj.id}
                AND exh_task.current_status IN (${exhStatusUnApprovedSysEntityObject.id},
                                                 ${exhNewTaskSysEntityObject.id})
                AND ${queryOnStr}
        """
        List<GroovyRowResult> countResult = executeSelectSql(QUERY_COUNT_TASK)
        return countResult[0].count
    }

    /**
     * @param companyId
     * @param queryOnStr - a type of query
     * @return -list of task
     */
    private List<ExhTask> getTaskList(long companyId, String queryOnStr) {
        SystemEntity customerTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_CUSTOMER_TASK, companyId)
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhStatusUnApprovedSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_UN_APPROVED, companyId)

        String QUERY_TASK = """
            SELECT * FROM exh_task
            WHERE
                exh_task.company_id = ${companyId}
                AND task_type_id=${customerTaskObj.id}
                AND exh_task.current_status IN (${exhStatusUnApprovedSysEntityObject.id},
                                                ${exhNewTaskSysEntityObject.id})
                AND  ${queryOnStr}
            ORDER BY ${queryType} ${sortOrder} LIMIT ${resultPerPage} OFFSET ${start}
        """
        return getEntityList(QUERY_TASK, ExhTask.class)
    }
}
