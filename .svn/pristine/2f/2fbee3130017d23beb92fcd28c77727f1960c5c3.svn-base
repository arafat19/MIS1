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
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of customer task for grid for cashier
 *  For details go through Use-Case doc named 'ListCustomerTaskForCashierActionService'
 */
class ListCustomerTaskForCashierActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String CURRENT_STATUS = 'currentStatus'

    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhTaskTypeCacheUtility exhTaskTypeCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result
        try {
            GrailsParameterMap params=(GrailsParameterMap) parameters
            if (!params.rp) {
                params.rp = DEFAULT_RESULT_PER_PAGE
            }

            if (!params.sortname) {
                params.sortname = CURRENT_STATUS                  // set sort name by current_status
                params.sortorder = ASCENDING_SORT_ORDER          // set default sort order ie 'asc'
            }

            initSearch(params)                                  // initialize parameters for flexGrid

            List<ExhTask> taskList = listCustomerTask()          // get list of task
            int taskCount = (int) countTask()

            result = [taskList: taskList, count: taskCount]
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
            List<ExhTask> taskList = (List<ExhTask>) taskResult.taskList        // cast object returned from execute method
            int count = (int) taskResult.count
            List tasks = wrapTaskList(taskList, start)
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
     * Wrap list of Task in grid entity
     * @param lstTask -list of Task object(s)
     * @param start -starting index of the page
     * @return -list of wrapped tasks
     */
    private List wrapTaskList(List<ExhTask> lstTask, int start) {

        List tasks = []
        int counter = start + 1
        ExhTask task
        GridEntity obj
        String payMethod
        String status
        Double temp_total_due
        Double amount_gbp
        double total_due
        for (int i = 0; i < lstTask.size(); i++) {
            task = lstTask[i]
            obj = new GridEntity()          // build grid entity object
            obj.id = task.id
            payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key    // get payment method i.e. Cash Collection or Bank Deposit
            amount_gbp = task.amountInLocalCurrency
            temp_total_due = amount_gbp + task.regularFee - task.discount
            total_due = temp_total_due.round(2)
            status = exhTaskStatusCacheUtility.read(task.currentStatus).key           // get task status ie UNAPPROVED or NEW

            obj.cell = [
                    counter,
                    task.id,
                    task.refNo,
                    task.currentStatus,
                    task.amountInForeignCurrency,
                    amount_gbp,
                    total_due,
                    task.customerName,
                    task.beneficiaryName,
                    payMethod,
                    status
            ]
            tasks << obj
            counter++
        }
        return tasks
    }

    /**
     * Get list of task which status is NEW & UN-APPROVED & type of CUSTOMER_TASK
     */
    private List<ExhTask> listCustomerTask() {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity customerTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_CUSTOMER_TASK, companyId)
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhStatusUnApprovedSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_UN_APPROVED, companyId)

        return ExhTask.findAllByCompanyIdAndTaskTypeIdAndCurrentStatusInList(
                companyId,
                customerTaskObj.id,
                [exhStatusUnApprovedSysEntityObject.id,
                        exhNewTaskSysEntityObject.id],
                [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true]
        )
    }

    /**
     * Get count of  task which status is NEW & UN-APPROVED & type of CUSTOMER_TASK
     */
    private int countTask() {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity customerTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_CUSTOMER_TASK, companyId)
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhStatusUnApprovedSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_UN_APPROVED, companyId)

        return ExhTask.countByCompanyIdAndTaskTypeIdAndCurrentStatusInList(
                companyId,
                customerTaskObj.id,
                [exhStatusUnApprovedSysEntityObject.id,
                        exhNewTaskSysEntityObject.id
                ]
        )
    }
}
