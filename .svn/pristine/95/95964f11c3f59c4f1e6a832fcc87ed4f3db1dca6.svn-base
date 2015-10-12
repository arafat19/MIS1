package com.athena.mis.fixedasset.actions.fxdcategorymaintenancetype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.fixedasset.entity.FxdCategoryMaintenanceType
import com.athena.mis.fixedasset.entity.FxdMaintenanceType
import com.athena.mis.fixedasset.service.FxdCategoryMaintenanceTypeService
import com.athena.mis.fixedasset.utility.FxdCategoryMaintenanceTypeCacheUtility
import com.athena.mis.fixedasset.utility.FxdMaintenanceTypeCacheUtility
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Create Maintenance Type.
 * For details go through Use-Case doc named 'FxdCreateCategoryMaintenanceTypeActionService'
 */
class FxdCreateCategoryMaintenanceTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    FxdCategoryMaintenanceTypeService fxdCategoryMaintenanceTypeService

    @Autowired
    FxdSessionUtil fxdSessionUtil
    @Autowired
    FxdCategoryMaintenanceTypeCacheUtility fxdCategoryMaintenanceTypeCacheUtility
    @Autowired
    FxdMaintenanceTypeCacheUtility fxdMaintenanceTypeCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility

    private static final String SAVE_SUCCESS_MESSAGE = "Category-Maintenance Type Mapping has been saved successfully"
    private static final String SAVE_FAILURE_MESSAGE = "Category-Maintenance Type Mapping could not be saved"
    private static final String MAPPING_ALREADY_EXISTS = "Category-Maintenance Type Mapping already exist"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred due to invalid input"
    private static final String ITEM_NOT_FOUND_ERROR = "Item not found"
    private static final String MAINTENANCE_TYPE_NOT_FOUND_ERROR = "Maintenance Type not found"
    private static final String CATEGORY_MAINTENANCE_TYPE = "fxdCategoryMaintenanceType"
    private static final String MAINTENANCE_TYPE_OBJ = "fxdMaintenanceType"

    /**
     * 1. check required fields
     * 2. pull item object from cache utility
     * 3. check item existence
     * 4. pull maintenance type
     * 5. check maintenance type existence
     * 6. duplicate check for maintenance type
     * 7. build Category Maintenance Type Object
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing Category Maintenance Type Object & item object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if (!parameterMap.itemId || !parameterMap.maintenanceTypeId) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }

            long itemId = Long.parseLong(parameterMap.itemId.toString())
            long maintenanceTypeId = Long.parseLong(parameterMap.maintenanceTypeId.toString())

            Item item = (Item) itemCacheUtility.read(itemId)

            if (!item) {
                result.put(Tools.MESSAGE, ITEM_NOT_FOUND_ERROR)
                return result
            }

            FxdMaintenanceType fxdMaintenanceType = (FxdMaintenanceType) fxdMaintenanceTypeCacheUtility.read(maintenanceTypeId)
            if (!fxdMaintenanceType) {
                result.put(Tools.MESSAGE, MAINTENANCE_TYPE_NOT_FOUND_ERROR)
                return result
            }

            FxdCategoryMaintenanceType oldFxdCategoryMaintenanceType = fxdCategoryMaintenanceTypeCacheUtility.readByItemIdAndMaintenanceTypeId(itemId, maintenanceTypeId)

            if (oldFxdCategoryMaintenanceType) {
                result.put(Tools.MESSAGE, MAPPING_ALREADY_EXISTS)
                return result
            }

            FxdCategoryMaintenanceType fxdCategoryMaintenanceType = buildCategoryMaintenanceTypeObject(itemId, maintenanceTypeId)
            result.put(CATEGORY_MAINTENANCE_TYPE, fxdCategoryMaintenanceType)
            result.put(MAINTENANCE_TYPE_OBJ, fxdMaintenanceType)
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
     * 1. receive Category Maintenance Type object from pre execute method
     * 2. create new Category Maintenance Type
     * 3. add newly created category maintenance to cache utility
     * @param parameters -N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing category maintenance type & item object & isError(true/false)
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map receivedResult = (Map) obj
            FxdCategoryMaintenanceType fxdCategoryMaintenanceType = (FxdCategoryMaintenanceType) receivedResult.get(CATEGORY_MAINTENANCE_TYPE)
            FxdCategoryMaintenanceType newFxdCategoryMaintenanceType = fxdCategoryMaintenanceTypeService.create(fxdCategoryMaintenanceType)
            fxdCategoryMaintenanceTypeCacheUtility.add(newFxdCategoryMaintenanceType, fxdCategoryMaintenanceTypeCacheUtility.SORT_ON_NAME, fxdCategoryMaintenanceTypeCacheUtility.SORT_ORDER_DESCENDING)
            result.put(CATEGORY_MAINTENANCE_TYPE, newFxdCategoryMaintenanceType)
            result.put(MAINTENANCE_TYPE_OBJ, receivedResult.get(MAINTENANCE_TYPE_OBJ))
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
     * 1. Category-Maintenance Type receive from execute method
     * 2. wrap object for grid entity
     * @param obj - object receive from execute method
     * @return - Category-Maintenance Type for grid entity
     */
    @Transactional(readOnly = true)
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            FxdCategoryMaintenanceType fxdCategoryMaintenanceType = (FxdCategoryMaintenanceType) receivedResult.get(CATEGORY_MAINTENANCE_TYPE)
            GridEntity object = new GridEntity()
            object.id = fxdCategoryMaintenanceType.id
            FxdMaintenanceType fxdMaintenanceType = (FxdMaintenanceType) receivedResult.get(MAINTENANCE_TYPE_OBJ)
            object.cell = [
                    Tools.LABEL_NEW,
                    fxdMaintenanceType.name
            ]

            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, fxdMaintenanceType.version)
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
     * Build Category Maintenance Type object
     * @param itemId - item id
     * @param maintenanceTypeId - maintenance type id
     * @return Category Maintenance Type object
     */
    private FxdCategoryMaintenanceType buildCategoryMaintenanceTypeObject(long itemId, long maintenanceTypeId) {
        FxdCategoryMaintenanceType fxdCategoryMaintenanceType = new FxdCategoryMaintenanceType()
        AppUser user = fxdSessionUtil.appSessionUtil.getAppUser()

        fxdCategoryMaintenanceType.version = 0
        fxdCategoryMaintenanceType.itemId = itemId
        fxdCategoryMaintenanceType.maintenanceTypeId = maintenanceTypeId
        fxdCategoryMaintenanceType.createdOn = new Date()
        fxdCategoryMaintenanceType.createdBy = user.id
        fxdCategoryMaintenanceType.updatedOn = null
        fxdCategoryMaintenanceType.updatedBy = 0L
        fxdCategoryMaintenanceType.companyId = user.companyId

        return fxdCategoryMaintenanceType
    }
}

