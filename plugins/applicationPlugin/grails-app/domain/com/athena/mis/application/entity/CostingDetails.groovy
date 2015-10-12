package com.athena.mis.application.entity

class CostingDetails {

    long id                             // primary key (Auto generated by its own sequence)
    int version
    String description                  // costing details description
    long costingTypeId                  // costingType.id
    long companyId                       // Company.id
    long createdBy                      // AppUser.id
    Date createdOn                      // Object creation DateTime
    long updatedBy = 0L                 // AppUser.id
    Date updatedOn                      // Object Updated DateTime
    double costingAmount = 0.0d         // Mendatory for costing amount
    Date costingDate                    // costing date

    static mapping = {
        id generator: 'sequence', params: [sequence: 'costing_details_id_seq']
        costingAmount sqlType: "numeric(16,4)"

        companyId index: 'costing_details_company_id_idx'
        createdBy index: 'costing_details_created_by_idx'
        updatedBy index: 'costing_details_updated_by_idx'
    }

    static constraints = {
        description(blank: false, nullable: true)
        companyId(nullable: false)
        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
        updatedOn(nullable: true)
        costingAmount(nulable: false)
        costingDate(nulable: false)
    }
}
