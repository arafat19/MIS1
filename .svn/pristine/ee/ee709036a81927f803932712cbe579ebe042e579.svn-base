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

/**
 *  Show list of approved task for grid for Customer
 *  For details go through Use-Case doc named 'ExhListApprovedTaskForCustomerActionService'
 */
class ExhListApprovedTaskForCustomerActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load task information page"
    private static final String STATUS_NEW_TASK = 'Received by Exchange House'
    private static final String TASK_OBJ = 'taskObj'
    private static final String GRID_OBJ = 'gridObj'

    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get list of task for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            initPager(parameterMap)                              // initialize parameters for flexGrid

            LinkedHashMap serviceReturn = listApprovedTask()       // get list of approved task
            List<ExhTask> taskList = (List<ExhTask>) serviceReturn.taskList
            int count = (int) serviceReturn.count

            result.put(TASK_OBJ, taskList)
            result.put(Tools.COUNT, Integer.valueOf(count))
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
     * do nothing for post operation
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
            Map executeResult = (Map) obj           // cast map returned from execute method
            List<ExhTask> taskList = (List<ExhTask>) executeResult.get(TASK_OBJ)
            int total = ((Integer) executeResult.get(Tools.COUNT)).intValue()
            List<ExhTask> tasks = wrapTasksForCustomer(taskList, start)           // wrap task(s)
            Map gridObject = [page: pageNumber, total: total, rows: tasks]
            result.put(GRID_OBJ, gridObject)
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
                LinkedHashMap preResult = (LinkedHashMap) obj           // cast map returned from execute method
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
            task = lstTask[i];
            obj = new GridEntity();                // build grid entity object
            obj.id = task.id;
            String createDate = DateUtility.getDateTimeFormatAsString(task.createdOn)              // set date format i.e. 'dd MMMM, yyyy [hh:mm a]'
            payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key                  // get payment method ie 'Cash Collection' or 'Bank Deposit'
            amountGBP = task.amountInLocalCurrency
            tempTotalDue = amountGBP + task.regularFee - task.discount
            totalDue = tempTotalDue.round(2)
            obj.cell = [counter,
                    task.refNo,
                    createDate,
                    amountGBP,
                    task.amountInForeignCurrency,
                    totalDue,
                    task.beneficiaryName,
                    payMethod,
                    STATUS_NEW_TASK]
            tasks << obj
            counter++
        }
        return tasks;
    }

    /**
     * Get list & count of task which status is NEW
     */
    private LinkedHashMap listApprovedTask() {
        long customerId = exhSessionUtil.getUserCustomerId()
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        List<ExhTask> taskList = ExhTask.findAllByCompanyIdAndCustomerIdAndCurrentStatus(
                companyId,
                customerId,
                exhNewTaskSysEntityObject.id,
                [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true]
        )
        int count = ExhTask.countByCompanyIdAndCustomerIdAndCurrentStatus(
                companyId,
                customerId,
                exhNewTaskSysEntityObject.id
        )

        return [taskList: taskList, count: count]
    }
}
