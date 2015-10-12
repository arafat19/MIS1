/**
 * Module Name - Application
 * Purpose:- Map AppUser with Different Entity
 * Cache Utility of Each Entity should pull data according to type. e.g. userProjectCacheUtility
 * */

package com.athena.mis.application.entity

class AppUserEntity {

    long appUserId                  // AppUser.id
    long entityTypeId               // SystemEntity.id (e.g. Project,Inventory,Customer etc)
    long entityId                   // id of corresponding entity (e.g. Project.id, Inventory.id)
    long companyId                   // Company.id

    static constraints = {
        appUserId(nullable: false)
        entityTypeId(nullable: false)
        entityId(nullable: false)
        companyId(nullable: false)
        appUserId(unique: ['entityId', 'entityTypeId', 'companyId'])
    }

    static mapping = {
        id generator: 'sequence', params: [sequence: 'app_user_entity_id_seq']
        version false
        appUserId index: 'app_user_entity_app_user_id_idx'
        entityTypeId index: 'app_user_entity_entity_type_id_idx'
        entityId index: 'app_user_entity_entity_id_idx'
        companyId index: 'app_user_entity_company_id_idx'
    }
}
