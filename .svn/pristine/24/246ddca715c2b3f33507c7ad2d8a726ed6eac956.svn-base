package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Get list of inventory(from inventory) of Inventory Out transaction by inventoryId(to inventory) for Inv-In-From-Inventory CRUD
 *  For details go through Use-Case doc named 'GetForInvListOfTransactionOutActionService'
 */
class GetForInvListOfTransactionOutActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to load inventory list"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String LST_INVENTORY = "lstInventory"

    /**
     * Check required parameters
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            // check required parameters
            if (!parameterMap.inventoryId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
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
     * Get list of inventory(from inventory) of Inventory Out transaction by inventoryId(to inventory)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            long inventoryId = Long.parseLong(parameterMap.inventoryId.toString())
            // get list of inventory(from inventory) of Inventory Out transaction by inventoryId(to inventory)
            List<GroovyRowResult> lstInventory = getInventoryListByInventoryId(inventoryId)

            result.put(LST_INVENTORY, Tools.listForKendoDropdown(lstInventory, null, null))
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
     * Get inventory list for drop down
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            List lstInventory = (List) executeResult.get(LST_INVENTORY)
            result = [lstInventory: lstInventory]
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

    private static final String INVENTORY_LIST_QUERY = """
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
                    iit2.transaction_entity_type_id=:transactionEntityTypeId AND
                    iit2.inventory_id=:transactionEntityId
                  )
        GROUP BY inv.id, inv.name
    """

    /**
     * Get list of inventory(from inventory) of Inventory Out transaction
     * @param transactionEntityId -inventoryId of Inventory-Out transaction (to inventory)
     * @return -a list of inventory containing name and id
     */
    public List<GroovyRowResult> getInventoryListByInventoryId(long transactionEntityId) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionTypeOut = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_OUT, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)

        Map queryParams = [
                transactionEntityId: transactionEntityId,
                transactionTypeIdOut: transactionTypeOut.id,
                transactionTypeIdIn: transactionTypeIn.id,
                transactionEntityTypeId: transactionEntityInventory.id,
        ]
        List<GroovyRowResult> result = executeSelectSql(INVENTORY_LIST_QUERY, queryParams)
        return result
    }
}