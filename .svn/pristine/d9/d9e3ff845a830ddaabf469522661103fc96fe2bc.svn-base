package com.athena.mis.budget.actions.budgsprint

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
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
 *  Class to set specific budget sprint as current sprint
 *  For details go through Use-Case doc named 'SetCurrentBudgSprintActionService'
 */
class SetCurrentBudgSprintActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    BudgSprintService budgSprintService
    @Autowired
    BudgSessionUtil budgSessionUtil

    private static final String GRID_OBJ = "gridObj"
    private static final String LST_SPRINT = "lstSprint"
    private static final String BUDG_SPRINT = "budgSprint"
    private static final String ALREADY_IS_ACTIVE = "This Sprint is already active"
    private static final String OBJ_NOT_FOUND_MASSAGE = "Selected sprint is not found"
    private static final String UPDATE_FAILURE_MESSAGE = "Sprint could not be updated"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred to invalid input"
    private static final String UPDATE_SUCCESS_MESSAGE = "Sprint has been updated successfully"

    /**
     * Check different criteria to set isActive of a BudgSprint object
     *      1) Check existence of required parameter
     *      2) Check existence of budgSprint object
     *      3) Check if selected budgSprint object is already CURRENT or not
     * @param parameters - parameters from UI
     * @param obj - N/A
     * @return - a map contains budgSprint object & isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) {//Check existence of required parameter
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            long budgSprintId = Long.parseLong(params.id.toString())
            BudgSprint oldBudgSprint = (BudgSprint) budgSprintService.read(budgSprintId)
            if (!oldBudgSprint) {//Check existence of budgSprint object
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MASSAGE)
                return result
            }

            if (oldBudgSprint.isActive) { //current budgSprint could not be set current again
                result.put(Tools.MESSAGE, ALREADY_IS_ACTIVE)
                return result
            }

            //build budgSprint object to set as current sprint
            BudgSprint newBudgSprint = buildBudgSprint(oldBudgSprint)
            result.put(BUDG_SPRINT, newBudgSprint)
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
     * Set selected BudgSprint object as current Sprint
     *      1) set is_current = FALSE of all Budget sprint objects in DB by project
     *      2) set is_current = TRUE of selected BudgSprint object in DB
     * @param parameters - N/A
     * @param obj - map returned from execute method
     * @return - a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            BudgSprint budgSprint = (BudgSprint) receivedResult.get(BUDG_SPRINT)
            setAllBudgSprintActiveFalseByProject(budgSprint)  // set is_active = FALSE of all budgSprint objects by project in DB

            //set is_active = TRUE of selected BudgSprint object in DB
            setBudgSprintActive(budgSprint)
            List<BudgSprint> lstSprint = budgSprintService.findAllByCompanyId(budgSprint.companyId, this)
            int count = budgSprintService.countByCompanyId(budgSprint.companyId)
            result.put(LST_SPRINT, lstSprint)
            result.put(Tools.COUNT, count)
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
     * Wrap sprint list for grid
     * @param obj - map returned from execute method
     * @return - a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            List<BudgSprint> lstSprint = (List<BudgSprint>) executeResult.get(LST_SPRINT)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedSprint = wrapSprintList(lstSprint, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedSprint]
            result.put(GRID_OBJ, output)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError(true) & relevant error message
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
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    private BudgSprint buildBudgSprint(BudgSprint oldBudgSprint) {
        AppUser systemUser = budgSessionUtil.appSessionUtil.getAppUser()
        oldBudgSprint.isActive = true
        oldBudgSprint.updatedBy = systemUser.id
        oldBudgSprint.updatedOn = new Date()
        return oldBudgSprint
    }

    private static final String QUERY_UPDATE = """
                    UPDATE budg_sprint
                      SET
                          is_active=false
                      WHERE
                          is_active=true AND
                          project_id=:projectId AND
                          company_id=:companyId
                          """

    /**
     * set is_active = FALSE of budgSprint objects by project
     * @param budgSprint - object of BudgSprint
     */
    private void setAllBudgSprintActiveFalseByProject(BudgSprint budgSprint) {
        Map queryParams = [
                companyId: budgSessionUtil.appSessionUtil.getCompanyId(),
                projectId: budgSprint.projectId
        ]
        executeUpdateSql(QUERY_UPDATE, queryParams)
    }

    private static final String UPDATE_QUERY = """
                    UPDATE budg_sprint
                    SET
                        version=:newVersion,
                        updated_on=:updatedOn,
                        updated_by=:updatedBy,
                        is_active=:isActive
                    WHERE
                        id=:id AND
                        project_id = :projectId AND
                        version=:version
    """
    /**
     * Set is_active = TRUE of selected BudgSprint object
     * @param budgSprint - object of budgSprint
     */
    private void setBudgSprintActive(BudgSprint budgSprint) {
        Map queryParams = [
                id: budgSprint.id,
                newVersion: budgSprint.version + 1,
                version: budgSprint.version,
                updatedBy: budgSprint.updatedBy,
                updateOn: DateUtility.getSqlDateWithSeconds(budgSprint.updatedOn),
                isActive: budgSprint.isActive,
                companyId: budgSprint.companyId,
                projectId: budgSprint.projectId
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
        }
    }

    /**
     * Wrap list of sprint in grid entity
     * @param lstSprint -list of sprint objects
     * @param start -starting index of the page
     * @return -list of wrapped sprints
     */
    private List wrapSprintList(List<BudgSprint> lstSprint, int start) {
        List lstWrappedSprint = []
        int counter = start + 1
        for (int i = 0; i < lstSprint.size(); i++) {
            BudgSprint sprint = lstSprint[i]
            int budgetCount = budgSprintService.countBySprintId(sprint.id)
            GridEntity obj = new GridEntity()    // build grid object
            obj.id = sprint.id
            obj.cell = [
                    counter,
                    sprint.id,
                    sprint.name,
                    sprint.isActive ? Tools.YES : Tools.NO,
                    budgetCount
            ]
            lstWrappedSprint << obj
            counter++
        }
        return lstWrappedSprint
    }
}
