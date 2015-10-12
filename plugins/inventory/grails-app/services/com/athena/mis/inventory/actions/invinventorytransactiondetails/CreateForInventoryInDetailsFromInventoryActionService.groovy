package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.config.InvSysConfigurationCacheUtility
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new inventory transaction details(Inventory In From Inventory) to receive item from another inventory and show in grid
 *  For details go through Use-Case doc named 'CreateForInventoryInDetailsFromInventoryActionService'
 */
class CreateForInventoryInDetailsFromInventoryActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String INVENTORY_IN_SAVE_SUCCESS_MESSAGE = "Inventory-In details from inventory has been saved successfully"
    private static final String INVENTORY_IN_SAVE_FAILURE_MESSAGE = "Can not saved Inventory-In details from inventory"
    private static final String UN_APPROVED_INVENTORY_OUT_MESSAGE = "Un-approved inventory-out can't be auto-approved"
    private static final String INV_OUT_TRANSACTION_NOT_FOUND = "Inventory out transaction details not found"
    private static final String INVENTORY_IN_DETAILS_OBJ = "invTransactionDetails"
    private static final String INVENTORY_OUT_DETAILS_OBJ = "invTransactionDetailsOut"
    private static final String INVENTORY_IN_OBJ = "invTransaction"
    private static final String INV_INVENTORY_TRANSACTION_NOT_FOUND = "Inventory transaction not found"
    private static final String TRANSACTION_OUT_EXISTS = "This item already received"
    private static final String LST_ITEM = "lstItem"
    private static final String ACTUAL_QTY_BIGGER = "Actual quantity can't be bigger than inventory out quantity"

    InvInventoryTransactionService invInventoryTransactionService
    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvSysConfigurationCacheUtility invSysConfigurationCacheUtility

    /**
     * Checking pre condition and building inventory transaction details object with parameters from UI
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            long inventoryTransactionId = Long.parseLong(parameterMap.inventoryTransactionId.toString())
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(inventoryTransactionId)
            // check if inventory transaction object exists or not
            if (!invInventoryTransaction) {
                result.put(Tools.MESSAGE, INV_INVENTORY_TRANSACTION_NOT_FOUND)
                return result
            }

            long invOutTransactionDetailsId = Long.parseLong(parameterMap.transactionDetailsId.toString())
            InvInventoryTransactionDetails invOutTransactionDetails = invInventoryTransactionDetailsService.read(invOutTransactionDetailsId)
            // check if inventory transaction out details object exists or not
            if (!invOutTransactionDetails || !invOutTransactionDetails.isCurrent || invOutTransactionDetails.acknowledgedBy) {
                result.put(Tools.MESSAGE, INV_OUT_TRANSACTION_NOT_FOUND)
                return result
            }

            // build inventory transaction details object(Inv In From Inventory)
            InvInventoryTransactionDetails invInventoryTransactionDetails = buildInvInventoryTransactionDetailsObject(parameterMap, invInventoryTransaction, invOutTransactionDetails)

            // checking if IN quantity is Bigger than OUT quantity
            double actualQuantity = invInventoryTransactionDetails.actualQuantity
            if (actualQuantity > invOutTransactionDetails.actualQuantity) {
                result.put(Tools.MESSAGE, ACTUAL_QTY_BIGGER)
                return result
            }

            InvInventory invInventoryObject = (InvInventory) invInventoryCacheUtility.read(invInventoryTransactionDetails.inventoryId)
            Project projectObject = (Project) projectCacheUtility.read(invInventoryObject.projectId)

                // check project with auto approval mapping
                if(projectObject.isApproveInFromInventory){
                    // unapproved inventory out can't be auto-approved for inventory in
                    if(invOutTransactionDetails.approvedBy <= 0){
                        result.put(Tools.MESSAGE, UN_APPROVED_INVENTORY_OUT_MESSAGE)
                        return result
                    }
                    long userId = invSessionUtil.appSessionUtil.getAppUser().id
                    invInventoryTransactionDetails.approvedBy = userId
                    invInventoryTransactionDetails.approvedOn = new Date()
                    invInventoryTransactionDetails.rate = invOutTransactionDetails.rate
                }

            result.put(INVENTORY_IN_DETAILS_OBJ, invInventoryTransactionDetails)
            result.put(INVENTORY_IN_OBJ, invInventoryTransaction)
            result.put(INVENTORY_OUT_DETAILS_OBJ, invOutTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INVENTORY_IN_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Create inventory transaction details object (Inventory In From Inventory)
     * Increase item count in inventory transaction object
     * Acknowledge inventory out transaction details
     * This method is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method

            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_IN_DETAILS_OBJ)
            InvInventoryTransactionDetails invOutTransactionDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_OUT_DETAILS_OBJ)
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) preResult.get(INVENTORY_IN_OBJ)
            // create the inventoryIn transaction details object
            InvInventoryTransactionDetails newInvInventoryTransactionDetails = invInventoryTransactionDetailsService.create(invInventoryTransactionDetails)
            // increase item count in inventory transaction object(parent object)
            increaseItemCount(newInvInventoryTransactionDetails.inventoryTransactionId)
            // acknowledge inventory out transaction details
            acknowledgeOutInvTransactionDetails(invOutTransactionDetails.id, invOutTransactionDetails.version, invInventoryTransactionDetails.createdBy);
            // get unacknowledged item list of inventory transaction details out object
            List<GroovyRowResult> lstItem = getItemListOfInventoryOut(invInventoryTransaction.transactionId)

            result.put(LST_ITEM, lstItem)
            result.put(INVENTORY_IN_DETAILS_OBJ, newInvInventoryTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(INVENTORY_IN_SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INVENTORY_IN_SAVE_FAILURE_MESSAGE)
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
     * Show newly created inventory transaction details object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) executeResult.get(INVENTORY_IN_DETAILS_OBJ)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(invInventoryTransactionDetails.createdBy)
            Item item = (Item) itemCacheUtility.read(invInventoryTransactionDetails.itemId)
            GridEntity object = new GridEntity()    // build grid object
            object.id = invInventoryTransactionDetails.id
            object.cell = [
                    Tools.LABEL_NEW,
                    invInventoryTransactionDetails.id,
                    item.name,
                    Tools.formatAmountWithoutCurrency(invInventoryTransactionDetails.suppliedQuantity) + Tools.SINGLE_SPACE + item.unit,
                    Tools.formatAmountWithoutCurrency(invInventoryTransactionDetails.actualQuantity) + Tools.SINGLE_SPACE + item.unit,
                    Tools.formatAmountWithoutCurrency(invInventoryTransactionDetails.shrinkage) + Tools.SINGLE_SPACE + item.unit,
                    createdBy.username,
                    Tools.EMPTY_SPACE
            ]

            result.put(Tools.MESSAGE, INVENTORY_IN_SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(LST_ITEM, Tools.listForKendoDropdown(executeResult.get(LST_ITEM),null,null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INVENTORY_IN_SAVE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, INVENTORY_IN_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INVENTORY_IN_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build inventory transaction details object (Inventory In From Inventory)
     * @param parameterMap -serialized parameters from UI
     * @param invInventoryTransaction -inventory transaction object (parent object)
     * @param invOutTransactionDetails -inventory transaction details out object
     * @return -new inventory transaction details object (Inventory In From Inventory)
     */
    private InvInventoryTransactionDetails buildInvInventoryTransactionDetailsObject(GrailsParameterMap parameterMap, InvInventoryTransaction invInventoryTransaction, InvInventoryTransactionDetails invOutTransactionDetails) {

        AppUser user = invSessionUtil.appSessionUtil.getAppUser()
        double actualQuantity = Double.parseDouble(parameterMap.actualQuantity.toString())
        InvInventoryTransactionDetails invInventoryTransactionDetails = new InvInventoryTransactionDetails()

        invInventoryTransactionDetails.version = 0
        invInventoryTransactionDetails.acknowledgedBy = 0L
        invInventoryTransactionDetails.approvedBy = 0L
        invInventoryTransactionDetails.comments = parameterMap.comments ? parameterMap.comments : null
        invInventoryTransactionDetails.createdBy = user.id
        invInventoryTransactionDetails.createdOn = new Date()
        invInventoryTransactionDetails.fifoQuantity = 0.0d
        invInventoryTransactionDetails.inventoryId = invInventoryTransaction.inventoryId
        invInventoryTransactionDetails.inventoryTransactionId = invInventoryTransaction.id
        invInventoryTransactionDetails.inventoryTypeId = invInventoryTransaction.inventoryTypeId
        invInventoryTransactionDetails.lifoQuantity = 0.0d
        invInventoryTransactionDetails.itemId = invOutTransactionDetails.itemId
        invInventoryTransactionDetails.mrfNo = invOutTransactionDetails.mrfNo
        invInventoryTransactionDetails.rate = invOutTransactionDetails.rate
        invInventoryTransactionDetails.stackMeasurement = invOutTransactionDetails.stackMeasurement
        invInventoryTransactionDetails.suppliedQuantity = invOutTransactionDetails.actualQuantity
        invInventoryTransactionDetails.actualQuantity = actualQuantity
        invInventoryTransactionDetails.shrinkage = invOutTransactionDetails.actualQuantity - actualQuantity
        invInventoryTransactionDetails.supplierChalan = invOutTransactionDetails.supplierChalan
        invInventoryTransactionDetails.transactionDate = invInventoryTransaction.transactionDate
        invInventoryTransactionDetails.transactionDetailsId = invOutTransactionDetails.id
        invInventoryTransactionDetails.updatedBy = 0L
        invInventoryTransactionDetails.updatedOn = null
        invInventoryTransactionDetails.vehicleId = invOutTransactionDetails.vehicleId
        invInventoryTransactionDetails.vehicleNumber = invOutTransactionDetails.vehicleNumber

        invInventoryTransactionDetails.adjustmentParentId = 0L
        invInventoryTransactionDetails.approvedOn = null
        invInventoryTransactionDetails.isIncrease = true
        invInventoryTransactionDetails.transactionTypeId = invInventoryTransaction.transactionTypeId

        invInventoryTransactionDetails.isCurrent = true
        invInventoryTransactionDetails.fixedAssetId = 0L
        invInventoryTransactionDetails.fixedAssetDetailsId = 0L
        invInventoryTransactionDetails.overheadCost = 0.0d

        return invInventoryTransactionDetails
    }

    private static final String ITEM_LST_INV_OUT_QUERY = """
                SELECT iitd.id, iitd.version, (item.name ||'( ' ||iitd.actual_quantity || ' ' || item.unit || ')') AS name,
                iitd.actual_quantity AS quantity, item.unit
                FROM inv_inventory_transaction_details iitd
                LEFT JOIN item ON item.id = iitd.item_id
                WHERE iitd.inventory_transaction_id=:inventoryTransactionId
                AND iitd.acknowledged_by <= 0
                AND iitd.is_current = true
                AND item.is_individual_entity = false
                ORDER BY item.name ASC
    """

    /**
     * Get item list of inventory out transaction details
     * @param inventoryTransactionId -id of inventory transaction out object
     * @return -list of unacknowledged item of inventory transaction out
     */
    private List<GroovyRowResult> getItemListOfInventoryOut(long inventoryTransactionId) {
        Map queryParams = [inventoryTransactionId: inventoryTransactionId]
        List<GroovyRowResult> itemList = executeSelectSql(ITEM_LST_INV_OUT_QUERY, queryParams)
        return itemList
    }

    private static final String ACKNOWLEDGE_QUERY = """
            UPDATE inv_inventory_transaction_details
            SET
                version=:newVersion,
                acknowledged_by=:acknowledgedBy
            WHERE
                id=:id AND
                version=:version
    """

    /**
     * Acknowledge inventory out transaction details after inventory in
     * @param id -id of inventory transaction out details object
     * @param version - version of inventory transaction out details object
     * @param acknowledgedBy -id of appUser who created inventory transaction in details
     * @return -an integer containing the value of update count
     */
    private int acknowledgeOutInvTransactionDetails(long id, int version, long acknowledgedBy) {
        Map queryParams = [
                id: id,
                acknowledgedBy: acknowledgedBy,
                version: version,
                newVersion: version + 1
        ]
        int updateCount = executeUpdateSql(ACKNOWLEDGE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to update inventory transaction details")
        }
        return updateCount
    }

    private static final String ITEM_COUNT_QUERY = """
        UPDATE inv_inventory_transaction SET
            item_count = item_count + 1,
             version=version+1
        WHERE
              id=:inventoryTransactionId
    """

    /**
     * Increase item count of inventory transaction object(parent object)
     * @param inventoryTransactionId -id of inventory transaction object
     * @return -an integer containing the value of update count
     */
    private int increaseItemCount(long inventoryTransactionId) {
        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId
        ]
        int updateCount = executeUpdateSql(ITEM_COUNT_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to increase item count")
        }
        return updateCount
    }
}
