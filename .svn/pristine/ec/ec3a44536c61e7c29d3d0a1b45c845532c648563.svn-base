package com.athena.mis.exchangehouse.entity

class ExhRemittancePurpose {
//    Integer id
    String name         // family support, donation
    long companyId      // Company.id
    String code         // code (used in SARB integration)


    static constraints = {
        name(nullable: false, unique: true)
        code(nullable: true)
        companyId(nullable: false)
//        name(nullable: false, size: 2..50, unique: true)
    }

    static mapping = {
        id generator: 'sequence', params: [sequence: 'exh_remittance_purpose_id_seq']
    }
}
