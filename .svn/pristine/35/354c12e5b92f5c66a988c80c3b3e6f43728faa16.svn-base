package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Get purchase order list by supplierId and projectId for drop down, used in Inv-In-From-Supplier CRUD
 * For details go through Use-Case doc named 'GetPurchaseOrderActionService'
 */
class GetPurchaseOrderActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String SERVER_ERROR_MESSAGE = "Fail to load Purchase Order List"
    private static final String DEFAULT_ERROR_MESSAGE = "Can't Load Purchase Order List"
    private static final String ENTITY_NOT_FOUND_ERROR_MESSAGE = "No entity found with this id or might have been deleted by someone"
    private static final String LST_PURCHASE_ORDER = "lstPurchaseOrder"
    private static final String INVENTORY_NOT_FOUND = "Inventory not found"
    private static final String PROJECT_ID = "projectId"
    private static final String PO_NO_LABEL = "PO No "

    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility

    /**
     * Check pre condition
     *  -Check required parameters
     *  -Check if inventory exists or not
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            // check required parameters
            if ((!parameterMap.supplierId) || (!parameterMap.inventoryId)) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            long inventoryId = Long.parseLong(parameterMap.inventoryId.toString())
            InvInventory invInventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
            // check if inventory exists or not
            if (!invInventory) {
                result.put(Tools.MESSAGE, INVENTORY_NOT_FOUND)
                return result
            }

            result.put(PROJECT_ID, invInventory.projectId)
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
     * Get list of purchase order for drop down
     * @param parameters -serialized parameters from UI
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            long projectId = Long.parseLong(preResult.get(PROJECT_ID).toString())
            long supplierId = Long.parseLong(parameterMap.supplierId.toString())
            // get purchase order list by supplierId and projectId
            List lstPurchaseOrder
            if (parameterMap.purchaseOrderId) {
                long purchaseOrderId = Long.parseLong(parameterMap.purchaseOrderId.toString())
                lstPurchaseOrder = wrapPOList(procurementImplService.listPOBySupplierIdAndProjectIdForEdit(supplierId, purchaseOrderId, projectId))
                Map purchaseOrderStr = [id: purchaseOrderId, name: PO_NO_LABEL + purchaseOrderId]
                lstPurchaseOrder << purchaseOrderStr
            } else {
                lstPurchaseOrder = wrapPOList(procurementImplService.listPOBySupplierIdAndProjectId(supplierId, projectId))
            }

            result.put(LST_PURCHASE_ORDER, lstPurchaseOrder)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ex.getMessage())
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
     * Get purchase order list for drop down
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            List<GroovyRowResult> lstPurchaseOrder = (List<GroovyRowResult>) executeResult.get(LST_PURCHASE_ORDER)
            result = [lstPurchaseOrder: Tools.listForKendoDropdown(lstPurchaseOrder,null,null)]
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
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Wrap purchase order list
     * @param lstPurchaseOrder - list of purchase order
     * @return -wrapped list of purchase order
     */
    private wrapPOList(List<Object> lstPurchaseOrder) {
        List lstWrappedPO = []
        int counter = start + 1
        for (int i = 0; i < lstPurchaseOrder.size(); i++) {
            lstWrappedPO << [id: lstPurchaseOrder[i].poId, name: lstPurchaseOrder[i].strPoId]
            counter++
        }
        return lstWrappedPO
    }
}
