package com.athena.mis.inventory.actions.report.inventorystock

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Search and show list of items with inventory stock quantity
 *  For details go through Use-Case doc named 'ListForInventoryStockActionService'
 */
class ListForInventoryStockActionService extends BaseService implements ActionIntf {
    protected final Logger log = Logger.getLogger(getClass())

    private static final String LST_INVENTORY_STOCK = "lstInventoryStock"
    private static final String INVENTORY_STOCK_LIST_GRID = "gridOutput"
    private static final String EXCEPTION_MESSAGE = "Internal Server Error"
    private static final String DEFAULT_ERROR_MESSAGE = "Fail to load Inventory Stock"
    private static final String NO_STOCK_FOUND = "No inventory stock found for selected project"
    private static final String PROJECT_ID = "projectId"
    private static final String INVENTORY_ID = "inventoryId"
    private static final String INVENTORY_TYPE_ID = "inventoryTypeId"
    private static final String NO_INVENTORY_MAPPED = "User is not mapped with any inventory"

    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    /**
     * Check required parameter
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
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
     * Get inventory ids and check association with user
     * Get item stock list for grid through specific search by project ids and inventory ids
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // check required parameters
            if (!parameterMap.rp) {
                parameterMap.rp = 20
                parameterMap.page = 1
            }
            initPager(parameterMap) // initialize parameters for flexGrid
            // assign default value -1 for 'ALL'-(projects/inventories/inventory types)
            long projectId = parameterMap.projectId.equals(Tools.EMPTY_SPACE)?-1:Long.parseLong(parameterMap.projectId.toString())
            long inventoryId =parameterMap.inventoryId.equals(Tools.EMPTY_SPACE)?-1:Long.parseLong(parameterMap.inventoryId.toString())
            long inventoryTypeId =parameterMap.inventoryTypeId.equals(Tools.EMPTY_SPACE)?-1: Long.parseLong(parameterMap.inventoryTypeId.toString())
            // get inventory ids
            List<Long> lstInventoryIds = []
            if (inventoryTypeId < 0 && inventoryId < 0) {
                lstInventoryIds = projectId > 0 ? invSessionUtil.getUserInventoryIdsByProject(projectId) : invSessionUtil.getUserInventoryIds()
            } else if (inventoryTypeId > 0 && inventoryId < 0) {
                lstInventoryIds = projectId > 0 ? invSessionUtil.getUserInventoryIdsByTypeAndProject(inventoryTypeId, projectId) : invSessionUtil.getUserInventoryIdsByType(inventoryTypeId)
            } else if (inventoryTypeId > 0 && inventoryId > 0) {
                lstInventoryIds << new Long(inventoryId)
            }
            // check if user is mapped with any inventory or not
            if (lstInventoryIds.size() <= 0) {
                result.put(Tools.MESSAGE, NO_INVENTORY_MAPPED)
                return result
            }
            // get project ids
            List<Long> projectIds = []
            if (projectId <= 0) {
                List<Long> tempProjectIdList = invSessionUtil.appSessionUtil.getUserProjectIds()
                if (tempProjectIdList.size() <= 0) { //if tempList is null then set 0 at main list, So that cache don't update
                    projectIds << new Long(0)
                } else {  //if tempList is not null then set tempProjectIdList at main list
                    projectIds = tempProjectIdList
                }
            } else {
                projectIds << new Long(projectId)
            }
            // get item stock list
            LinkedHashMap inventoryStockMap = getInventoryStock(lstInventoryIds, projectIds)
            if (inventoryStockMap.count.toInteger() <= 0) {
                result.put(Tools.MESSAGE, NO_STOCK_FOUND)
                return result
            }
            result.put(LST_INVENTORY_STOCK, inventoryStockMap.inventoryStockList)
            result.put(Tools.COUNT, inventoryStockMap.count)
            result.put(PROJECT_ID, projectId)
            result.put(INVENTORY_ID, inventoryId)
            result.put(INVENTORY_TYPE_ID, inventoryTypeId)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap item stock list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj    // cast map returned from execute method
            List<GroovyRowResult> lstInventoryStock = (List<GroovyRowResult>) executeResult.get(LST_INVENTORY_STOCK)
            // wrap item stock list
            List lstWrappedInventoryStock = wrapInventoryStockList(lstInventoryStock, start)
            int count = Integer.parseInt(executeResult.get(Tools.COUNT).toString())
            Map gridOutput = [page: pageNumber, total: count, rows: lstWrappedInventoryStock]

            long projectId = Long.parseLong(executeResult.get(PROJECT_ID).toString())
            long inventoryId = Long.parseLong(executeResult.get(INVENTORY_ID).toString())
            long inventoryTypeId = Long.parseLong(executeResult.get(INVENTORY_TYPE_ID).toString())

            result.put(PROJECT_ID, projectId)
            result.put(INVENTORY_ID, inventoryId)
            result.put(INVENTORY_TYPE_ID, inventoryTypeId)
            result.put(INVENTORY_STOCK_LIST_GRID, gridOutput)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
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
            LinkedHashMap previousResult = (LinkedHashMap) obj// cast map returned from previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of item stocks in grid entity
     * @param lstInventoryStock -list of item stocks
     * @param start -starting index of the page
     * @return -list of wrapped item stocks
     */
    private List wrapInventoryStockList(List<GroovyRowResult> lstInventoryStock, int start) {
        List lstWrappedInventoryStock = []
        int counter = start + 1
        GridEntity obj
        GroovyRowResult singleRow
        for (int i = 0; i < lstInventoryStock.size(); i++) {
            singleRow = lstInventoryStock[i]
            obj = new GridEntity()
            obj.id = singleRow.id
            obj.cell = [counter, singleRow.name, singleRow.curr_quantity]
            lstWrappedInventoryStock << obj
            counter++
        }
        return lstWrappedInventoryStock
    }

