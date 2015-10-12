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
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of agent task for grid for agent
 *  For details go through Use-Case doc named 'ExhListTaskForCustomerActionService'
 */
class ExhListTaskForCustomerActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String SORT_BY = "refNo"

    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhTaskTypeCacheUtility exhTaskTypeCacheUtility

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get task list through beneficiary if exists or not for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object execute(Object params, Object obj) {
        LinkedHashMap result
        try {
            GrailsParameterMap parameterMap=(GrailsParameterMap)params
            if (!parameterMap.rp) {
                parameterMap.rp = DEFAULT_RESULT_PER_PAGE           // set default result per page ie '15'
            }

            if (!parameterMap.sortname) {
                // if no sort name then sort by name/asc
                parameterMap.sortname = SORT_BY
                parameterMap.sortorder = ASCENDING_SORT_ORDER
            }

            initSearch(parameterMap)                   // initialize parameters for flexGrid

            List<ExhTask> taskList = []
            int count = 0
            long beneficiaryId = 0L
            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity customerTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_CUSTOMER_TASK, companyId)

            if (parameterMap.beneficiaryId) {
                beneficiaryId = Long.parseLong(parameterMap.beneficiaryId.toString())
                taskList = listTask(beneficiaryId, companyId, customerTaskObj.id)            // get task by beneficiary
                count = countTask(beneficiaryId, companyId, customerTaskObj.id)
            } else {
                taskList = listTask(companyId, customerTaskObj.id)
                count = countTask(companyId, customerTaskObj.id)
            }

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
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap output
        try {
            Map executeResult = (Map) obj               // cast object returned from execute method
            List<ExhTask> taskList = (List<ExhTask>) executeResult.taskList
            int count = (int) executeResult.count
            List tasks = wrapTasksForCustomer(taskList, start)        // wrap task(s)
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
    private List wrapTasksForCustomer(List<ExhTask> lstTask, int start) {
        List tasks = []
        int counter = start + 1
        ExhTask task
        GridEntity obj
        String payMethod
        Double tempTotalDue
        Double amountGBP
        double totalDue
        String taskPinNo
        for (int i = 0; i < lstTask.size(); i++) {
            task = lstTask[i]
            obj = new GridEntity()                      // build grid entity object
            obj.id = task.id
            String createDate = DateUtility.getDateTimeFormatAsString(task.createdOn)       // get date format ie  "dd MMMM,yyyy [hh:mm a]"
            payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key       // get payment method i.e. Cash Collection or Bank Deposit
            amountGBP = task.amountInLocalCurrency
            tempTotalDue = amountGBP + task.regularFee - task.discount
            totalDue = tempTotalDue.round(2)
            taskPinNo = task.pinNo ? task.pinNo : Tools.NOT_APPLICABLE
            obj.cell = [counter, task.id, task.refNo,
                    task.amountInForeignCurrency, amountGBP, totalDue,
                    task.customerName, task.beneficiaryName, payMethod, task.regularFee, task.discount,
                    createDate, task.currentStatus, taskPinNo]

            tasks << obj
            counter++
        }
        return tasks
    }

    /**
     * Get list of task which status is PENDING & type of CUSTOMER_TASK
     * @param beneficiaryId
     * @return -list of task
     */
    private List<ExhTask> listTask(long beneficiaryId, long companyId, long customerTaskId) {
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, exhSessionUtil.appSessionUtil.getCompanyId())

        return ExhTask.findAllByCompanyIdAndUserIdAndTaskTypeIdAndBeneficiaryIdAndCurrentStatus(
                companyId,
                exhSessionUtil.appSessionUtil.getAppUser().id,
                customerTaskId,
                beneficiaryId,
                exhPendingTaskSysEntityObject.id,
                [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true]
        )
    }

    /**
     * Get count of task which status is PENDING & type of CUSTOMER_TASK
     * @param beneficiaryId
     * @return -count of task
     */
    private int countTask(long beneficiaryId, long companyId, long customerTaskId) {
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, exhSessionUtil.appSessionUtil.getCompanyId())

        return ExhTask.countByCompanyIdAndUserIdAndTaskTypeIdAndBeneficiaryIdAndCurrentStatus(
                companyId,
                exhSessionUtil.appSessionUtil.getAppUser().id,
                customerTaskId,
                beneficiaryId,
                exhPendingTaskSysEntityObject.id
        )
    }

    /**
     * Get count of task which status is PENDING & type of CUSTOMER_TASK
     */
    private List<ExhTask> listTask(long companyId, long customerTaskId) {
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, exhSessionUtil.appSessionUtil.getCompanyId())

        return ExhTask.findAllByCompanyIdAndUserIdAndTaskTypeIdAndCurrentStatus(
                companyId,
                exhSessionUtil.appSessionUtil.getAppUser().id,
                customerTaskId,
                exhPendingTaskSysEntityObject.id,
                [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true]
        )
    }

    /**
     * Get count of task which status is PENDING & type of CUSTOMER_TASK
     */
    private int countTask(long companyId, long customerTaskId) {
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, exhSessionUtil.appSessionUtil.getCompanyId())

        return ExhTask.countByCompanyIdAndUserIdAndTaskTypeIdAndCurrentStatus(
                companyId,
                exhSessionUtil.appSessionUtil.getAppUser().id,
                customerTaskId,
                exhPendingTaskSysEntityObject.id
        )
    }

}
