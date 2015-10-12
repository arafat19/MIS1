package com.athena.mis.fixedasset.actions.fixedassetdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.OwnerTypeCacheUtility
import com.athena.mis.fixedasset.entity.FxdFixedAssetDetails
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Select the Object of Fixed Asset Details
 * For details go through Use-Case doc named 'SelectFixedAssetDetailsActionService'
 */
class SelectFixedAssetDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    InventoryPluginConnector inventoryImplService
    ProcurementPluginConnector procurementImplService
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    OwnerTypeCacheUtility ownerTypeCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to Select Fixed Asset Details"
    private static final String FIXED_ASSET_DETAILS_NOT_FOUND = "Fixed Asset Details not found"
    private static final String FIXED_ASSET_DETAILS_OBJ = "fixedAssetDetails"
    private static final String FIXED_ASSET_DETAILS_MAP = "fixedAssetDetailsMap"

    /**
     * 1. pull fixed asset details object
     * 2. check fixed asset details existence
     * @param params - serialized parameters from UI
     * @param obj -N/A
     * @return - fixed asset details object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap paramsMap = (GrailsParameterMap) params
            long id = Long.parseLong(paramsMap.id.toString())

            FxdFixedAssetDetails fixedAssetDetails = FxdFixedAssetDetails.read(id)
            if (!fixedAssetDetails) {
                result.put(Tools.MESSAGE, FIXED_ASSET_DETAILS_NOT_FOUND)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(FIXED_ASSET_DETAILS_OBJ, fixedAssetDetails)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * 1. receive fixed asset details object from pre execute method
     * 2. build fixed asset details map
     * @param parameters -N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing fixed asset details map and fixed asset details object
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            FxdFixedAssetDetails fixedAssetDetails = (FxdFixedAssetDetails) preResult.get(FIXED_ASSET_DETAILS_OBJ)
            Map fixedAssetDetailsMap = buildFADetailsMap(fixedAssetDetails)
            result.put(FIXED_ASSET_DETAILS_OBJ, fixedAssetDetails)
            result.put(FIXED_ASSET_DETAILS_MAP, fixedAssetDetailsMap)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * 1. receive fixed asset details from execute method
     * @param obj- object returned from execute method
     * @return - a map containing wrapped fixed asset details for grid show
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            FxdFixedAssetDetails fixedAssetDetails = (FxdFixedAssetDetails) receiveResult.get(FIXED_ASSET_DETAILS_OBJ)
            result.put(Tools.ENTITY, fixedAssetDetails)
            result.put(Tools.VERSION, fixedAssetDetails.version)
            result.put(FIXED_ASSET_DETAILS_MAP, receiveResult.get(FIXED_ASSET_DETAILS_MAP))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * 1. pull inventory, purchase order details and item object
     * 2. calculate current item quantity
     * @param fixedAssetDetails - fixed asset details object
     * @return fixed asset details map
     */
    private Map buildFADetailsMap(FxdFixedAssetDetails fixedAssetDetails) {
        Object inventory = inventoryImplService.readInventory(fixedAssetDetails.currentInventoryId)
        Object purchaseOrderDetails = procurementImplService.readPODetailsByPurchaseOrderAndItem(fixedAssetDetails.poId, fixedAssetDetails.itemId)
        Item item = (Item) itemCacheUtility.read(purchaseOrderDetails.itemId)

        double currentQuantity = purchaseOrderDetails.quantity - purchaseOrderDetails.fixedAssetDetailsCount + 1

        Map inventoryList = [id: inventory.id, name: inventory.name]
        Map poList = [id: fixedAssetDetails.poId, name: fixedAssetDetails.poId]
        Map itemList = [
                id: item.id,
                name: item.name + Tools.PARENTHESIS_START + currentQuantity + Tools.SINGLE_SPACE + item.unit + Tools.PARENTHESIS_END,
                rate: fixedAssetDetails.cost,
                quantity: currentQuantity
        ]
        Map fixedAssetDetailsMap = [
                poList: Tools.listForKendoDropdown([poList],null,null),
                inventoryList: Tools.listForKendoDropdown([inventoryList], null, null),
                itemList: Tools.listForKendoDropdown([itemList],null,null),
                purchaseDate: DateUtility.getDateForUI(fixedAssetDetails.purchaseDate),
                expireDate: fixedAssetDetails.expireDate ? DateUtility.getDateForUI(fixedAssetDetails.expireDate) : null,
                inventoryTypeId: inventory.typeId
        ]
        return fixedAssetDetailsMap
    }
}