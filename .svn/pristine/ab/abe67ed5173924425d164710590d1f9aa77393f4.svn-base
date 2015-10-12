package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 * ItemTypeService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class ItemTypeService extends BaseService {

    static transactional = true

    SystemEntityService systemEntityService
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility

    /**
     * Pull itemType object
     * @return - list of itemType
     */
    public List list() {
        return ItemType.list(sort: itemTypeCacheUtility.NAME, order: itemTypeCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }
    //create a itemType
    public ItemType create(ItemType itemType) {
        ItemType newItemType = itemType.save(false)
        return newItemType
    }

    private static final String UPDATE_QUERY =
        """
                    UPDATE item_type SET
                          version=:newVersion,
                          name=:name,
                          category_id=:categoryId,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy
                      WHERE
                          id=:id AND
                          version=:version
               """
    /**
     * Update supplied item type
     * @param itemType -item Type to be updated
     * @return updated itemType if saved successfully, otherwise throw RuntimeException
     */
    public int update(ItemType itemType) {
        Map queryParams = [
                id: itemType.id,
                version: itemType.version,
                newVersion: itemType.version + 1,
                name: itemType.name,
                categoryId: itemType.categoryId,
                updatedOn: DateUtility.getSqlDateWithSeconds(itemType.updatedOn),
                updatedBy: itemType.updatedBy
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Error occurred at itemTypeService.update")
        }
        return updateCount;
    }

    private static final String DELETE_QUERY =
        """
                    DELETE FROM item_type
                      WHERE id=:id
                 """

    /**
     * Delete item type
     * @param id - item type id
     * @return - boolean value(true for success & false for failure)
     */
    public Boolean delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("Error occurred at itemTypeService.delete")
        }
        return Boolean.TRUE
    }

    public void createDefaultData(long companyId) {
        SystemEntity categoryInventory = systemEntityService.findByReservedIdAndCompanyId(itemCategoryCacheUtility.INVENTORY, companyId)
        SystemEntity categoryNonInventory = systemEntityService.findByReservedIdAndCompanyId(itemCategoryCacheUtility.NON_INVENTORY, companyId)
        SystemEntity categoryFxdAsset = systemEntityService.findByReservedIdAndCompanyId(itemCategoryCacheUtility.FIXED_ASSET, companyId)

        new ItemType(version: 0, categoryId: categoryInventory.id, companyId: companyId, createdBy: 1, createdOn: new Date(), name: 'Material', updatedBy: 0, updatedOn: null).save()
        new ItemType(version: 0, categoryId: categoryNonInventory.id, companyId: companyId, createdBy: 1, createdOn: new Date(), name: 'Work', updatedBy: 0, updatedOn: null).save()
        new ItemType(version: 0, categoryId: categoryFxdAsset.id, companyId: companyId, createdBy: 1, createdOn: new Date(), name: 'Fixed Asset', updatedBy: 0, updatedOn: null).save()
        new ItemType(version: 0, categoryId: categoryNonInventory.id, companyId: companyId, createdBy: 1, createdOn: new Date(), name: 'Overhead', updatedBy: 0, updatedOn: null).save()
    }
}
