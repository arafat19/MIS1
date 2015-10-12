package com.athena.mis.qs.actions.report.budgetwiseqs

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
 * List of Budget Wise QS and show in UI
 * For details go through Use-Case doc named 'ListForBudgetWiseQsActionService'
 */
class ListForBudgetWiseQsActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate budget wise qs report."
    private static final String BUDGET_WISE_QS_LIST = "budgetWiseQsList"
    private static final String BUDGET_NOT_FOUND = "Budget not found within given dates"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String PROJECT_ID = "projectId"
    private static final String IS_GOVT_QS = "isGovtQs"
    private static final String COUNT = "count"

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
                    SELECT count(id) count
                    FROM budg_budget
                    WHERE project_id =:projectId
                    AND billable = true
                    AND (created_on BETWEEN :fromDate AND :toDate)
                    """

    /**
     * Get list of budget wise qs and wrap budget wise qs list in grid entity
     * 1. Get fromDate, toDate, projectId, isGovtQs from parameterMap
     * 2. Get budget Wise Qs List and it's count from raw sql query using executeSelectSql method
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
            initPager(parameterMap)

            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            boolean isGovtQs = Boolean.parseBoolean(parameterMap.isGovtQs)
            String queryStr = """SELECT
                    budget.id,
                    budget.budget_item AS budget_item,
                    budget.details AS details,
                    to_char(budget.budget_quantity,'${Tools.DB_QUANTITY_FORMAT}') || ' ' || un.key AS budget_quantity_unit,

                    to_char(SUM(qsm.quantity),'${Tools.DB_QUANTITY_FORMAT}') AS work_completed,
                    to_char(budget.budget_quantity-COALESCE(SUM(qsm.quantity),0),'${Tools.DB_QUANTITY_FORMAT}') as work_remaining,
                    (
                        to_char(
                                (COALESCE(SUM(qsm.quantity),0)/budget.budget_quantity)*100,'${Tools.DB_QUANTITY_FORMAT}'
                            )
                    ) as work_achieved_in_percent,
                    (
                    to_char(
                        trunc(
                            (
                                (budget.budget_quantity-COALESCE(SUM(qsm.quantity),0))/budget.budget_quantity)*100,2
                            ),'${Tools.DB_QUANTITY_FORMAT}'
                        )
                    ) as work_remaining_in_percent,
                    to_char(COALESCE((budget.contract_rate*SUM(qsm.quantity)),0),'${Tools.DB_CURRENCY_FORMAT}') AS gross_receivables,
                    to_char(COALESCE(((budget.contract_rate * (1-${Tools.VAT}-${Tools.AIT})) * SUM(qsm.quantity)),0),'${Tools.DB_CURRENCY_FORMAT}') AS net_receivables
                    FROM budg_budget budget
                    LEFT JOIN system_entity un ON un.id = budget.unit_id
                    LEFT JOIN qs_measurement qsm ON budget.id = qsm.budget_id
                        AND (qsm.created_on BETWEEN :fromDate AND :toDate)
                        AND qsm.is_govt_qs = :isGovtQs
                    WHERE budget.project_id =:projectId
                    AND budget.billable = true
                    AND (budget.created_on BETWEEN :fromDate AND :toDate)
                    GROUP BY budget.id,budget.budget_item,budget.details,budget.budget_quantity,un.key,budget.contract_rate
                    ORDER BY budget.budget_item ASC  LIMIT :resultPerPage  OFFSET :start
                    """

            Map queryParams = [
                    projectId: projectId,
                    isGovtQs: isGovtQs,
                    fromDate: DateUtility.getSqlDateWithSeconds(fromDate),
                    toDate: DateUtility.getSqlDateWithSeconds(toDate),
                    resultPerPage: resultPerPage,
                    start: start
            ]
            List<GroovyRowResult> budgetWiseQsList = executeSelectSql(queryStr, queryParams)
            List<GroovyRowResult> budgetWiseQsListCount = executeSelectSql(COUNT_QUERY, queryParams)
            int count = (int) budgetWiseQsListCount[0].count
            if (count <= 0) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            List wrapBudgetWiseQsList = wrapBudgetWiseQsListInGridEntity(budgetWiseQsList, start)   // get list of budget wise qs
            result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
            result.put(TO_DATE, DateUtility.getDateForUI(toDate))
            result.put(PROJECT_ID, projectId)
            result.put(IS_GOVT_QS, isGovtQs)
            result.put(BUDGET_WISE_QS_LIST, wrapBudgetWiseQsList)
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
     * Give budget wise QS list  as grid output in UI
     * 1. Get BUDGET WISE QS LIST, count, toDate, fromDate, projectId, isGovtQs from execute method and put into result map for showing in UI
     * @param obj - map returned from execute method
     * @return - a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List budgetWiseQsListWrap = (List) executeResult.get(BUDGET_WISE_QS_LIST)
            int count = (int) executeResult.get(COUNT)
            Map gridOutput = [page: pageNumber, total: count, rows: budgetWiseQsListWrap]
            result.put(BUDGET_WISE_QS_LIST, gridOutput)
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
     * Wrap Budget wise QS  LIST in grid entity
     * @param budgetWiseQsList - budget Wise Qs List  from execute method
     * @param start - starting index of the page
     * @return - list of wrapped budget Wise Qs List
     */
    private List wrapBudgetWiseQsListInGridEntity(List<GroovyRowResult> budgetWiseQsList, int start) {
        List lstBudgetWiseQsList = []
        int counter = start + 1
        GroovyRowResult budgetWiseQs
        GridEntity obj

        for (int i = 0; i < budgetWiseQsList.size(); i++) {
            budgetWiseQs = budgetWiseQsList[i]
            obj = new GridEntity()
            obj.id = budgetWiseQs.id
            lstBudgetWiseQsList << obj
            obj.cell = [counter,
                    budgetWiseQs.budget_item,
                    budgetWiseQs.budget_quantity_unit,
                    budgetWiseQs.work_completed,
                    budgetWiseQs.work_remaining,
                    budgetWiseQs.work_achieved_in_percent,
                    budgetWiseQs.work_remaining_in_percent,
                    budgetWiseQs.gross_receivables,
                    budgetWiseQs.net_receivables,
                    budgetWiseQs.details
            ]
            counter++
        }
        return lstBudgetWiseQsList
    }
}
