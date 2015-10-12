package com.athena.mis.arms.entity

class RmsTransactionDay {

    long id
    Date transactionDate
    Date openedOn
    Date closedOn
    long openedBy
    long closedBy = 0L
    long companyId

    static mapping = {
        id generator: 'sequence', params: [sequence: 'rms_transaction_day_id_seq']
        companyId index: 'rms_transaction_day_company_id_idx'
        transactionDate type: 'date'
    }

    static constraints = {
        closedOn(nullable: true)
    }
}
