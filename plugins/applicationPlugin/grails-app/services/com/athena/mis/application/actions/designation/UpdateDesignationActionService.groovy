package com.athena.mis.application.actions.designation

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Designation
import com.athena.mis.application.service.DesignationService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.DesignationCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update new designation object and show in grid
 *  For details go through Use-Case doc named 'UpdateDesignationActionService'
 */
class UpdateDesignationActionService extends BaseService implements ActionIntf {

    DesignationService designationService
    @Autowired
    DesignationCacheUtility designationCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String UPDATE_FAILURE_MESSAGE = "Failed to update designation information"
    private static final String UPDATE_SUCCESS_MESSAGE = "Designation has been updated successfully"
    private static final String INVALID_INPUT_MSG = "Failed to update designation due to invalid input"
    private static final String OBJ_CHANGED_MSG = "Selected designation has been changed by other user, Refresh the page again"
    private static final String DESIGNATION = "designation"
    private static final String NAME_EXISTS = "Same designation name already exists"
    private static final String SHORT_NAME_EXISTS = "Same short name already exists"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * 1. Check parameters
     * 2. Check designation name existence
     * 3. Check designation shortName existence
     * 4. Build designation object
     * @param parameterMap -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing designation object necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameterMap, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameterMap

            //Check parameters
            if ((!params.id) || (!params.version) || (!params.name) || (!params.shortName)) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }

            long id = Long.parseLong(params.id.toString())
            int version = Integer.parseInt(params.version.toString())

            //Check existing of Obj and version matching
            Designation oldDesignation = designationService.read(id)
            if ((!oldDesignation) || (oldDesignation.version != version)) {
                result.put(Tools.MESSAGE, OBJ_CHANGED_MSG)
                return result
            }

            // Check existing of same designation name
            String name = params.name.toString()
            Designation sameNameExists = designationCacheUtility.findByNameAndIdNotEqual(name, oldDesignation.id)
            if (sameNameExists) {
                result.put(Tools.MESSAGE, NAME_EXISTS)
                return result
            }
            // Check existing of same designation shortName
            String shortName = params.shortName.toString()
            Designation shortNameExists = designationCacheUtility.findByShortNameAndIdNotEqual(shortName, oldDesignation.id)
            if (shortNameExists) {
                result.put(Tools.MESSAGE, SHORT_NAME_EXISTS)
                return result
            }

            Designation designation = buildDesignationObject(params, oldDesignation)
            result.put(DESIGNATION, designation)
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
     * Update designation object in DB and update cache utility accordingly
     * This method is in transactional block and will roll back in case of any exception
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
            LinkedHashMap preResult = (LinkedHashMap) obj
            Designation designation = (Designation) preResult.get(DESIGNATION)
            designationService.update(designation)
            designationCacheUtility.update(designation, designationCacheUtility.SORT_ON_NAME, designationCacheUtility.SORT_ORDER_ASCENDING)
            result.put(DESIGNATION, designation)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. Show newly created designation object in grid
     * 2. Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            Designation designation = (Designation) executeResult.get(DESIGNATION)
            GridEntity object = new GridEntity()
            object.id = designation.id
            object.cell = [
                    Tools.LABEL_NEW,
                    designation.name,
                    designation.shortName
            ]

            result.put(Tools.ENTITY, object)
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
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Build designation object
     * @param parameterMap -serialized parameters from UI
     * @param oldDesignation -object of Designation
     * @return -new designation object
     */
    private Designation buildDesignationObject(GrailsParameterMap parameterMap, Designation oldDesignation) {
        oldDesignation.name = parameterMap.name.toString()
        oldDesignation.shortName = parameterMap.shortName.toString()
        oldDesignation.updatedBy = appSessionUtil.getAppUser().id
        oldDesignation.updatedOn = new Date()
        return oldDesignation
    }
}