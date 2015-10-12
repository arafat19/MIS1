package com.athena.mis.budget.actions.budgtask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgTask
import com.athena.mis.budget.service.BudgTaskService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.budget.utility.BudgetTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Update budget task object and grid data
 * For details go through Use-Case doc named 'UpdateBudgTaskActionService'
 */
class UpdateBudgTaskActionService extends BaseService implements ActionIntf {

    BudgTaskService budgTaskService
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    BudgetTaskStatusCacheUtility budgetTaskStatusCacheUtility

    private static final String BUDG_TASK_OBJ = "budgTaskObj"
    private static final String BUDGET_NOT_FOUND = "Budget not found"
    private static final String NAME_EXISTS = "Same task name already exists for this budget"
    private static final String UPDATE_FAILURE_MESSAGE = "Failed to update budget task information"
    private static final String UPDATE_SUCCESS_MESSAGE = "Budget task has been updated successfully"
    private static final String INVALID_INPUT_MSG = "Failed to update budget task due to invalid input"
    private static final String OBJ_CHANGED_MSG = "Selected budget task has been changed by other user"
    public static final String BUDGET_DATE_RANGE_EXCEEDS = "Given date range is not within current budget date range"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * 1. Check input validity from the parameters
     * 2. Pull old budget task object by budgTaskService
     * 3. Check the select object is changed by another user or not
     * 4. Duplicate checking by budget task name, budgetId and company id
     * 5. Build budget task object by updating old object of BudgTask
     * 6. Check the existence of budget object
     * 7. Check the budget task data range with budget data range
     * @param parameterMap - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for execute
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

            // check if budget object exist
            long budgetId = Long.parseLong(params.budgetId.toString())
            BudgBudget budget = BudgBudget.read(budgetId)
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

            long id = Long.parseLong(params.id.toString())
            int version = Integer.parseInt(params.version.toString())

            //Check existing of Obj and version matching
            BudgTask oldBudgTaskObj = budgTaskService.read(id)
            if ((!oldBudgTaskObj) || (oldBudgTaskObj.version != version)) {
                result.put(Tools.MESSAGE, OBJ_CHANGED_MSG)
                return result
            }

            BudgTask budgTask = buildBudgTaskObject(params, oldBudgTaskObj)

            // Check existing of same budget task name
            int countSameNameExists = budgTaskService.countByNameIlikeAndBudgetIdAndCompanyIdAndIdNotEqual(budgTask.name, budgetId, budgTask.companyId, budgTask.id)
            if (countSameNameExists > 0) {
                result.put(Tools.MESSAGE, NAME_EXISTS)
                return result
            }


            result.put(BUDG_TASK_OBJ, budgTask)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
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
     * Update budget task object in DB
     * 1. This method is in transactional block and will roll back in case of any exception
     * 2. Get budgTask object from executePreCondition
     * 3. budget task updates by update method of budgTaskService
     * @param parameters - N/A
     * @param obj - map returned from executePreCondition method
     * @return - a map containing an object of budgTask
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            BudgTask budgTask = (BudgTask) preResult.get(BUDG_TASK_OBJ)
            budgTaskService.update(budgTask)
            result.put(BUDG_TASK_OBJ, budgTask)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Give total object and update success message
     * 1. Get budget task object from execute
     * @param obj - get a map from execute method with budget task object
     * @return - a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            BudgTask budgTask = (BudgTask) receiveResult.get(BUDG_TASK_OBJ)
            SystemEntity budgTaskStatusObj = (SystemEntity) budgetTaskStatusCacheUtility.read(budgTask.statusId)
            GridEntity object = new GridEntity()
            object.id = budgTask.id
            object.cell = [
                    Tools.LABEL_NEW,
                    budgTask.name,
                    budgTaskStatusObj.key,
                    DateUtility.getDateForUI(budgTask.startDate),
                    DateUtility.getDateForUI(budgTask.endDate)
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
     * Build the new object of budget Task
     * @param parameterMap - params from UI
     * @param oldBudgTask -  Object of BudgTask
     * @return - updated object of budget task
     */
    private BudgTask buildBudgTaskObject(GrailsParameterMap parameterMap, BudgTask oldBudgTask) {
        BudgTask budgTask = new BudgTask(parameterMap)
        oldBudgTask.name = budgTask.name
        oldBudgTask.startDate = DateUtility.parseMaskedDate(parameterMap.startDate.toString())
        oldBudgTask.endDate = DateUtility.parseMaskedDate(parameterMap.endDate.toString())
        AppUser systemUser = budgSessionUtil.appSessionUtil.getAppUser()
        oldBudgTask.updatedOn = new Date()
        oldBudgTask.updatedBy = systemUser.id
        return oldBudgTask
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
}
