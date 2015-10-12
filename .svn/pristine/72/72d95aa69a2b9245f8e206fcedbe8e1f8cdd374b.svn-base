/**
 * Module  - Application
 * Purpose - Entity that contains all properties of SystemEntityType
 */

package com.athena.mis.application.entity

/**
 * Creating Procedure of System Entity Type.
 * 1. Declare a constant in 'SystemEntityTypeCacheUtility'.
 *    e.g - 'public static final long TYPE_GENDER = 1717'.
 * 2. Plugin id and lastCounter combined-ly generate ID. (pluginId + lastCounter ex: pluginId = 1, lastCounter = 1 --->> id = 11).
 *   i. To get lastCounter go to SystemEntityTypeService's header documentation.
 *   ii. ID = plugin id + lastCounter.
 *   iii. after created new type, update last counter info.
 * 3. Add new System Entity Type to 'SystemEntityTypeService'.
 *   i. add system entity type to allowed method for corresponding module.
 *      e.g - 'createDefaultDataForApp()' allowed for application module.
 *   ii. create new method if module is new and declare that method in 'AppDefaultDataBootStrapService'.
 * 4. Create a new cacheUtility.
 *   i. declare a constant as Entity Type with newly generated id.
 *      e.g - private static final long ENTITY_TYPE = 1717;
 * 5. Script should be written for new System Entity Type for existing Database.
 */

class SystemEntityType {

    long id             // primary key (pluginId + sequence ex: pluginId = 1, sequence = 1, id = 11)
    int version
    String name         // name of SystemEntityType
    String description  // description of SystemEntityType
    long pluginId       // id of plugin

    static constraints = {
        name(nullable: false)
        description(nullable: false)
        pluginId(nullable: false)
    }

    static mapping = {
        id generator: 'assigned'
        pluginId index: 'system_entity_type_plugin_id_idx'
    }
}
