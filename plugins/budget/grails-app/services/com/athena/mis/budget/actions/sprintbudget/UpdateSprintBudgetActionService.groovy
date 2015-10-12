package com.athena.mis.budget.actions.sprintbudget

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgSprint
import com.athena.mis.budget.entity.BudgSprintBudget
import com.athena.mis.budget.service.BudgSprintBudgetService
import com.athena.mis.budget.service.BudgSprintService
import com.athena.mis.budget.service.BudgetService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
/**
 *  Update sprint budget object in DB and grid data
 *  For details go through Use-Case doc named 'UpdateSprintBudgetActionService'
 */
class UpdateSprintBudgetActionService extends BaseService implements ActionIntf {
    private static final String UPDATE_SUCCESS_MESSAGE = "Sprint's budget has been updated successfully"
    private static final String UPDATE_FAILURE_MESSAGE = "Can not update sprint's budget"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred for invalid input"
    private static final String BUDGET_NOT_FOUND = "Budget not found"
    private static final String SPRINT_NOT_FOUND = "Sprint not found"
    private static final String SPRINT_BUDGET_OBJ = "sprintBudget"
    private static final String QUANTITY_EXCEED_MSG = "This quantity exceeds remaining budget quantity"

    private final Logger log = Logger.getLogger(getClass())

    BudgSprintService budgSprintService
    BudgetService budgetService
    BudgSprintBudgetService budgSprintBudgetService
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    UnitCacheUtility unitCacheUtility

    /**
     * Check preconditions and return sprint budget object
     * 1. check sprint existence
     * 2. check budget existence
     * 3. check quantity exceeds available budget quantity or not.
     * @param params - serialized parameters for UI
     * @param obj - N/A
     * @return - sprintBudget object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check here for required params are present
            if ((!parameterMap.id) || (!parameterMap.version) || (!parameterMap.budgetId) || (!parameterMap.sprintId)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            // check if sprint object exist
            long sprintId = Long.parseLong(parameterMap.sprintId.toString())
            BudgSprint sprint = budgSprintService.read(sprintId)
            if (!sprint) {
                result.put(Tools.MESSAGE, SPRINT_NOT_FOUND)
                return result
            }
            // check if budget object exist
            long budgetId = Long.parseLong(parameterMap.budgetId.toString())
            BudgBudget budget = BudgBudget.read(budgetId)
            if (!budget) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            //check if edited quantity is allowed
            long id = Long.parseLong(parameterMap.id.toString())
            double quantity = Double.parseDouble(parameterMap.quantity.toString())
            double remainingQuantity = getRemainingQuantity(budgetId)

            if (quantity > remainingQuantity) {
                result.put(Tools.MESSAGE, QUANTITY_EXCEED_MSG)
                return result
            }
            BudgSprintBudget oldSprintBudget = budgSprintBudgetService.read(id)
            BudgSprintBudget sprintBudget = buildBudgetDetails(parameterMap, oldSprintBudget) // get sprint's budget

            result.put(SPRINT_BUDGET_OBJ, sprintBudget)
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
     * Update sprint budget object in DB
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            BudgSprintBudget sprintBudget = (BudgSprintBudget) preResult.get(SPRINT_BUDGET_OBJ)
            budgSprintBudgetService.update(sprintBudget)
            result.put(SPRINT_BUDGET_OBJ, sprintBudget)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ex.message)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
     * Show updated sprint budget object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            BudgSprintBudget sprintBudget = (BudgSprintBudget) receiveResult.get(SPRINT_BUDGET_OBJ)
            BudgBudget budget = budgetService.read(sprintBudget.budgetId)
            SystemEntity unit = (SystemEntity) unitCacheUtility.read(budget.unitId)
            GridEntity object = new GridEntity()
            object.id = sprintBudget.id
            object.cell = [
                    Tools.LABEL_NEW,
                    sprintBudget.id,
                    budget.budgetItem,
                    Tools.formatAmountWithoutCurrency(sprintBudget.quantity) + Tools.SINGLE_SPACE + unit.key
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
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
     * Build sprint budget object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldSprintBudget -old sprint budget object
     * @return -updated sprint budget object
     */
    private BudgSprintBudget buildBudgetDetails(GrailsParameterMap parameterMap, BudgSprintBudget oldSprintBudget) {

        BudgSprintBudget sprintBudget = new BudgSprintBudget(parameterMap)
        AppUser systemUser = budgSessionUtil.appSessionUtil.getAppUser()

        //ensure internal inputs
        sprintBudget.updatedOn = new Date()
        sprintBudget.updatedBy = systemUser.id

        // set old sprint's budget property to new sprint's budget for validation
        sprintBudget.budgetId = oldSprintBudget.budgetId
        sprintBudget.sprintId = oldSprintBudget.sprintId
        sprintBudget.createdOn = oldSprintBudget.createdOn
        sprintBudget.createdBy = oldSprintBudget.createdBy
        sprintBudget.id = oldSprintBudget.id
        sprintBudget.version = oldSprintBudget.version

        return sprintBudget
    }

    private static final String SELECT_BUDGET_QUERY = """
       SELECT ((budget.budget_quantity-coalesce((SELECT SUM(quantity) FROM budg_sprint_budget WHERE budget_id = :budgetId),0))+sprint_budget.quantity) AS quantity
            FROM budg_sprint_budget sprint_budget
            LEFT JOIN budg_budget budget ON budget.id = sprint_budget.budget_id
            WHERE budget.id = :budgetId
            GROUP BY budget.budget_quantity,sprint_budget.quantity
    """
    /**
     *
     * @param budgetId - budget id
     * @return - double value of available quantity for budget
     */
    private double getRemainingQuantity(long budgetId){
        Map queryParams = [
                budgetId: budgetId
        ]
        List<GroovyRowResult> lstBudget = executeSelectSql(SELECT_BUDGET_QUERY, queryParams)
        return (double) lstBudget[0][0]
    }
}
