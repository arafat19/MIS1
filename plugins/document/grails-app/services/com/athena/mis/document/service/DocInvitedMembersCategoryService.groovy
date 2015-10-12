package com.athena.mis.document.service

import com.athena.mis.BaseService
import com.athena.mis.document.entity.DocInvitedMembersCategory

class DocInvitedMembersCategoryService extends BaseService {

    public DocInvitedMembersCategory create(DocInvitedMembersCategory docInvitedMembersCategory) {
        DocInvitedMembersCategory newDocInvitedMembersCategory = docInvitedMembersCategory.save()
        return newDocInvitedMembersCategory
    }

    public List<DocInvitedMembersCategory> findAllByInvitedMemberId(Long invitedMemberId) {
        List<DocInvitedMembersCategory> lstDocInvitedMembersCategory = DocInvitedMembersCategory.findAllByInvitedMemberId(invitedMemberId)
        return lstDocInvitedMembersCategory
    }

}
