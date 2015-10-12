package com.athena.mis.budget.actions.budgetdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.budget.entity.BudgBudgetDetails
import com.athena.mis.budget.model.BudgetProjectItemModel
import com.athena.mis.budget.service.BudgetDetailsService
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Delete budget details object from DB
 * For details go through Use-Case doc named 'DeleteBudgetDetailsActionService'
 */
class DeleteBudgetDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String DELETE_SUCCESS_MESSAGE = "Budget's Item has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Budget's Item could not be deleted, Please refresh the Budget's Material"
    private static final String FAILURE_DUE_TO_PR_MESSAGE = "Budget's Item could not be deleted, It has been added into Purchase Request"
    private static final String FAILURE_DUE_TO_CONSUME_MESSAGE = "Budget's Item could not be deleted, It may Consumed "
    private static final String BUDGET_DETAILS_OBJ = "budgetDetails"
    private static final String DELETED = "deleted"

    BudgetDetailsService budgetDetailsService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService

    /**
     * Checking pre condition and association before deleting the budget details object
     * 1. Get budgetDetails id from params
     * 2. Get budgetDetails object by budgetDetails id
     * 3. Check the existence of budgetDetails
     * 4. Check budget details purchase request details(items) quantity
     * 5. Check consumption of items against a budget details
     * @param parameters -  serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long budgetDetailsId = Long.parseLong(params.id.toString())
            BudgBudgetDetails budgetDetails = budgetDetailsService.read(budgetDetailsId)
            // Checking the existence of budget details object
            if (!budgetDetails) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            // check total budget quantity with total pr quantity from view
            BudgetProjectItemModel budgetProjectItem = BudgetProjectItemModel.findByProjectIdAndItemId(budgetDetails.projectId, budgetDetails.itemId)
            if ((budgetProjectItem.totalBudgetQuantity - budgetDetails.quantity) < budgetProjectItem.totalPrQuantity) {
                result.put(Tools.MESSAGE, FAILURE_DUE_TO_PR_MESSAGE)
                return result
            }
            //Checking the consumption of items against a budget details
            if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
                int countConsumeItem = inventoryImplService.getConsumeItemByBudgetAndItemId(budgetDetails.budgetId, budgetDetails.itemId)
                if (countConsumeItem > 0) {
                    result.put(Tools.MESSAGE, FAILURE_DUE_TO_CONSUME_MESSAGE)
                    return result
                }
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(BUDGET_DETAILS_OBJ, budgetDetails)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Delete budget details object from DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params - N/A
     * @param obj - parameters from UI
     * @return - a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            BudgBudgetDetails budgetDetails = (BudgBudgetDetails) preResult.get(BUDGET_DETAILS_OBJ)
            //delete budget details
            budgetDetailsService.delete(budgetDetails.id)
            decreaseCountForBudgetDetails(budgetDetails.budgetId)
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
     * do nothing for post operation
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
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

    private static final String UPDATE_QUERY = """
        UPDATE budg_budget SET
            item_count = item_count - 1,
            version= version + 1
        WHERE
            id=:budgetId
    """

    /**
     * Item number increase by sql query
     * @param budgetId - budget id comes from execute method
     * @return - an integer updateCount
     */
    private int decreaseCountForBudgetDetails(long budgetId) {
        Map queryParams = [budgetId: budgetId]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update item count')
        }
        return updateCount
    }
}