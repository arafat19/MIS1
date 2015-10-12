package com.athena.mis.fixedasset.service

import com.athena.mis.BaseService
import com.athena.mis.fixedasset.entity.FxdMaintenanceType
import com.athena.mis.fixedasset.utility.FxdMaintenanceTypeCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 * FxdMaintenanceTypeService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class FxdMaintenanceTypeService extends BaseService {

    static transactional = false

    @Autowired
    FxdMaintenanceTypeCacheUtility fxdMaintenanceTypeCacheUtility
    /**
     * @return - list of maintenance
     */
    public List list() {
        return FxdMaintenanceType.list(sort: fxdMaintenanceTypeCacheUtility.SORT_ON_NAME, order: fxdMaintenanceTypeCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }
    /**
     * @param id - maintenance type id
     * @return - maintenance type object
     */
    public FxdMaintenanceType read(long id) {
        FxdMaintenanceType fxdMaintenanceType = FxdMaintenanceType.read(id)
        return fxdMaintenanceType
    }
    /**
     * @param id - maintenance type id
     * @return- maintenance type object
     */
    public FxdMaintenanceType get(long id) {
        FxdMaintenanceType fxdMaintenanceType = FxdMaintenanceType.get(id)
        return fxdMaintenanceType
    }

    private static final String FXD_MAINTENANCE_CREATE_QUERY = """
            INSERT INTO fxd_maintenance_type
                (id, version, company_id, created_by, created_on, name, updated_by, updated_on)
            VALUES (NEXTVAL('fxd_maintenance_type_id_seq'), :version, :companyId, :createdBy,
                :createdOn, :name, :updatedBy, null);
        """
    /**
     * Create maintenance_type
     * @param fxdMaintenanceType -maintenance_type object
     * @return - newly created maintenance_type object
     */
    public FxdMaintenanceType create(FxdMaintenanceType fxdMaintenanceType) {
        Map queryParams = [
                version: fxdMaintenanceType.version,
                name: fxdMaintenanceType.name,
                createdBy: fxdMaintenanceType.createdBy,
                updatedBy: fxdMaintenanceType.updatedBy,
                companyId: fxdMaintenanceType.companyId,
                createdOn: DateUtility.getSqlDateWithSeconds(fxdMaintenanceType.createdOn)
        ]
        List result = executeInsertSql(FXD_MAINTENANCE_CREATE_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException("Exception occurred at FxdMaintenanceTypeService.create")
        }
        int fxdMaintenanceTypeId = (int) result[0][0]
        fxdMaintenanceType.id = fxdMaintenanceTypeId
        return fxdMaintenanceType
    }

    private static final String FXD_MAINTENANCE_UPDATE_QUERY =
        """ UPDATE fxd_maintenance_type
                SET  version = :newVersion,
                     name = :name,
                     updated_by = :updatedBy,
                     updated_on =:updatedOn
                WHERE id = :id AND
                version = :oldVersion
                """
    /**
     * Update maintenance_type
     * @param fxdMaintenanceType -maintenance_type object
     * @return - newly update maintenance_type object
     */
    public int update(FxdMaintenanceType fxdMaintenanceType) {
        Map queryParams = [
                id: fxdMaintenanceType.id,
                oldVersion: fxdMaintenanceType.version - 1,
                newVersion: fxdMaintenanceType.version,
                name: fxdMaintenanceType.name,
                updatedBy: fxdMaintenanceType.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(fxdMaintenanceType.updatedOn)
        ]
        int updateCount = executeUpdateSql(FXD_MAINTENANCE_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Exception occurred at FxdMaintenanceTypeService.update")
        }
        return updateCount
    }

    private static final String QUERY_DELETE = """
                     DELETE FROM fxd_maintenance_type
                          WHERE id = :id
                          """
    /**
     * Delete maintenance type
     * @param id - maintenance type id
     * @return - int value(e.g- 1 for success, 0 for failure)
     */
    public int delete(long id) {
        int deleteCount = executeUpdateSql(QUERY_DELETE, [id: id])
        if (deleteCount <= 0) {
            throw new RuntimeException("Exception occurred at FxdMaintenanceTypeService.delete")
        }
        return deleteCount
    }

    /**
     * applicable only for create default maintenance type
     */
    public void createDefaultData() {
        new FxdMaintenanceType(name: 'General Repair', companyId: 1, createdBy: 1, createdOn: new Date()).save()
    }
}
