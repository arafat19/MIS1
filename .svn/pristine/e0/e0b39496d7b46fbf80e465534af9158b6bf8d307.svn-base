package com.athena.mis.budget.actions.budgschema

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 * Get item list for budget schema
 * For details go through Use-Case doc named 'GetItemListForBudgetSchemaActionService'
 */
class GetItemListForBudgetSchemaActionService extends BaseService implements ActionIntf {

    private static final String DEFAULT_ERROR_MESSAGE = "Can't load item list"
    private static final String LST_ITEM = "lstItem"
    private static final String BUDGET_ID = "budgetId"
    private static final String ITEM_TYPE_ID = "itemTypeId"

    private static final String QUERY = """
        SELECT id, name, unit FROM item
        WHERE item_type_id=:itemTypeId AND id NOT IN
        (
            SELECT item_id FROM budg_schema
            WHERE budget_id=:budgetId
        )
        ORDER BY name
    """

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check required parameters
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            // check required parameters
            if (!parameterMap.budgetId || !parameterMap.itemTypeId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long budgetId = Long.parseLong(parameterMap.budgetId.toString())
            long itemTypeId = Long.parseLong(parameterMap.itemTypeId.toString())
            result.put(BUDGET_ID, budgetId)
            result.put(ITEM_TYPE_ID, itemTypeId)
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
     * Get list of item
     * @param parameters -N/A
     * @param obj -a map returned from executePreCondition method
     * @return -a map with item list & isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            long budgetId = (long) preResult.get(BUDGET_ID)
            long itemTypeId = (long) preResult.get(ITEM_TYPE_ID)
            Map queryParams = [
                    itemTypeId: itemTypeId,
                    budgetId: budgetId
            ]
            List<GroovyRowResult> lstItem = executeSelectSql(QUERY, queryParams)
            result.put(LST_ITEM, lstItem)
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
     * Show Item list on Gird of UI
     * @param obj -a map returned from execute method with item list
     * @return -a map with item list & isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List<GroovyRowResult> lstItem = (List<GroovyRowResult>) executeResult.get(LST_ITEM)
            lstItem = Tools.listForKendoDropdown(lstItem, null, null)
            result.put(LST_ITEM, lstItem)
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
            if (receiveResult.get(Tools.MESSAGE)) {
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
