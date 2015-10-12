package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.application.utility.ValuationTypeCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Service class for basic item CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'ItemService'
 */
class ItemService extends BaseService {

    static transactional = true

    SystemEntityService systemEntityService
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ValuationTypeCacheUtility valuationTypeCacheUtility
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility

    /**
     * @return -list of item object
     */
    public List<Item> list() {
        List<Item> itemList = []
        itemList = Item.list([sort: itemCacheUtility.NAME, order: itemCacheUtility.SORT_ORDER_ASCENDING, readOnly: true])
        return itemList
    }

    /**
     * Method to read Item by id
     * @param id -Item.id
     * @return -Item object
     */
    public Item read(long id) {
        Item item = Item.read(id)
        return item
    }

    private static final String INSERT_QUERY =
        """
            INSERT INTO item(id, version, name, code, unit, company_id, category_id,
            item_type_id, valuation_type_id, is_individual_entity, is_finished_product,created_on,created_by,updated_by,updated_on)
                VALUES (NEXTVAL('item_id_seq'),
                                :version,
                                :name,
                                :code,
                                :unit,
                                :companyId,
                                :categoryId,
                                :itemTypeId,
                                :valuationTypeId,
                                :isIndividualEntity,
                                :isFinishedProduct,
                                :createdOn,
                                :createdBy,
                                :updatedBy,
                                null
                );
        """
    /**
     * SQL  to save item object in database
     * @param item -Item object
     * @return -newly created item object
     */
    public Item create(Item item) {
        Map queryParams = [
                version: item.version,
                name: item.name,
                code: item.code,
                unit: item.unit,
                companyId: item.companyId,
                categoryId: item.categoryId,
                itemTypeId: item.itemTypeId,
                valuationTypeId: item.valuationTypeId,
                isIndividualEntity: item.isIndividualEntity,
                isFinishedProduct: item.isFinishedProduct,
                createdBy: item.createdBy,
                createdOn: DateUtility.getSqlDateWithSeconds(item.createdOn),
                updatedBy: item.updatedBy
        ]
        List result = executeInsertSql(INSERT_QUERY, queryParams)
        Long id = (Long) result[0][0]
        item.id = id
        return item;
    }

    private static final String UPDATE_QUERY =
        """
                    UPDATE item SET
                          version=:newVersion,
                          name=:name,
                          code=:code,
                          unit=:unit,
                          item_type_id=:itemTypeId,
                          valuation_type_id=:valuationTypeId,
                          is_individual_entity=:isIndividualEntity,
                          is_finished_product=:isFinishedProduct,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy
                      WHERE
                          id=:id AND
                          version=:version
               """
    /**
     * SQL to update item object in database
     * @param item -Item object
     * @return -int value(updateCount)
     */
    public int update(Item item) {
        Map queryParams = [
                newVersion: item.version + 1,
                name: item.name,
                code: item.code,
                unit: item.unit,
                itemTypeId: item.itemTypeId,
                id: item.id,
                version: item.version,
                valuationTypeId: item.valuationTypeId,
                isIndividualEntity: item.isIndividualEntity,
                isFinishedProduct: item.isFinishedProduct,
                updatedBy: item.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(item.updatedOn)
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Error occurred at itemService.update")
        }
        return updateCount;
    }

    private static final String DELETE_QUERY =
        """
                DELETE FROM item
                  WHERE
                      id=:id
             """
    /**
     * SQL to delete item object from database
     * @param id -Item.id
     * @return -boolean value
     */
    public Boolean delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("Error occurred at itemService.delete")
        }
        return Boolean.TRUE
    }

    /**
     * insert default items into database when application starts with bootstrap
     */
    public void createDefaultData(long companyId) {

        SystemEntity itemInvCategorySysEntityObject = systemEntityService.findByReservedIdAndCompanyId(itemCategoryCacheUtility.INVENTORY, companyId)
        SystemEntity itemNonInvCategorySysEntityObject = systemEntityService.findByReservedIdAndCompanyId(itemCategoryCacheUtility.NON_INVENTORY, companyId)
        SystemEntity itemFxdCategorySysEntityObject = systemEntityService.findByReservedIdAndCompanyId(itemCategoryCacheUtility.FIXED_ASSET, companyId)

        SystemEntity valuationTypeFifoObj = systemEntityService.findByReservedIdAndCompanyId(valuationTypeCacheUtility.VALUATION_TYPE_FIFO, companyId)
        SystemEntity valuationTypeLifoObj = systemEntityService.findByReservedIdAndCompanyId(valuationTypeCacheUtility.VALUATION_TYPE_LIFO, companyId)

        new Item(name: 'Brick', code: 'BRK', unit: 'piece', itemTypeId: 1, categoryId: itemInvCategorySysEntityObject.id, valuationTypeId: valuationTypeFifoObj.id, companyId: companyId, createdBy: 1, createdOn: new Date()).save()
        new Item(name: 'Cement', code: 'CMT', unit: 'Bag', itemTypeId: 1, categoryId: itemInvCategorySysEntityObject.id, valuationTypeId: valuationTypeFifoObj.id, companyId: companyId, createdBy: 1, createdOn: new Date()).save()
        new Item(name: 'Diesel', code: 'DIS', unit: 'Liter', itemTypeId: 1, categoryId: itemInvCategorySysEntityObject.id, valuationTypeId: valuationTypeLifoObj.id, companyId: companyId, createdBy: 1, createdOn: new Date()).save()
        new Item(name: 'Steel', code: 'STL', unit: 'KG', itemTypeId: 1, categoryId: itemInvCategorySysEntityObject.id, valuationTypeId: valuationTypeLifoObj.id, companyId: companyId, createdBy: 1, createdOn: new Date()).save()
        new Item(name: 'Painting', code: 'PNT', unit: 'Hours', itemTypeId: 2, categoryId: itemNonInvCategorySysEntityObject.id, valuationTypeId: valuationTypeFifoObj.id, companyId: companyId, createdBy: 1, createdOn: new Date()).save()
        new Item(name: 'AC', code: 'AC', unit: 'Pcs', itemTypeId: 3, categoryId: itemFxdCategorySysEntityObject.id, valuationTypeId: valuationTypeFifoObj.id, companyId: companyId, createdBy: 1, createdOn: new Date()).save()
    }
}
