package com.athena.mis.procurement.actions.transportcost

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.entity.ProcTransportCost
import com.athena.mis.procurement.service.PurchaseOrderService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for Transport cost CRUD and list of transport cost for grid
 *  For details go through Use-Case doc named 'ShowTransportCostActionService'
 */
class ShowTransportCostActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    BudgetPluginConnector budgetImplService
    PurchaseOrderService purchaseOrderService
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Can't load transport cost"
    private static final String PURCHASE_ORDER_NOT_FOUND = "purchase order not found"
    private static final String TRANSPORT_COST_LIST = "transportCostList"
    private static final String PURCHASE_ORDER_MAP = "purchaseOrderMap"
    private static final String PURCHASE_ORDER_OBJ = "purchaseOrder"

    /**
     * Get Purchase order object
     * 1. Get purchase Order id from params
     * 2. Get purchase order object using purchaseOrderService by using purchaseOrderId
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing purchaseOrder object and isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.purchaseOrderId) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND)
                return
            }

            long purchaseOrderId = Long.parseLong(params.purchaseOrderId.toString())

            ProcPurchaseOrder purchaseOrder = purchaseOrderService.read(purchaseOrderId)
            if (!purchaseOrder) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND)
                return result
            }

            result.put(PURCHASE_ORDER_OBJ, purchaseOrder)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap transport cost list for grid
     * 1. Get purchase Order object
     * 2. Get transport cost list  if (purchaseOrder.id > 0)
     * 3. Get purchaseOrder map by buildPurchaseOrderMap() method sending purchaseOrder object
     * 4. Get wrapped list of transport cost list by wrapTransportCosListInGridEntityList() method
     * @param parameters - serialized parameters from UI
     * @param obj - map from executePreCondition() method
     * @return - a map containing purchaseOrder map, wrapped transport cost list, total of transport cost list & isError(TRUE/FALSE) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            Map receiveResult = (Map) obj
            initPager(params)
            ProcPurchaseOrder purchaseOrder = (ProcPurchaseOrder) receiveResult.get(PURCHASE_ORDER_OBJ)
            List<ProcTransportCost> transportCostList = []
            int total = 0

            if (purchaseOrder.id > 0) {
                transportCostList = ProcTransportCost.findAllByPurchaseOrderId(purchaseOrder.id, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                total = ProcTransportCost.countByPurchaseOrderId(purchaseOrder.id)
            }

            Map purchaseOrderMap = buildPurchaseOrderMap(purchaseOrder)
            List transportCostListWrap = wrapTransportCosListInGridEntityList(transportCostList, start)

            result.put(PURCHASE_ORDER_MAP, purchaseOrderMap)
            result.put(TRANSPORT_COST_LIST, transportCostListWrap)
            result.put(Tools.COUNT, total)
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
     *  Wrap list of transport cost in grid entity
     * @param transportCostList - list of transport cost
     * @param start - starting index of the page
     * @return - list of wrapped transport costs
     */
    private List wrapTransportCosListInGridEntityList(List<ProcTransportCost> transportCostList, int start) {
        List transportCosts = [] as List

        int counter = start + 1
        ProcTransportCost transportCost
        GridEntity object
        for (int i = 0; i < transportCostList.size(); i++) {
            object = new GridEntity()
            transportCost = transportCostList[i]
            object.id = transportCost.id
            object.cell = [
                    counter,
                    Tools.makeAmountWithThousandSeparator(transportCost.amount),
                    Tools.formatAmountWithoutCurrency(transportCost.quantity),
                    Tools.makeAmountWithThousandSeparator(transportCost.rate)
            ]
            transportCosts << object
            counter++
        }
        return transportCosts
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get wrapped transportCost list from execute method
     * @param obj - a map from execute
     * @return - a map containing purchaseOrder map, gridOutput map & isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(Tools.COUNT)
            List transportCostList = (List) receiveResult.get(TRANSPORT_COST_LIST)
            Map gridOutput = [page: pageNumber, total: count, rows: transportCostList]

            result.put(PURCHASE_ORDER_MAP, receiveResult.get(PURCHASE_ORDER_MAP))
            result.put(TRANSPORT_COST_LIST, gridOutput)
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

    /**
     * Build purchaseOrder map
     * 1. Get project object from projectCacheUtility
     * 2. Get budget object from BudgetPluginConnector
     * @param purchaseOrder - object of ProcPurchaseOrder
     * @return - a map of purchaseOrder
     */
    private Map buildPurchaseOrderMap(ProcPurchaseOrder purchaseOrder) {
        Project project = (Project) projectCacheUtility.read(purchaseOrder.projectId)

        Map purchaseOrderMap = [
                purchaseOrderId: purchaseOrder.id,
                projectName: project.name
        ]
        return purchaseOrderMap
    }
}
