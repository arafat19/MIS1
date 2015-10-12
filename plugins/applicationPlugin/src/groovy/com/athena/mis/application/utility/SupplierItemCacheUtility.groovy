package com.athena.mis.application.utility

import com.athena.mis.BaseService
import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SupplierItem
import com.athena.mis.application.service.SupplierItemService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("supplierItemCacheUtility")
class SupplierItemCacheUtility extends ExtendedCacheUtility {

    @Autowired
    SupplierItemService supplierItemService
    @Autowired
    ItemCacheUtility itemCacheUtility

    public static final String SORT_ON_NAME = "id";

    public void init() {
        List list = supplierItemService.list();
        super.setList(list)
    }

    public SupplierItem readByItemIdAndSupplierId(long itemId, long supplierId) {
        List<SupplierItem> supplierItemList = super.list()
        int listSize = supplierItemList.size()
        int i = 0
        while (i < listSize) {
            if (supplierItemList[i].supplierId == supplierId && supplierItemList[i].itemId == itemId) {
                return supplierItemList[i]
            }
            i++
        }
        return null
    }

    public List<Item> getItemList(long supplierId, int itemTypeId) {
        List<SupplierItem> supplierItemList = super.list()
        List<Item> itemList = []

        int listSize = supplierItemList.size()
        Item item
        int i = 0
        while (i < listSize) {
            if (supplierItemList[i].supplierId == supplierId && supplierItemList[i].itemTypeId == itemTypeId) {
                item = (Item) itemCacheUtility.read(supplierItemList[i].itemId)
                itemList << item
            }
            i++
        }
        return itemList
    }

    public int countBySupplierId(long supplierId) {
        int count = 0
        List<SupplierItem> supplierItemList = (List<SupplierItem>) list()
        for (int i = 0; i < supplierItemList.size(); i++) {
            if (supplierItemList[i].supplierId == supplierId)
                count++
        }
        return count
    }

    public List<SupplierItem> getAllSupplierItemListBySupplierId(long supplierId, BaseService baseService) {
        List<SupplierItem> supplierItemList = super.list()
        List<SupplierItem> lstSupplierItem = []
        for (int i = 0; i < supplierItemList.size(); i++) {
            if (supplierItemList[i].supplierId == supplierId) {
                lstSupplierItem << supplierItemList[i]
            }
        }
        int end = lstSupplierItem.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstSupplierItem.size();
        return lstSupplierItem.subList(baseService.start, end);
    }

}
