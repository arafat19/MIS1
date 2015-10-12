package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvInventoryTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new inventory transaction(Inventory In From Inventory) to receive item from another inventory and show in grid
 *  For details go through Use-Case doc named 'CreateForInventoryInFromInventoryActionService'
 */
class CreateForInventoryInFromInventoryActionService extends BaseService implements ActionIntf {

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private static final String INVENTORY_IN_SAVE_SUCCESS_MESSAGE = "Inventory-In has been created successfully"
    private static final String INVENTORY_SAVE_FAILURE_MESSAGE = "Can not save Inventory-In"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String SERVER_ERROR_MESSAGE = "Fail to create Inventory-In"
    private static final String TRANSACTION_EXIST = "This transaction already exists"
    private static final String INVENTORY_TRANSACTION_OBJ = "inventoryTransaction"
    private static final String INVENTORY_TRANSACTION_OUT_OBJ = "inventoryTransactionOut"
    private static final String INV_INVENTORY_OBJ = "invInventory"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Checking pre condition and building inventory transaction object with parameters from UI
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
            if ((!parameterMap.transactionDate) || (!parameterMap.transactionId) || (!parameterMap.transactionEntityId) || (!parameterMap.inventoryId)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            long inventoryId = Long.parseLong(parameterMap.inventoryId)
            InvInventory invInventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
            long companyId = invSessionUtil.appSessionUtil.getCompanyId()
            //get inventory out transaction
            long invTransactionOutId = Long.parseLong(parameterMap.transactionId)
            InvInventoryTransaction invInventoryTransactionOut = invInventoryTransactionService.read(invTransactionOutId)
            // build inventory transaction object (Inventory In From Inventory)
            InvInventoryTransaction invInventoryTransaction = buildInvInventoryTransactionObject(parameterMap, invInventory, invInventoryTransactionOut, companyId)

            //Check validation
            invInventoryTransaction.validate()
            if (invInventoryTransaction.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            int countExistingTransaction = countTransactionByTransactionId(invInventoryTransaction.inventoryId, invInventoryTransaction.transactionId, companyId)
            // check whether this transaction already exists or not
            if (countExistingTransaction >= 1) {
                result.put(Tools.MESSAGE, TRANSACTION_EXIST)
                return result
            }

            result.put(INVENTORY_TRANSACTION_OBJ, invInventoryTransaction)
            result.put(INVENTORY_TRANSACTION_OUT_OBJ, invInventoryTransactionOut)
            result.put(INV_INVENTORY_OBJ, invInventory)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Create inventory transaction object (Inventory In From Inventory)
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

            InvInventory invInventory = (InvInventory) preResult.get(INV_INVENTORY_OBJ)
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) preResult.get(INVENTORY_TRANSACTION_OBJ)
            // create inventory transaction object
            InvInventoryTransaction returnInvInventoryTransaction = invInventoryTransactionService.create(invInventoryTransaction)
            if (!returnInvInventoryTransaction) {
                result.put(Tools.MESSAGE, INVENTORY_SAVE_FAILURE_MESSAGE)
                return result
            }

            result.put(INVENTORY_TRANSACTION_OBJ, returnInvInventoryTransaction)
            result.put(INVENTORY_TRANSACTION_OUT_OBJ, preResult.get(INVENTORY_TRANSACTION_OUT_OBJ))
            result.put(INV_INVENTORY_OBJ, invInventory)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(SERVER_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
     * Show newly created inventory transaction object in grid
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

            InvInventory toInventory = (InvInventory) executeResult.get(INV_INVENTORY_OBJ)
            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) executeResult.get(INVENTORY_TRANSACTION_OBJ)
            InvInventoryTransaction invInventoryTransactionOut = (InvInventoryTransaction) executeResult.get(INVENTORY_TRANSACTION_OUT_OBJ)

            String transactionDate = DateUtility.getLongDateForUI(invInventoryTransaction.transactionDate)
            String transferDate = DateUtility.getLongDateForUI(invInventoryTransactionOut.transactionDate)
            InvInventory fromInventory = (InvInventory) invInventoryCacheUtility.read(invInventoryTransaction.transactionEntityId)
            SystemEntity inventoryType = (SystemEntity) invInventoryTypeCacheUtility.read(invInventoryTransaction.inventoryTypeId)
            SystemEntity transactionEntityType = (SystemEntity) invInventoryTypeCacheUtility.read(fromInventory.typeId)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(invInventoryTransaction.createdBy)

            GridEntity object = new GridEntity()    // build grid object
            object.id = invInventoryTransaction.id
            object.cell = [
                    Tools.LABEL_NEW,
                    invInventoryTransaction.id,
                    transferDate,
                    transactionDate,
                    inventoryType.key + Tools.COLON + toInventory.name,
                    transactionEntityType.key + Tools.COLON + fromInventory.name,
                    invInventoryTransaction.itemCount,
                    Tools.STR_ZERO,
                    createdBy.username,
                    Tools.EMPTY_SPACE,
                    invInventoryTransactionOut.id
            ]

            result.put(Tools.MESSAGE, INVENTORY_IN_SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
                result.put(Tools.MESSAGE, INVENTORY_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build inventory transaction object (Inventory In From Inventory)
     * @param parameterMap -serialized parameters from UI
     * @param invInventory -inventory object (to inventory)
     * @param inventoryTransactionOut -inventory transaction out object
     * @param companyId -id of company
     * @return -new inventory transaction object
     */
    private InvInventoryTransaction buildInvInventoryTransactionObject(GrailsParameterMap parameterMap, InvInventory invInventory, InvInventoryTransaction inventoryTransactionOut,
                                                                                 long companyId) {
        InvInventoryTransaction invInventoryTransaction = new InvInventoryTransaction()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)

        invInventoryTransaction.version = 0
        invInventoryTransaction.transactionTypeId = transactionTypeIn.id
        invInventoryTransaction.transactionEntityTypeId = transactionEntityInventory.id
        invInventoryTransaction.transactionEntityId = inventoryTransactionOut.inventoryId
        invInventoryTransaction.transactionId = inventoryTransactionOut.id
        invInventoryTransaction.projectId = invInventory.projectId
        invInventoryTransaction.createdOn = new Date()
        invInventoryTransaction.createdBy = invSessionUtil.appSessionUtil.getAppUser().id
        invInventoryTransaction.updatedOn = null
        invInventoryTransaction.updatedBy = 0L
        invInventoryTransaction.comments = parameterMap.comments ? parameterMap.comments : null
        invInventoryTransaction.inventoryTypeId = invInventory.typeId
        invInventoryTransaction.inventoryId = invInventory.id
        invInventoryTransaction.budgetId = inventoryTransactionOut.budgetId
        invInventoryTransaction.itemCount = 0
        invInventoryTransaction.companyId = companyId
        invInventoryTransaction.transactionDate = DateUtility.parseMaskedFromDate(parameterMap.transactionDate)

        return invInventoryTransaction
    }

    private static final String TRANSACTION_COUNT_QUERY = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction
            WHERE inventory_id=:inventoryId AND
            transaction_id=:transactionId AND
            transaction_type_id=:transactionTypeId AND
            transaction_entity_type_id=:transactionEntityTypeId
    """

    /**
     * Count number of transaction by transactionId and inventoryId
     * @param inventoryId -id of inventory
     * @param transactionId -id of inventory transaction out
     * @param companyId -id of company
     * @return -an integer containing the value of count
     */
    private int countTransactionByTransactionId(long inventoryId, long transactionId, long companyId) {
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)
        Map queryParams = [
                inventoryId: inventoryId,
                transactionId: transactionId,
                transactionTypeId: transactionTypeIn.id,
                transactionEntityTypeId: transactionEntityInventory.id
        ]
        List results = executeSelectSql(TRANSACTION_COUNT_QUERY, queryParams)
        int count = results[0].count
        return count
    }
}