package com.athena.mis.budget.actions.budgschema

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgBudgetDetails
import com.athena.mis.budget.entity.BudgSchema
import com.athena.mis.budget.model.BudgetProjectItemModel
import com.athena.mis.budget.service.BudgSchemaService
import com.athena.mis.budget.service.BudgetDetailsService
import com.athena.mis.budget.service.BudgetService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update Budget schema object in DB and grid data
 *  For details go through Use-Case doc named 'UpdateBudgSchemaActionService'
 */
class UpdateBudgSchemaActionService extends BaseService implements ActionIntf {

    private static final String QUANTITY_LESS_THAN_PR = "Selected item already has been issued for PR, so quantity can not be lower than previous quantity"
    private static final String UPDATE_SUCCESS_MESSAGE = "Budget schema has been updated successfully"
    private static final String UPDATE_FAILURE_MESSAGE = "Could not update budget schema"
    private static final String BUDGET_NOT_FOUND = "Budget not found"
    private static final String BUDGET_SCHEMA_NOT_FOUND = "Budget schema not found"
    private static final String BUDGET_SCHEMA_OBJ = "budgSchema"
    private static final String IS_UP_TO_DATE = "isUpToDate"

    private final Logger log = Logger.getLogger(getClass())

    BudgetService budgetService
    BudgSchemaService budgSchemaService
    BudgetDetailsService budgetDetailsService
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility

    /**
     * 1. check required parameter
     * 2. pull budget object by budget id from params
     * 3. check the existence of budget object
     * 4. pull budget schema object by id from params
     * 5. check the existence of budget schema object
     * 6. check current schema quantity and rate with previous schema quantity and rate
     * 7. check total budget quantity and total pr quantity of the item
     * 6. Build budget schema object for update
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
            if ((!parameterMap.id) || (!parameterMap.version) || (!parameterMap.budgetId)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            // check if budget object exist
            long budgetId = Long.parseLong(parameterMap.budgetId.toString())
            double quantity = Double.parseDouble(parameterMap.quantity)
            double rate = Double.parseDouble(parameterMap.rate)
            BudgBudget budget = budgetService.read(budgetId)
            if (!budget) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            long id = Long.parseLong(parameterMap.id.toString())
            BudgSchema oldBudgetSchema = budgSchemaService.read(id)
            if (!oldBudgetSchema) {
                result.put(Tools.MESSAGE, BUDGET_SCHEMA_NOT_FOUND)
                return result
            }
            result.put(IS_UP_TO_DATE, Boolean.TRUE)
            if ((oldBudgetSchema.quantity != quantity) || (oldBudgetSchema.rate != rate)) {
                result.put(IS_UP_TO_DATE, Boolean.FALSE)
            }
            if (oldBudgetSchema.quantity > quantity) {
                BudgBudgetDetails budgetDetails = budgetDetailsService.findByBudgetIdAndItemId(oldBudgetSchema.budgetId, oldBudgetSchema.itemId)
                if (budgetDetails) {
                    BudgetProjectItemModel budgetProjectItem = BudgetProjectItemModel.findByProjectIdAndItemId(budgetDetails.projectId, budgetDetails.itemId)
                    if (((budgetProjectItem.totalBudgetQuantity - budgetDetails.quantity) + (quantity * budget.budgetQuantity)) < budgetProjectItem.totalPrQuantity) {
                        result.put(Tools.MESSAGE, QUANTITY_LESS_THAN_PR)
                        return result
                    }
                }
            }
            BudgSchema budgSchema = buildBudgetSchemaObject(parameterMap, oldBudgetSchema)
            result.put(BUDGET_SCHEMA_OBJ, budgSchema)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update budget schema object in DB
     * Update budget details(item) if exists
     * This function is in transactional boundary and will roll back in case of any exception
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
            Boolean isUpToDate = (Boolean) preResult.get(IS_UP_TO_DATE)
            budgSchemaService.update(budgSchema)
            if (!isUpToDate.booleanValue()) {
                int budgetItemCount = budgetDetailsService.countByBudgetIdAndItemId(budgSchema.budgetId, budgSchema.itemId)
                if (budgetItemCount > 0) {
                    updateBudgetDetails(budgSchema)
                }
            }
            result.put(BUDGET_SCHEMA_OBJ, budgSchema)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
     * Show updated budget schema object in grid
     * Show success message
     * @param obj -a map returned from execute method
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
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build budget schema object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldBudgetSchema -old budget schema object from executePreCondition
     * @return -updated budget schema object
     */
    private BudgSchema buildBudgetSchemaObject(GrailsParameterMap parameterMap, BudgSchema oldBudgetSchema) {
        BudgSchema budgSchema = new BudgSchema(parameterMap)
        oldBudgetSchema.quantity = budgSchema.quantity
        oldBudgetSchema.rate = budgSchema.rate
        oldBudgetSchema.comments = budgSchema.comments
        oldBudgetSchema.updatedBy = budgSessionUtil.appSessionUtil.getAppUser().id
        oldBudgetSchema.updatedOn = new Date()
        return oldBudgetSchema
    }

    private static final String UPDATE_QUERY = """
        UPDATE budg_budget_details
        SET
            is_up_to_date = false
        WHERE
        budget_id=:budgetId AND
        item_id=:itemId
    """

    private int updateBudgetDetails(BudgSchema budgSchema) {
        Map queryParams = [
                budgetId: budgSchema.budgetId,
                itemId: budgSchema.itemId
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Budget item update failed on UpdateBudgSchemaActionService')
        }
        return updateCount
    }
}
