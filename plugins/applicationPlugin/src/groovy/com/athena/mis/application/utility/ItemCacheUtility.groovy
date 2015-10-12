package com.athena.mis.application.utility

import com.athena.mis.BaseService
import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.ItemService
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 *  For details go through Use-Case doc named 'ItemCacheUtility'
 */
@Component('itemCacheUtility')
class ItemCacheUtility extends ExtendedCacheUtility {

    @Autowired
    ItemService itemService
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility

    public static final String NAME = "name";
    public static final String FAILURE_MESSAGE = 'Failed to populate Item list'
    public static final String SORT_ORDER_ASCENDING = 'asc';
    public static final String SORT_ORDER_DESCENDING = 'desc';

    /**
     * pull all list of items and store list in cache
     */
    @Transactional(readOnly = true)
    public void init() {
        try {
            List lstItems = itemService.list();
            super.setList(lstItems)
        } catch (Exception e) {
            throw new RuntimeException(FAILURE_MESSAGE)
        }
    }

    // Get list of items by itemTypeId
    public List<Item> listByItemTypeId(long itemTypeId) {
        List<Item> lstSpecificItems = []
        List<Item> lstAllItems = (List<Item>) super.list()
        if (lstAllItems.size() <= 0)
            return lstSpecificItems
        for (int i = 0; i < lstAllItems.size(); i++) {
            if (lstAllItems[i].itemTypeId == itemTypeId) {
                lstSpecificItems << lstAllItems[i]
            }
        }
        return lstSpecificItems
    }

    // used to populate drop-down with full name
    public List listByTypeForDropDown(long itemTypeId) {
        List lstSpecificItems = []
        List<Item> itemList = listByItemTypeId(itemTypeId)
        if (itemList.size() <= 0) return lstSpecificItems
        Map customItem
        for (int i = 0; i < itemList.size(); i++) {
            customItem = [id: itemList[i].id, name: itemList[i].name]
            lstSpecificItems << customItem
        }
        return lstSpecificItems
    }

    // Get list of items by itemCategoryId
    public List<Item> listItemByCategoryId(long categoryId) {
        List<Item> lstSpecificItems = []
        List<Item> lstAllItems = (List<Item>) super.list()
        if (lstAllItems.size() <= 0)
            return lstSpecificItems
        for (int i = 0; i < lstAllItems.size(); i++) {
            if (lstAllItems[i].categoryId == categoryId) {
                lstSpecificItems << lstAllItems[i]
            }
        }
        return lstSpecificItems
    }

    public Map listItemByCategoryId(long categoryId, BaseService baseService) {
        List<Item> lstSpecificItems = []
        List<Item> lstAllItems = (List<Item>) super.list()
        if (lstAllItems.size() <= 0)
            return [list: [], count: 0]
        for (int i = 0; i < lstAllItems.size(); i++) {
            if (lstAllItems[i].categoryId == categoryId) {
                lstSpecificItems << lstAllItems[i]
            }
        }
        int end = lstSpecificItems.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstSpecificItems.size()
        List lstResult = lstSpecificItems.subList(baseService.start, end)
        return [list: lstResult, count: lstSpecificItems.size()]
    }

    //Search category-wise item
    public Map searchCategoryWiseItemList(String queryType, String query, long categoryId, BaseService baseService) {
        List<Item> returnList = []
        long companyId = appSessionUtil.getCompanyId()
        query = Tools.escapeForRegularExpression(query)
        switch (categoryId) {
            case itemCategoryCacheUtility.INVENTORY:
                SystemEntity itemInvSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.INVENTORY, companyId)
                List listItemNonInventory = listItemByCategoryId(itemInvSysEntityObject.id)
                returnList = listItemNonInventory.findAll { it.properties.get(queryType) ==~ /(?i).*${query}.*/ }
                break;
            case itemCategoryCacheUtility.NON_INVENTORY:
                SystemEntity itemNonInvSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.NON_INVENTORY, companyId)
                List listItemNonInventory = listItemByCategoryId(itemNonInvSysEntityObject.id)
                returnList = listItemNonInventory.findAll { it.properties.get(queryType) ==~ /(?i).*${query}.*/ }
                break;
            case itemCategoryCacheUtility.FIXED_ASSET:
                SystemEntity itemFxdSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryCacheUtility.FIXED_ASSET, companyId)
                List listItemFixedAssets = listItemByCategoryId(itemFxdSysEntityObject.id)
                returnList = listItemFixedAssets.findAll { it.properties.get(queryType) ==~ /(?i).*${query}.*/ }
                break;
            default:
                returnList = []
                break;
        }
        int end = returnList.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : returnList.size()
        List lstResult = returnList.subList(baseService.start, end)
        return [list: lstResult, count: returnList.size()]
    }


    public int countByCategoryId(long category){
        int count = 0;
        List<Item> lstItemType = (List<Item>) list()
        for (int i = 0; i < lstItemType.size(); i++) {
            if (lstItemType[i].categoryId == category)
                count++
        }
        return count;
    }

    public int countByCategoryIdAndName(long categoryId, String name){
        int count = 0;
        List<Item> lstItemType = (List<Item>) list()
        for (int i = 0; i < lstItemType.size(); i++) {
            if (lstItemType[i].categoryId == categoryId && lstItemType[i].name.equalsIgnoreCase(name))
                count++
        }
        return count;
    }
    public int countByCategoryIdAndNameAndIdNotEqual(long categoryId, String name, long id){
        int count = 0;
        List<Item> lstItemType = (List<Item>) list()
        for (int i = 0; i < lstItemType.size(); i++) {
            if (lstItemType[i].categoryId == categoryId && lstItemType[i].name.equalsIgnoreCase(name)
                        && lstItemType[i].id != id)
                count++
        }
        return count;
    }

    public int countByItemTypeId(long itemTypeId){
        int count = 0;
        List<Item> lstItem = (List<Item>) list()
        for (int i = 0; i < lstItem.size(); i++) {
            if (lstItem[i].itemTypeId == itemTypeId)
                count++
        }
        return count;
    }

    public int countByValuationTypeId(long valuationTypeId){
        int count = 0;
        List<Item> lstItem = (List<Item>) list()
        for (int i = 0; i < lstItem.size(); i++) {
            if (lstItem[i].valuationTypeId == valuationTypeId)
                count++
        }
        return count;
    }
}
