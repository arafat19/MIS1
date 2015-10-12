package com.athena.mis.exchangehouse.entity

class ExhAgentCurrencyPosting {

    long id         // primary key, auto generated
    int version     // version
    long agentId         // ExhAgent.id
    long currencyId             // Currency.id
    double amount               // amount from user input

    long createdBy          // AppUser.id
    Date createdOn          // Object creation DateTime
    long updatedBy = 0L     // AppUser.id
    Date updatedOn          // Object Updated DateTime

    long taskId             //ExhTask.id

    static mapping = {
        id generator: 'sequence', params: [sequence: 'exh_agent_currency_posting_id_seq']
        amount sqlType: "numeric(16,4)"

        // indexing
        agentId index: 'exh_agent_currency_posting_agent_id_idx'
        currencyId index: 'exh_agent_currency_posting_currency_id_idx'
        createdBy index: 'exh_agent_currency_posting_created_by_idx'
        updatedBy index: 'exh_agent_currency_posting_updated_by_idx'
    }

    static constraints = {
        agentId(nullable: false)
        currencyId(nullable: false)
        amount(nullable: false)
        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
        updatedOn(nullable: true)
        taskId(nullable: false)
    }
}
