package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.VehicleCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for inventory transaction details(Inventory Out) adjustment and list of approved inventory transaction details for grid
 *  For details go through Use-Case doc named 'ShowForApprovedInventoryOutDetailsActionService'
 */
class ShowForApprovedInventoryOutDetailsActionService extends BaseService implements ActionIntf {
    protected final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired
    VehicleCacheUtility vehicleCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Can't load Inventory out details"
    private static final String LST_INVENTORY_OUT_DETAILS = "lstInventoryOutDetails"
    private static final String INV_OUT_NOT_FOUND = "Inventory out not found"
    private static final String LST_VEHICLE = "lstVehicle"
    private static final String LST_ITEM = "lstItem"
    private static final String INV_OUT_MAP = "inventoryOutMap"
    private static final String GRID_OBJ = "gridObj"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get approved inventory transaction details (Inventory Out) list for grid
     * Build map with inventory transaction object (parent object)
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
            List<GroovyRowResult> lstInventoryOutDetails = []
            int total = 0
            // get list and count of inventory transaction details(Inventory Out)
            long inventoryTransactionId = Long.parseLong(parameterMap.inventoryOutId.toString())
            if (inventoryTransactionId > 0) {
                LinkedHashMap serviceReturn = listForApprovedInvOutDetailsByInvTransId(inventoryTransactionId)
                lstInventoryOutDetails = (List<GroovyRowResult>) serviceReturn.inventoryOutDetailsList
                total = (int) serviceReturn.count
            }
            List lstVehicle = vehicleCacheUtility.list()   // get list of vehicle for drop down
            // get inventory transaction object (parent object)
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(inventoryTransactionId)
            // check if inventory transaction object (parent object) exists or not
            if (!invInventoryTransaction) {
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                result.put(Tools.MESSAGE, INV_OUT_NOT_FOUND)
                return result
            }
            // build map with inventory transaction object (parent object)
            Map inventoryOutMap = buildInventoryTransactionMap(invInventoryTransaction)


            List<GroovyRowResult> lstItem
            // get list of available item with consumable quantity
            lstItem = listAvailableItemInInventory(invInventoryTransaction.inventoryId)

