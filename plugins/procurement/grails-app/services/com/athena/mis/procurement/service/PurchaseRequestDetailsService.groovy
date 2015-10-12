package com.athena.mis.procurement.service

import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcPurchaseRequestDetails
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools

class PurchaseRequestDetailsService extends BaseService {

    static transactional = false

    /**
     * Method to read ProcPurchaseRequestDetails object by id
     * @param id - ProcPurchaseRequestDetails.id
     * @return - an object of ProcPurchaseRequestDetails
     */
    public ProcPurchaseRequestDetails read(long id) {
        ProcPurchaseRequestDetails purchaseRequestDetails = ProcPurchaseRequestDetails.read(id)
        return purchaseRequestDetails
    }

    /**
     * Method to save ProcPurchaseRequestDetails object
     * @param purchaseRequestDetails - object of ProcPurchaseRequestDetails
     * @return - newly created object of ProcPurchaseRequestDetails
     */
    public ProcPurchaseRequestDetails create(ProcPurchaseRequestDetails purchaseRequestDetails) {
        ProcPurchaseRequestDetails newPurchaseRequestDetails = purchaseRequestDetails.save()
        return newPurchaseRequestDetails
    }

    private static final String PROC_PURCHASE_REQUEST_DETAILS_UPDATE_QUERY = """
                      UPDATE proc_purchase_request_details SET
                          quantity=:quantity,
                          comments=:comments,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy,
                          rate=:rate,
                          version=:newVersion
                      WHERE
                          id=:id AND
                          version=:version
    """

    /**
     * Method to update ProcPurchaseRequestDetails object
     * @param purchaseRequestDetails - object of ProcPurchaseRequestDetails
     * @return - updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(ProcPurchaseRequestDetails purchaseRequestDetails) {
        Map queryParams = [
                id: purchaseRequestDetails.id,
                version: purchaseRequestDetails.version,
                newVersion: purchaseRequestDetails.version + 1,
                quantity: purchaseRequestDetails.quantity,
                comments: purchaseRequestDetails.comments,
                rate: purchaseRequestDetails.rate,
                updatedOn: DateUtility.getSqlDateWithSeconds(purchaseRequestDetails.updatedOn),
                updatedBy: purchaseRequestDetails.updatedBy
        ]

        int updateCount = executeUpdateSql(PROC_PURCHASE_REQUEST_DETAILS_UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update PR Details")
        }
        return updateCount
    }

    /**
     * Method to get the list of purchase request details by dynamic finder
     * @param baseService - object of BaseService
     * @param projectIds - list of project ids
     * @return - a LinkedHashMap containing list of purchase request details and it's count
     */
    public LinkedHashMap list(BaseService baseService, List<Long> projectIds) {
        List<ProcPurchaseRequestDetails> purchaseRequestDetailsList = ProcPurchaseRequestDetails.findAllByProjectIdInList(projectIds, [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
        int count = ProcPurchaseRequestDetails.countByProjectIdInList(projectIds)
        return [purchaseRequestDetailsList: purchaseRequestDetailsList, count: count]
    }

    /**
     * Method to search purchase request details by name query
     * @param baseService - object of BaseService
     * @param projectIds - list of project ids
     * @return - a LinkedHashMap containing list of purchase request details and it's total
     */
    public LinkedHashMap search(BaseService baseService, List<Long> projectIds) {
        List<ProcPurchaseRequestDetails> purchaseRequestDetailsList = ProcPurchaseRequestDetails.withCriteria {
            'in'("projectId", projectIds)
            ilike(baseService.queryType, Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            order(baseService.sortColumn, baseService.sortOrder)
        } as List;

        List counts = ProcPurchaseRequestDetails.withCriteria {
            'in'("projectId", projectIds)
            ilike(baseService.queryType, Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE)
            projections { rowCount() }
        } as List

        int total = counts[0] as int
        return [purchaseRequestDetailsList: purchaseRequestDetailsList, count: total]
    }

    private static final String DELETE_QUERY = """
        DELETE FROM proc_purchase_request_details
        WHERE
            id=:id
        AND company_id = :companyId
    """

    /**
     * Method to delete ProcPurchaseRequestDetails object
     * @param id - ProcPurchaseRequestDetails.id
     * @param companyId - company id
     * @return - if deleteCount <= 0 then throw exception to rollback transaction
     */
    public int delete(long id, long companyId) {
        int deleteCount = executeUpdateSql(DELETE_QUERY, [id: id, companyId: companyId])
        if (deleteCount <= 0) {
            throw new RuntimeException("Failed to delete PR Details")
        }
        return deleteCount
    }
}
