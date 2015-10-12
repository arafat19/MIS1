package com.athena.mis.fixedasset.actions.fxdcategorymaintenancetype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.fixedasset.entity.FxdCategoryMaintenanceType
import com.athena.mis.fixedasset.entity.FxdMaintenanceType
import com.athena.mis.fixedasset.model.FxdMaintenanceModel
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
 * Update Category Maintenance Type.
 * For details go through Use-Case doc named 'FxdUpdateCategoryMaintenanceTypeActionService'
 */
class FxdUpdateCategoryMaintenanceTypeActionService extends BaseService implements ActionIntf {

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

    private static
    final String UPDATE_SUCCESS_MESSAGE = "Category-Maintenance Type Mapping has been updated successfully"
    private static final String UPDATE_FAILURE_MESSAGE = "Category-Maintenance Type Mapping could not be updated"
    private static final String MAPPING_ALREADY_EXISTS = "The Category-Maintenance Type Mapping already exist"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred due to invalid input"
    private static final String ITEM_NOT_FOUND_ERROR = "Item not found"
    private static final String MAINTENANCE_TYPE_NOT_FOUND_ERROR = "Maintenance Type not found"
    private static final String CATEGORY_MAINTENANCE_TYPE = "fxdCategoryMaintenanceType"
    private static final String MAINTENANCE_TYPE_OBJ = "fxdMaintenanceType"
    private static final String ITEM_OBJ = "item"
    private static final String CANT_UPDATE_MESSAGE = "Category-Maintenance Type Mapping may in use or has been changed. Please refresh the page"
    private static final String HAS_ASSOCIATION_MESSAGE_MAPPING = " fixed asset maintenance associated with selected Mapping"

    /**
     * 1. check required fields
     * 2. pull category maintenance type
     * 3. check category maintenance type for company id and version
     * 4. duplicate check for category maintenance type
     * 5. check maintenance type existence
     * 6. pull item object
     * 7. set Category Maintenance Type for update
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
            if (!parameterMap.id || !parameterMap.itemId || !parameterMap.maintenanceTypeId || !parameterMap.version) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }

            long itemId = Long.parseLong(parameterMap.itemId.toString())
            long maintenanceTypeId = Long.parseLong(parameterMap.maintenanceTypeId.toString())
            long fxdCategoryMaintenanceTypeId = Long.parseLong(parameterMap.id.toString())
            int version = Integer.parseInt(parameterMap.version.toString())
            AppUser user = fxdSessionUtil.appSessionUtil.getAppUser()

            FxdCategoryMaintenanceType oldFxdCategoryMaintenanceType = (FxdCategoryMaintenanceType) fxdCategoryMaintenanceTypeCacheUtility.read(fxdCategoryMaintenanceTypeId)

            if ((!oldFxdCategoryMaintenanceType)
                    || (oldFxdCategoryMaintenanceType.companyId != user.companyId)
                    || (version != oldFxdCategoryMaintenanceType.version)) {
                result.put(Tools.MESSAGE, CANT_UPDATE_MESSAGE)
                return result
            }


            int countMaintenance = FxdMaintenanceModel.countByItemIdAndMaintenanceTypeId(oldFxdCategoryMaintenanceType.itemId, oldFxdCategoryMaintenanceType.maintenanceTypeId)
            if (countMaintenance > 0) {
                result.put(Tools.MESSAGE, countMaintenance + HAS_ASSOCIATION_MESSAGE_MAPPING)
                return result
            }

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

            FxdCategoryMaintenanceType existingFxdCategoryMaintenanceType = fxdCategoryMaintenanceTypeCacheUtility.readByItemIdAndMaintenanceTypeIdForEdit(itemId, maintenanceTypeId, fxdCategoryMaintenanceTypeId)

            if (existingFxdCategoryMaintenanceType) {
                result.put(Tools.MESSAGE, MAPPING_ALREADY_EXISTS)
                return result
            }


            oldFxdCategoryMaintenanceType.version = version
            oldFxdCategoryMaintenanceType.itemId = item.id
            oldFxdCategoryMaintenanceType.maintenanceTypeId = fxdMaintenanceType.id
            oldFxdCategoryMaintenanceType.updatedOn = new Date()
            oldFxdCategoryMaintenanceType.updatedBy = user.id

            result.put(CATEGORY_MAINTENANCE_TYPE, oldFxdCategoryMaintenanceType)
            result.put(MAINTENANCE_TYPE_OBJ, fxdMaintenanceType)
            result.put(ITEM_OBJ, item)
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
     * 1. receive Category Maintenance Type object from pre execute method
     * 2. update new Category Maintenance Type
     * 3. update category maintenance type cache utility
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
            fxdCategoryMaintenanceTypeService.update(fxdCategoryMaintenanceType)
            fxdCategoryMaintenanceTypeCacheUtility.update(fxdCategoryMaintenanceType, fxdCategoryMaintenanceTypeCacheUtility.SORT_ON_NAME, fxdCategoryMaintenanceTypeCacheUtility.SORT_ORDER_DESCENDING)
            result.put(CATEGORY_MAINTENANCE_TYPE, fxdCategoryMaintenanceType)
            result.put(MAINTENANCE_TYPE_OBJ, receivedResult.get(MAINTENANCE_TYPE_OBJ))
            result.put(ITEM_OBJ, receivedResult.get(ITEM_OBJ))
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
     * 1. Category-Maintenance Type receive from execute method
     * 2. wrap object for grid entity
     * @param obj - object receive from execute method
     * @return - Category-Maintenance Type for grid entity
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map receivedResult = (Map) obj
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
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
}