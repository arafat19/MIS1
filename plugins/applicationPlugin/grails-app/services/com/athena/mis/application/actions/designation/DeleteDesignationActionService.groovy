package com.athena.mis.application.actions.designation

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Designation
import com.athena.mis.application.service.DesignationService
import com.athena.mis.application.utility.DesignationCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete designation object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteDesignationActionService'
 */
class DeleteDesignationActionService extends BaseService implements ActionIntf {

    DesignationService designationService
    @Autowired
    DesignationCacheUtility designationCacheUtility

    private static final String HAS_ASSOCIATION_EMPLOYEE = " employee is associated with selected designation"

    private static final String DELETE_SUCCESS_MSG = "Designation has been successfully deleted"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete designation"
    private static final String INVALID_INPUT_MSG = "Failed to delete designation due to invalid input"
    private static final String OBJ_NOT_FOUND_MSG = "Selected designation not found, Refresh the page"
    private static final String DELETED = "deleted"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * 1. Check input validity
     * 2. pull designation object by designation id
     * 3. check designation existence
     * 4. Check employee-designation association
     * @param parameterMap -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing designation object necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.id) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }

            long designationId = Long.parseLong(params.id.toString())
            //Check existing of object
            Designation designation = (Designation) designationCacheUtility.read(designationId)
            if (!designation) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MSG)
                return result
            }

            //Check association
            Map associationResult = (Map) hasAssociation(designation)
            Boolean hasAssociation = (Boolean) associationResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, associationResult.get(Tools.MESSAGE))
                return result
            }

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
     * Delete designation object from DB and cache utility
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long designationId = Long.parseLong(parameterMap.id.toString())
            int deleteCount = designationService.delete(designationId)
            designationCacheUtility.delete(designationId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete designation')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(DELETED, Boolean.TRUE)
        result.put(Tools.MESSAGE, DELETE_SUCCESS_MSG)
        return result
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * 1. Check designation-employee association
     * @param designation -designation object
     * @return -a map containing hasAssociation(true/false) and relevant message
     */
    private LinkedHashMap hasAssociation(Designation designation) {
        LinkedHashMap result = new LinkedHashMap()
        long designationId = designation.id
        long companyId = designation.companyId
        int count = 0
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        count = countEmployee(designationId, companyId)
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_EMPLOYEE)
            return result
        }
        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    private static final String SELECT_QUERY = """
            SELECT COUNT(id) AS count
                FROM employee
                WHERE designation_id = :designationId
                  AND company_id = :companyId
            """
    /**
     * Get total employee number of a specific designation
     * @param designationId - designation id
     * @param companyId - company id
     * @return - int value of total number of employees of a specific designation
     */
    private int countEmployee(long designationId, long companyId) {

        Map queryParams = [
                designationId: designationId,
                companyId: companyId
        ]
        List results = executeSelectSql(SELECT_QUERY, queryParams)
        int count = results[0].count
        return count
    }
}

