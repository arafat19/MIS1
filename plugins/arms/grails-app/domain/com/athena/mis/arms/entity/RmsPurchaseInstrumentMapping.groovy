package com.athena.mis.arms.entity

class RmsPurchaseInstrumentMapping {

    long id
    int version
    long instrumentTypeId
    long bankId
    long bankBranchId
    long districtId
    String commissionScript
    long companyId

    static mapping = {
        id generator: 'sequence', params: [sequence: 'rms_purchase_instrument_mapping_id_seq']
        companyId index: 'rms_purchase_instrument_mapping_company_id_idx'
    }

    static constraints = {
        commissionScript nullable: true, maxSize:1500
    }
}
