package com.athena.mis.procurement.service

import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult

class PurchaseRequestService extends BaseService {

    static transactional = false

    /**
     * Method to read ProcPurchaseRequest object by id
     * @param id - ProcPurchaseRequest.id
     * @return - an object of ProcPurchaseRequest
     */
    public ProcPurchaseRequest read(long id) {
        return ProcPurchaseRequest.read(id)
    }

    /**
     * Method to save ProcPurchaseRequest object
     * @param purchaseRequest - object of ProcPurchaseRequest
     * @return - newly created ProcPurchaseRequest object
     */
    public ProcPurchaseRequest create(ProcPurchaseRequest purchaseRequest) {
        ProcPurchaseRequest newPurchaseRequest = purchaseRequest.save()
        return newPurchaseRequest
    }

    private static final String PROC_PURCHASE_REQUEST_UPDATE_QUERY = """
                      UPDATE proc_purchase_request SET
                          comments=:comments,
                          indent_id=:indentId,
                          approved_by_director_id=:approvedByDirectorId,
                          approved_by_project_director_id=:approvedByProjectDirectorId,
                          sent_for_approval=:isSentForApproval,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy,
                          version=:newVersion
                      WHERE
                          id=:id AND
                          version=:version
    """

    /**
     * Method to update ProcPurchaseRequest object
     * @param purchaseRequest - Object of ProcPurchaseRequest
     * @return - updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(ProcPurchaseRequest purchaseRequest) {
        Map queryParams = [
                id: purchaseRequest.id,
                version: purchaseRequest.version,
                newVersion: purchaseRequest.version + 1,
                comments: purchaseRequest.comments,
                indentId: purchaseRequest.indentId,
                approvedByDirectorId: purchaseRequest.approvedByDirectorId,
                approvedByProjectDirectorId: purchaseRequest.approvedByProjectDirectorId,
                isSentForApproval: purchaseRequest.sentForApproval,
                updatedOn: DateUtility.getSqlDate(purchaseRequest.updatedOn),
                updatedBy: purchaseRequest.updatedBy
        ]

        int updateCount = executeUpdateSql(PROC_PURCHASE_REQUEST_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update PR")
        }
        return updateCount
    }

    private static final String UPDATE_QUERY = """
                      UPDATE proc_purchase_request SET
                          approved_by_director_id=:approvedByDirectorId,
                          approved_by_project_director_id=:approvedByProjectDirectorId,
                          version = version + 1
                      WHERE
                          id=:id AND
                          version=:version
    """
    /**
     * Method to update ProcPurchaseRequest approval
     * @param purchaseRequest - object of ProcPurchaseRequest
     * @return - updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int updateForApproval(ProcPurchaseRequest purchaseRequest) {
        Map queryParams = [
                id: purchaseRequest.id,
                version: purchaseRequest.version,
                approvedByDirectorId: purchaseRequest.approvedByDirectorId,
                approvedByProjectDirectorId: purchaseRequest.approvedByProjectDirectorId
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to approve PR")
        }
        return updateCount
    }

    /**
     * Method to get the list of ProcPurchaseRequest by dynamic finder
     * @param baseService - object of BaseServices
     * @param projectIds - List of project ids
     * @return - a LinkedHashMap containing purchase request list and it's count
     */
    public LinkedHashMap list(BaseService baseService, List<Long> projectIds) {
        List<ProcPurchaseRequest> purchaseRequestList = ProcPurchaseRequest.findAllByProjectIdInList(projectIds, [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
        int count = ProcPurchaseRequest.countByProjectIdInList(projectIds)
        return [purchaseRequestList: purchaseRequestList, count: count]
    }

    //@todo:model adjust using PurchaseRequestModel.listByProject()
    /**
     * Method to search purchase request list by project ids
     * @param baseService - object of BaseService
     * @param projectIds - list of project of ids
     * @return - a LinkedHashMap containing a list of purchase request list and it's count
     */
    public LinkedHashMap search(BaseService baseService, List<Long> projectIds) {
        String lstProject = Tools.buildCommaSeparatedStringOfIds(projectIds)
        String queryStr = """
            SELECT pr.id, pr.project_id, project.name AS project_name, budget.budget_item AS budget_item,
                   pr.item_count, to_char(pr.target,'${Tools.DB_CURRENCY_FORMAT}') ||' '|| se.key AS target,
                   pr.approved_by_director_id AS approved_by_director_id,
                   pr.approved_by_project_director_id AS approved_by_project_director_id,
                   pr.created_by AS created_by
            FROM proc_purchase_request pr
            LEFT JOIN budg_budget budget ON pr.budget_id = budget.id
            LEFT JOIN project ON pr.project_id = project.id
            LEFT JOIN system_entity se ON se.id = budget.unit_id
            WHERE pr.project_id IN (${lstProject})
            LIMIT ${baseService.resultPerPage}  OFFSET ${baseService.start}
        """
        List<GroovyRowResult> purchaseRequestList = executeSelectSql(queryStr)

        //@todo:model adjust using dynamic finder
        String queryCount = """
            SELECT COUNT(pr.id)
            FROM proc_purchase_request pr
            LEFT JOIN budg_budget budget ON pr.budget_id = budget.id
            WHERE pr.project_id IN (${lstProject})
        """
        List countResults = executeSelectSql(queryCount)
        int count = countResults[0].count
        return [purchaseRequestList: purchaseRequestList, count: count]
    }

    private static final String DELETE_QUERY = """
       DELETE FROM proc_purchase_request
          WHERE
              id=:id
              AND company_id=:companyId
    """

    /**
     * Method to delete ProcPurchaseRequest object
     * @param purchaseRequest - object of ProcPurchaseRequest
     * @return - if deleteCount <= 0 then throw exception to rollback transaction
     */
    public Boolean delete(ProcPurchaseRequest purchaseRequest) {
        int deleteCount = executeUpdateSql(DELETE_QUERY, [id: purchaseRequest.id, companyId: purchaseRequest.companyId])
        if (deleteCount <= 0) {
            throw new RuntimeException('Budget update failed on PR Delete')
        }
        return Boolean.TRUE;
    }

    //@todo:model adjust using dynamic finder and cacheUtility
    /**
     * Get list of un approved Purchase Request for dash board
     * 1. Get project ids
     * @param baseService - object of BaseService
     * @param lstProjectIds - list of project Ids
     * @return - a LinkedHashMap of query result(list of un-approved PR) and it's count
     */
    public LinkedHashMap listUnApprovedPR(BaseService baseService, List<Long> lstProjectIds) {
        String projectIds = Tools.buildCommaSeparatedStringOfIds(lstProjectIds)
        String queryStr = """
            SELECT pr.id pr_id, app_user.username,
        CASE WHEN (pr.approved_by_director_id>0) THEN 'YES'
        ELSE 'NO'
        END approved_director,
            CASE WHEN (pr.approved_by_project_director_id>0) THEN 'YES'
        ELSE 'NO'
        END approved_pd
        FROM proc_purchase_request pr
        LEFT JOIN app_user ON app_user.id=pr.created_by
        WHERE ((pr.approved_by_director_id <=0) OR (pr.approved_by_project_director_id <=0))
        AND pr.item_count>0
        AND pr.project_id IN (${projectIds})
        ORDER BY ${baseService.sortColumn} ${baseService.sortOrder}
        LIMIT ${baseService.resultPerPage}  OFFSET ${baseService.start}
        """

        List<GroovyRowResult> result = executeSelectSql(queryStr)

        String queryCount = """
           SELECT COUNT(pr.id) AS count
            FROM proc_purchase_request pr
            WHERE ((pr.approved_by_director_id <=0) OR (pr.approved_by_project_director_id <=0))
            AND pr.item_count>0
             AND pr.project_id IN ( ${projectIds} )
        """

        List resultCount = executeSelectSql(queryCount)
        int count = resultCount[0].count
        return [unApprovedList: result, count: count]
    }
}