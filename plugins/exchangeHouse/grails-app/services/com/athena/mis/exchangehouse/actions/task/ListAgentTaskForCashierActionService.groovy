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
 *  Show list of agent task for grid for cashier
 *  For details go through Use-Case doc named 'ListAgentTaskForCashierActionService'
 */
class ListAgentTaskForCashierActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String REF_NO = 'refNo'

    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhTaskTypeCacheUtility exhTaskTypeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil


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
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameterMap, Object obj) {
        LinkedHashMap result
        try {
            GrailsParameterMap params=(GrailsParameterMap) parameterMap
            if (!params.rp) {
                params.rp = DEFAULT_RESULT_PER_PAGE     // set default result per page ie '15'
            }

            if (!params.sortname) {
                params.sortname = REF_NO                       // set sort name ie REF_NO
                params.sortorder = ASCENDING_SORT_ORDER       // set default sort order ie 'asc'
            }

            initSearch(params)                   // initialize parameters for flexGrid

            List<ExhTask> taskList = []
            int count = 0

            LinkedHashMap serviceReturn = listAgentTask()   // get list of task

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
            List<ExhTask> taskList = (List<ExhTask>) taskResult.taskList            // cast object returned from execute method
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
     * Wrap list of Task in grid entity
     * @param lstTask -list of Task object(s)
     * @param start -starting index of the page
     * @return -list of wrapped tasks
     */
    private List warpTaskList(List<ExhTask> lstTask, int start) {

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
            obj = new GridEntity()         // build grid entity object
            obj.id = task.id
            payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key       // get payment method i.e. Cash Collection or Bank Deposit
            amount_gbp = task.amountInLocalCurrency
            temp_total_due = amount_gbp + task.regularFee - task.discount
            total_due = temp_total_due.round(2)
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
                    task.discount]
            tasks << obj
            counter++
        }
        return tasks
    }

    /**
     * Get a map containing list & count of task which status is NEW & type of AGENT_TASK
     */
    private LinkedHashMap listAgentTask() {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity agentTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_AGENT_TASK, companyId)
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)

        List<ExhTask> taskList = ExhTask.findAllByCompanyIdAndTaskTypeIdAndCurrentStatus(
                companyId,
                agentTaskObj.id,
                exhNewTaskSysEntityObject.id,
                [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true]
        )
        int count = ExhTask.countByCompanyIdAndTaskTypeIdAndCurrentStatus(
                companyId,
                agentTaskObj.id,
                exhNewTaskSysEntityObject.id
        )
        return [taskList: taskList, count: count]
    }
}
