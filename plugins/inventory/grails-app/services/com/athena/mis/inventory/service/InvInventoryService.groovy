package com.athena.mis.inventory.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvInventoryTypeCacheUtility
import org.springframework.beans.factory.annotation.Autowired

class InvInventoryService extends BaseService {

    SystemEntityService systemEntityService
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility

    static transactional = false

    /**
     * Method to get list of inventory
     * @return -inventoryList
     */
    public List list() {
        return InvInventory.list(sort: invInventoryCacheUtility.SORT_ON_NAME, order: invInventoryCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    /**
     * Method to read InvInventory object by id
     * @param id -InvInventory.id
     * @return -InvInventory object
     */
    public InvInventory read(long id) {
        InvInventory invInventory = InvInventory.read(id)
        if (invInventory) return invInventory
        return null
    }

    private static final String UPDATE_QUERY = """
        UPDATE inv_inventory SET
              version =:newVersion,
              project_id =:projectId,
              name =:name,
              description =:description,
              is_factory =:isFactory,
              type_id =:typeId
        WHERE
              id =:id AND
              version =:version
    """
    /**
     * Method to update InvInventory object
     * @param InvInventory -InvInventory object
     * @return -updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(InvInventory invInventory) {
        Map queryParams = [
                id: invInventory.id,
                version: invInventory.version,
                newVersion: invInventory.version + 1,
                projectId: invInventory.projectId,
                name: invInventory.name,
                description: invInventory.description,
                typeId: invInventory.typeId,
                isFactory: invInventory.isFactory
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        invInventory.version = invInventory.version + 1
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update Inventory')
        }
        return updateCount;
    }

    private static final String DELETE_QUERY = """
                    DELETE FROM inv_inventory
                      WHERE  id=:id
                    """
    /**
     * Method to delete InvInventory object
     * @param id -InvInventory.id
     * @return -if deleteCount <= 0 then throw exception to rollback transaction; otherwise return true
     */
    public Boolean delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred to delete inventory')
        }
        return Boolean.TRUE
    }

    private static final String CREATE_QUERY = """
            INSERT INTO inv_inventory (id, version, description, is_factory, name,
                                        project_id, type_id, company_id)
               VALUES (NEXTVAL('inv_inventory_id_seq'), :version, :description, :isFactory,
                                :name, :projectId, :typeId, :companyId)
            """
    /**
     * Method to save InvInventory object
     * @param InvInventory -InvInventory object
     * @return-newly created InvInventory object
     */
    public InvInventory create(InvInventory invInventory) {
        Map queryParams = [
                version: invInventory.version,
                description: invInventory.description,
                isFactory: invInventory.isFactory,
                name: invInventory.name,
                projectId: invInventory.projectId,
                typeId: invInventory.typeId,
                companyId: invInventory.companyId
        ]
        List result = executeInsertSql(CREATE_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while creating inventory')
        }
        int inventoryId = (int) result[0][0]
        invInventory.id = inventoryId
        return invInventory
    }

    /**
     * applicable only for create default inventory
     */
    public void createDefaultData(long companyId) {
        SystemEntity site = systemEntityService.findByReservedIdAndCompanyId(invInventoryTypeCacheUtility.TYPE_SITE, companyId)
        SystemEntity store = systemEntityService.findByReservedIdAndCompanyId(invInventoryTypeCacheUtility.TYPE_STORE, companyId)
        new InvInventory(name: 'Site 1', typeId: site.id, projectId: 1, isFactory: true, companyId: companyId).save()
        new InvInventory(name: 'Store 1', typeId: store.id, projectId: 1, isFactory: true, companyId: companyId).save()
    }
}
