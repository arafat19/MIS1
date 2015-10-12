package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Reverse adjustment of approved inventory out transaction details object and remove form grid
 *  For details go through Use-Case doc named 'ReverseAdjustForInvOutActionService'
 */
class ReverseAdjustForInvOutActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String REVERSE_ADJ_SAVE_SUCCESS_MESSAGE = "Reverse adjustment has been saved successfully"
    private static final String REVERSE_ADJ_SAVE_FAILURE_MESSAGE = "Can not save the reverse adjustment"
    private static final String INVENTORY_REVERSE_ADJ_OBJ = "inventoryTransaction"
    private static final String INVENTORY_CURRENT_OBJ = "currentDetails"
    private static final String INVALID_ENTRY = "Error occurred due to invalid input"
    private static final String NOT_APPROVED = "Reverse adjustment not required for unapproved inventory transaction"
    private static final String INV_INVENTORY_TRANSACTION_NOT_FOUND = "Inventory transaction not found"
    private static final String ALREADY_ACK_ERROR = "Acknowledged transaction is not reversible"
    private static final String COMMENTS_NOT_FOUND = "Comments not found"
    private static final String REVERSE_NOT_ALLOWED = "Reverse adjustment not allowed for this item"
    private static final String REVERSED = "reversed"

    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility

    /**
     * Checking pre condition before doing reverse adjustment of transaction details object (Inventory Out)
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
            // check required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INVALID_ENTRY)
                return result
            }

            long adjustmentParentId = Long.parseLong(parameterMap.id.toString())
            String comments = parameterMap.comments
            // check if comments for reverse adjustment exists or not
            if (!comments) {
                result.put(Tools.MESSAGE, COMMENTS_NOT_FOUND)
                return result
            }

            int version = Integer.parseInt(parameterMap.version.toString())
            // get inventory transaction details object
            InvInventoryTransactionDetails currentTransactionDetails = invInventoryTransactionDetailsService.read(adjustmentParentId)
            // check if inventory transaction details object exists or not
            if (!currentTransactionDetails || currentTransactionDetails.version != version || !currentTransactionDetails.isCurrent) {
                result.put(Tools.MESSAGE, INV_INVENTORY_TRANSACTION_NOT_FOUND)
                return result
            }
            // reverse adjustment can not be done for unapproved transaction details object
            if (currentTransactionDetails.approvedBy <= 0) {
                result.put(Tools.MESSAGE, NOT_APPROVED)
                return result
            }
            // reverse adjustment can not be done for already acknowledged transaction details object
            if (currentTransactionDetails.acknowledgedBy > 0) {
                result.put(Tools.MESSAGE, ALREADY_ACK_ERROR)
                return result
            }
            // reverse adjustment is not applicable for items which are individual entity
            Item item = (Item) itemCacheUtility.read(currentTransactionDetails.itemId)
            if (item.isIndividualEntity) {
                result.put(Tools.MESSAGE, REVERSE_NOT_ALLOWED)
                return result
            }
            // copy current transaction details object for reverse adjustment
            InvInventoryTransactionDetails reverseTransDetails = copyForReverseAdjustment(currentTransactionDetails, comments, invSessionUtil.appSessionUtil.getAppUser())
            // update necessary properties of current transaction details object for reverse adjustment (isCurrent = false)
            currentTransactionDetails.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
            currentTransactionDetails.updatedOn = new Date()
            currentTransactionDetails.isCurrent = false

            result.put(INVENTORY_REVERSE_ADJ_OBJ, reverseTransDetails)
            result.put(INVENTORY_CURRENT_OBJ, currentTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REVERSE_ADJ_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Create reverse inventory transaction details object of current object (isCurrent = false)
     * Set isCurrent false of current inventory transaction details object
     * Decrease item count of inventory transaction out object (parent object)
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value

            InvInventoryTransactionDetails currentTransactionDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_CURRENT_OBJ)
            InvInventoryTransactionDetails reverseAdjDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_REVERSE_ADJ_OBJ)
            // create reverse inventory transaction details object (isCurrent = false)
            invInventoryTransactionDetailsService.create(reverseAdjDetails)
            // set isCurrent false of current inventory transaction details object
            setIsCurrentTransaction(currentTransactionDetails)

            // decrease item count of inventory transaction out object (parent object)
            decreaseItemCount(currentTransactionDetails.inventoryTransactionId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(REVERSE_ADJ_SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REVERSE_ADJ_SAVE_FAILURE_MESSAGE)
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
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(REVERSED, Boolean.TRUE.booleanValue())
            result.put(Tools.MESSAGE, REVERSE_ADJ_SAVE_SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REVERSE_ADJ_SAVE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, REVERSE_ADJ_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REVERSE_ADJ_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    private static final String INVENTORY_TRANSACTION_DETAILS_UPDATE_QUERY = """
                      UPDATE inv_inventory_transaction_details
                      SET is_current=false,
                        version=:newVersion,
                        updated_by =:updatedBy,
                        updated_on =:updatedOn
                      WHERE id =:id AND
                            version=:version
    """

    /**
     * Set isCurrent false of inventory transaction details object
     * @param invInventoryTransactionDetails -inventory transaction details object
     * @return -an integer containing the value of update count
     */
    private int setIsCurrentTransaction(InvInventoryTransactionDetails invInventoryTransactionDetails) {
        Map queryParams = [
                id: invInventoryTransactionDetails.id,
                version: invInventoryTransactionDetails.version,
                newVersion: invInventoryTransactionDetails.version + 1,
                updatedBy: invInventoryTransactionDetails.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(invInventoryTransactionDetails.updatedOn)
        ]
        int updateCount = executeUpdateSql(INVENTORY_TRANSACTION_DETAILS_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update inventory transaction details')
        }
        return updateCount
    }

    private static final String DECREASE_ITEM_COUNT_QUERY = """
        UPDATE inv_inventory_transaction SET
            item_count = item_count - 1,
            version=version+1
        WHERE
            id=:inventoryTransactionId
    """

    /**
     * Decrease item count of inventory transaction object
     * @param inventoryTransactionId -id of inventory transaction object
     * @return -an integer containing the value of update count
     */
    private int decreaseItemCount(long inventoryTransactionId) {
        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId
        ]
        int updateCount = executeUpdateSql(DECREASE_ITEM_COUNT_QUERY, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException("Fail to decrease item count")
        }
        return updateCount
    }

    private InvInventoryTransactionDetails copyForReverseAdjustment(InvInventoryTransactionDetails oldConsumpDetails, String comments, AppUser user) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeRevAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_REVERSE_ADJUSTMENT, companyId)


        InvInventoryTransactionDetails newDetails = new InvInventoryTransactionDetails()
        newDetails.id = 0
        newDetails.version = 0
        newDetails.inventoryTransactionId = oldConsumpDetails.inventoryTransactionId
        newDetails.transactionDetailsId = 0
        newDetails.inventoryTypeId = oldConsumpDetails.inventoryTypeId
        newDetails.inventoryId = oldConsumpDetails.inventoryId
        newDetails.approvedBy = user.id
        newDetails.itemId = oldConsumpDetails.itemId
        newDetails.vehicleId = 0
        newDetails.vehicleNumber = null
        newDetails.suppliedQuantity = oldConsumpDetails.suppliedQuantity
        newDetails.actualQuantity = oldConsumpDetails.actualQuantity
        newDetails.shrinkage = oldConsumpDetails.shrinkage
        newDetails.rate = oldConsumpDetails.rate                     // same as parents rate
        newDetails.supplierChalan = null
        newDetails.stackMeasurement = null
        newDetails.fifoQuantity = 0
        newDetails.lifoQuantity = 0
        newDetails.acknowledgedBy = 0
        newDetails.createdOn = new Date()
        newDetails.createdBy = user.id
        newDetails.updatedOn = null
        newDetails.updatedBy = 0
        newDetails.comments = comments
        newDetails.mrfNo = null
        newDetails.transactionDate = oldConsumpDetails.transactionDate
        newDetails.fixedAssetId = 0
        newDetails.fixedAssetDetailsId = 0
        newDetails.transactionTypeId = transactionTypeRevAdj.id              // TRANSACTION_TYPE_REVERSE_ADJUSTMENT
        newDetails.adjustmentParentId = oldConsumpDetails.adjustmentParentId == 0 ? oldConsumpDetails.id : oldConsumpDetails.adjustmentParentId
        newDetails.approvedOn = new Date()
        newDetails.isIncrease = !(oldConsumpDetails.isIncrease)
        newDetails.isCurrent = false
        newDetails.overheadCost = oldConsumpDetails.overheadCost
        newDetails.invoiceAcknowledgedBy = oldConsumpDetails.invoiceAcknowledgedBy
        return newDetails
    }
}