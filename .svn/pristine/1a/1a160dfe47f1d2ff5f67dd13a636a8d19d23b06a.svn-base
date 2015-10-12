package com.athena.mis.budget.actions.budgsprint

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.budget.entity.BudgSprint
import com.athena.mis.budget.service.BudgSprintService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new sprint object and show in grid
 *  For details go through Use-Case doc named 'CreateBudgSprintActionService'
 */
class CreateBudgSprintActionService extends BaseService implements ActionIntf {

    BudgSprintService budgSprintService
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String SPRINT_CREATE_SUCCESS_MSG = "Sprint has been saved successfully"
    private static final String SPRINT_CREATE_FAILURE_MSG = "Sprint has not been saved"
    private static final String DATE_EXIST_MESSAGE = "This date-range over-laps with another sprint of the project"
    private static final String BUDGET_SPRINT_OBJ = "budgSprintObj"

    /**
     * 1. check required parameters
     * 2. build sprint object
     * 3. check if date range overlaps with another sprint of the project
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
            if ((!parameterMap.projectId) || (!parameterMap.startDate) || (!parameterMap.endDate)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            BudgSprint sprint = buildSprintObject(parameterMap)
            // check duplicate sprint date range
            int count = budgSprintService.checkDateRange(sprint.startDate, sprint.endDate, sprint.projectId)
            if (count > 0) {
                result.put(Tools.MESSAGE, DATE_EXIST_MESSAGE)
                return result
            }
            result.put(BUDGET_SPRINT_OBJ, sprint)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Save sprint object in DB
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
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            BudgSprint sprint = (BudgSprint) preResult.get(BUDGET_SPRINT_OBJ)
            BudgSprint savedSprintObj = budgSprintService.create(sprint)
            result.put(BUDGET_SPRINT_OBJ, savedSprintObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(SPRINT_CREATE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_CREATE_FAILURE_MSG)
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
     * Show newly created sprint object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            BudgSprint sprint = (BudgSprint) executeResult.get(BUDGET_SPRINT_OBJ)
            int budgetCount = budgSprintService.countBySprintId(sprint.id)
            GridEntity object = new GridEntity()
            object.id = sprint.id
            object.cell = [
                    Tools.LABEL_NEW,
                    sprint.id,
                    sprint.name,
                    sprint.isActive ? Tools.YES : Tools.NO,
                    budgetCount
            ]
            result.put(Tools.MESSAGE, SPRINT_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_CREATE_FAILURE_MSG)
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
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, SPRINT_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build sprint object
     * @param parameterMap -serialized parameters from UI
     * @return -new sprint object
     */
    private BudgSprint buildSprintObject(GrailsParameterMap parameterMap) {
        BudgSprint sprint = new BudgSprint(parameterMap)
        sprint.startDate = DateUtility.parseMaskedDate(parameterMap.startDate.toString())
        sprint.endDate = DateUtility.parseMaskedDate(parameterMap.endDate.toString())
        Project project = (Project) projectCacheUtility.read(sprint.projectId)
        sprint.name = project.code + Tools.SINGLE_SPACE + sprint.startDate.format(DateUtility.dd_MMM_yyyy_DASH) + Tools.TO + sprint.endDate.format(DateUtility.dd_MMM_yyyy_DASH)
        sprint.companyId = project.companyId
        sprint.createdBy = budgSessionUtil.appSessionUtil.getAppUser().id
        sprint.createdOn = new Date()
        sprint.isActive = false
        return sprint
    }
}
