package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
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
 *  Show list of remittance transaction for admin & cashier
 *  For details go through Use-Case doc named 'ExhListRemittanceTransactionActionService'
 */
class ExhListRemittanceTransactionActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    private static final String EXCEPTION_MESSAGE = "Failed to load remittance transaction report"
    private static final String FROM_DATE_NOT_AVAILABLE = "From date is not available"
    private static final String TO_DATE_NOT_AVAILABLE = "To date is not available"
    private static final String SERVICE_RETURN_OBJ = "serviceReturn"
    private static final String GRID_OUT_PUT = "gridOutput"

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
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            if (!(DateUtility.parseMaskedDate(parameterMap.formDate))) {
                result.put(Tools.MESSAGE, FROM_DATE_NOT_AVAILABLE)
                return result
            }
            if (!(DateUtility.parseMaskedDate(parameterMap.toDate))) {
                result.put(Tools.MESSAGE, TO_DATE_NOT_AVAILABLE)
                return result
            }
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
     * Get list of remittance transaction for grid
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
            Date formDate = DateUtility.parseMaskedFromDate(params.formDate)      // get date format e.g. "dd/MM/yyyy"
            Date toDate = DateUtility.parseMaskedToDate(params.toDate)
            double amount = Double.parseDouble(params.amount)

            initPager(params)              // initialize params for flexGrid

            LinkedHashMap serviceReturn = listTransactionSummaryReport(formDate, toDate, amount)        // get list of remittance transaction
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

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap remittance transaction list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj           // cast map returned from execute method
            LinkedHashMap serviceReturn = (LinkedHashMap) executeResult.serviceReturn
            List<GroovyRowResult> listOfTasks = (List<GroovyRowResult>) serviceReturn.taskList
            List taskInfoList = wrapTasksInGrid(listOfTasks, start)
            int count = (int) serviceReturn.count

            Map gridOutput = [page: pageNumber, total: count, rows: taskInfoList]

            result.put(GRID_OUT_PUT, gridOutput)
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
     * Wrap list of listOfTasks as remittance transaction in grid entity
     * @param listOfTasks
     * @param start -starting index of the page
     * @return -list of wrapped taskInfos
     */
    private List wrapTasksInGrid(List<GroovyRowResult> listOfTasks, int start) {
        List taskInfos = []
        Integer counter = start + 1
        for (int i = 0; i < listOfTasks.size(); i++) {
            GridEntity obj = new GridEntity()
            GroovyRowResult result = listOfTasks[i]
            obj.id = result.customer_code
            obj.cell = [counter,
                    result.customer_code,
                    result.customer_name,
                    DateUtility.getDateFormatAsString((Date) result.customer_dob),
                    result.beneficiary_name,
                    result.task_count,
                    Double.parseDouble(result.total_amount_in_local.toString()).round(2),
                    Double.parseDouble(result.total_amount_in_foreign.toString()).round(2)
            ]
            taskInfos.add(obj)
            counter++
        }
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
                result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            return result
        }
    }

    /**
     * Get list of remittance transaction between dates
     * @param fromDate
     * @param toDate
     * @param amount
     * @return map containing taskList & count
     */
    private LinkedHashMap listTransactionSummaryReport(Date fromDate, Date toDate, double amount) {
        fromDate = DateUtility.setFirstHour(fromDate)
        toDate = DateUtility.setFirstHour(toDate)
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
        SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
        SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

        String strQuery = """
            SELECT
                SUM(exh_task.amount_in_foreign_currency) total_amount_in_foreign,
                SUM(exh_task.amount_in_local_currency) total_amount_in_local,
                COUNT(exh_task.id) task_count,
                (exh_customer.name || ' ' || COALESCE(exh_customer.surname,'')) as customer_name,
                exh_customer.code as customer_code,
                exh_customer.date_of_birth customer_dob,
                exh_task.beneficiary_name as beneficiary_name,
                exh_task.beneficiary_id
            FROM public.exh_task
            JOIN public.exh_customer ON exh_task.customer_id = exh_customer.id
            WHERE
                exh_task.company_id=${companyId} AND
                exh_task.created_on BETWEEN '${DateUtility.getFromDateWithSecond(fromDate)}' AND '${DateUtility.getToDateWithSecond(toDate)}' AND
                exh_task.current_status IN (${exhNewTaskSysEntityObject.id}, ${exhSentToBankSysEntityObject.id},
                ${exhSentToOtherBankSysEntityObject.id}, ${exhResolvedByOtherBankSysEntityObject.id})
            GROUP BY
                exh_task.customer_id,
                exh_customer.name,
                exh_customer.surname,
                exh_customer.code,
                exh_customer.date_of_birth,
                exh_task.beneficiary_id,
                exh_task.beneficiary_name
                HAVING
                    ( SELECT SUM(t.amount_in_local_currency)
                      FROM exh_task t
                      WHERE t.customer_id = exh_task.customer_id
                      AND t.created_on BETWEEN '${DateUtility.getFromDateWithSecond(fromDate)}' AND '${DateUtility.getToDateWithSecond(toDate)}') >= ${amount}
            ORDER BY exh_customer.name, exh_task.beneficiary_name LIMIT ${resultPerPage} OFFSET ${start}
            """

        String countQuery = """
            SELECT COUNT(*) FROM
            (
                SELECT
                    exh_task.customer_id,
                    exh_task.beneficiary_id
                FROM public.exh_task
                WHERE
                    exh_task.company_id=${companyId}
                    AND exh_task.created_on BETWEEN '${DateUtility.getFromDateWithSecond(fromDate)}' AND '${DateUtility.getToDateWithSecond(toDate)}'
                    AND exh_task.current_status IN (${exhNewTaskSysEntityObject.id}, ${exhSentToBankSysEntityObject.id},
                    ${exhSentToOtherBankSysEntityObject.id}, ${exhResolvedByOtherBankSysEntityObject.id})
                GROUP BY
                    exh_task.customer_id,
                    exh_task.beneficiary_id,
                    exh_task.beneficiary_name
                    HAVING
                        ( SELECT SUM(t.amount_in_local_currency)
                        FROM exh_task t
                        WHERE t.customer_id = exh_task.customer_id
                        AND t.created_on BETWEEN '${DateUtility.getFromDateWithSecond(fromDate)}' AND '${DateUtility.getToDateWithSecond(toDate)}') >= ${amount}
            ) as result
        """

        List<GroovyRowResult> taskList = executeSelectSql(strQuery)
        List<GroovyRowResult> count_result = executeSelectSql(countQuery)
        int total = count_result[0].count

        return [taskList: taskList, count: total]
    }
}
