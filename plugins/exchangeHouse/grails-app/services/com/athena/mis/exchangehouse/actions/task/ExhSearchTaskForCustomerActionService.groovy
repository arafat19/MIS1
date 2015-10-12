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
 *  Search customer task and show specific list of task for grid for customer
 *  For details go through Use-Case doc named 'ExhSearchTaskForCustomerActionService'
 */
class ExhSearchTaskForCustomerActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String COMPANY_ID = 'companyId'
    private static final String USER_ID = 'userId'
    private static final String CURRENT_STATUS = 'currentStatus'
    private static final String BENEFICIARY_ID = 'beneficiaryId'
    private static final String REF_NO = 'refNo'
    private static final String AMOUNT_IN_FOREIGN_CURRENCY = 'amountInForeignCurrency'

    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
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

    /**
     * Get task searchByBeneficiary for grid through specific search
     * 1. if exists beneficiary then search by beneficiary id otherwise normal search by customer id
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
                parameterMap.sortname = REF_NO                // set sort by 'ref_no'
                parameterMap.sortorder = ASCENDING_SORT_ORDER       // set default sort order 'asc'
            }

            initSearch(parameterMap)                  // initialize parameters for flexGrid

            List<ExhTask> taskList = []
            int count = 0
            LinkedHashMap serviceReturn
            long beneficiaryId = 0L
            if (parameterMap.beneficiaryId) {
                beneficiaryId = Long.parseLong(parameterMap.beneficiaryId.toString())
                serviceReturn = searchByBeneficiary(beneficiaryId)             // get task by beneficiary
            } else {
                serviceReturn = searchTask()
            }

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
            Map executeResult = (Map) taskResult           // cast object returned from execute method
            List<ExhTask> taskList = (List<ExhTask>) executeResult.taskList
            int count = (int) executeResult.count
            List tasks = wrapTasksForCustomer(taskList, start)  // wrap tasks
            output = [page: pageNumber, total: count, rows: tasks]
            return output
        } catch (Exception e) {
            log.error(e.getMessage());
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
            obj = new GridEntity()          // build grid entity object
            obj.id = task.id
            String createDate = DateUtility.getDateTimeFormatAsString(task.createdOn)         // get date format ie "dd MMMM,yyyy [hh:mm a]"
            payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key     // get payment method ie 'Cash Collection' or 'Bank Deposit'
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
     * Get list of task
     * @return a map containing taskList &  total of task(s)
     */
    private LinkedHashMap searchTask() {
        String strQuery = query
        double amountInForeignCurrency = 0d
        if (queryType.equals(AMOUNT_IN_FOREIGN_CURRENCY)) {
            amountInForeignCurrency = Double.parseDouble(strQuery)
        } else {
            strQuery = Tools.PERCENTAGE + strQuery + Tools.PERCENTAGE
        }

        //get exchange house id of the user
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        long userId = exhSessionUtil.appSessionUtil.getAppUser().id
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
        Long pendingStatus = exhPendingTaskSysEntityObject.id
        List<ExhTask> taskList = ExhTask.withCriteria {
            eq(COMPANY_ID, companyId)
            eq(USER_ID, userId)
            eq(CURRENT_STATUS, pendingStatus)
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
            eq(USER_ID, userId)
            eq(CURRENT_STATUS, pendingStatus)
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

    /**
     * Get list of task
     * @param beneficiaryId
     * @return a map containing taskList &  total of task(s)
     */
    private LinkedHashMap searchByBeneficiary(long beneficiaryId) {
        String strQuery = query
        double amountInForeignCurrency = 0d
        if (queryType.equals(AMOUNT_IN_FOREIGN_CURRENCY)) {
            amountInForeignCurrency = Double.parseDouble(strQuery)
        } else {
            strQuery = Tools.PERCENTAGE + strQuery + Tools.PERCENTAGE
        }

        long userId = exhSessionUtil.appSessionUtil.getAppUser().id
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, companyId)

        Long pendingStatus = exhPendingTaskSysEntityObject.id
        List<ExhTask> taskList = ExhTask.withCriteria {
            eq(COMPANY_ID, companyId)
            eq(USER_ID, userId)
            eq(BENEFICIARY_ID, beneficiaryId)
            eq(CURRENT_STATUS, pendingStatus)
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
            eq(USER_ID, userId)
            eq(BENEFICIARY_ID, beneficiaryId)
            eq(CURRENT_STATUS, pendingStatus)
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
