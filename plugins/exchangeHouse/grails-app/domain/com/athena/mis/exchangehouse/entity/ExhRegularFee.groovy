package com.athena.mis.exchangehouse.entity

class ExhRegularFee {
    long id
    int version
    String logic         // amount * 0.10
    long companyId      // Company.id

    long createdBy      // appUser.id
    Date createdOn
    long updatedBy      // appUser.id
    Date updatedOn


    static mapping = {
        id generator: 'sequence', params: [sequence: 'exh_regular_fee_id_seq']   // id generator: 'assigned'

        // indexing
        companyId index: 'exh_regular_fee_company_id_idx'
        createdBy index: 'exh_regular_fee_created_by_idx'
        updatedBy index: 'exh_regular_fee_updated_by_idx'
    }

    static constraints = {
        companyId(nullable: false)
        logic(nullable: false, size: 1..3000)
        updatedOn(nullable: true)
        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
    }
}
