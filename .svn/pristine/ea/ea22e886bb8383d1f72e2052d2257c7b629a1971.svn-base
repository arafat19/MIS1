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
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of agent task  for grid for admin
 *  For details go through Use-Case doc named 'ListAgentTaskForAdminActionService'
 */
class ListAgentTaskForAdminActionService extends BaseService implements ActionIntf {
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
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameterMap, Object obj) {
        LinkedHashMap result

        try {
            GrailsParameterMap params=(GrailsParameterMap) parameterMap
            if (!params.rp) {
                params.rp = DEFAULT_RESULT_PER_PAGE       // set default result per page '15'
            }

            if (!params.sortname) {
                params.sortname = REF_NO
                params.sortorder = ASCENDING_SORT_ORDER    // set default sort order 'asc'
            }

            initSearch(params)           // initialize parameters for flexGrid

            List<ExhTask> taskList = []
            int count = 0

            Date startDateStr = DateUtility.parseMaskedFromDate(params.createdDateFrom)         // set date format ie  "dd/MM/yyyy"
            Date endDateStr = DateUtility.parseMaskedToDate(params.createdDateTo)                // set date format ie  "dd/MM/yyyy"
            Long outletBankId = params.outletBankId.toLong()
            SystemEntity taskStatus = (SystemEntity)exhTaskStatusCacheUtility.read(Long.parseLong(params.taskStatus))     // get task status by id
            LinkedHashMap serviceReturn = listAgentTaskForAdmin(taskStatus, outletBankId, startDateStr, endDateStr)       // get list of task

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
            List<ExhTask> taskList = (List<ExhTask>) taskResult.taskList
            int count = (int) taskResult.count
            List tasks = wrapTaskListInGridEntityList(taskList, start)
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
        ExhTask task
        GridEntity obj
        String payMethod
        Double temp_total_due
        Double amount_gbp
        double total_due
        for (int i = 0; i < taskList.size(); i++) {
            task = taskList[i]
            obj = new GridEntity()         // build grid entity object
            obj.id = task.id
            payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key     // get payment method ie 'Cash Collection' or 'Bank Deposit'
            amount_gbp = task.amountInLocalCurrency
            temp_total_due = amount_gbp + task.regularFee - task.discount
            total_due = temp_total_due.round(2)

            obj.cell = ["${counter}", task.id, task.refNo,
                    task.amountInForeignCurrency, amount_gbp, total_due,
                    task.customerName, task.beneficiaryName, payMethod, task.regularFee, task.discount]
            tasks << obj
            counter++
        }
        return tasks
    }

    /**
     * Get list of task & count of task which task type is AGENT_TASK
     * @param status - a status can be CANCELLED, NEW_TASK, SENT_TO_BANK, SENT_TO_OTHER_BANK or RESOLVED_BY_OTHER_BANK
     * @param outletBankId
     * @param fromDate -from date
     * @param toDate   -to date
     * @return -a map containing taskList & count
     */
    private LinkedHashMap listAgentTaskForAdmin(SystemEntity status, Long outletBankId, Date fromDate, Date toDate) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity agentTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_AGENT_TASK, companyId)

        List<ExhTask> taskList =ExhTask.findAllByCompanyIdAndCurrentStatusAndOutletBankIdAndCreatedOnBetweenAndTaskTypeId(
                companyId,
                status.id,
                outletBankId,
                fromDate,
                toDate,
                agentTaskObj.id,
                [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true]
        )
        int count = ExhTask.countByCompanyIdAndCurrentStatusAndOutletBankIdAndCreatedOnBetweenAndTaskTypeId(
                companyId,
                status.id,
                outletBankId,
                fromDate,
                toDate,
                agentTaskObj.id
        )
        return [taskList: taskList, count: count]
    }
}

