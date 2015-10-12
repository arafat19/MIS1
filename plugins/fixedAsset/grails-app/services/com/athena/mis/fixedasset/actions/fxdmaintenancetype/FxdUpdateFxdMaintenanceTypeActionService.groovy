package com.athena.mis.fixedasset.actions.fxdmaintenancetype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.fixedasset.entity.FxdMaintenanceType
import com.athena.mis.fixedasset.service.FxdMaintenanceTypeService
import com.athena.mis.fixedasset.utility.FxdMaintenanceTypeCacheUtility
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Update Maintenance Type.
 * For details go through Use-Case doc named 'FxdUpdateFxdMaintenanceTypeActionService'
 */
class FxdUpdateFxdMaintenanceTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    FxdMaintenanceTypeService fxdMaintenanceTypeService
    @Autowired
    FxdSessionUtil fxdSessionUtil
    @Autowired
    FxdMaintenanceTypeCacheUtility fxdMaintenanceTypeCacheUtility

    private static final String UPDATE_SUCCESS_MESSAGE = "Maintenance Type has been updated successfully"
    private static final String UPDATE_FAILURE_MESSAGE = "Maintenance Type could not be updated"
    private static final String NAME_ALREADY_EXISTS = "Same Maintenance Type Name already exist"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred due to invalid input"
    private static final String FXD_MAINTENANCE_TYPE = "fxdMaintenanceType"
    private static final String NOT_FOUND = "Selected maintenance type is not found. Please refresh the page"
    private static final String CANT_UPDATE_MESSAGE = "Maintenance Type may in use or has been changed. Please refresh the page"
    /**
     * 1. check required fields
     * 2. pull maintenance type
     * 3. check maintenance type existence
     * 4. duplicate check for maintenance type
     * 5. check maintenance type existence
     * 6. build maintenance type object
     * @param params- serialized parameters from UI
     * @param obj- N/A
     * @return - a map containing Maintenance Type Object & item object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if (!parameterMap.id || !parameterMap.name || !parameterMap.version) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }

            String name = parameterMap.name.toString()
            long id = Long.parseLong(parameterMap.id.toString())
            long version = Long.parseLong(parameterMap.version.toString())

            FxdMaintenanceType oldFxdMaintenanceType = FxdMaintenanceType.read(id)
            if (!oldFxdMaintenanceType) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }

            if (version != oldFxdMaintenanceType.version) {
                result.put(Tools.MESSAGE, CANT_UPDATE_MESSAGE)
                return result
            }

            //Checking if Name is already exists (For Update)
            int typeListCount = FxdMaintenanceType.countByNameIlikeAndCompanyIdAndIdNotEqual(name, fxdSessionUtil.appSessionUtil.getCompanyId(), id)
            if (typeListCount > 0) {
                result.put(Tools.MESSAGE, NAME_ALREADY_EXISTS)
                return result
            }

            FxdMaintenanceType newFxdMaintenanceType = buildMaintenanceTypeObject(parameterMap, oldFxdMaintenanceType)
            result.put(FXD_MAINTENANCE_TYPE, newFxdMaintenanceType)
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
     * 1. receive Maintenance Type object from pre execute method
     * 2. update new Maintenance Type
     * 3. update maintenance type cache utility
     * @param parameters -N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing maintenance type & isError(true/false)
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj
            FxdMaintenanceType fxdMaintenanceType = (FxdMaintenanceType) preResult.get(FXD_MAINTENANCE_TYPE)
            fxdMaintenanceTypeService.update(fxdMaintenanceType)
            fxdMaintenanceTypeCacheUtility.update(fxdMaintenanceType, fxdMaintenanceTypeCacheUtility.SORT_ON_ID, fxdMaintenanceTypeCacheUtility.SORT_ORDER_DESCENDING)
            result.put(FXD_MAINTENANCE_TYPE, fxdMaintenanceType)
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
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. Maintenance Type receive from execute method
     * 2. wrap object for grid entity
     * @param obj - object receive from execute method
     * @return - Maintenance Type for grid entity
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            FxdMaintenanceType maintenanceType = (FxdMaintenanceType) preResult.get(FXD_MAINTENANCE_TYPE)
            GridEntity object = new GridEntity()
            object.id = maintenanceType.id
            object.cell = [
                    Tools.LABEL_NEW,
                    maintenanceType.id,
                    maintenanceType.name
            ]
            Map resultMap = [entity: object, version: maintenanceType.version]
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(FXD_MAINTENANCE_TYPE, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Build maintenance type object
     * @param parameterMap - serialize parameters from UI
     * @param oldFxdMaintenanceType - previous state of maintenance type object
     * @return maintenance type object
     */
    private FxdMaintenanceType buildMaintenanceTypeObject(GrailsParameterMap parameterMap, FxdMaintenanceType oldFxdMaintenanceType) {
        FxdMaintenanceType newFxdMaintenanceType = new FxdMaintenanceType()
        AppUser user = fxdSessionUtil.appSessionUtil.getAppUser()

        newFxdMaintenanceType.id = oldFxdMaintenanceType.id
        newFxdMaintenanceType.version = oldFxdMaintenanceType.version + 1
        newFxdMaintenanceType.name = parameterMap.name
        newFxdMaintenanceType.createdBy = oldFxdMaintenanceType.createdBy
        newFxdMaintenanceType.createdOn = oldFxdMaintenanceType.createdOn
        newFxdMaintenanceType.updatedOn = new Date()
        newFxdMaintenanceType.updatedBy = user.id
        newFxdMaintenanceType.companyId = oldFxdMaintenanceType.companyId

        return newFxdMaintenanceType
    }
}