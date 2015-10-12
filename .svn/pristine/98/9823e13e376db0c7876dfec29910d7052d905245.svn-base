package com.athena.mis.exchangehouse.entity

class ExhCurrencyConversion {

    Integer fromCurrency
    Integer toCurrency
    Double buyRate
    Double sellRate
    Long userId
    Date createdOn
    long updatedBy = 0L  // AppUser.id
    Date updatedOn       // Object updated DateTime
    Long companyId

    static mapping = {
        id generator: 'sequence', params: [sequence: 'exh_currency_conversion_id_seq']
		buyRate sqlType: "numeric(16,4)"
		sellRate sqlType: "numeric(16,4)"

        companyId index: 'exh_currency_conversion_company_id_idx'
        userId index: 'exh_currency_conversion_user_id_idx'
        updatedBy index: 'exh_currency_conversion_updated_by_idx'
    }

    static constraints = {
        createdOn(nullable: false)
        updatedOn(nullable: true)
        updatedBy(nullable: false)
    }
}
