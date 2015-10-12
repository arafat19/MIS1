package com.athena.mis.budget.actions.budgschema

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgSchema
import com.athena.mis.budget.service.BudgSchemaService
import com.athena.mis.budget.service.BudgetService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Create Budget Schema and save the object in DB
 * For details go through Use-Case doc named 'CreateBudgSchemaActionService'
 */
class CreateBudgSchemaActionService extends BaseService implements ActionIntf {

    private static final String SAVE_SUCCESS_MESSAGE = "Budget schema has been saved successfully"
    private static final String SAVE_FAILURE_MESSAGE = "Could not save budget schema"
    private static final String BUDGET_NOT_FOUND = "Budget not found"
    private static final String BUDGET_SCHEMA_OBJ = "budgSchema"

    private Logger log = Logger.getLogger(getClass())

    BudgetService budgetService
    BudgSchemaService budgSchemaService
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility

    /**
     * 1. check required parameters
     * 2. pull budget object by budget id from params
     * 3. check the existence of budget object
     * 4. Build budget schema object
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check required parameters
            if ((!parameterMap.budgetId)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long budgetId = Long.parseLong(parameterMap.budgetId.toString())
            BudgBudget budget = budgetService.read(budgetId)
            if (!budget) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            BudgSchema budgSchema = buildBudgetSchemaObject(parameterMap)  // build budget schema object
            result.put(BUDGET_SCHEMA_OBJ, budgSchema)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Save budget schema object in DB
     * This method is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned form executePreCondition method
            BudgSchema budgSchema = (BudgSchema) preResult.get(BUDGET_SCHEMA_OBJ)
            // create budget schema object
            BudgSchema budgetSchemaInstance = budgSchemaService.create(budgSchema)
            // check if the budget schema is created or not
            if (!budgetSchemaInstance) {
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
                return result
            }
            result.put(BUDGET_SCHEMA_OBJ, budgetSchemaInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ex.message)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
     * Show newly created budget schema object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned form execute method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            BudgSchema budgSchema = (BudgSchema) executeResult.get(BUDGET_SCHEMA_OBJ)
            Item item = (Item) itemCacheUtility.read(budgSchema.itemId)
            ItemType itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            GridEntity object = new GridEntity()
            object.id = budgSchema.id
            object.cell = [
                    Tools.LABEL_NEW,
                    itemType.name,
                    item.name,
                    Tools.formatAmountWithoutCurrency(budgSchema.quantity) + Tools.SINGLE_SPACE + item.unit,
                    Tools.makeAmountWithThousandSeparator(budgSchema.rate)
            ]
            result.put(Tools.MESSAGE, SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned form previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (preResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build BudgSchema object
     * @param parameterMap -serialized parameters from UI
     * @return -an object of BudgSchema
     */
    private BudgSchema buildBudgetSchemaObject(GrailsParameterMap parameterMap) {
        BudgSchema budgSchema = new BudgSchema(parameterMap)
        budgSchema.createdOn = new Date()
        budgSchema.createdBy = budgSessionUtil.appSessionUtil.getAppUser().id
        budgSchema.companyId = budgSessionUtil.appSessionUtil.getCompanyId()
        return budgSchema
    }
}
