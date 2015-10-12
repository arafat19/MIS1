package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.exchangehouse.utility.ExhPaidByCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


/**
 * Show list of cashier wise task report for Admin.
 * For details go through Use-Case doc named 'ExhListCashierWiseReportAdminActionService'
 */
class ExhListCashierWiseReportAdminActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    @Autowired
    ExhPaidByCacheUtility exhPaidByCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil


    private static final String EXCEPTION_MESSAGE = "Failed to load cashier wise task report"
    private static final String DEFAULT_ERROR_MESSAGE = "Can not load cashier wise task report"

    private static final String FROM_DATE_NOT_AVAILABLE = "From date is not available"
    private static final String TO_DATE_NOT_AVAILABLE = "To date is not available"
    private static final String CASHIERS_NOT_AVAILABLE = "Cashier is not available"

    private static final String SERVICE_RETURN_OBJ = "serviceReturn"
    private static final String GRID_OUT_PUT = "gridOutput"
    private static final String SUMMARY_DUE = "summaryDue"

    /**
     * Check necessary parameters
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!(DateUtility.parseMaskedDate(params.formDate))) {
                result.put(Tools.MESSAGE, FROM_DATE_NOT_AVAILABLE)
                return result
            }
            if (!(DateUtility.parseMaskedDate(params.toDate))) {
                result.put(Tools.MESSAGE, TO_DATE_NOT_AVAILABLE)
                return result
            }
            if (!params.cashierIds) {
                result.put(Tools.MESSAGE, CASHIERS_NOT_AVAILABLE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            return result
        }
    }

    /**
     * Get list of cashier wise task report for grid
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            Date formDate = DateUtility.parseMaskedFromDate(params.formDate)
            Date toDate = DateUtility.parseMaskedToDate(params.toDate)
            List ids = params.cashierIds.split(Tools.UNDERSCORE)
            String commaSeparatedUserId = buildCommaSeparatedStringOfIds(ids)

            initPager(params)           // initialize params for flexGrid

            LinkedHashMap serviceReturn = listCashierWiseTaskReport(commaSeparatedUserId, formDate, toDate)     // get list of cashier wise task report
            result.put(SERVICE_RETURN_OBJ, serviceReturn)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            return result
        }

    }

    /**
     * Build comma separated of id
     * @param ids
     * @return strIds
     */
    private String buildCommaSeparatedStringOfIds(List ids) {
        String strIds = Tools.EMPTY_SPACE
        for (int i = 0; i < ids.size(); i++) {
            strIds = strIds + ids[i]
            if ((i + 1) < ids.size()) strIds = strIds + Tools.COMA
        }
        return strIds
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
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj      // cast map returned from execute method
            LinkedHashMap serviceReturn = (LinkedHashMap) executeResult.get(SERVICE_RETURN_OBJ)
            List<GroovyRowResult> listOfTasks = (List<GroovyRowResult>) serviceReturn.taskList

            List taskInfoList = wrapTasksInGrid(listOfTasks, start)       // wrap list

            int count = (int) serviceReturn.count
            double total_amount_gbp = (double) serviceReturn.total_amount_gbp ? serviceReturn.total_amount_gbp : 0
            double total_commission = (double) serviceReturn.total_comission ? serviceReturn.total_comission : 0
            double total_discount = (double) serviceReturn.total_discount ? serviceReturn.total_discount : 0

            double tempSummaryDue = total_amount_gbp + total_commission - total_discount
            double summaryDue = tempSummaryDue.round(2)
            Map gridOutput = [page: pageNumber, total: count, rows: taskInfoList]

            result.put(GRID_OUT_PUT, gridOutput)
            result.put(SUMMARY_DUE, summaryDue)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            log.error(ex.getMessage());
            return result
        }
    }

    /**
     * Wrap list of tasks in grid entity
     * @param listOfTasks
     * @param start -starting index of the page
     * @return -list of wrapped taskInfos
     */
    private List wrapTasksInGrid(List<GroovyRowResult> listOfTasks, int start) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity paidByCashObj = (SystemEntity) exhPaidByCacheUtility.readByReservedAndCompany(exhPaidByCacheUtility.PAID_BY_ID_CASH, companyId)
        SystemEntity paidByCardObj = (SystemEntity) exhPaidByCacheUtility.readByReservedAndCompany(exhPaidByCacheUtility.PAID_BY_ID_PAY_POINT, companyId)

        List taskInfos = []
        Integer counter = start + 1;
        double received_in_till, received_onLine, received_by_card, temp_total_due, total_due, commission, discount
        int paidById
        String createdOnStr
        for (int i = 0; i < listOfTasks.size(); i++) {
            GridEntity obj = new GridEntity()
            obj.id = listOfTasks[i].id
            received_in_till = 0
            received_onLine = 0
            received_by_card = 0
            commission = listOfTasks[i].comission ? listOfTasks[i].comission : 0
            discount = listOfTasks[i].discount ? listOfTasks[i].discount : 0

            createdOnStr = DateUtility.getDateForUI(listOfTasks[i].created_on)

            temp_total_due = (double) listOfTasks[i].total_due ? listOfTasks[i].total_due : 0
            total_due = temp_total_due.round(2)
            paidById = (int) listOfTasks[i].paid_by_id


            if (paidById == paidByCashObj.id) {
                received_in_till = total_due
            } else if (paidById == paidByCardObj.id) {
                received_by_card = total_due
            } else {
                received_onLine = total_due
            }

            obj.cell = [counter, createdOnStr, listOfTasks[i].cashier_name,
                    listOfTasks[i].ref_no, listOfTasks[i].transactioan_type, listOfTasks[i].customer_name,
                    listOfTasks[i].beneficiary_name, listOfTasks[i].amount_bdt, listOfTasks[i].conversion_rate,
                    listOfTasks[i].amount_gbp,
                    commission.round(2),
                    discount.round(2),
                    received_in_till, received_by_card, received_onLine]
            taskInfos << obj
            counter++
        };

        return taskInfos
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap returnResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (returnResult.message) {
                result.put(Tools.MESSAGE, returnResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }

    /**
     * Get list of task summary between dates
     * @param fromDate
     * @param toDate
     * @return a map containing taskList & total
     */
    private LinkedHashMap listCashierWiseTaskReport(String commaSeparatedUserId, Date fromDate, Date toDate) {

        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
        SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
        SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

        String queryStr = """
            SELECT
            task.id as id,
                task.created_on as created_on,
                app_user.username as cashier_name,
                task.ref_no as ref_no,
                payment_method.key as transactioan_type,
                task.customer_name as customer_name,
                task.beneficiary_name as beneficiary_name,
                task.amount_in_foreign_currency as amount_bdt,
                task.conversion_rate as conversion_rate,
                task.amount_in_local_currency as amount_gbp,
                task.regular_fee as comission,
                task.discount as discount,
                (task.amount_in_local_currency+task.regular_fee - task.discount) as total_due,
                task.paid_by as paid_by_id
            FROM
                exh_task task
            left join app_user on task.user_id=app_user.id
            left join system_entity payment_method on task.payment_method = payment_method.id
            WHERE
                task.company_id=${companyId}
                AND user_id IN (${commaSeparatedUserId})
                AND task.created_on BETWEEN '${DateUtility.getFromDateWithSecond(fromDate)}' AND '${DateUtility.getToDateWithSecond(toDate)}'
                AND current_status IN('${exhNewTaskSysEntityObject.id}',
                '${exhSentToBankSysEntityObject.id}',
                '${exhSentToOtherBankSysEntityObject.id}',
                '${exhResolvedByOtherBankSysEntityObject.id}')
                ORDER BY task.ref_no asc LIMIT ${resultPerPage} OFFSET ${start}
        """

        String count_query = """
            SELECT
                count(task.id) as count,
                sum(task.amount_in_local_currency) as total_amount_gbp,
                sum(task.regular_fee) as total_comission,
                sum(task.discount) as total_discount
            FROM
                 exh_task task
            WHERE
                task.company_id=${companyId}
                AND task.user_id IN (${commaSeparatedUserId})
                AND current_status IN('${exhNewTaskSysEntityObject.id}',
                '${exhSentToBankSysEntityObject.id}',
                '${exhSentToOtherBankSysEntityObject.id}',
                '${exhResolvedByOtherBankSysEntityObject.id}')
                AND task.created_on BETWEEN '${DateUtility.getFromDateWithSecond(fromDate)}' AND '${DateUtility.getToDateWithSecond(toDate)}'
        """

        List<GroovyRowResult> taskList = executeSelectSql(queryStr)

        List<GroovyRowResult> count_result = executeSelectSql(count_query)
        int total = count_result[0].count

        double total_amount_gbp = count_result[0].total_amount_gbp ? count_result[0].total_amount_gbp : 0
        double total_comission = count_result[0].total_comission ? count_result[0].total_comission : 0
        double total_discount = count_result[0].total_discount ? count_result[0].total_discount : 0

        return [taskList: taskList, count: total, total_amount_gbp: total_amount_gbp,
                total_comission: total_comission,
                total_discount: total_discount]
    }
}