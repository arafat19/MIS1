package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.utility.ExhPaymentMethodCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ExhListTaskForOtherBankUserActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = "Failed to populate task list"
    private static final String REF_NO = 'refNo'

    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    public Object executePreCondition(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            if ((!parameters.createdDateFrom) || (!parameters.createdDateFrom) ||
                    (!parameters.taskStatus) || (!parameters.outletBankId)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, true)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }


    }

    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result

        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.rp) {
                params.rp = DEFAULT_RESULT_PER_PAGE
            }

            if (!params.sortname) {
                // if no sort name then sort by name/asc
                params.sortname = REF_NO
                params.sortorder = ASCENDING_SORT_ORDER
            }

            initSearch(params) // initSearch will call initPager()

            List<ExhTask> taskList = []
            int count = 0

            Date startDateStr = DateUtility.parseMaskedFromDate(params.createdDateFrom)
            Date endDateStr = DateUtility.parseMaskedToDate(params.createdDateTo)
            Long outletBankId = params.outletBankId.toLong()
            SystemEntity taskStatus = (SystemEntity) exhTaskStatusCacheUtility.read(Long.parseLong(params.taskStatus))
            Map serviceReturn = listForAdmin(taskStatus, outletBankId, startDateStr, endDateStr)

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

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

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

    public Object buildFailureResultForUI(Object obj) {
        return [page: pageNumber, total: 0, rows: null]
    }

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
            obj = new GridEntity()
            obj.id = task.id
            payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key
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

    private Map listForAdmin(SystemEntity status, Long outletBankId, Date fromDate, Date toDate) {
        //get exchange house id of the user
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        List<ExhTask> taskList = ExhTask.findAllByCompanyIdAndCurrentStatusAndOutletBankIdAndCreatedOnBetween(companyId, status.id, outletBankId, fromDate, toDate, [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
        int count = ExhTask.countByCompanyIdAndCurrentStatusAndOutletBankIdAndCreatedOnBetween(companyId, status.id, outletBankId, fromDate, toDate)

        return [taskList: taskList, count: count]
    }
}
