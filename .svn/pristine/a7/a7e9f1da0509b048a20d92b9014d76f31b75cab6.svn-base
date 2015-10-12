package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select inventory transaction object (Inventory In From Supplier) and show in UI for editing
 *  For details go through Use-Case doc named 'SelectForInventoryInFromSupplierActionService'
 */
class SelectForInventoryInFromSupplierActionService extends BaseService implements ActionIntf {

    InvInventoryTransactionService invInventoryTransactionService
    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired
    InvSessionUtil invSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String INVENTORY_IN_NOT_FOUND_MESSAGE = "Inventory-In transaction not found"
    private static final String SERVER_ERROR_MESSAGE = "Fail to load Inventory-In-Transaction"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String INVENTORY_TRANSACTION_OBJ = "inventoryTransaction"
    private static final String INVENTORY_IN_MAP = "inventoryInMap"
    private static final String PO_NO_LABEL = "PO No "

    /**
     * Get inventory transaction object (Inventory In From Supplier) by id
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
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
            // get inventory transaction object (Inventory In From Supplier)
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

            long purchaseOrderId = invInventoryTransaction.transactionId
            long supplierId = invInventoryTransaction.transactionEntityId
            long projectId = invInventoryTransaction.projectId
            long inventoryTypeId = invInventoryTransaction.inventoryTypeId
            // build PO object map by transactionId (PO id) of this inventory transaction object
            Map purchaseOrder = [id: invInventoryTransaction.transactionId,
                    name: PO_NO_LABEL + invInventoryTransaction.transactionId
            ]
            // get purchase order list for drop down excluding the PO of this transaction
            List purchaseOrderList
            purchaseOrderList = wrapPOList(procurementImplService.listPOBySupplierIdAndProjectIdForEdit(supplierId, purchaseOrderId, projectId))
            purchaseOrderList << purchaseOrder  // append PO object of this transaction
            // build inventory transaction map for show on UI
            Map inventoryInMap = [
                    transactionDate: DateUtility.getDateForUI(invInventoryTransaction.transactionDate),
                    inventoryList: invSessionUtil.getUserInventoriesByType(inventoryTypeId),
                    purchaseOrderList: purchaseOrderList
            ]

            result.put(Tools.ENTITY, invInventoryTransaction)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }

    /**
     * Get custom list of PO for drop down
     * @param lstPO -list of PO
     * @return -custom list of PO for drop down
     */
    private wrapPOList(List<Object> lstPO) {
        List lstCustomPO = []
        int counter = start + 1
        for (int i = 0; i < lstPO.size(); i++) {
            lstCustomPO << [id: lstPO[i].poId, name: lstPO[i].strPoId]
            counter++
        }
        return lstCustomPO
    }
}

