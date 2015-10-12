package com.athena.mis.accounting.actions.report.accvoucherlist

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger

/**
 * Only populate date range and voucher type and posted drop-down
 * For details go through Use-Case doc named 'SearchForVoucherListActionService'
 */
class ShowForVoucherListActionService extends BaseService implements ActionIntf {

    private static final String FAILURE_MSG = "Fail to load voucher list."
    private static final String POST_LIST = "postList"

    private Logger log = Logger.getLogger(getClass())
    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get post list
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return- a map containing post list, voucher type and error msg
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            List postList = [[name: Tools.YES, id: true], [name: Tools.NO, id: false]]

            result.put(POST_LIST, Tools.listForKendoDropdown(postList, null, "ALL"))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
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
     * do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }
    /**
     * do nothing for failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
}
