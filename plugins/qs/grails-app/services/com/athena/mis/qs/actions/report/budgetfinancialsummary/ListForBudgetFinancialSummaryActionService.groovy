package com.athena.mis.qs.actions.report.budgetfinancialsummary

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/*
 * List of Budget Financial Summary and show in UI
 * For details go through Use-Case doc named 'ListForBudgetFinancialSummaryActionService'
 */
class ListForBudgetFinancialSummaryActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate Budget Financial Summary report"
    private static final String FINANCIAL_SUMMARY_LIST = "financialSummaryList"
    private static final String BUDGET_NOT_FOUND = "Budget not found within given dates"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String PROJECT_ID = "projectId"
    private static final String COUNT = "count"
    private static final String IS_GOVT_QS = "isGovtQs"

    /**
     * Check input fields from UI
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return Map containing isError(true/false)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.fromDate || !params.toDate) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
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

    private static final String COUNT_QUERY = """
                SELECT count(budget.id) count
                FROM budg_budget budget
                WHERE budget.project_id =:projectId
                AND budget.billable = true
                AND (budget.created_on BETWEEN :fromDate AND :toDate)
                """

    /**
     * Get list of budget financial summary and wrap budget financial summary list in grid entity
     * 1. Get fromDate, toDate, projectId, isGovtQs from parameterMap
     * 2. Get budget financial summary  list and it's count from raw sql query using executeSelectSql method
     * 3. if count<=0 then UI show the message "Budget not found within given dates"
     * 4. Put fromDate and toDate to the result map using getDateForUI method of DateUtility
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return contains all necessary things for buildSuccessResultForUI and isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            LinkedHashMap preResult = (LinkedHashMap) obj
            if (!parameterMap.rp) {
                parameterMap.rp = 20
                parameterMap.page = 1
            }
            this.initPager(parameterMap)

            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            boolean isGovtQs = Boolean.parseBoolean(parameterMap.isGovtQs)
            String query = """SELECT  bdg.id,
                    bdg.budget_item, bdg.details AS budget_details,
                    to_char(bdg.budget_quantity,'${Tools.DB_QUANTITY_FORMAT}') AS budget_quantity, system_entity.key AS unit_name,
                    to_char((bdg.contract_rate*COALESCE(SUM(qsm.quantity),0)),'${Tools.DB_CURRENCY_FORMAT}') AS gross_receivables,
                    to_char(((bdg.contract_rate * (1-${Tools.VAT}-${Tools.AIT})) * COALESCE(SUM(qsm.quantity),0)),'${Tools.DB_CURRENCY_FORMAT}') AS net_receivables,
                    to_char((((COALESCE(SUM(qsm.quantity),0) / bdg.budget_quantity)*100) *
                    ((bdg.contract_rate * bdg.budget_quantity)/(SELECT coalesce(SUM(bdg1.contract_rate),1) FROM budg_budget bdg1
                    WHERE bdg1.project_id = :projectId AND bdg1.billable = true)))/100,'${Tools.DB_QUANTITY_FORMAT}') AS wt_of_contract_completed
                    FROM budg_budget bdg
                    LEFT JOIN qs_measurement qsm ON bdg.id = qsm.budget_id AND qsm.is_govt_qs = :isGovtQs
                    LEFT JOIN system_entity ON system_entity.id = bdg.unit_id
                    WHERE bdg.project_id = :projectId
                    AND bdg.billable = true
                    AND (bdg.created_on BETWEEN :fromDate AND :toDate)
                    GROUP BY bdg.id,bdg.budget_item,bdg.details,bdg.budget_quantity, system_entity.key,bdg.contract_rate
                    ORDER BY bdg.budget_item ASC  LIMIT :resultPerPage  OFFSET :start
                    """
            Map queryParams = [
                    projectId: projectId,
                    fromDate: DateUtility.getSqlDateWithSeconds(fromDate),
                    toDate: DateUtility.getSqlDateWithSeconds(toDate),
                    isGovtQs: isGovtQs,
                    resultPerPage: resultPerPage,
                    start: start
            ]
            List<GroovyRowResult> financialSummaryList = executeSelectSql(query, queryParams)
            List<GroovyRowResult> budgetCount = executeSelectSql(COUNT_QUERY, queryParams)
            int count = (int) budgetCount[0].count
            if (count <= 0) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            List lstFinancialSummary = wrapFinancialSummaryListInGridEntity(financialSummaryList, this.start)
            result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
            result.put(TO_DATE, DateUtility.getDateForUI(toDate))
            result.put(PROJECT_ID, projectId)
            result.put(IS_GOVT_QS, isGovtQs)
            result.put(FINANCIAL_SUMMARY_LIST, lstFinancialSummary)
            result.put(COUNT, count)
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
     * Give Budget Financial Summary list  as grid output in UI
     * 1. Get Budget Financial Summary list, count, toDate, fromDate, projectId, isGovtQs from execute method and put into result map for showing in UI
     * @param obj - map returned from execute method
     * @return - a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List financialSummaryListWrap = (List) executeResult.get(FINANCIAL_SUMMARY_LIST)
            int count = (int) executeResult.get(COUNT)
            Map gridOutput = [page: pageNumber, total: count, rows: financialSummaryListWrap]
            result.put(FINANCIAL_SUMMARY_LIST, gridOutput)
            result.put(FROM_DATE, executeResult.get(FROM_DATE))
            result.put(TO_DATE, executeResult.get(TO_DATE))
            result.put(PROJECT_ID, executeResult.get(PROJECT_ID))
            result.put(IS_GOVT_QS, executeResult.get(IS_GOVT_QS))
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
     * Wrap List of Financial Summary in grid entity
     * @param financialSummaryList - financial Summary List from execute method
     * @param start - starting index of the page
     * @return - list of wrapped financial Summary
     */
    private List wrapFinancialSummaryListInGridEntity(List<GroovyRowResult> financialSummaryList, int start) {
        List lstFinancialSummary = []
        int counter = start + 1
        GroovyRowResult financialSummary
        GridEntity obj

        for (int i = 0; i < financialSummaryList.size(); i++) {
            financialSummary = financialSummaryList[i]
            obj = new GridEntity()
            obj.id = financialSummary.id
            obj.cell = [
                    counter,
                    financialSummary.budget_item,
                    financialSummary.budget_details,
                    financialSummary.budget_quantity,
                    financialSummary.unit_name,
                    financialSummary.gross_receivables,
                    financialSummary.net_receivables,
                    financialSummary.wt_of_contract_completed
            ]
            lstFinancialSummary << obj
            counter++
        }
        return lstFinancialSummary
    }
}
