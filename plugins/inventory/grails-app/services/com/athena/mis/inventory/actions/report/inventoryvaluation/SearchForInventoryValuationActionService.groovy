package com.athena.mis.inventory.actions.report.inventoryvaluation

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Search and show list for grid view of inventory valuation
 *  For details go through Use-Case doc named 'SearchForInventoryValuationActionService'
 */
class SearchForInventoryValuationActionService extends BaseService implements ActionIntf {

    private static final String NOT_FOUND = "Inventory Valuation not found"
    private static final String FAILURE_MSG = "Fail to generate Inventory Valuation"
    private static final String LST_INVENTORY_VALUATION = "lstInventoryValuation"
    private static final String INVENTORY_VALUATION_LIST_GRID = "gridOutput"

    protected final Logger log = Logger.getLogger(getClass())

    /**
     * Check and get required parameters from UI
     * Get inventory valuation list
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            // check required parameter
            if (!params.inventoryId) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }
            long inventoryId = Long.parseLong(params.inventoryId.toString())
            generatePagination(params)  // initialize parameters for flexGrid
            // get list and count of inventory valuation
            Map invValuationSummaryMap = listForInventoryValuation(inventoryId)
            List<GroovyRowResult> lstInvValuationSummary = (List<GroovyRowResult>) invValuationSummaryMap.lstInventorySummary
            int count = (int) invValuationSummaryMap.count
            if (invValuationSummaryMap.size() == 0) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }
            result.put(LST_INVENTORY_VALUATION, lstInvValuationSummary)
            result.put(Tools.COUNT, count)
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
     * Build a map with necessary objects for build success result
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            List<GroovyRowResult> invValuationSummaryList = (List<GroovyRowResult>) preResult.get(LST_INVENTORY_VALUATION)
            result.put(LST_INVENTORY_VALUATION, invValuationSummaryList)
            result.put(Tools.COUNT, preResult.get(Tools.COUNT))
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap inventory valuation list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            List<GroovyRowResult> lstInvValuationSummary = (List<GroovyRowResult>) executeResult.get(LST_INVENTORY_VALUATION)
            // wrap inventory valuation list
            List lstWrappedInventoryValuation = wrapInventoryValuationList(lstInvValuationSummary, start)
            int count = (int) executeResult.get(Tools.COUNT)
            Map gridOutput = [page: pageNumber, total: count, rows: lstWrappedInventoryValuation]
            result.put(INVENTORY_VALUATION_LIST_GRID, gridOutput)
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
            LinkedHashMap previousResult = (LinkedHashMap) obj   // cast map returned from previous method
            result.put(Tools.IS_ERROR, previousResult.get(Tools.IS_ERROR))
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
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
     * Wrap list of inventory valuation in grid entity
     * @param lstInvValuationSummary -list of inventory valuation
     * @param start -starting index of the page
     * @return -list of wrapped inventory valuation
     */
    private List wrapInventoryValuationList(List<GroovyRowResult> lstInvValuationSummary, int start) {
        List lstWrappedInventoryValuation = []
        int counter = start + 1
        GridEntity obj
        for (int i = 0; i < lstInvValuationSummary.size(); i++) {
            GroovyRowResult eachSummary = lstInvValuationSummary[i]
            obj = new GridEntity()
            obj.id = eachSummary.id
            obj.cell = [
                    counter,
                    eachSummary.item_name,
                    eachSummary.quantity,
                    eachSummary.total_amount,
                    eachSummary.valuation_type
            ]

            lstWrappedInventoryValuation << obj
            counter++
        }
        return lstWrappedInventoryValuation
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
        }
        params.sortname = 'item_name'
        this.initPager(params)  // initialize parameters for flexGrid
    }

    private static final String COUNT_QUERY = """
        SELECT count (*) as count
        FROM vw_inv_inventory_valuation vwiiv
        WHERE vwiiv.inventory_id=:inventoryId
        AND vwiiv.available_stock > 0
    """

    /**
     * Get inventory valuation list by specific inventory id
     * @param inventoryId -id of inventory
     * @return -a map containing list and count of inventory valuation
     */
    private Map listForInventoryValuation(long inventoryId) {
        String queryStr = """
                    SELECT inventory_id as id,item_name,str_available_stock as quantity,str_total_amount as total_amount,valuation_type
                    FROM vw_inv_inventory_valuation vwiiv
                    WHERE
                    vwiiv.inventory_id=:inventoryId
                    AND vwiiv.available_stock > 0
                    ORDER BY ${sortColumn} ${sortOrder} LIMIT :resultPerPage OFFSET :start
        """
        Map queryParams = [
                inventoryId: inventoryId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstInventorySummary = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> resultCount = executeSelectSql(COUNT_QUERY, queryParams)
        int count = (int) resultCount[0][0]

        return [lstInventorySummary: lstInventorySummary, count: count]
    }
}
