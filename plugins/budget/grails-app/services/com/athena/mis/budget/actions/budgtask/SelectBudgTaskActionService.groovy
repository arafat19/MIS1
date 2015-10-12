package com.athena.mis.budget.actions.budgtask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.budget.entity.BudgTask
import com.athena.mis.budget.service.BudgTaskService
import com.athena.mis.budget.utility.BudgetTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Select budget task object and show in UI for editing
 * For details go through Use-Case doc named 'SelectBudgTaskActionService'
 */
class SelectBudgTaskActionService extends BaseService implements ActionIntf {

    BudgTaskService budgTaskService
    @Autowired
    BudgetTaskStatusCacheUtility budgetTaskStatusCacheUtility
    private final Logger log = Logger.getLogger(getClass())

    private static final String END_DATE = "endDate"
    private static final String START_DATE = "startDate"
    private static final String BUDG_TASK_OBJ = "budgTask"
    private static final String BUDG_TASK_STATUS_NAME = "budgTaskStatusName"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select budget task"
    private static final String OBJ_NOT_FOUND_MASSAGE = "Selected budget task not found"
    private static final String INVALID_INPUT_MASSAGE = "Failed to select budget task due to invalid input"

    /**
     * Checking pre condition for selecting the budget task object
     * 1. Get budget task id from params
     * 2. Checking input validity by budget task id
     * 3. Get budget task object by BudgetTask id
     * 4. Check the existence of BudgetTask object
     * @param params - parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            // check here for required params are present
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MASSAGE)
                return result
            }
            long id = Long.parseLong(parameterMap.id.toString())
            BudgTask budgTaskObj = budgTaskService.read(id)

            // Checking budgTask object existence
            if (!budgTaskObj) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(BUDG_TASK_OBJ, budgTaskObj)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

    /**
     * do nothing for execute operation
     */
    public Object execute(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }


    /**
     * Build a map with budget task  object, version, start date, end date & other related properties to show on UI
     * @param obj - map returned from execute method
     * @return - a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            BudgTask budgTask = (BudgTask) receiveResult.get(BUDG_TASK_OBJ)
            SystemEntity budgTaskStatusObj = (SystemEntity) budgetTaskStatusCacheUtility.read(budgTask.statusId)
            String startDate = DateUtility.getDateForUI(budgTask.startDate)
            String endDate = DateUtility.getDateForUI(budgTask.endDate)
            result.put(START_DATE, startDate)
            result.put(END_DATE, endDate)
            result.put(BUDG_TASK_STATUS_NAME, budgTaskStatusObj.key)
            result.put(Tools.ENTITY, budgTask)
            result.put(Tools.VERSION, budgTask.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
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
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
}
