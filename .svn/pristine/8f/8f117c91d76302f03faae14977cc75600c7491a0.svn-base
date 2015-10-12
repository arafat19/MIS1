package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.utility.ExhPaymentMethodCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of task for grid for Customer
 *  For details go through Use-Case doc named 'ExhShowApprovedTaskForCustomerActionService'
 */
class ExhShowApprovedTaskForCustomerActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load task information page"
    private static final String TASK_STATUS_MSG = 'Received by Exchange House'

    private static final String TASK_OBJ = 'taskObj'
    private static final String LOCAL_CURRENCY_NAME = 'localCurrencyName'
    private static final String CUSTOMER_NAME = 'customerName'

    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    CurrencyCacheUtility currencyCacheUtility

    /**
     * do nothing pre condition operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get list of Task for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            initPager(params)                                // initialize params for flexGrid

            List<ExhTask> exhTasks = listApprovedTask()       // list of task
            int total = countApprovedTask()

            result.put(TASK_OBJ, exhTasks)
            result.put(LOCAL_CURRENCY_NAME, currencyCacheUtility.localCurrency.symbol)
            result.put(Tools.COUNT, Integer.valueOf(total))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing post condition operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap task list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj          // cast map returned from execute method
            List<ExhTask> exhTasks = (List<ExhTask>) executeResult.get(TASK_OBJ)
            int total =((Integer) executeResult.get(Tools.COUNT)).intValue()
            AppUser user = exhSessionUtil.appSessionUtil.getAppUser()
            List<ExhTask> taskList = wrapTasksForCustomer(exhTasks, start)             // wrap task gor grid
            Map gridObject = [page: pageNumber, total: total, rows: taskList]           // build map for grid

            result.put(CUSTOMER_NAME, user.username)
            result.put(TASK_OBJ, gridObject)
            result.put(LOCAL_CURRENCY_NAME,  executeResult.get(LOCAL_CURRENCY_NAME))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of Task in grid entity
     * @param lstTask -list of Task object(s)
     * @param start -starting index of the page
     * @return -list of wrapped tasks
     */
    private List<ExhTask> wrapTasksForCustomer(List<ExhTask> lstTask, int start) {
        List tasks = []
        int counter = start + 1
        ExhTask task
        GridEntity obj
        String payMethod
        Double tempTotalDue
        Double amountGBP
        double totalDue
        for (int i = 0; i < lstTask.size(); i++) {
            task = lstTask[i]
            obj = new GridEntity()             // build grid entity object
            obj.id = task.id
            String createDate = DateUtility.getDateTimeFormatAsString(task.createdOn)      // set date format i.e. 'dd MMMM, yyyy [hh:mm a]'
            payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key         // get payment method ie 'Cash Collection' or 'Bank Deposit'
            amountGBP = task.amountInLocalCurrency
            tempTotalDue = amountGBP + task.regularFee - task.discount
            totalDue = tempTotalDue.round(2)
            obj.cell = [
                    counter,
                    task.refNo,
                    createDate,
                    amountGBP,
                    task.amountInForeignCurrency,
                    totalDue,
                    task.beneficiaryName,
                    payMethod,
                    TASK_STATUS_MSG
            ]
            tasks << obj
            counter++
        }
        return tasks
    }

    /**
     * Get list of task which status is NEW
     */
    private List<ExhTask> listApprovedTask() {
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
        return ExhTask.findAllByCompanyIdAndCustomerIdAndCurrentStatus(
                exhSessionUtil.appSessionUtil.getCompanyId(),
                exhSessionUtil.getUserCustomerId(),
                exhNewTaskSysEntityObject.id,
                [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true]
        )
    }

    /**
     * Get count of task which status is NEW
     */
    private int countApprovedTask() {
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
        return ExhTask.countByCompanyIdAndCustomerIdAndCurrentStatus(
                exhSessionUtil.appSessionUtil.getCompanyId(),
                exhSessionUtil.getUserCustomerId(),
                exhNewTaskSysEntityObject.id
        )
    }
}
