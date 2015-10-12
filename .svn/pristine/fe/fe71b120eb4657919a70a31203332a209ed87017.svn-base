package com.athena.mis.fixedasset.service

import com.athena.mis.BaseService
import com.athena.mis.fixedasset.entity.FxdMaintenance
import com.athena.mis.utility.DateUtility
/**
 * FxdMaintenanceService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class FxdMaintenanceService extends BaseService {

    static transactional = false
    /**
     * Get fixed asset maintenance object
     * @param id - id
     * @return - fixed asset maintenance
     */
    public FxdMaintenance read(long id) {
        return FxdMaintenance.read(id)
    }

    private static final String FXD_MAINTENANCE_CREATE_QUERY = """
            INSERT INTO fxd_maintenance
                (id, version, item_id, fixed_asset_details_id, maintenance_type_id,
                amount, maintenance_date, description,
                created_by, created_on, updated_by, updated_on, company_id)
            VALUES (NEXTVAL('fxd_maintenance_id_seq'), :version, :itemId, :fixedAssetDetailsId,
                :maintenanceTypeId, :amount, :maintenanceDate, :description,
                :createdBy, :createdOn, :updatedBy, null, :companyId);
        """
    /**
     * Create fixed asset maintenance
     * @param fxdMaintenance -fixed asset maintenance object
     * @return - newly created fixed asset maintenance object
     */
    public FxdMaintenance create(FxdMaintenance fxdMaintenance) {
        Map queryParams = [
                version: fxdMaintenance.version,
                itemId: fxdMaintenance.itemId,
                fixedAssetDetailsId: fxdMaintenance.fixedAssetDetailsId,
                maintenanceTypeId: fxdMaintenance.maintenanceTypeId,
                amount: fxdMaintenance.amount,
                description: fxdMaintenance.description,
                createdBy: fxdMaintenance.createdBy,
                updatedBy: fxdMaintenance.updatedBy,
                companyId: fxdMaintenance.companyId,
                maintenanceDate: DateUtility.getSqlDateWithSeconds(fxdMaintenance.maintenanceDate),
                createdOn: DateUtility.getSqlDateWithSeconds(fxdMaintenance.createdOn)
        ]

        List result = executeInsertSql(FXD_MAINTENANCE_CREATE_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException("Exception occurred at FxdMaintenanceService.create")
        }

        int fxdMaintenanceId = (int) result[0][0]
        fxdMaintenance.id = fxdMaintenanceId
        return fxdMaintenance
    }

    private static final String FXD_MAINTENANCE_UPDATE_QUERY =
        """ UPDATE fxd_maintenance
                SET  version = :newVersion,
                     item_id = :itemId,
                     fixed_asset_details_id =:fixedAssetDetailsId,
                     maintenance_type_id = :maintenanceTypeId,
                     amount = :amount,
                     maintenance_date = :maintenanceDate,
                     description = :description,
                     updated_by = :updatedBy,
                     updated_on =:updatedOn
                WHERE id = :id AND
                version = :version
                """

    /**
     * Update fixed asset maintenance
     * @param fxdMaintenance -fixed asset maintenance object
     * @return - newly update fixed asset maintenance object
     */
    public int update(FxdMaintenance fxdMaintenance) {
        Map queryParams = [
                id: fxdMaintenance.id,
                version: fxdMaintenance.version,
                newVersion: fxdMaintenance.version + 1,
                itemId: fxdMaintenance.itemId,
                fixedAssetDetailsId: fxdMaintenance.fixedAssetDetailsId,
                maintenanceTypeId: fxdMaintenance.maintenanceTypeId,
                amount: fxdMaintenance.amount,
                description: fxdMaintenance.description,
                updatedBy: fxdMaintenance.updatedBy,
                maintenanceDate: DateUtility.getSqlDateWithSeconds(fxdMaintenance.maintenanceDate),
                updatedOn: DateUtility.getSqlDateWithSeconds(fxdMaintenance.updatedOn)
        ]
        int updateCount = executeUpdateSql(FXD_MAINTENANCE_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Exception occurred at FxdMaintenanceService.update")
        }
        return updateCount
    }

    private static final String QUERY_DELETE = """
                     DELETE FROM fxd_maintenance
                          WHERE id = :id
                          """
    /**
     * Delete fixed asset maintenance
     * @param id - fixed asset maintenance id
     * @return - int value(e.g- 1 for success, 0 for failure)
     */
    public int delete(long id) {
        int deleteCount = executeUpdateSql(QUERY_DELETE, [id: id])
        if (deleteCount <= 0) {
            throw new RuntimeException("Exception occurred at FxdMaintenanceService.delete")
        }
        return deleteCount
    }
}
