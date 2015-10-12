package com.athena.mis.accounting.actions.accdivision

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccDivision
import com.athena.mis.accounting.service.AccDivisionService
import com.athena.mis.accounting.utility.AccDivisionCacheUtility
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update accDivision object and to show on grid list
 *  For details go through Use-Case doc named 'UpdateAccDivisionActionService'
 */
class UpdateAccDivisionActionService extends BaseService implements ActionIntf {

    private static final String UPDATE_FAILURE_MESSAGE = "Fail to update division"
    private static final String UPDATE_SUCCESS_MESSAGE = "Division has been updated successfully"
    private static final String INPUT_VALIDATION_ERROR_MSG = "Given inputs are not valid"
    private static final String NAME_EXISTS = "Division name already exists"
    private static final String OBJ_NOT_FOUND = "Selected division not exists"
    private static final String ACC_DIVISION = "accDivision"

    private final Logger log = Logger.getLogger(getClass())

    AccDivisionService accDivisionService
    @Autowired
    AccDivisionCacheUtility accDivisionCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility

    /**
     * Check different criteria to update accDivision object
     *      1) Check existence of selected(old) accDivision object
     *      2) Check duplicate accDivision name
     *      3) Validate accDivision object to update
     * @param params -N/A
     * @param obj -AccDivision object send from controller
     * @return -a map containing accDivision object for execute method
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            AccDivision accDivision = (AccDivision) obj

            AccDivision oldAccDivision = (AccDivision) accDivisionCacheUtility.read(accDivision.id)
            if (!oldAccDivision) {  //check existence of accDivision object
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            accDivision.createdBy = oldAccDivision.createdBy
            accDivision.createdOn = oldAccDivision.createdOn
            accDivision.projectId = oldAccDivision.projectId

            AccDivision existingAccDivision = accDivisionCacheUtility.readByNameForUpdate(accDivision.name, accDivision.id)
            if (existingAccDivision) { // check unique accDivision name
                result.put(Tools.MESSAGE, NAME_EXISTS)
                return result
            }

            //validate accDivision object
            accDivision.validate()
            if (accDivision.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR_MSG)
                return result
            }

            result.put(ACC_DIVISION, accDivision)
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
     * update accDivision object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -accDivisionObject send from executePreCondition
     * @return -updated accDivision object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccDivision accDivision = (AccDivision) preResult.get(ACC_DIVISION)
            AccDivision updatedDivision = accDivisionService.update(accDivision)//Update in DB
            //Update in cache and keep the data sorted
            accDivisionCacheUtility.update(updatedDivision, accDivisionCacheUtility.NAME, accDivisionCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_DIVISION, updatedDivision)
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
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap updated accDivision object to show on grid
     * @param obj -updated accDivision object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AccDivision accDivisionServiceReturn = (AccDivision) executeResult.get(ACC_DIVISION)
            GridEntity object = new GridEntity()
            Project project = (Project) projectCacheUtility.read(accDivisionServiceReturn.projectId)
            object.id = accDivisionServiceReturn.id
            object.cell = [
                    Tools.LABEL_NEW,
                    accDivisionServiceReturn.id,
                    accDivisionServiceReturn.name,
                    project.name,
                    accDivisionServiceReturn.isActive ? Tools.YES : Tools.NO
            ]
            Map resultMap = [entity: object, version: accDivisionServiceReturn.version]
            result.put(ACC_DIVISION, resultMap)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
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
}
