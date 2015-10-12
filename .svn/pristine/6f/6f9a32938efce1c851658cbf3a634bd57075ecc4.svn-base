package com.athena.mis.exchangehouse.entity


class ExhTaskTrace {
    char action  // create, update, delete
    Date actionDate
    Long taskId
    String refNo
    Long currentStatus

    // @todo: data type of the Id field of remittance purpose domain is Long. So we need to use it as long type into any domain declaration
    Integer remittancePurpose
    Integer paymentMethod

    Long customerId
    //String customerName
    Long beneficiaryId
    //String beneficiaryName

    String pinNo

    // Fees and charges
    Double amountInForeignCurrency  // BDT amount
    Double amountInLocalCurrency    //GBP amount

    Integer toCurrencyId
    Integer fromCurrencyId

    Double conversionRate
    Double regularFee                    // constant
    Double discount
    Integer paidBy                    // Cash , cheque
    String paidByNo                   // Online Tr. no, Credit Card no

    Long companyId
    Long agentId
    Long userId                     // task changed by (not only createdBy)

    long taskTypeId
    long approvedBy = 0L
    Date approvedOn
	boolean isGatewayPaymentDone    // for all paidBy -> true , for PAYPOINT -> false (true when payment done)
	double exhGain					// total gain of ExchangeHouse in buying and selling rate
                                    //((AmountInLocalCurrency + regularFee - dicsount - commision) * (buyRate(GBP) - sellRate(GBP))) * buyRate(BDT)

    static mapping = {
        version false
        id generator: 'sequence', params: [sequence: 'exh_task_trace_id_seq']

        amountInForeignCurrency sqlType: "numeric(16,4)"
        amountInLocalCurrency sqlType: "numeric(16,4)"
        regularFee sqlType: "numeric(16,4)"
        conversionRate sqlType: "numeric(16,4)"
        discount sqlType: "numeric(16,4)"
		exhGain sqlType: "numeric(16,4)"
        // not indexing of countryId, companyId, userId etc , if necessary
    }

    static constraints = {
        refNo(nullable: false)  // unique because auto id concatenation
        pinNo(nullable: true)
        paidByNo(nullable: true)
        userId(nullable: false)
        taskId(nullable: false)
        taskTypeId(nullable: false)
        approvedOn(nullable: true)
    }

}
