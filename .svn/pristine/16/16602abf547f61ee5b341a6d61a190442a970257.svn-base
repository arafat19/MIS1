package com.athena.mis.accounting.actions.acctype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccType
import com.athena.mis.accounting.service.AccTypeService
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccTypeCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Delete account type (accType) object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAccTypeActionService'
 */
class DeleteAccTypeActionService extends BaseService implements ActionIntf {

    AccTypeService accTypeService
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String DELETE_SUCCESS_MSG = "Account type has been successfully deleted"
    private static final String DELETE_FAILURE_MSG = "Account type has not been deleted"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete account type"
    private static final String OBJ_NOT_FOUND_MSG = "Selected account type not found"
    private static final String HAS_ASSOCIATION_MESSAGE = " chart of account(s) are associated with selected account type"
    private static final String HAS_NO_ACCESS_MESSAGE = "Only development user can delete account type"
    private static final String DELETED = "deleted"

    /**
     * Checking pre condition and association before deleting the account type(accType) object
     * @param params - parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)

            // Access check for delete, only development user has authority to delete account type (accType)
            if (!accSessionUtil.appSessionUtil.getAppUser().isConfigManager) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                result.put(Tools.MESSAGE, HAS_NO_ACCESS_MESSAGE)
                return result
            }
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            // check required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            // check whether selected accType object exists or not
            long accTypeId = Long.parseLong(parameterMap.id.toString())
            AccType accType = (AccType) accTypeCacheUtility.read(accTypeId)    // get account type(accType) object
            if (!accType) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MSG)
                return result
            }

            // Check Association with AccChartOfAccount domain
            int countCoa = AccChartOfAccount.countByAccTypeId(accType.id)
            if (countCoa > 0) {
                result.put(Tools.MESSAGE, countCoa.toString() + HAS_ASSOCIATION_MESSAGE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
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
     * Delete account type(accType) object from DB and cache utility
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params - parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long accTypeId = Long.parseLong(parameterMap.id.toString())
            accTypeService.delete(accTypeId)  // delete account type(accType) object from DB

            accTypeCacheUtility.delete(accTypeId)  // delete account type(accType) object from cache utility
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(DELETED, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_SUCCESS_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
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
                LinkedHashMap preResult = (LinkedHashMap) obj    // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
            return result
        }
    }
}
