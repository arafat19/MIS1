/**
 * Module Name - Application
 * Purpose - Keep user Group Information
 * */

package com.athena.mis.application.entity

class AppGroup {

    long id              // primary key (Auto generated by its own sequence)
    int version
    String name          //Group Name
    long createdBy       // AppUser.id
    Date createdOn       // Object creation DateTime
    long updatedBy = 0L  // AppUser.id
    Date updatedOn       // Object updated DateTime
    long companyId       // Company.id

    static mapping = {
        id generator: 'sequence', params: [sequence: 'app_group_id_seq']
        createdBy index: 'app_group_created_by_idx'
        updatedBy index: 'app_group_updated_by_idx'
        companyId index: 'app_group_company_id_idx'
    }

    static constraints = {
        name(nullable: false)
        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
        updatedOn(nullable: true)
        companyId(nullable: false)
    }
}
