package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of transaction summary for admin & cashier
 *  For details go through Use-Case doc named 'ExhListTransactionSummaryActionService'
 */
class ExhListTransactionSummaryActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String TRANSACTION_SUMMARY_MAP = "customerRemittanceMap"
    private static String ERROR_MESSAGE = "Failed to get transaction summary details"
    private static final String ERROR_FOR_INVALID_INPUT = "Error occurred for invalid inputs"
    private static final String SORT_COLUMN_NAME = 'name'

    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    /**
     * Check necessary parameters
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap();

        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)                // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if ((!params.createdDateFrom) || (!params.createdDateTo)) {             // check required parameters
                result.put(Tools.MESSAGE, ERROR_FOR_INVALID_INPUT)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
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
     * Get list of transaction summary for grid
     * list contains such columns are customerAccount, customerName, totalTask, totalAmount(BDT), totalAmount(Local)
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()

        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            sortColumn = SORT_COLUMN_NAME
            sortOrder = ASCENDING_SORT_ORDER
            initPager(params)        // initialize params for flexGrid

            Date startDate = DateUtility.parseMaskedFromDate(params.createdDateFrom)      // get date format e.g. "dd/MM/yyyy"
            Date endDate = DateUtility.parseMaskedToDate(params.createdDateTo)
            double amount = Double.parseDouble(params.amount)
            Map transactionSummaryMap = (Map) listRemittanceSummary(startDate, endDate, amount)       // get list of transaction summary

            result.put(TRANSACTION_SUMMARY_MAP, transactionSummaryMap)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap transaction summary list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap output = new LinkedHashMap()
        Map resultMap = new LinkedHashMap()
        try {
            resultMap = (Map) obj         // cast map returned from execute method
            Map customerRemittanceMap = (Map) resultMap.get(TRANSACTION_SUMMARY_MAP)
            List remittanceList = (List) customerRemittanceMap.remittanceList
            int count = (int) customerRemittanceMap.count

            List remittances = wrapRemittanceList(remittanceList, start)        // wrap remittance(s)

            Map gridOutput = [page: pageNumber, total: count, rows: remittances]
            output = [gridOutput: gridOutput]
            return output
        } catch (Exception e) {
            log.error(e.getMessage())
            output.put(Tools.IS_ERROR, Boolean.TRUE)
            output.put(Tools.MESSAGE, ERROR_MESSAGE)
            return output
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map preResult = (Map) obj
                result.put(Tools.MESSAGE, preResult.message)
            } else {
                result.put(Tools.MESSAGE, ERROR_MESSAGE)
            }

            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of lstRemittance in grid entity
     * @param lstRemittance
     * @param start -starting index of the page
     * @return -list of wrapped remittances
     */
    private List wrapRemittanceList(List lstRemittance, int start) {
        List remittances = []
        LinkedHashMap remittance
        int counter = start + 1
        for (int i = 0; i < lstRemittance.size(); i++) {
            remittance = (LinkedHashMap) lstRemittance[i]
            GridEntity obj = new GridEntity()      // build grid entity object
            obj.id = remittance.id
            obj.cell = ["${counter}",
                    remittance.customer_account,
                    remittance.customer_name,
                    remittance.count,
                    remittance.total_local_amount ? Tools.formatAmountWithoutCurrency(remittance.total_local_amount) : 0,
                    remittance.total_foreign_amount ? Tools.formatAmountWithoutCurrency(remittance.total_foreign_amount) : 0
            ]
            remittances << obj
            counter++
        }
        return remittances
    }

    /**
     * Get list of remittance between dates
     * @param fromDate
     * @param toDate
     * @param amount
     * @return returnResult -map containing summaryList & count
     */
    private Map listRemittanceSummary(Date fromDate, Date toDate, double amount) {

        Map returnResult = new LinkedHashMap()
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
        SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
        SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

        String query = """
          SELECT customer.id, customer.code AS customer_account, (customer.name || ' ' || COALESCE(customer.surname,'')) AS customer_name, count(task.id) AS count,
                COALESCE(sum(task.amount_in_local_currency),0) AS total_local_amount,
                COALESCE(sum(task.amount_in_foreign_currency),0) AS total_foreign_amount
            FROM
                 exh_task task
                 LEFT JOIN exh_customer customer ON customer.id = task.customer_id
            WHERE
               task.created_on BETWEEN '${DateUtility.getDBDateFormatWithSecond(fromDate)}' AND '${DateUtility.getDBDateFormatWithSecond(toDate)}'
               AND task.current_status IN
                (${exhNewTaskSysEntityObject.id}, ${exhSentToBankSysEntityObject.id},
                ${exhSentToOtherBankSysEntityObject.id},
                ${exhResolvedByOtherBankSysEntityObject.id})
            GROUP BY customer.id,customer_account, customer.name, customer.surname
            HAVING SUM(task.amount_in_local_currency) >= ${amount}
            ORDER BY ${sortColumn} ${sortOrder} LIMIT ${resultPerPage} OFFSET ${start}
        """
        String count_query = """
          SELECT COUNT(*) FROM
                    (
                    SELECT customer.id FROM
                    exh_customer customer
                    LEFT JOIN exh_task task ON customer.id = task.customer_id
             WHERE
                task.created_on BETWEEN '${DateUtility.getDBDateFormatWithSecond(fromDate)}' AND '${DateUtility.getDBDateFormatWithSecond(toDate)}'
                AND task.current_status IN
                (${exhNewTaskSysEntityObject.id},
                ${exhSentToBankSysEntityObject.id},
                ${exhSentToOtherBankSysEntityObject.id},
                ${exhResolvedByOtherBankSysEntityObject.id})
                GROUP BY customer.id
                HAVING SUM(task.amount_in_local_currency) >= ${amount}) AS result
            """
        List summaryList = executeSelectSql(query)
        List count = executeSelectSql(count_query)
        returnResult = [remittanceList: summaryList, count: count[0].count]
        return returnResult
    }

}
