package com.athena.mis.application.actions.item

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.ItemService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.ValuationTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update item object (Category: FixedAsset) and to show on grid list
 *  For details go through Use-Case doc named 'UpdateItemCategoryFixedAssetActionService'
 */
class UpdateItemCategoryFixedAssetActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    ItemService itemService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    ValuationTypeCacheUtility valuationTypeCacheUtility

    private static final String ITEM_UPDATE_FAILURE_MESSAGE = "Item could not be updated"
    private static final String ITEM_UPDATE_SUCCESS_MESSAGE = "Item has been updated successfully"
    private static final String INPUT_VALIDATION_ERROR_MSG = "Given inputs are not valid"
    private static final String ITEM_NAME_ALREADY_EXISTS = "Same item name already exists"
    private static final String HAS_ASSOCIATED_INV_TRANSACTION = "This item has association with inventory transaction"
    private static final String ITEM = "item"
    private static final String HAS_ASSOCIATION_FIXED_ASSET = " fixed asset details associated with selected item"
    private static final String OBJ_NOT_FOUND = "Selected item not found"

    /**
     * Check different criteria to update item object
     *      1) Check access permission to update item object
     *      2) Check existence of selected item object
     *      3) Validate item object to update
     *      4) Check duplicate item name
     * @param params -parameter send from UI
     * @param obj -N/A
     * @return -a map containing item object for execute method
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            //Only admin can create item object
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                result.put(Tools.MESSAGE, ITEM_UPDATE_FAILURE_MESSAGE)
                return result
            }
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long itemId = Long.parseLong(params.id.toString())
            Item oldItem = (Item) itemCacheUtility.read(itemId)
            if (!oldItem) {//Check existence of selected item object
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            //Build object for update
            Item item = buildItemObject(params, oldItem)

            //Check duplicate Item name
            int duplicateName = itemCacheUtility.countByCategoryIdAndNameAndIdNotEqual(item.categoryId, item.name, item.id)
            if (duplicateName > 0) {
                result.put(Tools.MESSAGE, ITEM_NAME_ALREADY_EXISTS)
                return result
            }

            //if isIndividualEntity property changed then check the association
            if (oldItem.isIndividualEntity != item.isIndividualEntity) {
                if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
                    int itemCountInInventoryTransaction = countInventoryTransactionDetails(item.id)
                    if (itemCountInInventoryTransaction > 0) {
                        result.put(Tools.MESSAGE, HAS_ASSOCIATED_INV_TRANSACTION)
                        return result
                    }
                }

                int countFAD = countFixedAssetDetails(item.id, item.companyId)
                if (countFAD > 0) {
                    result.put(Tools.MESSAGE, countFAD + HAS_ASSOCIATION_FIXED_ASSET)
                    return result
                }
            }

            result.put(ITEM, item)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ITEM_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * update item object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -itemObject send from controller
     * @return -newly updated item object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Item item = (Item) preResult.get(ITEM)
            itemService.update(item)//update in DB
            item.version = item.version + 1
            //update in cache and keep the data sorted
            itemCacheUtility.update(item, itemCacheUtility.NAME, itemCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ITEM, item)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ITEM_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ITEM_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap updated item object to show on grid
     * @param obj -updated item object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            Item item = (Item) executeResult.get(ITEM)
            GridEntity object = new GridEntity()
            ItemType itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            object.id = item.id
            object.cell = [
                    Tools.LABEL_NEW,
                    item.id,
                    item.name,
                    item.code,
                    item.unit,
                    itemType.name,
                    item.isIndividualEntity ? Tools.YES : Tools.NO
            ]

            Map resultMap = [entity: object, version: item.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, ITEM_UPDATE_SUCCESS_MESSAGE)
            result.put(ITEM, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ITEM_UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, ITEM_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ITEM_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build item object to update
     * @param parameterMap -GrailsParameterMap
     * @param oldItem -Item object
     * @return -item object
     */
    private Item buildItemObject(GrailsParameterMap parameterMap, Item oldItem) {
        long companyId = appSessionUtil.getCompanyId()
        SystemEntity valuationTypeAvgObj = (SystemEntity) valuationTypeCacheUtility.readByReservedAndCompany(valuationTypeCacheUtility.VALUATION_TYPE_AVG, companyId)

        Item item = new Item(parameterMap)

        item.id = oldItem.id
        item.version = oldItem.version
        item.companyId = oldItem.companyId
        item.categoryId = oldItem.categoryId
        item.isFinishedProduct = oldItem.isFinishedProduct
        item.valuationTypeId = valuationTypeAvgObj.id  //valuationType = AVG (for both IndividualEntity OR Not)
        item.updatedBy = appSessionUtil.getAppUser().id
        item.updatedOn = new Date()
        return item
    }

    private static final String INV_INVENTORY_TRANSACTION_DETAILS = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction_details
            WHERE item_id = :itemId """
    /**
     * count number of item(s) associated with inventory_transaction_details
     * @param itemId -Item.id
     * @return -int value
     */
    private int countInventoryTransactionDetails(long itemId) {
        List results = executeSelectSql(INV_INVENTORY_TRANSACTION_DETAILS, [itemId: itemId])
        int count = results[0].count
        return count
    }

    private static final String FXD_FIXED_ASSET_DETAILS = """
            SELECT COUNT(id) AS count
            FROM fxd_fixed_asset_details
            WHERE item_id = :itemId AND
                  company_id = :companyId
            """
    /**
     * count number of item(s) associated with fixed_asset_details
     * @param itemId -Item.id
     * @param companyId -Company.id
     * @return -int value
     */
    private int countFixedAssetDetails(long itemId, long companyId) {
        Map queryParams = [
                itemId: itemId,
                companyId: companyId
        ]
        List results = executeSelectSql(FXD_FIXED_ASSET_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

}
