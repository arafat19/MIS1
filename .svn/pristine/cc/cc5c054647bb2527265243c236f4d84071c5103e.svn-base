package com.athena.mis.fixedasset.actions.fxdmaintenance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.fixedasset.entity.FxdFixedAssetDetails
import com.athena.mis.fixedasset.entity.FxdMaintenance
import com.athena.mis.fixedasset.entity.FxdMaintenanceType
import com.athena.mis.fixedasset.service.FixedAssetDetailsService
import com.athena.mis.fixedasset.service.FxdMaintenanceService
import com.athena.mis.fixedasset.utility.FxdMaintenanceTypeCacheUtility
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Update Maintenance for fixed asset.
 * For details go through Use-Case doc named 'FxdUpdateMaintenanceActionService'
 */
class FxdUpdateMaintenanceActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    FxdMaintenanceService fxdMaintenanceService
    FixedAssetDetailsService fixedAssetDetailsService
    @Autowired
    FxdSessionUtil fxdSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    FxdMaintenanceTypeCacheUtility fxdMaintenanceTypeCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility

    private static final String UPDATE_SUCCESS_MESSAGE = "Fixed Asset Maintenance has been updated successfully"
    private static final String UPDATE_FAILURE_MESSAGE = "Fixed Asset Maintenance could not be updated"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred due to invalid input"
    private static final String CATEGORY_NOT_FOUND_ERROR = "Category not found"
    private static final String MODEL_NOT_FOUND_ERROR = "Model/Serial No not found"
    private static final String MAINTENANCE_TYPE_NOT_FOUND_ERROR = "Maintenance Type not found"
    private static final String INVALID_DATE_ERROR = "Invalid Date not found"
    private static final String FXD_MAINTENANCE_OBJ = "fxdMaintenance"
    private static final String MAINTENANCE_TYPE_OBJ = "fxdMaintenanceType"
    private static final String ITEM_OBJ = "item"
    private static final String FIXED_ASSET_DETAILS_OBJ = "fixedAssetDetails"
    private static final String NOT_FOUND = "Fixed Asset Maintenance may in use or has been changed. Please refresh the page"
    /**
     * 1. check required fields
     * 2. pull maintenance object
     * 3. check fixed asset maintenance for company id and version
     * 4. pull item object & check category existence
     * 5. pull fixed asset details object & check model/serial existence
     * 6. pull maintenance type from cache utility and check existence
     * 7. check current date validation for maintenance
     * 8. set fixed asset Maintenance for update
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
            if (!parameterMap.id || !parameterMap.itemId || !parameterMap.fixedAssetDetailsId
                    || !parameterMap.maintenanceTypeId || !parameterMap.description
                    || !parameterMap.maintenanceDate) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }

            long fxdMaintenanceId = Long.parseLong(parameterMap.id.toString())
            int version = Integer.parseInt(parameterMap.version.toString())
            long itemId = Long.parseLong(parameterMap.itemId.toString())
            long fixedAssetDetailsId = Long.parseLong(parameterMap.fixedAssetDetailsId.toString())
            long maintenanceTypeId = Long.parseLong(parameterMap.maintenanceTypeId.toString())
            double amount = parameterMap.amount ? Double.parseDouble(parameterMap.amount.toString()) : 0.0d
            Date maintenanceDate = DateUtility.parseMaskedFromDate(parameterMap.maintenanceDate.toString())
            Date currentDate = new Date()
            AppUser user = fxdSessionUtil.appSessionUtil.getAppUser()

            FxdMaintenance fxdMaintenance = (FxdMaintenance) fxdMaintenanceService.read(fxdMaintenanceId)

            if ((!fxdMaintenance)
                    || (fxdMaintenance.companyId != user.companyId)
                    || (version != fxdMaintenance.version)) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }

            Item item = (Item) itemCacheUtility.read(itemId)
            if (!item) {
                result.put(Tools.MESSAGE, CATEGORY_NOT_FOUND_ERROR)
                return result
            }

            FxdFixedAssetDetails fixedAssetDetails = fixedAssetDetailsService.read(fixedAssetDetailsId)
            if (!fixedAssetDetails) {
                result.put(Tools.MESSAGE, MODEL_NOT_FOUND_ERROR)
                return result
            }

            FxdMaintenanceType fxdMaintenanceType = (FxdMaintenanceType) fxdMaintenanceTypeCacheUtility.read(maintenanceTypeId)
            if (!fxdMaintenanceType) {
                result.put(Tools.MESSAGE, MAINTENANCE_TYPE_NOT_FOUND_ERROR)
                return result
            }

            if (currentDate.compareTo(maintenanceDate) < 0) {
                result.put(Tools.MESSAGE, INVALID_DATE_ERROR)
                return result
            }


            fxdMaintenance.version = version
            fxdMaintenance.itemId = item.id
            fxdMaintenance.fixedAssetDetailsId = fixedAssetDetails.id
            fxdMaintenance.maintenanceTypeId = fxdMaintenanceType.id
            fxdMaintenance.amount = amount
            fxdMaintenance.maintenanceDate = maintenanceDate
            fxdMaintenance.description = parameterMap.description.toString()
            fxdMaintenance.updatedOn = new Date()
            fxdMaintenance.updatedBy = user.id

            result.put(FXD_MAINTENANCE_OBJ, fxdMaintenance)
            result.put(MAINTENANCE_TYPE_OBJ, fxdMaintenanceType)
            result.put(ITEM_OBJ, item)
            result.put(FIXED_ASSET_DETAILS_OBJ, fixedAssetDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }
    /**
     * 1. receive fixed asset maintenance object from pre execute method
     * 2. update new fixed asset maintenance
     * 3. update maintenance cache utility
     * @param parameters -N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing fixed asset maintenance & isError(true/false)
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map receivedResult = (Map) obj
            FxdMaintenance fxdMaintenance = (FxdMaintenance) receivedResult.get(FXD_MAINTENANCE_OBJ)
            int updateCount = fxdMaintenanceService.update(fxdMaintenance)
            result.put(FXD_MAINTENANCE_OBJ, fxdMaintenance)
            result.put(MAINTENANCE_TYPE_OBJ, receivedResult.get(MAINTENANCE_TYPE_OBJ))
            result.put(ITEM_OBJ, receivedResult.get(ITEM_OBJ))
            result.put(FIXED_ASSET_DETAILS_OBJ, receivedResult.get(FIXED_ASSET_DETAILS_OBJ))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to update Fixed Asset Maintenance')
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
     * 1. fixed asset maintenance receive from execute method
     * 2. wrap object for grid entity
     * @param obj - object receive from execute method
     * @return - fixed asset maintenance for grid entity
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map receivedResult = (Map) obj
            FxdMaintenance fxdMaintenance = (FxdMaintenance) receivedResult.get(FXD_MAINTENANCE_OBJ)
            Item item = (Item) receivedResult.get(ITEM_OBJ)
            FxdFixedAssetDetails fixedAssetDetails = (FxdFixedAssetDetails) receivedResult.get(FIXED_ASSET_DETAILS_OBJ)
            FxdMaintenanceType fxdMaintenanceType = (FxdMaintenanceType) receivedResult.get(MAINTENANCE_TYPE_OBJ)
            AppUser user = (AppUser) appUserCacheUtility.read(fxdMaintenance.createdBy)
            String description = Tools.makeDetailsShort(fxdMaintenance.description, Tools.DEFAULT_LENGTH_DETAILS_OF_BUDGET)

            GridEntity object = new GridEntity()
            object.id = fxdMaintenance.id
            object.cell = [
                    Tools.LABEL_NEW,
                    item.name + Tools.SINGLE_SPACE + Tools.PARENTHESIS_START + fixedAssetDetails.name + Tools.PARENTHESIS_END,
                    fxdMaintenanceType.name,
                    Tools.makeAmountWithThousandSeparator(fxdMaintenance.amount),
                    DateUtility.getLongDateForUI(fxdMaintenance.maintenanceDate),
                    user.username,
                    description
            ]

            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, fxdMaintenance.version)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
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
}