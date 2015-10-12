/**
 * Module Name - Fixed asset
 * Purpose - Entity of Fixed asset maintenance type information
 * */

package com.athena.mis.fixedasset.entity

class FxdMaintenanceType {

    long id                 // primary key (Auto generated by its own sequence)
    int version
    String name             //Maintenance Type name(e.g : Service)

    Date createdOn           // Object creation DateTime
    long createdBy           // AppUser.id
    Date updatedOn           // Object updated DateTime
    long updatedBy           // AppUser.id

    long companyId          // Company.id

    static mapping = {
        id generator: 'sequence', params: [sequence: 'fxd_maintenance_type_id_seq']

        createdBy index: 'fxd_maintenance_type_created_by_idx'
        updatedBy index: 'fxd_maintenance_type_updated_by_idx'
        companyId index: 'fxd_maintenance_type_company_id_idx'
    }

    static constraints = {
        name(nullable: false)
        createdOn(nullable: false)
        createdBy(nullable: false)
        updatedBy(nullable: false)
        companyId(nullable: false)
        updatedOn(nullable: true)
    }
}

