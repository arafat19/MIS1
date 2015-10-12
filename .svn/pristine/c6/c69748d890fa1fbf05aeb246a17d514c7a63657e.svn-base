package com.athena.mis.application.entity

class Currency {
    Integer id
    Integer version
    String name
    String symbol
    Boolean isToCurrency = Boolean.FALSE

    long companyId         //Company.id

    long createdBy          // AppUser.id
    Date createdOn          // Object creation DateTime
    long updatedBy = 0L     // AppUser.id
    Date updatedOn          // Object Updated DateTime

    static mapping = {
        id generator: 'sequence', params: [sequence: 'currency_id_seq']
        companyId index: 'currency_company_id_idx'

        createdBy index: 'currency_created_by_idx'
        updatedBy index: 'currency_updated_by_idx'
    }

    static constraints = {
        name(nullable: false);
        symbol(nullable: false, unique: true);  // size: 3..3
        companyId(nullable: false)

        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
        updatedOn(nullable: true)
    }

    public String toString() {
        return symbol;
    }
}


