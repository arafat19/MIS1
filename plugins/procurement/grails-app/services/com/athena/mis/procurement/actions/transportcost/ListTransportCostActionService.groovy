package com.athena.mis.procurement.actions.transportcost

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.procurement.entity.ProcTransportCost
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 * Show list of Transport Cost for grid
 * For details go through Use-Case doc named 'ListTransportCostActionService'
 */
class ListTransportCostActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass());

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load transport cost list"
    private static final String TRANSPORT_COST_LIST = "transportCostList"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap transport cost for grid
     * 1. Get purchaseOrderId from params
     * 2. if (purchaseOrderId > 0) becomes TRUE then get the transport cost list
     * @param parameters - serialized params from UI
     * @param obj - N/A
     * @return - a map containing wrapped transport cost list, total & isError(TRUE/FALSE) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initPager(params)

            long purchaseOrderId = Long.parseLong(params.purchaseOrderId.toString())

            List<ProcTransportCost> transportCostList = []
            int total = 0

            if (purchaseOrderId > 0) {
                transportCostList = ProcTransportCost.findAllByPurchaseOrderId(purchaseOrderId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                total = ProcTransportCost.countByPurchaseOrderId(purchaseOrderId)
            }

            List transportCostListWrap = wrapTransportCosListInGridEntityList(transportCostList, start)
            result.put(TRANSPORT_COST_LIST, transportCostListWrap)
            result.put(Tools.COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
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
            object = new GridEntity();
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
        return transportCosts;
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
     * @return - a map containing pageNumber, count & transportCost List
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(Tools.COUNT)
            List transportCostList = (List) receiveResult.get(TRANSPORT_COST_LIST)
            result = [page: pageNumber, total: count, rows: transportCostList]
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
}
