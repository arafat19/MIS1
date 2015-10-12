package com.athena.mis.application.entity

// Theme domain contains different configurable objects related with theme e.g. logo, welcome-text

class Theme {
    long id             // primary key
    int version         // Object version in persistence layer
    String key          // key is unique by company, not changeable
    String value        // user can change/configure corresponding value of key
    String description  // description of the theme
    Date updatedOn       // Object updated DateTime
    long updatedBy       // AppUser.id
    long companyId       // Company.id

    static constraints = {
        key(nullable: false)
        value(nullable: false, maxSize: 15359)
        description(nullable: true, maxSize: 1000)
        companyId(nullable: false)
        updatedOn(nullable: true)
        updatedBy(nullable: false)
        key unique: 'companyId'
    }

    static mapping = {
        id generator: 'sequence', params: [sequence: 'theme_id_seq']
        updatedOn type: 'date'
        updatedBy index: 'theme_updated_by_idx'
        companyId index: 'theme_company_id_idx'
    }
}
