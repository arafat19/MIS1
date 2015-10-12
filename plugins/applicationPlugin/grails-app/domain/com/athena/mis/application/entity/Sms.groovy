package com.athena.mis.application.entity

class Sms {
    long id                     // Object Id
    int version                 // Object version in persistence layer
    String url                  // url of sms which should contain two variables: 1. content 2. recipient
    String body                 // content of SMS. content may contain one or more variables to evaluate
    String description          // description of SMS
    String transactionCode      // feature code
    long companyId              // Company.id
    boolean isActive            // only activated SMS will be sent by system
    String recipients           // comma separated string of phone numbers e.g. 8801671010101, 0171010101
    boolean isManualSend        // can be send manually = true, otherwise = false
    int pluginId                // plugin id (Application=1, Accounting=2 etc.)
    String controllerName       // controller name e.g. accReport
    String actionName           // action(closure) e.g. sendSms

    static constraints = {
        body(nullable: false, maxSize: 2040)
        transactionCode(nullable: false)
        companyId(nullable: false)
        description(nullable: true)
        isActive(nullable: false)
        pluginId(nullable: false)
        recipients(nullable: false)
        controllerName(nullable: true)
        actionName(nullable: true)
    }

    static mapping = {
        id generator: 'sequence', params: [sequence: 'sms_id_seq']
        // indexing
        companyId index: 'sms_company_id_idx'
    }
}
