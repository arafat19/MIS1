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
 *  Search other bank task and show specific list of task for grid for Other bank user
 *  For details go through Use-Case doc named 'ExhSearchTaskForOtherBankUserActionService'
 */
class ExhSearchTaskForOtherBankUserActionService extends BaseService implements ActionIntf {
    private  Logger log = Logger.getLogger(getClass())

    private static final String COMPANY_ID = 'companyId'
    private static final String CURRENT_STATUS = 'currentStatus'
    private static final String OUTLET_BANK_ID = 'outletBankId'
    private static final String CREATED_ON = 'createdOn'
    private static final String AMOUNT_IN_FOREIGN_CURRENCY = 'amountInForeignCurrency'
    private static final String NAME = 'name'
    private static final String FAILURE_MESSAGE = "Failed to populate task list"

    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    /**
     * Get parameters from UI and check required parameters
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }

    }

    /**
     * Get task(s) for grid through specific search
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    Object execute(Object params, Object obj) {
        LinkedHashMap result
        try {
            GrailsParameterMap parameterMap=(GrailsParameterMap) params
            if (!parameterMap.sortname) {
                parameterMap.sortname = NAME             // set sort by 'name'
                parameterMap.sortorder = ASCENDING_SORT_ORDER           // set default sort order 'asc'
            }

            if (parameterMap.qtype.toString().equals(Tools.ID)) {
                Long.parseLong(parameterMap.query) // check if task ID can be parsed
            }

            initSearch(params)               // initialize parameters for flexGrid
            List<ExhTask> taskList = []
            int count = 0

            Date startDateStr = DateUtility.parseMaskedFromDate(parameterMap.createdDateFrom)     // set date format ie  "dd/MM/yyyy"
            Date endDateStr = DateUtility.parseMaskedToDate(parameterMap.createdDateTo)           // set date format ie  "dd/MM/yyyy"
            Long outletBankId = parameterMap.outletBankId.toLong()
            SystemEntity taskStatus = (SystemEntity)exhTaskStatusCacheUtility.read(Long.parseLong(parameterMap.taskStatus))  // get task status by id
            LinkedHashMap serviceReturn = searchForOtherBank(taskStatus, outletBankId, startDateStr, endDateStr)             // get list of task

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
     *  do nothing for post operation
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
            List tasks = this.wrapTaskListInGridEntityList(taskList, start)
            output = [page: pageNumber, total: count, rows: tasks]
            return output
        } catch (Exception e) {
            log.error(e.getMessage())
            output = [page: pageNumber, total: 0, rows: null]
            return output
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
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
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
            obj = new GridEntity()       // build grid entity object
            obj.id = task.id
            payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key    // get payment method ie 'Cash Collection' or 'Bank Deposit'
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
     * Get list of task & count of task which task type is EXH_TASK, AGENT_TASK or CUSTOMER_TASK
     * @param status - a status SENT_TO_OTHER_BANK or RESOLVED_BY_OTHER_BANK
     * @param outletBankId
     * @param fromDate
     * @param toDate
     * @return -a map containing taskList & total
     */
    private LinkedHashMap searchForOtherBank(SystemEntity status, Long outletBankId, Date fromDate, Date toDate) {
        String strQuery = null
        double amountInForeignCurrency = 0d
         if (queryType.equals(AMOUNT_IN_FOREIGN_CURRENCY)) {
            amountInForeignCurrency = Double.parseDouble(query)
        } else {
            strQuery = Tools.PERCENTAGE + query + Tools.PERCENTAGE
        }

        //get exchange house id of the user
        long companyId =  exhSessionUtil.appSessionUtil.getCompanyId()
        List<ExhTask> taskList = ExhTask.withCriteria {
            eq(COMPANY_ID, companyId)
            eq(CURRENT_STATUS, status.id)
            eq(OUTLET_BANK_ID, outletBankId)
            between(CREATED_ON, fromDate, toDate)
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
            eq(CURRENT_STATUS, status.id)
            eq(OUTLET_BANK_ID, outletBankId)
            between(CREATED_ON, fromDate, toDate)
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