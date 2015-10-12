package com.athena.mis.qs.actions.report.budgetcontractdetails

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
 * List of Budget Contract Details and show in UI
 * For details go through Use-Case doc named 'ListForBudgetContractDetailsActionService'
 */
class ListForBudgetContractDetailsActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate Budget Contract Details report"
    private static final String CONTRACT_DETAILS_LIST = "contractDetailsList"
    private static final String BUDGET_NOT_FOUND = "Budget not found within given dates"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String PROJECT_ID = "projectId"
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
                SELECT count(budget.id) count
                FROM budg_budget budget
                WHERE budget.project_id =:projectId
                AND budget.billable = true
                AND (budget.created_on BETWEEN :fromDate AND :toDate)
                """

    /**
     * Get list of Budget Contract Details and wrap Budget Contract Details list in grid entity
     * 1. Get fromDate, toDate, projectId from parameterMap
     * 2. Get budget contract details list and it's count from raw sql query using executeSelectSql method
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
            if (!parameterMap.rp) {
                parameterMap.rp = 20
                parameterMap.page = 1
            }
            initPager(parameterMap)

            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            String queryStr = """SELECT  bdg.id,
                            bdg.budget_item, bdg.details AS budget_details,
                            to_char(bdg.budget_quantity,'${Tools.DB_CURRENCY_FORMAT}') AS budget_quantity, system_entity.key AS unit_name,
                            to_char(bdg.contract_rate,'${Tools.DB_CURRENCY_FORMAT}') AS contract_rate,
                            to_char((bdg.contract_rate * (1-${Tools.VAT}-${Tools.AIT})),'${Tools.DB_CURRENCY_FORMAT}') AS net_rate_of_item,
                            to_char((bdg.contract_rate * bdg.budget_quantity),'${Tools.DB_CURRENCY_FORMAT}') AS gross_value_of_item,
                            to_char((bdg.budget_quantity*(bdg.contract_rate * (1-${Tools.VAT}-${Tools.AIT}))),'${Tools.DB_CURRENCY_FORMAT}') AS net_value_of_item,
                            to_char(((bdg.contract_rate * bdg.budget_quantity)/(SELECT coalesce(SUM(bdg1.contract_rate),1) FROM budg_budget bdg1
                            WHERE bdg1.project_id = :projectId AND bdg1.billable = true)),'${Tools.DB_QUANTITY_FORMAT}') AS wt_of_item
                            FROM budg_budget bdg
                            LEFT JOIN system_entity ON system_entity.id = bdg.unit_id
                            WHERE bdg.project_id = :projectId
                            AND bdg.billable = true
                            AND (bdg.created_on BETWEEN :fromDate AND :toDate)
                            ORDER BY bdg.budget_item ASC  LIMIT :resultPerPage OFFSET :start
                            """
            Map queryParams = [
                    projectId: projectId,
                    fromDate: DateUtility.getSqlDateWithSeconds(fromDate),
                    toDate: DateUtility.getSqlDateWithSeconds(toDate),
                    resultPerPage: resultPerPage,
                    start: start
            ]

            List<GroovyRowResult> contractDetailsList = executeSelectSql(queryStr, queryParams)
            List<GroovyRowResult> contractDetailsListCount = executeSelectSql(COUNT_QUERY, queryParams)
            int count = (int) contractDetailsListCount[0].count
            if (count <= 0) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            List lstContractDetails = wrapContractDetailsListInGridEntity(contractDetailsList, start)   // get list of budget contract details
            result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
            result.put(TO_DATE, DateUtility.getDateForUI(toDate))
            result.put(PROJECT_ID, projectId)
            result.put(CONTRACT_DETAILS_LIST, lstContractDetails)
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
     * Give Budget Contract Details list  as grid output in UI
     * 1. Get Budget Contract Details list, count, toDate, fromDate, projectId from execute method and put into result map for showing in UI
     * @param obj - map returned from execute method
     * @return - a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List contractDetailsListWrap = (List) executeResult.get(CONTRACT_DETAILS_LIST)
            int count = (int) executeResult.get(COUNT)
            Map gridOutput = [page: pageNumber, total: count, rows: contractDetailsListWrap]
            result.put(CONTRACT_DETAILS_LIST, gridOutput)
            result.put(FROM_DATE, executeResult.get(FROM_DATE))
            result.put(TO_DATE, executeResult.get(TO_DATE))
            result.put(PROJECT_ID, executeResult.get(PROJECT_ID))
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
     * Wrap List of Budget Contract Details in grid entity
     * @param contractDetailsList - Budget Contract Details list from execute method
     * @param start - starting index of the page
     * @return - list of wrapped Budget Contract Details summary
     */
    private List wrapContractDetailsListInGridEntity(List<GroovyRowResult> contractDetailsList, int start) {
        List lstContractDetails = []
        int counter = start + 1
        GroovyRowResult contractDetails
        GridEntity obj

        for (int i = 0; i < contractDetailsList.size(); i++) {
            contractDetails = contractDetailsList[i]
            obj = new GridEntity()
            obj.id = contractDetails.id
            lstContractDetails << obj
            obj.cell = [counter,
                    contractDetails.budget_item,
                    contractDetails.budget_details,
                    contractDetails.budget_quantity,
                    contractDetails.unit_name,
                    contractDetails.contract_rate,
                    contractDetails.net_rate_of_item,
                    contractDetails.gross_value_of_item,
                    contractDetails.net_value_of_item,
                    contractDetails.wt_of_item
            ]
            counter++
        }
        return lstContractDetails
    }
}
