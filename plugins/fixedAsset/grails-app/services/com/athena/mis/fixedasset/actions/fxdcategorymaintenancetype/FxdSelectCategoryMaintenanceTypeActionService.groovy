package com.athena.mis.fixedasset.actions.fxdcategorymaintenancetype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.fixedasset.entity.FxdCategoryMaintenanceType
import com.athena.mis.fixedasset.utility.FxdCategoryMaintenanceTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Select Category Maintenance Type.
 * For details go through Use-Case doc named 'FxdSelectCategoryMaintenanceTypeActionService'
 */
class FxdSelectCategoryMaintenanceTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    FxdCategoryMaintenanceTypeCacheUtility fxdCategoryMaintenanceTypeCacheUtility

    private static final String NOT_FOUND_MASSAGE = "Selected category-maintenance type is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select maintenance type"
    private static final String FXD_CATEGORY_MAINTENANCE_TYPE = "fxdCategoryMaintenanceType"
    /**
     * 1. check category maintenance type existence
     * @param params - serialized parameters from UI
     * @param obj -N/A
     * @return - a map containing isError(true/false)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }
    /**
     * 1. check category maintenance type existence
     * 2. pull category maintenance type from cache utility
     * 3. check category maintenance type existence
     * @param params - serialized parameters from UI
     * @param obj -N/A
     * @return - list of category maintenance type
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
                return result
            }
            long id = Long.parseLong(parameterMap.id.toString())
            FxdCategoryMaintenanceType fxdCategoryMaintenanceType = (FxdCategoryMaintenanceType) fxdCategoryMaintenanceTypeCacheUtility.read(id)
            if (!fxdCategoryMaintenanceType) {
                result.put(Tools.MESSAGE, NOT_FOUND_MASSAGE)
                return result
            }
            result.put(FXD_CATEGORY_MAINTENANCE_TYPE, fxdCategoryMaintenanceType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }
    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. receive category maintenance type from execute method
     * @param obj - object returned from execute method
     * @return - a map containing wrapped category maintenance type for grid show
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            FxdCategoryMaintenanceType fxdCategoryMaintenanceType = (FxdCategoryMaintenanceType) executeResult.get(FXD_CATEGORY_MAINTENANCE_TYPE)
            result.put(Tools.ENTITY, fxdCategoryMaintenanceType)
            result.put(Tools.VERSION, fxdCategoryMaintenanceType.version)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
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
                Map previousResult = (Map) obj
                result.put(Tools.MESSAGE, previousResult.message)
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            }
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
}
