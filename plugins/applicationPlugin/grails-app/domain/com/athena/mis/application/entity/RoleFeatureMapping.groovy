package com.athena.mis.application.entity

/**
 RoleFeatureMapping entity is used to map different roleType with transactionCode
 * */

class RoleFeatureMapping implements Serializable {
    long roleTypeId             // RoleType.id
    String transactionCode      // RequestMap.transactionCode
    long pluginId              // e.g : 1 for Application, 2 for Accounting etc

    static mapping = {
        version false
        id composite: ['roleTypeId', 'transactionCode']
    }

    static constraints = {
        roleTypeId(nullable: false)
        transactionCode(nullable: false)
        pluginId(nullable: false)
    }
}
