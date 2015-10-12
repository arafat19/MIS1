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
 *  Show list of exh task for grid for cashier
 *  For details go through Use-Case doc named 'ListExhTaskForCashierActionService'
 */
class ListExhTaskForCashierActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String REF_NO = 'refNo'
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
     * Get task list through beneficiary if exists or not for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result
        try {
            GrailsParameterMap params=(GrailsParameterMap) parameters
            if (!params.rp) {
                params.rp = DEFAULT_RESULT_PER_PAGE        // set default result per page ie '15'
            }

            if (!params.sortname) {
                params.sortname = REF_NO                     // set order by 'ref_no'
                params.sortorder = ASCENDING_SORT_ORDER      // set default sort order ie 'asc'
            }

            initSearch(params)          // initialize parameters for flexGrid

            List<ExhTask> taskList = []
            int count = 0
            long beneficiaryId = 0L
            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity taskTypeObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_EXH_TASK, companyId)

            if (params.beneficiaryId) {
                beneficiaryId = Long.parseLong(params.beneficiaryId.toString())
                taskList = listTask(beneficiaryId, companyId, taskTypeObj.id)         // get task by beneficiary
                count = countTask(beneficiaryId, companyId, taskTypeObj.id)
            } else {
                taskList = listTask(companyId, taskTypeObj.id)
                count = countTask(companyId, taskTypeObj.id)
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
     * @param taskResult -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object taskResult) {
        LinkedHashMap output
        try {
            List<ExhTask> taskList = (List<ExhTask>) taskResult.taskList      // cast object returned from execute method
            int count = (int) taskResult.count
            List tasks = this.wrapTaskList(taskList, start)
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
        Double temp_total_due
        Double amount_gbp
        double total_due
        for (int i = 0; i < lstTask.size(); i++) {
            task = lstTask[i]
            obj = new GridEntity()     // build grid entity object
            obj.id = task.id
            payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key         // get payment method i.e. Cash Collection or Bank Deposit
            amount_gbp = task.amountInLocalCurrency
            temp_total_due = amount_gbp + task.regularFee - task.discount
            total_due = temp_total_due.round(2)
			String gatewayPayment = task.isGatewayPaymentDone ? Tools.YES : LABEL_NO_RED
            obj.cell = [counter,
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
     * Get list of task which status is NEW & type of EXH_TASK
     * @param beneficiaryId
     * @return -list of task
     */
    private List<ExhTask> listTask(long beneficiaryId, long companyId, long taskTypeId) {
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
        return ExhTask.findAllByCompanyIdAndTaskTypeIdAndBeneficiaryIdAndCurrentStatus(
                companyId,
                taskTypeId,
                beneficiaryId, exhNewTaskSysEntityObject.id,
                [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
    }

    /**
     * Get count of task which status is NEW & type of EXH_TASK
     * @param beneficiaryId
     * @return -count of task
     */
    private int countTask(long beneficiaryId, long companyId, long taskTypeId) {
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
        return ExhTask.countByCompanyIdAndTaskTypeIdAndBeneficiaryIdAndCurrentStatus(
                companyId,
                taskTypeId,
                beneficiaryId,
                exhNewTaskSysEntityObject.id
        )
    }

    /**
     * Get list of task which status is NEW & type of EXH_TASK
     */
    private List<ExhTask> listTask(long companyId, long taskTypeId) {
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
        return ExhTask.findAllByCompanyIdAndTaskTypeIdAndCurrentStatus(
                companyId,
                taskTypeId,
                exhNewTaskSysEntityObject.id,
                [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true]
        )
    }

    /**
     * Get count of task which status is NEW & type of EXH_TASK
     */
    private int countTask(long companyId, long taskTypeId) {
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
        return ExhTask.countByCompanyIdAndTaskTypeIdAndCurrentStatus(
                companyId,
                taskTypeId,
                exhNewTaskSysEntityObject.id
        )
    }
}
