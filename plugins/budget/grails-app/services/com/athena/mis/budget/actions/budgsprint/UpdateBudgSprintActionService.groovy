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
 *  Update sprint object in DB and grid data
 *  For details go through Use-Case doc named 'UpdateBudgSprintActionService'
 */
class UpdateBudgSprintActionService extends BaseService implements ActionIntf {

    BudgSprintService budgSprintService
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String SPRINT_UPDATE_SUCCESS_MSG = "Sprint has been updated successfully"
    private static final String SPRINT_UPDATE_FAILURE_MSG = "Sprint has not been updated"
    private static final String DATE_EXIST_MESSAGE = "This date-range over-laps with another sprint of the project"
    private static final String BUDGET_SPRINT_OBJ = "budgSprintObj"
    private static final String OBJ_NOT_FOUND = "Selected sprint not found"

    /**
     * 1. check required parameters
     * 2. get old sprint object
     * 3. check if sprint object exists or not
     * 4. build sprint object for update
     * 5. check if date range overlaps with another sprint of the project
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // check required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long sprintId = Long.parseLong(parameterMap.id.toString())
            BudgSprint oldSprint = budgSprintService.read(sprintId)
            // check whether selected object exists or not
            if (!oldSprint) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            BudgSprint sprint = buildSprintObject(parameterMap, oldSprint)
            // check duplicate sprint date range
            int countOverLap = budgSprintService.checkDateRangeForUpdate(sprint.id, sprint.startDate, sprint.endDate, sprint.projectId)
            if (countOverLap > 0) {
                result.put(Tools.MESSAGE, DATE_EXIST_MESSAGE)
                return result
            }
            result.put(BUDGET_SPRINT_OBJ, sprint)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_UPDATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Update sprint object in DB
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
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            BudgSprint sprint = (BudgSprint) preResult.get(BUDGET_SPRINT_OBJ)
            budgSprintService.update(sprint)
            result.put(BUDGET_SPRINT_OBJ, sprint)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(SPRINT_UPDATE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_UPDATE_FAILURE_MSG)
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
     * Show updated sprint object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
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
            result.put(Tools.MESSAGE, SPRINT_UPDATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_UPDATE_FAILURE_MSG)
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
            result.put(Tools.MESSAGE, SPRINT_UPDATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SPRINT_UPDATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build sprint object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldSprint -old sprint object
     * @return -updated sprint object
     */
    private BudgSprint buildSprintObject(GrailsParameterMap parameterMap, BudgSprint oldSprint) {
        oldSprint.startDate = DateUtility.parseMaskedDate(parameterMap.startDate.toString())
        oldSprint.endDate = DateUtility.parseMaskedDate(parameterMap.endDate.toString())
        oldSprint.updatedOn = new Date()
        oldSprint.updatedBy = budgSessionUtil.appSessionUtil.getAppUser().id
        Project project = (Project) projectCacheUtility.read(oldSprint.projectId)
        oldSprint.name = project.code + Tools.SINGLE_SPACE + oldSprint.startDate.format(DateUtility.dd_MMM_yyyy_DASH) + Tools.TO + oldSprint.endDate.format(DateUtility.dd_MMM_yyyy_DASH)
        return oldSprint
    }
}
