/**
 * Module  - Application
 * Purpose - Entity that contains all properties of SystemEntity
 */

package com.athena.mis.application.entity

/**
 * Creating Procedure of System Entity.
 * 1. ID = companyId+pluginId+sequence (ex: companyId=1,pluginId=1,sequence=3000, id=113000).
 * 2. Add new System Entity to 'SystemEntityService'.
 *   i. add system entity type to allowed method for corresponding type.
 *      e.g - 'createDefaultInventoryType()' allowed for inventory type.
 *   ii. create new method if type is new and declare that method in 'AppDefaultDataBootStrapService'.
 * 3. If system entity is reserved...
 *   i. go to 'ReservedSystemEntity.groovy' for details information about reserved system entity.
 *   ii. declare constant of reserved system entity in corresponding cacheUtility.
 * 4. Add new SystemEntity Type to... .. .
 *   i. 'CreateSystemEntityActionService'(N/A if already exists).
 *   ii. 'UpdateSystemEntityActionService'(N/A if already exists).
 *   iii. 'DeleteSystemEntityActionService'(N/A if already exists).
 *   iv. Association check for update & delete.
 *   v. 'SystemEntityDropDownTagLib'(N/A if already exists). [Used for DropDown]
 *   - location: GetDropDownSystemEntityTagLibActionService -- >> listSystemEntity().
 *   vi. 'SystemEntityByReservedTagLib'(N/A if already exists).
 * 5. Script should be written for new system entity for existing Database.
 */

class SystemEntity {
    long id              // primary key (companyId+pluginId+sequence ex: companyId=1,pluginId=1,sequence=3000, id=113000)
    int version
    String key           // key of SystemEntity
    String value         // value of SystemEntity
    long type            // SystemEntityType.id (payment method type, unit type etc.)
    boolean isActive     // flag for whether system entity is active or not
    long companyId       // Company.id
    long reservedId      // ReservedSystemEntity.id
    long pluginId        // id of plugin

    static constraints = {
        key(nullable: false)
        value(nullable: true)
        type(nullable: false)
        isActive(nullable: false)
        companyId(nullable: false)
        reservedId(nullable: false)
        pluginId(nullable: false)
    }

    static mapping = {
        id generator: 'assigned'
        key index: 'system_entity_key_idx'
        type index: 'system_entity_type_idx'
        companyId index: 'system_entity_company_id_idx'
        reservedId index: 'system_entity_reserved_id_idx'
        pluginId index: 'system_entity_plugin_id_idx'
    }
}
