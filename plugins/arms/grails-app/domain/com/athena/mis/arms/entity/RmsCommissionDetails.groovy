package com.athena.mis.arms.entity

class RmsCommissionDetails {
    long id
    int version
    float comm
    float pNt
    float postage
    float serviceCharge
    float vat
    float vatOnPnt
    float totalCharge

    long companyId

    static mapping = {
        id generator: 'sequence', params: [sequence: 'rms_commission_details_id_seq']
        companyId index: 'rms_commission_details_company_id_idx'
    }

    static constraints = {
    }
}
