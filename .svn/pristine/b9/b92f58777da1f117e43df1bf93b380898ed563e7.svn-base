package com.athena.mis.application.actions.item

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.ItemService
import com.athena.mis.application.utility.*
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


/**
 *  Class to create new item (Category: FixedAsset) and show on grid list
 *  For details go through Use-Case doc named 'CreateItemCategoryFixedAssetActionService'
 */
class CreateItemCategoryFixedAssetActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    ItemService itemService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ValuationTypeCacheUtility valuationTypeCacheUtility

    private static final String ITEM_CREATE_SUCCESS_MSG = "Item has been successfully saved"
    private static final String ITEM_CREATE_FAILURE_MSG = "Item has not been saved"
    private static final String ITEM_NAME_ALREADY_EXISTS = "Same item name already exists"
    private static final String ITEM = "item"

    /**
     * Check different criteria for creating new item
     *      1) Check access permission to create new item object
     *      2) Check duplicate item name
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
                return result
            }

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            Item item = buildItemObject(parameterMap)

            //Check duplicate name
            int duplicateName = itemCacheUtility.countByCategoryIdAndName(item.categoryId, item.name)
            if (duplicateName > 0) {
                result.put(Tools.MESSAGE, ITEM_NAME_ALREADY_EXISTS)
                return result
            }

            result.put(ITEM, item)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * Save item object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -itemObject send from previous method
     * @return -newly created item object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Item item = (Item) preResult.get(ITEM)
            Item newItem = itemService.create(item) //save in DB
            //save in cache and keep the data sorted
            itemCacheUtility.add(newItem, itemCacheUtility.NAME, itemCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ITEM, newItem)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ITEM_CREATE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ITEM_CREATE_FAILURE_MSG)
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
     * Wrap newly created item to show on grid
     * @param obj -newly created item object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
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
            result.put(Tools.MESSAGE, ITEM_CREATE_SUCCESS_MSG)
            result.put(ITEM, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ITEM_CREATE_FAILURE_MSG)
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
                result.put(Tools.MESSAGE, ITEM_CREATE_FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ITEM_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * build item object to create
     * @param parameterMap -grailsParameterMap
     * @return -item object
     */
    private Item buildItemObject(GrailsParameterMap parameterMap) {
        long companyId = appSessionUtil.getCompanyId()
        SystemEntity itemSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.FIXED_ASSET, companyId)
        SystemEntity valuationTypeAvgObj = (SystemEntity) valuationTypeCacheUtility.readByReservedAndCompany(valuationTypeCacheUtility.VALUATION_TYPE_AVG, companyId)

        Item item = new Item(parameterMap)
        item.companyId = companyId
        item.categoryId = itemSysEntityObject.id
        item.valuationTypeId = valuationTypeAvgObj.id //valuationType = AVG (for both IndividualEntity OR Not)
        item.isFinishedProduct = false
        item.createdOn = new Date()
        item.createdBy = appSessionUtil.getAppUser().id
        item.updatedOn = null
        item.updatedBy = 0
        return item
    }
}
