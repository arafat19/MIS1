package com.athena.mis.fixedasset.actions.fxdmaintenance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
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
 * Create Maintenance and show in the grid
 * For details go through Use-Case doc named 'FxdCreateMaintenanceActionService'
 */
class FxdCreateMaintenanceActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    FxdMaintenanceService fxdMaintenanceService
    FixedAssetDetailsService fixedAssetDetailsService
    @Autowired
    FxdSessionUtil fxdSessionUtil
    @Autowired
    FxdMaintenanceTypeCacheUtility fxdMaintenanceTypeCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility

    private static final String SAVE_SUCCESS_MESSAGE = "Fixed Asset Maintenance has been saved successfully"
    private static final String SAVE_FAILURE_MESSAGE = "Fixed Asset Maintenance could not be saved"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred due to invalid input"
    private static final String CATEGORY_NOT_FOUND_ERROR = "Category not found"
    private static final String MODEL_NOT_FOUND_ERROR = "Model/Serial No not found"
    private static final String MAINTENANCE_TYPE_NOT_FOUND_ERROR = "Maintenance type not found"
    private static final String INVALID_DATE_ERROR = "Maintenance date could not be future date"
    private static final String FXD_MAINTENANCE_OBJ = "fxdMaintenance"
    private static final String MAINTENANCE_TYPE_OBJ = "fxdMaintenanceType"
    private static final String ITEM_OBJ = "item"
    private static final String FIXED_ASSET_DETAILS_OBJ = "fixedAssetDetails"
    private static final String APP_USER_OBJ = "appUser"

    /**
     * Get newly created fixed asset Maintenance object
     * 1. Check invalid input error by parameterMap
     * 2. Get item object by itemId from itemCacheUtility
     * 3. Check the existence of item object
     * 4. Get fixed Asset Details by fixedAssetDetailsId from fixedAssetDetailsService
     * 5. Get maintenance type object by maintenanceTypeId from fxdMaintenanceTypeCacheUtility
     * 6. Check the existence of Maintenance Type
     * 7. Check invalid date error by  if (currentDate.compareTo(maintenanceDate) < 0)
     * 8. Build maintenance object by using buildMaintenanceObject method
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return - Map containing maintenance object, maintenance type object, item
     * -map contains fixedAssetDetails, app user & isError(true/false) with relevant message
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if (!parameterMap.itemId || !parameterMap.fixedAssetDetailsId
                    || !parameterMap.maintenanceTypeId || !parameterMap.description || !parameterMap.maintenanceDate) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }

            long itemId = Long.parseLong(parameterMap.itemId.toString())
            long fixedAssetDetailsId = Long.parseLong(parameterMap.fixedAssetDetailsId.toString())
            long maintenanceTypeId = Long.parseLong(parameterMap.maintenanceTypeId.toString())
            double amount = parameterMap.amount ? Double.parseDouble(parameterMap.amount.toString()) : 0.0d
            Date maintenanceDate = DateUtility.parseMaskedFromDate(parameterMap.maintenanceDate.toString())
            Date currentDate = new Date()

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
            AppUser user = fxdSessionUtil.appSessionUtil.getAppUser()
            FxdMaintenance fxdMaintenance = buildMaintenanceObject(user, itemId, fixedAssetDetailsId, maintenanceTypeId,
                    amount, maintenanceDate, currentDate, parameterMap)
            result.put(FXD_MAINTENANCE_OBJ, fxdMaintenance)
            result.put(MAINTENANCE_TYPE_OBJ, fxdMaintenanceType)
            result.put(ITEM_OBJ, item)
            result.put(FIXED_ASSET_DETAILS_OBJ, fixedAssetDetails)
            result.put(APP_USER_OBJ, user)
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
     * Save fixed asset maintenance object in the DB
     * 1. Get fixed asset maintenance object from executePreCondition method
     * 2. Create fixed asset maintenance object using fxdMaintenanceService
     * 3. This method is in transactional boundary and will roll back in case of any exception
     * @param parameters - N/A
     * @param obj - map returned from executePreCondition method
     * @returna - map containing all objects necessary for executePostCondition
     * -map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map receivedResult = (Map) obj
            FxdMaintenance fxdMaintenance = (FxdMaintenance) receivedResult.get(FXD_MAINTENANCE_OBJ)
            FxdMaintenance newFxdMaintenance = fxdMaintenanceService.create(fxdMaintenance)
            result.put(FXD_MAINTENANCE_OBJ, newFxdMaintenance)
            result.put(MAINTENANCE_TYPE_OBJ, receivedResult.get(MAINTENANCE_TYPE_OBJ))
            result.put(ITEM_OBJ, receivedResult.get(ITEM_OBJ))
            result.put(FIXED_ASSET_DETAILS_OBJ, receivedResult.get(FIXED_ASSET_DETAILS_OBJ))
            result.put(APP_USER_OBJ, receivedResult.get(APP_USER_OBJ))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to create Fixed Asset Maintenance')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * 1. Receive fixed asset maintenance object
     * 2. Get item object
     * 3. Get fixedAssetDetails object
     * 4. Get fxdMaintenanceType object
     * 5. Get user object
     * 6. Show newly created maintenance object in grid
     * 7. Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * -map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            FxdMaintenance fxdMaintenance = (FxdMaintenance) receivedResult.get(FXD_MAINTENANCE_OBJ)
            Item item = (Item) receivedResult.get(ITEM_OBJ)
            FxdFixedAssetDetails fixedAssetDetails = (FxdFixedAssetDetails) receivedResult.get(FIXED_ASSET_DETAILS_OBJ)
            FxdMaintenanceType fxdMaintenanceType = (FxdMaintenanceType) receivedResult.get(MAINTENANCE_TYPE_OBJ)
            AppUser user = (AppUser) receivedResult.get(APP_USER_OBJ)
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
     * Build fixed asset maintenance object
     * @param user - user object
     * @param itemId - item id
     * @param fixedAssetDetailsId - fixed asset details id
     * @param maintenanceTypeId - maintenance type id
     * @param amount - Given amount from UI
     * @param maintenanceDate - maintenance date from UI
     * @param currentDate - current date
     * @param parameterMap - params from UI
     * @return - a maintenance object
     */
    private static FxdMaintenance buildMaintenanceObject(AppUser user, long itemId, long fixedAssetDetailsId, long maintenanceTypeId,
                                                         double amount, Date maintenanceDate, Date currentDate, GrailsParameterMap parameterMap) {
        FxdMaintenance fxdMaintenance = new FxdMaintenance()
        fxdMaintenance.version = 0
        fxdMaintenance.itemId = itemId
        fxdMaintenance.fixedAssetDetailsId = fixedAssetDetailsId
        fxdMaintenance.maintenanceTypeId = maintenanceTypeId
        fxdMaintenance.amount = amount
        fxdMaintenance.maintenanceDate = maintenanceDate
        fxdMaintenance.description = parameterMap.description.toString()
        fxdMaintenance.createdBy = user.id
        fxdMaintenance.createdOn = currentDate
        fxdMaintenance.updatedOn = null
        fxdMaintenance.updatedBy = 0L
        fxdMaintenance.companyId = user.companyId

        return fxdMaintenance
    }

}

