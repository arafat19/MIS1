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
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Search exh task and show specific list of task for grid for cashier
 *  For details go through Use-Case doc named 'SearchExhTaskForCashierActionService'
 */
class SearchExhTaskForCashierActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String NAME = 'name'
    private static final String AMOUNT_IN_FOREIGN_CURRENCY = 'amountInForeignCurrency'
    private static final String COMPANY_ID = 'companyId'
    private static final String BENEFICIARY_ID = 'beneficiaryId'
    private static final String CURRENT_STATUS = 'currentStatus'
    private static final String TASK_TYPE_ID = 'taskTypeId'
	private static final String LABEL_NO_RED = "<span style='color:red;'>NO</span>"

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
     * Get task list for grid through specific search*
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result
        try {
            GrailsParameterMap params=(GrailsParameterMap) parameters
            if (!params.sortname) {
                params.sortname = NAME         // set sort by 'name'
                params.sortorder = ASCENDING_SORT_ORDER          // set default sort order 'asc'
            }

            if (params.qtype.toString().equals(Tools.ID)) {
                Long.parseLong(params.query)
            }

            initSearch(params)                      // initialize parameters for flexGrid

            List<ExhTask> taskList = []
            int count = 0

            long beneficiaryId = 0L
            if (params.beneficiaryId) {
                beneficiaryId = Long.parseLong(params.beneficiaryId.toString())
            }
            LinkedHashMap serviceReturn = search(beneficiaryId)       // get task by beneficiary

            taskList = (List<ExhTask>) serviceReturn.taskList
            count = (int) serviceReturn.count

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
            List tasks = wrapTaskList(taskList, start)       // wrap task
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
    private List wrapTaskList(List<ExhTask> taskList, int start) {
        List tasks = []
        if (taskList == null) return tasks
        int counter = start + 1
        ExhTask task
        GridEntity obj
        String payMethod
        Double temp_total_due
        Double amount_gbp
        double total_due
        for (int i = 0; i < taskList.size(); i++) {
            task = taskList[i]
            obj = new GridEntity()          // build grid entity object
            obj.id = task.id
            payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key      // get payment method ie 'Cash Collection' or 'Bank Deposit'
            amount_gbp = task.amountInLocalCurrency
            temp_total_due = amount_gbp + task.regularFee - task.discount
            total_due = temp_total_due.round(2)
			String gatewayPayment = task.isGatewayPaymentDone ? Tools.YES : LABEL_NO_RED
            obj.cell = [
                    "${counter}",
                    task.id,
                    task.refNo,
                    task.amountInForeignCurrency,
                    amount_gbp,
                    total_due,
                    task.customerName,
                    task.beneficiaryName,
                    payMethod,
                    task.regularFee,
                    task.discount,
					gatewayPayment
            ]
            tasks << obj
            counter++
        }
        return tasks
    }

    /**
     * Get list of task
     * @param beneficiaryId
     * @return a map containing taskList &  total of task(s)
     */
    private LinkedHashMap search(long beneficiaryId) {
        String strQuery = query
        double amountInForeignCurrency = 0d
        if (queryType.equals(AMOUNT_IN_FOREIGN_CURRENCY)) {
            amountInForeignCurrency = Double.parseDouble(strQuery)
        } else {
            strQuery = Tools.PERCENTAGE + strQuery + Tools.PERCENTAGE
        }

        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity taskTypeObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_EXH_TASK, companyId)
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)

        List<ExhTask> taskList = ExhTask.withCriteria {
            eq(COMPANY_ID, companyId)
            eq(TASK_TYPE_ID, taskTypeObj.id)
            if (beneficiaryId > 0) {
                eq(BENEFICIARY_ID, beneficiaryId)
            }
            eq(CURRENT_STATUS, exhNewTaskSysEntityObject.id)
            if (queryType.equals(AMOUNT_IN_FOREIGN_CURRENCY)) {
                eq(queryType, amountInForeignCurrency)
            } else {
                ilike(queryType, strQuery)
            }
            maxResults(resultPerPage)
            firstResult(start)
            order(sortColumn, sortOrder)
            setReadOnly(true)
        }

        List counts = ExhTask.withCriteria {
            eq(COMPANY_ID, companyId)
            eq(TASK_TYPE_ID, taskTypeObj.id)
            if (beneficiaryId > 0) {
                eq(BENEFICIARY_ID, beneficiaryId)
            }
            eq(CURRENT_STATUS, exhNewTaskSysEntityObject.id)
            if (queryType.equals(AMOUNT_IN_FOREIGN_CURRENCY)) {
                eq(queryType, amountInForeignCurrency)
            } else {
                ilike(queryType, strQuery)
            }
            projections { rowCount() }
        }

        int total = counts[0] as int
        return [taskList: taskList, count: total]
    }

}
