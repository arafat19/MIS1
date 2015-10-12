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
 * Create Category Maintenance Type.
 * For details go through Use-Case doc named 'FxdCreateCategoryMaintenanceTypeActionService'
 */
class FxdCreateFxdMaintenanceTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    FxdMaintenanceTypeService fxdMaintenanceTypeService
    @Autowired
    FxdSessionUtil fxdSessionUtil
    @Autowired
    FxdMaintenanceTypeCacheUtility fxdMaintenanceTypeCacheUtility

    private static final String SAVE_SUCCESS_MESSAGE = "Maintenance Type has been saved successfully"
    private static final String SAVE_FAILURE_MESSAGE = "Maintenance Type could not be saved"
    private static final String NAME_ALREADY_EXISTS = "Same Maintenance Type Name already exist"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred due to invalid input"
    private static final String MAINTENANCE_TYPE = "maintenanceType"
    private static final String FXD_MAINTENANCE_TYPE = "fxdMaintenanceType"

    /**
     * 1. check input validation
     * 2. Checking if Name is already exists
     * 3. build Maintenance Type Object
     * @param params- serialized parameters from UI
     * @param obj- N/A
     * @return - a map containing Maintenance Type Object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if (!parameterMap.name) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }

            String name = parameterMap.name.toString()
            AppUser user = fxdSessionUtil.appSessionUtil.getAppUser()
            //Checking if Name is already exists
            FxdMaintenanceType oldFxdMaintenanceType = FxdMaintenanceType.findByNameAndCompanyId(name, user.companyId, [readOnly: true])
            if (oldFxdMaintenanceType) {
                result.put(Tools.MESSAGE, NAME_ALREADY_EXISTS)
                return result
            }

            FxdMaintenanceType fxdMaintenanceType = buildMaintenanceTypeObject(parameterMap)
            result.put(MAINTENANCE_TYPE, fxdMaintenanceType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * 1. receive Maintenance Type object from pre execute method
     * 2. create new Maintenance Type
     * 3. add newly created maintenance to cache utility
     * @param parameters -N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing  maintenance type & isError(true/false)
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj
            FxdMaintenanceType fxdMaintenanceType = (FxdMaintenanceType) preResult.get(MAINTENANCE_TYPE)
            FxdMaintenanceType newFxdMaintenanceType = fxdMaintenanceTypeService.create(fxdMaintenanceType)
            fxdMaintenanceTypeCacheUtility.add(newFxdMaintenanceType, fxdMaintenanceTypeCacheUtility.SORT_ON_ID, fxdMaintenanceTypeCacheUtility.SORT_ORDER_DESCENDING)
            result.put(MAINTENANCE_TYPE, newFxdMaintenanceType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
            LinkedHashMap executeResult = (LinkedHashMap) obj
            FxdMaintenanceType fxdMaintenanceType = (FxdMaintenanceType) executeResult.get(MAINTENANCE_TYPE)
            GridEntity object = new GridEntity()
            object.id = fxdMaintenanceType.id
            object.cell = [
                    Tools.LABEL_NEW,
                    fxdMaintenanceType.id,
                    fxdMaintenanceType.name
            ]
            Map resultMap = [entity: object, version: fxdMaintenanceType.version]
            result.put(FXD_MAINTENANCE_TYPE, resultMap)
            result.put(Tools.MESSAGE, SAVE_SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
            Map receiveResult = (Map) obj
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Build Maintenance Type object
     * @param parameterMap - serialize parameters from UI
     * @return Maintenance Type object
     */
    private FxdMaintenanceType buildMaintenanceTypeObject(GrailsParameterMap parameterMap) {
        FxdMaintenanceType fxdMaintenanceType = new FxdMaintenanceType()
        AppUser user = fxdSessionUtil.appSessionUtil.getAppUser()

        fxdMaintenanceType.version = 0
        fxdMaintenanceType.name = parameterMap.name
        fxdMaintenanceType.createdOn = new Date()
        fxdMaintenanceType.createdBy = user.id
        fxdMaintenanceType.updatedOn = null
        fxdMaintenanceType.updatedBy = 0L
        fxdMaintenanceType.companyId = user.companyId

        return fxdMaintenanceType
    }

}

