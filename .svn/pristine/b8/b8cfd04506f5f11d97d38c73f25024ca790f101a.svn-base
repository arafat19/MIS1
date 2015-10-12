package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.service.ItemTypeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component('itemTypeCacheUtility')
class ItemTypeCacheUtility extends ExtendedCacheUtility {

    public final String NAME = 'name'

    @Autowired
    ItemTypeService itemTypeService

    @Transactional(readOnly = true)
    public void init() {
        List list = itemTypeService.list()
        super.setList(list)
    }

    public List listByCategoryId(long categoryId) {
        List lstItemType = []
        List lstTemp = list()
        if (lstTemp.size() <= 0)
            return lstItemType
        for (int i = 0; i < lstTemp.size(); i++) {
            ItemType itemType = (ItemType) lstTemp[i]
            if (itemType.categoryId == categoryId) {
                lstItemType << itemType
            }
        }
        return lstItemType
    }

    public List<Long> listIdsByCategoryId(long categoryId) {
        List<Long> lstItemTypeIds = []
        List lstTemp = list()
        if (lstTemp.size() <= 0)
            return lstItemTypeIds
        for (int i = 0; i < lstTemp.size(); i++) {
            ItemType itemType = (ItemType) lstTemp[i]
            if (itemType.categoryId == categoryId) {
                lstItemTypeIds << itemType.id
            }
        }
        return lstItemTypeIds
    }

    public List<Long> getAllItemTypeIds() {
        List<Long> lstItemTypeIds = []
        List lstTemp = list()
        if (lstTemp.size() <= 0)
            return lstItemTypeIds
        for (int i = 0; i < lstTemp.size(); i++) {
            lstItemTypeIds << lstTemp[i].id
        }
        return lstItemTypeIds
    }

    // return number of same ItemType name in a specific company
    public int countByName(String name){
        int count = 0;
        List<ItemType> lstItemType = (List<ItemType>) list()
        for (int i = 0; i < lstItemType.size(); i++) {
            if (lstItemType[i].name.equalsIgnoreCase(name))
                count++
        }
        return count;
    }
    public int countByNameAndIdNotEqual(String name, long id){
        int count = 0;
        List<ItemType> lstItemType = (List<ItemType>) list()
        for (int i = 0; i < lstItemType.size(); i++) {
            if (lstItemType[i].name.equalsIgnoreCase(name) && lstItemType[i].id != id)
                count++
        }
        return count;
    }
}
