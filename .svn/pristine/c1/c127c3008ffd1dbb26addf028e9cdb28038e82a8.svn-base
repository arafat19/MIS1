package com.athena.mis.budget.actions.budgtask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgTask
import com.athena.mis.budget.service.BudgTaskService
import com.athena.mis.budget.service.BudgetService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.budget.utility.BudgetTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Create budget task, save it to DB
 * For details go through Use-Case doc named 'CreateBudgTaskActionService'
 */
class CreateBudgTaskActionService extends BaseService implements ActionIntf {

    BudgetService budgetService
    BudgTaskService budgTaskService
    @Autowired
    BudgetTaskStatusCacheUtility budgetTaskStatusCacheUtility
    @Autowired
    BudgSessionUtil budgSessionUtil

    private static final String BUDG_TASK = "budgTask"
    private static final String BUDGET_NOT_FOUND = "Budget not found"
    private static final String NAME_EXISTS = "Same task name already exists for this budget"
    private static final String CREATE_FAILURE_MSG = "Budget task has not been saved"
    private static final String CREATE_SUCCESS_MSG = "Budget task has been successfully saved"
    private static final String INVALID_INPUT_MSG = "Failed to create budget task due to invalid input"
    public static final String BUDGET_DATE_RANGE_EXCEEDS = "Given date range is not within current budget date range"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * 1. Check input validity from the parameters
     * 2. Check budget object existence by budgetId
     * 3. Check is the task's date range within budget date range
     * 4. Check the unique name of budget task
     * @param parameterMap - serialized parameters from UI
     * @param obj - N/A
     * @return - map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameterMap, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameterMap

            //Check parameters
            if ((!params.name) || (!params.startDate) || (!params.endDate) ||
                    (!params.budgetId)) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }
            long budgetId = Long.parseLong(params.budgetId.toString())
            BudgBudget budget = budgetService.read(budgetId)
            if (!budget) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }

            Date startDate = DateUtility.parseMaskedDate(params.startDate.toString())
            Date endDate = DateUtility.parseMaskedDate(params.endDate.toString())

            String exceedsBudgetDateRange = checkBudgetDateRange(startDate, endDate, budget)
            if (exceedsBudgetDateRange) {
                result.put(Tools.MESSAGE, exceedsBudgetDateRange)
                return result
            }
            BudgTask budgTask = buildBudgTaskObject(params, budget)

            // Check existing of same budget task name
            int countSameNameExists = budgTaskService.countByNameIlikeAndBudgetIdAndCompanyId(budgTask.name, budgetId, budgTask.companyId)
            if (countSameNameExists > 0) {
                result.put(Tools.MESSAGE, NAME_EXISTS)
                return result
            }
            result.put(BUDG_TASK, budgTask)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
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
     * Save budget task object in the DB
     * 1. This method is in transactional block and will roll back in case of any exception
     * 2. Get budget task object from executePreCondition
     * 3. Budget task creates by using create method of budgTaskService
     * @param parameters - N/A
     * @param obj - map returned from executePreCondition method
     * @return - an object of budget task
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            BudgTask budgTask = (BudgTask) preResult.get(BUDG_TASK)
            BudgTask returnBudgTask = budgTaskService.create(budgTask)

            //increase task count of budget(parent)
            increaseTaskCountForBudget(budgTask.budgetId)

            result.put(BUDG_TASK, returnBudgTask)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(CREATE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Show newly created budget task object in grid
     * Show success message
     * @param obj - a map from execute method
     * @return - a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj   // cast map returned from execute method
            BudgTask budgTask = (BudgTask) receiveResult.get(BUDG_TASK)
            SystemEntity budgTaskStatusObj = (SystemEntity) budgetTaskStatusCacheUtility.read(budgTask.statusId)
            GridEntity object = new GridEntity()     //build grid object
            object.id = budgTask.id
            object.cell = [
                    Tools.LABEL_NEW,
                    budgTask.name,
                    budgTaskStatusObj.key,
                    DateUtility.getDateForUI(budgTask.startDate),
                    DateUtility.getDateForUI(budgTask.endDate)
            ]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
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
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build the new object of budget Task
     * @param parameterMap - params from UI
     * @param budget -  Object of BudgBudget
     * @return - updated object of budget task
     */
    private BudgTask buildBudgTaskObject(GrailsParameterMap parameterMap, BudgBudget budget) {
        BudgTask budgTask = new BudgTask(parameterMap)
        budgTask.companyId = budgSessionUtil.appSessionUtil.getCompanyId()
        budgTask.createdOn = new Date()
        budgTask.createdBy = budgSessionUtil.appSessionUtil.getAppUser().id
        budgTask.updatedOn = null
        budgTask.updatedBy = 0L
        budgTask.startDate = DateUtility.parseMaskedDate(parameterMap.startDate.toString())
        budgTask.endDate = DateUtility.parseMaskedDate(parameterMap.endDate.toString())
        budgTask.budgetId = budget.id
        SystemEntity budgTaskStatusDefined = (SystemEntity) budgetTaskStatusCacheUtility.readByReservedAndCompany(budgetTaskStatusCacheUtility.DEFINED_RESERVED_ID, budgTask.companyId)
        budgTask.statusId = budgTaskStatusDefined.id
        return budgTask
    }

    /**
     * Check budget task date range with budget date range
     * @param startDate - budgTask start date
     * @param endDate - budgTask end date
     * @param budgBudget - object of BudgBudget
     * @return - a static message string or null
     */
    public static String checkBudgetDateRange(Date startDate, Date endDate, BudgBudget budgBudget) {
        if ((startDate < budgBudget.startDate) || (endDate > budgBudget.endDate)) {
            return BUDGET_DATE_RANGE_EXCEEDS
        }
        return null
    }

    private static final String UPDATE_QUERY = """
            UPDATE budg_budget SET
                task_count = task_count + 1,
                version= version + 1
            WHERE
                id=:budgetId
        """

    /**
     * Task number increase by sql query
     * @param budgetId - budget id comes from execute method
     * @return - an integer of updateCount
     */
    private int increaseTaskCountForBudget(long budgetId) {
        Map queryParams = [budgetId: budgetId]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update task count')
        }
        return updateCount
    }
}
