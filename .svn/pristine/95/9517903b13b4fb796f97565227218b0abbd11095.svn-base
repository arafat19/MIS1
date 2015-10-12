package com.athena.mis.inventory.service

import com.athena.mis.BaseService
import com.athena.mis.inventory.entity.InvProductionDetails
import com.athena.mis.inventory.utility.InvProductionDetailsCacheUtility
import groovy.sql.GroovyRowResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class InvProductionDetailsService extends BaseService {

    static transactional = false

    @Autowired
    InvProductionDetailsCacheUtility invProductionDetailsCacheUtility

    /**
     * Method to get list of InvProductionDetails
     * @return -invProductionDetailsList
     */
    @Transactional(readOnly = true)
    public List list() {
        return InvProductionDetails.list(sort: invProductionDetailsCacheUtility.SORT_BY_LINE_ITEM_ID, order: invProductionDetailsCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    /**
     * Method to get list of inventoryProductionDetails
     * @param baseService
     * @return -list of inventoryProductionDetails
     */
    public List list(BaseService baseService) {
        String queryStr = """
                SELECT id, productionLine_item_id, production_item_type_id,
                material_id, overhead_cost FROM inv_production_details
                ORDER BY ${baseService.sortColumn} ${baseService.sortOrder}
                LIMIT ${baseService.resultPerPage} OFFSET ${baseService.start}
        """
        List<GroovyRowResult> lstInvProductionLineItem = executeSelectSql(queryStr)
        return lstInvProductionLineItem
    }

    private static final String CREATE_QUERY = """
            INSERT INTO inv_production_details(id, version,production_line_item_id,
                production_item_type_id, material_id, overhead_cost, company_id)
                VALUES (NEXTVAL('inv_production_details_id_seq'),
                :version,
                :productionLineItemId,
                :productionItemTypeId,
                :materialId,
                :overheadCost,
                :companyId
            );"""
    /**
     * Method to save InvProductionDetails object
     * @param InvProductionDetails -InvProductionDetails object
     * @return-newly created InvProductionDetails object
     */
    public InvProductionDetails create(InvProductionDetails invProductionDetails) {
        Map queryParams = [
                productionLineItemId: invProductionDetails.productionLineItemId,
                productionItemTypeId: invProductionDetails.productionItemTypeId,
                materialId: invProductionDetails.materialId,
                overheadCost: invProductionDetails.overheadCost,
                version: invProductionDetails.version,
                companyId: invProductionDetails.companyId
        ]
        List result = executeInsertSql(CREATE_QUERY, queryParams)
        Long id = (Long) result[0][0]
        invProductionDetails.id = id
        if (id <= 0) {
            throw new RuntimeException('error occurred at invProductionDetailsService.create')
        }
        return invProductionDetails;
    }

    /**
     * Method to read InvProductionDetails object from cacheUtility by id
     * @param id -InvProductionDetails.id
     * @return -InvProductionDetails object
     */
    public InvProductionDetails read(long id) {
        return (InvProductionDetails) invProductionDetailsCacheUtility.read(id);
    }

    private static final String UPDATE_QUERY = """
                    UPDATE inv_production_details SET
                          version=:newVersion,
                          production_line_item_id=:productionLineItemId,
                          production_item_type_id=:productionItemTypeId,
                          material_id=:materialId,
                          overhead_cost=:overheadCost
                    WHERE
                          id=:id AND
                          version=:version
                    """
    /**
     * Method to update InvProductionDetails object
     * @param InvProductionDetails -InvProductionDetails object
     * @return -updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(InvProductionDetails invProductionDetails) {
        Map queryParams = [
                newVersion: invProductionDetails.version + 1,
                productionLineItemId: invProductionDetails.productionLineItemId,
                productionItemTypeId: invProductionDetails.productionItemTypeId,
                materialId: invProductionDetails.materialId,
                overheadCost: invProductionDetails.overheadCost,
                id: invProductionDetails.id,
                version: invProductionDetails.version
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('error occurred at invProductionDetailsService.update')
        }
        invProductionDetails.version = invProductionDetails.version + 1
        return updateCount;
    }

    private static final String DELETE_QUERY = """
        DELETE FROM inv_production_details
        WHERE  id=:id
    """
    /**
     * Method to delete invProductionDetails object
     * @param id -invProductionDetails.id
     * @return -if deleteCount <= 0 then throw exception to rollback transaction; otherwise return true
     */
    public Boolean delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException('error occurred at invProductionDetailsService.create')

        }
        return Boolean.TRUE;
    }
}
