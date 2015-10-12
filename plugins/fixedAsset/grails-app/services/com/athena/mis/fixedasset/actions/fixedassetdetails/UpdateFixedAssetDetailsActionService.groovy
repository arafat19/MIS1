package com.athena.mis.fixedasset.actions.fixedassetdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.OwnerTypeCacheUtility
import com.athena.mis.fixedasset.entity.FxdFixedAssetDetails
import com.athena.mis.fixedasset.service.FixedAssetDetailsService
import com.athena.mis.fixedasset.service.FixedAssetTraceService
import com.athena.mis.fixedasset.utility.FxdSessionUtil
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Update Fixed Asset Details and Fixed Asset Trace
 * For details go through Use-Case doc named 'UpdateFixedAssetDetailsActionService'
 */
class UpdateFixedAssetDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    FixedAssetDetailsService fixedAssetDetailsService
    FixedAssetTraceService fixedAssetTraceService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired
    FxdSessionUtil fxdSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    OwnerTypeCacheUtility ownerTypeCacheUtility

    private static final String FA_DETAILS_UPDATE_SUCCESS_MESSAGE = "Fixed Asset Details has been updated successfully"
    private static final String FA_DETAILS_UPDATE_FAILURE_MESSAGE = "Can not update Fixed Asset Details"
    private static final String SERVER_ERROR_MESSAGE = "Can not be updated Fixed Asset Details"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String FA_DETAILS_OBJ = "fixedAssetDetails"
    private static final String NOT_FOUND = "Fixed Asset Details not found"
    private static final String DUPLICATE_NAME = "Model/Serial already exists"
    private static final String ITEM_LIST = "itemList"
    private static final String PO_LIST = "poList"
    private static final String ITEM_OBJ = "item"
    private static final String ITEM_NOT_FOUND = "Item may in use or change. Please refresh the page"


    /**
     * 1. check all input validations
     * 2. pull purchase order details object by po and item
     * 3. check po existence
     * 4. check item existence
     * 5. build Fixed Asset Details Object
     * 6. build Fixed Asset Object
     * 7. pull item from item cache utility
     * 8. duplicate check for fixed asset
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing item object, fixed asset trace object and fixed asset details object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if (!parameterMap.id || !parameterMap.version || !parameterMap.name || !parameterMap.ownerTypeId) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            long fixedAssetDetailsId = Long.parseLong(parameterMap.id)
            int version = Integer.parseInt(parameterMap.version)
            FxdFixedAssetDetails oldFixedAssetDetails = FxdFixedAssetDetails.read(fixedAssetDetailsId)
            if ((!oldFixedAssetDetails) || (oldFixedAssetDetails.version != version)) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }

            FxdFixedAssetDetails newFixedAssetDetails = buildFixedAssetDetailsObject(parameterMap, version, oldFixedAssetDetails)

            Item item = (Item) itemCacheUtility.read(newFixedAssetDetails.itemId)
            if (!item.isIndividualEntity) {
                result.put(Tools.MESSAGE, ITEM_NOT_FOUND)
                return result
            }

            int duplicateEntry = checkDuplicateEntryForUpdate(newFixedAssetDetails)
            if (duplicateEntry > 0) {
                result.put(Tools.MESSAGE, DUPLICATE_NAME)
                return result
            }

            result.put(ITEM_OBJ, item)
            result.put(FA_DETAILS_OBJ, newFixedAssetDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * 1. receive fixed asset details object from pre execute method
     * 2. update new fixed asset details
     * 2. update fixed asset trace
     * 3. update proc_purchase_order_details for item count
     * @param parameters -N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing item object and fixed asset details object
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            FxdFixedAssetDetails fixedAssetDetails = (FxdFixedAssetDetails) preResult.get(FA_DETAILS_OBJ)
            fixedAssetDetailsService.update(fixedAssetDetails)
            fixedAssetTraceService.update(fixedAssetDetails)
            List<GroovyRowResult> poList = procurementImplService.getPOListOfFixedAsset()
            result.put(ITEM_OBJ, preResult.get(ITEM_OBJ))
            result.put(FA_DETAILS_OBJ, fixedAssetDetails)
            result.put(PO_LIST, Tools.listForKendoDropdown(poList,null,null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to update Fixed Asset Details')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
     * 1. fixed asset details receive from execute method
     * 2. pull inventory object
     * 3. item receive from execute method
     * 4. pull owner type from System Entity
     * 5. wrap object for grid entity
     * @param obj - object receive from execute method
     * @return - item list and fixed asset details
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            GridEntity object = new GridEntity()
            FxdFixedAssetDetails fixedAssetDetails = (FxdFixedAssetDetails) receiveResult.get(FA_DETAILS_OBJ)
            Object inventory = inventoryImplService.readInventory(fixedAssetDetails.currentInventoryId)
            Item item = (Item) receiveResult.get(ITEM_OBJ)
            SystemEntity ownerType = (SystemEntity) ownerTypeCacheUtility.read(fixedAssetDetails.ownerTypeId)
            String purchaseDate = DateUtility.getLongDateForUI(fixedAssetDetails.purchaseDate)
            object.id = fixedAssetDetails.id
            object.cell = [
                    Tools.LABEL_NEW,
                    fixedAssetDetails.id,
                    item.name,
                    fixedAssetDetails.name,
                    inventory.name,
                    Tools.makeAmountWithThousandSeparator(fixedAssetDetails.cost),
                    purchaseDate,
                    fixedAssetDetails.poId,
                    ownerType.key
            ]

            List<GroovyRowResult> itemList = procurementImplService.getFixedAssetListByPOId(fixedAssetDetails.poId)
            result.put(ITEM_LIST, itemList)
            result.put(FA_DETAILS_OBJ, fixedAssetDetails)
            result.put(Tools.MESSAGE, FA_DETAILS_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(PO_LIST, receiveResult.get(PO_LIST))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FA_DETAILS_UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Build Fixed Asset Details object
     * @param params - serialize parameters from UI
     * @param purchaseOrderDetails - po details object
     * @return Fixed Asset Details object
     */
    private FxdFixedAssetDetails buildFixedAssetDetailsObject(GrailsParameterMap params, int version, FxdFixedAssetDetails oldFixedAssetDetails) {
        FxdFixedAssetDetails fixedAssetDetails = new FxdFixedAssetDetails()

        fixedAssetDetails.id = oldFixedAssetDetails.id
        fixedAssetDetails.version = version
        fixedAssetDetails.itemId = oldFixedAssetDetails.itemId
        fixedAssetDetails.cost = oldFixedAssetDetails.cost
        fixedAssetDetails.name = params.name
        fixedAssetDetails.description = params.description ? params.description : null
        fixedAssetDetails.currentInventoryId = oldFixedAssetDetails.currentInventoryId
        fixedAssetDetails.poId = oldFixedAssetDetails.poId
        fixedAssetDetails.supplierId = oldFixedAssetDetails.supplierId
        fixedAssetDetails.projectId = oldFixedAssetDetails.projectId
        fixedAssetDetails.purchaseDate = DateUtility.parseMaskedDate(params.purchaseDate.toString())

        fixedAssetDetails.ownerTypeId = Long.parseLong(params.ownerTypeId.toString())
        long companyId = fxdSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity rentalObj = (SystemEntity) ownerTypeCacheUtility.readByReservedAndCompany(ownerTypeCacheUtility.RENTAL, companyId)
        if (fixedAssetDetails.ownerTypeId == rentalObj.id) {
            fixedAssetDetails.expireDate = DateUtility.parseMaskedDate(params.expireDate.toString())
        } else {
            fixedAssetDetails.expireDate = null
        }

        fixedAssetDetails.createdOn = oldFixedAssetDetails.createdOn
        fixedAssetDetails.createdBy = oldFixedAssetDetails.createdBy
        fixedAssetDetails.updatedOn = new Date()
        fixedAssetDetails.updatedBy = fxdSessionUtil.appSessionUtil.getAppUser().id
        return fixedAssetDetails
    }


    private static final String QUERY_SELECT = """
                        SELECT COUNT(id) count
                        FROM fxd_fixed_asset_details
                        WHERE name ILIKE :name
                        AND id <> :id
                      """
    // read FxdFixedAssetDetails if object exists with same name
    private int checkDuplicateEntryForUpdate(FxdFixedAssetDetails fixedAssetDetails) {
        //@todo:model adjust using dynamic finder
        Map queryParams = [
                name: fixedAssetDetails.name,
                id: fixedAssetDetails.id
        ]
        List<GroovyRowResult> resultCount = executeSelectSql(QUERY_SELECT, queryParams)
        return resultCount[0].count
    }
}
