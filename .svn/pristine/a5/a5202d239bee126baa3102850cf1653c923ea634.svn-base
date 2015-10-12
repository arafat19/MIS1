package com.athena.mis.application.entity

class District {
    long id
    int version
    String name
    long companyId      // Company.id
    boolean isGlobal    //determine if ANY DISTRICT(for ARMS plugin)

    long createdBy          // AppUser.id
    Date createdOn          // Object creation DateTime
    long updatedBy = 0L     // AppUser.id
    Date updatedOn          // Object Updated DateTime

    static mapping = {
        id generator: 'sequence', params: [sequence: 'district_id_seq']
        createdBy index: 'district_created_by_idx'
        updatedBy index: 'district_updated_by_idx'
        companyId index: 'district_company_id_idx'

    }
    static constraints = {
        name(nullable: false, unique: true)
        companyId(nullable: false)
        isGlobal(nullable: true)

        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
        updatedOn(nullable: true)
    }

    public String toString() {
        return name
    }
}
