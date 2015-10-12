/**
 * Module Name - RequestMap
 * Purpose - Entity of RequestMap
 * */


package com.athena.mis.application.entity

class RequestMap {
    Integer version
    String url                 // application url (e.g : '/company/show')
    String configAttribute     // user role names separated by comma
    String featureName         // application feature name (e.g :Show For company)
    Integer pluginId           // application plugin id  (E.g: 1)
    String transactionCode     // unique transaction code (e.g: APP-10)
    Boolean isViewable         // True for show, False for rest
    Boolean isCommon           // determine if this feature is common for all company

    static mapping = {
        id generator: 'sequence', params: [sequence: 'request_map_id_seq']
    }

    static constraints = {
        url(blank: false, unique: true)
        transactionCode(blank: false, unique: true)
        configAttribute(size: 1..1000)
        featureName(nullable: true)
    }
}
