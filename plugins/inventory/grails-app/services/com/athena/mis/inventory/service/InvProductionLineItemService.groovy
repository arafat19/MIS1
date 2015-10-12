package com.athena.mis.inventory.service

import com.athena.mis.BaseService
import com.athena.mis.inventory.entity.InvProductionLineItem
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class InvProductionLineItemService extends BaseService {

    static transactional = false
    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility

    /**
     * Method to get list of invProductionLineItem
     * @return -invProductionLineItemList
     */
    @Transactional(readOnly = true)
    public List list() {
        return InvProductionLineItem.list(sort: invProductionLineItemCacheUtility.SORT_ON_NAME, order: invProductionLineItemCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    private static final String CREATE_QUERY = """
            INSERT INTO inv_production_line_item(id, version, name, company_id)
             VALUES (NEXTVAL('inv_production_line_item_id_seq'),:version,
                :name, :companyId);"""
    /**
     * Method to save InvProductionLineItem object
     * @param invProductionLineItem -InvProductionLineItem object
     * @return-newly created InvProductionLineItem object
     */
    public InvProductionLineItem create(InvProductionLineItem invProductionLineItem) {
        Map queryParams = [
                companyId: invProductionLineItem.companyId,
                name: invProductionLineItem.name,
                version: invProductionLineItem.version
        ]
        List result = executeInsertSql(CREATE_QUERY, queryParams)
        Long id = (Long) result[0][0]
        if (id <= 0) {
            throw new RuntimeException('error occurred at invProductionLineItemService.create')
        }
        invProductionLineItem.id = id
        return invProductionLineItem;
    }

    /**
     * Method to read InvProductionLineItem object from cacheUtility by id
     * @param id -InvProductionLineItem.id
     * @return -InvProductionLineItem object
     */
    public InvProductionLineItem read(long id) {
        return (InvProductionLineItem) invProductionLineItemCacheUtility.read(id);
    }

    private static final String UPDATE_QUERY = """
                    UPDATE inv_production_line_item SET
                          version=:newVersion,
                          name=:name
                      WHERE
                          id=:id AND
                          version=:version
                          """
    /**
     * Method to update invProductionLineItem object
     * @param invProductionLineItem -invProductionLineItem object
     * @return -updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(InvProductionLineItem invProductionLineItem) {
        Map queryParams = [
                newVersion: invProductionLineItem.version + 1,
                name: invProductionLineItem.name,
                id: invProductionLineItem.id,
                version: invProductionLineItem.version
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('error occurred at invProductionLineItemService.update')
        }
        invProductionLineItem.version = invProductionLineItem.version + 1
        return updateCount;
    }

    private static final String DELETE_QUERY = """
        DELETE FROM inv_production_line_item
        WHERE  id=:id
    """
    /**
     * Method to delete invProductionLineItem object
     * @param id -invProductionLineItem.id
     * @return -if deleteCount <= 0 then throw exception to rollback transaction; otherwise return true
     */
    public Boolean delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException('error occurred at invProductionLineItemService.delete')
        }
        return Boolean.TRUE;
    }
}