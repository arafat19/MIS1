package com.athena.mis.application.entity

// AppMail is used to determine the mail template for a particular process

class AppMail {
    String roleIds                  // List of Role Ids
    String recipients               // recipients email ids
    String subject                  // subject of mail
    String body                     // body of mail
    String mimeType                 // ---------------
    String transactionCode          // feature code
    long companyId                  // Company.id
    boolean isActive                // only activated mail will be sent by system
    boolean isRequiredRoleIds       // are role ids required or not
    boolean isRequiredRecipients    // are recipients required or not
    boolean isManualSend            // can be send manually = true, otherwise = false
    String controllerName           // controller name of corresponding use case (applicable for isManualSend = true)
    String actionName               // action(closure) name of corresponding use case (applicable for isManualSend = true)
    int pluginId                    // plugin id

    static constraints = {
        roleIds(nullable: true)
        recipients(nullable: true)
        subject(nullable: false)
        body(nullable: false, maxSize: 2040)
        mimeType(nullable: true)
        transactionCode(nullable: false)
        companyId(nullable: false)
        pluginId(nullable: false)
        controllerName(nullable: true)
        actionName(nullable: true)
        isRequiredRoleIds(nullable: false)
        isRequiredRecipients(nullable: false)
    }

    static mapping = {
        id generator: 'sequence', params: [sequence: 'app_mail_id_seq']
        companyId index: 'app_mail_company_id_idx'
    }
}
