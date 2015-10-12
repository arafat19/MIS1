package com.athena.mis.budget.actions.budgschema

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgBudgetDetails
import com.athena.mis.budget.entity.BudgSchema
import com.athena.mis.budget.model.BudgetProjectItemModel
import com.athena.mis.budget.service.BudgSchemaService
import com.athena.mis.budget.service.BudgetDetailsService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 * Delete budget schema object from DB
 * For details go through Use-Case doc named 'DeleteBudgSchemaActionService'
 */
class DeleteBudgSchemaActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_DUE_TO_PR_MESSAGE = "Selected item already has been issued for PR"
    private static final String DELETE_SUCCESS_MESSAGE = "Budget schema has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Budget schema could not be deleted"
    private static final String NOT_FOUND_MESSAGE = "Selected budget schema not found"
    private static final String BUDGET_SCHEMA_OBJ = "budgSchema"
    private static final String DELETED = "deleted"

    BudgSchemaService budgSchemaService
    BudgetDetailsService budgetDetailsService

    /**
     * 1. check required parameters
     * 2. get budget schema object by id
     * 3. check the existence of budget schema object
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            // check required parameters
            if (!params.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long id = Long.parseLong(params.id.toString())
            BudgSchema budgSchema = budgSchemaService.read(id)
            // checking the existence of budget schema object
            if (!budgSchema) {
                result.put(Tools.MESSAGE, NOT_FOUND_MESSAGE)
                return result
            }
            BudgBudgetDetails budgetDetails = budgetDetailsService.findByBudgetIdAndItemId(budgSchema.budgetId, budgSchema.itemId)
            if (budgetDetails) {
                BudgetProjectItemModel budgetProjectItem = BudgetProjectItemModel.findByProjectIdAndItemId(budgSchema.projectId, budgSchema.itemId)
                if ((budgetProjectItem.totalBudgetQuantity - budgetDetails.quantity) < budgetProjectItem.totalPrQuantity) {
                    result.put(Tools.MESSAGE, FAILURE_DUE_TO_PR_MESSAGE)
                    return result
                }
            }
            result.put(BUDGET_SCHEMA_OBJ, budgSchema)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Delete budget schema object from DB
     * Delete budget item from budget details if exists
     *      -decrease item count of budget
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -N/A
     * @param obj -a map returned from executePreCondition method
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            BudgSchema budgSchema = (BudgSchema) preResult.get(BUDGET_SCHEMA_OBJ)
            //delete budget details
            budgSchemaService.delete(budgSchema.id)
            int budgetItemCount = budgetDetailsService.countByBudgetIdAndItemId(budgSchema.budgetId, budgSchema.itemId)
            if (budgetItemCount > 0) {
                deleteBudgetItem(budgSchema)
                decreaseItemCount(budgSchema.budgetId)
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DELETE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(DELETED, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    private static final String DELETE_QUERY = """
        DELETE FROM budg_budget_details
        WHERE
        budget_id=:budgetId AND
        item_id=:itemId
    """

    private int deleteBudgetItem(BudgSchema budgSchema) {
        Map queryParams = [
                budgetId: budgSchema.budgetId,
                itemId: budgSchema.itemId
        ]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException('Budget item delete failed on DeleteBudgSchemaActionService')
        }
        return deleteCount
    }

    private static final String UPDATE_QUERY = """
        UPDATE budg_budget SET
            item_count = item_count - 1,
            version = version + 1
        WHERE
            id=:budgetId
    """

    /**
     * Decrease item count of budget
     * @param budgetId -id of budget
     * @return -an integer containing the value of update count
     */
    private int decreaseItemCount(long budgetId) {
        Map queryParams = [budgetId: budgetId]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Budget item count decrease failed on DeleteBudgSchemaActionService')
        }
        return updateCount
    }
}