    /**
     * Get item stock list through specific search by project ids and inventory ids
     * @param lstInventoryIds -list of inventory ids
     * @param projectIds -list of project ids
     * @return
     */
    private LinkedHashMap getInventoryStock(List<Long> lstInventoryIds, List<Long> projectIds) {
        String projectIdStr = Tools.buildCommaSeparatedStringOfIds(projectIds)
        String inventoryIds = Tools.buildCommaSeparatedStringOfIds(lstInventoryIds)
        // query for list
        String queryStr = """ SELECT item.id AS id, item.name  AS name,
                to_char(COALESCE(SUM(vw_inv_inventory_stock.available_stock),0),'${Tools.DB_QUANTITY_FORMAT}') || ' ' || item.unit AS curr_quantity
                FROM vw_inv_inventory_stock
                LEFT JOIN item ON vw_inv_inventory_stock.item_id = item.id
                WHERE
                vw_inv_inventory_stock.inventory_id IN (${inventoryIds}) AND
                vw_inv_inventory_stock.project_id IN (${projectIdStr})
                GROUP BY item.id, item.name, item.unit
                ORDER BY item.name asc
               LIMIT :resultPerPage OFFSET :start
        """
        // query for count
        String queryCount = """
        SELECT COUNT(count) AS count FROM(
        SELECT COUNT(item_id) AS count FROM vw_inv_inventory_stock
        WHERE
            vw_inv_inventory_stock.inventory_id IN (${inventoryIds}) AND
            vw_inv_inventory_stock.project_id IN (${projectIdStr})
            GROUP BY item_id) AS temp
        """
        Map queryParams = [
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> itemListWithQnty = executeSelectSql(queryStr, queryParams)

        List<GroovyRowResult> resultCount = executeSelectSql(queryCount)
        int total = resultCount[0].count ? resultCount[0].count as int : 0
        return [inventoryStockList: itemListWithQnty, count: total]
    }
}
