package com.athena.mis.budget.actions.budgetdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.budget.entity.BudgBudgetDetails
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select budget details object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectBudgetDetailsActionService'
 */
class SelectBudgetDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String BUDGET_DETAILS_NOT_FOUND_MESSAGE = "Budget details not found on server"
    private static final String SERVER_ERROR_MESSAGE = "Internal Server Error"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred for invalid input"
    private static final String BUDGET_DETAILS_OBJ = "budgeDetails"
    private static final String ITEM_LIST = "itemList"
    private static final String ITEM_ENTITY = "item"
    private static final String ITEM_TYPE = "itemType"

    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility

    /**
     * do nothing for post operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get budget details object
     * 1. Check required params for INPUT_VALIDATION_FAIL_ERROR
     * 2. Get budget details id from params
     * 3. Get budget details object
     * 4. Check the existence of budget details object
     * 5. Get item object from itemCacheUtility by budgetDetails item id
     * 6. Get am Item Map
     * 7. Get Item list
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            // check here for required params are present
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            long id = Long.parseLong(parameterMap.id.toString())

            BudgBudgetDetails budgetDetails = BudgBudgetDetails.read(id)
            if (!budgetDetails) {
                result.put(Tools.MESSAGE, BUDGET_DETAILS_NOT_FOUND_MESSAGE)
                return result
            }

            Item item = (Item) itemCacheUtility.read(budgetDetails.itemId)
            ItemType itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            Map itemMap = ['id': item.id, 'name': item.name, 'unit': item.unit]
            List<GroovyRowResult> itemList = []
            itemList << itemMap

            result.put(ITEM_ENTITY, item)
            result.put(ITEM_LIST, itemList)
            result.put(ITEM_TYPE, itemType)
            result.put(BUDGET_DETAILS_OBJ, budgetDetails)
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
     * Build a map with budget details object, item list, item entity & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            BudgBudgetDetails budgetDetails = (BudgBudgetDetails) receiveResult.get(BUDGET_DETAILS_OBJ)
            List itemList = (List) receiveResult.get(ITEM_LIST)
            result.put(Tools.ENTITY, budgetDetails)
            result.put(ITEM_LIST, itemList)
            result.put(ITEM_ENTITY, receiveResult.get(ITEM_ENTITY))
            result.put(ITEM_TYPE, receiveResult.get(ITEM_TYPE))
            result.put(Tools.VERSION, budgetDetails.version)
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
                result.put(Tools.MESSAGE, BUDGET_DETAILS_NOT_FOUND_MESSAGE)
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
