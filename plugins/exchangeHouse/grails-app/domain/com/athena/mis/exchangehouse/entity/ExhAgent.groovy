package com.athena.mis.exchangehouse.entity


class ExhAgent {
    long id             // agent id (primary key)
    int version         // agent version
    String name         // agent name
    String city         // agent city
    String address      // agent address
    String phone       // agent phone
    String commissionLogic  // logic
    long currencyId     // Currency.id
    long countryId      // Country.id
    long companyId      // Company.id
    double balance      // sum of currency posting amount
    double creditLimit  // credit limit amount

    long createdBy       // AppUser.id
    Date createdOn       // Object creation DateTime
    long updatedBy = 0L  // AppUser.id
    Date updatedOn       // Object updated DateTime

    static mapping = {
        id generator: 'sequence', params: [sequence: 'exh_agent_id_seq']
        balance sqlType: "numeric(16,4)"
        creditLimit sqlType: "numeric(16,4)"

        // indexing
        companyId index: 'exh_agent_company_id_idx'
        countryId index: 'exh_agent_country_id_idx'
        currencyId index: 'exh_agent_currency_id_idx'
        createdBy index: 'exh_agent_created_by_idx'
        updatedBy index: 'exh_agent_updated_by_idx'
    }

    static constraints = {
        name(nullable: false)
        address(nullable: false)
        city(nullable: false)
        phone(nullable: false)
        currencyId(nullable: false)
        companyId(nullable: false)
        countryId(nullable: false)
        balance(nullable: false)
        commissionLogic(nullable: false, size: 1..1500)
        creditLimit(nullable: false)
        updatedOn(nullable: true)
        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
    }
}
