package com.athena.mis.application.entity

class AppGroupEntity {

    long groupId                    // AppGroup.id
    long entityTypeId               // SystemEntity.id (e.g. DocCategory,DocSubCategory etc)
    long entityId                   // id of corresponding entity (e.g. DocCategory.id, DocSubCategory.id)
    long companyId                  // Company.id

    static constraints = {
        groupId(nullable: false)
        entityTypeId(nullable: false)
        entityId(nullable: false)
        companyId(nullable: false)
        groupId(unique: ['entityId', 'entityTypeId', 'companyId'])
    }

    static mapping = {
        id generator: 'sequence', params: [sequence: 'app_group_entity_id_seq']
        version false
        groupId index: 'app_group_entity_group_id_idx'
        entityTypeId index: 'app_group_entity_entity_type_id_idx'
        entityId index: 'app_group_entity_entity_id_idx'
        companyId index: 'app_group_entity_company_id_idx'
    }
}
