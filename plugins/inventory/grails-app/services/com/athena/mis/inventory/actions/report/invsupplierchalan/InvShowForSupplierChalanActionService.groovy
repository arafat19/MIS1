package com.athena.mis.inventory.actions.report.invsupplierchalan

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger

/**
 * Show UI for supplier chalan report
 * For details go through Use-Case doc named 'InvShowForSupplierChalanActionService'
 */
class InvShowForSupplierChalanActionService extends BaseService implements ActionIntf {

    private static final String FAILURE_MSG = "Fail to show supplier chalan report"
    private static final String LST_STATUS = "lstStatus"
    private static final String ALL = "ALL"
    private static final String PENDING = "Pending"
    private static final String ACKNOWLEDGED = "Acknowledged"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with necessary information for show page
     * @param parameters -N/A
     * @param obj -N/A
     * @return -a map containing all objects necessary for show
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            List lstStatus = [[name: ALL, id: -1], [name: PENDING, id: 1], [name: ACKNOWLEDGED, id: 0]]
            result.put(LST_STATUS, lstStatus)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
     * Do nothing for build success result
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Do nothing for build failure result
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}

