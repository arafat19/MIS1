package com.athena.mis.fixedasset.service

import com.athena.mis.BaseService
import com.athena.mis.fixedasset.entity.FxdFixedAssetDetails
import com.athena.mis.fixedasset.entity.FxdFixedAssetTrace
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
/**
 * FixedAssetTraceService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class FixedAssetTraceService extends BaseService {

    static transactional = false

    private static final String FIXED_ASSET_TRACE_CREATE_QUERY = """
             INSERT INTO fxd_fixed_asset_trace( id, version, fixed_asset_details_id,
                item_id, inventory_id, transaction_date, created_by, created_on,
                company_id, comments, is_current)
             VALUES (NEXTVAL('fxd_fixed_asset_trace_id_seq'), :version, :fixedAssetDetailsId, :itemId,
             :inventoryId, :transactionDate, :createdBy, :createdOn,
             :companyId, :comments, :isCurrent)
        """

    /**
     * Create Fixed-Asset-Trace object
     * @param fixedAssetTrace -fixed asset Trace object
     * @return - newly created fixed asset Trace object
     */
    public FxdFixedAssetTrace create(FxdFixedAssetTrace fixedAssetTrace) {
        Map queryParams = [
                version: fixedAssetTrace.version,
                fixedAssetDetailsId: fixedAssetTrace.fixedAssetDetailsId,
                itemId: fixedAssetTrace.itemId,
                inventoryId: fixedAssetTrace.inventoryId,
                createdBy: fixedAssetTrace.createdBy,
                companyId: fixedAssetTrace.companyId,
                comments: fixedAssetTrace.comments,
                isCurrent: fixedAssetTrace.isCurrent,
                transactionDate: DateUtility.getSqlDate(fixedAssetTrace.transactionDate),
                createdOn: DateUtility.getSqlDateWithSeconds(fixedAssetTrace.createdOn)
        ]

        log.debug(query + Tools.COMA + queryParams)
        List result = executeInsertSql(FIXED_ASSET_TRACE_CREATE_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException("Exception occurred at Fixed-Asset-Trace Create")
        }
        int fixedAssetTraceId = (int) result[0][0]
        fixedAssetTrace.id = fixedAssetTraceId
        return fixedAssetTrace
    }

    private static final String QUERY_DELETE = """
                    DELETE FROM fxd_fixed_asset_trace
                        WHERE fixed_asset_details_id = :detailId
                          """
    /**
     * Delete fixed asset trace
     * @param id - fixed asset trace id
     * @return - int value(e.g- 1 for success, 0 for failure)
     */
    public int delete(long detailId) {
        int deleteCount = executeUpdateSql(QUERY_DELETE, [detailId: detailId])
        if (deleteCount <= 0) {
            throw new RuntimeException("Error occurred to delete fixed asset trace")
        }
        return deleteCount
    }

    private static final String FXD_ASSET_TRACE_UPDATE_QUERY = """
                UPDATE fxd_fixed_asset_trace
                    SET version = :newVersion,
                    transaction_date = :purchaseDate
                    WHERE id = (SELECT id FROM fxd_fixed_asset_trace
                    WHERE fixed_asset_details_id =:fixedAssetDetailsId
                    ORDER BY id ASC LIMIT 1)
                """
    /**
     * Update fixed_asset_trace
     * @param fixedAssetDetails -fixed_asset_trace object
     * @return - newly update fixed_asset_trace object
     */
    public int update(FxdFixedAssetDetails fixedAssetDetails) {
        Map queryParams = [
                fixedAssetDetailsId: fixedAssetDetails.id,
                purchaseDate: DateUtility.getSqlDate(fixedAssetDetails.purchaseDate),
                newVersion: fixedAssetDetails.version + 1
        ]
        int updateTraceCount = executeUpdateSql(FXD_ASSET_TRACE_UPDATE_QUERY, queryParams)

        if (updateTraceCount <= 0) {
            throw new RuntimeException("Exception occurred at FixedAssetTraceService.updateFirstTrace")
        }
        return updateTraceCount
    }
}
