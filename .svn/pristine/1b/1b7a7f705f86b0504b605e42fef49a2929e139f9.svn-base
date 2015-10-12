package com.athena.mis.budget.actions.budgschema

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.budget.entity.BudgSchema
import com.athena.mis.budget.service.BudgSchemaService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select budget schema object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectBudgSchemaActionService'
 */
class SelectBudgSchemaActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String BUDGET_SCHEMA_NOT_FOUND_MSG = "Selected budget schema not found"
    private static final String SERVER_ERROR_MESSAGE = "Internal Server Error"
    private static final String BUDGET_SCHEMA_OBJ = "budgSchema"
    private static final String LST_ITEM = "lstItem"

    BudgSchemaService budgSchemaService
    @Autowired
    ItemCacheUtility itemCacheUtility

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * 1. check required parameters
     * 2. get budget schema object
     * 4. check the existence of budget schema object
     * 5. build item list to show on UI
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // check required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long id = Long.parseLong(parameterMap.id.toString())
            BudgSchema budgSchema = budgSchemaService.read(id)
            if (!budgSchema) {
                result.put(Tools.MESSAGE, BUDGET_SCHEMA_NOT_FOUND_MSG)
                return result
            }
            Item item = (Item) itemCacheUtility.read(budgSchema.itemId)
            Map itemMap = ['id': item.id, 'name': item.name, unit: item.unit]
            List lstItem = []
            lstItem << itemMap
            result.put(LST_ITEM, lstItem)
            result.put(BUDGET_SCHEMA_OBJ, budgSchema)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with budget schema object, item list & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            BudgSchema budgSchema = (BudgSchema) executeResult.get(BUDGET_SCHEMA_OBJ)
            result.put(Tools.ENTITY, budgSchema)
            result.put(LST_ITEM, executeResult.get(LST_ITEM))
            result.put(Tools.VERSION, budgSchema.version)
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
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, BUDGET_SCHEMA_NOT_FOUND_MSG)
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
