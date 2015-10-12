package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.SupplierItem
import com.athena.mis.application.utility.SupplierItemCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Service class for basic supplier-item CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'SupplierItemService'
 */
class SupplierItemService extends BaseService {

    static transactional = false

    @Autowired
    SupplierItemCacheUtility supplierItemCacheUtility

    /**
     * @return -list of supplier-item objects
     */
    public List list() {
        return SupplierItem.list(sort: supplierItemCacheUtility.SORT_ON_NAME, order: supplierItemCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    /**
     * Method to get the list of supplier item
     * @param supplierId - supplier id
     * @param companyId - company id
     * @return - a list of supplier item
     */
    public List<SupplierItem> findAllBySupplierIdAndCompanyId(long supplierId, long companyId) {
        List<SupplierItem> supplierItemList = SupplierItem.findAllBySupplierIdAndCompanyId(supplierId, companyId, [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
        return supplierItemList
    }

    /**
     * Method to get supplier item list
     * @param supplierId - supplier id
     * @param companyId - company id
     * @param lstMatchedItemIds - list of matched item id
     * @return - a list of supplier item
     */
    public List<SupplierItem> findAllBySupplierIdAndCompanyIdAndItemIdInList(long supplierId, long companyId, List<Long> lstMatchedItemIds) {
        List<SupplierItem> supplierItemList = SupplierItem.findAllBySupplierIdAndCompanyIdAndItemIdInList(supplierId, companyId, lstMatchedItemIds, [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
        return supplierItemList
    }

    /**
     * Method to get the count of supplier items
     * @param supplierId - supplier id
     * @param companyId - company id
     * @param lstMatchedItemIds - list of matched item ids
     * @return - an integer value of count
     */
    public int countBySupplierIdAndCompanyIdAndItemIdInList(long supplierId, long companyId, List<Long> lstMatchedItemIds){
        int count = SupplierItem.countBySupplierIdAndCompanyIdAndItemIdInList(supplierId, companyId, lstMatchedItemIds)
        return count
    }
    /**
     * Method to get the count of supplier items
     * @param supplierId - supplier id
     * @param companyId - company id
     * @return - an integer number of supplier item count
     */
    public int countBySupplierIdAndCompanyId(long supplierId, long companyId) {
        int count = SupplierItem.countBySupplierIdAndCompanyId(supplierId, companyId)
        return count
    }

    private static final String INSERT_QUERY = """
         INSERT INTO supplier_item(id, "version", supplier_id, item_id, company_id,created_on,created_by,updated_by,updated_on)
            VALUES (NEXTVAL('supplier_item_id_seq'), :version, :supplierId, :itemId, :companyId,:createdOn,:createdBy,:updatedBy,null)
            """
    /**
     * SQL to save supplierItem object in database
     * @param supplierItem -SupplierItem object
     * @return -newly created supplierItem object
     */
    public SupplierItem create(SupplierItem supplierItem) {
        Map queryParams = [
                version: 0,
                supplierId: supplierItem.supplierId,
                itemId: supplierItem.itemId,
                companyId: supplierItem.companyId,
                createdBy: supplierItem.createdBy,
                createdOn: DateUtility.getSqlDateWithSeconds(supplierItem.createdOn),
                updatedBy: supplierItem.updatedBy
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)

        Long id = (Long) result[0][0]
        if (id <= 0) {
            throw new RuntimeException('error occurred at supplierItemService.create')
        }
        supplierItem.id = id
        return supplierItem;
    }

    private static final String UPDATE_QUERY = """
                      UPDATE supplier_item SET
                          supplier_id=:supplierId,
                          item_id=:itemId,
                          version=:newVersion
                      WHERE
                          id=:id AND
                          version=:version"""
    /**
     * SQL to update supplierItem object in database
     * @param supplierItem -SupplierItem object
     * @return -int value(updateCount)
     */
    public Integer update(SupplierItem supplierItem) {
        Map queryParams = [
                supplierId: supplierItem.supplierId,
                itemId: supplierItem.itemId,
                newVersion: supplierItem.version + 1,
                id: supplierItem.id,
                version: supplierItem.version,
                updatedBy: supplierItem.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(supplierItem.updatedOn)
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('error occurred at supplierItemService.update')
        }
        supplierItem.version = supplierItem.version + 1
        return (new Integer(updateCount));
    }

    private static final String DELETE_QUERY = """
                    DELETE FROM supplier_item
                      WHERE id=:id   """
    /**
     * SQL to delete supplierItem object from database
     * @param id -SupplierItem.id
     * @return -boolean value
     */
    public int delete(long id) {
        int deleteCount = executeUpdateSql(DELETE_QUERY, [id: id])
        if (deleteCount <= 0) {
            throw new RuntimeException('error occurred at supplierItemService.delete')
        }
        return deleteCount
    }

    /**
     * get specific supplier-item object from cache by id
     * @param id -SupplierItem.id
     * @return -supplierItem object
     */
    public SupplierItem read(long id) {
        SupplierItem supplierItem = (SupplierItem) supplierItemCacheUtility.read(id)
        return supplierItem
    }
}
