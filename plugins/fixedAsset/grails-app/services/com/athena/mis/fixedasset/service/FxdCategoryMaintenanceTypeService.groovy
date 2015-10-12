package com.athena.mis.fixedasset.service

import com.athena.mis.BaseService
import com.athena.mis.fixedasset.entity.FxdCategoryMaintenanceType
import com.athena.mis.fixedasset.utility.FxdCategoryMaintenanceTypeCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
/**
 * FxdCategoryMaintenanceTypeService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class FxdCategoryMaintenanceTypeService extends BaseService {

    static transactional = false

    @Autowired
    FxdCategoryMaintenanceTypeCacheUtility fxdCategoryMaintenanceTypeCacheUtility
    /**
     * @return - list of category maintenance type
     */
    public List list() {
        return FxdCategoryMaintenanceType.list(sort: fxdCategoryMaintenanceTypeCacheUtility.SORT_ON_NAME, order: fxdCategoryMaintenanceTypeCacheUtility.SORT_ORDER_DESCENDING, readOnly: true);
    }

    private static final String FXD_CATEGORY_MAINTENANCE_TYPE_CREATE_QUERY = """
            INSERT INTO fxd_category_maintenance_type
                (id, version, item_id, maintenance_type_id, company_id,
                created_by, created_on, updated_by, updated_on)
            VALUES (NEXTVAL('fxd_category_maintenance_type_id_seq'), :version, :itemId,
                    :maintenanceTypeId, :companyId,
                    :createdBy, :createdOn, :updatedBy, null);
        """

    /**
     * Create fxd_category_maintenance_type
     * @param fxdCategoryMaintenanceType -category_maintenance_type object
     * @return - newly created category_maintenance_type object
     */
    public FxdCategoryMaintenanceType create(FxdCategoryMaintenanceType fxdCategoryMaintenanceType) {
        Map queryParams = [
                version: fxdCategoryMaintenanceType.version,
                itemId: fxdCategoryMaintenanceType.itemId,
                maintenanceTypeId: fxdCategoryMaintenanceType.maintenanceTypeId,
                companyId: fxdCategoryMaintenanceType.companyId,
                createdBy: fxdCategoryMaintenanceType.createdBy,
                updatedBy: fxdCategoryMaintenanceType.updatedBy,
                createdOn: DateUtility.getSqlDateWithSeconds(fxdCategoryMaintenanceType.createdOn)
        ]

        List result = executeInsertSql(FXD_CATEGORY_MAINTENANCE_TYPE_CREATE_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException("Exception occurred at FxdCategoryMaintenanceTypeService.create")
        }

        int fxdCategoryMaintenanceTypeId = (int) result[0][0]
        fxdCategoryMaintenanceType.id = fxdCategoryMaintenanceTypeId
        return fxdCategoryMaintenanceType
    }

    private static final String FXD_CATEGORY_MAINTENANCE_TYPE_UPDATE_QUERY =
        """
            UPDATE fxd_category_maintenance_type
                SET  version = :newVersion,
                     item_id = :itemId,
                     maintenance_type_id = :maintenanceTypeId,
                     updated_by = :updatedBy,
                     updated_on =:updatedOn
                WHERE id = :id AND
                version = :version
                """
    /**
     * Update fxd_category_maintenance_type
     * @param fxdCategoryMaintenanceType -category_maintenance_type object
     * @return - newly update category_maintenance_type object
     */
    public int update(FxdCategoryMaintenanceType fxdCategoryMaintenanceType) {
        Map queryParams = [
                id: fxdCategoryMaintenanceType.id,
                version: fxdCategoryMaintenanceType.version,
                newVersion: fxdCategoryMaintenanceType.version + 1,
                itemId: fxdCategoryMaintenanceType.itemId,
                maintenanceTypeId: fxdCategoryMaintenanceType.maintenanceTypeId,
                updatedBy: fxdCategoryMaintenanceType.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(fxdCategoryMaintenanceType.updatedOn)
        ]
        int updateCount = executeUpdateSql(FXD_CATEGORY_MAINTENANCE_TYPE_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Exception occurred at FxdCategoryMaintenanceTypeService.update")
        }
        fxdCategoryMaintenanceType.version = fxdCategoryMaintenanceType.version + 1
        return updateCount
    }

    private static final String QUERY_DELETE = """
                     DELETE FROM fxd_category_maintenance_type
                          WHERE id = :id
                          """
    /**
     * Delete category maintenance type
     * @param id - category maintenance type id
     * @return - int value(e.g- 1 for success, 0 for failure)
     */
    public int delete(long id) {
        int deleteCount = executeUpdateSql(QUERY_DELETE, [id: id])
        if (deleteCount <= 0) {
            throw new RuntimeException("Exception occurred at FxdCategoryMaintenanceTypeService.delete")
        }
        return deleteCount
    }
}
