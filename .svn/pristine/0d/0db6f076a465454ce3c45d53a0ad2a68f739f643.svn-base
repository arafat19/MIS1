package com.athena.mis.budget.actions.budgtask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgTask
import com.athena.mis.budget.service.BudgTaskService
import com.athena.mis.budget.service.BudgetService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.budget.utility.BudgetTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UpdateTaskForBudgetSprintActionService extends BaseService implements ActionIntf {

    BudgTaskService budgTaskService
    BudgetService budgetService
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    BudgetTaskStatusCacheUtility budgetTaskStatusCacheUtility

    private static final String BUDG_TASK_OBJ = "budgTaskObj"
    private static final String UPDATE_FAILURE_MESSAGE = "Failed to update budget task information"
    private static final String UPDATE_SUCCESS_MESSAGE = "Budget task has been updated successfully"
    private final Logger log = Logger.getLogger(getClass())

    /**
     *
     * @param parameterMap
     * @param obj
     * @return
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameterMap, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameterMap

            long id = Long.parseLong(params.id.toString())
            BudgTask oldTaskObj = budgTaskService.read(id)
            BudgTask taskObject = buildTaskObject(params, oldTaskObj)

            result.put(BUDG_TASK_OBJ, taskObject)
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
     *
     * @param parameters
     * @param obj
     * @return
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            BudgTask budgTask = (BudgTask) preResult.get(BUDG_TASK_OBJ)
            budgTaskService.updateStatus(budgTask)
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
            BudgBudget budget = budgetService.read(budgTask.budgetId)
            SystemEntity budgTaskStatusObj = (SystemEntity) budgetTaskStatusCacheUtility.read(budgTask.statusId)
            GridEntity object = new GridEntity()
            object.id = budgTask.id
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            object.cell = [
                    Tools.LABEL_NEW,
                    budget.budgetItem,
                    budgTask.name,
                    DateUtility.getLongDateForUI(budgTask.startDate),
                    DateUtility.getLongDateForUI(budgTask.endDate),
                    budgTaskStatusObj.key
            ]
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
    private BudgTask buildTaskObject(GrailsParameterMap parameterMap, BudgTask oldBudgTask) {
        AppUser systemUser = budgSessionUtil.appSessionUtil.getAppUser()
        long statusId = Long.parseLong(parameterMap.statusId.toString())
        oldBudgTask.statusId = statusId
        oldBudgTask.updatedOn = new Date()
        oldBudgTask.updatedBy = systemUser.id
        return oldBudgTask
    }

}
