package com.athena.mis.inventory.actions.invmodifyoverheadcost

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.inventory.entity.InvProductionLineItem
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

// Show inventory Production Line Item sList
class ShowInvModifyOverheadCostActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to load Modification page of Overhead Cost."
    private static final String PROD_LINE_ITEM_LIST = "prodLineItemList"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            List<InvProductionLineItem> invProductionLineItemList = invProductionLineItemCacheUtility.list()
            String fromDate = DateUtility.getDateForUI(new Date())
            String toDate = DateUtility.getDateForUI(new Date())

            result.put(PROD_LINE_ITEM_LIST, invProductionLineItemList)
            result.put(FROM_DATE, fromDate)
            result.put(TO_DATE, toDate)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            result.put(PROD_LINE_ITEM_LIST, Tools.listForKendoDropdown(executeResult.get(PROD_LINE_ITEM_LIST),null,null))
            result.put(FROM_DATE, executeResult.get(FROM_DATE))
            result.put(TO_DATE, executeResult.get(TO_DATE))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap returnResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (returnResult.message) {
                result.put(Tools.MESSAGE, returnResult.get(Tools.MESSAGE))
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

