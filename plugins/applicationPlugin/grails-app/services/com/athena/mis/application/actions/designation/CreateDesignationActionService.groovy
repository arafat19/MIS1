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
 *  Create new designation object and show in grid
 *  For details go through Use-Case doc named 'CreateDesignationActionService'
 */
class CreateDesignationActionService extends BaseService implements ActionIntf {

    DesignationService designationService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    DesignationCacheUtility designationCacheUtility

    private static final String CREATE_SUCCESS_MSG = "Designation has been successfully saved"
    private static final String CREATE_FAILURE_MSG = "Designation has not been saved"
    private static final String INVALID_INPUT_MSG = "Failed to create designation due to invalid input"
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
    public Object executePreCondition(Object parameterMap, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameterMap

            //Check parameters
            if ((!params.name) || (!params.shortName)) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }

            // Check existing of same designation name
            String name = params.name.toString()
            Designation sameNameExists = designationCacheUtility.findByName(name)
            if (sameNameExists) {
                result.put(Tools.MESSAGE, NAME_EXISTS)
                return result
            }
            // Check existing of same designation shortName
            String shortName = params.shortName.toString()
            Designation shortNameExists = designationCacheUtility.findByShortName(shortName)
            if (shortNameExists) {
                result.put(Tools.MESSAGE, SHORT_NAME_EXISTS)
                return result
            }
            Designation designation = buildDesignationObject(params)
            result.put(DESIGNATION, designation)
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
     * Save designation object in DB and update cache utility accordingly
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
            Designation returnDesignation = designationService.create(designation)
            designationCacheUtility.add(returnDesignation, designationCacheUtility.SORT_ON_NAME, designationCacheUtility.SORT_ORDER_ASCENDING)
            result.put(DESIGNATION, returnDesignation)
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
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj   // cast map returned from execute method
            Designation designation = (Designation) receiveResult.get(DESIGNATION)
            GridEntity object = new GridEntity()     //build grid object
            object.id = designation.id
            object.cell = [
                    Tools.LABEL_NEW,
                    designation.name,
                    designation.shortName
            ]
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
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
     * Build designation object
     * @param parameterMap -serialized parameters from UI
     * @return -new designation object
     */
    private Designation buildDesignationObject(GrailsParameterMap parameterMap) {
        Designation designation = new Designation(parameterMap)
        designation.companyId = appSessionUtil.getCompanyId()
        designation.createdOn = new Date()
        designation.createdBy = appSessionUtil.getAppUser().id
        designation.updatedOn = null
        designation.updatedBy = 0
        return designation
    }
}
