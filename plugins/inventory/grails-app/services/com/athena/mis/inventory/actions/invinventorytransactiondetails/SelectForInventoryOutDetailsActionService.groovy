package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Common action class to select invTranOutDetails object (Child) for both approved & unapproved and show in UI for editing
 *  For details go through Use-Case doc named 'SelectForInventoryOutDetailsActionService'
 */
class SelectForInventoryOutDetailsActionService extends BaseService implements ActionIntf {

    private static final String INV_OUT_NOT_FOUND_MESSAGE = "Inventory transaction details not found"
    private static final String FAILED_TO_SELECT = "Fail to select inventory out details"
    private static final String INV_OUT_DETAILS_OBJ = "InventoryTransactionDetails"
    private static final String TRANSACTION_DATE = "transactionDate"

    private final Logger log = Logger.getLogger(getClass())

    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired
    ItemCacheUtility itemCacheUtility

    /**
     * Get invTranOutDetails object (Child) by id
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap paramsMap = (GrailsParameterMap) params
            long inventoryTransactionDetailsId = Long.parseLong(paramsMap.id.toString())
            InvInventoryTransactionDetails invInventoryTransactionDetails = invInventoryTransactionDetailsService.read(inventoryTransactionDetailsId)
            if (!invInventoryTransactionDetails) {//Check existence of object
                result.put(Tools.MESSAGE, INV_OUT_NOT_FOUND_MESSAGE)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(INV_OUT_DETAILS_OBJ, invInventoryTransactionDetails)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_SELECT)
            return result
        }
    }

    /**
     * Build a map with invTranOutDetails object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            InvInventoryTransactionDetails invInventoryTransactionDetails = (InvInventoryTransactionDetails) preResult.get(INV_OUT_DETAILS_OBJ)

            result.put(Tools.ENTITY, invInventoryTransactionDetails)
            result.put(Tools.VERSION, invInventoryTransactionDetails.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_SELECT)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with invTranOutDetails object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
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
            result.put(Tools.MESSAGE, FAILED_TO_SELECT)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILED_TO_SELECT)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_SELECT)
            return result
        }
    }
}


