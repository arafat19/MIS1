/**
 * Module  - Project Track
 * Purpose - Entity that contains all properties of PtProject
 */

package com.athena.mis.projecttrack.entity

class PtProject {
    long id                     // primary key (Auto generated by its own sequence)
    int version
    String name                 // Project name
    String code                 // Project code
    long companyId              // Company.id
    Date createdOn              // Object creation DateTime
    long createdBy              // AppUser.id
    Date updatedOn              // Object update DateTime
    long updatedBy              // AppUser.id

    static mapping = {
        id generator: 'sequence', params: [sequence: 'pt_project_id_seq']
        companyId index: 'pt_project_company_id_idx'
        createdBy index: 'pt_project_created_by_id_idx'
        updatedBy index: 'pt_project_updated_by_id_idx'
    }

    static constraints = {
        name(unique: ['name', 'companyId'])
        code (unique: ['name', 'companyId'])
        updatedOn (nullable: true)
    }
}
