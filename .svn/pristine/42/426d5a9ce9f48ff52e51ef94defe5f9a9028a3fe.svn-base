package com.athena.mis.procurement.actions.transportcost

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.entity.ProcTransportCost
import com.athena.mis.procurement.service.PurchaseOrderService
import com.athena.mis.procurement.service.TransportCostService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Create Transport Cost and show in the grid
 * For details go through Use-Case doc named 'CreateTransportCostActionService'
 */
class CreateTransportCostActionService extends BaseService implements ActionIntf {

    private static final String CURRENT_USER = "currentUser"
    private static final String TRANSPORT_COST_SAVE_SUCCESS_MESSAGE = "Transport cost has been saved successfully"
    private static final String TRANSPORT_COST_SAVE_FAILURE_MESSAGE = "Can not saved transport cost"
    private static final String TRANSPORT_COST_OBJ = "transportCost"
    private static final String PURCHASE_ORDER_OBJ = "purchaseOrder"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred for invalid input"
    private static final String PURCHASE_ORDER_NOT_FOUND_MESSAGE = "Purchase order not found"
    private static final String NOT_EDITABLE_MSG = "The PO has been sent for approval, make PO editable to create transport cost"

    private final Logger log = Logger.getLogger(getClass())

    TransportCostService transportCostService
    PurchaseOrderService purchaseOrderService
    @Autowired
    ProcSessionUtil procSessionUtil

    /**
     * Get newly created transport cost object
     * 1. Check invalid input error by params.purchaseOrderId
     * 2. Get purchaseOrder object from purchaseOrder service by purchaseOrderId
     * 3. if purchase order is sent for approval then transport cost can not be created
     * 4. Check the existence of purchase order
     * 5. Build transport cost object by buildTransportCostObject method
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - Map containing isError(true/false) and relevant message, transport cost object & purchase Order object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.purchaseOrderId) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            long purchaseOrderId = Long.parseLong(params.purchaseOrderId.toString())
            ProcPurchaseOrder purchaseOrder = purchaseOrderService.read(purchaseOrderId)
            if (!purchaseOrder) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND_MESSAGE)
                return result
            }
            if (purchaseOrder.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_EDITABLE_MSG)
                return result
            }
            ProcTransportCost transportCost = buildTransportCostObject(params, purchaseOrder) // build transport cost object
            purchaseOrder.trCostTotal += transportCost.amount
            purchaseOrder.totalPrice += transportCost.amount

            result.put(TRANSPORT_COST_OBJ, transportCost)
            result.put(PURCHASE_ORDER_OBJ, purchaseOrder)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TRANSPORT_COST_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Save transport cost object in DB
     * 1. This method is in transactional boundary and will roll back in case of any exception
     * 2. Get transport cost, purchase order objects
     * 3. Create transport cost by transportCostService
     * 4. Update purchase order for transport cost by updateForTransportCostCreate method
     * @param parameters - N/A
     * @param obj - map returned from executePreCondition method
     * @return -a map containing all objects necessary for executePostCondition
     * -map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcTransportCost transportCost = (ProcTransportCost) preResult.get(TRANSPORT_COST_OBJ)
            ProcPurchaseOrder purchaseOrder = (ProcPurchaseOrder) preResult.get(PURCHASE_ORDER_OBJ)
            AppUser currentUser = procSessionUtil.appSessionUtil.getAppUser()
            purchaseOrder.updatedOn = new Date()
            purchaseOrder.updatedBy = currentUser.id

            ProcTransportCost returnTransportCost = transportCostService.create(transportCost)
            updateForTransportCostCreate(purchaseOrder)

            result.put(CURRENT_USER, currentUser)
            result.put(PURCHASE_ORDER_OBJ, purchaseOrder)
            result.put(TRANSPORT_COST_OBJ, returnTransportCost)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Failed to create Transport Cost")
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TRANSPORT_COST_SAVE_FAILURE_MESSAGE)
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
     * 1. Receive transport cost object
     * 2. Show newly created transport cost object in grid
     * 3. Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * -map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            ProcTransportCost transportCost = (ProcTransportCost) receiveResult.get(TRANSPORT_COST_OBJ)
            GridEntity object = new GridEntity()
            object.id = transportCost.id
            object.cell = [
                    Tools.LABEL_NEW,
                    Tools.makeAmountWithThousandSeparator(transportCost.amount),
                    Tools.formatAmountWithoutCurrency(transportCost.quantity),
                    Tools.makeAmountWithThousandSeparator(transportCost.rate)
            ]
            result.put(Tools.MESSAGE, TRANSPORT_COST_SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TRANSPORT_COST_SAVE_FAILURE_MESSAGE)
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
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, TRANSPORT_COST_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TRANSPORT_COST_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build new transport cost object
     * @param parameterMap - parameters from UI
     * @param purchaseOrder - object of purchase order
     * @return - new transport cost object
     */
    private ProcTransportCost buildTransportCostObject(GrailsParameterMap parameterMap, ProcPurchaseOrder purchaseOrder) {
        ProcTransportCost transportCost = new ProcTransportCost()
        transportCost.version = 0
        transportCost.purchaseOrderId = purchaseOrder.id
        transportCost.amount = Double.parseDouble(parameterMap.amount.toString())
        transportCost.quantity = Double.parseDouble(parameterMap.quantity.toString())
        transportCost.rate = 0.0d
        if (parameterMap.rate) {
            transportCost.rate = Double.parseDouble(parameterMap.rate.toString())
        }
        transportCost.comments = parameterMap.comments

        return transportCost
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
    private int updateForTransportCostCreate(ProcPurchaseOrder purchaseOrder) {
        Map queryParams = [
                id: purchaseOrder.id,
                version: purchaseOrder.version,
                trCostTotal: purchaseOrder.trCostTotal,
                totalPrice: purchaseOrder.totalPrice,
                trCostCount: purchaseOrder.trCostCount + 1,
                updatedBy: purchaseOrder.updatedBy,
                newVersion: purchaseOrder.version + 1,
                updatedOn: DateUtility.getSqlDate(purchaseOrder.updatedOn)
        ]
        int updateCount = executeUpdateSql(PROC_PURCHASE_ORDER_UPDATE_QUERY, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update transport cost')
        }

        return updateCount;
    }
}
