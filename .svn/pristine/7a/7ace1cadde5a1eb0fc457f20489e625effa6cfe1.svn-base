package com.athena.mis.fixedasset.actions.fxdmaintenance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.fixedasset.entity.FxdMaintenance
import com.athena.mis.fixedasset.service.FxdMaintenanceService
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Delete fixed asset maintenance from DB as well as from grid.
 * For details go through Use-Case doc named 'FxdDeleteMaintenanceActionService'
 */
class FxdDeleteMaintenanceActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    FxdMaintenanceService fxdMaintenanceService
    @Autowired
    FxdSessionUtil fxdSessionUtil

    private static final String DELETE_SUCCESS_MESSAGE = "Fixed Asset Maintenance has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Fixed Asset Maintenance could not be deleted, please refresh the page"
    private static final String NOT_FOUND = "Fixed Asset Maintenance may in use or change. Please refresh the page"
    private static final String FXD_MAINTENANCE = "fxdMaintenance"
    private static final String DELETED = "deleted"

    /**
     * Check pre-conditions
     * 1. Get fixed asset maintenance id from params
     * 2. Get fixed asset maintenance object from fxdMaintenanceService
     * 3. Check the existence of fixed asset maintenance object
     * @param parameters -  serialized parameters from UI
     * @param obj -  N/A
     * @return - a map containing isError(True/False), relative messages & other necessary objects
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long id = Long.parseLong(parameterMap.id.toString())
            FxdMaintenance fxdMaintenance = fxdMaintenanceService.read(id)
            if (!fxdMaintenance
                    || fxdMaintenance.companyId != fxdSessionUtil.appSessionUtil.getCompanyId()) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }

            result.put(FXD_MAINTENANCE, fxdMaintenance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * Delete fixed asset maintenance object from DB
     * 1. Get the fixed asset maintenance object
     * 2. Delete fixed asset maintenance object by using delete method of fxdMaintenanceService
     * @param params - N/A
     * @param obj - serialized parameters from UI
     * @return - a map containing success or failure message depending on execution & all necessary objects
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            FxdMaintenance fxdMaintenance = (FxdMaintenance) receivedResult.get(FXD_MAINTENANCE)
            int deleteCount = (int) fxdMaintenanceService.delete(fxdMaintenance.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete Fixed Asset Maintenance')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return Boolean.FALSE
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Set delete operation True
     * @param obj - N/A
     * @return - a map containing deleted=True and success msg
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(DELETED, Boolean.TRUE.booleanValue())
            result.put(Tools.MESSAGE, DELETE_SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }
}


