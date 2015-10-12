package com.athena.mis.fixedasset.service

import com.athena.mis.BaseService
import com.athena.mis.fixedasset.entity.FxdFixedAssetDetails
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
/**
 * FixedAssetDetailsService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class FixedAssetDetailsService extends BaseService {

    static transactional = false
    /**
     * Get fixed asset details object
     * @param id - id
     * @return - fixed asset details
     */
    public FxdFixedAssetDetails read(long id) {
        FxdFixedAssetDetails fixedAssetDetails = FxdFixedAssetDetails.read(id)
        return fixedAssetDetails
    }

    private static final String FIXED_ASSET_DETAILS_CREATE_QUERY = """
            INSERT INTO fxd_fixed_asset_details(id, version, item_id, cost, name,
                    description, current_inventory_id, po_id, supplier_id, project_id, owner_type_id, purchase_date, expire_date,
                    created_by, created_on, updated_by, updated_on, company_id)
            VALUES (NEXTVAL('fxd_fixed_asset_details_id_seq'), :version, :itemId, :cost, :name,
                        :description, :currentInventoryId, :poId, :supplierId, :projectId, :ownerTypeId, :purchaseDate,
                        :expireDate, :createdBy, :createdOn, :updatedBy, null, :companyId
                       )"""

    /**
     * Create fixed asset details
     * @param fixedAssetDetails -fixed asset details object
     * @return - newly created fixed asset details object
     */
    public FxdFixedAssetDetails create(FxdFixedAssetDetails fixedAssetDetails) {
        Map queryParams = [
                version: 0,
                itemId: fixedAssetDetails.itemId,
                cost: fixedAssetDetails.cost,
                name: fixedAssetDetails.name,
                description: fixedAssetDetails.description,
                currentInventoryId: fixedAssetDetails.currentInventoryId,
                poId: fixedAssetDetails.poId,
                supplierId: fixedAssetDetails.supplierId,
                projectId: fixedAssetDetails.projectId,
                ownerTypeId: fixedAssetDetails.ownerTypeId,
                createdBy: fixedAssetDetails.createdBy,
                updatedBy: fixedAssetDetails.updatedBy,
                companyId: fixedAssetDetails.companyId,
                purchaseDate: DateUtility.getSqlDate(fixedAssetDetails.purchaseDate),
                createdOn: DateUtility.getSqlDateWithSeconds(fixedAssetDetails.createdOn),
                expireDate: DateUtility.getSqlDate(fixedAssetDetails.expireDate)
        ]

        List result = executeInsertSql(FIXED_ASSET_DETAILS_CREATE_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException("Exception occurred at FixedAssetDetailsService.create")
        }
        int fixedAssetDetailsId = (int) result[0][0]
        fixedAssetDetails.id = fixedAssetDetailsId
        return fixedAssetDetails
    }

    private static final String FIXED_ASSET_DETAILS_UPDATE_QUERY = """
                    UPDATE fxd_fixed_asset_details
                        SET  version = :newVersion,
                             name = :name,
                             description = :description,
                             purchase_date = :purchaseDate,
                             expire_date = :expireDate,
                             owner_type_id = :ownerTypeId,
                             updated_by = :updatedBy,
                             updated_on =:updatedOn
                        WHERE id = :id AND
                        version = :version
                        """
    /**
     * Update fixed asset details
     * @param fixedAssetDetails -fixed asset details object
     * @return - newly update fixed asset details object
     */
    public int update(FxdFixedAssetDetails fixedAssetDetails) {
        Map queryParams = [
                id: fixedAssetDetails.id,
                version: fixedAssetDetails.version,
                newVersion: fixedAssetDetails.version + 1,
                name: fixedAssetDetails.name,
                description: fixedAssetDetails.description,
                ownerTypeId: fixedAssetDetails.ownerTypeId,
                updatedBy: fixedAssetDetails.updatedBy,
                purchaseDate: DateUtility.getSqlDate(fixedAssetDetails.purchaseDate),
                updatedOn: DateUtility.getSqlDateWithSeconds(fixedAssetDetails.updatedOn),
                expireDate: DateUtility.getSqlDate(fixedAssetDetails.expireDate)
        ]

        int updateCount = executeUpdateSql(FIXED_ASSET_DETAILS_UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException("Exception occurred at FixedAssetDetailsService.update")
        }
        return updateCount
    }

    private static final String QUERY_DELETE = """
                    DELETE FROM fxd_fixed_asset_details
                       WHERE
                          id = :id
                          """
    /**
     * Delete fixed asset details
     * @param id - fixed asset details id
     * @return - int value(e.g- 1 for success, 0 for failure)
     */
    public int delete(long id) {
        int deleteCount = executeUpdateSql(QUERY_DELETE, [id: id])
        if (deleteCount <= 0) {
            throw new RuntimeException("Exception occurred at FixedAssetDetailsService.delete")
        }
        return deleteCount
    }
}
