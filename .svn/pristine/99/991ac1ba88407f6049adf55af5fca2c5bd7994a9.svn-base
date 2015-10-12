package com.athena.mis.exchangehouse.entity

class ExhTask {

    String refNo                        // Auto generated
    Long currentStatus               // for now: 10= new task, 20= sent to bank
    Integer remittancePurpose           // Family support, job salary etc.
    Integer paymentMethod               // Bank deposit , cash collection, mobile pay

    Long outletBankId                   // mandatory either SEBL or other bank
    Long outletDistrictId               // not required (mandatory if other bank)
    Long outletBranchId                 // not required (mandatory if other bank)

    Long customerId                     // Redundant field
    String customerName
    Long beneficiaryId                  // Foreign key
    String beneficiaryName

    String pinNo

    // Fees and charges
    Double amountInForeignCurrency  // BDT amount
    Double amountInLocalCurrency    //GBP amount

    Integer toCurrencyId
    Integer fromCurrencyId

    Double conversionRate
    Double regularFee                    // constant
    double commission = 0d                 // constant
    Double discount
    Integer paidBy                    // Cash , cheque
    String paidByNo                   // Online Tr. no, Credit Card no

    Date createdOn
    Long companyId
    long agentId
    Long userId                     // task created By (redundant & will not change during lifecycle)

    long taskTypeId
    long approvedBy = 0L
    Date approvedOn
    boolean isGatewayPaymentDone   	// for all paidBy -> true , for PAYPOINT -> false (true when payment done)
	double exhGain					// total gain of ExchangeHouse in buying and selling rate
                                    //((AmountInLocalCurrency + regularFee - dicsount - commision) * (buyRate(GBP) - sellRate(GBP))) * buyRate(BDT)
    long refundTaskId         //used in sarb to create new task obj for refund task

	static mapping = {
        //createdOn type: 'date'
        id generator: 'sequence', params: [sequence: 'exh_task_id_seq']
        amountInForeignCurrency sqlType: "numeric(16,4)"
        amountInLocalCurrency sqlType: "numeric(16,4)"
        regularFee sqlType: "numeric(16,4)"
        conversionRate sqlType: "numeric(16,4)"
        discount sqlType: "numeric(16,4)"
        exhGain sqlType: "numeric(16,4)"

        // indexing
        refNo index: 'exh_task_ref_no_idx'
        pinNo index: 'exh_task_pin_no_idx'
        remittancePurpose index: 'exh_task_remittance_purpose_idx'
        paymentMethod index: 'exh_task_payment_method_idx'
        outletBankId index: 'exh_task_outlet_bank_id_idx'
        outletBranchId index: 'exh_task_outlet_branch_id_idx'
        outletDistrictId index: 'exh_task_outlet_district_id_idx'
        taskTypeId index: 'exh_task_task_type_id_idx'
        paidBy index: 'exh_task_paid_by_idx'
        currentStatus index: 'exh_task_current_status_idx'
        toCurrencyId index: 'exh_task_to_currency_id_idx'
        fromCurrencyId index: 'exh_task_from_currency_id_idx'
        customerId index: 'exh_task_customer_id_idx'
        beneficiaryId index: 'exh_task_beneficiary_id_idx'
        companyId index: 'exh_task_company_id_idx'
        agentId index: 'exh_task_agent_id_idx'
        userId index: 'exh_task_user_id_idx'
        approvedBy index: 'exh_task_approved_by_idx'
    }

    static constraints = {
        refNo(nullable: false)  // unique because auto id concatenation
        taskTypeId(nullable: false)
        pinNo(nullable: true)
        paidByNo(nullable: true)
        userId(nullable: false)
        outletBankId(nullable: false)
        outletDistrictId(nullable: true)
        outletBranchId(nullable: true)
        approvedOn(nullable: true)
    }
}
