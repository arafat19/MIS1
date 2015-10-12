package com.athena.mis.exchangehouse.entity

class ExhPhotoIdType {
    String name     // passport, national id
    long companyId      // Company.id
    String code
    boolean isSecondary


    static constraints = {
        name(nullable: false, unique: true)
        companyId(nullable: false)
        code(nullable: true)
        isSecondary(nullable: false)
//        name(nullable: false, size: 2..50, unique: true)
    }

    static mapping = {
        id generator: 'sequence', params: [sequence: 'exh_photo_id_type_id_seq']
    }

    public String toString() {
        return name
    }
}