            result.put(INV_OUT_MAP, inventoryOutMap)
            result.put(LST_ITEM, lstItem)
            result.put(LST_INVENTORY_OUT_DETAILS, lstInventoryOutDetails)
            result.put(LST_VEHICLE, lstVehicle)
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
     * Wrap inventory transaction details list (Inventory Out) for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view and UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            int count = (int) executeResult.get(Tools.COUNT)
            List lstInventoryOutDetails = (List) executeResult.get(LST_INVENTORY_OUT_DETAILS)
            List lstWrappedInvOutDetails = wrapInventoryOutDetailsList(lstInventoryOutDetails, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedInvOutDetails]
            result.put(GRID_OBJ, gridObj)
            result.put(INV_OUT_MAP, executeResult.get(INV_OUT_MAP))
            result.put(LST_ITEM, Tools.listForKendoDropdown(executeResult.get(LST_ITEM),null,null))
            result.put(LST_VEHICLE, Tools.listForKendoDropdown(executeResult.get(LST_VEHICLE),null,null))
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
        InvInventory fromInv = (InvInventory) invInventoryCacheUtility.read(invInventoryTransaction.inventoryId)
        InvInventory toInv = (InvInventory) invInventoryCacheUtility.read(invInventoryTransaction.transactionEntityId)
        String budgetLineItem = Tools.EMPTY_SPACE
        if (invInventoryTransaction.budgetId > 0) {
            Object budget = budgetImplService.readBudget(invInventoryTransaction.budgetId)
            budgetLineItem = budget.budgetItem
        }
        Map inventoryOutMap = [
                inventoryId: invInventoryTransaction.inventoryId,
                fromInvName: fromInv.name,
                toInvName: toInv.name,
                transactionEntityId: invInventoryTransaction.transactionEntityId,
                inventoryTransactionId: invInventoryTransaction.id,
                budgetLineName: budgetLineItem
        ]
        return inventoryOutMap
    }

    /**
     * Wrap list of inventory transactions details(Inventory In From Inventory) in grid entity
     * @param lstInventoryOutDetails -list of inventory transaction details object(s)
     * @param start -starting index of the page
     * @return -list of wrapped inventory transactions details(Inventory In From Inventory)
     */
    private List wrapInventoryOutDetailsList(List<GroovyRowResult> lstInventoryOutDetails, int start) {
        List lstWrappedInvOutDetails = [] as List
        int counter = start + 1
        GroovyRowResult invTransactionDetails
        GridEntity obj
        for (int i = 0; i < lstInventoryOutDetails.size(); i++) {
            invTransactionDetails = lstInventoryOutDetails[i]
            obj = new GridEntity()
            obj.id = invTransactionDetails.id
            obj.cell = [
                    counter,
                    invTransactionDetails.id,
                    invTransactionDetails.item_name,
                    invTransactionDetails.actual_quantity + Tools.SINGLE_SPACE + invTransactionDetails.unit,
                    invTransactionDetails.rate,
                    invTransactionDetails.transaction_date,
                    invTransactionDetails.mrf,
                    invTransactionDetails.vehicle,
                    invTransactionDetails.vehicle_number,
                    invTransactionDetails.created_by,
                    invTransactionDetails.approved_on,
                    invTransactionDetails.approved_by
            ]
            lstWrappedInvOutDetails << obj
            counter++
        }
        return lstWrappedInvOutDetails
    }

    private static final String COUNT_QUERY = """
            SELECT count(iitd.id) AS count
                FROM inv_inventory_transaction_details iitd
            WHERE inventory_transaction_id= :inventoryTransactionId
              AND iitd.approved_by > 0
              AND iitd.is_current = true
    """

    /**
     * Get list and count of approved inventory transactions details(Inventory Out)
     * @param inventoryTransactionId -id of inventory transaction object(parent object)
     * @return -a map containing list and count of approved inventory transactions details(Inventory Out)
     */
    private LinkedHashMap listForApprovedInvOutDetailsByInvTransId(long inventoryTransactionId) {
        // query for list
        String queryStr = """
                        SELECT
                        iitd.id, item.name AS item_name,iitd.rate, item.unit,to_char(iitd.transaction_date,'dd-Mon-yyyy') as transaction_date,
                        to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') actual_quantity, to_char(iitd.approved_on,'dd-Mon-yyyy') as approved_on,
                        user_created_by.username as created_by, user_approved_by.username AS approved_by,
                        iitd.is_increase,iitd.transaction_type_id,iitd.mrf_no as mrf,v.name as vehicle,iitd.vehicle_number
                        FROM inv_inventory_transaction_details iitd
                        LEFT JOIN item ON item.id = iitd.item_id
                        LEFT JOIN vehicle v ON v.id = iitd.vehicle_id
                        LEFT JOIN app_user user_approved_by ON user_approved_by.id = iitd.approved_by
                        LEFT JOIN app_user user_created_by ON user_created_by.id = iitd.created_by
                        LEFT JOIN system_entity transaction_type ON transaction_type.id = iitd.transaction_type_id
                        WHERE inventory_transaction_id= :inventoryTransactionId
                          AND iitd.approved_by > 0
                          AND iitd.is_current = true
                        ORDER BY iitd.transaction_date, iitd.id DESC LIMIT :resultPerPage OFFSET :start
        """

        Map queryParams = [inventoryTransactionId: inventoryTransactionId]

        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List resultCount = executeSelectSql(COUNT_QUERY, queryParams)
        int count = resultCount[0].count
        return [inventoryOutDetailsList: result, count: count]
    }

    private static final String LST_AVAILABLE_ITEM_QUERY = """
        SELECT item.id,item.name as name,item.unit as unit,vcs.consumeable_stock as curr_quantity
        FROM vw_inv_inventory_consumable_stock vcs
        LEFT JOIN item  on item.id=vcs.item_id
        WHERE vcs.inventory_id=:inventoryId
        AND vcs.consumeable_stock>0
        AND item.is_individual_entity = false
        ORDER BY item.name ASC
    """

    /**
     * Get list of available item with consumable quantity
     * @param inventoryId -id of inventory
     * @return -list of available item with consumable quantity
     */
    private List<GroovyRowResult> listAvailableItemInInventory(long inventoryId) {
        Map queryParams = [
                inventoryId: inventoryId
        ]
        List<GroovyRowResult> itemListWithQuantity = executeSelectSql(LST_AVAILABLE_ITEM_QUERY, queryParams)
        return itemListWithQuantity
    }
}