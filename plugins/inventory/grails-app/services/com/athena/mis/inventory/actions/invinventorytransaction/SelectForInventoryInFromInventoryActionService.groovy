package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select inventory transaction object (Inventory In From Inventory) and show in UI for editing
 *  For details go through Use-Case doc named 'SelectForInventoryInFromInventoryActionService'
 */
class SelectForInventoryInFromInventoryActionService extends BaseService implements ActionIntf {

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String INVENTORY_IN_NOT_FOUND_MESSAGE = "Inventory-In transaction not found"
    private static final String SERVER_ERROR_MESSAGE = "Fail to load Inventory-In-Transaction"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String INVENTORY_TRANSACTION_OBJ = "inventoryTransaction"
    private static final String INVENTORY_IN_MAP = "inventoryInMap"

    /**
     * Get inventory transaction object (Inventory In From Inventory) by id
     * @param params -parameters from UI
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
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            long id = Long.parseLong(parameterMap.id.toString())
            // get inventory transaction object (Inventory In From Inventory)
            InvInventoryTransaction invInventoryTransaction = invInventoryTransactionService.read(id)
            // check whether the inventory transaction object exists or not
            if (!invInventoryTransaction) {
                result.put(Tools.MESSAGE, INVENTORY_IN_NOT_FOUND_MESSAGE)
                return result
            }

            result.put(INVENTORY_TRANSACTION_OBJ, invInventoryTransaction)
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
     * Build a map with inventory transaction object & other related properties to show on UI
     * @param parameters - N/A
     * @param obj -map returned from executePrecondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            InvInventoryTransaction invInventoryTransaction = (InvInventoryTransaction) preResult.get(INVENTORY_TRANSACTION_OBJ)
            Map inventoryInMap = buildInvInMap(invInventoryTransaction) // build map with necessary properties to show on UI

            result.put(Tools.ENTITY, invInventoryTransaction)
            result.put(Tools.VERSION, invInventoryTransaction.version)
            result.put(INVENTORY_IN_MAP, inventoryInMap)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with necessary objects to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(INVENTORY_IN_MAP, executeResult.get(INVENTORY_IN_MAP))
            result.put(Tools.ENTITY, executeResult.get(Tools.ENTITY))
            result.put(Tools.VERSION, executeResult.get(Tools.VERSION))
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
                result.put(Tools.MESSAGE, INVENTORY_IN_NOT_FOUND_MESSAGE)
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
     * Build a map with necessary properties of inventory transaction object
     * @param invInventoryTransaction -inventory transaction object (Inventory In From Inventory)
     * @return -a map containing necessary properties to show on UI
     */
    private Map buildInvInMap(InvInventoryTransaction invInventoryTransaction) {
        Map transactionMap = [
                transactionDate: DateUtility.getDateForUI(invInventoryTransaction.transactionDate),
                invnetoryList: invSessionUtil.getUserInventoriesByType(invInventoryTransaction.inventoryTypeId),
                transactionEntityList: getInventoryListByInventoryIdForEdit(invInventoryTransaction.inventoryId, invInventoryTransaction.transactionId),
                transactionList: getInvTransactionListByInventoryIdForEdit(invInventoryTransaction.inventoryId, invInventoryTransaction.transactionEntityId, invInventoryTransaction.transactionId)
        ]
        return transactionMap
    }

    private static final String GET_INV_OUT_QUERY = """
        SELECT inv.name, inv.id
        FROM inv_inventory_transaction iit
        LEFT JOIN inv_inventory inv ON inv.id  = iit.inventory_id
            WHERE iit.transaction_type_id=:transactionTypeIdOut AND
                  iit.transaction_entity_type_id=:transactionEntityTypeId AND
                  iit.transaction_entity_id=:transactionEntityId AND
                  iit.item_count > 0 AND
                  iit.id NOT IN (
                    SELECT iit2.transaction_id FROM inv_inventory_transaction iit2
                    WHERE iit2.transaction_type_id=:transactionTypeIdIn AND
                    iit2.transaction_entity_type_id=:transactionEntityTypeId
                    AND iit2.transaction_id <> :transactionId
                    AND iit2.inventory_id=:transactionEntityId
                  )
        GROUP BY inv.id, inv.name
    """

    /**
     * Get inventory list of inventory-out for edit (from inventory)
     * @param transactionEntityId -inventoryId of Inventory-Out transaction
     * @param transactionId -id of transaction out object
     * @return -list of inventory out
     */
    private List<GroovyRowResult> getInventoryListByInventoryIdForEdit(long transactionEntityId, long transactionId) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionTypeOut = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_OUT, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)
        Map queryParams = [
                transactionId: transactionId,
                transactionEntityId: transactionEntityId,
                transactionTypeIdOut: transactionTypeOut.id,
                transactionTypeIdIn: transactionTypeIn.id,
                transactionEntityTypeId: transactionEntityInventory.id
        ]
        List<GroovyRowResult> result = executeSelectSql(GET_INV_OUT_QUERY, queryParams)
        return result
    }

    private static final String INV_TRANSACTION_DATE_QUERY = """
        SELECT iit.id, to_char(iit.transaction_date, 'dd-MON-YYYY') AS transaction_date_str,
                       to_char(iit.transaction_date, 'dd/mm/YYYY') AS transfer_date
        FROM inv_inventory_transaction iit
            WHERE iit.transaction_type_id=:transactionTypeIdOut AND
                  iit.transaction_entity_type_id=:transactionEntityTypeId AND
                  iit.inventory_id=:inventoryId AND
                  iit.transaction_entity_id=:transactionEntityId AND
                  iit.item_count > 0 AND
                  iit.id NOT IN (
                    SELECT iit2.transaction_id FROM inv_inventory_transaction iit2
                    WHERE iit2.transaction_type_id=:transactionTypeIdIn AND
                    iit2.transaction_entity_type_id=:transactionEntityTypeId
                    AND iit2.transaction_id <> :transactionId
                    AND iit2.inventory_id=:transactionEntityId

                  )
        GROUP BY iit.id, iit.transaction_date
    """

    /**
     * Get list of transaction date for edit (from inventory)
     * @param transactionEntityId -inventoryId of Inventory-Out transaction
     * @param transactionId -id of transaction out object
     * @return -list of transaction date
     */
    private List<GroovyRowResult> getInvTransactionListByInventoryIdForEdit(long transactionEntityId, long inventoryId, long transactionId) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionTypeOut = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_OUT, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)
        Map queryParams = [
                inventoryId: inventoryId,
                transactionId: transactionId,
                transactionEntityId: transactionEntityId,
                transactionTypeIdOut: transactionTypeOut.id,
                transactionTypeIdIn: transactionTypeIn.id,
                transactionEntityTypeId: transactionEntityInventory.id
        ]
        List<GroovyRowResult> result = executeSelectSql(INV_TRANSACTION_DATE_QUERY, queryParams)
        return result
    }
}

