package com.athena.mis.application.entity


class Bank {

    Integer version
    String name
    String code
    long companyId      // Company.id
    boolean isSystemBank

    long createdBy          // AppUser.id
    Date createdOn          // Object creation DateTime
    long updatedBy = 0L     // AppUser.id
    Date updatedOn          // Object Updated DateTime


    static mapping = {
        id generator: 'sequence', params: [sequence: 'bank_id_seq']
        createdBy index: 'bank_created_by_idx'
        updatedBy index: 'bank_updated_by_idx'
        companyId index: 'bank_company_id_idx'
    }

    static constraints = {
        name(blank: false, nullable: false, unique: true)
        code(blank: false, nullable: false, unique: true)
        companyId(nullable: false)
        isSystemBank(nullable: true)
        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
        updatedOn(nullable: true)
    }

    public String toString() {
        return name
    }
}
