package com.athena.mis.fixedasset.actions.fixedassetdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.OwnerTypeCacheUtility
import com.athena.mis.fixedasset.entity.FxdFixedAssetDetails
import com.athena.mis.fixedasset.entity.FxdFixedAssetTrace
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
 * Create Fixed Asset Details and Fixed Asset Trace.
 * For details go through Use-Case doc named 'CreateFixedAssetDetailsActionService'
 */
class CreateFixedAssetDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    FixedAssetDetailsService fixedAssetDetailsService
    FixedAssetTraceService fixedAssetTraceService
    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired
    FxdSessionUtil fxdSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    OwnerTypeCacheUtility ownerTypeCacheUtility

    private static final String FA_DETAILS_SAVE_SUCCESS_MESSAGE = "Fixed Asset Details has been saved successfully"
    private static final String FA_DETAILS_SAVE_FAILURE_MESSAGE = "Can not save Fixed Asset Details"
    private static final String SERVER_ERROR_MESSAGE = "Can not save Fixed Asset Details"
    private static final String PURCHASE_ORDER_NOT_FOUND = "Purchase order may in use or changed. Please refresh the page"
    private static final String ITEM_NOT_FOUND = "Item may in use or change. Please refresh the page"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String FA_DETAILS_OBJ = "fixedAssetDetails"
    private static final String FA_TRACE_OBJ = "fixedAssetTrace"
    private static final String DUPLICATE_NAME = "Model/Serial already exists"
    private static final String ITEM_LIST = "itemList"
    private static final String ITEM_OBJ = "item"

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
            if (!parameterMap.poId || !parameterMap.inventoryId || !parameterMap.itemId
                    || !parameterMap.name || !parameterMap.ownerTypeId) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            long poId = Long.parseLong(parameterMap.poId.toString())
            long itemId = Long.parseLong(parameterMap.itemId.toString())
            Object purchaseOrderDetails = procurementImplService.readPODetailsByPurchaseOrderAndItem(poId, itemId)
            if (!purchaseOrderDetails) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND)
                return result
            }

            if (purchaseOrderDetails.fixedAssetDetailsCount >= purchaseOrderDetails.quantity) {
                result.put(Tools.MESSAGE, ITEM_NOT_FOUND)
                return result
            }

            FxdFixedAssetDetails fixedAssetDetails = buildFixedAssetDetailsObject(parameterMap, purchaseOrderDetails)
            FxdFixedAssetTrace fixedAssetTrace = buildFixedAssetTrace(fixedAssetDetails)
            Item item = (Item) itemCacheUtility.read(fixedAssetDetails.itemId)
            if (!item.isIndividualEntity) {
                result.put(Tools.MESSAGE, ITEM_NOT_FOUND)
                return result
            }

            int duplicateEntry = checkDuplicateEntryForCreate(fixedAssetDetails)
            if (duplicateEntry > 0) {
                result.put(Tools.MESSAGE, DUPLICATE_NAME)
                return result
            }

            result.put(ITEM_OBJ, item)
            result.put(FA_TRACE_OBJ, fixedAssetTrace)
            result.put(FA_DETAILS_OBJ, fixedAssetDetails)
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
     * 1. receive objects from pre execute method
     * 2. create new fixed asset details
     * 2. create fixed asset trace
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
            FxdFixedAssetTrace fixedAssetTrace = (FxdFixedAssetTrace) preResult.get(FA_TRACE_OBJ)
            FxdFixedAssetDetails newDetails = fixedAssetDetailsService.create(fixedAssetDetails)
            fixedAssetTrace.fixedAssetDetailsId = newDetails.id
            fixedAssetTraceService.create(fixedAssetTrace)
            increaseFixedAssetDetailsCount(fixedAssetDetails.poId, fixedAssetDetails.itemId)
            result.put(ITEM_OBJ, preResult.get(ITEM_OBJ))
            result.put(FA_DETAILS_OBJ, newDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Error to create Fixed Asset Details")
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FA_DETAILS_SAVE_FAILURE_MESSAGE)
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
    @Transactional(readOnly = true)
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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
            result.put(Tools.MESSAGE, FA_DETAILS_SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
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
                result.put(Tools.MESSAGE, FA_DETAILS_SAVE_FAILURE_MESSAGE)
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
    private FxdFixedAssetDetails buildFixedAssetDetailsObject(GrailsParameterMap params, Object purchaseOrderDetails) {
        FxdFixedAssetDetails fixedAssetDetails = new FxdFixedAssetDetails()
        Object purchaseOrder = procurementImplService.readPO(purchaseOrderDetails.purchaseOrderId)
        AppUser user = fxdSessionUtil.appSessionUtil.getAppUser()
        fixedAssetDetails.id = 0
        fixedAssetDetails.version = 0
        fixedAssetDetails.itemId = purchaseOrderDetails.itemId
        fixedAssetDetails.cost = purchaseOrderDetails.rate
        fixedAssetDetails.name = params.name
        fixedAssetDetails.description = params.description ? params.description : null
        fixedAssetDetails.currentInventoryId = Long.parseLong(params.inventoryId.toString())
        fixedAssetDetails.ownerTypeId = Long.parseLong(params.ownerTypeId.toString())
        fixedAssetDetails.poId = purchaseOrder.id
        fixedAssetDetails.supplierId = purchaseOrder.supplierId
        fixedAssetDetails.projectId = purchaseOrder.projectId
        fixedAssetDetails.purchaseDate = DateUtility.parseMaskedDate(params.purchaseDate.toString())
        fixedAssetDetails.expireDate = params.expireDate ? DateUtility.parseMaskedDate(params.expireDate.toString()) : null

        fixedAssetDetails.createdOn = new Date()
        fixedAssetDetails.createdBy = user.id
        fixedAssetDetails.updatedOn = null
        fixedAssetDetails.updatedBy = 0L
        fixedAssetDetails.companyId = user.companyId
        return fixedAssetDetails
    }
    /**
     * Build fixed asset trace object
     * @param fixedAssetDetails - fixed asset details object
     * @return fixed asset trace object
     */
    private FxdFixedAssetTrace buildFixedAssetTrace(FxdFixedAssetDetails fixedAssetDetails) {
        FxdFixedAssetTrace fixedAssetTrace = new FxdFixedAssetTrace()

        fixedAssetTrace.version = 0
        fixedAssetTrace.fixedAssetDetailsId = 0 // will be assigned after creating Fixed Asset Details
        fixedAssetTrace.itemId = fixedAssetDetails.itemId
        fixedAssetTrace.inventoryId = fixedAssetDetails.currentInventoryId
        fixedAssetTrace.transactionDate = fixedAssetDetails.purchaseDate
        fixedAssetTrace.createdBy = fixedAssetDetails.createdBy
        fixedAssetTrace.createdOn = fixedAssetDetails.createdOn
        fixedAssetTrace.companyId = fixedAssetDetails.companyId
        fixedAssetTrace.comments = null
        fixedAssetTrace.isCurrent = Boolean.TRUE

        return fixedAssetTrace
    }

    // read FxdFixedAssetDetails if object exists with same name
    private static final String QUERY_COUNT = """
                        SELECT COUNT(id) count
                        FROM fxd_fixed_asset_details
                        WHERE name ilike :name
                      """
    private int checkDuplicateEntryForCreate(FxdFixedAssetDetails fixedAssetDetails) {
        Map queryParams = [
                name: fixedAssetDetails.name
        ]
        List<GroovyRowResult> resultCount = executeSelectSql(QUERY_COUNT, queryParams)
        return resultCount[0].count
    }

    //increase fixed asset details count of purchase order details
    private static final String QUERY_UPDATE = """
                    UPDATE proc_purchase_order_details
                            SET
                               fixed_asset_details_count = fixed_asset_details_count + 1
                            WHERE purchase_order_id = :poId
                                   AND item_id = :itemId
                    """
    private int increaseFixedAssetDetailsCount(long poId, long itemId) {
        Map queryParams = [
                poId: poId,
                itemId: itemId
        ]
        int updateCountPOD = executeUpdateSql(QUERY_UPDATE, queryParams);
        if (updateCountPOD <= 0) {
            throw new RuntimeException("Error to update Purchase Order Details")
        }
        return updateCountPOD
    }
}
