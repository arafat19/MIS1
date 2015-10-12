package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class for reverse adjustment operation of InventoryConsumptionDetails(Child)
 *  For details go through Use-Case doc named 'ReverseAdjustmentForInvConsumptionActionService'
 */
class ReverseAdjustmentForInvConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String REVERSE_ADJUSTMENT_SUCCESS_MESSAGE = "Reverse adjustment has been saved successfully"
    private static final String REVERSE_ADJUSTMENT_FAILURE_MESSAGE = "Can not saved reverse adjustment"
    private static final String INVENTORY_REVERSE_ADJ_OBJ = "inventoryTransaction"
    private static final String INVENTORY_CURRENT_OBJ = "currentDetails"
    private static final String INVALID_ENTRY = "Error occurred due to invalid input"
    private static final String NOT_APPROVED = "Adjustment(s) not required for unapproved inventory Transaction"
    private static final String INV_INVENTORY_TRANSACTION_NOT_FOUND = "Inventory Transaction not found"
    private static final String DELETED = "deleted"
    private static final String REVERSE_NOT_ALLOWED = "Reverse Adjustment not allowed for this item"
    private static final String NOT_PROJECT_MANAGER = "You are not allowed to reverse this consumption"

    InvInventoryTransactionService invInventoryTransactionService
    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility

    /**
     * validate different criteria to reverse-adjustment of InventoryConsumptionDetails(child). Such as :
     *      Check parameters
     *      Check if login user has ProjectManager Role or not
     *      Check existence of inventoryConsumptionDetails
     *      Check approval of child
     *      Check existence of inventoryConsumption(Parent)
     *      Check existence of corresponding BudgetDetails
     *
     * @Params parameters -Receives from UI
     * @Params obj -N/A
     *
     * @Return -a map containing 2 inventoryTransactionDetails(currentTranDetails, reverseTranDetails) object for execute
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            //check if login user has ProjectManager Role or not(Only ProjectManager can reverseAdjustConsumption)
            if (!hasRoleProjectManager()) {
                result.put(Tools.MESSAGE, NOT_PROJECT_MANAGER)
                return result
            }

            //check parameters
            if (!parameterMap.id || !parameterMap.comments) {
                result.put(Tools.MESSAGE, INVALID_ENTRY)
                return result
            }

            long adjustmentParentId = Long.parseLong(parameterMap.id.toString())
            int version = Integer.parseInt(parameterMap.version.toString())

            InvInventoryTransactionDetails currentTransactionDetails = invInventoryTransactionDetailsService.read(adjustmentParentId)
            //check existence of parentObject, version matching, and isCurrent
            if ((!currentTransactionDetails) || (currentTransactionDetails.version != version) || (!currentTransactionDetails.isCurrent)) {
                result.put(Tools.MESSAGE, INV_INVENTORY_TRANSACTION_NOT_FOUND)
                return result
            }

            if (currentTransactionDetails.approvedBy <= 0) {//unapproved transaction could not be reversed
                result.put(Tools.MESSAGE, NOT_APPROVED)
                return result
            }

            Item item = (Item) itemCacheUtility.read(currentTransactionDetails.itemId)
            if (item.isIndividualEntity) { //item not allowed for item which isIndividualEntity=true(For FixedAsset)
                result.put(Tools.MESSAGE, REVERSE_NOT_ALLOWED)
                return result
            }

            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(currentTransactionDetails.inventoryTransactionId)
            Object budgetDetails = (Object) budgetImplService.readBudgetDetailsByBudgetAndItem(invInventoryTransaction.budgetId, currentTransactionDetails.itemId)
            if (!budgetDetails) { //check existence of corresponding budgetDetails object
                result.put(Tools.MESSAGE, INVALID_ENTRY)
                return result
            }

            String comments = parameterMap.comments.toString()
            //build reverseTransactionDetails object
            InvInventoryTransactionDetails reverseTransDetails = copyForReverseAdjustment(currentTransactionDetails, comments, invSessionUtil.appSessionUtil.getAppUser())

            currentTransactionDetails.updatedBy = invSessionUtil.appSessionUtil.getAppUser().id
            currentTransactionDetails.updatedOn = new Date()
            currentTransactionDetails.isCurrent = false

            result.put(INVENTORY_REVERSE_ADJ_OBJ, reverseTransDetails)
            result.put(INVENTORY_CURRENT_OBJ, currentTransactionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REVERSE_ADJUSTMENT_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     *  Method to reverse InventoryConsumptionDetails(child)
     *
     * @Params parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains 2 inventoryTransactionDetails(currentTranDetails, reverseTranDetails) object
     * @Return -a map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            InvInventoryTransactionDetails currentTransactionDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_CURRENT_OBJ)
            InvInventoryTransactionDetails reverseAdjDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_REVERSE_ADJ_OBJ)

            //create reverseTransactionDetails object
            InvInventoryTransactionDetails newReverseAdjDetails = invInventoryTransactionDetailsService.create(reverseAdjDetails)

            //set isCurrent = FALSE of which transaction has been adjusted
            int updateInvInventoryTransaction = setIsCurrentTransaction(currentTransactionDetails)

            //decrease itemCount of parent transaction
            decreaseItemCount(currentTransactionDetails.inventoryTransactionId)

            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(newReverseAdjDetails.inventoryTransactionId)

            // increase total consumption at budget details
            double totalConsumedQuantity = getTotalConsumedQuantity(invInventoryTransaction.budgetId, newReverseAdjDetails.itemId)
            int updateConsumption = increaseTotalConsumption(invInventoryTransaction.budgetId, newReverseAdjDetails.itemId, totalConsumedQuantity )

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to reverse adjustment inventory consumption')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REVERSE_ADJUSTMENT_FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * method to show success message
     * @param obj -N/A
     * @return -a map containing all objects necessary for showing message at UI
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(DELETED, Boolean.TRUE.booleanValue())
            result.put(Tools.MESSAGE, REVERSE_ADJUSTMENT_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REVERSE_ADJUSTMENT_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to reverse inventoryConsumptionDetails(Child)
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, REVERSE_ADJUSTMENT_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REVERSE_ADJUSTMENT_FAILURE_MESSAGE)
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
     * update previous inv_inventory_transaction_details at the time of adjustment(set isCurrent = false)
     * @param invTransactionDetails -invInventoryTransactionDetails object which will be reversed
     * @return -updateCount(if updateCount<=0, then rollback whole transaction)
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

    private static final String DECRE_ITEM_COUNT_QUERY = """
        UPDATE inv_inventory_transaction SET
            item_count = item_count - 1,
            version=version+1
        WHERE
            id=:inventoryTransactionId
    """

    // method decrease item count
    /**
     * method to decrease itemCount at parent object
     * @param inventoryTransactionId -(InvInventoryTransaction.id)parent id
     * @return -updateCount(if updateCount<=0, then rollback whole transaction)
     */
    private int decreaseItemCount(long inventoryTransactionId) {
        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId
        ]
        int updateCount = executeUpdateSql(DECRE_ITEM_COUNT_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to decrease item count")
        }
        return updateCount
    }

    /**
     * Method to check that if login user has projectManager role or not
     * @return -return TRUE if loginUser has projectManager role; otherwise return FALSE
     */
    private boolean hasRoleProjectManager() {
        List<Long> userRoleList = invSessionUtil.appSessionUtil.getUserRoleIds()
        for (int i = 0; i < userRoleList.size(); i++) {
            if (userRoleList[i] == roleTypeCacheUtility.ROLE_TYPE_PROJECT_MANAGER) {
                return true
            }
        }
        return false
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

    private static final String TOTAL_APPROVED_CONSUMED_QUANTITY_QUERY = """
            SELECT  coalesce(sum(iitd.actual_quantity),0) AS total
        FROM inv_inventory_transaction_details iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
        WHERE iit.budget_id=:budgetId
            AND iitd.item_id=:itemId
            AND iit.transaction_type_id=:transactionTypeId
            AND iitd.approved_by > 0
            AND iitd.is_current = true
    """
    /**
     * Method to check totalConsumedAmount of selected item against budget
     * @Param budgetId -Budget.id
     * @Param itemId -Item.id
     *
     * @Return -double value (totalConsumedQuantity)
     */
    private double getTotalConsumedQuantity(long budgetId, long itemId) {
        // pull transactionType object
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        Map queryParams = [
                transactionTypeId: transactionTypeCons.id,
                budgetId: budgetId,
                itemId: itemId
        ]
        List<GroovyRowResult> invInventoryTransactionDetailsList = executeSelectSql(TOTAL_APPROVED_CONSUMED_QUANTITY_QUERY, queryParams)
        double totalConsumedQuantity = Double.parseDouble(invInventoryTransactionDetailsList[0].total.toString())
        return totalConsumedQuantity
    }

    private static final String INCREASE_TOTAL_CONSUMPTION_QUERY = """
        UPDATE budg_budget_details SET
            total_consumption = :totalConsumedQuantity,
            version=version+1
        WHERE
            budget_id = :budgetId
            AND item_id = :itemId
    """
    /**
     * method increase total consumption of parent
     * @Param budgetId -
     * @Param itemId -
     * @Return int -
     */
    private int increaseTotalConsumption(long budgetId, long itemId, double totalConsumedQuantity) {
        Map queryParams = [
                budgetId: budgetId,
                itemId: itemId,
                totalConsumedQuantity: totalConsumedQuantity
        ]
        int updateTotalConsumption = executeUpdateSql(INCREASE_TOTAL_CONSUMPTION_QUERY, queryParams)

        if (updateTotalConsumption <= 0) {
            throw new RuntimeException("Fail to increase total consumption")
        }
        return updateTotalConsumption
    }
}

