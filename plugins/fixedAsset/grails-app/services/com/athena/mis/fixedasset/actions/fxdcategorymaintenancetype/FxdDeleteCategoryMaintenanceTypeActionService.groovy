package com.athena.mis.fixedasset.actions.fxdcategorymaintenancetype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.fixedasset.entity.FxdCategoryMaintenanceType
import com.athena.mis.fixedasset.model.FxdMaintenanceModel
import com.athena.mis.fixedasset.service.FxdCategoryMaintenanceTypeService
import com.athena.mis.fixedasset.utility.FxdCategoryMaintenanceTypeCacheUtility
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Category Maintenance Type Delete
 * For details go through Use-Case doc named 'FxdDeleteCategoryMaintenanceTypeActionService'
 */
class FxdDeleteCategoryMaintenanceTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    FxdCategoryMaintenanceTypeService fxdCategoryMaintenanceTypeService
    @Autowired
    FxdSessionUtil fxdSessionUtil
    @Autowired
    FxdCategoryMaintenanceTypeCacheUtility fxdCategoryMaintenanceTypeCacheUtility

    private static final String DELETE_SUCCESS_MESSAGE = "Category-Maintenance Type has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Category-Maintenance Type could not be deleted, please refresh the page"
    private static final String NOT_FOUND = "Category-Maintenance Type may in use or change. Please refresh the page"
    private static final String FXD_CATEGORY_MAINTENANCE_TYPE = "fxdCategoryMaintenanceType"
    private static final String DELETED = "deleted"
    private static final String HAS_ASSOCIATION_MESSAGE_MAPPING = " fixed asset maintenance associated with selected Mapping"

    /**
     * 1. pull category maintenance type
     * 2. check category maintenance type existence
     * 2. association check with item id and category maintenance type
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
            long companyId = fxdSessionUtil.appSessionUtil.getCompanyId()
            FxdCategoryMaintenanceType fxdCategoryMaintenanceType = (FxdCategoryMaintenanceType) fxdCategoryMaintenanceTypeCacheUtility.read(id)
            if (!fxdCategoryMaintenanceType || fxdCategoryMaintenanceType.companyId != companyId ) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }

            int countMaintenance = FxdMaintenanceModel.countByItemIdAndMaintenanceTypeId(fxdCategoryMaintenanceType.itemId, fxdCategoryMaintenanceType.maintenanceTypeId)
            if (countMaintenance > 0) {
                result.put(Tools.MESSAGE, countMaintenance + HAS_ASSOCIATION_MESSAGE_MAPPING)
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
     * 1. receive category maintenance type from pre method
     * 2. delete category maintenance type
     * 3. delete category maintenance type from cache utility
     * @param params - serialize parameters from UI
     * @param obj - object receive from pre execute method
     * @return - a map containing isError(True/False)
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long id = Long.parseLong(parameterMap.id.toString())
            fxdCategoryMaintenanceTypeService.delete(id)
            FxdCategoryMaintenanceType fxdCategoryMaintenanceType = (FxdCategoryMaintenanceType) receivedResult.get(FXD_CATEGORY_MAINTENANCE_TYPE)
            boolean isDeleted = fxdCategoryMaintenanceTypeCacheUtility.delete(fxdCategoryMaintenanceType.id)
            if (!isDeleted) {
                throw new RuntimeException('Failed to delete Category-Maintenance Type')
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete Category-Maintenance Type')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
     * Set delete operation True
     * @param obj- N/A
     * @return - a map containing deleted=True and success msg
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap exeResult = (LinkedHashMap) obj
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

