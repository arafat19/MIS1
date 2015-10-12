package com.athena.mis.procurement.service

import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.springframework.beans.factory.annotation.Autowired

class PurchaseOrderService extends BaseService {

    static transactional = false

    @Autowired
    ProcSessionUtil procSessionUtil

    /**
     * Get Purchase order list by dynamic finder
     * @param baseService - object of BaseService
     * @param projectIds - list of project Ids
     * @return - a map containing purchase order list and count
     */
    public Map list(BaseService baseService, List<Long> projectIds) {
        List<ProcPurchaseOrder> purchaseOrderList = ProcPurchaseOrder.findAllByProjectIdInList(projectIds, [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
        int count = ProcPurchaseOrder.countByProjectIdInList(projectIds)
        return [purchaseOrderList: purchaseOrderList, count: count]
    }

    /**
     * Get approved purchase order object by id
     * @param id - ProcPurchaseOrder.id
     * @return - object of ProcPurchaseOrder
     */
    public ProcPurchaseOrder readApprovedPurchaseOrder(long id) {
        ProcPurchaseOrder purchaseOrder = ProcPurchaseOrder.findByIdAndApprovedByDirectorIdNotEqualAndApprovedByProjectDirectorIdNotEqualAndCompanyId(id, 0L, 0L, procSessionUtil.appSessionUtil.getCompanyId(), [readOnly: true])
        return purchaseOrder
    }

    /**
     * Get Purchase order list by name query
     * @param baseService - object of BaseService
     * @param projectIds - list of project Ids
     * @return - a map of purchase order list and it's total
     */
    public Map search(BaseService baseService, List<Long> projectIds) {
        List<ProcPurchaseOrder> purchaseOrderList = ProcPurchaseOrder.withCriteria {
            'in'("projectId", projectIds)
            ilike(baseService.queryType, Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            order(baseService.sortColumn, baseService.sortOrder)
        } as List

        List counts = ProcPurchaseOrder.withCriteria {
            'in'("projectId", projectIds)
            ilike(baseService.queryType, Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE)
            projections { rowCount() }
        } as List

        int total = counts[0] as int
        return [purchaseOrderList: purchaseOrderList, count: total]
    }

    private static final String PROC_PURCHASE_ORDER_CREATE_QUERY = """
            INSERT INTO proc_purchase_order(id, version,project_id,purchase_request_id,payment_method_id,
                    mode_of_payment,created_on,created_by,updated_by,approved_by_director_id,approved_by_project_director_id,
                    tr_cost_count,tr_cost_total,total_vat_tax,supplier_id,total_price,discount,company_id,comments,item_count, sent_for_approval)
            VALUES (NEXTVAL('proc_purchase_order_id_seq'),:version,:projectId,:purchaseRequestId,
                    :paymentMethodId,:modeOfPayment,:createdOn,:createdBy,:updatedBy,:approvedByDirectorId,
                    :approvedByProjectDirectorId,:trCostCount,:trCostTotal,:totalVatTax,:supplierId,:totalPrice,
                    :discount,:companyId,:comments,:itemCount,:sentForApproval);
           """

    /**
     * Method to create ProcPurchaseOrder object
     * @param purchaseOrder - object of ProcPurchaseOrder
     * @return- object of ProcPurchaseOrder
     */
    public ProcPurchaseOrder create(ProcPurchaseOrder purchaseOrder) {
        Map queryParams = [
                version: purchaseOrder.version,
                projectId: purchaseOrder.projectId,
                purchaseRequestId: purchaseOrder.purchaseRequestId,
                paymentMethodId: purchaseOrder.paymentMethodId,
                modeOfPayment: purchaseOrder.modeOfPayment,
                createdBy: purchaseOrder.createdBy,
                updatedBy: purchaseOrder.updatedBy,
                approvedByDirectorId: purchaseOrder.approvedByDirectorId,
                approvedByProjectDirectorId: purchaseOrder.approvedByProjectDirectorId,
                trCostCount: purchaseOrder.trCostCount,
                trCostTotal: purchaseOrder.trCostTotal,
                totalVatTax: 0.0,
                supplierId: purchaseOrder.supplierId,
                totalPrice: purchaseOrder.totalPrice,
                discount: purchaseOrder.discount,
                companyId: purchaseOrder.companyId,
                comments: purchaseOrder.comments,
                itemCount: purchaseOrder.itemCount,
                createdOn: DateUtility.getSqlDateWithSeconds(purchaseOrder.createdOn),
                sentForApproval: purchaseOrder.sentForApproval

        ]
        List result = executeInsertSql(PROC_PURCHASE_ORDER_CREATE_QUERY, queryParams)
        if (result.size() <= 0) { // if result.size() <= 0 then the method throw a RuntimeException
            throw new RuntimeException('Error occurred while insert budget information')
        }
        int purchaseOrderId = (int) result[0][0]
        purchaseOrder.id = purchaseOrderId
        return purchaseOrder
    }

    private static final String PROC_PURCHASE_ORDER_UPDATE_QUERY = """
                      UPDATE proc_purchase_order SET
                          supplier_id=:supplierId,
                          payment_method_id=:paymentMethodId,
                          mode_of_payment=:modeOfPayment,
                          comments=:comments,
                          discount=:discount,
                          total_price = :totalPrice,
                          approved_by_director_id=:approvedByDirectorId,
                          approved_by_project_director_id=:approvedByProjectDirectorId,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy,
                          version=:newVersion
                      WHERE
                          id=:id AND
                          version=:version
        """

    /**
     * Method to update ProcPurchaseOrder object
     * @param purchaseOrder - object of ProcPurchaseOrder
     * @return- updateCount ( intValue ) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(ProcPurchaseOrder purchaseOrder) {
        Map queryParams = [
                id: purchaseOrder.id,
                version: purchaseOrder.version,
                newVersion: purchaseOrder.version + 1,
                modeOfPayment: purchaseOrder.modeOfPayment,
                comments: purchaseOrder.comments,
                discount: purchaseOrder.discount,
                totalPrice: purchaseOrder.totalPrice,
                supplierId: purchaseOrder.supplierId,
                paymentMethodId: purchaseOrder.paymentMethodId,
                approvedByDirectorId: purchaseOrder.approvedByDirectorId,
                approvedByProjectDirectorId: purchaseOrder.approvedByProjectDirectorId,
                updatedOn: DateUtility.getSqlDateWithSeconds(purchaseOrder.updatedOn),
                updatedBy: purchaseOrder.updatedBy
        ]
        int updateCount = executeUpdateSql(PROC_PURCHASE_ORDER_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('PO update failed')
        }
        purchaseOrder.version + purchaseOrder.version + 1
        return updateCount
    }

    private static final String UPDATE_QUERY = """
                      UPDATE proc_purchase_order SET
                          approved_by_director_id=:approvedByDirectorId,
                          approved_by_project_director_id=:approvedByProjectDirectorId,
                          version = version + 1
                      WHERE
                          id=:id AND
                          version=:version
                      """

    /**
     * Method to update ProcPurchaseOrder
     * @param purchaseOrder - object of ProcPurchaseOrder
     * @return - updateCount an  integer
     */
    public int updateForApproval(ProcPurchaseOrder purchaseOrder) {
        Map queryParams = [
                id: purchaseOrder.id,
                version: purchaseOrder.version,
                approvedByDirectorId: purchaseOrder.approvedByDirectorId,
                approvedByProjectDirectorId: purchaseOrder.approvedByProjectDirectorId
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        return updateCount
    }

    /**
     * Method to read ProcPurchaseOrder object by id
     * @param id - ProcPurchaseOrder.id
     * @return - object of ProcPurchaseOrder
     */
    public ProcPurchaseOrder read(long id) {
        return (ProcPurchaseOrder) ProcPurchaseOrder.read(id);
    }


    private static final String DELETE_QUERY = """
                                            DELETE FROM proc_purchase_order
                                                WHERE id=:id
                                                AND company_id=:companyId
                                        """
    /**
     * Method to delete ProcPurchaseOrder object
     * @param id - ProcPurchaseOrder.id
     * @param companyId - company id
     * @return if deleteCount <= 0 then throw exception to rollback transaction
     */
    public Boolean delete(long id, long companyId) {

        int deleteCount = executeUpdateSql(DELETE_QUERY, [id: id, companyId: companyId])
        if (deleteCount <= 0) {
            throw new RuntimeException('Failed to delete Purchase Order')
        }
        return Boolean.TRUE;
    }

    /**
     * Get list of un approved Purchase Order
     * 1. Get project ids
     * @param baseService - object of BaseService
     * @param lstProjectIds - list of project Ids
     * @return - a LinkedHashMap of query result(list of un-approved PO) and it's count
     */
    public LinkedHashMap listUnApprovedPO(BaseService baseService, List<Long> lstProjectIds) {
        String projectIds = Tools.buildCommaSeparatedStringOfIds(lstProjectIds)
        String queryStr = """
        SELECT po.id po_id, app_user.username,
        CASE WHEN (po.approved_by_director_id>0) THEN 'YES'
        ELSE 'NO'
        END approved_director,
        CASE WHEN (po.approved_by_project_director_id>0) THEN 'YES'
        ELSE 'NO'
        END approved_pd
        FROM proc_purchase_order po
        LEFT JOIN app_user ON app_user.id=po.created_by
        WHERE ((po.approved_by_director_id <=0) OR (po.approved_by_project_director_id <=0))
        AND po.item_count>0
        AND po.project_id IN ( ${projectIds} )
        ORDER BY ${baseService.sortColumn} ${baseService.sortOrder}
        LIMIT ${baseService.resultPerPage}  OFFSET ${baseService.start}
        """

        List<GroovyRowResult> result = executeSelectSql(queryStr)

        String queryCount = """
           SELECT COUNT(po.id) AS count
            FROM proc_purchase_order po
            WHERE ((po.approved_by_director_id <=0) OR (po.approved_by_project_director_id <=0))
            AND po.item_count>0
             AND po.project_id IN ( ${projectIds} )
        """

        List resultCount = executeSelectSql(queryCount)
        int count = resultCount[0].count
        return [unApprovedList: result, count: count]
    }
}
