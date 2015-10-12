package com.athena.mis.qs.actions.report.qsmeasurement

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
 * List of QS Measurement and show in UI
 * For details go through Use-Case doc named 'ListForQsMeasurementActionService'
 */
class ListForQsMeasurementActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate qsMeasurement report"
    private static final String QS_MEASUREMENT_LIST = "qsMeasurementList"
    private static final String BUDGET_NOT_FOUND = "Quantity Survey not found within given dates"
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.projectId || !params.fromDate || !params.toDate) {
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
                WHERE (SELECT SUM(qsm.quantity) FROM  qs_measurement qsm
                       WHERE qsm.project_id =:projectId
                AND (qsm.qs_measurement_date BETWEEN :fromDate AND :toDate)
                AND qsm.is_govt_qs =:isGovtQs
                AND qsm.budget_id = budget.id
                )>0
                AND budget.billable = true
                """

    /**
     * Get list of QS Measurement and wrap qs measurement list in grid entity
     * 1. Get fromDate, toDate, projectId, isGovtQs from parameterMap
     * 2. Get qs measurement list and it's count from raw sql query using executeSelectSql method
     * 3. if count<=0 then UI show the message "Quantity Survey not found within given dates"
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

            String queryStr = """ SELECT
                budget.id,
                budget.budget_item AS budget_item,
                budget.details AS details,
                to_char(budget.budget_quantity,'${Tools.DB_QUANTITY_FORMAT}')||' '||un.key AS budget_quantity,
                to_char(SUM(qsm.quantity),'${Tools.DB_QUANTITY_FORMAT}') as work_completed,
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
                ) as work_remaining_in_percent
                FROM budg_budget budget
                LEFT JOIN system_entity un ON un.id = budget.unit_id
                LEFT JOIN qs_measurement qsm ON budget.id = qsm.budget_id
                WHERE qsm.project_id =:projectId
                AND (qsm.qs_measurement_date BETWEEN :fromDate AND :toDate)
                AND qsm.is_govt_qs =:isGovtQs
                AND budget.billable = true
                GROUP BY budget.id,budget.budget_item,budget.details,budget.budget_quantity,un.key
                ORDER BY budget_item ASC  LIMIT :resultPerPage  OFFSET :start
                """

            Map queryParams = [
                    projectId: projectId,
                    isGovtQs: isGovtQs,
                    fromDate: DateUtility.getSqlDateWithSeconds(fromDate),
                    toDate: DateUtility.getSqlDateWithSeconds(toDate),
                    resultPerPage: resultPerPage,
                    start: start
            ]

            List<GroovyRowResult> qsMeasurementList = executeSelectSql(queryStr, queryParams)
            List<GroovyRowResult> qsMeasurementListCount = executeSelectSql(COUNT_QUERY, queryParams)
            int count = (int) qsMeasurementListCount[0].count
            if (count <= 0) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            List lstQSMeasurement = wrapQsMeasurementListInGridEntity(qsMeasurementList, start) // get list of qs measurement
            result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
            result.put(TO_DATE, DateUtility.getDateForUI(toDate))
            result.put(PROJECT_ID, projectId)
            result.put(IS_GOVT_QS, isGovtQs)
            result.put(QS_MEASUREMENT_LIST, lstQSMeasurement)
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
     * Give QS MEASUREMENT list  as grid output in UI
     * 1. Get QS MEASUREMENT LIST, count, toDate, fromDate, projectId, isGovtQs from execute method and put into result map for showing in UI
     * @param obj - map returned from execute method
     * @return - a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List qsMeasurementListWrap = (List) executeResult.get(QS_MEASUREMENT_LIST)
            int count = (int) executeResult.get(COUNT)
            Map gridOutput = [page: pageNumber, total: count, rows: qsMeasurementListWrap]
            result.put(QS_MEASUREMENT_LIST, gridOutput)
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
     * Wrap QS MEASUREMENT LIST in grid entity
     * @param qsMeasurementList - qs measurement list from execute method
     * @param start - starting index of the page
     * @return - list of wrapped budget wise po summary
     */
    private List wrapQsMeasurementListInGridEntity(List<GroovyRowResult> qsMeasurementList, int start) {
        List lstQsMeasurementList = []
        int counter = start + 1
        GroovyRowResult qsMeasurement
        GridEntity obj

        for (int i = 0; i < qsMeasurementList.size(); i++) {
            qsMeasurement = qsMeasurementList[i]
            obj = new GridEntity()
            obj.id = qsMeasurement.id
            lstQsMeasurementList << obj
            obj.cell = [counter,
                    qsMeasurement.budget_item,
                    qsMeasurement.budget_quantity,
                    qsMeasurement.work_completed,
                    qsMeasurement.work_remaining,
                    qsMeasurement.work_achieved_in_percent,
                    qsMeasurement.work_remaining_in_percent,
                    qsMeasurement.details
            ]
            counter++
        }
        return lstQsMeasurementList
    }
}
