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
 *  Class to create AccDivision object and to show on grid list
 *  For details go through Use-Case doc named 'CreateAccDivisionActionService'
 */
class CreateAccDivisionActionService extends BaseService implements ActionIntf {

    AccDivisionService accDivisionService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AccDivisionCacheUtility accDivisionCacheUtility

    private static final String CREATE_FAILURE_MSG = "Division has not been saved"
    private static final String CREATE_SUCCESS_MSG = "Division has been successfully saved"
    private static final String INPUT_VALIDATION_ERROR_MSG = "Given inputs are not valid"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to create Division"
    private static final String NAME_EXISTS_MSG = "Division name already exists"
    private static final String ACC_DIVISION = "accDivision"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria for creating new accDivision
     *      1) Check duplicate accDivision name
     *      2) Validate accDivision object
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
            AccDivision existingAccDivision = accDivisionCacheUtility.readByName(accDivision.name)
            if (existingAccDivision) {//Check duplicate accDivision name
                result.put(Tools.MESSAGE, NAME_EXISTS_MSG)
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Save accDivision object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -accDivisionObject send from controller
     * @return -newly created accDivision object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccDivision accDivision = (AccDivision) preResult.get(ACC_DIVISION)
            AccDivision newAccDivision = (AccDivision) accDivisionService.create(accDivision) // save in DB
            // save in cache and keep the data sorted
            accDivisionCacheUtility.add(accDivision, accDivisionCacheUtility.NAME, accDivisionCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_DIVISION, newAccDivision)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
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
     * Wrap newly created accDivision to show on grid
     * @param obj -newly created accDivision object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AccDivision accDivision = (AccDivision) executeResult.get(ACC_DIVISION)
            Project project = (Project) projectCacheUtility.read(accDivision.projectId)
            GridEntity object = new GridEntity()
            object.id = accDivision.id
            object.cell = [
                    Tools.LABEL_NEW,
                    accDivision.id,
                    accDivision.name,
                    project.name,
                    accDivision.isActive ? Tools.YES : Tools.NO
            ]
            Map resultMap = [entity: object, version: accDivision.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MSG)
            result.put(ACC_DIVISION, resultMap)
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
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }
}
