package com.athena.mis.budget.actions.sprintbudget

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
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
 *  Create new sprint's budget object and show in grid
 *  For details go through Use-Case doc named 'CreateSprintBudgetActionService'
 */
class CreateSprintBudgetActionService extends BaseService implements ActionIntf {
    private static final String SAVE_SUCCESS_MESSAGE = "Budget has been added successfully"
    private static final String SAVE_FAILURE_MESSAGE = "Can not add sprint sprint"
    private static final String SERVER_ERROR_MESSAGE = "Internal Server Error"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred for invalid input"
    private static final String QUANTITY_EXCEED_MSG = "This quantity exceeds remaining budget quantity"
    private static final String ALREADY_EXISTS_MSG = "This budget already exists in this sprint"
    private static final String SPRINT_NOT_FOUND = "Sprint not found"
    private static final String BUDGET_NOT_FOUND = "Budget not found"
    private static final String SPRINT_BUDGET_OBJ = "sprintBudgetObj"

    private Logger log = Logger.getLogger(getClass())

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
     * 3. sprint-budget existence
     * 4. check quantity exceeds available budget quantity or not.
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
            if ((!parameterMap.sprintId || !parameterMap.budgetId)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            long sprintId = Long.parseLong(parameterMap.sprintId.toString())
            long budgetId = Long.parseLong(parameterMap.budgetId.toString())
            BudgSprint sprint = budgSprintService.read(sprintId)
            if (!sprint) {
                result.put(Tools.MESSAGE, SPRINT_NOT_FOUND)
                return result
            }

            BudgBudget budget = budgetService.read(budgetId)
            if (!budget) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            int count = budgSprintBudgetService.countBySprintIdAndBudgetId(sprintId,budgetId)
            if(count > 0){
                result.put(Tools.MESSAGE, ALREADY_EXISTS_MSG)
                return result
            }
            double quantity = Double.parseDouble(parameterMap.quantity.toString())
            double remainingQuantity = getRemainingQuantity(budgetId)
            if (quantity > remainingQuantity) {
                result.put(Tools.MESSAGE, QUANTITY_EXCEED_MSG)
                return result
            }

            BudgSprintBudget sprintBudget = buildSprintBudget(parameterMap, sprint, budget) // build sprint sprint object

            result.put(SPRINT_BUDGET_OBJ, sprintBudget)
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
     *
     * @param parameters - N/A
     * @param obj - object receive from pre condition
     * @return - a map containing sprintBudget object & message
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            BudgSprintBudget sprintBudget = (BudgSprintBudget) preResult.get(SPRINT_BUDGET_OBJ)
            //create sprint budget
            BudgSprintBudget sprintBudgetInstance = budgSprintBudgetService.create(sprintBudget)
            // Checking is the sprint budget added or not
            if (!sprintBudgetInstance) {
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
                return result
            }
            result.put(SPRINT_BUDGET_OBJ, sprintBudgetInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ex.message)
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
     *
     * @param obj - object receive form execute method
     * @return - a map containing entity object for grid display and success message
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
                    Tools.formatAmountWithoutCurrency(sprintBudget.quantity) + Tools.SINGLE_SPACE +unit.key
            ]
            result.put(Tools.MESSAGE, SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
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
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     *
     * @param parameterMap - serialized parameters for UI
     * @param sprint - sprint object
     * @param budget - budget object
     * @return - sprintBudget object
     */
    private BudgSprintBudget buildSprintBudget(GrailsParameterMap parameterMap, BudgSprint sprint, BudgBudget budget) {

        BudgSprintBudget sprintBudget = new BudgSprintBudget(parameterMap)
        AppUser systemUser = budgSessionUtil.appSessionUtil.getAppUser()

        sprintBudget.budgetId = budget.id
        sprintBudget.sprintId = sprint.id
        sprintBudget.createdOn = new Date()
        sprintBudget.createdBy = systemUser.id
        sprintBudget.updatedOn = null
        sprintBudget.updatedBy = 0
        return sprintBudget
    }

    private static final String SELECT_BUDGET_QUERY = """
       SELECT (budget.budget_quantity-coalesce((SELECT SUM(quantity) FROM budg_sprint_budget WHERE budget_id = :budgetId),0)) AS quantity
            FROM budg_budget budget
            LEFT JOIN budg_sprint_budget sprint_budget ON budget.id = sprint_budget.budget_id
            WHERE budget.id = :budgetId
            GROUP BY budget.budget_quantity
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
