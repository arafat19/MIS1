package com.athena.mis.procurement.actions.transportcost

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.entity.ProcTransportCost
import com.athena.mis.procurement.service.PurchaseOrderService
import com.athena.mis.procurement.service.TransportCostService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Delete Transport Cost from DB as well as from grid.
 * For details go through Use-Case doc named 'DeleteTransportCostActionService'
 */
class DeleteTransportCostActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String CURRENT_USER = "currentUser"
    private static final String DELETE_TRANSPORT_COST_SUCCESS_MESSAGE = "Transport Cost has been deleted successfully"
    private static final String DELETE_TRANSPORT_COST_FAILURE_MESSAGE = "Transport Cost could not be deleted, Please refresh the Transport Cost List"
    private static final String PURCHASE_ORDER_NOT_FOUND_MESSAGE = "Purchase order not found"
    private static final String TRANSPORT_COST_NOT_FOUND = "Transport Cost not found"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred for invalid input"
    private static final String DELETED = "deleted"
    private static final String TRANSPORT_COST_OBJ = "transportCost"
    private static final String PURCHASE_ORDER_OBJ = "purchaseOrder"
    private static final String NOT_EDITABLE_MSG = "The PO has been sent for approval, make PO editable to delete transport cost"

    TransportCostService transportCostService
    PurchaseOrderService purchaseOrderService
    @Autowired
    ProcSessionUtil procSessionUtil

    /**
     * Check pre-conditions
     * 1. Check input validation error by checking the existence of params.id
     * 2. Get transport cost object by transportCostId
     * 3. Check transport cost existence
     * 4. Get purchase order object by the read() method of purchaseOrderService using transportCost.purchaseOrderId
     * 5. Check existence of purchase order
     * 6. if purchase order is sent for approval then transport cost can not be deleted
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing isError(True/False), relative messages & other necessary objects
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            long transportCostId = Long.parseLong(params.id.toString())
            ProcTransportCost transportCost = transportCostService.read(transportCostId)
            if (!transportCost) {
                result.put(Tools.MESSAGE, TRANSPORT_COST_NOT_FOUND)
                return result
            }
            ProcPurchaseOrder purchaseOrder = purchaseOrderService.read(transportCost.purchaseOrderId)
            if (!purchaseOrder) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND_MESSAGE)
                return result
            }
            if (purchaseOrder.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_EDITABLE_MSG)
                return result
            }
            purchaseOrder.trCostTotal = purchaseOrder.trCostTotal - transportCost.amount
            purchaseOrder.totalPrice = purchaseOrder.totalPrice - transportCost.amount

            result.put(TRANSPORT_COST_OBJ, transportCost)
            result.put(PURCHASE_ORDER_OBJ, purchaseOrder)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND_MESSAGE)
            return result
        }
    }

    /**
     * Delete transport cost from DB
     * 1. Get the transportCost & purchase order object
     * 2. Delete transportCost by using delete method of transportCostService
     * 3. Update purchase order by updateForTransportCostDelete method using purchaseOrder object
     * @param parameters - N/A
     * @param obj - serialized parameters from UI
     * @return - a map containing success or failure message depending on execution & all necessary objects
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj

            ProcTransportCost transportCost = (ProcTransportCost) receiveResult.get(TRANSPORT_COST_OBJ)
            ProcPurchaseOrder purchaseOrder = (ProcPurchaseOrder) receiveResult.get(PURCHASE_ORDER_OBJ)
            AppUser currentUser = procSessionUtil.appSessionUtil.getAppUser()
            purchaseOrder.updatedOn = new Date()
            purchaseOrder.updatedBy = currentUser.id

            transportCostService.delete(transportCost)
            updateForTransportCostDelete(purchaseOrder)

            result.put(CURRENT_USER, currentUser)
            result.put(PURCHASE_ORDER_OBJ, purchaseOrder)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Failed to delete Transport Cost")
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND_MESSAGE)
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
     * Set delete operation True
     * @param obj - N/A
     * @return - a map containing deleted=True and success msg
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(DELETED, Boolean.TRUE.booleanValue())
            result.put(Tools.MESSAGE, DELETE_TRANSPORT_COST_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_TRANSPORT_COST_FAILURE_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_TRANSPORT_COST_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_TRANSPORT_COST_FAILURE_MESSAGE)
            return result
        }
    }

    private static final String PROC_PURCHASE_ORDER_UPDATE_QUERY = """
                      UPDATE proc_purchase_order SET
                          tr_cost_count=:trCostCount,
                          tr_cost_total=:trCostTotal,
                          total_price=:totalPrice,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy,
                          version=:newVersion
                      WHERE
                          id=:id AND
                          version=:version
    """

    /**
     * Update the purchase order by raw sql
     * @param purchaseOrder - object of purchase order
     * @return - an integer value of update count
     */
    private int updateForTransportCostDelete(ProcPurchaseOrder purchaseOrder) {
        Map queryParams = [
                id: purchaseOrder.id,
                version: purchaseOrder.version,
                trCostTotal: purchaseOrder.trCostTotal,
                totalPrice: purchaseOrder.totalPrice,
                trCostCount: purchaseOrder.trCostCount - 1,
                updatedBy: purchaseOrder.updatedBy,
                newVersion: purchaseOrder.version + 1,
                updatedOn: DateUtility.getSqlDateWithSeconds(purchaseOrder.updatedOn)
        ]
        int updateCount = executeUpdateSql(PROC_PURCHASE_ORDER_UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update transport cost')
        }

        return updateCount
    }
}
