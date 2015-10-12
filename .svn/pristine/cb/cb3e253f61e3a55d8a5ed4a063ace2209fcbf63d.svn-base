/**
 * Module Name - Application
 * Purpose - Entity that contains all properties of ReservedSystemEntity
 * */

 package com.athena.mis.application.entity

/**
 * Creating Procedure of Reserved System Entity.
 *  1. ID assigned(pluginId + lastCounter ex: pluginId = 1, lastCounter = 1, id = 11).
 *     i. to get lastCounter go to ReservedSystemEntityService's header documentation.
 *     ii. always update lastCounter after creating new one.
 *  2. Add reserved system entity to allowed method of corresponding module in 'ReservedSystemEntityService'.
 *  3. Script should be written for new reserved system entity for existing Database.
 */

class ReservedSystemEntity {
    long id         // primary key assigned(pluginId + sequence ex: pluginId = 1, sequence = 1, id = 11)
    String key      // key of ReservedSystemEntity
    String value    // value of ReservedSystemEntity
    long type       // SystemEntityType.id
    long pluginId   // id of plugin

    static constraints = {
        key(nullable: false)
        value(nullable: true)
        type(nullable: false)
        pluginId(nullable: false)
    }

    static mapping = {
        id generator: 'assigned'
        version false
        type index: 'reserved_system_entity_type_idx'
        pluginId index: 'reserved_system_entity_plugin_id_idx'
    }
}
