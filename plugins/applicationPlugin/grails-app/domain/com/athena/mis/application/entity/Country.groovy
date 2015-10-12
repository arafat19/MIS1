package com.athena.mis.application.entity

class Country {

    long id
    int version
    String name                 // Unique name
    String code                 // Unique Code
    String isdCode              // Unique Code
    String phoneNumberPattern  // mobile or phone pattern ie ^(\+088[\-\s]?)\d{11}$
    String nationality
    long currencyId            // Currency.id
    long companyId             // Company.id

    long createdBy          // AppUser.id
    Date createdOn          // Object creation DateTime
    long updatedBy = 0L     // AppUser.id
    Date updatedOn          // Object Updated DateTime

    static constraints = {
        name (unique: true, nullable: false)
        code(unique: true, nullable: false)
        isdCode(unique: true, nullable: false, size: 1..5)
        phoneNumberPattern(nullable: false)
        nationality(nullable: false)
        currencyId(nullable: false)
        companyId(nullable: false)

        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
        updatedOn(nullable: true)
    }

    static mapping = {
        id generator: 'sequence', params: [sequence: 'country_id_seq']
        companyId index: 'country_company_id_idx'
        currencyId index: 'country_currency_id_idx'
        createdBy index: 'country_created_by_idx'
        updatedBy index: 'country_updated_by_idx'
    }
}
