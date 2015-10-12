package com.athena.mis.document.entity

class DocInvitedMembers {

    long id                         //Primary Key (Auto Generated By it's own sequence)
    String email                    //Email Address
    Date invitationSentOn           //Invitation Sent Date
    Date invitationAcceptedOn       //Invitation Accepted Date
    String invitationCode           //Invitation Code(will be Auto Generated)
    String invitationLink           //Invitation link
    String message                  //Message
    Date expiredOn                  //Invitation Expired date
    long createdBy                  //AppUser.id
    long resendBy                  //AppUser.id
    long companyId                  //Company.id

    static mapping = {
        version false
        id generator: 'sequence', params: [sequence: 'doc_invited_members_id_seq']
        createdBy index: 'doc_invited_members_created_by_idx'
        companyId index: 'doc_invited_members_company_id_idx'
        expiredOn type: 'date'
    }
    static constraints = {
        email(nullable: false)
        invitationSentOn(nullable: false)
        invitationAcceptedOn(nullable: true)
        invitationCode(nullable: false)
        invitationLink(nullable: false)
        message(nullable: false)
        expiredOn(nullable: false)
        companyId(nullable: false)
        resendBy(nullable: false)
    }
}
