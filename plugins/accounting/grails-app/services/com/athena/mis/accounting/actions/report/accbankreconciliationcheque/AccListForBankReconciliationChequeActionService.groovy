package com.athena.mis.accounting.actions.report.accbankreconciliationcheque

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Fetch list of bank reconciliation cheque  when clear button of grid is fired.
 * For details go through Use-Case doc named 'AccListForBankReconciliationChequeActionService'
 */
class AccListForBankReconciliationChequeActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String BANK_RECONCILIATION_CHEQUE_NOT_FOUND = "Bank Reconciliation Cheque not found."
    private static final String FAILURE_MSG = "Fail to generate Bank Reconciliation Cheque"
    private static final String BANK_RECONCILIATION_CHEQUE_LIST = "bankReconciliationChequeList"
    private static final String TO_DATE_NOT_FOUND = "To Date not found"
    private static final String TO_DATE = "toDate"

    @Autowired
    AccSessionUtil accSessionUtil
    /**
     * Check pre-conditions for input data
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return - a map containing isError msg(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.toDate) {
                result.put(Tools.MESSAGE, TO_DATE_NOT_FOUND)
                return result
            }

            Date toDate = DateUtility.parseMaskedDate(parameterMap.toDate.toString())
            result.put(TO_DATE, toDate)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
     * Get bank reconciliation cheque  list
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return - a map containing bank reconciliation cheque  for grid and error msg(True/False)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if(!parameterMap.rp){
                parameterMap.rp = 20
                parameterMap.page = 1
            }
            initPager(parameterMap)
            Date toDate = (Date) receivedResult.get(TO_DATE)
            Map serviceReturn = getBankReconciliationChequeList(toDate)
            List<GroovyRowResult> bankReconciliationChequeList = serviceReturn.bankReconciliationChequeList
            int count = serviceReturn.count
            if (count <= 0) {
                result.put(Tools.MESSAGE, BANK_RECONCILIATION_CHEQUE_NOT_FOUND)
                return result
            }

            List wrapBankReconciliationChequeList = wrapBankReconciliationChequeListInGridEntityList(bankReconciliationChequeList, start)
            result.put(BANK_RECONCILIATION_CHEQUE_LIST, wrapBankReconciliationChequeList)
            result.put(Tools.COUNT, count)
            result.put(TO_DATE, DateUtility.getDateForUI(toDate))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
    /**
     * Get bank reconciliation cheque list
     * @param obj -received parameters from execute method
     * @return - a map containing wrapped bank reconciliation cheque for grid and error msg(True/False)
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            List<GroovyRowResult> lstBankReconciliationCheque = (List<GroovyRowResult>) receivedResult.get(BANK_RECONCILIATION_CHEQUE_LIST)
            int countBankReconciliationCheque = (int) receivedResult.get(Tools.COUNT)
            Map bankReconciliationGrid = [page: pageNumber, total: countBankReconciliationCheque, rows: lstBankReconciliationCheque]
            result.put(BANK_RECONCILIATION_CHEQUE_LIST, bankReconciliationGrid)
            result.put(TO_DATE, receivedResult.get(TO_DATE))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.message) {
                result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
    /**
     * Wrap bank reconciliation cheque list for grid
     * @param trialBalanceList -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    private List wrapBankReconciliationChequeListInGridEntityList(List<GroovyRowResult> bankReconciliationChequeList, int start) {
        List newBankReconciliationChequeList = [] as List
        int counter = start + 1
        GroovyRowResult bankReconciliation
        GridEntity obj
        for (int i = 0; i < bankReconciliationChequeList.size(); i++) {
            bankReconciliation = bankReconciliationChequeList[i]
            obj = new GridEntity()
            obj.id = counter
            obj.cell = [
                    counter,
                    bankReconciliation.source,
                    bankReconciliation.cheque_no,
                    bankReconciliation.cheque_date,
                    bankReconciliation.amount
            ]
            newBankReconciliationChequeList << obj
            counter++
        }
        return newBankReconciliationChequeList
    }
    /**
     * Get bank reconciliation cheque list
     * @param toDate - current date received from params
     * @return - a list of bank reconciliation cheque
     */
    private Map getBankReconciliationChequeList(Date toDate) {

        String queryStr = """
            SELECT source,cheque_no, amount,cheque_date FROM
            ((SELECT 'COMPANY' AS cheque_from, ('Voucher: ' || av.trace_no) AS source, av.cheque_no,
            TO_CHAR(av.amount,'${Tools.DB_CURRENCY_FORMAT}') AS amount,
            TO_CHAR(av.cheque_date,'dd-MON-yyyy') AS  cheque_date
            FROM acc_voucher av
            WHERE av.cheque_no IS NOT NULL
            AND av.company_id = :companyId
            AND av.cheque_date <= :toDateStr
            AND av.cheque_no NOT IN
                (
                SELECT cheque_no FROM acc_voucher WHERE cheque_no IS NOT NULL AND company_id = :companyId AND cheque_date <= :toDateStr
                INTERSECT
                SELECT cheque_no FROM acc_bank_statement WHERE cheque_no IS NOT NULL AND company_id =:companyId AND transaction_date <= :toDateStr
                )
            )
            UNION ALL
            (SELECT 'BANK' AS cheque_from, (coa.description || ': ' || coa.code) AS source,  abs.cheque_no,
            TO_CHAR(abs.amount,'${Tools.DB_CURRENCY_FORMAT}') AS amount,
            TO_CHAR(abs.transaction_date,'dd-MON-yyyy') AS cheque_date
            FROM acc_bank_statement abs
            LEFT JOIN acc_chart_of_account coa ON coa.id = abs.bank_acc_id
            WHERE abs.cheque_no IS NOT NULL
            AND abs.company_id = :companyId
            AND abs.transaction_date <= :toDateStr
            AND abs.cheque_no NOT IN
                (
                    SELECT cheque_no FROM acc_voucher WHERE cheque_no IS NOT NULL AND company_id = :companyId AND cheque_date <= :toDateStr
                    INTERSECT
                    SELECT cheque_no FROM acc_bank_statement WHERE cheque_no IS NOT NULL AND company_id = :companyId AND transaction_date <= :toDateStr
                )
            )
            ) bank_reconciliation_cheque

            ORDER BY cheque_from desc, source asc
            LIMIT :resultPerPage  OFFSET :start
        """

        String queryCount = """
            SELECT count(cheque_no) FROM
            ((SELECT av.cheque_no
            FROM acc_voucher av
            WHERE av.cheque_no IS NOT NULL
            AND av.company_id = :companyId
            AND av.cheque_date <= :toDateStr
            AND av.cheque_no NOT IN
            (
            SELECT cheque_no FROM acc_voucher WHERE cheque_no IS NOT NULL AND company_id = :companyId AND cheque_date <= :toDateStr
            INTERSECT
            SELECT cheque_no FROM acc_bank_statement WHERE cheque_no IS NOT NULL AND company_id = :companyId AND transaction_date <= :toDateStr
            ))
            UNION ALL
            (SELECT abs.cheque_no
            FROM acc_bank_statement abs
            WHERE abs.cheque_no IS NOT NULL
            AND abs.company_id = :companyId
            AND abs.transaction_date <= :toDateStr
            AND abs.cheque_no NOT IN
            (
            SELECT cheque_no FROM acc_voucher WHERE cheque_no IS NOT NULL AND company_id = :companyId AND cheque_date <= :toDateStr
            INTERSECT
            SELECT cheque_no FROM acc_bank_statement WHERE cheque_no IS NOT NULL AND company_id = :companyId AND transaction_date <= :toDateStr
            ))
            ) bank_reconciliation_cheque
        """

        Map queryParams = [
                companyId: accSessionUtil.appSessionUtil.getAppUser().companyId,
                toDateStr: DateUtility.getSqlDate(toDate),
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> bankReconciliationChequeList = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> bankReconciliationChequeCount = executeSelectSql(queryCount, queryParams)

        return [bankReconciliationChequeList: bankReconciliationChequeList, count: bankReconciliationChequeCount[0].count]
    }
}