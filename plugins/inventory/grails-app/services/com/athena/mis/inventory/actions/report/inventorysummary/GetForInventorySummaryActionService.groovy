package com.athena.mis.inventory.actions.report.inventorysummary

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Search and show list for grid view of inventory summery
 *  For details go through Use-Case doc named 'GetForInventorySummaryActionService'
 */
class GetForInventorySummaryActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MSG = "Failed to load inventory summary grid"
    private static final String INVALID_INPUT = "Error occurred for invalid inputs"
    private static final String NOT_FOUND = "Inventory Summary Report not found within given dates"
    private static final String START_DATE = "startDate"
    private static final String END_DATE = "endDate"
    private static final String INVENTORY_ID = "inventoryId"
    private static final String SERVICE_RETURN = "serviceReturn"
    private static final String OBJ_GRID = "objGrid"

    /**
     * Check and get required parameters from UI
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            // check required parameters
            if ((!params.inventoryId) || (!params.startDate) || (!params.endDate)) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            // get required parameters from UI
            long inventoryId = Long.parseLong(params.inventoryId.toString())
            Date startDate = DateUtility.parseMaskedFromDate(params.startDate)
            Date endDate = DateUtility.parseMaskedToDate(params.endDate)

            result.put(INVENTORY_ID, inventoryId)
            result.put(START_DATE, startDate)
            result.put(END_DATE, endDate)
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
     * Get inventory summary list for grid through specific search by inventory id
     * @param params -serialized parameters from UI
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            Map preResult = (Map) obj   // cast map returned from executePreCondition method
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long inventoryId = Long.parseLong(preResult.get(INVENTORY_ID).toString())
            Date startDate = (Date) preResult.get(START_DATE)
            Date endDate = (Date) preResult.get(END_DATE)
            generatePagination(parameterMap)    // initialize parameters for flexGrid
            Map serviceReturn = getInventorySummary(inventoryId, startDate, endDate)    // get inventory summary list
            List<GroovyRowResult> inventorySummaryList = (List<GroovyRowResult>) serviceReturn.inventoryTransactionSummaryList
            if (inventorySummaryList.size() <= 0) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(SERVICE_RETURN, serviceReturn)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap inventory summary list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            Map objGrid = null
            Map serviceReturn = (LinkedHashMap) executeResult.get(SERVICE_RETURN)
            List<GroovyRowResult> lstInventorySummary = (List<GroovyRowResult>) serviceReturn.inventoryTransactionSummaryList
            int count = (int) serviceReturn.count
            if (serviceReturn.size() > 0) {
                List lstWrappedInventorySummary = wrapInventorySummaryList(lstInventorySummary, start)
                objGrid = [page: pageNumber, total: count, rows: lstWrappedInventorySummary]
            }
            result.put(OBJ_GRID, objGrid)
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
        Map result = new LinkedHashMap()
        try {
            Map previousResult = (Map) obj  // cast map returned from previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Wrap list of inventory summary in grid entity
     * @param lstInventorySummary -list of inventory summary
     * @param start -starting index of the page
     * @return -list of wrapped inventory summary
     */
    private List wrapInventorySummaryList(List<GroovyRowResult> lstInventorySummary, int start) {
        List lstWrappedInventorySummary = []
        int counter = start + 1
        for (int i = 0; i < lstInventorySummary.size(); i++) {
            GridEntity obj = new GridEntity()
            GroovyRowResult singleRow = lstInventorySummary[i]
            Date dt = singleRow.d_in ? singleRow.d_in : singleRow.d_out
            String strDate = DateUtility.getDateFormatAsString(dt)
            String itemName = singleRow.m_in_name ? singleRow.m_in_name : singleRow.m_out_name
            obj.id = counter
            obj.cell = [
                    counter,
                    strDate,
                    itemName,
                    singleRow.q_in,
                    singleRow.count_in,
                    singleRow.q_out,
                    singleRow.count_out
            ]
            lstWrappedInventorySummary << obj
            counter++
        }
        return lstWrappedInventorySummary
    }

    /**
     * Check and get required parameters
     * Initialize parameters for flexGrid
     * @param params -serialized parameters from UI
     */
    private void generatePagination(GrailsParameterMap params) {
        if (!params.page || !params.rp) {
            params.page = 1
            params.rp = 20
            params.currentCount = 0
            params.sortname = sortColumn
            params.sortorder = sortOrder
        }
        initSearch(params)  // initialize parameters for flexGrid
    }

    private static final String COUNT_QUERY = """
                SELECT
                count(1)
                FROM
                (
                SELECT date(iitd.transaction_date) AS transaction_date
                        FROM inv_inventory_transaction_details iitd
                        WHERE iitd.transaction_date BETWEEN :fromDate AND :toDate
                        AND iitd.inventory_id=:inventoryId
                        AND iitd.approved_by>0
                        GROUP BY DATE(iitd.transaction_date),iitd.item_id
                ) AS totals_a
    """

    /**
     * Get inventory summary list by inventory id
     * @param inventoryId -id of inventory
     * @param startDate -starting date
     * @param endDate -end date
     * @return -a list of inventory summary
     */
    private Map getInventorySummary(long inventoryId, Date startDate, Date endDate) {
        Date fromDate = DateUtility.getSqlDateWithSeconds(startDate)
        Date toDate = DateUtility.getSqlDateWithSeconds(endDate)
        // query for list
        String queryStr = """
        SELECT
            totals_a.item_id m_in_id,
            m1.name m_in_name,
            totals_b.item_id m_out_id,
            m2.name m_out_name,
            totals_a.transaction_date d_in,
            totals_b.transaction_date d_out,
            (to_char(totals_a.total_quantity,'${Tools.DB_QUANTITY_FORMAT}') || ' ' || m1.unit) q_in,
            (to_char(totals_b.total_quantity,'${Tools.DB_QUANTITY_FORMAT}') || ' ' || m2.unit) q_out,
            totals_a.count_in, totals_b.count_out
        FROM
        (
            SELECT count(id) count_in, date(iitd.transaction_date) AS transaction_date,iitd.item_id, SUM(iitd.actual_quantity) AS total_quantity
            FROM inv_inventory_transaction_details iitd
            WHERE iitd.transaction_date BETWEEN :fromDate AND :toDate
            AND iitd.inventory_id=:inventoryId
            AND iitd.is_increase=true
            AND iitd.approved_by>0
            GROUP BY DATE(iitd.transaction_date),iitd.item_id
        ) AS totals_a
        FULL OUTER JOIN
        (
            SELECT count(id) count_out, date(iitd.transaction_date) AS transaction_date,iitd.item_id, SUM(iitd.actual_quantity) AS total_quantity
            FROM inv_inventory_transaction_details iitd
            WHERE iitd.transaction_date BETWEEN :fromDate AND :toDate
            AND iitd.inventory_id=:inventoryId
            AND iitd.is_increase=false
             AND iitd.approved_by>0
            GROUP BY DATE(iitd.transaction_date),iitd.item_id
        ) AS totals_b
        ON totals_a.item_id = totals_b.item_id
        AND totals_a.transaction_date = totals_b.transaction_date
        LEFT JOIN item  m1 ON (m1.id=totals_a.item_id)
        LEFT JOIN item  m2 ON (m2.id=totals_b.item_id)
        ORDER BY totals_a.transaction_date,totals_b.transaction_date
        LIMIT :resultPerPage OFFSET :start
        """

        Map queryParams = [
                inventoryId: inventoryId,
                fromDate: fromDate,
                toDate: toDate,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstInventoryDetails = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> resultCount = executeSelectSql(COUNT_QUERY, queryParams)
        int total = resultCount[0].count ? resultCount[0].count as int : 0
        return [inventoryTransactionSummaryList: lstInventoryDetails, count: total]
    }
}
