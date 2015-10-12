package com.athena.mis.document.service

import com.athena.mis.BaseService
import com.athena.mis.document.entity.DocMemberJoinRequest
import com.athena.mis.utility.DateUtility

class DocMemberJoinRequestService extends BaseService {

    public DocMemberJoinRequest create(DocMemberJoinRequest docJoinRequest) {
        DocMemberJoinRequest newDocJoinRequest = docJoinRequest.save()
        return newDocJoinRequest
    }

    public DocMemberJoinRequest findByCategoryIdAndEmailAndCompanyId(long categoryId, String email, long companyId) {
        DocMemberJoinRequest docJoinRequest = DocMemberJoinRequest.findByCategoryIdAndEmailAndCompanyId(categoryId, email, companyId)
        return docJoinRequest
    }

    public DocMemberJoinRequest findByCategoryIdAndSubCategoryIdAndEmailAndCompanyId(long categoryId, long subCategoryId, String email, long companyId) {
        DocMemberJoinRequest docJoinRequest = DocMemberJoinRequest.findByCategoryIdAndSubCategoryIdAndEmailAndCompanyId(categoryId,subCategoryId, email, companyId)
        return docJoinRequest
    }

    public Map appliedJoinRequestedList(Date fromDate, Date toDate, BaseService baseService) {
        Map result
        List<DocMemberJoinRequest> lstJoinRequest = DocMemberJoinRequest.withCriteria {
            between('createdOn', fromDate, toDate)
            order(baseService.sortColumn, baseService.sortOrder)
            maxResults(baseService.resultPerPage)
            setFirstResult(baseService.start)
            setReadOnly(true)
        }
        List<DocMemberJoinRequest> lstJoinRequestCount = DocMemberJoinRequest.withCriteria {
            between('createdOn', fromDate, toDate)
        }
        result = [lstJoinRequest: lstJoinRequest, count: lstJoinRequestCount.size()]
        return result
    }

    public DocMemberJoinRequest read(long id) {
        DocMemberJoinRequest docJoinRequest = DocMemberJoinRequest.read(id)
        return docJoinRequest
    }

    /**
     * SQL to update DocMemberJoinRequest object in database
     * @param joinRequest -DocMemberJoinRequest object
     * @return -int value(updateCount)
     */
    public boolean update(DocMemberJoinRequest joinRequest) {
        String query = """UPDATE doc_member_join_request SET
                          approved_by=:approvedBy,
                          approved_on=:approvedOn
                      WHERE
                          id=:id """

        Map queryParams = [
                approvedBy: joinRequest.approvedBy,
                approvedOn: DateUtility.getSqlDateWithSeconds(joinRequest.approvedOn),
                id: joinRequest.id
        ]
        int updateCount = executeUpdateSql(query, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("error occurred at DocMemberJoinRequest update")
        }
        return true
    }

}
