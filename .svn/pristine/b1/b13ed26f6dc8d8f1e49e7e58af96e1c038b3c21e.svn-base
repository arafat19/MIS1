package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.Vehicle
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.application.utility.VehicleCacheUtility
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Class to update unapprovedInventoryOutDetails and show in grid
 * For details go through Use-Case doc named 'UpdateForInventoryOutDetailsActionService'
 */
class UpdateForInventoryOutDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionService invInventoryTransactionService
    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    VehicleCacheUtility vehicleCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private static final String INV_OUT_UPDATE_SUCCESS_MESSAGE = "Inventory-Out has been updated successfully"
    private static final String INV_OUT_UPDATE_FAILURE_MESSAGE = "Fail to update Inventory-Out"
    private static final String UNAVAILABLE_QUANTITY = "Quantity is not available in stock"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String INV_OUT_DETAILS_OBJ = "inventoryTransactionDetails"
    private static final String OBJECT_NOT_FOUND = "Inventory-Out details not found, try again"
    private static final String MATERIAL_LIST = "itemList"
    private static final String INV_INV_TRANSACTION_NOT_FOUND = "Inventory Transaction not found"
    private static final String ALREADY_ACK_ERROR = "Acknowledged transaction is not updated"
    private static final String APPROVED_INV_TRANSACTION_DELETE_PROHIBITED = "Approved inventory transaction cannot be deleted."

    /**
     * validate different criteria to update outItem. Such as :
     *      Check existence of oldInvInventoryTransactionOutDetails(child) Obj which will be updated,
     *      check approval of outDetails object
     *      check acknowledgment of outDetails object
     *      Check existence of InvInventoryTransaction(Parent) Obj,
     *      Check availableStockAmount to out
     *
     * @Params params -Receives the serialized parameters send from UI
     * @Params obj -N/A
     *
     * @Return -a map containing all objects necessary for execute (InventoryTransactionOutDetails(child))
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check here for required params are present
            if ((!parameterMap.id) || (!parameterMap.version) || (!parameterMap.actualQuantity)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            // check siteIn Transaction Object existence
            long inventoryTransactionDetailsId = Long.parseLong(parameterMap.id.toString())
            int version = Integer.parseInt(parameterMap.version.toString())
            InvInventoryTransactionDetails oldInvInventoryTransactionDetails = invInventoryTransactionDetailsService.read(inventoryTransactionDetailsId)
            if (!oldInvInventoryTransactionDetails || (oldInvInventoryTransactionDetails.version != version)) {//check existence of object
                result.put(Tools.MESSAGE, OBJECT_NOT_FOUND)
                return result
            }

            if (oldInvInventoryTransactionDetails.approvedBy > 0) {//check approval(approved outTransaction could not be updated)
                result.put(Tools.MESSAGE, APPROVED_INV_TRANSACTION_DELETE_PROHIBITED)
                return result
            }

            if (oldInvInventoryTransactionDetails.acknowledgedBy > 0) { //check acknowledgment(Acknowledged outTransaction could not be updated)
                result.put(Tools.MESSAGE, ALREADY_ACK_ERROR)
                return result
            }

            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(oldInvInventoryTransactionDetails.inventoryTransactionId)
            if (!invInventoryTransaction) {//check existence of parent object
                result.put(Tools.MESSAGE, INV_INV_TRANSACTION_NOT_FOUND)
                return result
            }

            //build object to update
            InvInventoryTransactionDetails newInvInventoryTransactionDetails = buildInventoryOutForUpdate(parameterMap, oldInvInventoryTransactionDetails)

            //get consumableStock of item in an inventory to out
            double consumableQuantity = getConsumableStock(oldInvInventoryTransactionDetails.inventoryId, oldInvInventoryTransactionDetails.itemId)
            double previousQuantity = consumableQuantity + oldInvInventoryTransactionDetails.actualQuantity
            double availableQuantity = previousQuantity - newInvInventoryTransactionDetails.actualQuantity
            if (availableQuantity < 0) {
                result.put(Tools.MESSAGE, UNAVAILABLE_QUANTITY)
                return result
            }

            result.put(INV_OUT_DETAILS_OBJ, newInvInventoryTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INV_OUT_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Method to update InvTransactionOutDetails
     *
     * @param parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains InvInventoryTransactionOutDetails(child)
     * @Return -a map containing all objects necessary for buildSuccessResultForUI(InvInventoryTransactionOutDetails(child))
     * map -contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) preResult.get(INV_OUT_DETAILS_OBJ)

            //update invTranOutDetails object
            updateInventoryOutDetails(invInventoryTransactionDetails)

            result.put(INV_OUT_DETAILS_OBJ, invInventoryTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INV_OUT_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap unapprovedInventoryOur object for grid
     * @param obj -map returned from execute
     * @return -a map containing all objects necessary for show page (wrappedUnapprovedInventoryOut)
     * map -contains isError(true/false) depending on method success, available itemList for drop-down to out again
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            InvInventoryTransactionDetails inventoryOutDetails = (InvInventoryTransactionDetails) receiveResult.get(INV_OUT_DETAILS_OBJ)
            Item item = (Item) itemCacheUtility.read(inventoryOutDetails.itemId)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(inventoryOutDetails.createdBy)
            AppUser updatedBy = (AppUser) appUserCacheUtility.read(inventoryOutDetails.updatedBy)
            Vehicle vehicle = (Vehicle) vehicleCacheUtility.read(inventoryOutDetails.vehicleId)
            String transactionDateStr = DateUtility.getLongDateForUI(inventoryOutDetails.transactionDate)

            GridEntity object = new GridEntity()
            object.id = inventoryOutDetails.id
            object.cell = [Tools.LABEL_NEW,
                    inventoryOutDetails.id,
                    item.name,
                    Tools.formatAmountWithoutCurrency(inventoryOutDetails.actualQuantity) + Tools.SINGLE_SPACE + item.unit,
                    transactionDateStr,
                    inventoryOutDetails.mrfNo,
                    vehicle.name,
                    inventoryOutDetails.vehicleNumber,
                    createdBy.username,
                    updatedBy.username
            ]

            // pull latest material list (to out again) to show on drop-down
            List materialList = listAvailableItemInInventory(inventoryOutDetails.inventoryId)
            result.put(MATERIAL_LIST, Tools.listForKendoDropdown(materialList,null,null))
            result.put(Tools.MESSAGE, INV_OUT_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INV_OUT_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to update outDetails object
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, INV_OUT_UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INV_OUT_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Method to build outDetails object to update
     * @param parameterMap -GrailsParameterMap
     * @param oldInvTranOutDetails -OldInventoryTransactionOutDetails object(child) which will be updated
     * @return -newInventoryTransactionOutDetails object
     */
    private InvInventoryTransactionDetails buildInventoryOutForUpdate(GrailsParameterMap parameterMap, InvInventoryTransactionDetails oldInvTranOutDetails) {
        InvInventoryTransactionDetails invTranOutDetails = new InvInventoryTransactionDetails(parameterMap)
        AppUser systemUser = invSessionUtil.appSessionUtil.getAppUser()
        invTranOutDetails.id = Long.parseLong(parameterMap.id.toString())
        invTranOutDetails.version = Integer.parseInt(parameterMap.version.toString())
        invTranOutDetails.inventoryId = oldInvTranOutDetails.inventoryId
        invTranOutDetails.inventoryTypeId = oldInvTranOutDetails.inventoryTypeId
        invTranOutDetails.inventoryTransactionId = oldInvTranOutDetails.inventoryTransactionId
        invTranOutDetails.itemId = oldInvTranOutDetails.itemId
        invTranOutDetails.updatedBy = systemUser.id
        invTranOutDetails.updatedOn = new Date()
        invTranOutDetails.transactionDate = oldInvTranOutDetails.transactionDate
        invTranOutDetails.rate = oldInvTranOutDetails.rate
        invTranOutDetails.createdOn = oldInvTranOutDetails.createdOn
        invTranOutDetails.createdBy = oldInvTranOutDetails.createdBy
        invTranOutDetails.createdOn = oldInvTranOutDetails.createdOn
        invTranOutDetails.transactionDetailsId = 0L

        invTranOutDetails.comments = parameterMap.comments ? parameterMap.comments : Tools.EMPTY_SPACE
        invTranOutDetails.supplierChalan = parameterMap.supplierChalan ? parameterMap.supplierChalan : Tools.EMPTY_SPACE

        invTranOutDetails.adjustmentParentId = oldInvTranOutDetails.id
        invTranOutDetails.approvedOn = null
        invTranOutDetails.isIncrease = false
        invTranOutDetails.isCurrent = true

        return invTranOutDetails
    }

    private static final String COSUM_STOCK_QUERY = """
        SELECT consumeable_stock
        FROM vw_inv_inventory_consumable_stock
        WHERE inventory_id=:inventoryId
            AND item_id=:itemId
    """
    /**
     * Method to get consumableStock of an item in an inventory from view(vw_inv_inventory_consumable_stock)
     * @param inventoryId -InvInventory.id
     * @param itemId -item.id
     * @return double value
     */
    private double getConsumableStock(long inventoryId, long itemId) {
        Map queryParams = [inventoryId: inventoryId, itemId: itemId]
        List<GroovyRowResult> result = executeSelectSql(COSUM_STOCK_QUERY, queryParams)
        if (result.size() > 0) {
            return (double) result[0].consumeable_stock
        }
        return 0.0d
    }

    private static final String LST_AVAIL_ITEM_IN_INV_QUERY = """
                        SELECT item.id,item.name as name,item.unit as unit,vcs.consumeable_stock as curr_quantity
                        FROM vw_inv_inventory_consumable_stock vcs
                        LEFT JOIN item  on item.id=vcs.item_id
                        WHERE vcs.inventory_id=:inventoryId
                        AND vcs.consumeable_stock>0
                        AND item.is_individual_entity = false
                        ORDER BY item.name ASC
                      """
    /**
     * method to get list of available item(s) from view(vw_inv_inventory_consumable_stock) to out again
     * @param inventoryId -InvInventory.id
     * @return GroovyRowResult ( list of items )
     */
    private List<GroovyRowResult> listAvailableItemInInventory(long inventoryId) {
        Map queryParams = [inventoryId: inventoryId]
        List<GroovyRowResult> itemListWithQnty = executeSelectSql(LST_AVAIL_ITEM_IN_INV_QUERY, queryParams)
        return itemListWithQnty
    }

    private static final String UPDATE_QUERY = """
                      UPDATE inv_inventory_transaction_details SET
                          vehicle_id = :vehicleId,
                          vehicle_number = :vehicleNumber,
                          actual_quantity= :actualQuantity,
                          stack_measurement=:stackMeasurement,
                          mrf_no = :mrfNo,
                          comments=:comments,
                          updated_on=:updatedOn,
                          updated_by= :updatedBy,
                          version= :newVersion
                      WHERE
                          id=:id AND
                          version=:version
                      """
    /**
     * Method to update invTransactionOutDetailsObject
     * @param invInventoryTransactionDetails -InvInventoryTransactionDetails object (child)
     * @return int value updateCount (if updateCount<=0 then throw exception to rollback all DB transaction)
     */
    private int updateInventoryOutDetails(InvInventoryTransactionDetails invInventoryTransactionDetails) {
        Map queryParams = [
                id: invInventoryTransactionDetails.id,
                version: invInventoryTransactionDetails.version,
                newVersion: invInventoryTransactionDetails.version + 1,
                vehicleId: invInventoryTransactionDetails.vehicleId,
                vehicleNumber: invInventoryTransactionDetails.vehicleNumber,
                actualQuantity: invInventoryTransactionDetails.actualQuantity,
                mrfNo: invInventoryTransactionDetails.mrfNo,
                stackMeasurement: invInventoryTransactionDetails.stackMeasurement,
                updatedBy: invInventoryTransactionDetails.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(invInventoryTransactionDetails.updatedOn),
                comments: invInventoryTransactionDetails.comments
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to update inventory transaction out details")
        }
        return updateCount
    }
}