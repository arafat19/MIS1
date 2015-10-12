package com.athena.mis.qs.actions.report.combinedqsmeasurement

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * List of Combined QS Measurement for grid show
 * For details go through Use-Case doc named 'ListForCombinedQSMActionService'
 */
class ListForCombinedQSMActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate combined qs measurement report."
    private static final String COMBINED_QS_MEASUREMENT_LIST = "combinedQsMeasurementList"
    private static final String BUDGET_NOT_FOUND = "Budget not found within given dates."
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String PROJECT_ID = "projectId"
    private static final String COUNT = "count"
    /**
     * Check input validation
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - isError(True/False)
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
    /**
     * 1. get qsm list by project in a date range
     * 2. wrap qsm list for grid show
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - wrapped qsm list & date range and project id
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

            Map qsCombinedQsMeasurementReturn = listForCombinedQsMeasurement(projectId, fromDate, toDate)

            List<GroovyRowResult> combinedQsMeasurementList = (List<GroovyRowResult>) qsCombinedQsMeasurementReturn.combinedQsMeasurementList
            int totalCount = (int) qsCombinedQsMeasurementReturn.count
            if (totalCount <= 0) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            List wrapCombinedQsMeasurementList = wrapCombinedQsMeasurementListInGridEntity(combinedQsMeasurementList, start)
            result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
            result.put(TO_DATE, DateUtility.getDateForUI(toDate))
            result.put(PROJECT_ID, projectId)
            result.put(COMBINED_QS_MEASUREMENT_LIST, wrapCombinedQsMeasurementList)
            result.put(COUNT, totalCount)
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
     * Get qsm list for grid show, & date range and project id for hidden field(use to download report)
     * @param obj - object receive from execute method
     * @return - a map containing combined qs measurement list for grid & date range and project id
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List combinedQsMeasurementListWrap = (List) executeResult.get(COMBINED_QS_MEASUREMENT_LIST)
            int count = (int) executeResult.get(COUNT)
            Map gridOutput = [page: pageNumber, total: count, rows: combinedQsMeasurementListWrap]
            result.put(COMBINED_QS_MEASUREMENT_LIST, gridOutput)
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
     *
     * @param combinedQsMeasurementList - qsm list
     * @param start - starting point of index
     * @return - lstBudgetWiseQsList
     */
    private List wrapCombinedQsMeasurementListInGridEntity(List<GroovyRowResult> combinedQsMeasurementList, int start) {
        List lstBudgetWiseQsList = []
        int counter = start + 1
        GroovyRowResult eachRow
        GridEntity obj

        for (int i = 0; i < combinedQsMeasurementList.size(); i++) {
            eachRow = combinedQsMeasurementList[i]
            obj = new GridEntity()
            obj.id = eachRow.id
            lstBudgetWiseQsList << obj
            obj.cell = [counter,
                    eachRow.budget_item,
                    eachRow.budget_quantity_unit,
                    eachRow.work_completed_intern,
                    eachRow.work_completed_gov,
                    eachRow.work_achieved_in_percent_intern,
                    eachRow.work_achieved_in_percent_gov,
                    eachRow.rate_per_unit,
                    eachRow.work_completed_intern > 0 ? eachRow.cost_per_unit_intern : Tools.STR_ZERO_DECIMAL,
                    eachRow.work_certified_amount_intern,
                    eachRow.work_certified_amount_gov,
                    eachRow.details
            ]
            counter++
        }
        return lstBudgetWiseQsList
    }

    private static final String COUNT_QUERY = """
                    SELECT count(id) count
                    FROM budg_budget
                    WHERE project_id =:projectId
                    AND billable = true
                    AND (created_on BETWEEN :fromDate AND :toDate)
                    """
    /**
     *
     * @param projectId
     * @param fromDate
     * @param toDate
     * @return
     */
    private Map listForCombinedQsMeasurement(long projectId, Date fromDate, Date toDate) {
        long invTransactionTypeConsumptionId = inventoryImplService.getInvTransactionTypeIdConsumption()
        String queryStr = """SELECT
                            budget.id,
                            budget.budget_item AS budget_item,
                            budget.details AS details,
                            TO_CHAR(budget.budget_quantity,'${Tools.DB_QUANTITY_FORMAT}') || ' ' || un.key AS budget_quantity_unit,
                        SUM(
                            CASE
                            WHEN qsm.is_govt_qs = false
                            THEN qsm.quantity
                            ELSE 0
                            END
                            ) AS work_completed_intern,
                            SUM(
                            CASE
                            WHEN qsm.is_govt_qs = true
                            THEN qsm.quantity
                            ELSE 0
                            END
                            ) AS work_completed_gov,
                    (to_char(SUM(
                            CASE
                            WHEN qsm.is_govt_qs = false
                            THEN qsm.quantity*budget.contract_rate
                            ELSE 0
                            END
                    ),'${Tools.DB_CURRENCY_FORMAT}' )) AS work_certified_amount_intern,
                    (to_char(SUM(
                            CASE
                            WHEN qsm.is_govt_qs = true
                            THEN qsm.quantity*budget.contract_rate
                            ELSE 0
                            END
                    ),'${Tools.DB_CURRENCY_FORMAT}' ))AS work_certified_amount_gov,
                            (
                                to_char(
                                        (SUM(
                            CASE
                            WHEN qsm.is_govt_qs = false
                            THEN qsm.quantity
                            ELSE 0
                            END
                            )/budget.budget_quantity)*100,'${Tools.DB_QUANTITY_FORMAT}'
                                    )
                            ) AS work_achieved_in_percent_intern,

                            (
                                to_char(
                                        (SUM(
                            CASE
                            WHEN qsm.is_govt_qs = true
                            THEN qsm.quantity
                            ELSE 0
                            END
                            )/budget.budget_quantity)*100,'${Tools.DB_QUANTITY_FORMAT}'
                                    )
                            ) as work_achieved_in_percent_gov,

                            to_char(budget.contract_rate,'${Tools.DB_CURRENCY_FORMAT}') AS rate_per_unit,
                            to_char(
                            (SELECT COALESCE(SUM(iitd.actual_quantity*iitd.rate),0) FROM inv_inventory_transaction_details iitd
                            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
                            WHERE
                            iit.transaction_type_id = ${invTransactionTypeConsumptionId} AND
                            iitd.approved_by >0 AND iitd.is_current = true AND iit.budget_id = budget.id AND
                            iitd.transaction_date BETWEEN :fromDate AND :toDate)/
                            (
                                CASE
                                    WHEN SUM(
                                        CASE
                                            WHEN qsm.is_govt_qs = false
                                            THEN qsm.quantity
                                            ELSE 0
                                        END
                                        ) >0
                                    THEN SUM(
                                        CASE
                                            WHEN qsm.is_govt_qs = false
                                            THEN qsm.quantity
                                            ELSE 0
                                        END
                                        )
                                    ELSE 1
                                END
                                )
                        ,'${Tools.DB_CURRENCY_FORMAT}') AS cost_per_unit_intern,
                        to_char(
                            (SELECT COALESCE(SUM(iitd.actual_quantity*iitd.rate),0) FROM inv_inventory_transaction_details iitd
                            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
                            WHERE
                            iit.transaction_type_id = ${invTransactionTypeConsumptionId} AND
                            iitd.approved_by >0 AND iitd.is_current = true AND iit.budget_id = budget.id AND
                            iitd.transaction_date BETWEEN :fromDate AND :toDate)/
                            (
                                CASE
                                    WHEN SUM(
                                        CASE
                                            WHEN qsm.is_govt_qs = true
                                            THEN qsm.quantity
                                            ELSE 0
                                        END
                                        ) >0
                                    THEN SUM(
                                        CASE
                                            WHEN qsm.is_govt_qs = true
                                            THEN qsm.quantity
                                            ELSE 0
                                        END
                                        )
                                    ELSE 1
                                END
                                )
                        ,'${Tools.DB_CURRENCY_FORMAT}') AS cost_per_unit_gov ,
                        budget.details

                    FROM budg_budget budget
                    LEFT JOIN system_entity un ON un.id = budget.unit_id
                    LEFT JOIN qs_measurement qsm ON budget.id = qsm.budget_id
                        AND (qsm.created_on BETWEEN :fromDate AND :toDate)
                    WHERE budget.project_id =:projectId
                    AND budget.billable = true
                    AND (budget.created_on BETWEEN :fromDate AND :toDate)
                    GROUP BY budget.id,budget.budget_item,budget.details,budget.budget_quantity,un.key,budget.contract_rate
                    ORDER BY budget.budget_item ASC  LIMIT :resultPerPage  OFFSET :start
                    """

        Map queryParams = [
                projectId: projectId,
                fromDate: DateUtility.getSqlDateWithSeconds(fromDate),
                toDate: DateUtility.getSqlDateWithSeconds(toDate),
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> combinedQsMeasurementListCount = executeSelectSql(COUNT_QUERY, queryParams)
        int total = (int) combinedQsMeasurementListCount[0].count

        return [combinedQsMeasurementList: result, count: total]
    }
}
