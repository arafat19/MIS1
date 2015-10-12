package com.athena.mis.fixedasset.actions.fxdmaintenancetype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.fixedasset.entity.FxdMaintenanceType
import com.athena.mis.fixedasset.service.FxdMaintenanceTypeService
import com.athena.mis.fixedasset.utility.FxdCategoryMaintenanceTypeCacheUtility
import com.athena.mis.fixedasset.utility.FxdMaintenanceTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Delete Maintenance Type.
 * For details go through Use-Case doc named 'FxdDeleteFxdMaintenanceTypeActionService'
 */
class FxdDeleteFxdMaintenanceTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    FxdMaintenanceTypeService fxdMaintenanceTypeService
    @Autowired
    FxdMaintenanceTypeCacheUtility fxdMaintenanceTypeCacheUtility
    @Autowired
    FxdCategoryMaintenanceTypeCacheUtility fxdCategoryMaintenanceTypeCacheUtility

    private static final String DELETE_SUCCESS_MESSAGE = "Maintenance Type has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Maintenance Type could not be deleted, please refresh the page"
    private static final String NOT_FOUND = "Maintenance Type may in use or change. Please refresh the page"
    private static final String HAS_ASSOCIATION_MESSAGE_CAT_MAINTENANCE_MAPPING = " category-maintenance type mapping associated with selected Maintenance Type"
    private static final String FXD_MAINTENANCE_TYPE = "fxdMaintenanceType"
    private static final String DELETED = "deleted"

    /**
     * 1. pull maintenance type
     * 2. check maintenance type existence
     * 2. association check with item id and maintenance type
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing fixed asset details object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long id = Long.parseLong(parameterMap.id.toString())
            FxdMaintenanceType fxdMaintenanceType = fxdMaintenanceTypeService.get(id)
            if (!fxdMaintenanceType) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }

            List<Item> itemList = fxdCategoryMaintenanceTypeCacheUtility.getItemListByMaintenanceTypeId(fxdMaintenanceType.id)
            if (itemList.size() > 0) {
                result.put(Tools.MESSAGE, itemList.size() + HAS_ASSOCIATION_MESSAGE_CAT_MAINTENANCE_MAPPING)
                return result
            }
            result.put(FXD_MAINTENANCE_TYPE, fxdMaintenanceType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * 1. receive maintenance type from pre method
     * 2. delete maintenance type
     * 3. delete maintenance type from cache utility
     * @param params - serialize parameters from UI
     * @param obj - object receive from pre execute method
     * @return - a map containing isError(True/False)
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long id = Long.parseLong(parameterMap.id.toString())
            int deleteCount = (int) fxdMaintenanceTypeService.delete(id)
            fxdMaintenanceTypeCacheUtility.delete(id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete FxdMaintenanceType')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return Boolean.FALSE
        }
    }
    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Set delete operation True
     * @param obj- N/A
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

