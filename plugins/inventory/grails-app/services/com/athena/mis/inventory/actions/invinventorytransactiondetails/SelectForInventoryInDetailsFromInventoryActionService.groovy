package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Select inventory transaction details object (Inventory In From Inventory) and show in UI for editing
 *  For details go through Use-Case doc named 'SelectForInventoryInDetailsFromInventoryActionService'
 */
class SelectForInventoryInDetailsFromInventoryActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String INVENTORY_IN_NOT_FOUND_MESSAGE = "Inventory transaction not found"
    private static final String SERVER_ERROR_MESSAGE = "Fail to get Inventory-In-Transaction"
    private static final String INVENTORY_TRANSACTION_DETAILS_OBJ = "inventoryTransactionDetails"
    private static final String TRANSACTION_DATE = "transactionDate"

    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    InvInventoryTransactionService invInventoryTransactionService

    /**
     * Get inventory transaction details object (Inventory In From Inventory) by id
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
            GrailsParameterMap paramsMap = (GrailsParameterMap) params
            long storeTransactionDetailsId = Long.parseLong(paramsMap.id.toString())
            // get inventory transaction details object (Inventory In From Inventory)
            InvInventoryTransactionDetails invInventoryTransactionDetails = invInventoryTransactionDetailsService.read(storeTransactionDetailsId)
            // check whether the inventory transaction details object exists or not
            if (!invInventoryTransactionDetails) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(INVENTORY_TRANSACTION_DETAILS_OBJ, invInventoryTransactionDetails)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build a map with inventory transaction details object & other related properties to show on UI
     * @param parameters -N/A
     * @param obj -map returned from executePrecondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) preResult.get(INVENTORY_TRANSACTION_DETAILS_OBJ)
            result.put(Tools.ENTITY, invInventoryTransactionDetails)
            result.put(Tools.VERSION, invInventoryTransactionDetails.version)
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

            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) executeResult.get(Tools.ENTITY)
            String transactionDate = DateUtility.getDateForUI(invInventoryTransactionDetails.transactionDate)

            result.put(Tools.ENTITY, invInventoryTransactionDetails)
            result.put(TRANSACTION_DATE, transactionDate)
            result.put(Tools.VERSION, invInventoryTransactionDetails.version)
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
            LinkedHashMap previousResult = (LinkedHashMap) obj  // cast map returned from previous method
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
}
