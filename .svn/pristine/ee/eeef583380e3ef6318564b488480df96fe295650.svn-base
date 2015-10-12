package com.athena.mis.fixedasset.utility

import com.athena.mis.BaseService
import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.fixedasset.entity.FxdCategoryMaintenanceType
import com.athena.mis.fixedasset.entity.FxdMaintenanceType
import com.athena.mis.fixedasset.service.FxdCategoryMaintenanceTypeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("fxdCategoryMaintenanceTypeCacheUtility")
class FxdCategoryMaintenanceTypeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    FxdCategoryMaintenanceTypeService fxdCategoryMaintenanceTypeService
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    FxdMaintenanceTypeCacheUtility fxdMaintenanceTypeCacheUtility

    public static final String SORT_ON_NAME = "id";
    private static final String QUERY_TYPE_ITEM = "item";
    private static final String QUERY_TYPE_MAINTENANCE_TYPE = "maintenanceType";

    public void init() {
        List list = fxdCategoryMaintenanceTypeService.list();
        super.setList(list)
    }

    public FxdCategoryMaintenanceType readByItemIdAndMaintenanceTypeId(long itemId, long maintenanceTypeId) {
        List<FxdCategoryMaintenanceType> fxdCategoryMaintenanceTypeList = (List<FxdCategoryMaintenanceType>) super.list()
        for (int i = 0; i < fxdCategoryMaintenanceTypeList.size(); i++) {
            if (fxdCategoryMaintenanceTypeList[i].itemId == itemId
                    && fxdCategoryMaintenanceTypeList[i].maintenanceTypeId == maintenanceTypeId) {
                return fxdCategoryMaintenanceTypeList[i]
            }
        }
        return null
    }

    public FxdCategoryMaintenanceType readByItemIdAndMaintenanceTypeIdForEdit(long itemId, long maintenanceTypeId, long fxdCategoryMaintenanceTypeId) {
        List<FxdCategoryMaintenanceType> fxdCategoryMaintenanceTypeList = (List<FxdCategoryMaintenanceType>) super.list()
        for (int i = 0; i < fxdCategoryMaintenanceTypeList.size(); i++) {
            if (fxdCategoryMaintenanceTypeList[i].itemId == itemId
                    && fxdCategoryMaintenanceTypeList[i].maintenanceTypeId == maintenanceTypeId
                    && fxdCategoryMaintenanceTypeList[i].id != fxdCategoryMaintenanceTypeId) {
                return fxdCategoryMaintenanceTypeList[i]
            }
        }
        return null
    }

    public Map searchByField(String queryType, String queryStr, BaseService baseService) {
        List<FxdCategoryMaintenanceType> fxdCategoryMaintenanceTypeList = (List<FxdCategoryMaintenanceType>) super.list()
        List<FxdCategoryMaintenanceType> lstSearchResult = []
        if (queryType == QUERY_TYPE_ITEM) {
            List<Item> itemList = (List<Item>) itemCacheUtility.search(itemCacheUtility.NAME, queryStr)
            List<Long> itemIdList = []
            for (int i = 0; i < itemList.size(); i++) {
                itemIdList << itemList[i].id
            }
            for (int i = 0; i < fxdCategoryMaintenanceTypeList.size(); i++) {
                if (fxdCategoryMaintenanceTypeList[i].itemId in itemIdList) {
                    lstSearchResult << fxdCategoryMaintenanceTypeList[i]
                }
            }
        } else if (queryType == QUERY_TYPE_MAINTENANCE_TYPE) {
            List<FxdMaintenanceType> maintenanceTypeList = (List<FxdMaintenanceType>) fxdMaintenanceTypeCacheUtility.search('name', queryStr)
            List<Long> fxdMaintenanceTypeIdList = []
            for (int i = 0; i < maintenanceTypeList.size(); i++) {
                fxdMaintenanceTypeIdList << maintenanceTypeList[i].id
            }
            for (int i = 0; i < fxdCategoryMaintenanceTypeList.size(); i++) {
                if (fxdCategoryMaintenanceTypeList[i].maintenanceTypeId in fxdMaintenanceTypeIdList) {
                    lstSearchResult << fxdCategoryMaintenanceTypeList[i]
                }
            }
        }
        int end = lstSearchResult.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstSearchResult.size()
        List lstResult = lstSearchResult.subList(baseService.start, end)
        return [list: lstResult, count: lstSearchResult.size()]
    }

    public List<FxdMaintenanceType> getMaintenanceTypeListByItemId(long itemId) {
        List<FxdCategoryMaintenanceType> fxdCategoryMaintenanceTypeList = (List<FxdCategoryMaintenanceType>) super.list()
        List<FxdMaintenanceType> fxdMaintenanceTypeList = []
        for (int i = 0; i < fxdCategoryMaintenanceTypeList.size(); i++) {
            if (fxdCategoryMaintenanceTypeList[i].itemId == itemId) {
                fxdMaintenanceTypeList << (FxdMaintenanceType) fxdMaintenanceTypeCacheUtility.read(fxdCategoryMaintenanceTypeList[i].maintenanceTypeId)
            }
        }
        return fxdMaintenanceTypeList
    }

    public List<Item> getItemListByMaintenanceTypeId(long maintenanceTypeId) {
        List<FxdCategoryMaintenanceType> fxdCategoryMaintenanceTypeList = (List<FxdCategoryMaintenanceType>) super.list()
        List<Item> itemList = []
        for (int i = 0; i < fxdCategoryMaintenanceTypeList.size(); i++) {
            if (fxdCategoryMaintenanceTypeList[i].maintenanceTypeId == maintenanceTypeId) {
                itemList << (Item) itemCacheUtility.read(fxdCategoryMaintenanceTypeList[i].itemId)
            }
        }
        return itemList
    }
}
