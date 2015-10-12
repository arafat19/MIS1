/**
 * Module Name - Application
 * Purpose - Entity of system configuration
 * */

package com.athena.mis.application.entity

class SysConfiguration {

    Integer id
    String key              // key to access the config
    String value            // value of config
    String description      // Description of config
    int pluginId
    long companyId          // Foreign key of company

    static mapping = {
        id generator: 'sequence', params: [sequence: 'sys_configuration_id_seq']
        version false
        companyId index: 'sys_configuration_company_id_idx'
    }

    static constraints = {
        key(unique: true,nullable: false)
        value(nullable: false)
        description(nullable: false, maxSize: 1000)        //@todo: maxSize is not working for this domain
        pluginId(nullable: false)
        companyId(nullable: false)
        key unique: 'companyId'
    }
}
