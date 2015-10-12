package com.athena.mis.procurement.actions.transportcost

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcTransportCost
import com.athena.mis.procurement.service.TransportCostService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Select specific object of selected transport Cost at grid row and show for UI
 *  For details go through Use-Case doc named 'SelectTransportCostActionService'
 */
class SelectTransportCostActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to edit transport cost"
    private static final String TRANSPORT_COST_NOT_FOUND = "Transport cost not found"
    private static final String TRANSPORT_COST_OBJ = "transportCost"

    TransportCostService transportCostService

    /**
     * Get transport Cost object
     * 1. Get transportCostId from params
     * 2. Get transportCost object from transportCostService
     * 3. Check the existence of transport cost
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing transport cost object and isError(TRUE/FALSE) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap paramsMap = (GrailsParameterMap) parameters
            long transportCostId = Long.parseLong(paramsMap.id.toString())

            ProcTransportCost transportCost = transportCostService.read(transportCostId)
            if (!transportCost) {
                result.put(Tools.MESSAGE, TRANSPORT_COST_NOT_FOUND)
                return result
            }
            result.put(TRANSPORT_COST_OBJ, transportCost)
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
     * Get transport cost
     * @param parameters - serialized parameters from UI
     * @param obj - a map from executePreCondition
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            ProcTransportCost transportCost = (ProcTransportCost) receiveResult.get(TRANSPORT_COST_OBJ)

            result.put(Tools.ENTITY, transportCost)
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
     * Show selected object on the form
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            ProcTransportCost transportCost = (ProcTransportCost) receiveResult.get(Tools.ENTITY)
            result.put(Tools.ENTITY, transportCost)
            result.put(Tools.VERSION, transportCost.version)
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
}
