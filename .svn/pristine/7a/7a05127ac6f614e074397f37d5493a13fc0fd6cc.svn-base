package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvInventoryTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for inventory transaction details(Inventory In From Inventory) adjustment and list of approved inventory transaction details for grid
 *  For details go through Use-Case doc named 'ShowForApprovedInvInFromInventoryActionService'
 */
class ShowForApprovedInvInFromInventoryActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Can't load Inventory-In page"
    private static final String LST_INVENTORY_IN_DETAILS = "lstInventoryInDetails"
    private static final String INVENTORY_IN_NOT_FOUND = "Inventory in not found"
    private static final String INVENTORY_IN_MAP = "inventoryInMap"
    private static final String TRANSACTION_DATE = "transactionDate"
    private static final String GRID_OBJ = "gridObj"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get inventory transaction details(Inventory In From Inventory) list for grid
     * Build map with inventory transaction object(parent object)
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap) // initialize parameters for flexGrid
            List<GroovyRowResult> lstInventoryInDetails = []
            int total = 0
            // get list and count of inventory transaction details(Inventory In From Inventory)
            long inventoryTransactionId = Long.parseLong(parameterMap.inventoryTransactionId.toString())
            if (inventoryTransactionId > 0) {
                LinkedHashMap serviceReturn = listForApprovedInvInDetailsByInvTransId(inventoryTransactionId)
                lstInventoryInDetails = (List<GroovyRowResult>) serviceReturn.invTransDetailsList
                total = (int) serviceReturn.count
            }

            // get inventory transaction object(parent object)
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(inventoryTransactionId)
            // check if inventory transaction object(parent object) exists or not
            if (!invInventoryTransaction) {
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                result.put(Tools.MESSAGE, INVENTORY_IN_NOT_FOUND)
                return result
            }
            // build map with inventory transaction object(parent object)
            Map inventoryInMap = buildInventoryTransactionMap(invInventoryTransaction)

            String transactionDate = DateUtility.getDateForUI(invInventoryTransaction.transactionDate)
            result.put(TRANSACTION_DATE, transactionDate)
            result.put(INVENTORY_IN_MAP, inventoryInMap)
            result.put(LST_INVENTORY_IN_DETAILS, lstInventoryInDetails)
            result.put(Tools.COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Wrap inventory transaction details list (Inventory In From Inventory) for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view and UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            int count = (int) executeResult.get(Tools.COUNT)
            List lstInventoryInDetails = (List) executeResult.get(LST_INVENTORY_IN_DETAILS)
            // wrap inventory transaction details list (Inventory In From Inventory) in grid entity
            List lstWrappedInventoryInDetails = wrapInventoryInDetailsList(lstInventoryInDetails, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedInventoryInDetails]
            result.put(GRID_OBJ, gridObj)
            result.put(INVENTORY_IN_MAP, executeResult.get(INVENTORY_IN_MAP))
            result.put(TRANSACTION_DATE, executeResult.get(TRANSACTION_DATE))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build map with inventory transaction object
     * @param invInventoryTransaction -inventory transaction object
     * @return -map with necessary properties of inventory transaction object
     */
    private Map buildInventoryTransactionMap(InvInventoryTransaction invInventoryTransaction) {
        InvInventory invInventory = (InvInventory) invInventoryCacheUtility.read(invInventoryTransaction.inventoryId)
        InvInventory fromInventory = (InvInventory) invInventoryCacheUtility.read(invInventoryTransaction.transactionEntityId)
        SystemEntity invInventoryType = (SystemEntity) invInventoryTypeCacheUtility.read(invInventoryTransaction.inventoryTypeId)

        Map inventoryInMap = [
                inventoryId: invInventoryTransaction.inventoryId,
                inventoryName: invInventory.name,
                transactionId: invInventoryTransaction.transactionId,
                fromInventoryName: fromInventory.name,
                inventoryTransactionId: invInventoryTransaction.id,
                inventoryType: invInventoryType.key
        ]
        return inventoryInMap
    }

    /**
     * Wrap list of inventory transactions details(Inventory In From Inventory) in grid entity
     * @param lstInventoryInDetails -list of inventory transaction details object(s)
     * @param start -starting index of the page
     * @return -list of wrapped inventory transactions details(Inventory In From Inventory)
     */
    private List wrapInventoryInDetailsList(List<GroovyRowResult> lstInventoryInDetails, int start) {
        List lstWrappedInventoryInDetails = []
        int counter = start + 1
        GroovyRowResult invTransactionDetails
        GridEntity obj
        for (int i = 0; i < lstInventoryInDetails.size(); i++) {
            invTransactionDetails = lstInventoryInDetails[i]
            obj = new GridEntity()
            obj.id = invTransactionDetails.id
            obj.cell = [counter,
                    invTransactionDetails.id,
                    invTransactionDetails.item_name,
                    invTransactionDetails.supplied_quantity + Tools.SINGLE_SPACE + invTransactionDetails.unit,
                    invTransactionDetails.actual_quantity + Tools.SINGLE_SPACE + invTransactionDetails.unit,
                    invTransactionDetails.shrinkage + Tools.SINGLE_SPACE + invTransactionDetails.unit,
                    invTransactionDetails.rate,
                    invTransactionDetails.approved_on,
                    invTransactionDetails.approved_by,
                    invTransactionDetails.created_by
            ]
            lstWrappedInventoryInDetails << obj
            counter++
        }
        return lstWrappedInventoryInDetails
    }

    private static final String COUNT_QUERY = """
        SELECT count(iitd.id) AS count
        FROM inv_inventory_transaction_details iitd
        WHERE inventory_transaction_id= :inventoryTransactionId
          AND iitd.approved_by > 0
          AND iitd.is_current = true
    """

    /**
     * Get list and count of approved inventory transactions details(Inventory In From Inventory)
     * @param inventoryTransactionId -id of inventory transaction object(parent object)
     * @return -a map containing list and count of approved inventory transactions details(Inventory In From Inventory)
     */
    private Map listForApprovedInvInDetailsByInvTransId(long inventoryTransactionId) {
        // query for list
        String queryStr = """
        SELECT iitd.id, item.name AS item_name, item.unit, iitd.rate,to_char(iitd.transaction_date,'dd-Mon-yyyy') as transaction_date,
        to_char(iitd.supplied_quantity,'${Tools.DB_QUANTITY_FORMAT}') supplied_quantity,
        to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') actual_quantity,
        to_char(iitd.shrinkage,'${Tools.DB_QUANTITY_FORMAT}') shrinkage, to_char(iitd.approved_on,'dd-Mon-yyyy') as approved_on,
        user_approved_by.username AS approved_by, user_created_by.username as created_by,
        iitd.supplier_chalan,vehicle.name AS vehicle_name,iitd.transaction_type_id
        FROM inv_inventory_transaction_details iitd
        LEFT JOIN item ON item.id = iitd.item_id
        LEFT JOIN app_user user_approved_by ON user_approved_by.id = iitd.approved_by
        LEFT JOIN app_user user_created_by ON user_created_by.id = iitd.created_by
        LEFT JOIN vehicle ON vehicle.id = iitd.vehicle_id
        WHERE iitd.inventory_transaction_id = :inventoryTransactionId
        AND iitd.approved_by > 0
        AND iitd.is_current = true
        ORDER BY ${sortColumn} ${sortOrder} LIMIT :resultPerPage OFFSET :start
        """
        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(COUNT_QUERY, queryParams)
        int total = countResult[0].count
        return [invTransDetailsList: result, count: total]
    }
}