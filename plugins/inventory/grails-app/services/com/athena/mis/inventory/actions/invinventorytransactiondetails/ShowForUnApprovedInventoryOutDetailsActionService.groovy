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
 *  Child class to show UI of unapprovedInventoryOutItem(s)
 *  For details go through Use-Case doc named 'ShowForUnApprovedInventoryOutDetailsActionService'
 */
class ShowForUnApprovedInventoryOutDetailsActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    VehicleCacheUtility vehicleCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService

    private static final String DEFAULT_ERROR_MESSAGE = "Can't load inventory out details"
    private static final String INV_OUT_DETAILS_LIST_WRAP = "inventoryOutDetailsListWrap"
    private static final String INV_OUT_NOT_FOUND = "Inventory out not found"
    private static final String VEHICLE_LIST = "vehicleList"
    private static final String MATERIAL_LIST = "materialList"
    private static final String INV_TRANS_ID = "inventoryTransactionId"
    private static final String COUNT = "count"
    private static final String INV_OUT_MAP = "inventoryOutMap"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Method to build map with necessary object to show invTranOutDetails(Child) UI
     * @Params parameters -serialized parameter sent from UI
     * @Params obj -N/A
     *
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)
            long inventoryTransactionId = Long.parseLong(parameterMap.inventoryOutId.toString())

            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(inventoryTransactionId)
            if (!invInventoryTransaction) { //check existence of parent object
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                result.put(Tools.MESSAGE, INV_OUT_NOT_FOUND)
                return result
            }

            List<GroovyRowResult> inventoryOutList = []
            int total = 0
            if (inventoryTransactionId > 0) {
                //get unapprovedInvOutDetailsList
                LinkedHashMap serviceReturn = listForUnapprovedInvOutDetailsByInvTransId(inventoryTransactionId)
                inventoryOutList = (List<GroovyRowResult>) serviceReturn.inventoryOutDetailsList
                total = (int) serviceReturn.count
            }

            //wrap unapprovedInvOutDetailsList for grid data
            List inventoryOutListWrap = wrapInventoryInListInGridEntityList(inventoryOutList, start)

            //get vehicleList for drop-down
            List vehicleList = vehicleCacheUtility.list()

            //build information map to show on UI label
            Map inventoryOutMap = buildInventoryTransactionMap(invInventoryTransaction)

            //get available material list of this inventory for drop-down
            List<GroovyRowResult> materialList = listAvailableItemInInventory(invInventoryTransaction.inventoryId)

            result.put(INV_OUT_MAP, inventoryOutMap)
            result.put(MATERIAL_LIST, materialList)
            result.put(INV_TRANS_ID, inventoryTransactionId)
            result.put(INV_OUT_DETAILS_LIST_WRAP, inventoryOutListWrap)
            result.put(VEHICLE_LIST, vehicleList)
            result.put(COUNT, total)
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
     * build information map to show on UI label
     * @param invInventoryTransaction -InvInventoryTransaction (Parent) object
     * @return -a map containing necessary information to show on UI
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

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap unapprovedOutDetailsList for grid
     * @param obj -map returned from execute method
     * @return -a map containing all necessary objects to show page(wrappedUnapprovedOutDetails, consumableItemList for drop-down, vehicleList for drop-down)
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(COUNT)
            List inventoryOut = (List) receiveResult.get(INV_OUT_DETAILS_LIST_WRAP)
            List itemList = (List) receiveResult.get(MATERIAL_LIST)
            Long inventoryTransactionId = (Long) receiveResult.get(INV_TRANS_ID)
            List vehicleList = (List) receiveResult.get(VEHICLE_LIST)
            Map inventoryOutMap = (Map) receiveResult.get(INV_OUT_MAP)
            result = [page: pageNumber, total: count, rows: inventoryOut, vehicleList: vehicleList, inventoryTransactionId: inventoryTransactionId,
                    inventoryOutMap: inventoryOutMap, itemList: Tools.listForKendoDropdown(itemList,null,null)]
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
     * @param obj -map returned from previous method, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
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
     * Wrap list of unapprovedOutDetailsList for gird
     *
     * @param inventoryOutList -list of unapprovedOutDetails object(s)
     * @param start -starting index of the page
     *
     * @return -list of wrappedInvTransactionDetailsList
     */
    private List wrapInventoryInListInGridEntityList(List<GroovyRowResult> inventoryOutList, int start) {
        List invTransactionDetailsList = [] as List
        int counter = start + 1
        for (int i = 0; i < inventoryOutList.size(); i++) {
            GroovyRowResult invTransactionDetails = inventoryOutList[i]
            GridEntity obj = new GridEntity()
            obj.id = invTransactionDetails.id
            obj.cell = [counter,
                    invTransactionDetails.id,
                    invTransactionDetails.item_name,
                    invTransactionDetails.actual_quantity + Tools.SINGLE_SPACE + invTransactionDetails.unit,
                    invTransactionDetails.transaction_date,
                    invTransactionDetails.mrf,
                    invTransactionDetails.vehicle,
                    invTransactionDetails.vehicle_number,
                    invTransactionDetails.created_by,
                    invTransactionDetails.updated_by ? invTransactionDetails.updated_by : Tools.EMPTY_SPACE
            ]
            invTransactionDetailsList << obj
            counter++
        }
        return invTransactionDetailsList
    }

    /**
     * method to get list-of-unapproved-inventory-out-item(s) for grid
     * @param inventoryTransactionId -InventoryTransaction.Id(parent object id)
     * @return -a map containing groovyRawResult and total outItems
     */
    private LinkedHashMap listForUnapprovedInvOutDetailsByInvTransId(long inventoryTransactionId) {
        String query = """
                        SELECT
                        iitd.id, item.name AS item_name, item.unit,
                        to_char(iitd.transaction_date,'dd-Mon-yyyy') as transaction_date,
                        to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') actual_quantity, user_created_by.username as created_by,
                        user_updated_by.username as updated_by,iitd.is_increase,
                        iitd.transaction_type_id,iitd.mrf_no as mrf,v.name as vehicle,iitd.vehicle_number
                        FROM
                        inv_inventory_transaction_details iitd
                        LEFT JOIN item ON item.id = iitd.item_id
                        LEFT JOIN vehicle v ON v.id = iitd.vehicle_id
                        LEFT JOIN app_user user_created_by ON user_created_by.id = iitd.created_by
                        LEFT JOIN app_user user_updated_by ON user_updated_by.id = iitd.updated_by
                        LEFT JOIN system_entity transaction_type ON transaction_type.id = iitd.transaction_type_id
                        WHERE inventory_transaction_id= :inventoryTransactionId
                          AND iitd.approved_by <= 0
                          AND is_current = true
                        ORDER BY iitd.transaction_date, iitd.id DESC LIMIT :resultPerPage  OFFSET :start
                        """
        String queryCount = """
                        SELECT count(iitd.id) AS count
                        FROM inv_inventory_transaction_details iitd
                        WHERE inventory_transaction_id= :inventoryTransactionId
                          AND iitd.approved_by <= 0
                          AND is_current = true
                         """
        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> result = executeSelectSql(query, queryParams)
        List resultCount = executeSelectSql(queryCount, queryParams)
        int count = resultCount[0].count
        return [inventoryOutDetailsList: result, count: count]
    }

    private static final String LST_AVAIL_ITEM_QUERY = """
                        SELECT item.id,item.name as name,item.unit as unit,vcs.consumeable_stock as curr_quantity
                        FROM vw_inv_inventory_consumable_stock vcs
                        LEFT JOIN item  on item.id=vcs.item_id
                        WHERE vcs.inventory_id =:inventoryId
                        AND vcs.consumeable_stock>0
                        AND item.is_individual_entity = false
                        ORDER BY item.name ASC
                      """
    /**
     * method to get consumable-item-list in an inventory for drop-down
     * @param inventoryId -inventory.id
     * @return -GroovyRawResult(consumable-item-list)
     */
    private List<GroovyRowResult> listAvailableItemInInventory(long inventoryId) {
        Map queryParams = [inventoryId: inventoryId]
        List<GroovyRowResult> itemListWithQnty = executeSelectSql(LST_AVAIL_ITEM_QUERY, queryParams)
        return itemListWithQnty
    }
}
