package com.athena.mis.exchangehouse.entity

class ExhPostalCode {

    long id
    int version
    long companyId
    String code

    static mapping={
        id generator: 'sequence', params: [sequence: 'exh_postal_code_id_seq']
        companyId index: 'exh_postal_code_company_id_idx'
    }


}
