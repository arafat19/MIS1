package com.athena.mis.document.service

import com.athena.mis.BaseService
import com.athena.mis.document.entity.DocInvitedMembers
import com.athena.mis.utility.DateUtility

class DocInvitedMembersService extends BaseService {

    public DocInvitedMembers create(DocInvitedMembers docInvitedMembers) {
        DocInvitedMembers newDocInvitedMembers = docInvitedMembers.save()
        return newDocInvitedMembers
    }

    public DocInvitedMembers findByInvitationCodeAndCompanyId(String invitationCode, long companyId) {
        DocInvitedMembers docInvitedMembers = DocInvitedMembers.findByInvitationCodeAndCompanyId(invitationCode, companyId)
        return docInvitedMembers
    }

    public DocInvitedMembers readByIdAndCompany(long id, long companyId) {
        DocInvitedMembers docInvitedMember = DocInvitedMembers.findByIdAndCompanyId(id, companyId)
        return docInvitedMember
    }


    /**
     * SQL to update DocInvitedMembers object in database
     * @param oldDocInvitedMembers -DocInvitedMembers object
     * @return -int value(updateCount)
     */
    public boolean update(DocInvitedMembers invitedMember) {
        String query = """UPDATE doc_invited_members SET
                          invitation_accepted_on=:invitationAcceptedOn
                      WHERE
                          id=:id """

        Map queryParams = [
                invitationAcceptedOn: DateUtility.getSqlDateWithSeconds(invitedMember.invitationAcceptedOn),
                id: invitedMember.id,
        ]
        int updateCount = executeUpdateSql(query, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("error occurred at DocInvitedMembers update")
        }
        return true
    }

    public Map outstandingInvitationList(Date fromDate, Date toDate, BaseService baseService) {
        Map result
        List<DocInvitedMembers> lstInvitedMember = DocInvitedMembers.withCriteria {
            between('invitationSentOn', fromDate, toDate)
            maxResults(baseService.resultPerPage)
            setFirstResult(baseService.start)
            setReadOnly(true)
        }
        List<DocInvitedMembers> lstInvitedMemberCount = DocInvitedMembers.withCriteria {
            between('invitationSentOn', fromDate, toDate)
        }
        result = [lstInvitedMember: lstInvitedMember, count: lstInvitedMemberCount.size()]
        return result
    }

}
