package com.athena.mis.accounting.actions.accleaseaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccLeaseAccount
import com.athena.mis.accounting.service.AccLeaseAccountService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


/**
 *  Select specific accLeaseAccount object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectAccLeaseAccountActionService'
 */
class SelectAccLeaseAccountActionService extends BaseService implements ActionIntf {

    AccLeaseAccountService accLeaseAccountService
    @Autowired
    ItemCacheUtility itemCacheUtility

    private static final String OBJ_NOT_FOUND_MASSAGE = "Selected Lease Account not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select Lease Account"
    private static final String START_DATE = "startDate"
    private static final String END_DATE = "endDate"
    private static final String ITEM_LIST = "itemList"
    private static final String ITEM = "item"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get accLeaseAccount object by id
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing accLeaseAccount, item object & itemType list for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {//check existence of required parameter
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long leaseAccountId = Long.parseLong(parameterMap.id.toString())
            AccLeaseAccount accLeaseAccount = (AccLeaseAccount) accLeaseAccountService.read(leaseAccountId)
            if (!accLeaseAccount) {//check existence of accLeaseAccount object
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
                return result
            }
            Item item = (Item) itemCacheUtility.read(accLeaseAccount.itemId)
            List itemList = itemCacheUtility.listByTypeForDropDown(item.itemTypeId)

            result.put(Tools.ENTITY, accLeaseAccount)
            result.put(ITEM, item)
            result.put(ITEM_LIST, itemList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with necessary objects to show on UI
     * @param obj -map contains accLeaseAccount, item object & itemType list
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AccLeaseAccount accLeaseAccount = (AccLeaseAccount) executeResult.get(Tools.ENTITY)
            String startDate = DateUtility.getDateForUI(accLeaseAccount.startDate)
            String endDate = DateUtility.getDateForUI(accLeaseAccount.endDate)

            result.put(Tools.ENTITY, accLeaseAccount)
            result.put(Tools.VERSION, accLeaseAccount.version)
            result.put(ITEM_LIST, executeResult.get(ITEM_LIST))
            result.put(ITEM, executeResult.get(ITEM))
            result.put(START_DATE, startDate)
            result.put(END_DATE, endDate)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError(true) & relevant error message
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
}
