package com.athena.mis.budget.actions.budgetdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 * Get Item List For Budget Details
 * For details go through Use-Case doc named 'GetForItemListForBudgetDetailsActionService'
 */
class GetForItemListForBudgetDetailsActionService extends BaseService implements ActionIntf {

    private static final String SERVER_ERROR_MESSAGE = "Fail to load Purchase Order List"
    private static final String DEFAULT_ERROR_MESSAGE = "Can't Load Purchase Order List"
    private static final String ITEM_LIST = "itemList"
    private static final String BUDGET_ID = "budgetId"
    private static final String ITEM_TYPE_ID = "itemTypeId"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred for invalid input"

    //@todo:model use dynamic finder
    private static final String QUERY = """
                        SELECT id, name, unit FROM item
                        WHERE item_type_id = :itemTypeId AND id NOT IN (
                        SELECT item_id FROM budg_budget_details
                        WHERE budget_id=:budgetId )
                        ORDER BY name
                        """

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Get budget id, item type id from params
     * 1. Check parameters for INPUT_VALIDATION_ERROR
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            // Checking parameterMap input validation error
            if (!parameterMap.budgetId || !parameterMap.itemTypeId) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }

            long budgetId = Long.parseLong(parameterMap.budgetId.toString())
            int itemTypeId = Integer.parseInt(parameterMap.itemTypeId.toString())

            result.put(BUDGET_ID, budgetId)
            result.put(ITEM_TYPE_ID, itemTypeId)
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
     * Get Item List
     * @param parameters - N/A
     * @param obj - get a map from executePreCondition method
     * @return - a map with item list & isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            long budgetId = (long) preResult.get(BUDGET_ID)
            int itemTypeId = (int) preResult.get(ITEM_TYPE_ID)
            Map queryParams = [
                    itemTypeId: itemTypeId,
                    budgetId: budgetId
            ]
            List<GroovyRowResult> itemList = executeSelectSql(QUERY, queryParams)
            result.put(ITEM_LIST, itemList)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show Item list on Gird of UI
     * @param obj - get a map from execute method with item list
     * @return - a map with item list & isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            List<GroovyRowResult> itemList = (List<GroovyRowResult>) receiveResult.get(ITEM_LIST)
            itemList = Tools.listForKendoDropdown(itemList, null, null)
            result.put(ITEM_LIST, itemList)
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
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }
}
